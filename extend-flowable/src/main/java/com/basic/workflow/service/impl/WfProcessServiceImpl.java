package com.basic.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.utils.SecurityUtils;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.domain.WfCommentInfo;
import com.basic.workflow.domain.WfDeployForm;
import com.basic.workflow.domain.vo.*;
import com.basic.workflow.service.IWfProcessService;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoServiceBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description 工作流流程管理业务实现，负责流程的发起、查询、详情、撤销与删除等实例操作
 * @author yanbin
 * @date 2023/11/3 10:39
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WfProcessServiceImpl extends WfProcessInfoServiceBase implements IWfProcessService {
    /**
     * @return List<WfTaskVo> 我发起且符合查询条件的流程列表，无匹配时为空列表
     * @description 补齐当前登录用户作为发起人条件后，分页查询我发起的流程
     * @params [processQuery, pageQuery] 查询条件，含流程名、分类、状态与发起人；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectPageOwnProcessList(
            ProcessQuery processQuery, PageQuery pageQuery) {
        this.fillCurrentUserForOwn(processQuery);
        return wfOwnProcessService.selectPageOwnProcessList(processQuery, pageQuery);
    }

    /**
     * @return List<WfTaskVo> 我发起且符合查询条件的流程列表，无匹配时为空列表
     * @description 补齐当前登录用户作为发起人条件后，查询全部我发起的流程，不分页
     * @params [processQuery] 查询条件，含流程名、分类、状态与发起人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectOwnProcessList(ProcessQuery processQuery) {
        this.fillCurrentUserForOwn(processQuery);
        return wfOwnProcessService.selectOwnProcessList(processQuery);
    }

    /**
     * @description 发起人条件为空时用当前登录用户补齐，避免「我的流程」查询缺少发起人过滤而返回全部流程实例
     * @params [processQuery] 我的流程查询条件，其中为空的发起人会被回填为当前登录用户
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void fillCurrentUserForOwn(ProcessQuery processQuery) {
        String startUserId = processQuery.getStartUserId();
        if (startUserId == null || startUserId.isEmpty()) {
            processQuery.setStartUserId(String.valueOf(SecurityUtils.getUserId()));
        }
    }

    /**
     * @return List<WfTaskVo> 待办任务列表，无匹配时为空列表
     * @description 补齐当前登录用户及其角色作为查询条件后，分页查询我的待办任务
     * @params [processQuery, pageQuery] 查询条件，含流程名、分类、状态；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectPageTodoProcessList(
            ProcessQuery processQuery, PageQuery pageQuery) {
        this.fillCurrentUserForTodo(processQuery);
        return wfToDoTaskService.selectPageTodoProcessList(processQuery, pageQuery);
    }

    /**
     * @return List<WfTaskVo> 待办任务列表，无匹配时为空列表
     * @description 补齐当前登录用户及其角色作为查询条件后，查询我的全部待办任务，不分页
     * @params [processQuery] 查询条件，含流程名、分类、状态
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectTodoProcessList(ProcessQuery processQuery) {
        this.fillCurrentUserForTodo(processQuery);
        return wfToDoTaskService.selectTodoProcessList(processQuery);
    }

    /**
     * @description 用户标识为空时填入当前登录用户，角色集合为空时填入当前登录用户的角色，避免待办查询缺少用户过滤而返回他人任务
     * @params [processQuery] 待办查询条件，其中为空的用户标识与角色集合会被回填
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void fillCurrentUserForTodo(ProcessQuery processQuery) {
        if (processQuery.getUserId() == null) {
            processQuery.setUserId(String.valueOf(SecurityUtils.getUserId()));
        }
        if (CollUtil.isEmpty(processQuery.getGroupId())) {
            processQuery.setGroupId(SecurityUtils.getLoginUser().getUser().getRoles().stream()
                    .map(r -> String.valueOf(r.getRoleId()))
                    .collect(Collectors.toList()));
        }
    }

    /**
     * @return TableDataInfo<WfTaskVo> 待签收任务分页结果，无匹配时为空集合
     * @description 以当前登录用户及其角色补齐候选人条件后，分页查询待签收任务
     * @params [processQuery, pageQuery] 查询条件，含流程名、分类、状态；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public TableDataInfo<WfTaskVo> selectPageClaimProcessList(
            ProcessQuery processQuery, PageQuery pageQuery) {
        // 待签任务使用 Flowable 原生 TaskQuery，需要 userId/groupId 不能为 null
        // 这里从 SecurityUtils 取当前登录用户信息填充（与 WfAllTaskService 一致）
        if (processQuery.getUserId() == null) {
            processQuery.setUserId(String.valueOf(SecurityUtils.getUserId()));
        }
        if (CollUtil.isEmpty(processQuery.getGroupId())) {
            processQuery.setGroupId(SecurityUtils.getLoginUser().getUser().getRoles().stream()
                    .map(r -> String.valueOf(r.getRoleId()))
                    .collect(Collectors.toList()));
        }
        return wfProcessClaimTaskService.selectPageClaimProcessList(processQuery, pageQuery);
    }

    /**
     * @return List<WfTaskVo> 待签收任务列表，无匹配时为空列表
     * @description 按调用方传入的用户与角色条件查询待签收任务，不分页且不做条件补齐
     * @params [processQuery] 查询条件，需自行带上 userId 与角色列表 groupId
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectClaimProcessList(ProcessQuery processQuery) {
        return wfProcessClaimTaskService.selectClaimProcessList(processQuery);
    }

    /**
     * @return List<WfTaskVo> 我审批过的已办任务列表，无匹配时为空列表
     * @description 按查询条件分页查询我审批过的已办任务
     * @params [processQuery, pageQuery] 查询条件，含流程名、分类、状态与处理人；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectPageFinishedProcessList(
            ProcessQuery processQuery, PageQuery pageQuery) {
        return wfFinishTaskService.selectPageFinishedProcessList(processQuery, pageQuery);
    }

    /**
     * @return List<WfTaskVo> 我审批过的已办任务列表，无匹配时为空列表
     * @description 按查询条件查询我审批过的已办任务，不分页
     * @params [processQuery] 查询条件，含流程名、分类、状态与处理人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectFinishedProcessList(ProcessQuery processQuery) {
        return wfFinishTaskService.selectFinishedProcessList(processQuery);
    }


    /**
     * @return WfDetailVo 流程详情，含 BPMN XML、历史节点、当前任务表单、流程图高亮与审批意见
     * @description 组装流程详情视图，流程实例不存在时抛业务异常
     * @params [procInsId, taskId] 流程实例 ID；当前任务 ID，为空时不返回任务表单与操作权限
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfDetailVo queryProcessDetail(String procInsId, String taskId) throws DocumentException {
        return wfProcessDetailService.queryProcessDetail(procInsId, taskId);
    }

    /**
     * @return WfTaskFormVo 当前任务的节点表单数据，任务不存在时返回 null
     * @description 取当前任务节点的表单数据，模型改过图时按启动时记录的流程定义去查表单
     * @params [deployId, taskId] 部署 ID；任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private WfTaskFormVo currTaskFormData(String deployId, String taskId) {
        return wfProcessDetailService.currTaskFormData(deployId, taskId);
    }

    /**
     * @return List<WfCommentInfo> 批复意见列表，无记录时为空列表
     * @description 查询流程实例的流转记录，并标注每条意见所属的节点类型
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfCommentInfo> getProcessCommentInfo(String procInsId) {
        return wfProcessCommentService.getProcessCommentInfo(procInsId);
    }

    /**
     * @return List<WfTaskVo> 历史流程实例列表，无匹配时为空列表
     * @description 按查询条件查历史流程实例，管理员看全部、普通用户只看自己参与的
     * @params [processQuery] 查询条件，可带 role=admin 走管理员口径
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectHisProcInsProcess(ProcessQuery processQuery) {
        return wfAllProcessOrTaskService.selectHisProcInsProcess(processQuery);
    }

    /**
     * @return WfTaskVo 当前用户可处理的任务信息并回填业务拓展信息；实例已结束时只带回实例 ID
     * @description 按流程实例 ID 取当前用户名下可处理的任务并回填业务拓展信息
     * @params [procInsId, userId] 流程实例 ID；用户标识
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfTaskVo getProcInsIdToTask(String procInsId, String userId) {
        return wfProcessCurrentTaskService.getProcInsIdToTask(procInsId, userId);
    }

    /**
     * @return int 流程实例当前活动节点上的任务条数
     * @description 统计流程实例当前活动节点上挂着的任务数量
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public int getCurrentTaskInfo(String procInsId) {
        return wfProcessCurrentTaskService.getCurrentTaskNum(procInsId);
    }

    /**
     * @return TableDataInfo<WfTaskVo> 全部流程实例分页结果，无匹配时为空集合
     * @description 从运维视角分页查询所有流程实例，包含已结束的实例
     * @params [processQuery, pageQuery] 查询条件，含发起人、流程名、状态；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public TableDataInfo<WfTaskVo> selectProcessPrcInstance(ProcessQuery processQuery, PageQuery pageQuery) {
        return wfAllProcessService.selectProcessPrcInstance(processQuery, pageQuery);
    }

    /**
     * @return List<WfTaskVo> 全部任务列表，无匹配时为空列表
     * @description 从运维视角查询全部任务，分页行为由传入的分页参数控制
     * @params [processQuery, pageQuery] 查询条件，含处理人、流程名、状态；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectProcessAllTask(ProcessQuery processQuery, PageQuery pageQuery) {
        return wfAllTaskService.selectProcessAllTask(processQuery, pageQuery);
    }


    /**
     * @return List<WfTaskFormVo> 该流程实例各节点提交过的历史表单列表，无记录时为空列表
     * @description 查询流程实例在各节点提交过的历史表单
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskFormVo> processFormList(String procInsId) {
        return wfProcessHiFormService.processFormList(procInsId);
    }

    /**
     * @return List<WfProcNodeVo> 已完成节点的历史信息列表，无记录时为空列表
     * @description 查询已完成节点的历史信息，供审批记录时间轴使用
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfProcNodeVo> historyProcNodeList(String procInsId) {
        return wfHistoryProcNodeService.historyProcNodeList(procInsId);
    }

    /**
     * @return WfViewerVo 流程图查看器数据，含已完成、进行中与被驳回元素；实例无历史活动时返回空对象
     * @description 查询流程执行过程，组装流程图高亮所需的数据
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfViewerVo getFlowViewer(String procInsId) {
        return wfProcessFlowViewerService.getFlowViewer(procInsId);
    }

    /**
     * @return List<UserTask> 流程定义里的全部用户任务节点，无节点时为空列表
     * @description 取流程定义里的全部用户任务节点，含多实例节点
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<UserTask> getAllFlowNode(String procInsId) {
        return wfProcessFlowNodeService.getAllFlowNode(procInsId);
    }

    /**
     * @return List<UserTask> 配置了多实例特性的用户任务节点列表，无此类节点时为空列表
     * @description 只取流程定义里配置了多实例的用户任务节点
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<UserTask> getAllMultiInstanceNode(String procInsId) {
        return wfProcessFlowNodeService.getAllMultiInstanceNode(procInsId);
    }

    /**
     * @return String 流程已结束时返回 finish，进行中返回 running
     * @description 按流程实例判断该流程是否已走到结束节点
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public String checkFlowIsEnd(String procInsId) {
        return wfProcessDetailService.checkFlowIsEnd(procInsId);
    }

    /**
     * @return Object 流程实例的业务拓展信息对象，无记录时为 null
     * @description 按流程实例 ID 取流程实例的业务拓展信息
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Object getProcInsIdToExtends(String procInsId) {
        return extendProcinstService.selectProcInstanId(procInsId);
    }

    /**
     * @return String 流程定义 KEY，定义不存在时抛空指针异常
     * @description 由流程定义 ID 查出流程定义并返回其 KEY
     * @params [definitionId] 流程定义 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public String getProcessModelKey(String definitionId) {
        ProcessDefinition processDefinition =
                repositoryService
                        .createProcessDefinitionQuery()
                        .processDefinitionId(definitionId)
                        .singleResult();
        return processDefinition.getKey();
    }

    /**
     * @return TableDataInfo<Object> 待办与抄送合并后的分页结果
     * @description 合并查询我的待办任务与抄送给我的流程，按分页返回
     * @params [processQuery, pageQuery] 查询条件，含流程名、分类、状态；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public TableDataInfo<Object> getTaskTodoAndCopy(ProcessQuery processQuery, PageQuery pageQuery) throws ParseException {
        return wfToDoTaskService.getTaskTodoAndCopy(processQuery, pageQuery);
    }

    /**
     * @return List<WfDeployForm> 流程部署关联的表单列表；实例记录的流程定义与传入不一致时返回 null
     * @description 按流程定义与流程实例查询流程部署关联的表单内容
     * @params [procDefId, procInsId] 流程定义 ID；流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfDeployForm> selectFormContent(String procDefId, String procInsId) {
        return wfProcessHiFormService.selectFormContent(procDefId, procInsId);
    }

    /**
     * @return String 流程定义部署的 BPMN 2.0 XML 文本
     * @description 读取流程定义部署的 BPMN XML 内容，读取失败时抛出运行时异常
     * @params [processDefId] 流程定义 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public String queryBpmnXmlById(String processDefId) {
        InputStream inputStream = repositoryService.getProcessModel(processDefId);
        try {
            return IoUtil.readUtf8(inputStream);
        } catch (IORuntimeException exception) {
            throw new RuntimeException("加载xml文件异常");
        }
    }

    /**
     * @return Map<String, Object> 任务变量键值对，无变量时为空 Map
     * @description 取任务级变量，含任务本地变量
     * @params [taskId] 任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Map<String, Object> getTaskVariables(String taskId) {
        return wfVariablesService.getTaskVariables(taskId);
    }

    /**
     * @return Map<String, Object> 流程实例变量键值对，无变量时为空 Map
     * @description 取流程实例级变量
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Map<String, Object> getProcessVariables(String procInsId) {
        return wfVariablesService.getProcessVariables(procInsId);
    }

    /**
     * @return List<WfTaskVo> 该用户参与过的任务列表，无匹配时为空列表
     * @description 按用户标识查询该用户参与过的任务
     * @params [processQuery, pageQuery] 查询条件，含用户标识、流程名、状态；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectProcessTaskByUser(ProcessQuery processQuery, PageQuery pageQuery) {
        return wfAllTaskService.selectProcessTaskByUser(processQuery, pageQuery);
    }

    /**
     * @description 打印流程实例已完成节点、当前活动节点、已走连线与起止节点的调试信息，供问题排查使用
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void getProcessActivityInfo(String procInsId) {
        // 获取流程实例
        HistoricProcessInstance historicProcIns =
                historyService
                        .createHistoricProcessInstanceQuery()
                        .processInstanceId(procInsId)
                        .includeProcessVariables()
                        .singleResult();
        //获取所有已经完成的节点
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(procInsId)
                .finished()
                .activityTypes(
                        CollUtil.newHashSet(
//                                BpmnXMLConstants.ELEMENT_EVENT_START,
//                                BpmnXMLConstants.ELEMENT_EVENT_END,
                                BpmnXMLConstants.ELEMENT_TASK_USER))
                .orderByHistoricActivityInstanceStartTime()
                .desc()
                .orderByHistoricActivityInstanceEndTime()
                .desc()
                .list();
        //已完成的活动id集合
        List<String> activityList = new ArrayList<>();
        historicActivityInstances.forEach(historicActivityInstance -> {
            activityList.add(historicActivityInstance.getActivityId());
        });
        //判断流程是否结束
        String status = this.checkFlowIsEnd(procInsId);
        //获取bpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcIns.getProcessDefinitionId());
        //获取当前活动列表
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(procInsId);
        //获取连线的列表
        List<ActivityInstance> highLightedFlowInstances = runtimeService.createActivityInstanceQuery()
                .activityType(BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW).processInstanceId(procInsId).list();
        List<String> sequenceFlow = new ArrayList<>();
        highLightedFlowInstances.forEach(item -> {
            sequenceFlow.add(item.getActivityId());
        });
        log.debug("已完成的节点activity");
        log.debug("{}", activityList);
        log.debug("当前的节点activity");
        log.debug("{}", activeActivityIds);
        log.debug("当前的连线列表");
        log.debug("{}", sequenceFlow);
        ModelUtils.selectStartToEndUserTask(bpmnModel);
        //获取开始节点
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        //获取结束节点
        EndEvent endEvent = ModelUtils.getEndEvent(bpmnModel);
        List<WfActivityVo> wfActivityVoList = new ArrayList<>();
        //放入开始节点
        wfActivityVoList.add(new WfActivityVo(startEvent.getId(), startEvent.getName(), "finish"));
        //获取全部用户任务
        Collection<UserTask> userTaskList = ModelUtils.getAllUserTaskEvent(bpmnModel);
        //已经完成活动中包含 结束节点的话，代表流程结束  否则 则未结束
        if (activityList.contains(endEvent.getId())) {
            wfActivityVoList.add(new WfActivityVo(startEvent.getId(), startEvent.getName(), "finish"));
            //完结的话直接返回，完成的节点Id就好
        } else {
            wfActivityVoList.add(new WfActivityVo(endEvent.getId(), endEvent.getName(), "running"));

        }
        log.debug("{}", historicActivityInstances);
    }
}
