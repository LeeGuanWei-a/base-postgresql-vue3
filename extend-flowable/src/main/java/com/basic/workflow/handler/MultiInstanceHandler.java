package com.basic.workflow.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.basic.constant.ExtensionConstant;
import com.basic.workflow.service.impl.ExtensionElementsService;
import lombok.AllArgsConstructor;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 多实例处理类（多实例监听器）
 * <p>
 * 已去除 process 业务定制（ExtensionCandidateService），
 * 改为基于 Flowable 原生 UserTask API 处理候选人/角色。
 *
 * @author yanbin
 * @date 2023/11/3
 */
@AllArgsConstructor
@Component("multiInstanceHandler")
public class MultiInstanceHandler {

    private final RuntimeService runtimeService;
    private final ExtensionElementsService extensionElementsService;

    public Set<String> getUserIds(DelegateExecution execution) {
        Set<String> assigneeList = new LinkedHashSet<>();
        FlowElement flowElement = execution.getCurrentFlowElement();
        if (ObjectUtil.isNotEmpty(flowElement) && flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;
            ProcessInstance processInstance = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(execution.getProcessInstanceId())
                    .singleResult();

            String datatype = null;
            if (CollUtil.isNotEmpty(userTask.getExtensionElements())) {
                List<ExtensionElement> extensionElement = userTask.getExtensionElements().get("assigneeType");
                if (extensionElement != null && !extensionElement.isEmpty()) {
                    datatype = extensionElement.get(0).getElementText();
                }
            }

            // 固定值：表达式方式（通用：直接把 expression 字段视为 userId/roleKey）
            if ("0".equals(datatype)) {
                String expression = userTask.getAssignee();
                // 形如 ${assignee} / ${starterCode} 的是表达式占位符，不是真实用户 id。
                // 原来无脑把它当一个人加进集合，会让 nrOfInstances = 1，
                // 顺序会签就退化成"一个人办完就过"（这正是线上踩到的现象）。
                if (expression != null && !expression.contains("${")) {
                    // 简化版：单一处理人表达式直接当 userId
                    if (expression.contains(",")) {
                        for (String s : expression.split(",")) {
                            if (s != null && !s.isEmpty()) {
                                assigneeList.add(s.trim());
                            }
                        }
                    } else {
                        assigneeList.add(expression);
                    }
                }
                execution.setVariable(userTask.getId().concat("assigneeList"), assigneeList);
            }
            // 身份存储：取 userTask 上的 candidateUsers / candidateGroups
            else if ("1".equals(datatype)) {
                if (CollUtil.isNotEmpty(userTask.getCandidateUsers())) {
                    assigneeList.addAll(userTask.getCandidateUsers());
                }
                execution.setVariable(userTask.getId().concat("assigneeList"), assigneeList);
                Map<String, Object> map = new HashMap<>();
                map.put("assigneeList", assigneeList.stream().collect(Collectors.joining(",")));
                runtimeService.setVariables(execution.getId(), map);
            }
            // 指定审批人：直接使用 userTask 原生 candidateUsers
            else if ("3".equals(datatype)) {
                if (CollUtil.isNotEmpty(userTask.getCandidateUsers())) {
                    assigneeList.addAll(userTask.getCandidateUsers());
                }
                execution.setVariable(userTask.getId().concat("assigneeList"), assigneeList);
                Map<String, Object> map = new HashMap<>();
                map.put("assigneeList", assigneeList.stream().collect(Collectors.joining(",")));
                runtimeService.setVariables(execution.getId(), map);
            }
            // 兜底：上面任何分支都没取到人时（例如 assigneeType 配错/缺失），
            // 回退到节点原生的 candidateUsers，至少保证多实例人数正确 ——
            // 否则集合为空时 nrOfInstances=0，节点会被直接跳过；为 1 时顺序会签会退化成或签。
            if (assigneeList.isEmpty() && CollUtil.isNotEmpty(userTask.getCandidateUsers())) {
                assigneeList.addAll(userTask.getCandidateUsers());
                execution.setVariable(userTask.getId().concat("assigneeList"), assigneeList);
            }
        }
        return assigneeList;
    }
}
