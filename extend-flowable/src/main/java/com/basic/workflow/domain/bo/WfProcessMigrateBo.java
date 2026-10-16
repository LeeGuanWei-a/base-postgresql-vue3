package com.basic.workflow.domain.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * HTTP 入参：触发流程替换。
 *
 * <p>仅描述 HTTP 入参，没有 BiConsumer 字段（业务回调不暴露给 HTTP）。
 * HTTP 入口的 {@code businessCallback} 由 Controller 在收到请求后注入：
 * 调用 {@code WfProcessMigratePlan#setBusinessCallback(...)} 设值。</p>
 *
 * @author integration-supplement
 */
@Data
@ApiModel(value = "WfProcessMigrateBo", description = "流程替换入参")
public class WfProcessMigrateBo {

    /**
     * 必填：要替换的旧流程实例 id
     */
    @ApiModelProperty(value = "旧流程实例 id", required = true)
    private String oldProcInsId;

    /**
     * 必填：新流程定义 key
     */
    @ApiModelProperty(value = "新流程定义 key", required = true)
    private String newProcessDefinitionKey;

    /**
     * 选填：启动新流程时附加的变量
     */
    @ApiModelProperty(value = "启动新流程时附加的变量")
    private Map<String, Object> startVariables;

    /**
     * 选填：是否复制历史意见，默认 true
     */
    @ApiModelProperty(value = "是否复制历史意见", notes = "默认 true")
    private Boolean copyComments = Boolean.TRUE;

    /**
     * 选填：是否复制附件，默认 true
     */
    @ApiModelProperty(value = "是否复制附件", notes = "默认 true")
    private Boolean copyAttachments = Boolean.TRUE;

    /**
     * 选填：变更原因
     */
    @ApiModelProperty(value = "变更原因")
    private String reason;

    /**
     * 镜像推进步骤
     */
    @ApiModelProperty(value = "镜像推进步骤")
    private List<AdvanceStepBo> advanceSteps;

    @Data
    @ApiModel(value = "AdvanceStepBo", description = "镜像推进步骤入参")
    public static class AdvanceStepBo {
        @ApiModelProperty(value = "taskDefinitionKey", required = true)
        private String taskDefinitionKey;

        @ApiModelProperty(value = "附加流程变量")
        private Map<String, Object> variables;

        @ApiModelProperty(value = "本步操作人 userId")
        private String currentUserId;
    }
}
