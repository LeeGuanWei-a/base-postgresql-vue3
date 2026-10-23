package com.basic.workflow.controller;

import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.workflow.domain.bo.WfProcessMigrateBo;
import com.basic.workflow.service.migrate.IWfProcessMigrateService;
import com.basic.workflow.service.migrate.WfProcessMigratePlan;
import com.basic.workflow.service.migrate.WfProcessMigrateResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程替换（运行中实例迁移到新流程定义）HTTP 入口。
 *
 * <p>本 Controller <b>不感知任何具体业务表</b>——只暴露纯流程引擎层的能力。
 * 如果调用方需要"替换完顺手更新某业务表 procInsId 字段"，应在调用 Service 时注入
 * {@code businessCallback}，而不是在 Controller 里硬编码业务表更新 SQL。</p>
 *
 * <p>典型调用：</p>
 * <pre>
 * POST /workflow/process/migrate/replace
 * {
 *   "oldProcInsId": "a1b2c3...",
 *   "newProcessDefinitionKey": "payOld",
 *   "startVariables": { "st": 0 },
 *   "advanceSteps": [
 *     { "taskDefinitionKey": "Activity_audit_first" }
 *   ],
 *   "reason": "人事变动：A 离职，由 B 接替"
 * }
 * </pre>
 *
 * @author integration-supplement
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "migrate", description = "工作流流程管理/流程替换（运行中实例迁移到新流程定义）")
@RequestMapping("/workflow/process/migrate")
public class WfProcessMigrateController extends BaseController {

    private final IWfProcessMigrateService wfProcessMigrateService;
    private final RepositoryService repositoryService;
    private final TaskService taskService;
    private final HistoryService historyService;

    /**
     * @return AjaxResult 迁移结果，替换失败时整体回滚并返回错误
     * @description 用新流程定义替换运行中的旧流程实例，事务内执行，失败整体回滚
     * @params [bo] 替换参数，含旧实例ID、新流程定义KEY与镜像推进步骤
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "触发流程替换", description = "用新流程定义替换旧流程实例。失败回滚。")
    @PreAuthorize("@ss.hasPermi('workflow:process:migrate')")
    @PostMapping("/replace")
    public AjaxResult replace(@RequestBody WfProcessMigrateBo bo) {
        WfProcessMigratePlan plan = toPlan(bo);
        WfProcessMigrateResult result = wfProcessMigrateService.replaceProcessInstance(plan);
        return AjaxResult.success(result);
    }

    /**
     * @return AjaxResult 校验通过时返回成功提示，否则返回具体错误原因
     * @description 只校验替换参数与流程状态，不执行实际替换
     * @params [bo] 替换参数，结构同 replace 接口
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "校验流程替换参数", description = "不实际执行，仅校验参数合法性。")
    @PreAuthorize("@ss.hasPermi('workflow:process:migrate')")
    @PostMapping("/validate")
    public AjaxResult validate(@RequestBody WfProcessMigrateBo bo) {
        WfProcessMigratePlan plan = toPlan(bo);
        String err = wfProcessMigrateService.validate(plan);
        if (err != null) {
            return AjaxResult.error(err);
        }
        return AjaxResult.success("参数校验通过");
    }

    /**
     * @return AjaxResult 用户任务节点的 id 与 name 列表，流程定义不存在时返回错误提示
     * @description 按流程定义KEY取主流程并列出全部 userTask 节点的 id 与 name，供前端挑选镜像推进点
     * @params [processDefinitionKey] 流程定义KEY，不含 .bpmn 后缀
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "列出新流程定义的 userTask 节点", description = "供前端做镜像推进点的智能推荐")
    @PreAuthorize("@ss.hasPermi('workflow:process:migrate')")
    @GetMapping("/userTasks/{processDefinitionKey}")
    public AjaxResult listUserTasks(@PathVariable String processDefinitionKey) {
        if (processDefinitionKey == null || processDefinitionKey.isEmpty()) {
            return AjaxResult.error("processDefinitionKey 不能为空");
        }
        // 模型保存时 WfModelSaveXmlService.save() 会给 process id 拼上 ".bpmn" 后缀，
        // 所以 act_re_procdef.key_ 实际是 "cs.bpmn"。先按原 key 查，没有再补后缀查。
        ProcessDefinition def = findLatestDef(processDefinitionKey);
        if (def == null) {
            // 给个更友好的诊断：是不是只有 model 没部署？提示去流程模型看
            boolean hasModel = repositoryService.createModelQuery()
                    .modelKey(processDefinitionKey)
                    .latestVersion()
                    .count() > 0;
            String hint = hasModel
                    ? "流程定义不存在：" + processDefinitionKey
                        + "（检测到该 key 有模型但未部署，请到「流程模型」点「部署」后再试）"
                    : "流程定义不存在：" + processDefinitionKey
                        + "（未找到该 key 的模型与部署版本）";
            return AjaxResult.error(hint);
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(def.getId());
        if (bpmnModel == null || bpmnModel.getMainProcess() == null) {
            return AjaxResult.error("BpmnModel 解析失败：" + processDefinitionKey);
        }
        List<Map<String, String>> userTasks = new ArrayList<>();
        bpmnModel.getMainProcess().findFlowElementsOfType(UserTask.class)
                .forEach(ut -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("id", ut.getId());
                    m.put("name", ut.getName() == null ? ut.getId() : ut.getName());
                    userTasks.add(m);
                });
        return AjaxResult.success(userTasks);
    }

    /**
     * @return AjaxResult 含定义版本列表、全部定义KEY与模型 BPMN 匹配结果的诊断数据
     * @description 汇总流程定义的所有版本、全部定义KEY与模型 BPMN 里的 process id，用于排查流程定义不存在的问题
     * @params [processDefinitionKey] 待排查的流程定义KEY
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "诊断流程定义是否存在", description = "调试用：列出 act_re_procdef 里所有匹配 key 的版本")
    @PreAuthorize("@ss.hasPermi('workflow:process:migrate')")
    @GetMapping("/debug/definition/{processDefinitionKey}")
    public AjaxResult debugDefinition(@PathVariable String processDefinitionKey) {
        Map<String, Object> result = new HashMap<>();
        result.put("queryKey", processDefinitionKey);
        // 1. act_re_procdef 所有版本
        List<ProcessDefinition> allVersions = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .orderByProcessDefinitionVersion().desc()
                .list();
        List<Map<String, Object>> defs = new ArrayList<>();
        for (ProcessDefinition d : allVersions) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", d.getId());
            m.put("key", d.getKey());
            m.put("name", d.getName());
            m.put("version", d.getVersion());
            m.put("deploymentId", d.getDeploymentId());
            m.put("suspended", d.isSuspended());
            defs.add(m);
        }
        result.put("processDefinitions", defs);
        result.put("processDefinitionCount", defs.size());
        // 2. 模糊匹配（避免 key 拼写错误看不出来）
        List<ProcessDefinition> fuzzy = repositoryService.createProcessDefinitionQuery()
                .list();
        List<String> allKeys = fuzzy.stream().map(ProcessDefinition::getKey).distinct().sorted()
                .collect(Collectors.toList());
        result.put("allProcessDefinitionKeys", allKeys);
        // 3. modelQuery 查
        long modelCount = repositoryService.createModelQuery()
                .modelKey(processDefinitionKey)
                .latestVersion()
                .count();
        result.put("hasModelLatest", modelCount > 0);
        // 4. 对比 model 表里的 bpmn xml 中 process 的 id（modelKey 跟 process id 可能不同）
        org.flowable.engine.repository.Model m = repositoryService.createModelQuery()
                .modelKey(processDefinitionKey)
                .latestVersion()
                .singleResult();
        if (m != null) {
            try {
                byte[] xml = repositoryService.getModelEditorSource(m.getId());
                if (xml != null) {
                    String bpmnXml = new String(xml, java.nio.charset.StandardCharsets.UTF_8);
                    // 简单匹配：bpmn:process id="xxx"
                    java.util.regex.Matcher matcher = java.util.regex.Pattern
                            .compile("bpmn:process[^>]*id=\"([^\"]+)\"")
                            .matcher(bpmnXml);
                    if (matcher.find()) {
                        result.put("bpmnProcessId", matcher.group(1));
                    }
                    // 匹配所有 process id
                    java.util.regex.Matcher m2 = java.util.regex.Pattern
                            .compile("id=\"([^\"]+)\"")
                            .matcher(bpmnXml);
                    List<String> allProcessIds = new ArrayList<>();
                    while (m2.find()) {
                        allProcessIds.add(m2.group(1));
                    }
                    result.put("bpmnAllIdsSample", allProcessIds.subList(0, Math.min(5, allProcessIds.size())));
                }
            } catch (Exception e) {
                result.put("bpmnParseError", e.getMessage());
            }
        }
        return AjaxResult.success(result);
    }

    /**
     * @return AjaxResult 当前节点 key 与名称、流程定义信息及按结束时间排序的已完成用户任务 key 列表
     * @description 查询旧流程实例当前活动任务与已完成的用户任务节点，供镜像推进使用
     * @params [procInsId] 旧流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "取旧流程当前 taskDefinitionKey", description = "供前端做镜像推进点的智能推荐")
    @PreAuthorize("@ss.hasPermi('workflow:process:migrate')")
    @GetMapping("/currentTask/{procInsId}")
    public AjaxResult currentTask(@PathVariable String procInsId) {
        if (procInsId == null || procInsId.isEmpty()) {
            return AjaxResult.error("procInsId 不能为空");
        }
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procInsId).list();
        HistoricProcessInstance hi = historyService
                .createHistoricProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        if (hi == null) {
            return AjaxResult.error("流程实例不存在：" + procInsId);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("procDefKey", hi.getProcessDefinitionKey());
        result.put("procDefName", hi.getProcessDefinitionName());
        result.put("startUserId", hi.getStartUserId());

        // 旧流程已完成的 userTask 节点（按真实执行顺序），
        // 供前端展示"旧流程已走过"标记；也是后端"自动镜像"的依据
        List<String> finishedUserTaskKeys = new ArrayList<>();
        List<org.flowable.engine.history.HistoricActivityInstance> acts =
                historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(procInsId)
                        .activityType("userTask")
                        .finished()
                        .orderByHistoricActivityInstanceEndTime().asc()
                        .list();
        if (acts != null) {
            for (org.flowable.engine.history.HistoricActivityInstance a : acts) {
                if (a != null && a.getActivityId() != null) {
                    finishedUserTaskKeys.add(a.getActivityId());
                }
            }
        }
        result.put("finishedUserTaskKeys", finishedUserTaskKeys);

        if (tasks == null || tasks.isEmpty()) {
            // 可能已结束或没活动 task
            result.put("currentTaskKey", null);
            result.put("currentTaskName", null);
            return AjaxResult.success(result);
        }
        Task t = tasks.get(0);
        result.put("currentTaskKey", t.getTaskDefinitionKey());
        result.put("currentTaskName", t.getName());
        return AjaxResult.success(result);
    }

    /**
     * @return ProcessDefinition 匹配到的最新流程定义，两种 KEY 都查不到时返回 null
     * @description 先按原 KEY 查最新流程定义，查不到时补 .bpmn 后缀再查一次，兼容 modelKey 与部署 KEY 不一致
     * @params [processDefinitionKey] 流程定义KEY
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private ProcessDefinition findLatestDef(String processDefinitionKey) {
        ProcessDefinition def = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .latestVersion()
                .singleResult();
        if (def != null) return def;
        // 项目 WfModelSaveXmlService.save() 会把 process id 改成 modelKey + ".bpmn"
        if (!processDefinitionKey.endsWith(".bpmn")) {
            def = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(processDefinitionKey + ".bpmn")
                    .latestVersion()
                    .singleResult();
        }
        return def;
    }

    /**
     * @return WfProcessMigratePlan 迁移计划对象，未提供推进步骤时步骤列表为空
     * @description 把接口请求参数转换成 Service 可执行的迁移计划，跳过空推进步骤并把业务回调置空
     * @params [bo] 请求体参数，为空时抛出 IllegalArgumentException
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private WfProcessMigratePlan toPlan(WfProcessMigrateBo bo) {
        if (bo == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        WfProcessMigratePlan.WfProcessMigratePlanBuilder builder = WfProcessMigratePlan.builder()
                .oldProcInsId(bo.getOldProcInsId())
                .newProcessDefinitionKey(bo.getNewProcessDefinitionKey())
                .startVariables(bo.getStartVariables() == null ? new HashMap<>() : new HashMap<>(bo.getStartVariables()))
                .copyComments(bo.getCopyComments() == null ? Boolean.TRUE : bo.getCopyComments())
                .copyAttachments(bo.getCopyAttachments() == null ? Boolean.TRUE : bo.getCopyAttachments())
                .reason(bo.getReason());

        // 镜像推进步骤
        if (bo.getAdvanceSteps() != null && !bo.getAdvanceSteps().isEmpty()) {
            List<WfProcessMigratePlan.AdvanceStep> steps = new ArrayList<>(bo.getAdvanceSteps().size());
            for (WfProcessMigrateBo.AdvanceStepBo s : bo.getAdvanceSteps()) {
                if (s == null || s.getTaskDefinitionKey() == null) {
                    continue;
                }
                WfProcessMigratePlan.AdvanceStep step = new WfProcessMigratePlan.AdvanceStep(
                        s.getTaskDefinitionKey(),
                        s.getVariables() == null ? new HashMap<>() : new HashMap<>(s.getVariables()));
                step.setCurrentUserId(s.getCurrentUserId());
                steps.add(step);
            }
            builder.advanceSteps(steps);
        }

        // HTTP 入口不暴露 businessCallback：调用方若需业务回调，应调 Service API（注入 lambda）
        builder.businessCallback(null);
        return builder.build();
    }
}
