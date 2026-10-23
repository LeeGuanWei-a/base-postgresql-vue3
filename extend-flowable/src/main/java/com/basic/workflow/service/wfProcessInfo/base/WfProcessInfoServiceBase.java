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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @description 流程流转信息服务基类，集中注入流程实例、任务、历史节点与意见等查询服务。
 * 子服务字段全部加 {@code @Lazy}，避免与子服务继承链（ProcessInfoBase → ProcessInfoServiceBase → ...）形成
 * Spring 启动期的 bean 创建循环。运行时第一次调用才解析真实依赖。
 * @author yanbin
 * @date 2024/03/04
 */
@Component
public abstract class WfProcessInfoServiceBase extends WfProcessInfoBase {
    /**
     * Flowable获取所有流程实例信息
     */
    @Resource
    @Lazy
    protected WfAllProcessService wfAllProcessService;
    /**
     * Flowable获取所有任务信息
     */
    @Resource
    @Lazy
    protected WfAllTaskService wfAllTaskService;
    /**
     * Flowable根据实例procInsId查询历史任务信息
     */
    @Resource
    @Lazy
    protected WfHistoryProcNodeService wfHistoryProcNodeService;
    /**
     * Flowable流程历史意见信息
     */
    @Resource
    @Lazy
    protected WfProcessCommentService wfProcessCommentService;
    /**
     * Flowable当前流程的当前任务信息
     */
    @Resource
    @Lazy
    protected WfProcessCurrentTaskService wfProcessCurrentTaskService;
    /**
     * Flowable流程流转历史详情查询
     */
    @Resource
    @Lazy
    protected WfProcessDetailService wfProcessDetailService;
    /**
     * Flowable获取流程执行过程
     */
    @Resource
    @Lazy
    protected WfProcessFlowViewerService wfProcessFlowViewerService;
    /**
     * flowable历史表单信息
     */
    @Resource
    @Lazy
    protected WfProcessHiFormService wfProcessHiFormService;
    /**
     * Flowable已办任务查询
     */
    @Resource
    @Lazy
    protected WfFinishTaskService wfFinishTaskService;
    /**
     * Flowable节点相关信息查询
     */
    @Resource
    @Lazy
    protected WfProcessFlowNodeService wfProcessFlowNodeService;
    /**
     * Flowable已发任务查询
     */
    @Resource
    @Lazy
    protected WfOwnProcessService wfOwnProcessService;
    /**
     * Flowable待办任务查询
     */
    @Resource
    @Lazy
    protected WfToDoTaskService wfToDoTaskService;
    /**
     * Flowable代签任务查询
     */
    @Resource
    @Lazy
    protected WfProcessClaimTaskService wfProcessClaimTaskService;
    /**
     * Flowable所有任务实例查询
     */
    @Resource
    @Lazy
    protected WfAllProcessOrTaskService wfAllProcessOrTaskService;
    /**
     * Flowable流程变量
     */
    @Resource
    @Lazy
    protected WfVariablesService wfVariablesService;
    /**
     * 工作额外方法
     */
    @Resource
    @Lazy
    protected WfExtendProcessService wfExtendProcessService;
}
