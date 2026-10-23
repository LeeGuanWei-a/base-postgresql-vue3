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
     * @return WfProcessMigrateResult 替换结果，含新流程实例 ID
     * @description 用新流程定义替换旧流程实例，依次完成校验、启动新流程、镜像推进、复制意见与附件、
     * 调用业务回调、废除旧流程，任一步失败整体回滚并抛出迁移异常
     * @params [plan] 替换计划，提供旧实例 ID、新流程定义标识与各项复制开关
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfProcessMigrateResult replaceProcessInstance(WfProcessMigratePlan plan);

    /**
     * @return String 校验失败信息，null 表示通过
     * @description 校验替换计划是否可执行，只做前置检查不实际执行，供先校验再执行的场景使用
     * @params [plan] 待校验的替换计划
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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

    /**
     * @return boolean true 表示 null 或全为空白字符
     * @description 判断字符串是否为 null 或只含空白字符
     * @params [s] 待判断的字符串
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
