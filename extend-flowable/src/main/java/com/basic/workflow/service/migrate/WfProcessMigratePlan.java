package com.basic.workflow.service.migrate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 流程替换计划（运行中实例迁移到新流程定义）。
 *
 * <p>本类只描述"如何替换"，不感知任何具体业务表（业务表更新通过 {@link #businessCallback} 由调用方提供）。
 * 这保证本能力在 base-postgresql-vue3 内部不绑死具体业务对象，调用方自由指定要更新的字段。</p>
 *
 * <h3>典型用法</h3>
 * <pre>
 * WfProcessMigratePlan plan = WfProcessMigratePlan.builder()
 *     .oldProcInsId("a1b2c3...")
 *     .newProcessDefinitionKey("payOld")
 *     .startVariables(Map.of("st", 0))
 *     .advanceSteps(List.of(
 *         WfProcessMigratePlan.AdvanceStep.skip("Activity_audit_first"),
 *         WfProcessMigratePlan.AdvanceStep.skip("Activity_audit_second")))
 *     .businessCallback((oldId, newId) -> jkApplyMoneyService.updateProcIncId(newId))
 *     .build();
 * String newProcInsId = migrateService.replaceProcessInstance(plan);
 * </pre>
 *
 * @author integration-supplement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "WfProcessMigratePlan", description = "流程替换计划")
public class WfProcessMigratePlan {

    /**
     * 必填：要替换的旧流程实例 id
     */
    @ApiModelProperty(value = "旧流程实例 id", required = true)
    private String oldProcInsId;

    /**
     * 必填：新流程定义 key（不带 .bpmn 后缀，本服务会自己拼）
     */
    @ApiModelProperty(value = "新流程定义 key", required = true)
    private String newProcessDefinitionKey;

    /**
     * 选填：启动新流程时附加的变量
     */
    @ApiModelProperty(value = "启动新流程时附加的变量")
    private Map<String, Object> startVariables = new HashMap<>();

    /**
     * 选填：是否复制旧流程的历史意见到新流程。默认 true
     */
    @ApiModelProperty(value = "是否复制历史意见", notes = "默认 true")
    private Boolean copyComments = Boolean.TRUE;

    /**
     * 选填：是否复制旧流程的附件到新流程。默认 true
     */
    @ApiModelProperty(value = "是否复制附件", notes = "默认 true")
    private Boolean copyAttachments = Boolean.TRUE;

    /**
     * 选填：变更原因，会作为一条新的审批意见写入新流程（"流程替换：xxx"）
     */
    @ApiModelProperty(value = "变更原因")
    private String reason;

    /**
     * 选填：镜像推进步骤——按顺序在新流程上调用 complete 把指定 taskDefinitionKey 推过去
     * 每一项：(taskDefinitionKey, 推进时附加的变量)
     */
    @ApiModelProperty(value = "镜像推进步骤", notes = "按 taskDefinitionKey 顺序推进，每步可带变量")
    private List<AdvanceStep> advanceSteps;

    /**
     * 选填：业务表更新回调——旧流程实例 id -> 新流程实例 id
     * 调用方在此更新自己业务表里的 procInsId 字段。
     * <p><b>注意：</b>本回调在事务内执行，回调内不要做跨事务操作（不要发消息、不要调外部接口、不要起新流程）。
     * 有这类需求请在 Service 返回后由调用方在外层做。</p>
     */
    @ApiModelProperty(hidden = true, notes = "业务表更新回调，旧实例 id -> 新实例 id")
    private BiConsumer<String, String> businessCallback;

    /**
     * 镜像推进步骤
     */
    @Data
    @ApiModel(value = "AdvanceStep", description = "镜像推进步骤")
    public static class AdvanceStep {
        /**
         * 必填：目标 taskDefinitionKey
         * 推进逻辑：查到新流程上当前 taskDefinitionKey 等于这个值的 task，complete 它；
         * 如果查不到（已推过或流程设计中没有这个 key），跳过本步
         */
        @ApiModelProperty(value = "taskDefinitionKey", required = true)
        private String taskDefinitionKey;

        /**
         * 选填：推进这一步时附加的流程变量（如 ${flag}、${frame} 等）
         */
        @ApiModelProperty(value = "附加流程变量")
        private Map<String, Object> variables = new HashMap<>();

        /**
         * 选填：当前步骤的"操作人 userId"，会写入 ACT_HI_TASKINST.assignee
         * 不填时取新流程当前 task 的 assignee（默认）
         */
        @ApiModelProperty(value = "本步操作人 userId", notes = "不填时取新流程当前 task 的 assignee")
        private String currentUserId;

        public AdvanceStep() {
        }

        public AdvanceStep(String taskDefinitionKey) {
            this.taskDefinitionKey = taskDefinitionKey;
        }

        public AdvanceStep(String taskDefinitionKey, Map<String, Object> variables) {
            this.taskDefinitionKey = taskDefinitionKey;
            this.variables = variables;
        }

        public static AdvanceStep skip(String taskDefinitionKey) {
            return new AdvanceStep(taskDefinitionKey);
        }

        public static AdvanceStep skip(String taskDefinitionKey, Map<String, Object> variables) {
            return new AdvanceStep(taskDefinitionKey, variables);
        }
    }
}
