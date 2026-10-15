package com.basic.workflow.service.wfProcessInfo.detail;

import com.basic.common.core.domain.entity.SysUser;
import com.basic.common.utils.StringUtils;
import com.basic.constant.ExtensionConstant;
import com.basic.workflow.domain.dto.WfUserTaskDto;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (Flowable当前流程的当前任务信息)
 * @date 2024/03/04
 */
@Service
public class WfProcessCurrentTaskService extends WfProcessInfoBase {
    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @description TODO (根据实例id获取当前节点(任务)的详情，任务数量)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/4 10:48
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int getCurrentTaskNum(String procInsId) {
        //获取流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procInsId).singleResult();
        //流程已经结束
        if (processInstance == null) {
            return 0;
        }
        //获取当前任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
        if (CollectionUtils.isEmpty(taskList)) {
            return 1;
        }
        //获取当前活动id
        String activityId = taskList.get(0).getTaskDefinitionKey();
        //TODO 1.多实例或签-->>生成多条任务，仅一个人审批即可 可显示指定下一审批人;
        //TODO 2.多实例会签  2.会签串行  任务一条一条的生成 根据  任务实例总数-已完成的任务数 是否是1进行判断
        //TODO 2.2会签并行   根据任务数判断
        if (activityId != null) {
            //查找当前活动的UserTask模块
            UserTask userTask = (UserTask) wfBpmnModelService.findFlowNodeByActivityId
                    (processInstance.getProcessDefinitionId(), activityId);
            //获取多实例属性
            MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = userTask.getLoopCharacteristics();
            if (multiInstanceLoopCharacteristics != null) {
                //串行并行
                boolean sequential = multiInstanceLoopCharacteristics.isSequential();
                //完成条件--》判断 会签或签
                String completion = multiInstanceLoopCharacteristics.getCompletionCondition();
                //获取执行实例
                List<Execution> executions = runtimeService.createExecutionQuery()
                        .processInstanceId(procInsId).activityId(activityId).list();
                Integer count = 0;
                //多实例  会签且是串行   其他情况下都会生成多条任务，且完成一条少一条!!!
                if (sequential && completion.equals("${nrOfCompletedInstances >= nrOfInstances}")) {
                    for (Execution execution : executions) {
                        // 优先用 nrOfActiveInstances —— 它才是"当前真正存在的待办数"。
                        // 原来用 nrOfInstances - nrOfCompletedInstances 会算错：
                        // 「会签回退」会把 nrOfCompletedInstances 重置回退点的值，
                        // 于是 2 - 0 被显示成"当前任务数 = 2"，而实际只有 1 条待办
                        // （顺序会签任何时刻都只有 1 条待办，nrOfActiveInstances = 1）。
                        Integer nrOfActiveInstances = (Integer) runtimeService.getVariable(
                                execution.getId(), "nrOfActiveInstances");
                        if (nrOfActiveInstances != null) {
                            count = nrOfActiveInstances;
                            continue;
                        }
                        // 取不到 active 变量时退回老算法
                        Integer nrOfCompletedInstances = (Integer) runtimeService.getVariable(execution.getId(), "nrOfCompletedInstances");
                        Integer nrOfInstances = (Integer) runtimeService.getVariable(execution.getId(), "nrOfInstances");
                        if (nrOfInstances != null && nrOfCompletedInstances != null) {
                            count = nrOfInstances - nrOfCompletedInstances;
                        }
                    }
                    return count;
                }
            }
        }
        return 1;
    }

    /**
     * @return com.basic.workflow.domain.vo.WfTaskVo
     * @description TODO (根据实例procInsId获取当前任务)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/4 11:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfTaskVo getProcInsIdToTask(String procInsId, String userId) {
        WfTaskVo wfTaskVo = new WfTaskVo();
        if (procInsId == null) {
            return null;
        }
        //获取流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procInsId).singleResult();
        if (processInstance == null) {
            wfTaskVo.setProcInsId(procInsId);
            return wfTaskVo;
        }
        wfTaskVo.setProcInsId(processInstance.getProcessInstanceId());
        //查询业务拓展表
        wfTaskVo = this.selectProcessOfExtendProcinst(wfTaskVo, null, procInsId);
        //流程发起的定义id
        wfTaskVo.setProcDefId(processInstance.getProcessDefinitionId());
        //获取当前任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
        for (Task task : taskList) {
            if (StringUtils.isNotBlank(task.getAssignee())) {
                if (task.getAssignee().equals(userId)) {
                    wfTaskVo.setTaskId(task.getId());
                    break;
                }
            }
            List<IdentityLink> identityLinkEntityList = taskService.getIdentityLinksForTask(task.getId());
            for (IdentityLink identityLink : identityLinkEntityList) {
                //TODO 判断当前用户是否是次用户组
                if (StringUtils.isNotBlank(identityLink.getUserId())) {
                    if (identityLink.getUserId().equals(userId)) {
                        wfTaskVo.setTaskId(task.getId());
                        break;
                    }
                }
                //TODO 判断当前用户是否是此角色内
                else if (StringUtils.isNotBlank(identityLink.getGroupId())) {
                    List<SysUser> userList = sysUserService.selectRoleToUser(Long.valueOf(identityLink.getGroupId()));
                    for (SysUser sysUser : userList) {
                        if (sysUser.getUserId().toString().equals(userId)) {
                            wfTaskVo.setTaskId(task.getId());
                            break;
                        }
                    }
                }
            }
        }
        return wfTaskVo;
    }

    /**
     * @return com.basic.workflow.domain.vo.WfTaskVo
     * @description TODO (根据taskID获取任务信息)
     * @params [taskId]
     * @author yanbin
     * @date 2024/3/6 9:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfUserTaskDto getCurrentTaskInfo(String taskId) {
        WfUserTaskDto wfUserTaskDto = new WfUserTaskDto();
        if (StringUtils.isBlank(taskId)) {
            return null;
        }
        TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
        if (taskEntity == null) {
            return null;
        }
        wfUserTaskDto.setActivityId(taskEntity.getTaskDefinitionKey());
        wfUserTaskDto.setActivityName(taskEntity.getName());
        // 该节点允许的操作按钮（节点「授权功能」idmPermission），前端据此动态渲染审批按钮
        wfUserTaskDto.setPermissionJson(this.getPermission(taskId));
        // 是否顺序会签：决定要不要显示「会签回退」（同节点回退到上一个处理人）
        wfUserTaskDto.setSequentialMultiInstance(
                this.isSequentialMultiInstance(taskEntity.getProcessDefinitionId(), taskEntity.getTaskDefinitionKey()));
        return wfUserTaskDto;
    }

    /**
     * 判断节点是否为「顺序会签」：串行多实例 + 完成条件为全票通过。
     *
     * <p>判断条件与 WfCompleteTaskService#mulitiSequentialUnComplete 的前置校验保持一致，
     * 避免前端显示了「会签回退」按钮、点下去却被后端拒绝。</p>
     */
    private Boolean isSequentialMultiInstance(String processDefinitionId, String activityId) {
        try {
            // 这里刻意用 Object 接返回值，避免为本方法额外引入 FlowElement 的 import
            Object flowElement = wfBpmnModelService.findFlowNodeByActivityId(processDefinitionId, activityId);
            if (!(flowElement instanceof UserTask)) {
                return false;
            }
            MultiInstanceLoopCharacteristics loop =
                    ((UserTask) flowElement).getLoopCharacteristics();
            if (loop == null || !loop.isSequential()) {
                return false;
            }
            String completion = loop.getCompletionCondition();
            return completion != null
                    && ("${nrOfCompletedInstances >= nrOfInstances}".equals(completion)
                        || completion.contains("nrOfCompletedInstances == nrOfInstances"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return java.util.List<java.lang.String>
     * @description TODO (根据任务TaskId获取当前节点的操作权限)
     * @params [taskId]
     * @author yanbin
     * @date 2024/3/7 9:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String getPermission(String taskId) {
        if (StringUtils.isNotBlank(taskId)) {
            TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                return null;
            }
            String permission = extensionElementsService.getCustomPropertyText(task.getTaskDefinitionKey(),
                    task.getProcessDefinitionId(), ExtensionConstant.IDM_PERMISSION);
            return permission;
        }
        return null;
    }
}
