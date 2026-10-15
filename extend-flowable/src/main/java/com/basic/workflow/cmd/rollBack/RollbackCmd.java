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
 * @Author Zengjunlong
 * @Description //TODO 执行撤回策略 Command实现类
 * @Date 17:07 2024/3/6
 * @Param 
 * @return 
 **/
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

    public RollbackCmd(String taskId, String assignee) {
        this.taskId = taskId;
        this.assignee = assignee;

        this.historyService = SpringUtils.getBean(HistoryService.class);
        this.runtimeService = SpringUtils.getBean(RuntimeService.class);
        this.taskservice = SpringUtils.getBean(TaskService.class);
        this.rollbackStrategyFactory = SpringUtils.getBean(RollbackStrategyFactory.class);
    }

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
