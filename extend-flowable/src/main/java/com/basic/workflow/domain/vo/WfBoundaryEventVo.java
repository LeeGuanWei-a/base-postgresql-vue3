package com.basic.workflow.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 流程实例上已注册的 boundary 事件 VO（用于流程图查看器高亮、调试面板等）。
 *
 * @author integration-supplement
 */
@Data
@ApiModel(value = "WfBoundaryEventVo", description = "已注册的 boundary 事件")
public class WfBoundaryEventVo {

    /**
     * boundary 在 BPMN 里的 id
     */
    @ApiModelProperty(value = "boundary 节点 id")
    private String id;

    /**
     * boundary 名称
     */
    @ApiModelProperty(value = "boundary 节点名称")
    private String name;

    /**
     * 事件类型：timer/message/signal/error/cancel/...
     */
    @ApiModelProperty(value = "事件类型")
    private String eventType;

    /**
     * 挂在哪个宿主活动 id 上
     */
    @ApiModelProperty(value = "宿主活动 id")
    private String attachedToRefId;

    /**
     * 宿主活动名称（便于前端展示）
     */
    @ApiModelProperty(value = "宿主活动名称")
    private String attachedToRefName;

    /**
     * 是否中断宿主活动（cancelActivity = true）
     */
    @ApiModelProperty(value = "是否中断宿主活动")
    private Boolean cancelActivity;

    /**
     * timer 边界 - 时长表达式（ISO 8601 duration，如 PT2H）
     */
    @ApiModelProperty(value = "timer 时长")
    private String timerDuration;

    /**
     * timer 边界 - 周期表达式
     */
    @ApiModelProperty(value = "timer 周期")
    private String timerCycle;

    /**
     * error 边界 - 错误码
     */
    @ApiModelProperty(value = "error 错误码")
    private String errorCode;

    /**
     * signal 边界 - 信号 code
     */
    @ApiModelProperty(value = "signal code")
    private String signalCode;

    /**
     * message 边界 - 消息 id
     */
    @ApiModelProperty(value = "message id")
    private String messageId;
}
