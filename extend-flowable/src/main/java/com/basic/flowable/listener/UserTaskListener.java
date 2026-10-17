package com.basic.flowable.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description 用户任务监听器，在用户任务事件触发时查询任务及其历史参与人链接，供扩展办理人逻辑
 * @author yanbin
 * @date 2023/10/19
 */
@Slf4j
@AllArgsConstructor
@Component(value = "userTaskListener")
public class UserTaskListener implements TaskListener {

    private final TaskService taskService;
    private final HistoryService historyService;

    /**
     * 注入字段（名称与流程设计时字段名称一致${userTaskListener}代理表达式）
     */
    // private FixedValue field;
    /**
     * @description 用户任务事件触发时按任务ID查出运行中的任务及其历史参与人链接，供监听器扩展办理人逻辑
     * @params [delegateTask] 流程引擎传入的用户任务
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        Task task = taskService.createTaskQuery().taskId(delegateTask.getId()).singleResult();
        List<HistoricIdentityLink> historicIdentityLinkList =
                historyService.getHistoricIdentityLinksForTask(task.getId());
        // TODO 实现你的任务监听器逻辑
        log.debug("执行任务监听器...");
    }

}
