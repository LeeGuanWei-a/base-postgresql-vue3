package com.basic.workflow.cmd.rollBack;


import com.basic.common.utils.spring.SpringUtils;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @description 执行撤回策略的 Command 实现，校验历史任务与流程实例后按下一节点类型执行撤回
 * @author Zengjunlong
 * @date 17:07 2024/3/6
 */
public class RollbackCmd implements Command {

    private Logger LOGGER = LoggerFactory.getLogger(RollbackCmd.class);
    /**
     * 任务ID
     */
    private String taskId;

    private String assignee;

    private HistoryService historyService;

    private RuntimeService runtimeService;

    private TaskService taskservice;

    private RollbackStrategyFactory rollbackStrategyFactory;

    /**
     * 会签任务 单个执行人 表达式
     */
    private String assigneeExpr = "assignee";

    /**
     * 会签任务 集合 表达式
     */
    private String assigneeListExpr = "assigneeList";

    /**
     * @description 绑定待回退任务与回退后办理人，并从 Spring 容器取出回滚所需的引擎服务与策略工厂
     * @params [taskId, assignee] 待回退的历史任务ID；回退后任务的办理人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public RollbackCmd(String taskId, String assignee) {
        this.taskId = taskId;
        this.assignee = assignee;

        this.historyService = SpringUtils.getBean(HistoryService.class);
        this.runtimeService = SpringUtils.getBean(RuntimeService.class);
        this.taskservice = SpringUtils.getBean(TaskService.class);
        this.rollbackStrategyFactory = SpringUtils.getBean(RollbackStrategyFactory.class);
    }

    /**
     * @return Object 固定返回 null，本项目约定撤回命令无返回结果
     * @description 校验历史任务已结束且流程实例仍在运行，再按下一节点类型选取撤回策略并执行撤回
     * @params [commandContext] 命令上下文
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Object execute(CommandContext commandContext) {

        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();

        if (null == hisTask){
            String msg = "任务不存在，请联系管理员！";
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }

        if (null == hisTask.getEndTime()){
            String msg = "任务正在执行,不需要回退";
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }

        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(hisTask.getProcessInstanceId()).singleResult();
        if (null == pi) {
            String msg = "该流程已经完成，无法进行任务回退。";
            throw new RuntimeException(msg);
        }
        //创建策略
        RollbackOperateStrategy strategy = rollbackStrategyFactory.createStrategy(hisTask);

        // 配置任务执行表达式
        strategy.setAssigneeExpr(assigneeExpr, assigneeListExpr);
        // 处理
        strategy.process(commandContext, assignee);

        // 判断下一节点类型，根据下一节点类型获得任务处理策略

        //

        return null;
    }
}
