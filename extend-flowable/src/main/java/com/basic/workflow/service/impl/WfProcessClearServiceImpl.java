package com.basic.workflow.service.impl;

import com.basic.common.exception.ServiceException;
import com.basic.workflow.customCache.CustomDeploymentCache;
import com.basic.workflow.mapper.WfProcessClearMapper;
import com.basic.workflow.service.IWfProcessClearService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 流程数据清盘服务实现。
 *
 * <p>执行顺序与原因：</p>
 * <ol>
 *   <li><b>运行中实例</b>：调 {@code runtimeService.deleteProcessInstance} 逐个强制终止。
 *       必须走引擎 API —— 它会同时清理执行树、任务、变量、身份链接、定时器作业与事件订阅，
 *       并正确维护历史。直接 DELETE 表会留下引擎内存态与孤儿作业。</li>
 *   <li><b>历史实例</b>：{@code historyService.bulkDeleteHistoricProcessInstances} 分批删除
 *       （单次调用有数量上限，这里按 500 一批）。</li>
 *   <li><b>部署</b>：{@code repositoryService.deleteDeployment(id, true)} 级联删除。
 *       必须走引擎 API —— 它会驱逐<b>引擎内部</b>的流程定义缓存；
 *       项目自研的应用级 {@code CustomDeploymentCache} 不在引擎缓存链路上，需另行 clear（下一步）。</li>
 *   <li><b>模型</b>：{@code repositoryService.deleteModel(id)}。</li>
 *   <li><b>应用级定义缓存</b>：{@code customDeploymentCache.clear()}。</li>
 *   <li><b>残留表 + 业务表</b>：前面步骤之后，act_ru_* / act_hi_* / act_ge_bytearray
 *       通常已经空了（这里仍做一次兜底清理，用于回收孤儿行），再清 wf_* 业务表。
 *       表清单先经 information_schema 过滤，只删当前库里真实存在的表。</li>
 * </ol>
 *
 * @author integration-supplement
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WfProcessClearServiceImpl implements IWfProcessClearService {

    /**
     * 确认短语。前端要求用户手动输入同样的文字，防止误点。
     */
    public static final String CONFIRM_TEXT = "清空流程";

    /**
     * 历史实例批量删除的分批大小。
     */
    private static final int HISTORY_BATCH_SIZE = 500;

    /**
     * 表名合法性校验（表名不能用预编译参数，必须自己把住）。
     */
    private static final Pattern SAFE_TABLE_NAME = Pattern.compile("^[a-z_][a-z0-9_]*$");

    /**
     * 待清空的表清单，<b>按外键依赖关系从子到父排列</b>。
     *
     * <p>不含 {@code act_ge_property}（引擎 schema 版本）、
     * {@code flw_ev_databasechangelog*}（Liquibase 变更日志）、
     * {@code act_de_*}（flowable-ui-modeler 的表，本项目不用且部分环境不存在）、
     * {@code sys_menu}/{@code sys_role_menu}（权限配置，不属于流程数据）。</p>
     */
    private static final List<String> CLEAR_TABLES = Arrays.asList(
            // ---- Flowable 运行时（子表 -> 父表）----
            "act_ru_entitylink",
            "act_ru_identitylink",
            "act_ru_variable",
            "act_ru_task",
            "act_ru_actinst",
            "act_ru_event_subscr",
            "act_ru_deadletter_job",
            "act_ru_external_job",
            "act_ru_history_job",
            "act_ru_suspended_job",
            "act_ru_timer_job",
            "act_ru_job",
            "act_ru_execution",
            // ---- Flowable 历史（子表 -> 父表）----
            "act_hi_detail",
            "act_hi_varinst",
            "act_hi_tsk_log",
            "act_hi_attachment",
            "act_hi_comment",
            "act_hi_entitylink",
            "act_hi_identitylink",
            "act_hi_actinst",
            "act_hi_taskinst",
            "act_hi_procinst",
            // ---- 定义 / 部署 / 模型 ----
            "act_procdef_info",
            "act_re_procdef",
            "act_re_model",
            "act_re_deployment",
            "act_ge_bytearray",
            "act_evt_log",
            // ---- 事件注册表 / 批处理 ----
            "flw_ru_batch_part",
            "flw_ru_batch",
            "flw_event_resource",
            "flw_event_definition",
            "flw_event_deployment",
            "flw_channel_definition",
            // ---- 业务流程表 ----
            "wf_task_form",
            "wf_deploy_form",
            "wf_comment_info",
            "wf_copy",
            "wf_category_org_link",
            "wf_category",
            "wf_form",
            "wf_form_category",
            "wf_form_template",
            "tbl_flow_extend_procinst",
            "flw_expression"
    );

    /**
     * 预览时展示行数的业务表（让用户知道会删掉多少业务数据）。
     */
    private static final List<String> PREVIEW_BUSINESS_TABLES = Arrays.asList(
            "wf_category",
            "wf_form",
            "wf_form_category",
            "wf_form_template",
            "wf_comment_info",
            "wf_copy",
            "wf_task_form",
            "wf_deploy_form",
            "tbl_flow_extend_procinst",
            "flw_expression"
    );

    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final RepositoryService repositoryService;
    private final WfProcessClearMapper wfProcessClearMapper;
    /**
     * 项目自研的应用级流程定义缓存（单条）。
     *
     * <p>注意：它<b>没有注册进 Flowable 引擎</b>（全项目没有 setProcessDefinitionCache 调用），
     * 只是 {@code WfBpmnModelServiceImpl} 自己用的缓存，所以引擎删除部署时<b>不会</b>连带清它，
     * 必须在清盘时手动 clear，否则清完还能从内存里读到已删除的流程定义。</p>
     */
    private final CustomDeploymentCache customDeploymentCache;

    @Override
    public Map<String, Object> preview() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("runningInstanceCount", runtimeService.createProcessInstanceQuery().count());
        result.put("historicInstanceCount", historyService.createHistoricProcessInstanceQuery().count());
        result.put("deploymentCount", repositoryService.createDeploymentQuery().count());
        result.put("modelCount", repositoryService.createModelQuery().count());
        result.put("confirmText", CONFIRM_TEXT);
        result.put("businessCounts", countBusinessTables());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> clearAll(String confirmText) {
        if (!CONFIRM_TEXT.equals(confirmText)) {
            throw new ServiceException("确认短语不正确，请输入：" + CONFIRM_TEXT);
        }
        long begin = System.currentTimeMillis();
        log.warn("[process-clear] ========== 开始清空全部流程数据 ==========");

        Map<String, Object> result = new LinkedHashMap<>();

        // ---------- 1. 运行中实例：强制终止 ----------
        List<ProcessInstance> runningList = runtimeService.createProcessInstanceQuery().list();
        int killed = 0;
        for (ProcessInstance pi : runningList) {
            try {
                runtimeService.deleteProcessInstance(pi.getId(), "流程数据清盘");
                killed++;
            } catch (Exception e) {
                // 单条失败不阻断整体清盘：后续表清理会兜底回收残留
                log.warn("[process-clear] 终止实例失败，已跳过 procInsId={} err={}",
                        pi.getId(), e.getMessage());
            }
        }
        result.put("killedRunningInstances", killed);
        log.warn("[process-clear] 运行中实例已强制终止 {} 个（共 {} 个）", killed, runningList.size());

        // ---------- 2. 历史实例：分批删除 ----------
        List<String> hisIds = historyService.createHistoricProcessInstanceQuery().list()
                .stream().map(HistoricProcessInstance::getId).collect(Collectors.toList());
        int deletedHistoric = 0;
        for (int i = 0; i < hisIds.size(); i += HISTORY_BATCH_SIZE) {
            List<String> batch = hisIds.subList(i, Math.min(i + HISTORY_BATCH_SIZE, hisIds.size()));
            historyService.bulkDeleteHistoricProcessInstances(new ArrayList<>(batch));
            deletedHistoric += batch.size();
        }
        result.put("deletedHistoricInstances", deletedHistoric);
        log.warn("[process-clear] 历史实例已删除 {} 个", deletedHistoric);

        // ---------- 3. 部署：级联删除（同时驱逐流程定义缓存）----------
        List<Deployment> deployments = repositoryService.createDeploymentQuery().list();
        int deletedDeployments = 0;
        for (Deployment d : deployments) {
            try {
                repositoryService.deleteDeployment(d.getId(), true);
                deletedDeployments++;
            } catch (Exception e) {
                log.warn("[process-clear] 删除部署失败，已跳过 deploymentId={} err={}",
                        d.getId(), e.getMessage());
            }
        }
        result.put("deletedDeployments", deletedDeployments);
        log.warn("[process-clear] 部署已删除 {} 个（共 {} 个）", deletedDeployments, deployments.size());

        // ---------- 4. 模型 ----------
        List<Model> models = repositoryService.createModelQuery().list();
        int deletedModels = 0;
        for (Model m : models) {
            try {
                repositoryService.deleteModel(m.getId());
                deletedModels++;
            } catch (Exception e) {
                log.warn("[process-clear] 删除模型失败，已跳过 modelId={} err={}",
                        m.getId(), e.getMessage());
            }
        }
        result.put("deletedModels", deletedModels);
        log.warn("[process-clear] 模型已删除 {} 个（共 {} 个）", deletedModels, models.size());

        // ---------- 5. 应用级流程定义缓存 ----------
        // 项目自研的 CustomDeploymentCache 不在 Flowable 引擎缓存链路上，
        // 引擎删部署不会连带清它，这里必须手动清，否则清盘后仍能从内存读到已删定义
        try {
            customDeploymentCache.clear();
            result.put("customCacheCleared", true);
            log.warn("[process-clear] 应用级流程定义缓存已清空");
        } catch (Exception e) {
            result.put("customCacheCleared", false);
            log.warn("[process-clear] 应用级流程定义缓存清理失败：{}", e.getMessage());
        }

        // ---------- 6. 残留表 + 业务表：兜底清空 ----------
        List<String> existingTables = findExistingTables(CLEAR_TABLES);
        List<String> skippedTables = CLEAR_TABLES.stream()
                .filter(t -> !existingTables.contains(t))
                .collect(Collectors.toList());
        if (!skippedTables.isEmpty()) {
            log.warn("[process-clear] 以下表在当前库中不存在，已跳过：{}", skippedTables);
        }
        Map<String, Integer> tableRows = new LinkedHashMap<>();
        for (String table : existingTables) {
            int rows = wfProcessClearMapper.deleteAllFromTable(table);
            tableRows.put(table, rows);
        }
        result.put("clearedTableRows", tableRows);
        result.put("skippedTables", skippedTables);
        int totalRows = tableRows.values().stream().mapToInt(Integer::intValue).sum();
        result.put("clearedTotalRows", totalRows);

        long cost = System.currentTimeMillis() - begin;
        result.put("costMs", cost);
        log.warn("[process-clear] ========== 清空完成：表清理 {} 张 / {} 行，耗时 {} ms ==========",
                existingTables.size(), totalRows, cost);
        return result;
    }

    /**
     * 过滤出当前库里真实存在的表，并做表名安全校验。
     */
    private List<String> findExistingTables(List<String> candidates) {
        // 白名单内再做一次正则校验：表名要拼进 SQL，必须确保形态安全
        List<String> safe = candidates.stream()
                .filter(t -> t != null && SAFE_TABLE_NAME.matcher(t).matches())
                .collect(Collectors.toList());
        if (safe.size() != candidates.size()) {
            log.warn("[process-clear] 有 {} 个表名未通过安全校验，已剔除", candidates.size() - safe.size());
        }
        if (safe.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> existing = wfProcessClearMapper.selectExistingTables(safe);
        return existing == null ? new ArrayList<>() : existing;
    }

    /**
     * 统计业务表行数（只统计存在的表）。
     */
    private Map<String, Integer> countBusinessTables() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String table : findExistingTables(PREVIEW_BUSINESS_TABLES)) {
            counts.put(table, wfProcessClearMapper.countRows(table));
        }
        return counts;
    }
}
