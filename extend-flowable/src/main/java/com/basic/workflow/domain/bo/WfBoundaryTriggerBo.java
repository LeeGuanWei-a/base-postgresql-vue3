package com.basic.workflow.domain.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 边界事件触发请求业务对象。
 *
 * <p>由 {@code /workflow/boundary/{signal|message|error|cancel}} 接口接收，
 * 携带流程实例 id、事件 code/ID、附加变量。</p>
 *
 * @author integration-supplement
 */
@Data
@ApiModel(value = "WfBoundaryTriggerBo", description = "边界事件触发请求")
public class WfBoundaryTriggerBo {

    /**
     * 流程实例 id（必填）
     */
    @ApiModelProperty(value = "流程实例id", required = true)
    private String procInsId;

    /**
     * 信号 code / 消息 id / 错误 code（按 type 决定字段含义）
     */
    @ApiModelProperty(value = "事件 code（signal:信号code, message:消息id, error:错误code）")
    private String eventCode;

    /**
     * 触发原因 / 备注
     */
    @ApiModelProperty(value = "触发原因 / 备注（写入流程变量）")
    private String comment;

    /**
     * 触发时附加的流程变量
     */
    @ApiModelProperty(value = "触发时附加的流程变量")
    private Map<String, Object> variables;
}
