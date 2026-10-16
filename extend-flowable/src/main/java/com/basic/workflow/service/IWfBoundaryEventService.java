package com.basic.workflow.service;

import com.basic.workflow.domain.bo.WfBoundaryTriggerBo;
import com.basic.workflow.domain.vo.WfBoundaryEventVo;

import java.util.List;

/**
 * 流程边界事件（Boundary Event）运行时触发服务。
 *
 * <p>封装对 Flowable {@code RuntimeService} 的 signal/message/error/cancel 触发调用，
 * 同时把触发记录写进 {@code processStatus} 流程变量，
 * 便于前端列表/详情页标识"此流程是被 boundary 触发的"。</p>
 *
 * <p>本服务为「新增能力」，不修改现有 {@code stopProcess} / {@code abolishProcess} 的人工逻辑。
 * 业务侧如果需要"用 boundary 替代人工取消"，单独在 WfTaskController 里加路由判断。</p>
 *
 * @author integration-supplement
 */
public interface IWfBoundaryEventService {

    /**
     * 触发信号边界事件（全局广播或指定 executionId）。
     *
     * @param bo 触发请求（procInsId / eventCode / variables）
     * @return true 触发成功
     */
    boolean triggerSignal(WfBoundaryTriggerBo bo);

    /**
     * 触发消息边界事件（按 message name 定位到具体 execution）。
     *
     * @param bo 触发请求（procInsId / eventCode=messageName / variables）
     * @return true 触发成功
     */
    boolean triggerMessage(WfBoundaryTriggerBo bo);

    /**
     * 触发错误边界事件（按 BpmnError(errorCode) 抛出）。
     * <p>错误由当前任务的执行人/监听器抛给引擎，定位到 errorRef=errorCode 的 boundary。</p>
     *
     * @param bo 触发请求（procInsId / eventCode=errorCode / comment=errorMessage）
     * @return true 触发成功
     */
    boolean triggerError(WfBoundaryTriggerBo bo);

    /**
     * 触发取消边界事件（transaction / call activity 子流程边界上的 cancel 边界）。
     * <p>常规 UserTask 上挂的 cancel 边界不会被 Flowable 触发——会直接抛业务异常提示。</p>
     *
     * @param bo 触发请求（procInsId / comment 原因）
     * @return true 触发成功
     */
    boolean triggerCancel(WfBoundaryTriggerBo bo);

    /**
     * 查询指定流程实例上「定义阶段」注册的 boundary 事件列表。
     * <p>从最新部署的流程定义里取，<b>不是</b>当前活动的 execution subscription。
     * 用于流程图查看器高亮、调试面板展示。</p>
     *
     * @param procInsId 流程实例 id
     * @return 流程定义上的 boundary 事件清单
     */
    List<WfBoundaryEventVo> listBoundaries(String procInsId);
}
