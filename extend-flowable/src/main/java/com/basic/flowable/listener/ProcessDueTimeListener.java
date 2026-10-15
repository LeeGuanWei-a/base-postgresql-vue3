package com.basic.flowable.listener;

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
 * @author yanbin
 * @description TODO (定时任务监听器)
 * @date 2024/05/21
 */
@Component
public class ProcessDueTimeListener implements ExecutionListener {
    private static ProcessDueTimeListener myListener;

    // 解决监听器中 Bean 获取不到问题
//    @PostConstruct
//    public void init() {
//        myListener = this;
////        myListener.historyService = this.historyService;
//    }

    @Override
    public void notify(DelegateExecution delegateExecution) {
        HistoryService historyService = Context.getProcessEngineConfiguration().getHistoryService();
        TaskService taskService = Context.getProcessEngineConfiguration().getTaskService();
        String actvityId = delegateExecution.getCurrentActivityId();
        System.out.println(2121);
        //获取当前的实例信息
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(delegateExecution.getProcessInstanceId()).singleResult();
        System.out.println(12121);
//        // 结束的实例
        System.out.println("****结束时间监听*****");
        System.out.println(1212);
        System.out.println(2212);
        System.out.println(4411114);
    }
}
