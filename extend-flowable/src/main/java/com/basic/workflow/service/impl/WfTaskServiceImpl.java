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
import cn.hutool.core.collection.CollUtil;
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
 * @description 工作流任务管理的业务实现，负责任务的办理、退回与驳回、转办委派、加签以及撤回、流程图生成等操作
 * @author yanbin
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
   * @return Boolean 审批通过返回 true
   * @description 校验当前用户对该任务的操作权限后完成当前任务，并把流程实例推进到下一节点，同时写入审批意见与表单快照
   * @params [taskBo] 任务参数，含 taskId、procInsId、审批意见、表单数据与下一节点审批人
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public Boolean complete(WfTaskBo taskBo) throws RuntimeException {
    this.taskAuthService.checkOperateAuth(taskBo.getTaskId());
    return wfCompleteTaskService.complete(taskBo);
  }

  /**
   * @return boolean 驳回成功返回 true
   * @description 校验操作权限后写入驳回意见，把流程状态置为已终结并直接终止到结束节点，同时保存表单、附件与抄送
   * @params [taskBo] 任务参数，含 taskId、procInsId、驳回意见与表单数据
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean taskReject(WfTaskBo taskBo) {
    this.taskAuthService.checkOperateAuth(taskBo.getTaskId());
    return wfRejectTaskService.taskReject(taskBo);
  }

  /**
   * @return boolean 退回成功返回 true
   * @description 校验操作权限后把当前节点退回指定的历史节点，只允许退到与当前节点串行的上游节点，并写退回意见、还原目标节点处理人
   * @params [bo] 任务参数，targetKey 指定退回的目标节点
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public boolean taskReturn(WfTaskBo bo) {
    this.taskAuthService.checkOperateAuth(bo.getTaskId());
    return wfReturnTaskService.taskReturn(bo);
  }

  /**
   * @return List<FlowElement> 可退回的节点元素列表，无可退节点时为空列表
   * @description 列出当前任务可退回的节点，取已完成且能到达当前节点的上游用户任务
   * @params [bo] 任务参数，需含 taskId
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public List<FlowElement> findReturnTaskList(WfTaskBo bo) {
    return wfReturnTaskService.findReturnTaskList(bo);
  }

  /**
   * @return boolean 认领成功返回 true
   * @description 校验操作权限后把任务的办理人设置为当前登录用户，完成签收
   * @params [taskBo] 任务参数，含 taskId 与 currentUserId
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean claim(WfTaskBo taskBo) {
    this.taskAuthService.checkOperateAuth(taskBo.getTaskId());
    return wfClaimTaskService.claim(taskBo);
  }

  /**
   * @return boolean 取消认领成功返回 true
   * @description 校验操作权限后清空任务的办理人，让任务回到待签收状态
   * @params [bo] 任务参数，需含 taskId
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean unClaim(WfTaskBo bo) {
    this.taskAuthService.checkOperateAuth(bo.getTaskId());
    return wfClaimTaskService.unClaim(bo);
  }

  /**
   * @return boolean 委派成功返回 true
   * @description 校验操作权限后写委派意见并保存表单与附件，清空任务所有者、把办理人置为当前用户，再交给引擎委派给被委派人办理
   * @params [bo] 任务参数，含 taskId、被委派人 assignee、意见与表单数据
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean delegateTask(WfTaskBo bo) {
    this.taskAuthService.checkOperateAuth(bo.getTaskId());
    return wfDelegateTaskService.delegateTask(bo);
  }

  /**
   * @return boolean 转办成功返回 true
   * @description 校验操作权限后把任务办理人直接改为被转办人、所有者记为当前用户，并写审批意见、保存表单与附件
   * @params [bo] 任务参数，含 taskId、被转办人 assignee、意见与表单数据
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean transferTask(WfTaskBo bo) {
    this.taskAuthService.checkOperateAuth(bo.getTaskId());
    return wfTransferTaskService.transferTask(bo);
  }

  /**
   * @return boolean 全部流程处理完成返回 true
   * @description 逐条终止运行中的流程实例，把执行实例推进到结束节点并写终止意见、把流程状态置为已取消
   * @params [bo] 任务参数，含 procInsIdList 与终止意见
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean stopProcess(WfTaskBo bo) {
    return wfStopProcInstService.stopProcess(bo);
  }

  /**
   * @return boolean 撤回成功返回 true
   * @description 用撤回命令把流程回滚到该任务所在节点重新生成待办，修正会签节点的多实例计数变量，并删除该任务的历史活动与审批意见
   * @params [taskBo] 任务参数，含 taskId、procInsId 与要回退到的处理人 assignee
   * @author liguanwei
   * @date 2026/10/17 20:52
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
        // 这里获取了completedTaskList 这个之后，如果这个数据集存在 ababcbac 这种处理之后，那么数据量会变多，需要过滤
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
            // 候选人
            if (userTask.getCandidateUsers().size() > 0) {
              assigneeList = userTask.getCandidateUsers().stream().collect(Collectors.joining(","));
            }
            // 角色
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
    // 存在一种情况  例如A节点的会签人员是abcd  那么任务可能会存在abcdc这种，那么到c会发生d的处理人
    Iterator<HistoricTaskInstance> iterators = hisTaskList.iterator();
    while (iterators.hasNext()) {
      HistoricTaskInstance taskInstance = iterators.next();
      int currentIndex = assigneeIdList.indexOf(historicTaskInstance.getAssignee());
      int taskIndex = assigneeIdList.indexOf(taskInstance.getAssignee());
      // 过滤掉当前的用户，只保留当前用户之前的处理人任务
      if (taskIndex >= currentIndex) {
        iterators.remove();
      }
    }

    List<Task> taskList = taskService.createTaskQuery()
        .processInstanceId(historicTaskInstance.getProcessInstanceId()).list();
    Task task = taskList.get(0);
    // 如果同节点，同执行实例id下的历史任务大于1且不等于1的情况下，只有是会签时会发生
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
            log.warn("查询历史活动实例失败，跳过该任务 taskId={}, err={}", taskInstance.getId(), e.getMessage());
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
        // nrOfInstances          一共多少实例
        // nrOfCompletedInstances 已经完成审批数量(包含审批结果为通过和拒绝的)
        // nrOfActiveInstances    还未完成审批数量
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
        // 设置会签人员
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
   * @description 把执行实例批量迁移到指定节点，用于撤回时的流程状态变更，流程实例已变化或迁移失败时转为运行时异常
   * @params [executionIds, activityId] 执行实例ID列表；目标节点ID
   * @author liguanwei
   * @date 2026/10/17 20:52
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
   * @return WfUserTaskDto 下一节点信息，含节点类型、审批人候选与是否多实例
   * @description 预测当前任务的下一节点，返回节点类型、可选审批人与表单信息
   * @params [wfTaskVo] 任务信息，需含 taskId 或 procInsId
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfUserTaskDto getNextFlowNode(WfTaskVo wfTaskVo) {
    return wfUserTaskInfoService.getNextFlowNode(wfTaskVo);
  }

  /**
   * @description 处理下一节点为表达式的节点，按表达式结果为新建任务自动设置处理人，只覆盖与发起人同类的表达式绑定
   * @params [procInsId] 流程实例ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void handleFlowTask(String procInsId) throws RuntimeException {
    wfNextUserTaskService.handleFlowTask(procInsId);
  }

  /**
   * @description 处理下一节点按角色绑定审批人时，把角色替换为角色下的具体人员
   * @params [procInsId, currentUserId] 流程实例ID；当前处理人标识
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public void handleIsRoleReplace(String procInsId, String currentUserId) {
    wfNextUserTaskService.handleIsRoleReplace(procInsId, currentUserId);
  }

  /**
   * @return boolean 变更成功返回 true，processStatus 为空或取值非预期时返回 false
   * @description 变更任务处理人，运行中的任务改运行态任务表与其身份关联，已完成的任务改历史任务与历史身份关联
   * @params [bo] 任务参数，含 taskId、processStatus（running/completed）与 assignee
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean editProcessTaskAssignee(WfTaskBo bo) {
    return wfEditTaskService.editProcessTaskAssignee(bo);
  }

  /**
   * @return boolean 修改成功返回 true
   * @description 先删除该任务在流程引擎意见表里的旧意见再写入新意见，并按任务与处理人更新扩展意见表记录
   * @params [bo] 任务参数，含 taskId、procInsId、currentUserId 与新的意见内容
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean updateTaskComment(WfTaskBo bo) {
    return wfEditTaskService.updateTaskComment(bo);
  }

  /**
   * @return boolean 处理成功返回 true，未传实例列表时返回 false
   * @description 废除流程，未结束的实例先终止，再删除历史实例及其历史意见、业务拓展信息、抄送与流程表单
   * @params [bo] 任务参数，含 procInsIdList
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean abolishProcess(WfTaskBo bo) {
    return wfDeleteProcessService.abolishProcess(bo);
  }

  /**
   * @return boolean 删除成功返回 true，processStatus 为空时返回 false
   * @description 按单条任务粒度删除任务，运行中的任务同步调整会签多实例计数并清理运行态任务、执行、活动、身份关联、变量与表单，已完成的任务只删历史记录与意见
   * @params [bo] 任务参数，含 taskId、procInsId 与 processStatus
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean deleteProcessTask(WfTaskBo bo) {
    return wfDeleteProcessService.deleteProcessTask(bo);
  }

  /**
   * @return boolean 固定返回 true，附件写入失败时由底层抛出异常
   * @description 保存当前任务上传的附件并绑定到目标节点
   * @params [taskBo] 任务参数，含 procInsId、targetKey 与文件列表
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean saveFileToModel(WfTaskBo taskBo) {
    wfFlowableTaskService.saveFileToModel(taskBo, taskBo.getTargetKey());
    return true;
  }

  /**
   * @description 处理首节点未绑定审批人的情况，把发起时选中的审批人设置为首节点处理人
   * @params [procInsId, startUserCodes] 流程实例ID；发起时选中的审批人标识列表
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void handleNotBlankTask(String procInsId, List<String> startUserCodes) {
    wfNextUserTaskService.handleNotBlankTask(procInsId, startUserCodes);
  }

  /**
   * @return Boolean 跳转成功返回 true，未指定目标节点或实例不存在时返回 false
   * @description 把当前节点直接迁移到指定节点，并写跳转意见、表单与抄送
   * @params [bo] 任务参数，含 procInsId、taskId（发起时可空）、skipActivityId 与意见
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Boolean skipUserTask(WfTaskBo bo) {
    return wfSkipTaskService.skipUserTask(bo);
  }

  /**
   * @return Boolean 处理完成返回 true
   * @description 取流程图上的上一个用户任务作为目标节点执行退回，即默认驳回到上一节点
   * @params [bo] 任务参数，需含 taskId 与 procInsId
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Boolean rejectLastTask(WfTaskBo bo) {
    return wfReturnTaskService.rejectLastTask(bo);
  }

  /**
   * @return Boolean 删除成功返回 true
   * @description 按实例ID批量删除已结束流程的历史数据，连带清理历史意见、业务拓展信息、抄送与流程表单，仍处于进行中的实例不允许删除
   * @params [instanceIds] 流程实例ID数组
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean deleteProcessByIds(String[] instanceIds) {
    return wfDeleteProcessService.deleteProcessByIds(instanceIds);
  }

  /**
   * @return WfUserTaskDto 当前节点信息，含流程定义KEY、节点类型与当前用户可用的操作按钮
   * @description 取当前节点信息，返回节点类型、审批人候选与当前用户可用的操作按钮
   * @params [vo] 节点信息，只需传 taskId
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfUserTaskDto getCurrentTaskNode(WfTaskVo vo) {
    return wfUserTaskInfoService.getCurrentTaskNode(vo);
  }

  /**
   * @return Boolean 加签成功返回 true，任务不存在时返回 false
   * @description 校验操作权限后在当前节点插入子任务完成加签，首次加签把任务所有者置为操作人并清空办理人，再为每个被加签人创建子任务并写意见、表单与附件
   * @params [bo, userCode, sign] 任务参数，含 taskId、procInsId、意见与表单数据；被加签人员的用户标识列表；加签方式，before 为前加签、after 为后加签
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Boolean addSignTask(WfTaskBo bo, List<String> userCode, String sign) {
    this.taskAuthService.checkOperateAuth(bo.getTaskId());
    return wfAddSignTaskService.addSignTask(bo, userCode, sign);
  }


  /**
   * @return String 下一节点类型，如 UserTask、ExclusiveGateway、EndEvent
   * @description 判断下一节点是用户任务、网关还是结束事件
   * @params [wfTaskVo] 任务信息，含 taskId 与 procInsId
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public String getNextNode(WfTaskVo wfTaskVo) {
    return wfUserTaskInfoService.getNextNode(wfTaskVo);
  }

  /**
   * @return boolean 该节点配置了多实例（会签）返回 true，否则返回 false
   * @description 按流程定义取出全部用户任务，判断任务所属节点是否配置了多实例循环特性
   * @params [task] 任务实体
   * @author liguanwei
   * @date 2026/10/17 20:52
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
    if (CollUtil.isNotEmpty(userTasks)) {
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
   * @return Boolean 流程实例仍在运行返回 true，已结束或不存在返回 false
   * @description 判断流程实例是否还存在且处于运行中
   * @params [procInsId] 流程实例ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Boolean checkProcessIsExistByProcInsId(String procInsId) {
    return wfFlowableTaskService.checkProcessIsExistByProcInsId(procInsId);
  }

  /**
   * @return WfTaskVo 命中时返回带 taskId 的任务信息，未命中返回空对象
   * @description 判断当前用户是否为该流程当前任务的办理人，候选人、候选角色与候选组均参与匹配
   * @params [bo] 任务参数，含 procInsId、userId 与 groupId
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfTaskVo checkUserIsAuditByProcInsIdAndUserIdAndGroupId(WfTaskBo bo) {
    return wfFlowableTaskService.checkUserIsAuditByProcInsIdAndUserIdAndGroupId(bo);
  }

  /**
   * @return WfTaskVo 是发起人时返回任务信息，否则返回空对象
   * @description 判断当前用户是否为该流程的发起人
   * @params [bo] 任务参数，含 procInsId、userId 与 groupId
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfTaskVo checkUserIsStartByProcInsIdAndUserIdAndGroupId(WfTaskBo bo) {
    return wfFlowableTaskService.checkUserIsStartByProcInsIdAndUserIdAndGroupId(bo);
  }

  /**
   * @return Object 回退结果
   * @description 校验操作权限后处理顺序会签中的不通过，回退到同节点上的上一位处理人而不是整单驳回
   * @params [bo] 任务参数，含 taskId、procInsId、当前处理人与意见
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Object mulitiSequentialUnComplete(WfTaskBo bo) {
    this.taskAuthService.checkOperateAuth(bo.getTaskId());
    return wfCompleteTaskService.mulitiSequentialUnComplete(bo);
  }

  /**
   * @return InputStream 带高亮的流程图 PNG 图片输入流
   * @description 生成带高亮的流程图 PNG 流，已完成节点与连线高亮、当前节点额外标注，流程已结束时取历史实例的流程定义
   * @params [procInsId] 流程实例ID
   * @author liguanwei
   * @date 2026/10/17 20:52
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


