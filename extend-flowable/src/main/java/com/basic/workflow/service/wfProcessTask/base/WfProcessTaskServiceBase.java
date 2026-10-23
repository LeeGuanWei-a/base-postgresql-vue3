package com.basic.workflow.service.wfProcessTask.base;

import com.basic.workflow.service.wfProcessTask.addSign.WfAddSignTaskService;
import com.basic.workflow.service.wfProcessTask.claim.WfClaimTaskService;
import com.basic.workflow.service.wfProcessTask.complete.WfCompleteTaskService;
import com.basic.workflow.service.wfProcessTask.delegate.WfDelegateTaskService;
import com.basic.workflow.service.wfProcessTask.delete.WfDeleteProcessService;
import com.basic.workflow.service.wfProcessTask.editTask.WfEditTaskService;
import com.basic.workflow.service.wfProcessTask.reject.WfRejectTaskService;
import com.basic.workflow.service.wfProcessTask.returnInfo.WfReturnTaskService;
import com.basic.workflow.service.wfProcessTask.skip.WfSkipTaskService;
import com.basic.workflow.service.wfProcessTask.stop.WfStopProcInstService;
import com.basic.workflow.service.wfProcessTask.transfer.WfTransferTaskService;
import com.basic.workflow.service.wfProcessTask.wfUserTask.WfNextUserTaskService;
import com.basic.workflow.service.wfProcessTask.wfUserTask.WfUserTaskInfoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @description 任务处理服务基类，集中注入审批、加签、签收、驳回等各类任务处理服务。
 * 子服务字段全部加 {@code @Lazy}，避免与子服务继承链（ProcessTaskBase → ProcessTaskServiceBase → ...）形成
 * Spring 启动期的 bean 创建循环。
 * @author yanbin
 * @date 2024/03/01
 */
@Component
public abstract class WfProcessTaskServiceBase extends WfProcessTaskBase {
    /**
     * Flowable拒绝任务处理类
     */
    @Resource
    @Lazy
    protected WfRejectTaskService wfRejectTaskService;
    /**
     * Flowable加签任务处理类
     */
    @Resource
    @Lazy
    protected WfAddSignTaskService wfAddSignTaskService;
    /**
     * flowable签收任务 处理类
     */
    @Resource
    @Lazy
    protected WfClaimTaskService wfClaimTaskService;
    /**
     * flowable审批任务处理类
     */
    @Resource
    @Lazy
    protected WfCompleteTaskService wfCompleteTaskService;
    /**
     * Flowable委派任务处理类
     */
    @Resource
    @Lazy
    protected WfDelegateTaskService wfDelegateTaskService;
    /**
     * (Flowable删除流程实例，删除所选任务)处理类
     */
    @Resource
    @Lazy
    protected WfDeleteProcessService wfDeleteProcessService;
    /**
     * Flowable修改任务信息处理类
     */
    @Resource
    @Lazy
    protected WfEditTaskService wfEditTaskService;
    /**
     * Flowable退回任务处理类
     */
    @Resource
    @Lazy
    protected WfReturnTaskService wfReturnTaskService;
    /**
     * Flowable结束流程处理类
     */
    @Resource
    @Lazy
    protected WfStopProcInstService wfStopProcInstService;
    /**
     * Flowab0le转办任务处理类
     */
    @Resource
    @Lazy
    protected WfTransferTaskService wfTransferTaskService;
    /**
     * Flowable处理下一节点任务
     */
    @Resource
    @Lazy
    protected WfNextUserTaskService wfNextUserTaskService;
    /**
     * Flowable获取节点详细信息
     */
    @Resource
    @Lazy
    protected WfUserTaskInfoService wfUserTaskInfoService;
    /**
     * 跳转任意节点实现
     */
    @Resource
    @Lazy
    protected WfSkipTaskService wfSkipTaskService;
}
