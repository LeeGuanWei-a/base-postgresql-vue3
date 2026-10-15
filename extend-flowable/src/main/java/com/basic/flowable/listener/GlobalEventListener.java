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
 * @author yanbin
 * @description TODO (全局监听器)
 * @date 2023/10/19
 */
@Component
public class GlobalEventListener extends AbstractFlowableEngineEventListener {

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 流程结束监听器
     */
    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        String processInstanceId = event.getProcessInstanceId();
        Object variable = runtimeService.getVariable(processInstanceId, ProcessConstants.PROCESS_STATUS_KEY);
        ProcessStatus status = ProcessStatus.getProcessStatus(Convert.toStr(variable));
        if (ObjectUtil.isNotNull(status) && ProcessStatus.RUNNING == status) {
            runtimeService.setVariable(processInstanceId, ProcessConstants.PROCESS_STATUS_KEY, ProcessStatus.COMPLETED.getStatus());
        }
        super.processCompleted(event);
    }
}
