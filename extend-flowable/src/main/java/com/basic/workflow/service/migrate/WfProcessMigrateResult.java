package com.basic.workflow.service.migrate;

import lombok.Builder;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * 流程替换结果。
 *
 * <p>成功返回后，调用方应：
 * <ol>
 *   <li>用 {@link #newProcInsId} 替换业务表里旧 procInsId（如果没在 plan.businessCallback 里做）</li>
 *   <li>如果是想给用户一个友好的跳转 URL，{@link #newProcessDefinitionKey} + {@link #newProcInsId} 拼出详情页</li>
 *   <li>{@link #copyCommentCount} / {@link #copyAttachmentCount} 用于审计 / 给用户展示</li>
 * </ol>
 *
 * @author integration-supplement
 */
@Data
@Builder
@Schema(description = "流程替换结果")
public class WfProcessMigrateResult {

    /**
     * 旧流程实例 id（回传）
     */
    @Schema(description = "旧流程实例 id")
    private String oldProcInsId;

    /**
     * 旧流程定义 key
     */
    @Schema(description = "旧流程定义 key")
    private String oldProcessDefinitionKey;

    /**
     * 新流程实例 id
     */
    @Schema(description = "新流程实例 id")
    private String newProcInsId;

    /**
     * 新流程定义 key
     */
    @Schema(description = "新流程定义 key")
    private String newProcessDefinitionKey;

    /**
     * 复制的历史意见条数
     */
    @Schema(description = "复制的历史意见条数")
    private Integer copyCommentCount;

    /**
     * 复制的附件条数
     */
    @Schema(description = "复制的附件条数")
    private Integer copyAttachmentCount;

    /**
     * 跳过的镜像推进步骤数
     */
    @Schema(description = "跳过的镜像推进步骤数")
    private Integer advancedStepCount;
}
