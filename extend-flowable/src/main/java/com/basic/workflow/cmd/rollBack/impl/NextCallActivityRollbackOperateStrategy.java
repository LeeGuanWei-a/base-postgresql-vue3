package com.basic.workflow.cmd.rollBack.impl;


import com.basic.workflow.cmd.rollBack.AbstractRollbackOperateStrategy;
import com.basic.workflow.cmd.rollBack.RollbackConstants;
import com.basic.workflow.cmd.rollBack.RollbackParamsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.CallActivity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.HistoricTaskInstanceQueryImpl;
import org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: NextCallActivityRollbackOperateStrategy
 * @Author: huangrenhao
 * @Description:
 * @CreateTime： 2020/4/14 0014 上午 11:29
 * @Version：
 **/
@Slf4j
public class NextCallActivityRollbackOperateStrategy extends AbstractRollbackOperateStrategy {

    /**
     * 下一节点 callActivity
     */
    private CallActivity callActivity;

    private List<ExecutionEntity> callActivityExecutionList;

    private ExecutionEntity callActivityProcess;

    /**
     * @description 用回滚参数构造「下一节点是调用子流程」的回滚策略
     * @params [paramsTemplate] 回滚参数模板，含历史任务与当前节点元素
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public NextCallActivityRollbackOperateStrategy(RollbackParamsTemplate paramsTemplate) {
        super(paramsTemplate);
    }

    /**
     * @description 定位下一节点的调用子流程实例，若该子流程下已存在已完成任务则抛出 RuntimeException 禁止回退
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void existNextFinishedTask() {

        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();

        Map<String, CallActivity> callActivityMap = paramsTemplate.getCallActivityMap();
        String key = callActivityMap.keySet().iterator().next();
        this.callActivity = callActivityMap.get(key);

        // 下一节点callActivity的 flowId
        callActivityExecutionList = CommandContextUtil.getExecutionEntityManager(commandContext)
                .findExecutionsByParentExecutionAndActivityIds(hisTask.getProcessInstanceId(), Collections.singletonList(callActivity.getId()));

        // callActivity 在 父级流程的 executionId = 子流程的 processInstanceId
        ExecutionEntity executionEntity = callActivityExecutionList.get(0);

        // 子流程
        callActivityProcess = CommandContextUtil.getExecutionEntityManager(commandContext)
                .findSubProcessInstanceBySuperExecutionId(executionEntity.getId());

        List<HistoricTaskInstance> hisTaskList = CommandContextUtil.getHistoricTaskService(commandContext)
                .findHistoricTaskInstancesByQueryCriteria(
                        (HistoricTaskInstanceQueryImpl) new HistoricTaskInstanceQueryImpl()
                                .finished()
                                .processInstanceId(callActivityProcess.getId())
                );

        if (!hisTaskList.isEmpty()) {
            throw new RuntimeException("子流程已经具有完成的任务,流程无法回退");
        }
    }

    /**
     * @description 设置回退任务的执行人，并以「流程实例+任务定义」为键登记调用子流程回滚策略类型，供全局监听识别
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void setAssignee() {
        // 进行任务执行人配置,之后使用全局监听出发更新
        super.setAssignee();
        String type = RollbackConstants.TASK_TYPE_PREFIX_KEY + paramsTemplate.getHisTask().getProcessInstanceId() + paramsTemplate.getHisTask().getTaskDefinitionKey();
        variables.put(type,NextCallActivityRollbackOperateStrategy.class.getSimpleName());
    }

    /**
     * @description 从历史任务的执行记录出发新建子执行并创建回退任务，随后删除原历史任务
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void createExecution() {
        HistoricTaskInstance hisTask = paramsTemplate.getHisTask();
        ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager(commandContext)
                .findById(hisTask.getExecutionId());

        if (null == executionEntity) {
            log.info("没有找到execution");
            executionEntity = callActivityExecutionList.get(0);
        }

        ExecutionEntity newExecution = CommandContextUtil.getExecutionEntityManager(commandContext)
                .createChildExecution(executionEntity.getParent());

        // 创建新任务
        createExecution(newExecution);
        // 移除历史任务
        removeHisTask(hisTask);
    }

    /**
     * @description 清理被回退掉的调用子流程实例，并删除主流程中对应的运行态父执行记录
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void deleteRuntimeTasks() {

        ExecutionEntity parentExecution = callActivityExecutionList.get(0);


        // 清理子流程
        cleanCallActivityProcessInstance(callActivityProcess);
        // 清理主流程记录
        CommandContextUtil.getExecutionEntityManager(commandContext)
                .delete(parentExecution);

    }

    /**
     * @description 逐个清除指定子流程实例的运行态任务、历史任务、身份关联与变量，最后删除该子流程执行实例
     * @params [processInstance] 待清理的调用子流程执行实例
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void cleanCallActivityProcessInstance(ExecutionEntity processInstance) {
        // 移除正在运行任务信息
        List<Task> list =  taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .list();
        list.forEach(obj->removeRuntimeTaskOperate((TaskEntity) obj));

        // 移除历史任务信息
        List<HistoricTaskInstanceEntity> historicTaskInstanceList = CommandContextUtil.getHistoricTaskService(commandContext)
                .findHistoricTasksByProcessInstanceId(processInstance.getId());
        historicTaskInstanceList.forEach(obj-> CommandContextUtil.getHistoricTaskService(commandContext).deleteHistoricTask(obj));

        // 移除 子流程实例
        CommandContextUtil.getIdentityLinkService(commandContext).deleteIdentityLinksByProcessInstanceId(processInstance.getId());
        CommandContextUtil.getVariableService(commandContext).deleteVariablesByExecutionId(processInstance.getId());
        CommandContextUtil.getExecutionEntityManager(commandContext).delete(processInstance.getId());
    }


}
