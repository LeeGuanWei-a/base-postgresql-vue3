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
     * @description 在当前任务节点之后以命令方式注入一个新的用户任务节点
     * @params [currentTaskId, newTaskKey, newTaskName, newTaskAssignee] 当前任务 ID；新节点标识；新节点名称；新节点处理人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @return Boolean 固定返回 true，表示替换命令已提交执行
     * @description 用待替换的下一节点信息组装动态任务参数，先取回原节点处理人，再执行替换节点的命令
     * @params [wfTaskBo, wfUserTaskDto] 任务业务参数，含下一处理人、审批模式、多实例配置与流程实例信息；待替换的下一节点定义
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @return List<String> 该节点原有审批人 ID 列表
     * @description 读取节点扩展属性中的审批人类型，按固定值或身份存储两种方式取回原节点的审批人 ID
     * @params [processInstance, flowElement] 目标流程实例；待替换的节点元素
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @return List<String> 节点候选用户 ID 列表，未配置时为空列表
     * @description 身份存储类节点直接取其候选用户作为原审批人列表
     * @params [userTask] 待读取的用户任务节点
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private List<String> handleIdentityTask(UserTask userTask) {
        List<String> originalUserList = new ArrayList<>();
        if (CollUtil.isNotEmpty(userTask.getCandidateUsers())) {
            originalUserList.addAll(userTask.getCandidateUsers());
        }
        return originalUserList;
    }

    /**
     * @return List<String> 节点原处理人 ID 列表，处理人表达式为空时为空列表
     * @description 固定值类节点把其处理人表达式按逗号拆成多个审批人 ID，作为原审批人列表
     * @params [userTask, processInstance] 待读取的用户任务节点；节点所属流程实例
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
