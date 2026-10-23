package com.basic.workflow.cmd.rollBack.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SimpleQuery;
import com.basic.common.utils.StringUtils;
import com.basic.system.domain.SysUserRole;
import com.basic.workflow.cmd.rollBack.AbstractGateWayRollbackOperateStrategy;
import com.basic.workflow.cmd.rollBack.RollbackConstants;
import com.basic.workflow.cmd.rollBack.RollbackParamsTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.service.impl.HistoricVariableInstanceQueryImpl;

/**
 * @description 已完成会签任务且下一节点为默认网关时的回滚策略，处理会签回退与网关分支定位
 * @author Zengjunlong
 * @date 17:55 2024/3/6
 */
@Slf4j
public class CompletedMultiInstanceTaskAndNextDefaultGateWayTaskRollbackOperateStrategy extends
    AbstractGateWayRollbackOperateStrategy {


  private boolean isSequence = false;

  /**
   * 会签任务 单个执行人 表达式
   */
  private String assigneeExpr = "assignee";

  /**
   * 会签任务 集合 表达式
   */
  private String assigneeListExpr = "assigneeList";


  /**
   * @description 用回滚参数构造策略，用于撤回已完成会签且下一节点是网关的场景
   * @params [paramsTemplate] 回滚参数模板，含历史任务与当前节点元素
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public CompletedMultiInstanceTaskAndNextDefaultGateWayTaskRollbackOperateStrategy(
      RollbackParamsTemplate paramsTemplate) {
    super(paramsTemplate);
  }

  /**
   * @description 读取会签历史变量，重建会签主执行与各实例并把审核人换回撤回人，随后清理历史任务与网关运行态数据；串行场景还要求撤回节点是最后一个实例
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void createExecution() {

    if (paramsTemplate.getCurrentTaskElement()
        .getBehavior() instanceof SequentialMultiInstanceBehavior) {
      isSequence = true;
    }

    // 获取 当前执行的 execution
    ExecutionEntity executionEntity = getExecutionEntity();
    List<HistoricVariableInstance> historicVars = historyService.createHistoricVariableInstanceQuery()
        .processInstanceId(executionEntity.getProcessInstanceId()).variableName("nrOfInstances")
        .list();
    if (historicVars == null || historicVars.size() == 0) {
      throw new RuntimeException("没有可用会签参数:" + assigneeListExpr);
    }

    Integer sum = 0;
    for (HistoricVariableInstance hi : historicVars) {
      if (!hi.getExecutionId().equals(executionEntity.getId())) {
        sum = (Integer) hi.getValue();
      }
    }

    List<HistoricVariableInstance> historicVariableInstances = CommandContextUtil.getHistoricVariableService()
        .findHistoricVariableInstancesByQueryCriteria(
            new HistoricVariableInstanceQueryImpl().executionId(
                paramsTemplate.getHisTask().getExecutionId()));

    if (historicVariableInstances.isEmpty()) {
      throw new RuntimeException("没有可用会签任务参数");
    }

    // 历史变量
    Map<String, Object> hisVarMap = historicVariableInstances.stream().collect(
        Collectors.toMap(HistoricVariableInstance::getVariableName, item -> item.getValue()));

    if (hisVarMap.containsKey(assigneeExpr) && hisVarMap.containsKey(
        RollbackConstants.MultiInstanceConstants.LOOP_COUNTER)) {
      log.info("变量有效");
    } else {
      throw new RuntimeException("缺少会签任务变量");
    }

    /**
     *  串行 最终的 loopCounter assignee 都会是最后一个人
     */
    if (isSequence) {
      List<String> assigneeList = new ArrayList<>();
      // 存在一种情况，需要撤回的节点是会签节点，当前运行的任务也是多实例节点，
      // 那么assigneeList就会有问题，撤回节点获取的assigneeList其实是当前运行任务的
      try {
        // 优先尝试从历史任务中获取会签人员
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();
        // 获取当前任务
        List<Task> taskList = taskService.createTaskQuery()
            .processInstanceId(hisTask.getProcessInstanceId()).list();
        Task task = taskList.get(0);
        // 如果是同一节点的任务，尝试从历史任务变量获取
        if (hisTask != null && hisTask.getTaskDefinitionKey().equals(task.getTaskDefinitionKey())) {
          HistoricVariableInstance assigneeVar = historyService.createHistoricVariableInstanceQuery()
              .processInstanceId(hisTask.getProcessInstanceId()).taskId(hisTask.getId())
              .variableName(assigneeListExpr).singleResult();

          if (assigneeVar != null) {
            Object obj = assigneeVar.getValue();
            assigneeList = parseAssigneeList(obj);
          }
        } else {
          // 如果不是同一节点的任务，那么就有可能是A多实例，B多实例，A-B，A的assigneeList被B覆盖，
          // 这里是可以根据A生成的所有任务的处理构成集合，但是存在问题，A可能有人不通过回滚回去是一个多实例，那么就不可以这样
          // 最后这里还是根据流程图的userTask获取，如果流程图原本就没有绑定人员，那么会出现无法获取会签人员，最后只能根据历史任务获取
          // 从流程定义中获取初始配置
          BpmnModel bpmnModel = repositoryService.getBpmnModel(
              runtimeService.createProcessInstanceQuery()
                  .processInstanceId(hisTask.getProcessInstanceId()).singleResult()
                  .getProcessDefinitionId());
          UserTask userTask = (UserTask) bpmnModel.getFlowElement(hisTask.getTaskDefinitionKey());
          if (userTask != null) {
            // 从multiInstanceLoopCharacteristics配置获取
            MultiInstanceLoopCharacteristics loopCharacteristics = userTask.getLoopCharacteristics();
            // 是否存在多实例
            if (loopCharacteristics != null) {
              // 如果存在多实例的话，这里根据这个节点生成的任务来判断。
              List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery()
                  .executionId(hisTask.getExecutionId())
                  .taskDefinitionKey(hisTask.getTaskDefinitionKey())
                  .orderByHistoricTaskInstanceStartTime().asc().list();
              for (HistoricTaskInstance item : hisTaskList) {
                if (StringUtils.isNotEmpty(item.getAssignee()) && !assigneeList.contains(
                    item.getAssignee())) {
                  assigneeList.add(item.getAssignee());
                }
              }
              // 如果撤回节点的历史任务的处理人没有，那么退而其次直接获取节点模块的绑定人员
              if (assigneeList.size() == 0) {
                if (userTask.getCandidateUsers().size() > 0) {
                  assigneeList.addAll(userTask.getCandidateUsers());
                }
                if (userTask.getCandidateGroups().size() > 0) {
                  List<Long> groups = userTask.getCandidateGroups().stream().map(Long::valueOf)
                      .collect(Collectors.toList());
                  List<Long> userIds = new ArrayList<>();
                  LambdaQueryWrapper<SysUserRole> lqw = Wrappers.lambdaQuery(SysUserRole.class)
                      .select(SysUserRole::getUserId).in(SysUserRole::getRoleId, groups);
                  userIds = SimpleQuery.list(lqw, SysUserRole::getUserId);
                  assigneeList.addAll(userIds.stream().map(String::valueOf).distinct()
                      .collect(Collectors.toList()));
                }
              }
            } else {
              // 如果原图不存在多实例------一般不会发生这种情况，因为如果流程图显示的不是多实例，
              // 但是流程定义走了多实例，那么就是进行了改图，但是改图之后定义就发生了变化，那么这个运行的定义就是修改图之后的定义，则拿到的图就是多实例的图
            }
          }
        }
      } catch (Exception e) {
        log.error("获取历史任务变量失败", e);
      }
      if (assigneeList.size() == 0) {
        throw new RuntimeException("会签人员未查询成功，请联系管理员");
      }

      if (!assigneeList.get(assigneeList.size() - 1)
          .equals(paramsTemplate.getHisTask().getAssignee())) {
        String msg = "不是串行最后一个节点，无法进行回退";
        throw new RuntimeException(msg);
      }
      // 替换任务执行变量
      assigneeList.set(assigneeList.size() - 1, assignee);
      runtimeService.setVariableLocal(paramsTemplate.getHisTask().getProcessInstanceId(),
          assigneeListExpr, assigneeList);
    }

    // 流程执行变量
    Integer loopCounter = (Integer) hisVarMap.get(
        RollbackConstants.MultiInstanceConstants.LOOP_COUNTER);

    // 会签主任务
    ExecutionEntity parentExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
        .createChildExecution(executionEntity.getParent());

    parentExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());
    parentExecution.setActive(false);
    // 配置 会签 root execution
    parentExecution.setMultiInstanceRoot(true);

    // 配置主 execution 变量
    Map<String, Object> parentVarMap = new HashMap<>();
    parentVarMap.put(RollbackConstants.MultiInstanceConstants.NR_OF_ACTIVE_INSTANCES, 1);
    parentVarMap.put(RollbackConstants.MultiInstanceConstants.NR_OF_COMPLETE_INSTANCES, 1);
    parentVarMap.put(RollbackConstants.MultiInstanceConstants.NR_OF_INSTANCE, 2);
    parentExecution.setVariablesLocal(parentVarMap);

    if (isSequence) {
      log.info("创建 串行 会签任务");
      createSequenceMultiInstance(parentExecution, sum);
    } else {
      log.info("创建 并行 会签任务");
      createParallelMultiInstance(parentExecution, sum, loopCounter);

    }

    removeHisTask(paramsTemplate.getHisTask());

    // 特殊处理并行网关
    processGateway(executionEntity.getParent());

  }

  // 辅助方法：解析assignee列表
  /**
   * @return List<String> 会签人员列表，无法解析时返回空列表
   * @description 把历史变量里的会签人员解析成字符串列表，支持 List 与逗号分隔字符串两种存法
   * @params [obj] 历史变量值
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private List<String> parseAssigneeList(Object obj) {
    if (obj == null) {
      return Collections.emptyList();
    }

    if (obj instanceof List) {
      return (List<String>) obj;
    } else if (obj instanceof String) {
      return Arrays.asList(obj.toString().split(","));
    }
    return Collections.emptyList();
  }

  /**
   * @description 按串行会签重建单个执行实例，把循环计数器退回到最后一位并交给多实例行为继续推进
   * @params [parentExecution, sum] 会签主执行实例；会签实例总数
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private void createSequenceMultiInstance(ExecutionEntity parentExecution, Integer sum) {

    ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
        .createChildExecution(parentExecution);

    Map<String, Object> varMap = new HashMap<>();
    varMap.put(assigneeExpr, assignee);
    varMap.put(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER, sum - 1);
    newExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());

    newExecution.setVariablesLocal(varMap);
    newExecution.setActive(true);
    CommandContextUtil.getAgenda(commandContext)
        .planContinueMultiInstanceOperation(newExecution, parentExecution, sum - 1);

  }

  /**
   * @description 按并行会签为总数中每个实例各建一个子执行，历史实例置为已完成占位，当前循环位实例激活并挂上撤回人
   * @params [parentExecution, sum, loopCounter] 会签主执行实例；会签实例总数，决定创建的实例个数；当前生效的循环计数器位置，跳过该位后单独处理
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private void createParallelMultiInstance(ExecutionEntity parentExecution, Integer sum,
      Integer loopCounter) {

    for (int i = 0; i < sum; i++) {
      if (i != loopCounter) {
        Map<String, Object> varMap = new HashMap<>();
        varMap.put(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER, i);
        varMap.put(assigneeExpr, "已完成任务");

//                // 创建 新执行任务
        ExecutionEntity newExecution = newExecution = CommandContextUtil.getExecutionEntityManager(
            commandContext).createChildExecution(parentExecution);
        newExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());
        newExecution.setActive(false);

        newExecution.setVariablesLocal(varMap);
      } else {
        ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
            .createChildExecution(parentExecution);
        newExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());

        Map<String, Object> varMap = new HashMap<>();
        log.debug("{}", "创建任务的时候撤回审核人===" + assignee);
        varMap.put(assigneeExpr, assignee);
        varMap.put(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER, i);

        newExecution.setVariablesLocal(varMap);
        newExecution.setActive(true);

        CommandContextUtil.getAgenda(commandContext)
            .planContinueMultiInstanceOperation(newExecution, parentExecution, loopCounter);
      }
    }


  }


}
