package com.basic.workflow.service.wfModel.validate;

import com.basic.flowable.utils.ModelUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BoundaryEvent;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.CancelEventDefinition;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.ErrorEventDefinition;
import org.flowable.bpmn.model.EventDefinition;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.SubProcess;
import org.flowable.bpmn.model.TimerEventDefinition;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // 3. 边界事件（boundary event）业务校验
        validateBoundaryEvents(bpmnModel, problems);
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
     * 边界事件（BoundaryEvent）业务校验。
     *
     * <p>校验规则：</p>
     * <ul>
     *   <li>每个 boundary 必须挂在某个 Activity（UserTask / SubProcess / CallActivity）边界上</li>
     *   <li>同一宿主活动上同一种事件定义类型（按 eventDefinition 的 $type 区分）只能出现 1 次，
     *       否则引擎行为未定义且容易踩坑（不是阻断性错误，记录 warning）</li>
     *   <li>timer boundary：timeDuration / timeCycle / timeDate 至少要有一个
     *       （Flowable 自身不阻断，但运行期永远不会触发，留着只是占位）</li>
     *   <li>error boundary：必须配置 errorRef（错误码），且不能挂在 start event 上</li>
     *   <li>cancel boundary：仅在子流程（含 call activity）边界有效，
     *       挂在 UserTask 上 Flowable 会忽略</li>
     *   <li>message / signal boundary：建议配置 messageRef / signalRef（不阻断但记录告警）</li>
     * </ul>
     */
    private void validateBoundaryEvents(BpmnModel bpmnModel, List<String> problems) {
        Process mainProcess = bpmnModel.getMainProcess();
        if (mainProcess == null) {
            return;
        }
        // 1. 遍历主流程与所有子流程的 FlowElement
        // 宿主活动 id -> 该宿主上的 boundary 类型 -> 出现次数
        Map<String, Map<String, Integer>> hostBoundaryTypeCount = new HashMap<>();
        walkBoundary(mainProcess.getFlowElements(), hostBoundaryTypeCount, problems);
        // 2. 检查重复（同宿主同类型 boundary 重复）
        hostBoundaryTypeCount.forEach((hostId, typeMap) -> typeMap.forEach((type, count) -> {
            if (count > 1) {
                problems.add("宿主活动[" + hostId + "] 上挂了 " + count + " 个同类型（" + type + "）的边界事件，"
                        + "Flowable 行为未定义，建议合并或换成不同类型");
            }
        }));
    }

    /**
     * 递归遍历主流程/子流程/事件子流程的 FlowElement，校验每个 BoundaryEvent。
     */
    private void walkBoundary(Collection<FlowElement> elements,
                              Map<String, Map<String, Integer>> hostBoundaryTypeCount,
                              List<String> problems) {
        if (elements == null || elements.isEmpty()) {
            return;
        }
        for (FlowElement element : elements) {
            if (element instanceof BoundaryEvent) {
                checkOneBoundary((BoundaryEvent) element, hostBoundaryTypeCount, problems);
            }
            if (element instanceof SubProcess) {
                walkBoundary(((SubProcess) element).getFlowElements(), hostBoundaryTypeCount, problems);
            }
        }
    }

    /**
     * 校验单个 boundary event。
     */
    private void checkOneBoundary(BoundaryEvent boundary,
                                  Map<String, Map<String, Integer>> hostBoundaryTypeCount,
                                  List<String> problems) {
        String name = StringUtils.isNotBlank(boundary.getName()) ? boundary.getName() : boundary.getId();
        // 1. 宿主活动校验
        String hostActivityId = boundary.getAttachedToRefId();
        if (StringUtils.isBlank(hostActivityId)) {
            problems.add("边界事件[" + name + "] 没有挂在任何活动上（必须 attachedToRef 一个 UserTask/SubProcess/CallActivity）");
            return;
        }
        // 2. 统计 type 用于后续"同宿主同类型"重复检查
        EventDefinition eventDef = boundary.getEventDefinitions().isEmpty() ? null
                : boundary.getEventDefinitions().get(0);
        if (eventDef == null) {
            problems.add("边界事件[" + name + "] 没有配置事件定义（必须配 timer/error/message/signal/cancel/... 之一）");
            return;
        }
        String type = eventDef.getClass().getSimpleName();
        hostBoundaryTypeCount.computeIfAbsent(hostActivityId, k -> new HashMap<>())
                .merge(type, 1, Integer::sum);
        // 3. 按事件类型逐项校验
        if (eventDef instanceof TimerEventDefinition) {
            TimerEventDefinition timer = (TimerEventDefinition) eventDef;
            boolean hasAny = timer.getTimeDuration() != null
                    || timer.getTimeCycle() != null
                    || timer.getTimeDate() != null;
            if (!hasAny) {
                problems.add("定时器边界事件[" + name + "] 没有配置时长(timeDuration/cycle/date)，"
                        + "运行期永远不会触发，请补充配置");
            }
        } else if (eventDef instanceof ErrorEventDefinition) {
            ErrorEventDefinition error = (ErrorEventDefinition) eventDef;
            if (StringUtils.isBlank(error.getErrorCode())) {
                problems.add("错误边界事件[" + name + "] 必须配置 errorCode（错误码），"
                        + "没有错误码则 ServiceTask 抛 BpmnError 时无法定位到该边界");
            }
        } else if (eventDef instanceof CancelEventDefinition) {
            // cancel 边界按 BPMN 规范只能挂在 transaction 子流程或 call activity 边界上
            // Flowable 不会阻断，但运行期永远不会触发；这里给个 warning
            problems.add("取消边界事件[" + name + "] 仅在 transaction / call activity 子流程边界有效，"
                    + "挂在 UserTask 上时运行期不会触发，建议改用 timer/error/message 类型");
        }
        // 4. message / signal / escalation / compensation / conditional 的非强制校验（如需要可后续补）
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
