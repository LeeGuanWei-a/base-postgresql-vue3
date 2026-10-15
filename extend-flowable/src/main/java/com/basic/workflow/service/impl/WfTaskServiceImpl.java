package com.basic.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SimpleQuery;
import com.basic.flowable.flow.CustomProcessDiagramGenerator;
import com.basic.flowable.utils.ModelUtils;
import com.basic.system.domain.SysUserRole;
import com.basic.workflow.cmd.rollBack.RollbackCmd;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.dto.WfUserTaskDto;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.IWfBpmnModelService;
import com.basic.workflow.service.IWfTaskService;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskServiceBase;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author yanbin
 * @description TODO (工作流任务管理Service实现类)
 * @date 2023/11/3 10:39
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class WfTaskServiceImpl extends WfProcessTaskServiceBase implements IWfTaskService {

  @Autowired
  private final WfFlowableTaskServiceImpl wfFlowableTaskService;
  @Autowired
  private final IWfBpmnModelService wfBpmnModelService;

  /**
   * 任务操作权限校验：确认当前登录用户是该任务的处理人/所有者/候选人。
   * 只用于「任务级」动作；流程级动作（终止/撤销/废除）没有 taskId，不走这里。
   */
  @Autowired
  private com.basic.workflow.service.wfProcessTask.auth.WfTaskAuthService taskAuthService;

  /**
   * @return java.lang.Object
   * @description TODO (完成任务)
   * @params [taskBo]
   * @author yanbin
   * @date 2023/11/3 10:39
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public Boolean complete(WfTaskBo taskBo) throws RuntimeException {
    this.taskAuthService.checkOperateAuth(taskBo.getTaskId());
    return wfCompleteTaskService.complete(taskBo);
  }

  /**
   * @return boolean
   * @description TODO (拒绝任务)-->流程终止完结
   * @params [taskBo]
   * @author yanbin
   * @date 2023/11/3 10:39
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean taskReject(WfTaskBo taskBo) {
    this.taskAuthService.checkOperateAuth(taskBo.getTaskId());
    return wfRejectTaskService.taskReject(taskBo);
  }

  /**
   * @return boolean
   * @description TODO (退回任务)
   * @params [bo]
   * @author yanbin
   * @date 2023/11/3 10:39
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean taskReturn(WfTaskBo bo) {
    this.taskAuthService.checkOperateAuth(bo.getTaskId());
    return wfReturnTaskService.taskReturn(bo);
  }

  /**
   * @return java.util.List<org.flowable.bpmn.model.FlowElement>
   * @description TODO (获取所有可回退的节点)
   * @params [bo]
   * @author yanbin
   * @date 2023/11/3 10:39
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public List<FlowElement> findReturnTaskList(WfTaskBo bo) {
    return wfReturnTaskService.findReturnTaskList(bo);
  }

  /**
   * @return boolean
   * @description TODO (认领/签收任务)
   * @params [taskBo]
   * @author yanbin
   * @date 2023/11/3 10:40
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean claim(WfTaskBo taskBo) {
    this.taskAuthService.checkOperateAuth(taskBo.getTaskId());
    return wfClaimTaskService.claim(taskBo);
  }

  /**
   * @return boolean
   * @description TODO (取消认领/签收任务)
   * @params [bo]
   * @author yanbin
   * @date 2023/11/3 10:40
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean unClaim(WfTaskBo bo) {
    this.taskAuthService.checkOperateAuth(bo.getTaskId());
    return wfClaimTaskService.unClaim(bo);
  }

  /**
   * @return boolean
   * @description TODO (委派任务)
   * @params [bo]
   * @author yanbin
   * @date 2023/11/3 10:40
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean delegateTask(WfTaskBo bo) {
    this.taskAuthService.checkOperateAuth(bo.getTaskId());
    return wfDelegateTaskService.delegateTask(bo);
  }

  /**
   * @return boolean
   * @description TODO (转办任务)
   * @params [bo]
   * @author yanbin
   * @date 2023/11/3 10:40
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean transferTask(WfTaskBo bo) {
    this.taskAuthService.checkOperateAuth(bo.getTaskId());
    return wfTransferTaskService.transferTask(bo);
  }

  /**
   * @return boolean
   * @description TODO (取消流程)--》终止流程
   * @params [bo]
   * @author yanbin
   * @date 2023/11/3 10:29
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean stopProcess(WfTaskBo bo) {
    return wfStopProcInstService.stopProcess(bo);
  }

  /**
   * @return boolean
   * @description TODO (撤回流程任务)
   * @params [taskBo]
   * @author yanbin
   * @date 2023/11/3 10:40
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean revokeProcess(WfTaskBo taskBo) {
    String assignee = taskBo.getAssignee();
    String taskId = taskBo.getTaskId();
    HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
        .taskId(taskId).singleResult();
    Object assigneeList = wfFlowableProcessMapper.selectActHiVariableById(
        historicTaskInstance.getProcessInstanceId(), "assigneeList");
    List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery()
        .processInstanceId(taskBo.getProcInsId()).executionId(historicTaskInstance.getExecutionId())
        .taskDefinitionKey(historicTaskInstance.getTaskDefinitionKey()).finished()
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
    managementService.executeCommand(new RollbackCmd(taskId, assignee));
    if (assigneeList == null) {
      ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
          .processInstanceId(taskBo.getProcInsId()).singleResult();
      // 获取流程定义模型
      BpmnModel bpmnModel = repositoryService.getBpmnModel(
          processInstance.getProcessDefinitionId());
      FlowElement flowElement = bpmnModel.getFlowElement(
          historicTaskInstance.getTaskDefinitionKey());
      if (flowElement instanceof UserTask) {
        UserTask userTask = (UserTask) flowElement;
        if (userTask.hasMultiInstanceLoopCharacteristics()) {
          MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = userTask.getLoopCharacteristics();
          if (multiInstanceLoopCharacteristics.getCompletionCondition()
              .equals("${nrOfCompletedInstances >= nrOfInstances}")
              || multiInstanceLoopCharacteristics.getCompletionCondition()
              .contains("nrOfCompletedInstances == nrOfInstances")) {
            //TODO 候选人
            if (userTask.getCandidateUsers().size() > 0) {
              assigneeList = userTask.getCandidateUsers().stream().collect(Collectors.joining(","));
            }
            //TODO 角色
            if (userTask.getCandidateGroups().size() > 0) {
              List<Long> groups = userTask.getCandidateGroups().stream().map(Long::valueOf)
                  .collect(Collectors.toList());
              List<Long> userIds = new ArrayList<>();
              LambdaQueryWrapper<SysUserRole> lqw = Wrappers.lambdaQuery(SysUserRole.class)
                  .select(SysUserRole::getUserId).in(SysUserRole::getRoleId, groups);
              userIds = SimpleQuery.list(lqw, SysUserRole::getUserId);
              assigneeList = userIds.stream().map(String::valueOf).distinct()
                  .collect(Collectors.joining(","));
            }
          }
        }
      }
    }
    List<String> assigneeIdList = new ArrayList<>();
    if (assigneeList != null) {
      assigneeIdList = Arrays.asList(assigneeList.toString().split(","));
    }
    //TODO 存在一种情况  例如A节点的会签人员是abcd  那么任务可能会存在abcdc这种，那么到c会发生d的处理人
    Iterator<HistoricTaskInstance> iterators = hisTaskList.iterator();
    while (iterators.hasNext()) {
      HistoricTaskInstance taskInstance = iterators.next();
      int currentIndex = assigneeIdList.indexOf(historicTaskInstance.getAssignee());
      int taskIndex = assigneeIdList.indexOf(taskInstance.getAssignee());
      //TODO 过滤掉当前的用户，只保留当前用户之前的处理人任务
      if (taskIndex >= currentIndex) {
        iterators.remove();
      }
    }

    List<Task> taskList = taskService.createTaskQuery()
        .processInstanceId(historicTaskInstance.getProcessInstanceId()).list();
    Task task = taskList.get(0);
    //TODO 如果同节点，同执行实例id下的历史任务大于1且不等于1的情况下，只有是会签时会发生
    if (hisTaskList.size() > 1) {
      Iterator<HistoricTaskInstance> iterator = hisTaskList.iterator();
      while (iterator.hasNext()) {
        HistoricTaskInstance taskInstance = iterator.next();
        if (taskInstance != null && !taskInstance.getId().equals(historicTaskInstance.getId())) {
          HistoricActivityInstance activityInstance = null;
          try {
            activityInstance = historyService.createHistoricActivityInstanceQuery()
                .executionId(taskInstance.getExecutionId())
                .activityId(taskInstance.getTaskDefinitionKey())
                .taskAssignee(taskInstance.getAssignee()).singleResult();
          } catch (Exception e) {
            e.printStackTrace();
            activityInstance = null;
            iterator.remove();
          }
          if (activityInstance != null) {
            wfFlowableProcessMapper.updateHisTaskByIdToExecutionId(taskInstance.getId(),
                task.getExecutionId());
            wfFlowableProcessMapper.updateHisActByIdToExecutionId(activityInstance.getId(),
                task.getExecutionId());
          }
        } else {
          iterator.remove();
        }
      }
      Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId())
          .singleResult();
      Execution parentExecution = runtimeService.createExecutionQuery()
          .executionId(execution.getParentId()).singleResult();
      if (parentExecution != null) {
        List<String> userIds = Arrays.asList(assigneeList.toString().split(","));
        //TODO nrOfInstances          一共多少实例
        //TODO nrOfCompletedInstances 已经完成审批数量(包含审批结果为通过和拒绝的)
        //TODO nrOfActiveInstances    还未完成审批数量
        wfFlowableTaskMapper.updateHisVariableByExecutionIdToLongText(parentExecution.getId(),
            "nrOfActiveInstances", String.valueOf(userIds.size() - hisTaskList.size()));
        wfFlowableTaskMapper.updateHisVariableByExecutionIdToLongText(parentExecution.getId(),
            "nrOfCompletedInstances", String.valueOf(hisTaskList.size()));
        wfFlowableTaskMapper.updateHisVariableByExecutionIdToLongText(parentExecution.getId(),
            "nrOfInstances", String.valueOf(userIds.size()));
        wfFlowableTaskMapper.updateRuVariableByExecutionIdToLongText(parentExecution.getId(),
            "nrOfActiveInstances", String.valueOf(userIds.size() - hisTaskList.size()));
        wfFlowableTaskMapper.updateRuVariableByExecutionIdToLongText(parentExecution.getId(),
            "nrOfCompletedInstances", String.valueOf(hisTaskList.size()));
        wfFlowableTaskMapper.updateRuVariableByExecutionIdToLongText(parentExecution.getId(),
            "nrOfInstances", String.valueOf(userIds.size()));
        //TODO 设置会签人员
        runtimeService.setVariable(task.getExecutionId(), "assigneeList", assigneeList);
      }
    }
    // 删除任务节点
    wfFlowableProcessMapper.deleteHisActivityInstance(taskId,
        historicTaskInstance.getTaskDefinitionKey());
    // 删除任务意见
    wfCommentInfoService.deleteToCommentForTaskId(historicTaskInstance.getId());
    return true;
  }


  /**
   * @return void
   * @description TODO (执行跳转)
   * @params [executionIds, activityId]
   * @author yanbin
   * @date 2023/12/29 9:49
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private void moveExecutionsToSingleActivityId(List<String> executionIds, String activityId) {
    try {
      runtimeService.createChangeActivityStateBuilder()
          .moveExecutionsToSingleActivityId(executionIds, activityId).changeState();
    } catch (FlowableObjectNotFoundException e) {
      throw new RuntimeException("未找到流程实例，流程可能已发生变化");
    } catch (FlowableException e) {
      throw new RuntimeException("执行撤回操作失败");
    }
  }

  /**
   * @return com.basic.common.core.domain.AjaxResult
   * @description TODO (获取下一节点用户任务)
   * @params [vo] taskId,procInsId
   * @author yanbin
   * @date 2023/11/14 13:41
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfUserTaskDto getNextFlowNode(WfTaskVo wfTaskVo) {
    return wfUserTaskInfoService.getNextFlowNode(wfTaskVo);
  }

  /**
   * @return void
   * @description TODO (仅处理仅为表达式的节点,还只处理与发起人同节点的人员，不考虑根据上一节点的处理人来设置下一节点的处理人)
   * @params [procInsId]
   * @author yanbin
   * @date 2024/2/19 14:06
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void handleFlowTask(String procInsId) throws RuntimeException {
    wfNextUserTaskService.handleFlowTask(procInsId);
  }

  /**
   * @return void
   * @description TODO (处理下一节点是交替节点问题)
   * @params [procInsId]
   * @author yanbin
   * @date 2024/2/20 17:53
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public void handleIsRoleReplace(String procInsId, String currentUserId) {
    wfNextUserTaskService.handleIsRoleReplace(procInsId, currentUserId);
  }

  /**
   * @return boolean
   * @description TODO (变更处理人)
   * @params [bo]
   * @author yanbin
   * @date 2024/2/21 14:07
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean editProcessTaskAssignee(WfTaskBo bo) {
    return wfEditTaskService.editProcessTaskAssignee(bo);
  }

  /**
   * @return boolean
   * @description TODO (修改审批意见)
   * @params [bo]
   * @author yanbin
   * @date 2024/2/21 17:48
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean updateTaskComment(WfTaskBo bo) {
    return wfEditTaskService.updateTaskComment(bo);
  }

  /**
   * @return boolean
   * @description TODO (废除所选流程)
   * @params [bo]
   * @author yanbin
   * @date 2024/2/22 9:00
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean abolishProcess(WfTaskBo bo) {
    return wfDeleteProcessService.abolishProcess(bo);
  }

  /**
   * @return boolean
   * @description TODO (删除所选任务)
   * @params [bo]
   * @author yanbin
   * @date 2024/2/22 9:25
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean deleteProcessTask(WfTaskBo bo) {
    return wfDeleteProcessService.deleteProcessTask(bo);
  }

  /**
   * @return int
   * @description TODO (修改附件信息)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/2/23 11:34
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean saveFileToModel(WfTaskBo taskBo) {
    wfFlowableTaskService.saveFileToModel(taskBo, taskBo.getTargetKey());
    return true;
  }

  /**
   * @return void
   * @description TODO (处理第一个节点为空且选择了审批人的节点)
   * @params [procInsId, startUserCodes]
   * @author yanbin
   * @date 2024/4/18 8:36
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void handleNotBlankTask(String procInsId, List<String> startUserCodes) {
    wfNextUserTaskService.handleNotBlankTask(procInsId, startUserCodes);
  }

  /**
   * @param bo
   * @return java.lang.Boolean
   * @description TODO (跳转任意节点)
   * @params [bo]
   * @author yanbin
   * @date 2024/5/28 15:37
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Boolean skipUserTask(WfTaskBo bo) {
    return wfSkipTaskService.skipUserTask(bo);
  }

  /**
   * @param bo
   * @return java.lang.Boolean
   * @description TODO (默认驳回到上一节点)
   * @params [bo]
   * @author yanbin
   * @date 2024/5/29 8:46
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Boolean rejectLastTask(WfTaskBo bo) {
    return wfReturnTaskService.rejectLastTask(bo);
  }

  /**
   * @return boolean
   * @description TODO (删除流程实例)
   * @params [instanceIds]
   * @author yanbin
   * @date 2023/11/17 16:30
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean deleteProcessByIds(String[] instanceIds) {
    return wfDeleteProcessService.deleteProcessByIds(instanceIds);
  }

  /**
   * @return com.basic.workflow.domain.dto.WfUserTaskDto
   * @description TODO (获取当前节点用户信息)
   * @params [vo] 只需要传taskId
   * @author yanbin
   * @date 2023/11/14 15:21
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfUserTaskDto getCurrentTaskNode(WfTaskVo vo) {
    return wfUserTaskInfoService.getCurrentTaskNode(vo);
  }

  /**
   * @return java.lang.Boolean
   * @description TODO (加签) 前加，后加
   * @params [bo(task参数), userCode(选中的加签人员), sign(加签方式 向前 向后加签)]
   * @author yanbin
   * @date 2024/3/7 17:25
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Boolean addSignTask(WfTaskBo bo, List<String> userCode, String sign) {
    this.taskAuthService.checkOperateAuth(bo.getTaskId());
    return wfAddSignTaskService.addSignTask(bo, userCode, sign);
  }


  /**
   * @return java.lang.String 用户，网关，结束
   * @description TODO (下一节点是什么模块)
   * @params [wfTaskVo] taskId,procInsId
   * @author yanbin
   * @date 2023/11/15 13:58
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public String getNextNode(WfTaskVo wfTaskVo) {
    return wfUserTaskInfoService.getNextNode(wfTaskVo);
  }

  /**
   * @return boolean
   * @description TODO (判断当前任务是不是多实例节点)
   * @params [task]
   * @author yanbin
   * @date 2023/11/16 9:10
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean checkMultiInstance(TaskEntity task) {
    boolean flag = false;
    //流程定义id
    String processDefId = task.getProcessDefinitionId();
    //流程activityId
    String taskDefinitionKey = task.getTaskDefinitionKey();
    //获取bpmnModel
    BpmnModel bpmnModel = wfBpmnModelService.getBpmnModelByProcessDefId(processDefId);
    //获取usertask集合
    Collection<UserTask> userTasks = ModelUtils.getAllUserTaskEvent(bpmnModel);
    if (CollectionUtils.isNotEmpty(userTasks)) {
      for (UserTask userTask : userTasks) {
        if (userTask.getId().equals(taskDefinitionKey)
            && userTask.hasMultiInstanceLoopCharacteristics()) {
          flag = true;
          break;
        }
      }
    }
    return flag;
  }

  /**
   * @param procInsId
   * @return java.lang.Boolean
   * @description TODO (根据实例id判断流程是否存在)
   * @params [procInsId]
   * @author yanbin
   * @date 2024/6/4 10:35
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Boolean checkProcessIsExistByProcInsId(String procInsId) {
    return wfFlowableTaskService.checkProcessIsExistByProcInsId(procInsId);
  }

  /**
   * @return com.basic.workflow.domain.vo.WfTaskVo
   * @description TODO (判断当前用户是否是当前任务的处理人)
   * @params [bo]
   * @author yanbin
   * @date 2024/6/4 14:30
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfTaskVo checkUserIsAuditByProcInsIdAndUserIdAndGroupId(WfTaskBo bo) {
    return wfFlowableTaskService.checkUserIsAuditByProcInsIdAndUserIdAndGroupId(bo);
  }

  /**
   * @param bo
   * @return com.basic.workflow.domain.vo.WfTaskVo
   * @description TODO (判断当前用户是否是当前流程的发起人)
   * @params [bo]
   * @author yanbin
   * @date 2024/6/4 14:30
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfTaskVo checkUserIsStartByProcInsIdAndUserIdAndGroupId(WfTaskBo bo) {
    return wfFlowableTaskService.checkUserIsStartByProcInsIdAndUserIdAndGroupId(bo);
  }

  /**
   * @return java.lang.Object
   * @description TODO (完成任务不通过流转回顺序会签的当前处理人的上一个人(顺序会签))
   * @params bo
   * @author yanbin
   * @date 2025/9/28 8:56
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Object mulitiSequentialUnComplete(WfTaskBo bo) {
    this.taskAuthService.checkOperateAuth(bo.getTaskId());
    return wfCompleteTaskService.mulitiSequentialUnComplete(bo);
  }

  /**
   * @return java.io.InputStream
   * @description TODO (获取流程过程图)
   * @params [procInsId]
   * @author yanbin
   * @date 2023/11/3 10:40
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public InputStream diagram(String procInsId) {
    String processDefinitionId;
    // 获取当前的流程实例
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceId(procInsId).singleResult();
    // 如果流程已经结束，则得到结束节点
    if (Objects.isNull(processInstance)) {
      HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery()
          .processInstanceId(procInsId).singleResult();

      processDefinitionId = pi.getProcessDefinitionId();
    } else { // 如果流程没有结束，则取当前活动节点
      // 根据流程实例ID获得当前处于活动状态的ActivityId合集
      ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId)
          .singleResult();
      processDefinitionId = pi.getProcessDefinitionId();
    }

    // 获得活动的节点
    List<HistoricActivityInstance> highLightedFlowList = historyService.createHistoricActivityInstanceQuery()
        .processInstanceId(procInsId).orderByHistoricActivityInstanceStartTime().asc().list();

    List<String> highLightedFlows = new ArrayList<>();
    List<String> highLightedNodes = new ArrayList<>();
    // 高亮线
    for (HistoricActivityInstance tempActivity : highLightedFlowList) {
      if ("sequenceFlow".equals(tempActivity.getActivityType())) {
        // 高亮线
        highLightedFlows.add(tempActivity.getActivityId());
      } else {
        // 高亮节点
        highLightedNodes.add(tempActivity.getActivityId());
      }
    }

    // 获取流程图
    BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
    ProcessEngineConfiguration configuration = processEngine.getProcessEngineConfiguration();
    // 获取自定义图片生成器
    ProcessDiagramGenerator diagramGenerator = new CustomProcessDiagramGenerator();
    return diagramGenerator.generateDiagram(bpmnModel, "png", highLightedNodes, highLightedFlows,
        configuration.getActivityFontName(), configuration.getLabelFontName(),
        configuration.getAnnotationFontName(), configuration.getClassLoader(), 1.0, true);
  }
}


