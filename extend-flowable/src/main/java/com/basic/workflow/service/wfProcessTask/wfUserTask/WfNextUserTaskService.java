package com.basic.workflow.service.wfProcessTask.wfUserTask;

import cn.hutool.core.collection.CollUtil;
import com.basic.common.core.domain.entity.SysRole;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.flowable.utils.TaskUtils;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.dto.WfUserTaskDto;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yanbin
 * @description TODO (Flowable处理下一节点任务)
 * @date 2024/03/01
 */
@Service
public class WfNextUserTaskService extends WfProcessTaskBase {
    @Autowired
    private WfUserTaskInfoService wfUserTaskInfoService;

    /**
     * @return void
     * @description TODO (仅处理仅为表达式的节点,还只处理与发起人同节点的人员，不考虑根据上一节点的处理人来设置下一节点的处理人)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/1 16:44
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void handleFlowTask(String procInsId) throws RuntimeException {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        //TODO 处理是否是处理人为空自动跳过的节点
        this.handleNullTaskSkip(procInsId);
        //TODO 1.获取当前任务
        if (processInstance == null) {
            return;
        }
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        int index = 0;
        if (CollUtil.isNotEmpty(taskList)) {
            for (Task task : taskList) {
                WfTaskVo wfTaskVo = new WfTaskVo();
                wfTaskVo.setTaskId(task.getId());
                //TODO 2.获取当前任务节点(这里的当前已经是下一节点)
                WfUserTaskDto wfUserTaskDto = wfUserTaskInfoService.getCurrentTaskNode(wfTaskVo);
                UserTask userTask = wfUserTaskDto.getUserTask();
                //TODO 3.确保当前节点是固定值表达式的节点
                if (wfUserTaskDto.getDataType().equals("固定值") && StringUtils.isNotBlank(wfUserTaskDto.getAssignee())) {
                    //TODO 3.1解析绑定表达式（process 业务定制已去除，保留表达式原文作为 userId）
                    String expression = wfUserTaskDto.getAssignee();
                    //TODO 3.2当前节点是否是组织筛选节点
                    if (wfUserTaskDto.getIsOrgScreen() != null && Boolean.valueOf(wfUserTaskDto.getIsOrgScreen())) {
                        //组织筛选 stub 化
                        taskService.setAssignee(task.getId(), expression);
                    }
                    //TODO 4.当前节点表达式不为空
                    else if (expression != null) {
                        //TODO 4.1直接以表达式作为处理人（简化版，去除 process 业务定制）
                        taskService.setAssignee(task.getId(), expression);
                    }
                }
                //TODO 提交人节点处理
                else if (userTask.getSkipExpression() != null && userTask.getSkipExpression().equals("${initiator == ''}")) {
                    taskService.setAssignee(task.getId(), processInstance.getStartUserId());
                }
            }
        }
    }

    /**
     * @return void
     * @description TODO (下一节点设置了相同处理人自动跳过，则当前节点处理人与下一节点处理人(或候选处理人相同)则下一节点跳过审批)
     * @params [taskBo, nextTask, currentTask, taskEntity]
     * @author yanbin
     * @date 2024/3/1 16:44
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void nextTaskForIsSkip(WfTaskBo taskBo, WfUserTaskDto nextTask, WfUserTaskDto currentTask, TaskEntity taskEntity) {
        //TODO 1.获取当前实例下的任务  其实这个已经是下一条任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(taskBo.getProcInsId()).list();
        if (CollectionUtils.isNotEmpty(taskList)) {
            //TODO 1.1获取目前  activityId
            String nextActivity = taskList.get(0).getTaskDefinitionKey();
            //TODO 若当前节点的  activityId和下一节点的activityId 相同则当前模块未通过，不进行操作
            // 反之则进行操作
            if (!nextActivity.equals(taskEntity.getTaskDefinitionKey())) {
                //TODO 2.获取下一节点的拓展 isSkip
                String isSkip = nextTask.getIsSkip();
                if (isSkip != null && isSkip.equals("true")) {
                    //TODO 3.当前处理人和发起人，以及userTask模块绑定的人员信息都相同的情况下
                    if ((Objects.equals(currentTask.getAssignee(), nextTask.getAssignee()) && TaskUtils.checkListEqual(currentTask.getUserList(), nextTask.getUserList()) && TaskUtils.checkListEqual(currentTask.getRoleList(), nextTask.getRoleList()))) {
                        for (Task task : taskList) {
                            //TODO 3.1生成审批意见
                            this.addComment(task.getId(), taskBo.getCurrentUserId(), task.getProcessInstanceId(), FlowComment.NORMAL.getType(), "通过");
                            //TODO 3.2设置任务处理人
                            taskService.setAssignee(task.getId(), taskBo.getCurrentUserId());
                            //TODO 3.3完结任务
                            taskService.complete(task.getId(), taskBo.getVariables());
                        }
                    }
                }
            }
        }
    }

    /**
     * @return void
     * @description TODO (处理退回之后， 再次流转到多实例节点事件) -->存在问题eg:节点撤回，撤回的节点处理人中存在撤回人，也不需在此审批
     * @params [procInsId, currentTaskDto]
     * @author yanbin
     * @date 2024/3/1 16:46
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void handleReturnTaskInfo(String procInsId, WfUserTaskDto currentTaskDto) {
        //获取当前任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
        if (CollectionUtils.isNotEmpty(taskList)) {
            //活动id
            String activityId = currentTaskDto.getActivityId();
            // 历史任务 id
            List<String> taskIdList = this.selectProcInsIdActivity(procInsId, activityId);
            List<String> userIdList = new ArrayList<>();
            //获取历史处理人id
            taskIdList.forEach(taskId -> {
                HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
                if(historicTaskInstance!=null) {
                    userIdList.add(historicTaskInstance.getAssignee());
                }
            });
            //节点是否 开启  再次流转自动跳过属性
            Boolean isExamined = Boolean.valueOf(currentTaskDto.getIsExamined());
            for (Task task : taskList) {
                if (isExamined && userIdList.contains(task.getAssignee())) {
                    //完结任务
                    taskService.complete(task.getId());
                    //删除历史记录
                    wfFlowableTaskService.deleteTaskIdActivity(task.getId(), task.getProcessInstanceId());
                }
            }
        }
    }

    /**
     * @return void
     * @description TODO (处理节点配置了处理人为空时跳过的节点)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/1 16:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void handleNullTaskSkip(String procInsId) {
        //获取流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        if (processInstance == null) {
            return;
        }
        //TODO 1.获取当前任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        if (CollUtil.isNotEmpty(taskList)) {
            for (Task task : taskList) {
                WfTaskVo wfTaskVo = new WfTaskVo();
                wfTaskVo.setTaskId(task.getId());
                //TODO 2.获取当前任务节点(这里的当前已经是下一节点)
                WfUserTaskDto wfUserTaskDto = wfUserTaskInfoService.getCurrentTaskNode(wfTaskVo);
                //TODO 2.1当前节点是处理人为空自动跳过的节点
                if (StringUtils.isNotBlank(wfUserTaskDto.getIsNullSkip()) && Boolean.valueOf(wfUserTaskDto.getIsNullSkip())) {
                    //获取 任务处理人信息
                    List<HistoricIdentityLink> linkForTask = historyService.getHistoricIdentityLinksForTask(task.getId());
                    if (CollectionUtils.isEmpty(linkForTask)) {
                        //默认处理设置为管理员
//                        taskService.setAssignee(task.getId(),"1");
                        //这里的参数  是启动参数
                        taskService.complete(task.getId(), processInstance.getProcessVariables());
                    }
                }
            }
        }
    }

    /**
     * @return void
     * @description TODO (处理下一节点是交替节点问题)
     * @params [procInsId, currentUserId]
     * @author yanbin
     * @date 2024/3/1 17:53
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void handleIsRoleReplace(String procInsId, String currentUserId) {
        //获取当前任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
        //获取实例信息
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        if (CollUtil.isNotEmpty(taskList)) {
            for (Task task : taskList) {
                WfTaskVo wfTaskVo = new WfTaskVo();
                wfTaskVo.setTaskId(task.getId());
                //TODO 1.获取当前任务模块(这里的当前已经是下一节点)
                WfUserTaskDto wfUserTaskDto = wfUserTaskInfoService.getCurrentTaskNode(wfTaskVo);
                //获取当前 用户模块信息
                UserTask userTask = wfUserTaskDto.getUserTask();
                //TODO 2.是否是交替节点
                if (StringUtils.isNotBlank(wfUserTaskDto.getIsRoleReplace()) && Boolean.valueOf(wfUserTaskDto.getIsRoleReplace())) {
                    //获取发起人信息
                    String startUserId = processInstance.getStartUserId();
                    //process 业务定制已去除：角色交替逻辑 stub 化（不再做角色替换）
                    //TODO 3.获取当前节点处理人信息  只有角色或者角色表达式
                    if (wfUserTaskDto.getDataType().equals("身份存储")) {
                        //TODO 简化：直接用 userTask 上的候选用户
                        if (CollUtil.isNotEmpty(userTask.getCandidateUsers())) {
                            userTask.getCandidateUsers().forEach(userId -> {
                                if (userId != null) {
                                    taskService.addCandidateUser(task.getId(), userId);
                                }
                            });
                        }
                    } else if (wfUserTaskDto.getDataType().equals("固定值")) {
                        String expression = wfUserTaskDto.getAssignee();
                        if (StringUtils.isBlank(expression)) {
                            return;
                        }
                        //简化：直接以 expression 为处理人
                        taskService.setAssignee(task.getId(), expression);
                    }
                }
                System.out.println(2212);
            }
        }
    }

    /**
     * @return void
     * @description TODO (处理第一个节点为空且选择了审批人的节点)
     * @params [procInsId, startUserCodes]
     * @author yanbin
     * @date 2024/4/18 8:39
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void handleNotBlankTask(String procInsId, List<String> startUserCodes) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
        if (taskList.size() == 0) {
            return;
        }
        //为空的节点  不准配置多实例， 这种情况处于流程错误
        if (taskList.size() >= 1 && startUserCodes == null) {
            UserTask userTask = wfBpmnModelService.findUserTaskForProcDefIdAndKey(taskList.get(0).getProcessDefinitionId(), taskList.get(0).getTaskDefinitionKey());
            if (!"${initiator == ''}".equals(userTask.getSkipExpression())) {
                if (userTask.getAssignee() == null && CollectionUtils.isEmpty(
                    userTask.getCandidateUsers())
                    && CollectionUtils.isEmpty(userTask.getCandidateGroups())) {
                    throw new RuntimeException("流程有误，请联系管理员!");
                }
            }
        }
        Task task = taskList.get(0);
        //添加处理人
        if (CollectionUtils.isNotEmpty(startUserCodes)) {
            //设置处理人  直接设置assignee（task表)
            if (startUserCodes.size() == 1) {
                taskService.setAssignee(task.getId(), startUserCodes.get(0));
            }
            //设置候选处理    设置candidateUser（indentity_link表)
            else {
                startUserCodes.forEach(userId -> {
                    taskService.addCandidateUser(task.getId(), userId);
                });
            }
        }
    }
}
