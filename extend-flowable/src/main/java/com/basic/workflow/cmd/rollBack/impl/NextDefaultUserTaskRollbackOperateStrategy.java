package com.basic.workflow.cmd.rollBack.impl;


import com.basic.workflow.cmd.rollBack.AbstractRollbackOperateStrategy;
import com.basic.workflow.cmd.rollBack.RollbackConstants;
import com.basic.workflow.cmd.rollBack.RollbackParamsTemplate;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.api.history.HistoricTaskInstance;

/**
 * @description 普通用户任务节点的回滚策略，在父执行下重建执行与任务并移除本次回滚涉及的历史任务
 * @author Zengjunlong
 * @date 16:02 2024/3/7
 */
public class NextDefaultUserTaskRollbackOperateStrategy extends AbstractRollbackOperateStrategy {

    /**
     * @description 用回滚参数构造策略，对应撤回普通用户任务节点的场景
     * @params [paramsTemplate] 回滚参数模板，含历史任务与当前节点元素
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public NextDefaultUserTaskRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        super(paramsTemplate);
    }

    /**
     * @description 在父执行下新建执行并创建任务，再移除本次回滚涉及的历史任务
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void createExecution() {
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

        // 获取正在执行 execution
        ExecutionEntity executionEntity = getExecutionEntity();

        ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext).createChildExecution(executionEntity.getParent());
        // 创建新任务
        createExecution(newExecution);
        // 移除历史任务
        removeHisTask(hisTask);
    }

    /**
     * @description 先按父类写入任务办理人变量，再登记普通节点回滚的策略类型供全局监听识别
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void setAssignee() {
        // 进行任务执行人配置,之后使用全局监听出发更新
        super.setAssignee();
        String type = RollbackConstants.TASK_TYPE_PREFIX_KEY + paramsTemplate.getHisTask().getProcessInstanceId() + paramsTemplate.getHisTask().getTaskDefinitionKey();
        variables.put(type,NextDefaultUserTaskRollbackOperateStrategy.class.getSimpleName());
    }




}
