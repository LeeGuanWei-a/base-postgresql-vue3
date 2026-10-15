package com.basic.workflow.service;


import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.workflow.domain.WfCommentInfo;
import com.basic.workflow.domain.WfDeployForm;
import com.basic.workflow.domain.vo.*;
import org.dom4j.DocumentException;
import org.flowable.bpmn.model.UserTask;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (工作流流程管理Service)
 * @date 2023/11/3 10:37
 */
public interface IWfProcessService {
    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询我的流程列表) 已发
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/1/23 11:10
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectPageOwnProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询我的流程列表)已发不分页
     * @params [processQuery]
     * @author yanbin
     * @date 2024/1/23 11:10
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectOwnProcessList(ProcessQuery processQuery);

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询代办任务列表)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/1/23 11:10
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectPageTodoProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询代办任务列表)不分页
     * @params [processQuery]
     * @author yanbin
     * @date 2024/1/23 11:10
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectTodoProcessList(ProcessQuery processQuery);

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询待签任务列表)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/1/23 11:10
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<WfTaskVo> selectPageClaimProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询待签任务列表)不分页
     * @params [processQuery]
     * @author yanbin
     * @date 2024/1/23 11:11
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectClaimProcessList(ProcessQuery processQuery);

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询已办任务列表)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/1/23 11:11
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectPageFinishedProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询已办任务列表)
     * @params [processQuery]
     * @author yanbin
     * @date 2024/1/23 11:11
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectFinishedProcessList(ProcessQuery processQuery);


    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfDeployForm>
     * @description TODO (查询流程部署关联表单信息)
     * @params [procDefId, procInsId]
     * @author yanbin
     * @date 2024/3/5 13:54
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfDeployForm> selectFormContent(String procDefId, String procInsId);

    /**
     * @return java.lang.String
     * @description TODO (读取xml文件)
     * @params [processDefId] 流程定义ID
     * @author yanbin
     * @date 2024/1/23 11:16
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    String queryBpmnXmlById(String processDefId);


    /**
     * @return com.basic.workflow.domain.vo.WfDetailVo
     * @description TODO (获取全部节点的详细内容)
     * @params [procInsId 流程实例ID, taskId 任务ID]
     * @author yanbin
     * @date 2024/1/23 11:16
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfDetailVo queryProcessDetail(String procInsId, String taskId) throws DocumentException;

    /**
     * @return java.lang.String
     * @description TODO (根据流程定义ID获取流程key)
     * @params [definitionId]
     * @author yanbin
     * @date 2024/1/23 11:17
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    String getProcessModelKey(String definitionId);

    /**
     * @return java.util.List<org.flowable.bpmn.model.UserTask>
     * @description TODO (获取全部节点的详细内容)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/23 11:17
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<UserTask> getAllFlowNode(String procInsId);

    /**
     * @return java.util.List<org.flowable.bpmn.model.UserTask>
     * @description TODO (获取流程中全部多实例节点详细内容)
     * @params [procInsId] 流程实例ID
     * @author yanbin
     * @date 2024/1/23 11:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<UserTask> getAllMultiInstanceNode(String procInsId);

    /**
     * @return java.lang.String
     * @description TODO (根据实例id判断流程是否已经结束)
     * @params [procInsId] 流程实例ID
     * @author yanbin
     * @date 2024/1/23 11:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    String checkFlowIsEnd(String procInsId);

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (获取所有流程)区分管理员和普通用户, 管理员获取全部流程，用户获取所参与的流程)
     * @params [processQuery]
     * @author yanbin
     * @date 2024/1/22 16:51
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectHisProcInsProcess(ProcessQuery processQuery);

    /**
     * @return java.util.List<com.basic.workflow.domain.WfCommentInfo>
     * @description TODO (根据实例procInsId获取流程流转记录(审批意见))
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/15 11:53
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCommentInfo> getProcessCommentInfo(String procInsId);

    /**
     * @return java.lang.String
     * @description TODO (根据实例id获取流程业务拓展信息)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/19 16:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Object getProcInsIdToExtends(String procInsId);

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfProcNodeVo>
     * @description TODO (获取历史任务信息列表 已经完成的节点信息)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/29 8:37
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfProcNodeVo> historyProcNodeList(String procInsId);

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskFormVo>
     * @description TODO (获取历史流程表单信息)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/2/28 15:31
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskFormVo> processFormList(String procInsId);

    /**
     * @return com.basic.workflow.domain.vo.WfViewerVo
     * @description TODO (获取流程执行过程)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/29 8:46
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfViewerVo getFlowViewer(String procInsId);

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (根据实例id获取当前任务)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/4 11:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfTaskVo getProcInsIdToTask(String procInsId, String userId);

    /**
     * @return int
     * @description TODO (根据实例id获取当前节点(任务)的详情，任务数量)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/29 8:51
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    int getCurrentTaskInfo(String procInsId);

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (工作流流程管理/运维管理/获取所有实例信息)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/2/4 8:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<WfTaskVo> selectProcessPrcInstance(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (工作流流程管理/运维管理/获取所有任务信息)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/2/5 9:01
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectProcessAllTask(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return com.basic.common.core.page.TableDataInfo<java.lang.Object>
     * @description TODO (获取代办任务和抄送信息)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/2/29 15:08
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<Object> getTaskTodoAndCopy(ProcessQuery processQuery, PageQuery pageQuery) throws ParseException;

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @description TODO (根据任务taskId获取当前任务的变量)
     * @params [taskId]任务id
     * @author yanbin
     * @date 2024/1/23 11:07
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Map<String, Object> getTaskVariables(String taskId);

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @description TODO (根据实例procInsId获取流程所有变量)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/5 15:30
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Map<String, Object> getProcessVariables(String procInsId);

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (工作流流程管理/运维管理/获取当前任务信息)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/2/5 9:01
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectProcessTaskByUser(ProcessQuery processQuery, PageQuery pageQuery);
}
