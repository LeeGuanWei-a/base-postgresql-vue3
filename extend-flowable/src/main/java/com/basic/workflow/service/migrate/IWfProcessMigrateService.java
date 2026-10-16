package com.basic.workflow.service.migrate;

/**
 * 流程替换服务（运行中实例迁移到新流程定义）。
 *
 * <p>典型场景：人事变动后，某个 UserTask 的审批人换了；或者业务模式变了，需要把已发起的流程
 * 切换到新版本的流程定义。本服务做"原子替换"——<b>不感知任何具体业务表</b>，业务表的 procInsId 更新
 * 由调用方通过 {@link WfProcessMigratePlan#getBusinessCallback()} 注入。</p>
 *
 * <h3>事务语义</h3>
 * <ul>
 *   <li>本服务所有实现都开启事务，<b>任何一步失败整体回滚</b></li>
 *   <li>启动新流程、镜像推进、复制意见、复制附件、业务回调、废除旧流程 6 步在同一事务内</li>
 *   <li>失败时抛 {@link WfProcessMigrateException}，调用方 try-catch 即可</li>
 * </ul>
 *
 * <h3>不能做什么</h3>
 * <ul>
 *   <li>不能跨 modelKey 映射变量/表单（旧流程的流程变量不会自动迁到新流程）</li>
 *   <li>不能"部分迁移"——要么全换，要么不换</li>
 *   <li>不能迁移"草稿"状态（没实例可换）</li>
 *   <li>不能迁移"已结束"流程</li>
 * </ul>
 *
 * @author integration-supplement
 */
public interface IWfProcessMigrateService {

    /**
     * 用新流程定义替换旧流程实例。
     *
     * <p>执行步骤：</p>
     * <ol>
     *   <li>校验旧流程存在且未结束</li>
     *   <li>校验新流程定义存在且未挂起</li>
     *   <li>启动新流程（沿用旧流程的启动人 / businessName / formName 等）</li>
     *   <li>按 {@code advanceSteps} 镜像推进新流程到与旧流程等价的下一站</li>
     *   <li>复制历史意见（按 plan.copyComments）</li>
     *   <li>复制附件（按 plan.copyAttachments）</li>
     *   <li>调 businessCallback 通知调用方更新业务表</li>
     *   <li>废除旧流程（abolishProcess）</li>
     * </ol>
     *
     * @param plan 替换计划
     * @return 替换结果（含新流程实例 id）
     * @throws WfProcessMigrateException 任何步骤失败抛此异常
     */
    WfProcessMigrateResult replaceProcessInstance(WfProcessMigratePlan plan);

    /**
     * 校验给定的 plan 是否能执行（不实际执行）。
     * 用于 HTTP 入口"先校验，再执行"的场景。
     *
     * @return 校验失败信息（null 表示通过）
     */
    default String validate(WfProcessMigratePlan plan) {
        if (plan == null) {
            return "plan 不能为空";
        }
        if (isBlank(plan.getOldProcInsId())) {
            return "oldProcInsId 不能为空";
        }
        if (isBlank(plan.getNewProcessDefinitionKey())) {
            return "newProcessDefinitionKey 不能为空";
        }
        return null;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
