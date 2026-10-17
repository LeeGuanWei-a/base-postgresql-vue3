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

    /**
     * @return WfProcessMigrateResult 替换结果，含新流程实例id、复制的意见与附件条数、推进步数
     * @description 在同一事务内用新流程定义替换运行中的旧流程实例：校验参数与新定义后启动新流程，
     * 镜像推进到与旧流程位置等价的节点，复制历史意见与附件并写替换说明，业务回调后废除旧流程
     * @params [plan] 替换计划，含新旧流程标识、推进步骤、启动变量与业务表更新回调
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
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
     * @return List<WfProcessMigratePlan.AdvanceStep> 按旧流程实际执行顺序生成的推进步骤
     * @description 取旧流程已完成的用户任务历史，按活动结束时间升序生成推进步骤，跳过被回退或终止取消的节点，并沿用节点原处理人
     * @params [oldProcInsId] 旧流程实例id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @return List<WfProcessMigratePlan.AdvanceStep> 按新流程走向排好序的推进步骤
     * @description 先解析新流程图的节点走向序号，再把勾选的推进步骤按序号升序排列，解析失败时保留传入顺序，避免先推后面的节点被静默跳过
     * @params [newDef, manualSteps] newDef 新流程定义；manualSteps 用户勾选的推进步骤
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @return Map<String, Integer> 节点key到走向序号的映射，解析不出时返回空 Map
     * @description 从开始事件沿顺序流做广度优先遍历，按遍历先后给每个用户任务节点编号
     * @params [bm] 新流程的 BPMN 模型
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @description 在新流程上按节点key找任务并以系统身份完成它，会签节点循环推进到该节点没有活动任务为止，单节点最多推 200 次，找不到任务时静默跳过
     * @params [newProcInsId, step, startUserIdFallback] newProcInsId 新流程实例id；step 本次要推进的步骤；startUserIdFallback 解析不出操作人时使用的兜底用户
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @return String 本次推进使用的操作人
     * @description 按步骤指定处理人、任务当前办理人、任务第一位候选人、旧流程发起人的优先级依次解析出操作人
     * @params [step, task, startUserIdFallback] step 推进步骤；task 新流程上的任务；startUserIdFallback 兜底用户
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @return int 成功复制的意见条数
     * @description 读出旧流程的全部历史审批意见，逐条改写流程实例id并标注来源后写入新流程，单条失败只记日志跳过
     * @params [oldProcInsId, newProcInsId] oldProcInsId 旧流程实例id；newProcInsId 新流程实例id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @return int 成功复制的附件条数
     * @description 读出旧流程的全部附件记录，逐条改写流程实例id后重新入库到新流程，单条失败只记日志跳过
     * @params [oldProcInsId, newProcInsId] oldProcInsId 旧流程实例id；newProcInsId 新流程实例id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
