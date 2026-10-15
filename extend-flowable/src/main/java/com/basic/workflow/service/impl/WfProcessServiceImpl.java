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
 * @author yanbin
 * @description TODO (工作流流程管理Service实现类)
 * @date 2023/11/3 10:39
 */
@RequiredArgsConstructor
@Service
public class WfProcessServiceImpl extends WfProcessInfoServiceBase implements IWfProcessService {
    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询我的流程列表)已发
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2023/11/17 16:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectPageOwnProcessList(
            ProcessQuery processQuery, PageQuery pageQuery) {
        this.fillCurrentUserForOwn(processQuery);
        return wfOwnProcessService.selectPageOwnProcessList(processQuery, pageQuery);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询我的流程列表) 已发 不分页
     * @params [processQuery]
     * @author yanbin
     * @date 2023/11/17 16:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectOwnProcessList(ProcessQuery processQuery) {
        this.fillCurrentUserForOwn(processQuery);
        return wfOwnProcessService.selectOwnProcessList(processQuery);
    }

    /**
     * 填充「我的流程（已发）」查询的当前登录用户条件。
     *
     * <p>mapper 的 selectOwnProcessList 里过滤是这样写的 ——</p>
     * <pre>
     *   &lt;if test="startUserId!=null and startUserId !=''"&gt;
     *     and pi.start_user_id_ = #{startUserId}
     *   &lt;/if&gt;
     * </pre>
     * <p>一旦 startUserId 为 null，<b>过滤整段不拼接</b>，
     * "我的流程"就会返回<b>所有人的流程实例</b>。这里补上当前登录用户。</p>
     *
     * <p>调用方显式传了 startUserId 时以调用方为准。</p>
     */
    private void fillCurrentUserForOwn(ProcessQuery processQuery) {
        String startUserId = processQuery.getStartUserId();
        if (startUserId == null || startUserId.isEmpty()) {
            processQuery.setStartUserId(String.valueOf(SecurityUtils.getUserId()));
        }
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询代办任务列表)分页
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2023/11/17 16:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectPageTodoProcessList(
            ProcessQuery processQuery, PageQuery pageQuery) {
        this.fillCurrentUserForTodo(processQuery);
        return wfToDoTaskService.selectPageTodoProcessList(processQuery, pageQuery);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询代办任务列表)不分页
     * @params [processQuery]
     * @author yanbin
     * @date 2023/11/17 16:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectTodoProcessList(ProcessQuery processQuery) {
        this.fillCurrentUserForTodo(processQuery);
        return wfToDoTaskService.selectTodoProcessList(processQuery);
    }

    /**
     * 填充「待办」查询的当前登录用户条件。
     *
     * <p>必须填充：mapper 的 selectTodoTaskList 里用户过滤是这样的 ——</p>
     * <pre>
     *   &lt;if test="userId != null and userId != ''"&gt;
     *     AND ( t.assignee_ = #{userId} OR ( (assignee 为空) AND (我是候选人/我的角色是候选角色) ) )
     *   &lt;/if&gt;
     * </pre>
     * <p>一旦 userId 为 null，<b>整个用户过滤会被跳过</b>，待办列表就会返回所有人的任务
     * （表现为：别人的待办也出现在"我的待办"里，多个账号看到同一批数据）。
     * 原来只有"待签收"做了填充，"待办"两个方法都没有，这里补上。</p>
     *
     * <p>调用方若显式传了 userId（例如管理员查他人待办），以调用方为准。</p>
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
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询待签任务列表)分页
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2023/11/17 16:29
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
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO （查询待签任务列表)不分页
     * @params [processQuery]
     * @author yanbin
     * @date 2023/11/17 16:29
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectClaimProcessList(ProcessQuery processQuery) {
        return wfProcessClaimTaskService.selectClaimProcessList(processQuery);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询已办任务列表) 分页
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2023/11/17 16:29
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectPageFinishedProcessList(
            ProcessQuery processQuery, PageQuery pageQuery) {
        return wfFinishTaskService.selectPageFinishedProcessList(processQuery, pageQuery);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询已办任务列表) 不分页
     * @params [processQuery]
     * @author yanbin
     * @date 2023/11/17 16:29
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectFinishedProcessList(ProcessQuery processQuery) {
        return wfFinishTaskService.selectFinishedProcessList(processQuery);
    }


    /**
     * @return com.basic.workflow.domain.vo.WfDetailVo
     * @description TODO (流程详情信息)
     * @params [procInsId 流程实例ID, taskId 任务ID]
     * @author yanbin
     * @date 2024/1/19 14:09
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfDetailVo queryProcessDetail(String procInsId, String taskId) throws DocumentException {
        return wfProcessDetailService.queryProcessDetail(procInsId, taskId);
    }

    /**
     * @return com.basic.workflow.domain.vo.WfTaskFormVo
     * @description TODO (根据部署deployID和taskId获取当前任务的表单)
     * @params [deployId, taskId]
     * @author yanbin
     * @date 2024/2/28 15:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private WfTaskFormVo currTaskFormData(String deployId, String taskId) {
        return wfProcessDetailService.currTaskFormData(deployId, taskId);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.WfCommentInfo>
     * @description TODO 根据实例procInsId获取流程流转记录(审批意见)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/2/20 15:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfCommentInfo> getProcessCommentInfo(String procInsId) {
        return wfProcessCommentService.getProcessCommentInfo(procInsId);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (TODO(获取所有流程)区分管理员和普通用户, 管理员获取全部流程，用户获取所参与的流程)
     * @params [processQuery] params:role = admin 另一种验证管理的方式
     * @author yanbin
     * @date 2024/1/22 9:12
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectHisProcInsProcess(ProcessQuery processQuery) {
        return wfAllProcessOrTaskService.selectHisProcInsProcess(processQuery);
    }

    /**
     * @return com.basic.workflow.domain.vo.WfTaskVo
     * @description TODO (根据实例id获取当前任务)
     * @params [procInsId]
     * @author yanbin
     * @date 2023/12/27 11:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfTaskVo getProcInsIdToTask(String procInsId, String userId) {
        return wfProcessCurrentTaskService.getProcInsIdToTask(procInsId, userId);
    }

    /**
     * @return int
     * @description TODO (根据实例id获取当前节点(任务)的详情，任务数量)
     * @params [procInsId]
     * @author yanbin
     * @date 2023/12/7 8:39
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public int getCurrentTaskInfo(String procInsId) {
        return wfProcessCurrentTaskService.getCurrentTaskNum(procInsId);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (工作流流程管理/运维管理/获取所有实例信息)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/2/4 8:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public TableDataInfo<WfTaskVo> selectProcessPrcInstance(ProcessQuery processQuery, PageQuery pageQuery) {
        return wfAllProcessService.selectProcessPrcInstance(processQuery, pageQuery);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (工作流流程管理/运维管理/获取所有任务信息)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/2/5 9:01
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectProcessAllTask(ProcessQuery processQuery, PageQuery pageQuery) {
        return wfAllTaskService.selectProcessAllTask(processQuery, pageQuery);
    }


    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskFormVo>
     * @description TODO (获取历史流程表单信息)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/2/28 15:32
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskFormVo> processFormList(String procInsId) {
        return wfProcessHiFormService.processFormList(procInsId);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfProcNodeVo>
     * @description TODO (获取历史任务信息列表 已经完成的节点信息)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/29 8:38
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfProcNodeVo> historyProcNodeList(String procInsId) {
        return wfHistoryProcNodeService.historyProcNodeList(procInsId);
    }

    /**
     * @return com.basic.workflow.domain.vo.WfViewerVo
     * @description TODO (获取流程执行过程)
     * @params [bpmnModel, procInsId]
     * @author yanbin
     * @date 2024/1/11 9:32
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfViewerVo getFlowViewer(String procInsId) {
        return wfProcessFlowViewerService.getFlowViewer(procInsId);
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (获取全部节点的详细内容)包含多实例节点
     * @params [procInsId]
     * @author yanbin
     * @date 2023/11/15 16:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<UserTask> getAllFlowNode(String procInsId) {
        return wfProcessFlowNodeService.getAllFlowNode(procInsId);
    }

    /**
     * @return java.util.List<org.flowable.bpmn.model.UserTask>
     * @description TODO (获取当前流程的全部多实例节点)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/15 14:12
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<UserTask> getAllMultiInstanceNode(String procInsId) {
        return wfProcessFlowNodeService.getAllMultiInstanceNode(procInsId);
    }

    /**
     * @return java.lang.String
     * finsh 表示流程已结束,running表示流程正在进行
     * @description TODO (根据实例id判断流程是否已经结束)
     * @params [procInsId]
     * @author yanbin
     * @date 2023/12/5 17:24
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public String checkFlowIsEnd(String procInsId) {
        return wfProcessDetailService.checkFlowIsEnd(procInsId);
    }

    /**
     * @return java.lang.Object
     * @description TODO (根据实例id获取流程业务拓展信息)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/19 16:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Object getProcInsIdToExtends(String procInsId) {
        return extendProcinstService.selectProcInstanId(procInsId);
    }

    /**
     * @return java.lang.String
     * @description TODO (根据流程定义ID获取流程key)
     * @params [definitionId]
     * @author yanbin
     * @date 2023/11/1 17:36
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
     * @return com.basic.common.core.page.TableDataInfo<java.lang.Object>
     * @description TODO (获取代办任务和抄送信息)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/2/29 15:08
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public TableDataInfo<Object> getTaskTodoAndCopy(ProcessQuery processQuery, PageQuery pageQuery) throws ParseException {
        return wfToDoTaskService.getTaskTodoAndCopy(processQuery, pageQuery);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfDeployForm>
     * @description TODO (查询流程部署关联表单信息)
     * @params [procDefId, procInsId]
     * @author yanbin
     * @date 2024/3/5 13:54
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfDeployForm> selectFormContent(String procDefId, String procInsId) {
        return wfProcessHiFormService.selectFormContent(procDefId, procInsId);
    }

    /**
     * @return java.lang.String
     * @description TODO （读取xml文件)
     * @params [processDefId]
     * @author yanbin
     * @date 2024/3/4 11:45
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
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @description TODO (根据任务taskId获取当前任务的变量)
     * @params [taskId]
     * @author yanbin
     * @date 2023/11/3 10:41
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Map<String, Object> getTaskVariables(String taskId) {
        return wfVariablesService.getTaskVariables(taskId);
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @description TODO (根据实例procInsId获取流程所有变量)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/5 15:30
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Map<String, Object> getProcessVariables(String procInsId) {
        return wfVariablesService.getProcessVariables(procInsId);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (获取当前任务信息)
     * @params processQuery
     * pageQuery
     * @author yanbin
     * @date 2025/2/21 11:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfTaskVo> selectProcessTaskByUser(ProcessQuery processQuery, PageQuery pageQuery) {
        return wfAllTaskService.selectProcessTaskByUser(processQuery, pageQuery);
    }

    /**
     * @return void
     * @description TODO (流程概况)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/29 11:50
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
        System.out.println("已完成的节点activity");
        System.out.println(activityList);
        System.out.println("当前的节点activity");
        System.out.println(activeActivityIds);
        System.out.println("当前的连线列表");
        System.out.println(sequenceFlow);
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
        System.out.println(historicActivityInstances);
    }
}
