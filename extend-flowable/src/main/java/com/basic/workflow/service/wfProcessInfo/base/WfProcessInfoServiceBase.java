package com.basic.workflow.service.wfProcessInfo.base;

import com.basic.workflow.service.wfProcessInfo.allProcessInfo.WfAllProcessOrTaskService;
import com.basic.workflow.service.wfProcessInfo.allProcessInfo.WfAllProcessService;
import com.basic.workflow.service.wfProcessInfo.allProcessInfo.WfAllTaskService;
import com.basic.workflow.service.wfProcessInfo.claimTask.WfProcessClaimTaskService;
import com.basic.workflow.service.wfProcessInfo.detail.*;
import com.basic.workflow.service.wfProcessInfo.extend.WfExtendProcessService;
import com.basic.workflow.service.wfProcessInfo.finishTask.WfFinishTaskService;
import com.basic.workflow.service.wfProcessInfo.flowNode.WfProcessFlowNodeService;
import com.basic.workflow.service.wfProcessInfo.ownProcess.WfOwnProcessService;
import com.basic.workflow.service.wfProcessInfo.toDoTask.WfToDoTaskService;
import com.basic.workflow.service.wfProcessInfo.variables.WfVariablesService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yanbin
 * @description TODO (Flowable流程流转历史信息处理抽象整合类)
 * @date 2024/03/04
 */
@Component
public abstract class WfProcessInfoServiceBase extends WfProcessInfoBase {
    /**
     * Flowable获取所有流程实例信息
     */
    @Resource
    protected WfAllProcessService wfAllProcessService;
    /**
     * Flowable获取所有任务信息
     */
    @Resource
    protected WfAllTaskService wfAllTaskService;
    /**
     * Flowable根据实例procInsId查询历史任务信息
     */
    @Resource
    protected WfHistoryProcNodeService wfHistoryProcNodeService;
    /**
     * Flowable流程历史意见信息
     */
    @Resource
    protected WfProcessCommentService wfProcessCommentService;
    /**
     * Flowable当前流程的当前任务信息
     */
    @Resource
    protected WfProcessCurrentTaskService wfProcessCurrentTaskService;
    /**
     * Flowable流程流转历史详情查询
     */
    @Resource
    protected WfProcessDetailService wfProcessDetailService;
    /**
     * Flowable获取流程执行过程
     */
    @Resource
    protected WfProcessFlowViewerService wfProcessFlowViewerService;
    /**
     * flowable历史表单信息
     */
    @Resource
    protected WfProcessHiFormService wfProcessHiFormService;
    /**
     * Flowable已办任务查询
     */
    @Resource
    protected WfFinishTaskService wfFinishTaskService;
    /**
     * Flowable节点相关信息查询
     */
    @Resource
    protected WfProcessFlowNodeService wfProcessFlowNodeService;
    /**
     * Flowable已发任务查询
     */
    @Resource
    protected WfOwnProcessService wfOwnProcessService;
    /**
     * Flowable待办任务查询
     */
    @Resource
    protected WfToDoTaskService wfToDoTaskService;
    /**
     * Flowable代签任务查询
     */
    @Resource
    protected WfProcessClaimTaskService wfProcessClaimTaskService;
    /**
     * Flowable所有任务实例查询
     */
    @Resource
    protected WfAllProcessOrTaskService wfAllProcessOrTaskService;
    /**
     * Flowable流程变量
     */
    @Resource
    protected WfVariablesService wfVariablesService;
    /**
     * 工作额外方法
     */
    @Resource
    protected WfExtendProcessService wfExtendProcessService;
}
