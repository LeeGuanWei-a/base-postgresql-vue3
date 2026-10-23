package com.basic.workflow.cmd.rollBack.impl;


import com.basic.workflow.cmd.rollBack.AbstractRollbackOperateStrategy;
import com.basic.workflow.cmd.rollBack.RollbackConstants;
import com.basic.workflow.cmd.rollBack.RollbackParamsTemplate;
import org.flowable.bpmn.model.SubProcess;
import org.flowable.engine.impl.ActivityInstanceQueryImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.HistoricTaskInstanceQueryImpl;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @description 下一节点为嵌入式子流程时的回滚策略，负责定位子流程内部节点并完成回退
 * @author Zengjunlong
 * @date 17:55 2024/3/6
 */
public class NextSubProcessRollbackOperateStrategy extends AbstractRollbackOperateStrategy {

    private Logger LOGGER = LoggerFactory.getLogger(NextSubProcessRollbackOperateStrategy.class);

    /**
     * 嵌入子流程中所有节点ID集合
     */
    private Set<String> subProcessItemKeySet = new HashSet<>();

    private SubProcess subProcess;

    /**
     * @description 用回滚参数构造策略，用于撤回下一节点是嵌入式子流程的场景
     * @params [paramsTemplate] 回滚参数模板，含历史任务与当前节点元素
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public NextSubProcessRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        super(paramsTemplate);
    }

    /**
     * @description 缓存子流程内的节点 ID 集合，并校验子流程中是否已有晚于当前任务完成的任务，有则禁止回退
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void existNextFinishedTask() {

        Map<String, SubProcess> subProcessMap = paramsTemplate.getSubProcessMap();

        List<HistoricTaskInstance> historicTaskInstances = CommandContextUtil.getHistoricTaskService(commandContext)
                .findHistoricTaskInstancesByQueryCriteria(
                        (HistoricTaskInstanceQueryImpl) new HistoricTaskInstanceQueryImpl()
                                .taskCompletedAfter(paramsTemplate.getHisTask().getEndTime())
                                .finished()
                );

        String key = subProcessMap.keySet().iterator().next();
        this.subProcess = subProcessMap.get(key);

        subProcess.getFlowElements().forEach(obj -> subProcessItemKeySet.add(obj.getId()));
        if (!historicTaskInstances.isEmpty()) {
            historicTaskInstances.forEach(obj -> {
                if (subProcessItemKeySet.contains(obj.getTaskDefinitionKey())) {
                    LOGGER.error("出现已完成任务，无法进行流程节点撤回: [" + obj + "]");
                    throw new RuntimeException("出现已完成任务，无法进行流程节点撤回");
                }
            });
        }

    }

    /**
     * @description 设置任务执行人，并把嵌入子流程回滚策略类型登记到流程变量，供全局监听识别回滚来源
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void setAssignee() {
        // 进行任务执行人配置,之后使用全局监听出发更新
        super.setAssignee();
        String type = RollbackConstants.TASK_TYPE_PREFIX_KEY + paramsTemplate.getHisTask().getProcessInstanceId() + paramsTemplate.getHisTask().getTaskDefinitionKey();
        variables.put(type,NextSubProcessRollbackOperateStrategy.class.getSimpleName());
    }


    /**
     * @description 在父执行下新建主执行并据此创建回退任务，随后移除原历史任务；历史执行已关闭时改从临近节点取执行
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void createExecution() {

        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();
        ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager(commandContext).findById(hisTask.getExecutionId());

        /**
         *  subProcess 作为下一节点的时候，hisTask的execution会被关闭调。所以需要重新创建
         */
        if (null == executionEntity) {
            LOGGER.info("hisTask:execution 为 null");

            List<ExecutionEntity> executionEntityList = CommandContextUtil
                    .getExecutionEntityManager(commandContext)
                    .findExecutionsByParentExecutionAndActivityIds(hisTask.getProcessInstanceId(), paramsTemplate.getNextFlowIdList());
            if (executionEntityList.isEmpty()) {
                throw new RuntimeException("没有找到临近节点");
            }
            executionEntity = executionEntityList.get(0);
        }
        // 创建主线
        ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext).createChildExecution(executionEntity.getParent());

        // 创建新任务
        createExecution(newExecution);
        // 移除历史任务
        removeHisTask(hisTask);
    }

    /**
     * @description 移除子流程内晚于当前任务产生的节点历史实例与对应连线记录
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void deleteHisActInstance() {

        List<ActivityInstance> activityInstanceEntityList = CommandContextUtil.getActivityInstanceEntityManager(commandContext)
                .findActivityInstancesByQueryCriteria(
                        new ActivityInstanceQueryImpl()
                                .processInstanceId(paramsTemplate.getHisTask().getProcessInstanceId())
                );


        List<String> ids = new ArrayList<>();
        activityInstanceEntityList.forEach(obj -> {
            // 时间大于 任务创建时间 之后线条
            if (obj.getStartTime().getTime() > paramsTemplate.getHisTask().getCreateTime().getTime()
                    && subProcessItemKeySet.contains(obj.getActivityId())) {
                ids.add(obj.getId());
            }
            // 当前任务的连线 ID
            if (paramsTemplate.getHisTask().getTaskDefinitionKey().equals(obj.getActivityId())
                    && obj.getEndTime().getTime() > paramsTemplate.getHisTask().getCreateTime().getTime()
            ) {
                ids.add(obj.getId());
            }
        });

        LOGGER.debug("移除历史任务连线");
        ids.forEach(obj -> historyActivityInstanceMapper.delete(obj));

    }

    /**
     * @description 移除子流程内正在运行的任务，并级联删除该子流程的执行实例层级
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void deleteRuntimeTasks() {
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

        List<TaskEntity> taskEntityList = CommandContextUtil.getTaskService(commandContext).findTasksByProcessInstanceId(hisTask.getProcessInstanceId());

        taskEntityList.forEach(obj -> {
            if (subProcessItemKeySet.contains(obj.getTaskDefinitionKey())) {
                LOGGER.info("移除正在执行的下一节点任务");
                // 移除任务
                removeRuntimeTaskOperate(obj);
            }
        });

        // 获取 subProcess 的 ExecutionEntity
        Collection<ExecutionEntity> executionEntities = CommandContextUtil
                .getExecutionEntityManager(commandContext)
                .findExecutionsByParentExecutionAndActivityIds(hisTask.getProcessInstanceId(), Collections.singletonList(subProcess.getId()));

        executionEntities.forEach(obj -> {
            LOGGER.info("移除 subProcess 层级execution");
            List<ExecutionEntity> children = CommandContextUtil
                    .getExecutionEntityManager(commandContext)
                    .findChildExecutionsByParentExecutionId(obj.getId());

            // 移除级联子节点
            children.forEach(item -> CommandContextUtil
                    .getExecutionEntityManager(commandContext)
                    .delete(item));

            // 移除 subProcess 顶级
            CommandContextUtil
                    .getExecutionEntityManager(commandContext)
                    .delete(obj);
        });

    }


}
