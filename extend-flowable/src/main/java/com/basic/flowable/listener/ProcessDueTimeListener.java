package com.basic.flowable.listener;

import lombok.extern.slf4j.Slf4j;

import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.context.Context;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @description 流程执行监听器，节点被执行时读取当前活动节点与所属流程实例信息并输出调试日志
 * @author yanbin
 * @date 2024/05/21
 */
@Slf4j
@Component
public class ProcessDueTimeListener implements ExecutionListener {
    private static ProcessDueTimeListener myListener;

    // 解决监听器中 Bean 获取不到问题
//    @PostConstruct
//    public void init() {
//        myListener = this;
////        myListener.historyService = this.historyService;
//    }

    /**
     * @description 流程执行到该节点时读取当前活动节点与所属流程实例信息，当前仅输出调试日志
     * @params [delegateExecution] 流程引擎传入的执行上下文
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void notify(DelegateExecution delegateExecution) {
        HistoryService historyService = Context.getProcessEngineConfiguration().getHistoryService();
        TaskService taskService = Context.getProcessEngineConfiguration().getTaskService();
        String actvityId = delegateExecution.getCurrentActivityId();
        //获取当前的实例信息
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(delegateExecution.getProcessInstanceId()).singleResult();
//        // 结束的实例
        log.debug("****结束时间监听*****");
    }
}
