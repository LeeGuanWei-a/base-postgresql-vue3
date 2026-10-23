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
 * @description 流程管理 Service 接口，提供我发起的、待办、待签、已办流程的查询以及流程详情、流程变量等信息获取
 * @author yanbin
 * @date 2023/11/3 10:37
 */
public interface IWfProcessService {
    /**
     * @return List<WfTaskVo> 我发起的流程分页结果
     * @description 分页查询当前用户已发起的流程列表
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectPageOwnProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return List<WfTaskVo> 我发起的流程列表，不分页
     * @description 查询当前用户已发起的流程列表，不分页
     * @params [processQuery] 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectOwnProcessList(ProcessQuery processQuery);

    /**
     * @return List<WfTaskVo> 待办任务分页结果
     * @description 分页查询当前用户的待办任务列表
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectPageTodoProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return List<WfTaskVo> 待办任务列表，不分页
     * @description 查询当前用户的待办任务列表，不分页
     * @params [processQuery] 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectTodoProcessList(ProcessQuery processQuery);

    /**
     * @return TableDataInfo<WfTaskVo> 待签任务分页结果
     * @description 分页查询当前用户可签收的任务列表
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<WfTaskVo> selectPageClaimProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return List<WfTaskVo> 待签任务列表，不分页
     * @description 查询当前用户可签收的任务列表，不分页
     * @params [processQuery] 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectClaimProcessList(ProcessQuery processQuery);

    /**
     * @return List<WfTaskVo> 已办任务分页结果
     * @description 分页查询当前用户已办理的任务列表
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectPageFinishedProcessList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return List<WfTaskVo> 已办任务列表，不分页
     * @description 查询当前用户已办理的任务列表，不分页
     * @params [processQuery] 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectFinishedProcessList(ProcessQuery processQuery);


    /**
     * @return List<WfDeployForm> 该流程下各节点关联的部署表单
     * @description 按流程定义 ID 与流程实例 ID 查询流程部署关联的表单内容
     * @params [procDefId, procInsId] 流程定义 ID；流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfDeployForm> selectFormContent(String procDefId, String procInsId);

    /**
     * @return String 流程定义的 BPMN XML 内容
     * @description 按流程定义 ID 读取流程定义的 BPMN XML 文件内容
     * @params [processDefId] 流程定义 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    String queryBpmnXmlById(String processDefId);


    /**
     * @return WfDetailVo 流程实例详情，含全部节点信息与流转记录
     * @description 解析流程定义 XML 后获取流程全部节点的详细内容，供流程详情页展示
     * @params [procInsId, taskId] 流程实例 ID；任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfDetailVo queryProcessDetail(String procInsId, String taskId) throws DocumentException;

    /**
     * @return String 流程定义对应的流程标识 modelKey
     * @description 按流程定义 ID 获取流程定义的流程标识
     * @params [definitionId] 流程定义 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    String getProcessModelKey(String definitionId);

    /**
     * @return List<UserTask> 该流程实例流程定义中的全部用户任务节点
     * @description 按流程实例 ID 获取流程定义中的全部用户任务节点
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<UserTask> getAllFlowNode(String procInsId);

    /**
     * @return List<UserTask> 该流程中全部多实例用户任务节点
     * @description 按流程实例 ID 获取流程定义中全部多实例节点的详细内容
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<UserTask> getAllMultiInstanceNode(String procInsId);

    /**
     * @return String 流程是否结束的判断结果，已结束时返回结束标识
     * @description 按流程实例 ID 判断该流程是否已经结束
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    String checkFlowIsEnd(String procInsId);

    /**
     * @return List<WfTaskVo> 历史流程实例列表，管理员返回全部、普通用户只返回参与的
     * @description 查询历史流程实例列表，管理员取全部流程、普通用户只取本人参与过的流程
     * @params [processQuery] 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectHisProcInsProcess(ProcessQuery processQuery);

    /**
     * @return List<WfCommentInfo> 流程流转记录（审批意见）列表
     * @description 按流程实例 ID 获取流程流转记录，即各节点的审批意见
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCommentInfo> getProcessCommentInfo(String procInsId);

    /**
     * @return Object 流程业务拓展信息
     * @description 按流程实例 ID 获取流程的业务拓展信息
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Object getProcInsIdToExtends(String procInsId);

    /**
     * @return List<WfProcNodeVo> 已完成节点的历史任务信息列表
     * @description 按流程实例 ID 获取历史任务信息列表，即已完成节点的信息
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfProcNodeVo> historyProcNodeList(String procInsId);

    /**
     * @return List<WfTaskFormVo> 历史流程各节点的表单信息列表
     * @description 按流程实例 ID 获取历史流程各节点填写的表单信息
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskFormVo> processFormList(String procInsId);

    /**
     * @return WfViewerVo 流程执行过程数据，供流程图高亮展示
     * @description 按流程实例 ID 获取流程图的执行过程数据
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfViewerVo getFlowViewer(String procInsId);

    /**
     * @return WfTaskVo 当前任务信息，无待办任务时为空
     * @description 按流程实例 ID 与用户 ID 获取该用户当前的待办任务
     * @params [procInsId, userId] 流程实例 ID；用户 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfTaskVo getProcInsIdToTask(String procInsId, String userId);

    /**
     * @return int 当前节点（任务）的数量
     * @description 按流程实例 ID 统计当前节点（任务）的数量，用于判断后续审批入口
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    int getCurrentTaskInfo(String procInsId);

    /**
     * @return TableDataInfo<WfTaskVo> 流程实例分页结果
     * @description 运维管理中分页查询全部流程实例信息
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<WfTaskVo> selectProcessPrcInstance(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return List<WfTaskVo> 全部任务信息列表
     * @description 运维管理中查询全部任务信息
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectProcessAllTask(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return TableDataInfo<Object> 待办任务与抄送信息的组合分页结果
     * @description 合并查询当前用户的待办任务与抄送信息，统一分页返回
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<Object> getTaskTodoAndCopy(ProcessQuery processQuery, PageQuery pageQuery) throws ParseException;

    /**
     * @return Map<String, Object> 当前任务的全部流程变量
     * @description 按任务 ID 获取当前任务的流程变量
     * @params [taskId] 任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Map<String, Object> getTaskVariables(String taskId);

    /**
     * @return Map<String, Object> 流程实例的全部流程变量
     * @description 按流程实例 ID 获取流程的全部流程变量
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Map<String, Object> getProcessVariables(String procInsId);

    /**
     * @return List<WfTaskVo> 指定用户相关的任务信息列表
     * @description 运维管理中按用户查询当前任务信息
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfTaskVo> selectProcessTaskByUser(ProcessQuery processQuery, PageQuery pageQuery);
}
