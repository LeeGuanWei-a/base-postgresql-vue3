package com.basic.workflow.service.wfModel.validate;

import com.basic.flowable.utils.ModelUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 流程模型保存前的合规校验。
 *
 * <p>调用 Flowable 自带的结构校验器 + 本项目的业务规则，
 * 把问题一次性列出来，而不是等到「发起流程」时才一个个抛 NPE。</p>
 *
 * <p>业务规则来自实战踩过的坑：设计器允许节点不配处理人 / 不配 assigneeType，
 * 但运行期代码假设它们一定存在，于是发起流程时连环 NPE。</p>
 *
 * @author 集成审计补充
 */
@Component
public class WfProcessComplianceValidator {

    private static final Logger log = LoggerFactory.getLogger(WfProcessComplianceValidator.class);

    /**
     * assigneeType 扩展元素名（固定值 / 身份存储）
     */
    private static final String EXT_ASSIGNEE_TYPE = "assigneeType";

    @Autowired
    private ProcessEngine processEngine;

    /**
     * 校验流程模型。
     *
     * @param bpmnModel Flowable 解析后的流程模型
     * @return 问题清单；为空表示校验通过
     */
    public List<String> validate(BpmnModel bpmnModel) {
        List<String> problems = new ArrayList<>();
        if (bpmnModel == null || bpmnModel.getMainProcess() == null) {
            problems.add("流程模型解析失败：没有解析出主流程，请检查流程图是否完整");
            return problems;
        }
        // 1. Flowable 自带结构校验（开始/结束节点、孤立节点、连线指向等）
        validateByFlowable(bpmnModel, problems);
        // 2. 本项目的业务规则
        Process mainProcess = bpmnModel.getMainProcess();
        validateUserTasks(bpmnModel, problems);
        validateGateways(mainProcess, problems);
        validateStartAndEnd(mainProcess, problems);
        return problems;
    }

    /**
     * 调用 Flowable 自带的流程校验器。
     */
    private void validateByFlowable(BpmnModel bpmnModel, List<String> problems) {
        try {
            ProcessValidator validator =
                    ((ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration())
                            .getProcessValidator();
            if (validator == null) {
                return;
            }
            List<ValidationError> errors = validator.validate(bpmnModel);
            if (errors == null || errors.isEmpty()) {
                return;
            }
            for (ValidationError error : errors) {
                // 警告不阻断保存
                if (error.isWarning()) {
                    continue;
                }
                String where = StringUtils.isNotBlank(error.getActivityId())
                        ? "节点[" + error.getActivityId() + "] "
                        : "";
                problems.add("流程图结构：" + where + error.getProblem());
            }
        } catch (Exception e) {
            // 校验器本身出问题不能阻断业务保存
            log.warn("Flowable 流程结构校验执行失败，已跳过：{}", e.getMessage());
        }
    }

    /**
     * 用户任务节点：必须配处理人、必须配 assigneeType。
     */
    private void validateUserTasks(BpmnModel bpmnModel, List<String> problems) {
        Collection<UserTask> userTasks = ModelUtils.getAllUserTaskEvent(bpmnModel);
        if (userTasks == null || userTasks.isEmpty()) {
            problems.add("流程里没有任何用户任务节点，无法流转");
            return;
        }
        for (UserTask task : userTasks) {
            String name = StringUtils.isNotBlank(task.getName()) ? task.getName() : task.getId();
            // 2.1 处理人
            boolean hasAssignee = StringUtils.isNotBlank(task.getAssignee());
            boolean hasCandidateUsers = task.getCandidateUsers() != null && !task.getCandidateUsers().isEmpty();
            boolean hasCandidateGroups = task.getCandidateGroups() != null && !task.getCandidateGroups().isEmpty();
            if (!hasAssignee && !hasCandidateUsers && !hasCandidateGroups) {
                problems.add("节点[" + name + "] 未配置处理人："
                        + "「分配人员 / 候选人员 / 候选角色 / 流程发起人」至少要选一个，否则流程发起后会卡死在该节点");
            }
            // 2.2 assigneeType（固定值 / 身份存储）
            boolean hasAssigneeType = task.getExtensionElements() != null
                    && task.getExtensionElements().containsKey(EXT_ASSIGNEE_TYPE);
            if (!hasAssigneeType) {
                problems.add("节点[" + name + "] 未设置「固定值 / 身份存储」："
                        + "请在节点配置里选一次（缺这个扩展会导致发起流程报错）");
            }
        }
    }

    /**
     * 排他网关：必须有条件表达式或默认流。
     */
    private void validateGateways(Process mainProcess, List<String> problems) {
        for (FlowElement element : mainProcess.getFlowElements()) {
            if (!(element instanceof ExclusiveGateway)) {
                continue;
            }
            ExclusiveGateway gateway = (ExclusiveGateway) element;
            String name = StringUtils.isNotBlank(gateway.getName()) ? gateway.getName() : gateway.getId();
            if (StringUtils.isNotBlank(gateway.getDefaultFlow())) {
                continue;
            }
            List<SequenceFlow> outgoing = gateway.getOutgoingFlows();
            if (outgoing == null || outgoing.isEmpty()) {
                problems.add("网关[" + name + "] 没有出口连线");
                continue;
            }
            boolean anyCondition = false;
            for (SequenceFlow flow : outgoing) {
                if (StringUtils.isNotBlank(flow.getConditionExpression())) {
                    anyCondition = true;
                    break;
                }
            }
            if (!anyCondition) {
                problems.add("网关[" + name + "] 的所有出口都没有配置流转条件："
                        + "请给每条线配置条件表达式，或指定一条默认流");
            }
        }
    }

    /**
     * 开始 / 结束节点。
     */
    private void validateStartAndEnd(Process mainProcess, List<String> problems) {
        int startCount = 0;
        int endCount = 0;
        for (FlowElement element : mainProcess.getFlowElements()) {
            if (element instanceof StartEvent) {
                startCount++;
            } else if (element instanceof EndEvent) {
                endCount++;
            }
        }
        if (startCount != 1) {
            problems.add("流程必须有且只有一个开始节点（当前 " + startCount + " 个）");
        }
        if (endCount < 1) {
            problems.add("流程至少需要一个结束节点");
        }
    }

    /**
     * 便捷方法：把问题清单拼成给前端展示的多行文本。
     */
    public static String toMessage(List<String> problems) {
        StringBuilder sb = new StringBuilder("流程校验未通过，请修正后再保存：");
        for (int i = 0; i < problems.size(); i++) {
            sb.append("\n").append(i + 1).append(". ").append(problems.get(i));
        }
        return sb.toString();
    }
}
