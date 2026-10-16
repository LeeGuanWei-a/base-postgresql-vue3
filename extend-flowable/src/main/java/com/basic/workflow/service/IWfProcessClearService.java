package com.basic.workflow.service;

import java.util.Map;

/**
 * 流程数据清盘服务。
 *
 * <p><b>危险操作</b>：会把流程相关的全部数据物理删除，不可恢复。仅用于开发/测试环境重置，
 * 或交付前的环境清理。生产环境请务必限制权限（{@code workflow:process:clearAll}）。</p>
 *
 * <p>清除范围：</p>
 * <ol>
 *   <li><b>流程实例</b>：运行中的强制终止后删除；历史实例批量删除</li>
 *   <li><b>流程定义 / 部署 / 模型</b>：act_re_procdef、act_re_deployment、act_re_model</li>
 *   <li><b>引擎残留</b>：act_ru_* / act_hi_* / act_ge_bytearray / act_procdef_info / act_evt_log</li>
 *   <li><b>业务流程表</b>：wf_category、wf_category_org_link、wf_comment_info、wf_copy、
 *       wf_deploy_form、wf_form、wf_form_category、wf_form_template、wf_task_form、
 *       tbl_flow_extend_procinst、flw_expression</li>
 *   <li><b>事件注册表残留</b>：flw_event_* / flw_channel_definition / flw_ru_batch*</li>
 * </ol>
 *
 * <p><b>明确不清除</b>：</p>
 * <ul>
 *   <li>{@code act_ge_property} —— 引擎 schema 版本与自增 id，删了引擎起不来</li>
 *   <li>{@code flw_ev_databasechangelog} / {@code flw_ev_databasechangeloglock} —— Liquibase 变更日志</li>
 *   <li>{@code sys_menu} / {@code sys_role_menu} —— 菜单与权限配置，不属于"流程数据"</li>
 *   <li>{@code act_de_*} —— flowable-ui-modeler 的表，本项目不使用且部分环境不存在</li>
 * </ul>
 *
 * @author integration-supplement
 */
public interface IWfProcessClearService {

    /**
     * 预览将要删除的数量，用于前端二次确认展示。
     *
     * @return 运行中实例数、历史实例数、部署数、模型数等
     */
    Map<String, Object> preview();

    /**
     * 执行清盘。
     *
     * @param confirmText 确认短语，必须等于约定的固定值，防止误调用
     * @return 各步骤实际删除的数量统计
     */
    Map<String, Object> clearAll(String confirmText);
}
