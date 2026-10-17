package com.basic.workflow.domain.vo;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * 流程实例上已注册的 boundary 事件 VO（用于流程图查看器高亮、调试面板等）。
 *
 * @author integration-supplement
 */
@Data
@Schema(description = "已注册的 boundary 事件")
public class WfBoundaryEventVo {

    /**
     * boundary 在 BPMN 里的 id
     */
    @Schema(description = "boundary 节点 id")
    private String id;

    /**
     * boundary 名称
     */
    @Schema(description = "boundary 节点名称")
    private String name;

    /**
     * 事件类型：timer/message/signal/error/cancel/...
     */
    @Schema(description = "事件类型")
    private String eventType;

    /**
     * 挂在哪个宿主活动 id 上
     */
    @Schema(description = "宿主活动 id")
    private String attachedToRefId;

    /**
     * 宿主活动名称（便于前端展示）
     */
    @Schema(description = "宿主活动名称")
    private String attachedToRefName;

    /**
     * 是否中断宿主活动（cancelActivity = true）
     */
    @Schema(description = "是否中断宿主活动")
    private Boolean cancelActivity;

    /**
     * timer 边界 - 时长表达式（ISO 8601 duration，如 PT2H）
     */
    @Schema(description = "timer 时长")
    private String timerDuration;

    /**
     * timer 边界 - 周期表达式
     */
    @Schema(description = "timer 周期")
    private String timerCycle;

    /**
     * error 边界 - 错误码
     */
    @Schema(description = "error 错误码")
    private String errorCode;

    /**
     * signal 边界 - 信号 code
     */
    @Schema(description = "signal code")
    private String signalCode;

    /**
     * message 边界 - 消息 id
     */
    @Schema(description = "message id")
    private String messageId;
}
