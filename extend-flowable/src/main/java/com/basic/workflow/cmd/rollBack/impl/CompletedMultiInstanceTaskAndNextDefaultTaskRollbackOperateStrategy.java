package com.basic.workflow.cmd.rollBack.impl;

import com.basic.workflow.cmd.rollBack.AbstractRollbackOperateStrategy;
import com.basic.workflow.cmd.rollBack.RollbackConstants;
import com.basic.workflow.cmd.rollBack.RollbackParamsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.service.impl.HistoricVariableInstanceQueryImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description 撤回已完成会签任务且下一节点为普通任务的策略，重建会签执行并校验回退人是否为最后一位
 * @author Zengjunlong
 * @date 17:55 2024/3/6
 */
@Slf4j
public class CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy extends AbstractRollbackOperateStrategy {


    private boolean isSequence = false;

    /**
     * 会签任务 单个执行人 表达式
     */
    private String assigneeExpr = "assignee";

    /**
     * 会签任务 集合 表达式
     */
    private String assigneeListExpr = "assigneeList";


    /**
     * @description 用回滚参数构造策略，对应撤回已完成会签且下一节点是普通任务的场景
     * @params [paramsTemplate] 回滚参数模板，含历史任务与当前节点元素
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        super(paramsTemplate);
    }

    /**
     * @description 读取会签历史变量重建会签主执行与各分支执行，串行会签另行校验回退人是否为最后一位，最后移除已回滚的历史任务
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void createExecution() {

        if (paramsTemplate.getCurrentTaskElement().getBehavior() instanceof SequentialMultiInstanceBehavior) {
            isSequence = true;
        }

        // 获取 当前执行的 execution
        ExecutionEntity executionEntity = getExecutionEntity();
        List<HistoricVariableInstance> historicVars =  historyService.createHistoricVariableInstanceQuery().processInstanceId(executionEntity.getProcessInstanceId()).variableName("nrOfInstances").list();
        if(historicVars==null||historicVars.size()==0){
            throw new RuntimeException("没有可用会签参数:" + assigneeListExpr);
        }

        Integer sum = 0;
        for(HistoricVariableInstance hi:historicVars){
            if(!hi.getExecutionId().equals(executionEntity.getId())){
                sum = (Integer) hi.getValue();
            }
        }

        List<HistoricVariableInstance> historicVariableInstances = CommandContextUtil.getHistoricVariableService()
                .findHistoricVariableInstancesByQueryCriteria(
                        new HistoricVariableInstanceQueryImpl()
                                .executionId(paramsTemplate.getHisTask().getExecutionId())
                );

        if (historicVariableInstances.isEmpty()) {
            throw new RuntimeException("没有可用会签任务参数");
        }

        // 历史变量
        Map<String, Object> hisVarMap = historicVariableInstances.stream().collect(Collectors.toMap(HistoricVariableInstance::getVariableName, item -> item.getValue()));

        if (hisVarMap.containsKey(assigneeExpr) && hisVarMap.containsKey(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER)) {
            log.info("变量有效");
        } else {
            throw new RuntimeException("缺少会签任务变量");
        }

        /**
         *  串行 最终的 loopCounter assignee 都会是最后一个人
         */
        if (isSequence) {

            List<String> assigneeList = (List<String>) runtimeService.getVariableLocal(paramsTemplate.getHisTask().getProcessInstanceId(), assigneeListExpr);
            if (!assigneeList.get(assigneeList.size() - 1).equals(paramsTemplate.getHisTask().getAssignee())) {
                String msg = "不是串行最后一个节点，无法进行回退 ";
                throw new RuntimeException(msg);
            }
            // 替换任务执行变量
            assigneeList.set(assigneeList.size() - 1, assignee);
            runtimeService.setVariableLocal(paramsTemplate.getHisTask().getProcessInstanceId(), assigneeListExpr, assigneeList);
        }

        // 流程执行变量
        Integer loopCounter = (Integer) hisVarMap.get(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER);

        // 会签主任务
        ExecutionEntity parentExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
                .createChildExecution(executionEntity.getParent());

        parentExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());
        parentExecution.setActive(false);
        // 配置 会签 root execution
        parentExecution.setMultiInstanceRoot(true);

        // 配置主 execution 变量
        Map<String, Object> parentVarMap = new HashMap<>();
        parentVarMap.put(RollbackConstants.MultiInstanceConstants.NR_OF_ACTIVE_INSTANCES, 1);
        parentVarMap.put(RollbackConstants.MultiInstanceConstants.NR_OF_COMPLETE_INSTANCES, 1);
        parentVarMap.put(RollbackConstants.MultiInstanceConstants.NR_OF_INSTANCE, 2);
        parentExecution.setVariablesLocal(parentVarMap);

        if (isSequence) {
            log.info("创建 串行 会签任务");
            createSequenceMultiInstance(parentExecution, sum);
        } else {
            log.info("创建 并行 会签任务");
            createParallelMultiInstance(parentExecution, sum, loopCounter);

        }

        removeHisTask(paramsTemplate.getHisTask());
    }

    /**
     * @description 按串行会签重建单个执行实例，写入办理人并把循环计数器退回到最后一位
     * @params [parentExecution, sum] parentExecution 会签主执行实例；sum 会签实例总数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void createSequenceMultiInstance(ExecutionEntity parentExecution, Integer sum) {

        ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
                .createChildExecution(parentExecution);

        Map<String, Object> varMap = new HashMap<>();
        varMap.put(assigneeExpr, assignee);
        varMap.put(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER, sum - 1);
        newExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());

        newExecution.setVariablesLocal(varMap);
        newExecution.setActive(true);
        CommandContextUtil.getAgenda(commandContext).planContinueMultiInstanceOperation(newExecution, parentExecution, sum - 1);

    }

    /**
     * @description 按并行会签为每个实例建一条子执行，已完成的实例置为不活动，回退点那条置为活动并写入办理人
     * @params [parentExecution, sum, loopCounter] parentExecution 会签主执行实例；sum 会签实例总数；loopCounter 回退所在的循环序号
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void createParallelMultiInstance(ExecutionEntity parentExecution, Integer sum, Integer loopCounter) {

        for (int i = 0; i < sum; i++) {
            if (i != loopCounter) {
                Map<String, Object> varMap = new HashMap<>();
                varMap.put(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER, i);
                varMap.put(assigneeExpr, "已完成任务");

//                // 创建 新执行任务
                ExecutionEntity newExecution = newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
                        .createChildExecution(parentExecution);
                newExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());
                newExecution.setActive(false);

                newExecution.setVariablesLocal(varMap);

//
//                CommandContextUtil.getExecutionEntityManager(commandContext)
//                        .update(newExecution);
            } else {
                ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
                        .createChildExecution(parentExecution);
                newExecution.setCurrentFlowElement(paramsTemplate.getCurrentTaskElement());

                Map<String, Object> varMap = new HashMap<>();
                log.debug("{}", "创建任务的时候撤回审核人==="+assignee);
                varMap.put(assigneeExpr, assignee);
                varMap.put(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER, i);

                newExecution.setVariablesLocal(varMap);
                newExecution.setActive(true);


                CommandContextUtil.getAgenda(commandContext).planContinueMultiInstanceOperation(newExecution, parentExecution, loopCounter);
            }
        }


    }


}
