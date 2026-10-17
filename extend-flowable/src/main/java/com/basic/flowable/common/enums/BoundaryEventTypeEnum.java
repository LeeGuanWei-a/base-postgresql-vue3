package com.basic.flowable.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 边界事件类型枚举。
 *
 * <p>对应 BPMN 2.0 规范的 8 种边界事件（挂在 Activity 边界上的 catch 事件）。
 * 与 {@code bpmn:BoundaryEvent} 下 {@code eventDefinitions[0].$type} 取值一一对应。</p>
 *
 * <p>约定：</p>
 * <ul>
 *   <li>{@link #TIMER} — 定时器，依赖 Flowable Job Executor，无需外部触发</li>
 *   <li>{@link #MESSAGE} — 消息，调用 {@code RuntimeService.messageEventReceived} 触发</li>
 *   <li>{@link #SIGNAL} — 信号，调用 {@code RuntimeService.signalEventReceived} 触发</li>
 *   <li>{@link #ERROR} — 错误，由 ServiceTask/JavaDelegate 抛 BpmnError 触发</li>
 *   <li>{@link #ESCALATION} — 升级，由 ServiceTask/JavaDelegate 抛 BpmnEscalation 触发</li>
 *   <li>{@link #CANCEL} — 取消，仅在子流程边界上有效，由事务/外部取消触发</li>
 *   <li>{@link #COMPENSATION} — 补偿，由 throw compensation 触发</li>
 *   <li>{@link #CONDITIONAL} — 条件，依赖条件表达式求值为 true 触发</li>
 * </ul>
 *
 * @author integration-supplement
 */
@Getter
@AllArgsConstructor
public enum BoundaryEventTypeEnum {

    /**
     * 定时器边界
     */
    TIMER("TimerEventDefinition", "定时器", "timer"),
    /**
     * 消息边界
     */
    MESSAGE("MessageEventDefinition", "消息", "message"),
    /**
     * 信号边界
     */
    SIGNAL("SignalEventDefinition", "信号", "signal"),
    /**
     * 错误边界
     */
    ERROR("ErrorEventDefinition", "错误", "error"),
    /**
     * 升级边界
     */
    ESCALATION("EscalationEventDefinition", "升级", "escalation"),
    /**
     * 取消边界（仅在子流程边界有效）
     */
    CANCEL("CancelEventDefinition", "取消", "cancel"),
    /**
     * 补偿边界
     */
    COMPENSATION("CompensateEventDefinition", "补偿", "compensation"),
    /**
     * 条件边界
     */
    CONDITIONAL("ConditionalEventDefinition", "条件", "conditional");

    /**
     * BPMN moddle 里的 $type 值，例如 {@code bpmn:TimerEventDefinition} 的 {@code TimerEventDefinition}
     */
    private final String definitionType;
    /**
     * 中文展示名
     */
    private final String name;
    /**
     * 英文简写（前缀会拼到自定义扩展元素名上，如 {@code boundaryType}）
     */
    private final String shortName;

    /**
     * @return BoundaryEventTypeEnum 命中的枚举，未命中或入参为空时返回 null
     * @description 按 BPMN moddle 的 $type 短名反查并返回对应的边界事件类型枚举
     * @params [definitionType] bpmn:*EventDefinition 的类型名，可带 bpmn: 前缀
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static BoundaryEventTypeEnum ofDefinitionType(String definitionType) {
        if (definitionType == null || definitionType.isEmpty()) {
            return null;
        }
        // 取冒号后的短名（如 "bpmn:TimerEventDefinition" -> "TimerEventDefinition"），
        // 单独声明为 final 局部变量，避免在 if 里重新赋值后无法被 lambda 引用
        final String shortType;
        int colonIdx = definitionType.indexOf(':');
        if (colonIdx >= 0 && colonIdx < definitionType.length() - 1) {
            shortType = definitionType.substring(colonIdx + 1);
        } else {
            shortType = definitionType;
        }
        return Arrays.stream(values())
                .filter(e -> e.definitionType.equals(shortType))
                .findFirst()
                .orElse(null);
    }

    /**
     * @return List<String> 全部枚举项的英文简写列表
     * @description 收集全部枚举项的 shortName 组成列表，供前端下拉框直接使用
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<String> allShortNames() {
        return Arrays.stream(values()).map(BoundaryEventTypeEnum::getShortName).collect(Collectors.toList());
    }
}
