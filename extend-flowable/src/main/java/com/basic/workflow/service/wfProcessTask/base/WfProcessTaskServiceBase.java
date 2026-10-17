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
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @description 任务处理服务基类，集中注入审批、加签、签收、驳回等各类任务处理服务
 * @author yanbin
 * @date 2024/03/01
 */
@Component
public abstract class WfProcessTaskServiceBase extends WfProcessTaskBase {
    /**
     * Flowable拒绝任务处理类
     */
    @Resource
    protected WfRejectTaskService wfRejectTaskService;
    /**
     * Flowable加签任务处理类
     */
    @Resource
    protected WfAddSignTaskService wfAddSignTaskService;
    /**
     * flowable签收任务 处理类
     */
    @Resource
    protected WfClaimTaskService wfClaimTaskService;
    /**
     * flowable审批任务处理类
     */
    @Resource
    protected WfCompleteTaskService wfCompleteTaskService;
    /**
     * Flowable委派任务处理类
     */
    @Resource
    protected WfDelegateTaskService wfDelegateTaskService;
    /**
     * (Flowable删除流程实例，删除所选任务)处理类
     */
    @Resource
    protected WfDeleteProcessService wfDeleteProcessService;
    /**
     * Flowable修改任务信息处理类
     */
    @Resource
    protected WfEditTaskService wfEditTaskService;
    /**
     * Flowable退回任务处理类
     */
    @Resource
    protected WfReturnTaskService wfReturnTaskService;
    /**
     * Flowable结束流程处理类
     */
    @Resource
    protected WfStopProcInstService wfStopProcInstService;
    /**
     * Flowab0le转办任务处理类
     */
    @Resource
    protected WfTransferTaskService wfTransferTaskService;
    /**
     * Flowable处理下一节点任务
     */
    @Resource
    protected WfNextUserTaskService wfNextUserTaskService;
    /**
     * Flowable获取节点详细信息
     */
    @Resource
    protected WfUserTaskInfoService wfUserTaskInfoService;
    /**
     * 跳转任意节点实现
     */
    @Resource
    protected WfSkipTaskService wfSkipTaskService;
}
