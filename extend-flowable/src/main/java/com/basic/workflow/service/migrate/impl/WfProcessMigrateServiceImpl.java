package com.basic.workflow.service.migrate.impl;

import com.basic.common.core.domain.AjaxResult;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.system.domain.SysFiles;
import com.basic.system.service.ISysFilesService;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.WfCommentInfo;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.vo.StartProcessInstanceVo;
import com.basic.workflow.service.IWfCommentInfoService;
import com.basic.workflow.service.IWfExtendProcinstService;
import com.basic.workflow.service.IWfTaskService;
import com.basic.workflow.service.migrate.IWfProcessMigrateService;
import com.basic.workflow.service.migrate.WfProcessMigrateException;
import com.basic.workflow.service.migrate.WfProcessMigratePlan;
import com.basic.workflow.service.migrate.WfProcessMigrateResult;
import com.basic.workflow.service.wfModel.start.WfModelStartService;
import com.basic.workflow.service.wfProcessTask.auth.WfTaskAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 流程替换服务实现。
 *
 * <p>实现思路（与 process/BusinessTask.changePayOld 模式同源，但抽到通用层）：</p>
 * <ol>
 *   <li>读旧流程的启动人 / businessName 等元信息</li>
 *   <li>调 {@link WfModelStartService#startFormFlow} 启动新流程</li>
 *   <li>按 advanceSteps 镜像推进</li>
 *   <li>复制历史意见（按 procInsId 重写）</li>
 *   <li>复制附件（按 procInsId 重新插入）</li>
 *   <li>businessCallback</li>
 *   <li>abolish 旧流程</li>
 * </ol>
 *
 * <p><b>不要绑死业务对象</b>：通过 plan.businessCallback lambda 让调用方注入业务表更新逻辑，
 * 因此本服务在 base-postgresql-vue3 不依赖任何具体业务实体。</p>
 *
 * @author integration-supplement
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WfProcessMigrateServiceImpl implements IWfProcessMigrateService {

    private final HistoryService historyService;
    private final RepositoryService repositoryService;
    private final TaskService taskService;
    private final WfModelStartService wfModelStartService;
    private final IWfTaskService wfTaskService;
    private final IWfCommentInfoService wfCommentInfoService;
    private final IWfExtendProcinstService wfExtendProcinstService;
    private final ISysFilesService sysFilesService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WfProcessMigrateResult replaceProcessInstance(WfProcessMigratePlan plan) {
        // ============ 第 0 步：基础校验 ============
        String err = validate(plan);
        if (err != null) {
            throw new WfProcessMigrateException("流程替换参数校验失败：" + err);
        }

        String oldProcInsId = plan.getOldProcInsId();
        log.info("[process-migrate] 开始替换流程 oldProcInsId={} newKey={}",
                oldProcInsId, plan.getNewProcessDefinitionKey());

        // ============ 第 1 步：读旧流程 ============
        HistoricProcessInstance oldInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(oldProcInsId)
                .singleResult();
        if (oldInstance == null) {
            throw new WfProcessMigrateException("旧流程实例不存在：" + oldProcInsId);
        }
        if (oldInstance.getEndTime() != null) {
            throw new WfProcessMigrateException("旧流程已结束，不能替换：" + oldProcInsId
                    + " endTime=" + oldInstance.getEndTime());
        }
        String oldProcessDefKey = oldInstance.getProcessDefinitionKey();
        String startUserId = oldInstance.getStartUserId();
        if (StringUtils.isBlank(startUserId)) {
            throw new WfProcessMigrateException("旧流程没有启动人，无法替换：" + oldProcInsId);
        }

        // 旧流程当前 task（用于回退/记录 oldTaskKey）
        List<Task> oldTasks = taskService.createTaskQuery().processInstanceId(oldProcInsId).list();
        String oldTaskKey = oldTasks.isEmpty() ? null : oldTasks.get(0).getTaskDefinitionKey();
        log.info("[process-migrate] 旧流程元信息 oldProcDefKey={} startUserId={} oldTaskKey={}",
                oldProcessDefKey, startUserId, oldTaskKey);

        // 取旧流程的 processName（流程详情页显示用）
        ExtendProcinst oldExtend = wfExtendProcinstService.selectProcInstanId(oldProcInsId);
        String businessName = oldExtend == null ? null : oldExtend.getProcessName();

        // ============ 第 2 步：校验新流程定义 ============
        // 项目 WfModelSaveXmlService.save() 会给 process id 拼 .bpmn 后缀，所以
        // act_re_procdef.key_ 实际是 "cs.bpmn"。先按原 key 查，没有再补 .bpmn。
        ProcessDefinition newDef = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(plan.getNewProcessDefinitionKey())
                .latestVersion()
                .singleResult();
        if (newDef == null && !plan.getNewProcessDefinitionKey().endsWith(".bpmn")) {
            newDef = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(plan.getNewProcessDefinitionKey() + ".bpmn")
                    .latestVersion()
                    .singleResult();
        }
        if (newDef == null) {
            throw new WfProcessMigrateException("新流程定义不存在：" + plan.getNewProcessDefinitionKey());
        }
        if (newDef.isSuspended()) {
            throw new WfProcessMigrateException("新流程定义已挂起：" + plan.getNewProcessDefinitionKey());
        }

        // ============ 第 3 步：启动新流程 ============
        StartProcessInstanceVo startVo = new StartProcessInstanceVo();
        Map<String, Object> startVariables = plan.getStartVariables() == null
                ? new HashMap<>() : new HashMap<>(plan.getStartVariables());
        startVo.setVariables(startVariables);
        startVo.setProcessDefinitionKey(plan.getNewProcessDefinitionKey());
        // modelKey = processDefinitionKey，startFormFlow 内部会拼 .bpmn
        startVo.setModelId(plan.getNewProcessDefinitionKey());
        startVo.setCurrentUserCode(startUserId);
        startVo.setCurrentUserName(startUserId);
        startVo.setCreator(startUserId);
        // 复制旧流程的 businessName，作为新流程的 formName
        startVo.setFormName(StringUtils.isNotBlank(businessName)
                ? businessName : "流程替换：迁移自 " + oldProcInsId);
        // 沿用旧流程的 modelKey（如果有）—— appSn 字段为业务系统标识
        if (oldExtend != null && StringUtils.isNotBlank(oldExtend.getModelKey())) {
            startVo.setAppSn(oldExtend.getModelKey());
        }

        AjaxResult startResult = wfModelStartService.startFormFlow(startVo);
        // AjaxResult 没有 isSuccess 静态方法，手工判断
        Object codeObj = startResult.get("code");
        boolean startOk = codeObj != null && Integer.valueOf(200).equals(codeObj);
        if (!startOk || startResult.get("data") == null
                || Objects.isNull(startResult.get("data").toString())) {
            String msg = startResult.get("msg") == null ? "" : startResult.get("msg").toString();
            throw new WfProcessMigrateException("启动新流程失败：" + msg);
        }
        String newProcInsId = startResult.get("data").toString();
        log.info("[process-migrate] 新流程已启动 newProcInsId={}", newProcInsId);

        // ============ 第 4 步：镜像推进 ============
        // 两种模式：
        //   A) 未指定 advanceSteps（推荐）：按「旧流程已完成的 userTask 历史」自动镜像，
        //      新流程会被推到与旧流程完全等价的位置（会签按旧流程实际完成的次数推）。
        //   B) 指定了 advanceSteps：按用户勾选的节点推进，但必须按「新流程图的走向顺序」
        //      排序后再推 —— 否则先推后面的节点会因"还没走到"而查不到 task 被跳过。
        //      注意：bpmn 文件里元素的声明顺序不等于流程走向顺序，必须用 sequenceFlow 算。
        List<WfProcessMigratePlan.AdvanceStep> steps;
        if (plan.getAdvanceSteps() == null || plan.getAdvanceSteps().isEmpty()) {
            steps = deriveStepsFromOldFlow(oldProcInsId);
            log.info("[process-migrate] 未指定 advanceSteps，自动镜像旧流程已完成的 userTask，共 {} 步", steps.size());
        } else {
            steps = sortStepsByNewFlowOrder(newDef, plan.getAdvanceSteps());
            log.info("[process-migrate] 使用指定的 advanceSteps，按新流程走向排序后共 {} 步", steps.size());
        }

        int advancedStepCount = 0;
        for (WfProcessMigratePlan.AdvanceStep step : steps) {
            if (step == null || StringUtils.isBlank(step.getTaskDefinitionKey())) {
                continue;
            }
            try {
                completeNewTaskIfPresent(newProcInsId, step, startUserId);
                advancedStepCount++;
            } catch (Exception e) {
                throw new WfProcessMigrateException(
                        "镜像推进失败 step.taskDefinitionKey=" + step.getTaskDefinitionKey()
                                + " 原因：" + e.getMessage(), e);
            }
        }
        log.info("[process-migrate] 镜像推进完成 advancedStepCount={}", advancedStepCount);

        // ============ 第 5 步：复制历史意见 ============
        int copyCommentCount = 0;
        if (Boolean.TRUE.equals(plan.getCopyComments())) {
            copyCommentCount = copyComments(oldProcInsId, newProcInsId);
        }
        log.info("[process-migrate] 历史意见复制完成 copyCommentCount={}", copyCommentCount);

        // ============ 第 6 步：复制附件 ============
        int copyAttachmentCount = 0;
        if (Boolean.TRUE.equals(plan.getCopyAttachments())) {
            copyAttachmentCount = copyAttachments(oldProcInsId, newProcInsId);
        }
        log.info("[process-migrate] 附件复制完成 copyAttachmentCount={}", copyAttachmentCount);

        // ============ 第 7 步：写"流程替换"意见到新流程 ============
        if (StringUtils.isNotBlank(plan.getReason())) {
            try {
                WfCommentInfo replaceComment = new WfCommentInfo(
                        FlowComment.TJ.getType(),
                        startUserId,
                        newProcInsId,
                        "流程替换：" + plan.getReason()
                                + "（旧流程 " + oldProcInsId + " 已废除）",
                        null,
                        null);
                wfCommentInfoService.addCommentInfo(replaceComment);
            } catch (Exception e) {
                log.warn("[process-migrate] 写『流程替换』意见失败，不阻断主流程 newProcInsId={} err={}",
                        newProcInsId, e.getMessage());
            }
        }

        // ============ 第 8 步：业务回调（更新业务表 procInsId） ============
        if (plan.getBusinessCallback() != null) {
            try {
                plan.getBusinessCallback().accept(oldProcInsId, newProcInsId);
                log.info("[process-migrate] businessCallback 已执行 oldProcInsId={} newProcInsId={}",
                        oldProcInsId, newProcInsId);
            } catch (Exception e) {
                throw new WfProcessMigrateException(
                        "businessCallback 执行失败（业务表更新异常）：" + e.getMessage(), e);
            }
        }

        // ============ 第 9 步：废除旧流程 ============
        try {
            WfTaskBo abolishBo = new WfTaskBo();
            abolishBo.setProcInsId(oldProcInsId);
            // WfDeleteProcessService.abolishProcess 只看 procInsIdList 字段，
            // 不看 procInsId，所以必须把 oldProcInsId 放进 list 才会被真正废除
            List<String> procInsIdList = new ArrayList<>();
            procInsIdList.add(oldProcInsId);
            abolishBo.setProcInsIdList(procInsIdList);
            wfTaskService.abolishProcess(abolishBo);
            log.info("[process-migrate] 旧流程已废除 oldProcInsId={}", oldProcInsId);
        } catch (Exception e) {
            throw new WfProcessMigrateException("废除旧流程失败：" + e.getMessage(), e);
        }

        // ============ 收尾：构造返回 ============
        WfProcessMigrateResult result = WfProcessMigrateResult.builder()
                .oldProcInsId(oldProcInsId)
                .oldProcessDefinitionKey(oldProcessDefKey)
                .newProcInsId(newProcInsId)
                .newProcessDefinitionKey(plan.getNewProcessDefinitionKey())
                .copyCommentCount(copyCommentCount)
                .copyAttachmentCount(copyAttachmentCount)
                .advancedStepCount(advancedStepCount)
                .build();
        log.info("[process-migrate] 替换完成 result={}", result);
        return result;
    }

    /**
     * 自动镜像：按「旧流程已完成的 userTask 历史」生成推进步骤。
     *
     * <p>这是最忠实的镜像方式——旧流程完成了哪些节点、每个节点完成了几次（会签会有多次），
     * 新流程就按同样顺序推同样次数，最终落点与旧流程当前位置等价，不会过冲也不会欠推。</p>
     *
     * <p>顺序按 act_hi_actinst 的结束时间升序（即真实执行顺序），不做新流程图排序——
     * 因为迁移的典型场景就是流程图结构变了，旧流程的实际执行顺序才是要复刻的东西。</p>
     */
    private List<WfProcessMigratePlan.AdvanceStep> deriveStepsFromOldFlow(String oldProcInsId) {
        List<WfProcessMigratePlan.AdvanceStep> steps = new ArrayList<>();
        List<HistoricActivityInstance> acts = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(oldProcInsId)
                .activityType("userTask")
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();
        if (acts == null || acts.isEmpty()) {
            return steps;
        }
        for (HistoricActivityInstance act : acts) {
            if (act == null || StringUtils.isBlank(act.getActivityId())) {
                continue;
            }
            // 只认"正常完成"的节点：被回退 / 终止而取消的节点 deleteReason 不是 completed，
            // 如果把它们也算进来会导致新流程被推过头
            String deleteReason = act.getDeleteReason();
            if (StringUtils.isNotBlank(deleteReason) && !"completed".equalsIgnoreCase(deleteReason)) {
                log.info("[process-migrate] 自动镜像忽略非正常完成节点 activityId={} deleteReason={}",
                        act.getActivityId(), deleteReason);
                continue;
            }
            WfProcessMigratePlan.AdvanceStep step =
                    new WfProcessMigratePlan.AdvanceStep(act.getActivityId());
            // 用旧流程该节点的实际处理人，保证新流程历史里 assignee 与旧流程一致
            if (StringUtils.isNotBlank(act.getAssignee())) {
                step.setCurrentUserId(act.getAssignee());
            }
            steps.add(step);
        }
        return steps;
    }

    /**
     * 把用户勾选的 advanceSteps 按「新流程图的走向顺序」排序。
     *
     * <p>bpmn 文件里元素的声明顺序 ≠ 流程走向顺序（bpmn-js 保存时的书写顺序是画图顺序），
     * 所以必须用 sequenceFlow 从 startEvent 做 BFS 算真实走向。否则先推靠后的节点会因
     * "新流程还没走到那里"而 taskQuery 查不到，被静默跳过。</p>
     */
    private List<WfProcessMigratePlan.AdvanceStep> sortStepsByNewFlowOrder(
            ProcessDefinition newDef, List<WfProcessMigratePlan.AdvanceStep> manualSteps) {
        Map<String, Integer> orderIndex = new HashMap<>();
        if (newDef != null) {
            try {
                BpmnModel bm = repositoryService.getBpmnModel(newDef.getId());
                orderIndex = calcUserTaskFlowOrder(bm);
            } catch (Exception e) {
                log.warn("[process-migrate] 解析新流程图走向失败，将按传入顺序推进 err={}", e.getMessage());
            }
        }
        List<WfProcessMigratePlan.AdvanceStep> sorted = new ArrayList<>(manualSteps);
        final Map<String, Integer> idx = orderIndex;
        sorted.sort((a, b) -> {
            Integer ai = idx.get(a.getTaskDefinitionKey());
            Integer bi = idx.get(b.getTaskDefinitionKey());
            if (ai == null) {
                ai = Integer.MAX_VALUE;
            }
            if (bi == null) {
                bi = Integer.MAX_VALUE;
            }
            return ai.compareTo(bi);
        });
        return sorted;
    }

    /**
     * 从 startEvent 沿 sequenceFlow 做 BFS，算出每个 userTask 在流程走向上的先后序号。
     *
     * @return taskDefinitionKey -> 序号（越小越靠前）；解析不出时返回空 Map
     */
    private Map<String, Integer> calcUserTaskFlowOrder(BpmnModel bm) {
        Map<String, Integer> order = new HashMap<>();
        if (bm == null || bm.getMainProcess() == null) {
            return order;
        }
        Process process = bm.getMainProcess();
        Map<String, FlowElement> elements = new HashMap<>();
        Map<String, List<String>> outgoing = new HashMap<>();
        String startId = null;
        for (FlowElement fe : process.getFlowElements()) {
            elements.put(fe.getId(), fe);
            if (fe instanceof SequenceFlow) {
                SequenceFlow sf = (SequenceFlow) fe;
                outgoing.computeIfAbsent(sf.getSourceRef(), k -> new ArrayList<>())
                        .add(sf.getTargetRef());
            } else if (fe instanceof StartEvent && startId == null) {
                startId = fe.getId();
            }
        }
        if (startId == null) {
            return order;
        }
        Set<String> visited = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        queue.add(startId);
        int idx = 0;
        while (!queue.isEmpty()) {
            String id = queue.poll();
            if (!visited.add(id)) {
                continue;
            }
            FlowElement fe = elements.get(id);
            if (fe instanceof UserTask) {
                order.put(id, idx++);
            }
            List<String> next = outgoing.get(id);
            if (next != null) {
                for (String n : next) {
                    if (!visited.contains(n)) {
                        queue.add(n);
                    }
                }
            }
        }
        return order;
    }

    /**
     * 镜像推进：在新流程上按 taskDefinitionKey 找 task，complete 它。
     * 找不到时（已推过 / 流程设计无此 key）静默跳过。
     *
     * <p>对会签节点（multiInstance）会循环推进直到该 taskDefinitionKey 没有活动 task 为止：
     * 并行会签一次 complete 全部完成；串行会签需要逐个 complete。</p>
     *
     * @param startUserIdFallback 解析不出操作人时的兜底（一般传旧流程发起人）
     * @throws Exception complete() 内部可能抛业务异常
     */
    private void completeNewTaskIfPresent(String newProcInsId,
                                          WfProcessMigratePlan.AdvanceStep step,
                                          String startUserIdFallback) throws Exception {
        // 防御性循环：会签可能需要 complete 多次（串行会签 N 次 / 并行会签 1 次）
        // 安全网：单 taskDefinitionKey 最多推 200 次，避免死循环
        int maxLoop = 200;
        int loop = 0;
        while (loop < maxLoop) {
            Task newTask = taskService.createTaskQuery()
                    .processInstanceId(newProcInsId)
                    .taskDefinitionKey(step.getTaskDefinitionKey())
                    .singleResult();
            if (newTask == null) {
                if (loop > 0) {
                    log.info("[process-migrate] 镜像推进完成 taskDefinitionKey={} 共推 {} 次",
                            step.getTaskDefinitionKey(), loop);
                } else {
                    log.info("[process-migrate] 镜像推进跳过：新流程无 taskDefinitionKey={}（可能已推过）",
                            step.getTaskDefinitionKey());
                }
                return;
            }
            WfTaskBo bo = new WfTaskBo();
            bo.setProcInsId(newProcInsId);
            bo.setTaskId(newTask.getId());
            bo.setCurrentUserId(resolveOperator(step, newTask, startUserIdFallback));
            bo.setComment("流程替换：镜像推进");
            bo.setVariables(step.getVariables() == null ? new HashMap<>() : new HashMap<>(step.getVariables()));
            // 迁移是"代表他人推进流程"的系统行为：当前登录用户通常不是该节点处理人/候选人，
            // 因此必须以系统身份调用，跳过 checkOperateAuth
            WfTaskAuthService.callAsSystem(() -> {
                wfTaskService.complete(bo);
                return null;
            });
            loop++;
        }
        log.warn("[process-migrate] 镜像推进达到 {} 次上限，强制停止 taskDefinitionKey={}（请检查流程定义是否异常）",
                maxLoop, step.getTaskDefinitionKey());
    }

    /**
     * 解析本次推进的"操作人"。
     *
     * <p>优先级：步骤上指定的处理人 &gt; 任务当前 assignee &gt; 任务的第一位候选人 &gt; 流程发起人。
     * 候选人兜底是必需的：迁移推进时节点往往还没被签收（assignee 为空），
     * 若不解析出操作人，写入的历史记录里办理人会是空的。</p>
     */
    private String resolveOperator(WfProcessMigratePlan.AdvanceStep step, Task task,
                                   String startUserIdFallback) {
        if (step != null && StringUtils.isNotBlank(step.getCurrentUserId())) {
            return step.getCurrentUserId();
        }
        if (task != null && StringUtils.isNotBlank(task.getAssignee())) {
            return task.getAssignee();
        }
        // 从候选人里取第一位（未签收的候选任务）
        if (task != null) {
            try {
                List<IdentityLink> links = taskService.getIdentityLinksForTask(task.getId());
                if (links != null) {
                    for (IdentityLink link : links) {
                        if ("candidate".equals(link.getType()) && StringUtils.isNotBlank(link.getUserId())) {
                            return link.getUserId();
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("[process-migrate] 解析候选操作人失败 taskId={} err={}",
                        task.getId(), e.getMessage());
            }
        }
        return startUserIdFallback;
    }

    /**
     * 复制旧流程的所有历史意见到新流程。
     * 每条意见重写 procInsId，保留 userId / type / message / time。
     */
    private int copyComments(String oldProcInsId, String newProcInsId) {
        List<WfCommentInfo> hisComments = wfCommentInfoService.selectProcessCommentForProcInsId(oldProcInsId);
        if (hisComments == null || hisComments.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (WfCommentInfo his : hisComments) {
            WfCommentInfo copy = new WfCommentInfo();
            copy.setType(his.getType());
            copy.setUserId(his.getUserId());
            copy.setUserName(his.getUserName());
            copy.setTime(his.getTime());
            copy.setTaskId(his.getTaskId());
            copy.setActivityId(his.getActivityId());
            copy.setActivityName(his.getActivityName());
            copy.setProcInsId(newProcInsId);
            copy.setAction(his.getAction());
            copy.setMessage("[从旧流程 " + oldProcInsId + " 复制] " + his.getMessage());
            copy.setEndTime(his.getEndTime());
            copy.setDuration(his.getDuration());
            copy.setActivityType(his.getActivityType());
            copy.setDelFlag("0");
            try {
                wfCommentInfoService.addCommentInfo(copy);
                count++;
            } catch (Exception e) {
                log.warn("[process-migrate] 复制单条意见失败，跳过 procInsId={} message={} err={}",
                        newProcInsId, his.getMessage(), e.getMessage());
            }
        }
        return count;
    }

    /**
     * 复制旧流程的所有附件到新流程。
     * 重写 processInstanceId 字段（SysFiles 实际字段名）。
     */
    private int copyAttachments(String oldProcInsId, String newProcInsId) {
        List<SysFiles> hisFiles = sysFilesService.getListByProcessInstanceId(oldProcInsId);
        if (hisFiles == null || hisFiles.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (SysFiles his : hisFiles) {
            try {
                SysFiles copy = new SysFiles();
                copy.setName(his.getName());
                copy.setAbsolutePath(his.getAbsolutePath());
                copy.setRelativePath(his.getRelativePath());
                copy.setSize(his.getSize());
                copy.setIdentity(his.getIdentity());
                copy.setIsDel("0");
                copy.setTaskId(his.getTaskId());
                copy.setProcessInstanceId(newProcInsId);
                copy.setUserId(his.getUserId());
                copy.setNickName(his.getNickName());
                copy.setActivityId(his.getActivityId());
                copy.setUrl(his.getUrl());
                copy.setUploadType(his.getUploadType());
                copy.setRemark(his.getRemark());
                sysFilesService.save(copy);
                count++;
            } catch (Exception e) {
                log.warn("[process-migrate] 复制单条附件失败，跳过 oldFileId={} url={} err={}",
                        his.getId(), his.getUrl(), e.getMessage());
            }
        }
        return count;
    }
}
