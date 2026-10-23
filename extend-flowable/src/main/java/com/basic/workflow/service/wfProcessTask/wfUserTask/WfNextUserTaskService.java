package com.basic.workflow.service.wfProcessTask.wfUserTask;

import lombok.extern.slf4j.Slf4j;

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
import cn.hutool.core.collection.CollUtil;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description 下一节点任务服务，按流程模型推导后续用户任务并为其计算处理人、候选组与审批意见
 * @author yanbin
 * @date 2024/03/01
 */
@Slf4j
@Service
public class WfNextUserTaskService extends WfProcessTaskBase {
    @Autowired
    private WfUserTaskInfoService wfUserTaskInfoService;

    /**
     * @description 先跳过处理人为空的节点，再逐个处理当前任务节点：固定值表达式的节点按其表达式设置处理人，提交人节点回退设为发起人
     * @params [procInsId] 流程实例 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void handleFlowTask(String procInsId) throws RuntimeException {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        // 处理是否是处理人为空自动跳过的节点
        this.handleNullTaskSkip(procInsId);
        // 1.获取当前任务
        if (processInstance == null) {
            return;
        }
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        int index = 0;
        if (CollUtil.isNotEmpty(taskList)) {
            for (Task task : taskList) {
                WfTaskVo wfTaskVo = new WfTaskVo();
                wfTaskVo.setTaskId(task.getId());
                // 2.获取当前任务节点(这里的当前已经是下一节点)
                WfUserTaskDto wfUserTaskDto = wfUserTaskInfoService.getCurrentTaskNode(wfTaskVo);
                UserTask userTask = wfUserTaskDto.getUserTask();
                // 3.确保当前节点是固定值表达式的节点
                if (wfUserTaskDto.getDataType().equals("固定值") && StringUtils.isNotBlank(wfUserTaskDto.getAssignee())) {
                    // 3.1解析绑定表达式（process 业务定制已去除，保留表达式原文作为 userId）
                    String expression = wfUserTaskDto.getAssignee();
                    // 3.2当前节点是否是组织筛选节点
                    if (wfUserTaskDto.getIsOrgScreen() != null && Boolean.valueOf(wfUserTaskDto.getIsOrgScreen())) {
                        //组织筛选 stub 化
                        taskService.setAssignee(task.getId(), expression);
                    }
                    // 4.当前节点表达式不为空
                    else if (expression != null) {
                        // 4.1直接以表达式作为处理人（简化版，去除 process 业务定制）
                        taskService.setAssignee(task.getId(), expression);
                    }
                }
                // 提交人节点处理
                else if (userTask.getSkipExpression() != null && userTask.getSkipExpression().equals("${initiator == ''}")) {
                    taskService.setAssignee(task.getId(), processInstance.getStartUserId());
                }
            }
        }
    }

    /**
     * @description 下一节点开启了相同处理人自动跳过且两节点处理人、候选人与角色一致时，为下一节点写「通过」意见、设置处理人并直接完结任务
     * @params [taskBo, nextTask, currentTask, taskEntity] 任务业务对象，提供流程实例id、处理人与流程变量；下一节点信息；当前节点信息；当前任务实体
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void nextTaskForIsSkip(WfTaskBo taskBo, WfUserTaskDto nextTask, WfUserTaskDto currentTask, TaskEntity taskEntity) {
        // 1.获取当前实例下的任务  其实这个已经是下一条任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(taskBo.getProcInsId()).list();
        if (CollUtil.isNotEmpty(taskList)) {
            // 1.1获取目前  activityId
            String nextActivity = taskList.get(0).getTaskDefinitionKey();
            // 若当前节点的  activityId和下一节点的activityId 相同则当前模块未通过，不进行操作
            // 反之则进行操作
            if (!nextActivity.equals(taskEntity.getTaskDefinitionKey())) {
                // 2.获取下一节点的拓展 isSkip
                String isSkip = nextTask.getIsSkip();
                if (isSkip != null && isSkip.equals("true")) {
                    // 3.当前处理人和发起人，以及userTask模块绑定的人员信息都相同的情况下
                    if ((Objects.equals(currentTask.getAssignee(), nextTask.getAssignee()) && TaskUtils.checkListEqual(currentTask.getUserList(), nextTask.getUserList()) && TaskUtils.checkListEqual(currentTask.getRoleList(), nextTask.getRoleList()))) {
                        for (Task task : taskList) {
                            // 3.1生成审批意见
                            this.addComment(task.getId(), taskBo.getCurrentUserId(), task.getProcessInstanceId(), FlowComment.NORMAL.getType(), "通过");
                            // 3.2设置任务处理人
                            taskService.setAssignee(task.getId(), taskBo.getCurrentUserId());
                            // 3.3完结任务
                            taskService.complete(task.getId(), taskBo.getVariables());
                        }
                    }
                }
            }
        }
    }

    /**
     * @description 处理退回后再次流转到同一节点的场景，节点开启再次流转自动跳过且处理人在该节点历史处理人中时，直接完结任务并删除其历史记录
     * @params [procInsId, currentTaskDto] 流程实例 id；当前任务节点信息，含活动id与再次流转跳过开关
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void handleReturnTaskInfo(String procInsId, WfUserTaskDto currentTaskDto) {
        //获取当前任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
        if (CollUtil.isNotEmpty(taskList)) {
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
     * @description 对开启了「处理人为空自动跳过」且当前无任何身份关联的节点，用流程启动参数直接完结该任务
     * @params [procInsId] 流程实例 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void handleNullTaskSkip(String procInsId) {
        //获取流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        if (processInstance == null) {
            return;
        }
        // 1.获取当前任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        if (CollUtil.isNotEmpty(taskList)) {
            for (Task task : taskList) {
                WfTaskVo wfTaskVo = new WfTaskVo();
                wfTaskVo.setTaskId(task.getId());
                // 2.获取当前任务节点(这里的当前已经是下一节点)
                WfUserTaskDto wfUserTaskDto = wfUserTaskInfoService.getCurrentTaskNode(wfTaskVo);
                // 2.1当前节点是处理人为空自动跳过的节点
                if (StringUtils.isNotBlank(wfUserTaskDto.getIsNullSkip()) && Boolean.valueOf(wfUserTaskDto.getIsNullSkip())) {
                    //获取 任务处理人信息
                    List<HistoricIdentityLink> linkForTask = historyService.getHistoricIdentityLinksForTask(task.getId());
                    if (CollUtil.isEmpty(linkForTask)) {
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
     * @description 处理下一节点为交替节点的场景，身份存储类型把节点候选用户逐个加为候选人，固定值类型直接把表达式设为处理人
     * @params [procInsId, currentUserId] 流程实例 id；当前处理人 id，用于标识发起人上下文
     * @author liguanwei
     * @date 2026/10/17 20:52
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
                // 1.获取当前任务模块(这里的当前已经是下一节点)
                WfUserTaskDto wfUserTaskDto = wfUserTaskInfoService.getCurrentTaskNode(wfTaskVo);
                //获取当前 用户模块信息
                UserTask userTask = wfUserTaskDto.getUserTask();
                // 2.是否是交替节点
                if (StringUtils.isNotBlank(wfUserTaskDto.getIsRoleReplace()) && Boolean.valueOf(wfUserTaskDto.getIsRoleReplace())) {
                    //获取发起人信息
                    String startUserId = processInstance.getStartUserId();
                    //process 业务定制已去除：角色交替逻辑 stub 化（不再做角色替换）
                    // 3.获取当前节点处理人信息  只有角色或者角色表达式
                    if (wfUserTaskDto.getDataType().equals("身份存储")) {
                        // 简化：直接用 userTask 上的候选用户
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
            }
        }
    }

    /**
     * @description 首个节点处理人为空但发起时选了审批人时，单人直接设为任务处理人、多人加为候选处理人；节点未配任何处理人相关配置时抛运行时异常
     * @params [procInsId, startUserCodes] 流程实例 id；发起时选中的审批人编码集合，为空时不处理
     * @author liguanwei
     * @date 2026/10/17 20:52
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
                if (userTask.getAssignee() == null && CollUtil.isEmpty(
                    userTask.getCandidateUsers())
                    && CollUtil.isEmpty(userTask.getCandidateGroups())) {
                    throw new RuntimeException("流程有误，请联系管理员!");
                }
            }
        }
        Task task = taskList.get(0);
        //添加处理人
        if (CollUtil.isNotEmpty(startUserCodes)) {
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
