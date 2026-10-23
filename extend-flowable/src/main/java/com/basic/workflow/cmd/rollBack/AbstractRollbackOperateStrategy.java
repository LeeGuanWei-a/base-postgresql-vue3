package com.basic.workflow.cmd.rollBack;


import com.basic.common.utils.spring.SpringUtils;
import com.basic.workflow.mapper.HistoryActivityInstanceMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.ActivityInstanceQueryImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.HistoricTaskInstanceQueryImpl;
import org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

/**
 * @description 流程撤回策略抽象基类，持有回滚参数、办理人及流程引擎服务，供各具体撤回场景实现
 * @author Zengjunlong
 * @date 17:56 2024/3/6
 */
@Slf4j
public abstract class AbstractRollbackOperateStrategy implements RollbackOperateStrategy {

  protected RollbackParamsTemplate paramsTemplate;
  /**
   * 任务处理人
   */
  protected String assignee;

  protected Map<String, Object> variables;

  protected CommandContext commandContext;

  /**
   * 无法操作
   */
  protected HistoryActivityInstanceMapper historyActivityInstanceMapper;

  protected RuntimeService runtimeService;
  protected RepositoryService repositoryService;

  protected TaskService taskService;

  protected HistoryService historyService;

  /**
   * @description 绑定回滚参数模板，并从 Spring 容器取出回滚所需的引擎服务与自定义 mapper
   * @params [paramsTemplate] 本次回滚使用的参数模板
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public AbstractRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
    this.paramsTemplate = paramsTemplate;
    this.historyActivityInstanceMapper = SpringUtils.getBean(HistoryActivityInstanceMapper.class);
    this.taskService = SpringUtils.getBean(TaskService.class);
    this.runtimeService = SpringUtils.getBean(RuntimeService.class);
    this.historyService = SpringUtils.getBean(HistoryService.class);
    this.repositoryService = SpringUtils.getBean(RepositoryService.class);
  }

  /**
   * @description 以空变量表调用三参重载，执行一次不带流程变量的回滚
   * @params [commandContext, assignee] commandContext 命令上下文；assignee 回退后任务的办理人
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void process(CommandContext commandContext, String assignee) {
    process(commandContext, assignee, new HashMap<>());
  }

  /**
   * @description 回滚主流程：依次做合理性校验、处理人设置、执行重建，再清理运行时任务与历史数据
   * @params [commandContext, assignee, variables] commandContext 命令上下文；assignee 回退后任务的办理人；variables 回退时需要写入的流程变量
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void process(CommandContext commandContext, String assignee,
      Map<String, Object> variables) {

    this.commandContext = commandContext;
    this.assignee = assignee;
    this.variables = variables;
    log.debug("{}", "撤回审核人===" + assignee);
    log.info("处理 existNextFinishedTask");
    existNextFinishedTask();
    log.info("配置任务执行人 setAssignee");
    setAssignee();
    log.info("处理 createExecution");
    createExecution();
    log.info("处理 deleteRuntimeTasks");
    deleteRuntimeTasks();
    log.info("处理 deleteHisActInstance");
    deleteHisActInstance();
    log.info("移除扩展表、历史用户关系表数据");
    deleteExtentHis();

  }

  /**
   * @return ExecutionEntity 回退任务所在的执行实例，取不到原执行时退化为下一节点的执行实例
   * @description 先按历史任务的执行id查找执行实例，找不到再按父执行与下一节点活动id取出第一个执行实例，都没有则抛异常
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  protected ExecutionEntity getExecutionEntity() {

    ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager(commandContext)
        .findById(paramsTemplate.getHisTask().getExecutionId());
    log.debug("{}", paramsTemplate.getNextFlowIdList());
    if (null == executionEntity) {

      log.info("没找到回退任务的 execution,获取下一节点");
      List<ExecutionEntity> executionEntityList = CommandContextUtil.getExecutionEntityManager(
          commandContext).findExecutionsByParentExecutionAndActivityIds(
          paramsTemplate.getHisTask().getProcessInstanceId(), paramsTemplate.getNextFlowIdList());
      if (executionEntityList.isEmpty()) {
        throw new RuntimeException("没有找到下一节点");
      }
      executionEntity = executionEntityList.get(0);
    }

    return executionEntity;
  }

  /**
   * @description 删除指定的历史任务，并清理同一执行路径上应一并撤销的后续历史任务
   * @params [hisTask] 待删除的历史任务
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  protected void removeHisTask(HistoricTaskInstance hisTask) {
    // 移除历史任务列表
    log.info("移除历史任务 [ id = " + hisTask.getId() + " ]");
    CommandContextUtil.getHistoricTaskService()
        .deleteHistoricTask((HistoricTaskInstanceEntity) hisTask);
    List<Task> taskList = taskService.createTaskQuery()
        .processInstanceId(hisTask.getProcessInstanceId()).list();
    Task task = taskList.get(0);
    // 2. 获取当前任务的执行信息
    ExecutionEntity currentExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
        .findById(task.getExecutionId());
    // 移除历史任务信息
    List<HistoricTaskInstanceEntity> historicTaskInstanceList = CommandContextUtil.getHistoricTaskService(
        commandContext).findHistoricTasksByProcessInstanceId(hisTask.getProcessInstanceId());
    // 4. 只删除当前执行路径上的后续任务
    for (HistoricTaskInstanceEntity item : historicTaskInstanceList) {
      if (shouldDeleteTask(item, currentExecution)) {
        log.debug("{}", "*********删除的实例：" + hisTask.getProcessInstanceId());
        log.debug("{}", "*********删除的节点：" + item.getName());
        log.info("删除关联历史任务[id={},key={},startTime={}]", item.getId(),
            item.getTaskDefinitionKey(), item.getStartTime());
        CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTask(item);
      }
    }
  }

  /**
   * @return boolean 该任务属于当前执行路径上应被撤销的后续任务时返回 true
   * @description 依次校验任务与当前执行是否同一多实例分支、同一执行路径、创建时间更晚、节点类型相同，且由当前用户办理未完成
   * @params [task, currentExecution] task 待判断的历史任务；currentExecution 当前任务的执行实例
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private boolean shouldDeleteTask(HistoricTaskInstanceEntity task,
      ExecutionEntity currentExecution) {
    // 1. 检查任务是否属于当前执行路径
    ExecutionEntity taskExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
        .findById(task.getExecutionId());

    return taskExecution != null && isSameMultiInstanceBranch(taskExecution, currentExecution)
        // 同一多实例分支
        && isSameExecutionPath(taskExecution, currentExecution) && isAfterCurrentTask(task,
        currentExecution) && isSameActivityType(task, currentExecution)
        && isNotCompletedByCurrentUser(task) // 修改为仅判断当前用户任务
        // 任务未完成或属于当前活动
        && (task.getEndTime() == null || taskExecution.isActive());
  }

  /**
   * @return boolean 任务办理人是当前操作人且任务尚未结束时返回 true
   * @description 取当前操作用户id，判断待检查任务的办理人是否为其本人且没有结束时间
   * @params [task] 待判断的历史任务
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private boolean isNotCompletedByCurrentUser(HistoricTaskInstanceEntity task) {
    // 获取当前操作用户ID
    String currentUserId = paramsTemplate.getHisTask().getAssignee();
    return task.getAssignee() != null && task.getAssignee().equals(currentUserId)
        && task.getEndTime() == null;
  }

  /**
   * @return boolean 两个执行实例的活动id相同时返回 true
   * @description 比较任务执行与当前执行的活动id，判断两者是否落在同一个多实例分支上
   * @params [taskExecution, currentExecution] taskExecution 任务所属的执行实例；currentExecution 当前任务的执行实例
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private boolean isSameMultiInstanceBranch(ExecutionEntity taskExecution,
      ExecutionEntity currentExecution) {
    // 获取多实例父执行ID
    String taskParentId = taskExecution.getParentId();
    String currentParentId = currentExecution.getParentId();
    // 如果是多实例分支，比较具体的执行分支ID
    return Objects.equals(taskExecution.getActivityId(), currentExecution.getActivityId());
  }

  /**
   * @return boolean 任务执行与当前执行位于同一分支路径上时返回 true
   * @description 同时向上追溯两个执行实例的父级，逐层比较执行id判断是否属于同一分支
   * @params [taskExecution, currentExecution] taskExecution 任务所属的执行实例；currentExecution 当前任务的执行实例
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private boolean isSameExecutionPath(ExecutionEntity taskExecution,
      ExecutionEntity currentExecution) {
    // 向上追溯执行树，检查是否属于同一分支（增强判断）
    ExecutionEntity currentParent = currentExecution;
    while (taskExecution != null && currentParent != null) {
      if (taskExecution.getId().equals(currentParent.getId())) {
        return true;
      }
      // 同时向上追溯两个执行实例的父级
      taskExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
          .findById(taskExecution.getParentId());
      currentParent = CommandContextUtil.getExecutionEntityManager(commandContext)
          .findById(currentParent.getParentId());
    }
    return false;
  }

  /**
   * @return boolean 任务开始时间晚于当前执行最新活动的开始时间时返回 true
   * @description 按执行id与活动id查出最新的历史活动记录，用任务开始时间与之比较
   * @params [task, currentExecution] task 待判断的历史任务；currentExecution 当前任务的执行实例
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private boolean isAfterCurrentTask(HistoricTaskInstanceEntity task,
      ExecutionEntity currentExecution) {
    // 添加活动ID过滤，确保唯一性
    List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
        .executionId(currentExecution.getId())
        .activityId(currentExecution.getActivityId()) // 添加活动ID过滤
        .orderByHistoricActivityInstanceStartTime().desc().list();
    // 取当前执行实例的最新活动记录
    HistoricActivityInstance currentActivity = activities.isEmpty() ? null : activities.get(0);
    return currentActivity != null && task.getStartTime().after(currentActivity.getStartTime());
  }

  /**
   * @return boolean 任务定义key与当前执行的活动id一致时返回 true
   * @description 比较历史任务的任务定义key与当前执行的活动id，判断两者是否同一节点类型
   * @params [task, currentExecution] task 待判断的历史任务；currentExecution 当前任务的执行实例
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private boolean isSameActivityType(HistoricTaskInstanceEntity task,
      ExecutionEntity currentExecution) {
    return task.getTaskDefinitionKey().equals(currentExecution.getActivityId());
  }

  /**
   * @description 按任务逐层清除运行时数据：身份链接、流程变量、任务本身以及对应的执行实例
   * @params [obj] 待移除的运行时任务实体
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  protected void removeRuntimeTaskOperate(TaskEntity obj) {
    log.debug("移除 IdentityLink: " + obj.getId());
    CommandContextUtil.getIdentityLinkService(commandContext)
        .deleteIdentityLinksByTaskId(obj.getId());
    log.debug("移除 Variable: " + obj.getId());
    CommandContextUtil.getVariableService(commandContext)
        .deleteVariablesByExecutionId(obj.getExecutionId());
    log.debug("移除 Task: " + obj.getId());
    CommandContextUtil.getTaskService(commandContext)
        .deleteTasksByExecutionId(obj.getExecutionId());
    log.debug("移除 execution: " + obj.getExecutionId());
    CommandContextUtil.getExecutionEntityManager(commandContext).delete(obj.getExecutionId());
  }


  /**
   * @description 把执行置为活动状态、写入办理人变量并挂回当前节点，交给引擎重新创建任务
   * @params [newExecution] 复用或新建的执行实例
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  protected void createExecution(ExecutionEntity newExecution) {
    newExecution.setActive(true);
    // 测试设置变量
    variables.put("assigneeList", assignee);
    newExecution.setVariablesLocal(variables);
    newExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());

    // 创建新任务
    log.debug("创建新任务");
    CommandContextUtil.getAgenda(commandContext).planContinueProcessInCompensation(newExecution);
  }

  /**
   * @description 校验回退任务的下一节点是否已有完成记录，存在则抛出异常拒绝回滚
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void existNextFinishedTask() {
    HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

    List<HistoricTaskInstance> hisTaskList = CommandContextUtil.getHistoricTaskService()
        .findHistoricTaskInstancesByQueryCriteria(
            (HistoricTaskInstanceQueryImpl) new HistoricTaskInstanceQueryImpl().processInstanceId(
                hisTask.getProcessInstanceId()).taskCompletedAfter(hisTask.getEndTime()));

    if (!hisTaskList.isEmpty()) {
      hisTaskList.forEach(obj -> {
        if (paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey())) {
          String msg = "存在已完成下一节点任务";
          throw new RuntimeException(msg);
        }
      });
    }
  }

  /**
   * @description 清理该任务在扩展表、历史用户关系表、审批意见与表单中的数据
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void deleteExtentHis() {
    HistoricTaskInstance hisTask = paramsTemplate.getHisTask();
    historyActivityInstanceMapper.deleteHisIdentitylink(hisTask.getId());
    historyActivityInstanceMapper.deleteToCommentForTaskId(hisTask.getId());
    historyActivityInstanceMapper.deleteTaskForm(hisTask.getId(), hisTask.getTaskDefinitionKey());
    log.debug("移除扩展表、历史用户关系表数据");
  }

  /**
   * @description 挑出回退点之后的历史活动连线，绕开引擎接口改用自定义 mapper 批量删除
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void deleteHisActInstance() {

    List<ActivityInstance> activityInstanceEntityList = CommandContextUtil.getActivityInstanceEntityManager(
        commandContext).findActivityInstancesByQueryCriteria(
        new ActivityInstanceQueryImpl().processInstanceId(
            paramsTemplate.getHisTask().getProcessInstanceId()));

    List<String> ids = new ArrayList<>();
    activityInstanceEntityList.forEach(obj -> {
      // 时间大于 任务创建时间 之后线条
      if (obj.getStartTime().getTime() > paramsTemplate.getHisTask().getCreateTime().getTime()
          && paramsTemplate.getNextFlowIdList().contains(obj.getActivityId())) {
        ids.add(obj.getId());
      }
      log.debug("{}", paramsTemplate.getHisTask().getTaskDefinitionKey());
      log.debug("{}", obj.getActivityId());
      log.debug("{}", obj.getEndTime());
      // 当前任务的连线 ID
      if (obj.getEndTime() != null && paramsTemplate.getHisTask().getTaskDefinitionKey()
          .equals(obj.getActivityId()) && obj.getEndTime().getTime() > paramsTemplate.getHisTask()
          .getCreateTime().getTime()) {
        ids.add(obj.getId());
      }
    });

    // 移除当前任务连线
//        LOGGER.debug("移除当前任务连线");
//        ids.forEach(obj -> CommandContextUtil.getActivityInstanceEntityManager(commandContext).delete(obj));
    // 移除历史任务连线
    // 历史任务删除失败，改用自己写mapper 完成删除功能
//        ids.forEach(obj -> CommandContextUtil.getHistoricActivityInstanceEntityManager(commandContext).delete(obj));
    log.debug("移除历史任务连线");
    ids.forEach(obj -> historyActivityInstanceMapper.delete(obj));

  }

  /**
   * @description 删除下一节点上的运行时任务（连同身份链接、变量、执行实例）以及对应的历史任务
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void deleteRuntimeTasks() {
    HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

    List<TaskEntity> taskEntityList = CommandContextUtil.getTaskService(commandContext)
        .findTasksByProcessInstanceId(hisTask.getProcessInstanceId());
    taskEntityList.forEach(obj -> {
      if (paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey())) {
        log.info("移除正在执行的下一节点任务");
        // 移除任务
        removeRuntimeTaskOperate(obj);
      }
    });

    // 移除历史任务信息
    List<HistoricTaskInstanceEntity> historicTaskInstanceList = CommandContextUtil.getHistoricTaskService(
        commandContext).findHistoricTasksByProcessInstanceId(hisTask.getProcessInstanceId());
    historicTaskInstanceList.forEach(obj -> {
      if (paramsTemplate.getNextFlowIdList().contains(obj.getTaskDefinitionKey())) {
        CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTask(obj);
      }
    });
  }

  /**
   * @description 按流程实例与节点key拼接变量名，把回退后的办理人写入流程变量供监听器取值
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void setAssignee() {
    HistoricTaskInstance hisTask = paramsTemplate.getHisTask();
    String key = RollbackConstants.ASSIGNEE_PREFIX_KEY + hisTask.getProcessInstanceId()
        + hisTask.getTaskDefinitionKey();
    variables.put(key, assignee);
  }

  /**
   * @description 空实现，由会签类回滚策略覆盖以自定义办理人表达式
   * @params [assigneeExpr, assigneeListExpr] assigneeExpr 单个办理人表达式；assigneeListExpr 办理人集合表达式
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void setAssigneeExpr(String assigneeExpr, String assigneeListExpr) {
    // to override
  }


}
