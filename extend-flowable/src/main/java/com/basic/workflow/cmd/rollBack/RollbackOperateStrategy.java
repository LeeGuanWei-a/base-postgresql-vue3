package com.basic.workflow.cmd.rollBack;

import org.flowable.common.engine.impl.interceptor.CommandContext;

import java.util.Map;

/**
 * @description 流程撤回策略接口，约定了按场景执行回滚、校验后续节点与设置会签表达式等能力
 * @author Zengjunlong
 * @date 17:54 2024/3/6
 */
public interface RollbackOperateStrategy {


    /**
     * @description 在不带入额外流程变量的情况下执行一次回滚处理
     * @params [commandContext, assignee] 命令上下文；回滚后任务的受理人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void process(CommandContext commandContext, String assignee);

    /**
     * @description 带入指定流程变量执行一次回滚处理，供需要覆盖变量的场景使用
     * @params [commandContext, assignee, variables] 命令上下文；回滚后任务的受理人；回滚过程中要写入的流程变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void process(CommandContext commandContext, String assignee, Map<String, Object> variables);


    /**
     * @description 配置会签任务中单个受理人与受理人集合的变量表达式名
     * @params [assigneeExpr, assigneeListExpr] 单个受理人的变量表达式名；受理人集合的变量表达式名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void setAssigneeExpr(String assigneeExpr, String assigneeListExpr);

    /**
     * @description 配置回滚后任务的处理人，由实现类按自身回滚场景决定取值来源
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void setAssignee();

    /**
     * @description 移除回滚目标节点之后已完成的后续任务及其关联数据
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void existNextFinishedTask();

    /**
     * @description 删除回滚目标节点之后的运行态活动实例，抹掉流程图上已走过的痕迹
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteHisActInstance();

    /**
     * @description 删除回滚目标节点之后仍在运行的任务
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteRuntimeTasks();

    /**
     * @description 清理业务扩展表与历史用户关系表中与回滚节点相关的数据
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteExtentHis();

    /**
     * @description 在回滚目标节点上重建执行与任务，把流程实例挂回该节点
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void createExecution();

}
