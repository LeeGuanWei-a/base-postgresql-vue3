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
 * @Author Zengjunlong
 * @Description //TODO 撤回抽象策略类
 * @Date 17:56 2024/3/6
 * @Param
 * @return
 **/
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

  public AbstractRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
    this.paramsTemplate = paramsTemplate;
    this.historyActivityInstanceMapper = SpringUtils.getBean(HistoryActivityInstanceMapper.class);
    this.taskService = SpringUtils.getBean(TaskService.class);
    this.runtimeService = SpringUtils.getBean(RuntimeService.class);
    this.historyService = SpringUtils.getBean(HistoryService.class);
    this.repositoryService = SpringUtils.getBean(RepositoryService.class);
  }

  @Override
  public void process(CommandContext commandContext, String assignee) {
    process(commandContext, assignee, new HashMap<>());
  }

  @Override
  public void process(CommandContext commandContext, String assignee,
      Map<String, Object> variables) {

    this.commandContext = commandContext;
    this.assignee = assignee;
    this.variables = variables;
    System.out.println("撤回审核人===" + assignee);
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
   * 获取 当前执行 execution
   *
   * @return
   */
  protected ExecutionEntity getExecutionEntity() {

    ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager(commandContext)
        .findById(paramsTemplate.getHisTask().getExecutionId());
    System.out.println(paramsTemplate.getNextFlowIdList());
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
        System.out.println("*********删除的实例：" + hisTask.getProcessInstanceId());
        System.out.println("*********删除的节点：" + item.getName());
        log.info("删除关联历史任务[id={},key={},startTime={}]", item.getId(),
            item.getTaskDefinitionKey(), item.getStartTime());
        CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTask(item);
      }
    }
  }

  /**
   * 判断任务是否应该被删除
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
   * @return boolean
   * @description TODO (修改完成用户判断方法)
   * @params task
   * @author yanbin
   * @date 2025/9/26 15:44
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private boolean isNotCompletedByCurrentUser(HistoricTaskInstanceEntity task) {
    // 获取当前操作用户ID
    String currentUserId = paramsTemplate.getHisTask().getAssignee();
    return task.getAssignee() != null && task.getAssignee().equals(currentUserId)
        && task.getEndTime() == null;
  }

  /**
   * @return boolean
   * @description TODO (新增多实例分支判断方法)
   * @params taskExecution currentExecution
   * @author yanbin
   * @date 2025/9/26 15:44
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
   * @return boolean
   * @description TODO (检查是否属于同一执行路径)
   * @params taskExecution currentExecution
   * @author yanbin
   * @date 2025/9/26 15:44
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
   * @return boolean
   * @description TODO (检查任务是否在当前任务之后创建)
   * @params task currentExecution
   * @author yanbin
   * @date 2025/9/26 15:44
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
   * @return boolean
   * @description TODO (检查是否是同一类型活动)
   * @params task currentExecution
   * @author yanbin
   * @date 2025/9/26 15:44
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private boolean isSameActivityType(HistoricTaskInstanceEntity task,
      ExecutionEntity currentExecution) {
    return task.getTaskDefinitionKey().equals(currentExecution.getActivityId());
  }

  /**
   * 移除 ru_ 相关数据
   *
   * @param obj
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

  @Override
  public void deleteExtentHis() {
    HistoricTaskInstance hisTask = paramsTemplate.getHisTask();
    historyActivityInstanceMapper.deleteHisIdentitylink(hisTask.getId());
    historyActivityInstanceMapper.deleteToCommentForTaskId(hisTask.getId());
    historyActivityInstanceMapper.deleteTaskForm(hisTask.getId(), hisTask.getTaskDefinitionKey());
    log.debug("移除扩展表、历史用户关系表数据");
  }

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
      System.out.println(paramsTemplate.getHisTask().getTaskDefinitionKey());
      System.out.println(obj.getActivityId());
      System.out.println(obj.getEndTime());
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

  @Override
  public void setAssignee() {
    HistoricTaskInstance hisTask = paramsTemplate.getHisTask();
    String key = RollbackConstants.ASSIGNEE_PREFIX_KEY + hisTask.getProcessInstanceId()
        + hisTask.getTaskDefinitionKey();
    variables.put(key, assignee);
  }

  @Override
  public void setAssigneeExpr(String assigneeExpr, String assigneeListExpr) {
    // to override
  }


}
