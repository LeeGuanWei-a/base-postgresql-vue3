package com.basic.flowable.listener;

import lombok.AllArgsConstructor;
import org.flowable.engine.*;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (用户任务监听器)
 * @date 2023/10/19
 */
@AllArgsConstructor
@Component(value = "userTaskListener")
public class UserTaskListener implements TaskListener {

    private final TaskService taskService;
    private final HistoryService historyService;

    /**
     * 注入字段（名称与流程设计时字段名称一致${userTaskListener}代理表达式）
     */
    // private FixedValue field;
    @Override
    public void notify(DelegateTask delegateTask) {
        Task task = taskService.createTaskQuery().taskId(delegateTask.getId()).singleResult();
        List<HistoricIdentityLink> historicIdentityLinkList =
                historyService.getHistoricIdentityLinksForTask(task.getId());
        //TODO 实现你的任务监听器逻辑
        System.out.println("执行任务监听器...");
    }

}
