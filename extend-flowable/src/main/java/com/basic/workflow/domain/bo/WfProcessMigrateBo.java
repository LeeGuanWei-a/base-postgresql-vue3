package com.basic.workflow.domain.bo;

import lombok.Data;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "流程替换入参")
public class WfProcessMigrateBo {

    /**
     * 必填：要替换的旧流程实例 id
     */
    @Schema(description = "旧流程实例 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldProcInsId;

    /**
     * 必填：新流程定义 key
     */
    @Schema(description = "新流程定义 key", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newProcessDefinitionKey;

    /**
     * 选填：启动新流程时附加的变量
     */
    @Schema(description = "启动新流程时附加的变量")
    private Map<String, Object> startVariables;

    /**
     * 选填：是否复制历史意见，默认 true
     */
    @Schema(description = "是否复制历史意见")
    private Boolean copyComments = Boolean.TRUE;

    /**
     * 选填：是否复制附件，默认 true
     */
    @Schema(description = "是否复制附件")
    private Boolean copyAttachments = Boolean.TRUE;

    /**
     * 选填：变更原因
     */
    @Schema(description = "变更原因")
    private String reason;

    /**
     * 镜像推进步骤
     */
    @Schema(description = "镜像推进步骤")
    private List<AdvanceStepBo> advanceSteps;

    @Data
    @Schema(description = "镜像推进步骤入参")
    public static class AdvanceStepBo {
        @Schema(description = "taskDefinitionKey", requiredMode = Schema.RequiredMode.REQUIRED)
        private String taskDefinitionKey;

        @Schema(description = "附加流程变量")
        private Map<String, Object> variables;

        @Schema(description = "本步操作人 userId")
        private String currentUserId;
    }
}
