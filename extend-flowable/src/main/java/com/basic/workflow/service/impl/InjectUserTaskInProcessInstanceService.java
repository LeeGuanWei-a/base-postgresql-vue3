package com.basic.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.factory.FlowServiceFactory;
import com.basic.workflow.builder.DynamicUserTaskBuilder;
import com.basic.workflow.cmd.InjectUserTaskInProcessInstanceCmd;
import com.basic.workflow.cmd.ReplaceUserTaskInProcessInstanceCmd;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.dto.WfUserTaskDto;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 工作流节点处理（新增临时节点或替换节点）
 * <p>
 * 已去除 process 业务定制（ExtensionCandidateService），
 * 改为基于 Flowable 原生 UserTask API。
 *
 * @author yanbin
 * @date 2023/11/3
 */
@Service
public class InjectUserTaskInProcessInstanceService extends FlowServiceFactory {

    /**
     * 在当前节点之后添加新的节点
     */
    public void addUserTaskInProcessInstance(String currentTaskId, String newTaskKey, String newTaskName, String newTaskAssignee) {
        DynamicUserTaskBuilder dynamicUserTaskBuilder = new DynamicUserTaskBuilder();
        dynamicUserTaskBuilder.setId(newTaskKey);
        dynamicUserTaskBuilder.setName(newTaskName);
        dynamicUserTaskBuilder.setAssignee(newTaskAssignee);
        InjectUserTaskInProcessInstanceCmd injectUserTaskInProcessInstanceCmd =
                new InjectUserTaskInProcessInstanceCmd(currentTaskId, dynamicUserTaskBuilder);
        managementService.executeCommand(injectUserTaskInProcessInstanceCmd);
    }

    /**
     * 替换下一节点
     */
    public Boolean replaceUserTaskInProcessInstance(WfTaskBo wfTaskBo, WfUserTaskDto wfUserTaskDto) {
        DynamicUserTaskBuilder dynamicUserTaskBuilder = new DynamicUserTaskBuilder();
        UserTask userTask = wfUserTaskDto.getUserTask();
        String activityId = userTask.getId();
        List<String> appointUserList = Arrays.asList(wfTaskBo.getNextUserIds().split(","));
        dynamicUserTaskBuilder.setId(activityId);
        dynamicUserTaskBuilder.setName(userTask.getName());
        Map<String, Object> map = new HashMap<>();
        map.put("nodeType", wfTaskBo.getNodeType());
        map.put("isSequential", wfTaskBo.getIsSequential());
        map.put("pattern", wfTaskBo.getPattern());
        map.put("multiInstance", wfTaskBo.getMultiInstance());
        map.put("appointUserList", appointUserList);
        dynamicUserTaskBuilder.setArgument(map);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(wfTaskBo.getProcDefId());
        Process process = bpmnModel.getProcesses().get(0);
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(wfTaskBo.getProcInsId())
                .singleResult();
        List<String> originalUserList = this.handleReplaceUserTask(processInstance, process.getFlowElement(activityId));
        map.put("originalUserList", originalUserList);
        ReplaceUserTaskInProcessInstanceCmd replaceUserTaskInProcessInstanceCmd =
                new ReplaceUserTaskInProcessInstanceCmd(wfTaskBo.getTaskId(), dynamicUserTaskBuilder, process.getFlowElement(activityId));
        managementService.executeCommand(replaceUserTaskInProcessInstanceCmd);
        return true;
    }

    /**
     * 处理身份存储和固定值 userTask
     */
    private List<String> handleReplaceUserTask(ProcessInstance processInstance, FlowElement flowElement) {
        UserTask userTask = (UserTask) flowElement;
        String datatype = null;
        if (CollUtil.isNotEmpty(userTask.getExtensionElements())) {
            List<ExtensionElement> extensionElement = userTask.getExtensionElements().get("assigneeType");
            if (extensionElement != null && !extensionElement.isEmpty()) {
                datatype = extensionElement.get(0).getElementText();
            }
        }
        List<String> originalUserList = new ArrayList<>();
        if ("0".equals(datatype)) {
            originalUserList = this.handleFixedTask(userTask, processInstance);
        } else if ("1".equals(datatype)) {
            originalUserList = this.handleIdentityTask(userTask);
        }
        return originalUserList;
    }

    /**
     * 身份存储 userTask 获取审批人 id（简化为：直接取 userTask.candidateUsers）
     */
    private List<String> handleIdentityTask(UserTask userTask) {
        List<String> originalUserList = new ArrayList<>();
        if (CollUtil.isNotEmpty(userTask.getCandidateUsers())) {
            originalUserList.addAll(userTask.getCandidateUsers());
        }
        return originalUserList;
    }

    /**
     * 固定值 userTask 获取审批人 id（简化为：取 userTask.assignee 作为单用户）
     */
    private List<String> handleFixedTask(UserTask userTask, ProcessInstance processInstance) {
        List<String> originalUserList = new ArrayList<>();
        if (StringUtils.isBlank(userTask.getAssignee())) {
            return new ArrayList<>();
        }
        // 简化版：单一处理人表达式
        String expression = userTask.getAssignee();
        if (expression.contains(",")) {
            for (String s : expression.split(",")) {
                if (s != null && !s.isEmpty()) {
                    originalUserList.add(s.trim());
                }
            }
        } else {
            originalUserList.add(expression);
        }
        return originalUserList;
    }
}
