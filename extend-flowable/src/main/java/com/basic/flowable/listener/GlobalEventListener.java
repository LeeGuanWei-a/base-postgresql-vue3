package com.basic.flowable.listener;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;

import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.common.enums.ProcessStatus;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description 全局流程引擎事件监听器，流程实例结束时把流程状态变量归一化为已完成
 * @author yanbin
 * @date 2023/10/19
 */
@Component
public class GlobalEventListener extends AbstractFlowableEngineEventListener {

    @Autowired
    private RuntimeService runtimeService;

    /**
     * @description 流程实例结束时把流程状态变量归一化为 completed，running 状态与 boundary_ 前缀状态都改写为已完成
     * @params [event] 流程引擎实体事件，从中取流程实例 ID 与状态变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        String processInstanceId = event.getProcessInstanceId();
        Object variable = runtimeService.getVariable(processInstanceId, ProcessConstants.PROCESS_STATUS_KEY);
        ProcessStatus status = ProcessStatus.getProcessStatus(Convert.toStr(variable));
        // 仅在状态为 running（普通流程）时把状态改为 completed。
        // 状态为 boundary_*（被边界事件触发后走完的流程）时也归一化回 completed，
        // 便于列表筛选统一按"completed / running / canceled"看。
        if (ObjectUtil.isNotNull(status) && ProcessStatus.RUNNING == status) {
            runtimeService.setVariable(processInstanceId, ProcessConstants.PROCESS_STATUS_KEY, ProcessStatus.COMPLETED.getStatus());
        } else if (variable != null && String.valueOf(variable).startsWith(ProcessConstants.BOUNDARY_TRIGGER_STATUS_PREFIX)) {
            runtimeService.setVariable(processInstanceId, ProcessConstants.PROCESS_STATUS_KEY, ProcessStatus.COMPLETED.getStatus());
        }
        super.processCompleted(event);
    }
}
