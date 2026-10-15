package com.basic.workflow.service.wfProcessTask.complete;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SimpleQuery;
import com.basic.common.utils.StringUtils;
import com.basic.constant.FlowConstant;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.system.domain.SysUserRole;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.dto.WfUserTaskDto;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import com.basic.workflow.service.wfProcessTask.delete.WfDeleteProcessService;
import com.basic.workflow.service.wfProcessTask.wfUserTask.WfNextUserTaskService;
import com.basic.workflow.service.wfProcessTask.wfUserTask.WfUserTaskInfoService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yanbin
 * @description TODO (flowable审批任务)
 * @date 2024/03/01
 */
@Service
public class WfCompleteTaskService extends WfProcessTaskBase {

  @Autowired
  private WfNextUserTaskService wfNextUserTaskService;
  @Autowired
  private WfUserTaskInfoService wfUserTaskInfoService;
  @Autowired
  private WfDeleteProcessService wfDeleteProcessService;

  /**
   * @return com.basic.common.core.domain.AjaxResult
   * @description TODO (审批任务 通过)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/3/1 16:39
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Transactional(rollbackFor = Exception.class)
  public Boolean complete(WfTaskBo taskBo) throws RuntimeException {
    //TODO 1. 是否存在
    if (StringUtils.isNotBlank(taskBo.getProcInsId()) && StringUtils.isNotBlank(
        taskBo.getTaskId())) {
      //TODO 2.查看当前任务是存在
      TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskBo.getTaskId())
          .singleResult();
      taskBo.setProcDefId(taskEntity.getProcessDefinitionId());
      List<Task> currentTaskList = taskService.createTaskQuery()
          .processInstanceId(taskBo.getProcInsId()).list();
      WfTaskVo wfTaskVo = new WfTaskVo();
      wfTaskVo.setTaskId(taskBo.getTaskId());
      wfTaskVo.setProcInsId(taskBo.getProcInsId());
      wfTaskVo.setVariables(taskBo.getVariables());
      //TODO 3.获取当前节点详细信息
      WfUserTaskDto currentTask = wfUserTaskInfoService.getCurrentTaskNode(wfTaskVo);
      //TODO 4.获取下一节点详细信息
      WfUserTaskDto nextTask = wfUserTaskInfoService.getNextFlowNode(wfTaskVo);
      if (nextTask.getNodeType().equals("UserTask")) {
        //TODO 4.1下一节点未绑定审批人，且当前未选择审批人
        UserTask userTask = nextTask.getUserTask();
        if (!StringUtils.isNotBlank(taskBo.getNextUserIds()) && !StringUtils.isNotEmpty(
            userTask.getCandidateUsers()) && !StringUtils.isNotEmpty(userTask.getCandidateGroups())
            && !StringUtils.isNotBlank(userTask.getAssignee())) {
          throw new RuntimeException("下一节点未选择审批人,请选择审批人");
        }
      }
      Boolean multiInstance = false;
      if (taskEntity != null) {
        String taskId = taskBo.getTaskId();
        String type = FlowComment.NORMAL.getType();
        //TODO complete 手动更新----审批类型
        if (StringUtils.isNotBlank(taskBo.getCommentType())) {
          type = taskBo.getCommentType();
        }
        //TODO 5.1如果选择了指定审批人 (处理下一节点)
        if (StringUtils.isNotBlank(taskBo.getMultiInstance()) && (
            nextTask.getNodeType().equals("UserTask") || nextTask.getNodeType()
                .equals("ExclusiveGateway"))) {
          multiInstance = injectUserTaskInProcessInstanceService.replaceUserTaskInProcessInstance(
              taskBo, nextTask);
          this.updateProcDefInfo(taskBo);
        }
        //TODO 5.2 委派处理
        if (DelegationState.PENDING.equals(taskEntity.getDelegationState())) {
          //TODO 5.2.1生成历史记录
          TaskEntity task = wfFlowableTaskService.createSubTask(taskBo.getCurrentUserId(),
              taskEntity, taskEntity.getId(), taskBo.getCurrentUserId());
          taskService.complete(task.getId());
          taskId = task.getId();
          //TODO 5.2.2执行委派
          taskService.resolveTask(taskBo.getTaskId(), taskBo.getVariables());
        } else {
          //TODO 5.3.1修改执行人 其实我这里就相当于签收了
          taskService.setAssignee(taskBo.getTaskId(), taskBo.getCurrentUserId());
          //TODO 5.3.2执行任务
          taskService.complete(taskBo.getTaskId(), taskBo.getVariables());
          wfFlowableTaskMapper.updateHistorcTask(taskBo.getTaskId(), taskBo.getCurrentUserId());
          //TODO 5.3.3处理加签父任务
          String parentTaskId = taskEntity.getParentTaskId();
          //审批类型   默认是通过
          if (StringUtils.isNotBlank(parentTaskId)) {
            String tableName = managementService.getTableName(TaskEntity.class);
            //查询是否有子任务
            String sql =
                "select count(1) from " + tableName + " where PARENT_TASK_ID_=#{parentTaskId}";
            long subTaskCount = taskService.createNativeTaskQuery().sql(sql)
                .parameter("parentTaskId", parentTaskId).count();
            type = FlowComment.JQSP.getType();
            if (subTaskCount == 0) {
              Task task = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
              // 处理前后加签的任务
              taskService.resolveTask(parentTaskId);
              //后加签可能下一节点遇到网关，需要传参数
              if (FlowConstant.AFTER_ADDSIGN.equals(task.getScopeType())) {
                taskService.complete(parentTaskId, taskBo.getVariables());
              }
            }
          }
        }
        //TODO 6.1生成审批意见
        this.addComment(taskId, taskBo.getCurrentUserId(), taskBo.getProcInsId(), type,
            taskBo.getComment());
        //TODO 6.2保存自定义表单内容
        this.saveTaskForForm(taskBo.getTaskId(), taskBo.getProcInsId(), taskBo.getCurrentUserId(),
            taskEntity.getTaskDefinitionKey(), taskBo.getFormData());
      } else {
        throw new RuntimeException("没有此任务，请确认!");
      }
      //TODO 获取当前实例信息--->用于判断网关结束事件
      ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
          .processInstanceId(taskBo.getProcInsId()).singleResult();
      List<Task> taskList = taskService.createTaskQuery().processInstanceId(taskBo.getProcInsId())
          .list();
      //TODO 如果遇到了网关的情况，并且是或签的情况下，这里需要考虑删除多余的任务，保留一条任务
      if (StringUtils.isNotBlank(taskBo.getMultiInstance()) && (
          nextTask.getNodeType().equals("UserTask") || nextTask.getNodeType()
              .equals("ExclusiveGateway"))) {
        if (taskList.size() > 1 && taskBo.getMultiInstance().equals("ParallelMultiInstance")) {
          Task task = taskList.get(0);
          taskList.forEach(t -> {
            if (!task.getId().equals(t.getId())) {
              WfTaskBo bo = new WfTaskBo();
              bo.setProcessStatus("running");
              bo.setProcInsId(t.getProcessInstanceId());
              bo.setTaskId(t.getId());
              wfDeleteProcessService.deleteProcessTask(bo);
            }
          });
          taskService.setAssignee(task.getId(), null);
        }
      }
      //TODO 流程未结束 进行下一节点操作
      if (processInstance != null && nextTask.getNodeType() != null && !nextTask.getNodeType()
          .equals("EndEvent")) {
        //TODO 7.处理下一节点为表达式的节点(
        // 需要判断
        // 1.下一节点是固定值绑定的表达式节点，
        // 2.当前节点任务仅仅只有一条，也就是非多条任务的节点，
        // 3.没有选择制定审批人的操作)
        if (nextTask.getDataType() != null && nextTask.getDataType().equals("固定值")
            && currentTaskList.size() == 1 && !multiInstance) {
          wfNextUserTaskService.handleFlowTask(taskBo.getProcInsId());
        }
        //TODO 8.处理相同处理人节点设置
        wfNextUserTaskService.nextTaskForIsSkip(taskBo, nextTask, currentTask, taskEntity);
        //TODO 9.处理当前任务是被退回的任务 再次流转到多实例节点时 已审批人员无需再次审批
        wfNextUserTaskService.handleReturnTaskInfo(taskBo.getProcInsId(), nextTask);
        //TODO 10.处理 处理人为空自动跳过的节点
        wfNextUserTaskService.handleNullTaskSkip(taskBo.getProcInsId());
      }
      //TODO 11.保存上传的附件
      if (StringUtils.isNotEmpty(taskBo.getFileList())) {
        wfFlowableTaskService.saveFileToModel(taskBo, taskEntity.getTaskDefinitionKey());
      }
      // 设置任务节点名称
      taskBo.setTaskName(taskEntity.getName());
      //TODO 12.处理抄送用户
      if (!this.makeCopy(taskBo)) {
        throw new RuntimeException("抄送任务失败");
      }
    } else {
      throw new RuntimeException("请输入正确的参数!");
    }
    return true;
  }

  /**
   * @return void
   * @description TODO (指定审批人之后，流程定义发生变化，则改变流程定义)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/5/8 11:36
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private void updateProcDefInfo(WfTaskBo taskBo) {
    HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
        .processInstanceId(taskBo.getProcInsId()).singleResult();
    //TODO 获取旧的流程定义
    ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(
        historicProcessInstance.getId());
    //TODO 判断流程定义是否发生了变化
    if (!extendProcinst.getProcDefId().equals(historicProcessInstance.getProcessDefinitionId())) {
//            wfFlowableTaskMapper.updateReProcDefCategoryByProcDefId(historicProcessInstance.getProcessDefinitionId(), processDefinition.getCategory(), processDefinition.getDeploymentId());
      //更新实例信息的流程定义
      wfFlowableTaskMapper.updateHiProcInstByProcInsId(historicProcessInstance.getId(),
          extendProcinst.getProcDefId());
    }
  }

  /***
   * @description TODO (完成任务不通过流转回顺序会签的当前处理人的上一个人(顺序会签))
   * @params bo
   * @return java.lang.Object
   * @author yanbin
   * @date 2025/9/28 8:57
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public Object mulitiSequentialUnComplete(WfTaskBo taskBo) {
    //TODO 此方法只适用于会签，串行（顺序会签）回退到当前会签的处理人的上一个处理人同节点
    if (StringUtils.isNotBlank(taskBo.getProcInsId()) && StringUtils.isNotBlank(
        taskBo.getTaskId())) {
      //TODO 查看当前任务是存在
      TaskEntity currentTask = (TaskEntity) taskService.createTaskQuery().taskId(taskBo.getTaskId())
          .singleResult();
      //TODO 获取当前实例
      ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
          .processInstanceId(taskBo.getProcInsId()).singleResult();
      if (processInstance == null) {
        throw new RuntimeException("流程已结束");
      }
      if (currentTask == null) {
        throw new RuntimeException("任务不存在");
      }
      Execution execution = runtimeService.createExecutionQuery()
          .executionId(currentTask.getExecutionId()).singleResult();
      if (execution == null) {
        throw new RuntimeException("当前任务的执行实例查询失败，请联系管理员");
      }
      //TODO  获取会签节点所有已完成任务---------必须根据执行实例查询，根据节点和实例查询的话，同一个节点多次生成会重复查询
      //TODO 获取会签的处理人
      Object assigneeIdStr = wfFlowableProcessMapper.selectActHiVariableById(
          processInstance.getProcessInstanceId(), "assigneeList");
      List<String> assigneeIdList = new ArrayList<>();
      if (assigneeIdStr != null) {
        assigneeIdList = Arrays.asList(assigneeIdStr.toString().split(","));
      }
      List<HistoricTaskInstance> completedTaskList = historyService.createHistoricTaskInstanceQuery()
          .processInstanceId(taskBo.getProcInsId()).executionId(currentTask.getExecutionId())
          .taskDefinitionKey(currentTask.getTaskDefinitionKey()).finished()
          .orderByHistoricTaskInstanceEndTime().desc().list().stream()
          //TODO  这里获取了completedTaskList 这个之后，如果这个数据集存在 ababcbac 这种处理之后，那么数据量会变多，需要过滤
          //添加唯一性过滤（按处理人分组取最新）
          .collect(Collectors.toMap(task -> task.getTaskDefinitionKey() + ":" + task.getAssignee(),
              // 唯一键
              Function.identity(),
              (existing, replacement) -> existing.getEndTime().after(replacement.getEndTime())
                  ? existing : replacement)).values().stream()
          .sorted(Comparator.comparing(HistoricTaskInstance::getEndTime).reversed())
          .collect(Collectors.toList());
      // 获取流程定义模型
      BpmnModel bpmnModel = repositoryService.getBpmnModel(
          processInstance.getProcessDefinitionId());
      FlowElement flowElement = bpmnModel.getFlowElement(currentTask.getTaskDefinitionKey());
      if (flowElement instanceof UserTask) {
        UserTask userTask = (UserTask) flowElement;
        if (userTask.hasMultiInstanceLoopCharacteristics()) {
          MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = userTask.getLoopCharacteristics();
          if (multiInstanceLoopCharacteristics.getCompletionCondition()
              .equals("${nrOfCompletedInstances >= nrOfInstances}")
              || multiInstanceLoopCharacteristics.getCompletionCondition()
              .contains("nrOfCompletedInstances == nrOfInstances")) {
            if (multiInstanceLoopCharacteristics.isSequential()) {
              //TODO 顺序会签节点 第一个处理人点击了不通过， 不能进行回退操作，反馈让根据业务需求进行回退操作
              if (completedTaskList.size() == 0) {
                //TODO 存在当前节点已经处理过了，但是被撤回来了，然后在点击不通过，此时的执行实例已经发生了变化
                List<HistoricTaskInstance> allTaskList = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(taskBo.getProcInsId())
                    .taskDefinitionKey(currentTask.getTaskDefinitionKey())
                    .orderByHistoricTaskInstanceEndTime().finished().desc().list();
                List<String> assigneeList = new ArrayList<>();
                for (HistoricTaskInstance task : allTaskList) {
                  if (StringUtils.isNotBlank(task.getAssignee())) {
                    if (!assigneeList.contains(task.getAssignee()) && !currentTask.getAssignee()
                        .equals(task.getAssignee())) {
                      assigneeList.add(task.getAssignee());
                    }
                  }
                }
                System.out.println(assigneeList.contains(currentTask.getAssignee()));
                //TODO 历史节点处理人存在，且 当前用户不在历史处理人中
                if (assigneeList.size() > 0 && !assigneeList.contains(currentTask.getAssignee())) {
                  HistoricTaskInstance hisTask = allTaskList.get(0);
                  //TODO 二次验证 确保执行实例不相同
                  ExecutionEntity currentExecutionEntity = (ExecutionEntity) runtimeService.createExecutionQuery()
                      .executionId(currentTask.getExecutionId()).singleResult();
                  if (currentExecutionEntity != null && (
                      !hisTask.getExecutionId().equals(currentExecutionEntity.getId())
                          || !hisTask.getExecutionId()
                          .equals(currentExecutionEntity.getParentId()))) {
                    completedTaskList = allTaskList;
                  } else {
                    throw new RuntimeException(
                        "当前节点为顺序会签节点且当前处理人为当前节点的第一个处理，不能进行操作，请根据业务需求重新选择操作！");
                  }
                } else {
                  throw new RuntimeException(
                      "当前节点为顺序会签节点且当前处理人为当前节点的第一个处理，不能进行操作，请根据业务需求重新选择操作！");
                }
              }

              //TODO 如果会签的人员为空，那么直接获取节点上的处理人
              if (assigneeIdList.size() == 0) {
                //TODO 候选人
                if (userTask.getCandidateUsers().size() > 0) {
                  assigneeIdList.addAll(userTask.getCandidateUsers());
                }
                //TODO 角色
                if (userTask.getCandidateGroups().size() > 0) {
                  List<Long> groups = userTask.getCandidateGroups().stream().map(Long::valueOf)
                      .collect(Collectors.toList());
                  List<Long> userIds = new ArrayList<>();
                  LambdaQueryWrapper<SysUserRole> lqw = Wrappers.lambdaQuery(SysUserRole.class)
                      .select(SysUserRole::getUserId).in(SysUserRole::getRoleId, groups);
                  userIds = SimpleQuery.list(lqw, SysUserRole::getUserId);
                  for (Long userId : userIds) {
                    if (!assigneeIdList.contains(userId)) {
                      assigneeIdList.add(userId.toString());
                    }
                  }
                }
              }
              //TODO 存在一种情况  例如A节点的会签人员是abcd  那么任务可能会存在abcdc这种，那么到c会发生d的处理人
              Iterator<HistoricTaskInstance> iterator = completedTaskList.iterator();
              while (iterator.hasNext()) {
                HistoricTaskInstance taskInstance = iterator.next();
                int currentIndex = assigneeIdList.indexOf(currentTask.getAssignee());
                int taskIndex = assigneeIdList.indexOf(taskInstance.getAssignee());
                //TODO 过滤掉当前的用户，只保留当前用户之前的处理人任务
                if (taskIndex >= currentIndex) {
                  iterator.remove();
                }
              }
              if (completedTaskList.size() == 0) {
                throw new RuntimeException(
                    "当前节点为顺序会签节点且当前处理人为当前节点的第一个处理，不能进行操作，请根据业务需求重新选择操作！");
              } else {
                //TODO  开始执行回退到顺序会签的上一个处理人
                Map<String, Object> variables = new HashMap<>();
                variables.put("_FLOWABLE_SKIP_EXPRESSION_ENABLED", true); // 跳过表达式计算
                //确保控制变量设置在执行实例级别而非流程实例级别
                runtimeService.setVariableLocal(currentTask.getExecutionId(),
                    "_FLOWABLE_NO_TASK_CREATE",
                    true);// Flowable内置变量，阻止任务创建-----这里创建一条当前处理人，当前节点的任务，将这个任务替换
                // TODO 更新多实例计数器（减少完成实例数）
                int newCompleted = completedTaskList.size() - 1;
                //TODO nrOfInstances          一共多少实例
                //TODO nrOfCompletedInstances 已经完成审批数量(包含审批结果为通过和拒绝的)
                //TODO nrOfActiveInstances    还未完成审批数量
                variables.put("nrOfCompletedInstances", newCompleted);
                variables.put("loopCounter", newCompleted);
                //TODO 当前用户完成任务
                taskService.complete(currentTask.getId(), variables);
                wfFlowableTaskMapper.updateHisVariableTextByKeyAndValue(
                    currentTask.getProcessInstanceId(), "nrOfCompletedInstances", newCompleted);
                wfFlowableTaskMapper.updateHisVariableTextByKeyAndValue(
                    currentTask.getProcessInstanceId(), "loopCounter", newCompleted);
                wfFlowableTaskMapper.updateRuVariableTextByKeyAndValue(
                    currentTask.getProcessInstanceId(), "nrOfCompletedInstances", newCompleted);
                wfFlowableTaskMapper.updateRuVariableTextByKeyAndValue(
                    currentTask.getProcessInstanceId(), "loopCounter", newCompleted);
                //TODO 手动获取当前任务
                List<Task> taskList = taskService.createTaskQuery()
                    .processInstanceId(currentTask.getProcessInstanceId()).list();
                //TODO 将这条任务的处理人变更为上一个处理人
                Task newTask = taskList.get(0);
                HistoricTaskInstance lastTask = completedTaskList.get(0);
                taskService.setAssignee(newTask.getId(), lastTask.getAssignee());

                //TODO 生成审批意见---会签回退（原来硬编码 "3" 会被显示成"驳回"）
                this.addComment(currentTask.getId(), taskBo.getCurrentUserId(),
                    taskBo.getProcInsId(), FlowComment.HSH.getType(), taskBo.getComment());
                //TODO 保存自定义表单内容
                this.saveTaskForForm(taskBo.getTaskId(), taskBo.getProcInsId(),
                    taskBo.getCurrentUserId(), currentTask.getTaskDefinitionKey(),
                    taskBo.getFormData());
                //TODO 保存上传的附件
                if (StringUtils.isNotEmpty(taskBo.getFileList())) {
                  wfFlowableTaskService.saveFileToModel(taskBo, currentTask.getTaskDefinitionKey());
                }
                // 设置任务节点名称
                taskBo.setTaskName(currentTask.getName());
                //TODO 处理抄送用户
                if (!this.makeCopy(taskBo)) {
                  throw new RuntimeException("抄送任务失败");
                }
              }
            } else {
              throw new RuntimeException("当前节点未非顺序会签节点，无法进行回退，请联系管理员");
            }
          } else {
            throw new RuntimeException("当前节点为或签节点，无法进行回退，请联系管理员");
          }
        } else {
          throw new RuntimeException("当前节点非多实例节点，无法进行回退，请联系管理员");
        }
      }


    }
    return null;
  }
}
