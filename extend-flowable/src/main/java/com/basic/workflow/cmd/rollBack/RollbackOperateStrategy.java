package com.basic.workflow.cmd.rollBack;

import org.flowable.common.engine.impl.interceptor.CommandContext;

import java.util.Map;

/**
 * @Author Zengjunlong
 * @Description //TODO 撤回抽象类定义抽象策略接口
 * @Date 17:54 2024/3/6
 * @Param 
 * @return 
 **/
public interface RollbackOperateStrategy {


    /**
     * 处理
     */
    void process(CommandContext commandContext, String assignee);

    /**
     * 处理
     */
    void process(CommandContext commandContext, String assignee, Map<String, Object> variables);


    /**
     *  配置处理标识
     * @param assigneeExpr
     * @param assigneeListExpr
     */
    void setAssigneeExpr(String assigneeExpr, String assigneeListExpr);

    /**
     *  配置任务处理人
     */
    void setAssignee();

    /**
     * 移除相关关联
     */
    void existNextFinishedTask();

    /**
     * 移除历史痕迹
     */
    void deleteHisActInstance();

    /**
     * 移除正在运行的任务
     */
    void deleteRuntimeTasks();

    /**
     * 移除扩展表、历史用户关系表、数据
     */
    void deleteExtentHis();

    /**
     * 创建任务
     */
    void createExecution();

}
