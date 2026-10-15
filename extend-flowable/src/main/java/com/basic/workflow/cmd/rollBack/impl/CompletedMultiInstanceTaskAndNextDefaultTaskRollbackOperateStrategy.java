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
 * @Author Zengjunlong
 * @Description //TODO 已完成会签撤回，下个节点是普通节点
 * @Date 17:55 2024/3/6
 * @Param 
 * @return 
 **/
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


    public CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        super(paramsTemplate);
    }

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
     * 创建并行会签任务
     *
     * @param parentExecution
     * @param loopCounter
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
                System.out.println("创建任务的时候撤回审核人==="+assignee);
                varMap.put(assigneeExpr, assignee);
                varMap.put(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER, i);

                newExecution.setVariablesLocal(varMap);
                newExecution.setActive(true);


                CommandContextUtil.getAgenda(commandContext).planContinueMultiInstanceOperation(newExecution, parentExecution, loopCounter);
            }
        }


    }


}
