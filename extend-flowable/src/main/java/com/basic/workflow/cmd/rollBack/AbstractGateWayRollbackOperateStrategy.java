package com.basic.workflow.cmd.rollBack;


import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Gateway;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.HistoricTaskInstanceQueryImpl;
import org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description 网关回滚策略抽象基类，提供网关类节点回滚的公共校验与目标节点计算逻辑
 * @author Zengjunlong
 * @date 17:57 2024/3/6
 */
@Slf4j
public abstract class AbstractGateWayRollbackOperateStrategy extends
    AbstractRollbackOperateStrategy {

  /**
   * @description 绑定回滚参数模板，网关类回滚策略的公共数据都从模板取
   * @params [paramsTemplate] 回滚参数模板
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public AbstractGateWayRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
    super(paramsTemplate);
  }

  /**
   * @description 校验当前任务之后是否已有下一节点任务完成，存在则抛出异常阻止回滚
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void existNextFinishedTask() {
    HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

    List<HistoricTaskInstance> historicTaskInstanceList = CommandContextUtil.getHistoricTaskService(
            commandContext)
        .findHistoricTaskInstancesByQueryCriteria(
            (HistoricTaskInstanceQueryImpl) new HistoricTaskInstanceQueryImpl()
                .finished()
                .processInstanceId(hisTask.getProcessInstanceId())
                .taskCompletedAfter(hisTask.getEndTime())
        );

    if (!historicTaskInstanceList
        .stream()
        .filter(obj -> paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey()))
        .collect(Collectors.toList())
        .isEmpty()) {
      String msg = "存在已完成下一节点任务";
      throw new RuntimeException(msg);
    }
  }


  /**
   * @description 删除下一节点上仍在运行的任务及其历史记录，为网关回滚清场
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void deleteRuntimeTasks() {

    HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

    // 删除正在执行任务
    List<Task> taskList = taskService.createTaskQuery()
        .processInstanceId(hisTask.getProcessInstanceId())
        .taskCreatedAfter(hisTask.getEndTime())
        .list();
    taskList.stream()
        .filter(obj -> paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey()))
        .forEach(obj -> {
          log.info("删除运行时任务：" + obj);
          removeRuntimeTaskOperate((TaskEntity) obj);
        });

    // 删除历史任务
    List<HistoricTaskInstanceEntity> historicTaskInstances = CommandContextUtil.getHistoricTaskService(
            commandContext)
        .findHistoricTasksByProcessInstanceId(hisTask.getProcessInstanceId());
    for (HistoricTaskInstanceEntity obj : historicTaskInstances) {
      if (obj.getId().equals(hisTask.getId()) // 排除当前任务本身
          || obj.getEndTime() == null // 跳过未完成任务
          || !isSameMultiInstanceParent(obj, hisTask)) { //检查多实例父执行
        // 注意：这里必须用 continue 跳过当前这一条，不能用 return——
        // return 会直接结束整个方法，导致后续满足条件的任务永远清不掉
        continue;
      }
      // 调整时间判断（使用开始时间且严格大于）
      if (obj.getStartTime().after(hisTask.getCreateTime())
          && paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey())) {
        log.info("删除后续任务[id={},key={}]", obj.getId(), obj.getTaskDefinitionKey());
        CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTask(obj);
      }
    }
  }
  /**
   * @return boolean 父执行 ID 相同返回 true，任一执行查不到时按不同分支处理
   * @description 比较两个任务所在执行的父执行 ID，判断是否属于同一多实例分支
   * @params [task, hisTask] 待比较的历史任务；当前回滚的历史任务
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private boolean isSameMultiInstanceParent(HistoricTaskInstanceEntity task, HistoricTaskInstance hisTask) {
    ExecutionEntity taskExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
        .findById(task.getExecutionId());
    ExecutionEntity currentExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
        .findById(hisTask.getExecutionId());
    // 获取多实例父执行ID进行比较
    String taskParentId = taskExecution != null ? taskExecution.getParentId() : null;
    String currentParentId = currentExecution != null ? currentExecution.getParentId() : null;

    return Objects.equals(taskParentId, currentParentId);
  }

  /**
   * @description 处理并行网关线条流汇总的特殊场景，清理或补齐网关连线执行后再删除下一节点任务
   * @params [parent] 当前回滚所在的父执行
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  protected void processGateway(ExecutionEntity parent) {

    // 当前正在运行所以任务
    List<TaskEntity> taskEntityList = CommandContextUtil.getTaskService(commandContext)
        .findTasksByProcessInstanceId(parent.getProcessInstanceId());

    boolean isExistPassGatewayTask = false;

    // 下一节点任务
    List<TaskEntity> nextTaskList = taskEntityList.stream()
        .filter(obj -> paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey()))
        .collect(Collectors.toList());

    if (!nextTaskList.isEmpty()) {
      log.info("已经生成过网关任务");
      isExistPassGatewayTask = true;

      // 网关的连线
      Map<String, SequenceFlow> sqFlowMap = new HashMap<>();

      paramsTemplate.getGatewayMap().values().forEach(obj -> {
        obj.getIncomingFlows().forEach(item -> {

          if (null != paramsTemplate.getGatewayMap().get(item.getSourceRef())) {
            log.info("跳过gateway 间连线:" + item);
            return;
          }
          if (paramsTemplate.getHisTask().getTaskDefinitionKey().equals(item.getSourceRef())) {
            log.info("跳过当前回退历史任务:" + item);
            return;
          }
          sqFlowMap.put(item.getSourceRef(), item);
        });
      });

      // 创建网关相关连线 execution
      createCompleteGatewayExecution(parent, sqFlowMap);

      // 删除当前正在执行任务
      Set<String> nestTaskIdSet = new HashSet<>();
      nextTaskList.forEach(obj -> {
        removeRuntimeTaskOperate(obj);
        nestTaskIdSet.add(obj.getId());
      });

      // 移除正在执行下一节点历史任务
      List<HistoricTaskInstanceEntity> historicTaskInstanceList = CommandContextUtil.getHistoricTaskService(
              commandContext)
          .findHistoricTasksByProcessInstanceId(parent.getProcessInstanceId());
      historicTaskInstanceList.forEach(obj -> {
        if (nestTaskIdSet.contains(obj.getId())) {
          CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTask(obj);
        }
      });
    }

    if (isExistPassGatewayTask) {

    } else {
      log.info("移除网关连线");

      List<ExecutionEntity> executionEntityList = CommandContextUtil.getExecutionEntityManager(
              commandContext)
          .findExecutionsByParentExecutionAndActivityIds(parent.getProcessInstanceId(),
              paramsTemplate.getGatewayMap().keySet());

      Map<String, FlowElement> targetGatewayMap = paramsTemplate.getCurrentTaskElement()
          .getOutgoingFlows().stream().filter(obj -> obj.getTargetFlowElement() instanceof Gateway)
          .map(obj -> obj.getTargetFlowElement())
          .collect(Collectors.toMap(FlowElement::getId, obj -> obj));

      List<ExecutionEntity> toRemoveList = new ArrayList<>();

      executionEntityList.forEach(obj -> {
        if (null != targetGatewayMap.get(obj.getActivityId())) {
          toRemoveList.add(obj);
          targetGatewayMap.put(obj.getActivityId(), null);
        }
      });

      if (!toRemoveList.isEmpty()) {
        toRemoveList.forEach(obj -> {
          log.info("移除连线:" + obj);
          CommandContextUtil.getExecutionEntityManager(commandContext).delete(obj);
        });
      }
    }
  }

  /**
   * @description 为每条待补的网关连线创建非活动子执行并计划继续执行，补齐网关分支
   * @params [parent, sqFlowMap] 新建子执行挂载的父执行；待补齐的连线，按源节点 ID 索引
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  protected void createCompleteGatewayExecution(ExecutionEntity parent,
      Map<String, SequenceFlow> sqFlowMap) {

    sqFlowMap.values().forEach(obj -> {

      ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
          .createChildExecution(parent);

      newExecution.setCurrentFlowElement(obj.getTargetFlowElement());
      newExecution.setActive(false);

      log.debug("创建 gateway 连线 execution");
      CommandContextUtil.getAgenda(commandContext).planContinueProcessInCompensation(newExecution);
    });
  }

}
