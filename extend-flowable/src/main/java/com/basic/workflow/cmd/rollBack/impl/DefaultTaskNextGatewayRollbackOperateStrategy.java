package com.basic.workflow.cmd.rollBack.impl;


import com.basic.workflow.cmd.rollBack.AbstractGateWayRollbackOperateStrategy;
import com.basic.workflow.cmd.rollBack.RollbackConstants;
import com.basic.workflow.cmd.rollBack.RollbackParamsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.api.history.HistoricTaskInstance;

/**
 * @Author Zengjunlong
 * @Description //TODO 网关撤回实现类
 * @Date 15:20 2024/3/7
 * @Param 
 * @return 
 **/
@Slf4j
public class DefaultTaskNextGatewayRollbackOperateStrategy extends AbstractGateWayRollbackOperateStrategy {

    public DefaultTaskNextGatewayRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        super(paramsTemplate);
    }

    @Override
    public void createExecution() {
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

        // 获取正在执行 execution
        ExecutionEntity executionEntity = getExecutionEntity();

        ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
                .createChildExecution(executionEntity.getParent());
        // 创建新任务
        createExecution(newExecution);
        // 特殊处理并行网关
        processGateway(executionEntity.getParent());

        // 移除历史任务
        removeHisTask(hisTask);

    }


    @Override
    public void setAssignee() {
        // 进行任务执行人配置,之后使用全局监听出发更新
        super.setAssignee();
        String type = RollbackConstants.TASK_TYPE_PREFIX_KEY + paramsTemplate.getHisTask().getProcessInstanceId() + paramsTemplate.getHisTask().getTaskDefinitionKey();
        variables.put(type,DefaultTaskNextGatewayRollbackOperateStrategy.class.getSimpleName());
    }

}
