package com.basic.workflow.service.wfProcessTask.delete;

import com.basic.common.exception.ServiceException;
import com.basic.flowable.common.enums.ProcessStatus;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import com.basic.workflow.service.wfProcessTask.stop.WfStopProcInstService;
import cn.hutool.core.collection.CollUtil;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @description 流程实例删除服务，校验流程均已结束后清理实例及其意见、拓展信息、抄送与表单数据
 * @author yanbin
 * @date 2024/03/01
 */
@Service
public class WfDeleteProcessService extends WfProcessTaskBase {
    @Autowired
    private WfStopProcInstService wfStopProcInstService;

    /**
     * @return Boolean 删除成功返回 true
     * @description 校验流程均已结束后批量删除历史流程实例，并连带清理历史意见、业务拓展信息、抄送与流程表单，存在进行中的实例时拒绝删除
     * @params [instanceIds] 流程实例ID数组
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProcessByIds(String[] instanceIds) {
        List<String> ids = Arrays.asList(instanceIds);
        // 校验流程是否结束
        long activeInsCount =
                runtimeService
                        .createProcessInstanceQuery()
                        .processInstanceIds(new HashSet<>(ids))
                        .active()
                        .count();
        if (activeInsCount > 0) {
            throw new ServiceException("不允许删除进行中的流程实例");
        }
        // 删除历史流程实例
        historyService.bulkDeleteHistoricProcessInstances(ids);
        //删除历史意见
        wfCommentInfoService.delProcessComment(ids);
        //删除历史流程业务拓展信息
        extendProcinstService.delProcessExtend(ids);
        //删除历史抄送
        wfCopyService.delProcessCopyInfo(ids);
        //删除流程表单
        wfTaskFormService.delProcessForm(ids);
        return true;
    }

    /**
     * @return boolean 删除成功返回 true，processStatus 为空时返回 false
     * @description 按任务粒度删除单条任务，运行中的任务先按会签特性回退多实例计数再逐表清理运行态数据，已完成的任务只删历史任务、历史活动、意见与表单
     * @params [bo] 任务参数，含 taskId、procInsId 与 processStatus
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean deleteProcessTask(WfTaskBo bo) {
        if (bo.getProcessStatus() != null) {
            if (bo.getProcessStatus().equals(ProcessStatus.RUNNING.getStatus())) {
                List<Task> taskList = taskService.createTaskQuery().processInstanceId(bo.getProcInsId()).list();
                if (taskList.size() == 1) {
                    throw new RuntimeException("此任务为此节点最后一条任务,请先完结在删除!");
                }
                TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
                boolean frame = this.checkMultiInstance(taskEntity);
                if (frame) {
                    // 判断是否是串行并行 true是顺序执行 false不是
                    Boolean isSequential = extensionElementsService.getMulTIinstSequentialForKey(taskEntity.getProcessDefinitionId()
                            , taskEntity.getTaskDefinitionKey());
                    // 顺序执行 一条一条的生成多实例
                    if (isSequential) {
                        // 修改参数表act_ru_variable ,act_hi_varinst
                        // 如果是多实例节点  总实例数-1 act_ru_variable
                        wfFlowableTaskMapper.updateRuVariable(taskEntity.getProcessInstanceId(), "nrOfInstances");
                        // 还未完成的实例数-1 act_ru_variable
                        wfFlowableTaskMapper.updateRuVariable(taskEntity.getProcessInstanceId(), "nrOfActiveInstances");
                        // 如果是多实例节点  总实例数-1 act_hi_varinst
                        wfFlowableTaskMapper.updateHisVariable(taskEntity.getProcessInstanceId(), "nrOfInstances");
                        // 还未完成的实例数-1 act_hi_varinst
                        wfFlowableTaskMapper.updateHisVariable(taskEntity.getProcessInstanceId(), "nrOfActiveInstances");
                    }
                    // 非一条一条生成
                    else {
                        // 如果是多实例节点  总实例数-1 act_ru_variable
                        wfFlowableTaskMapper.updateRuVariable(taskEntity.getProcessInstanceId(), "nrOfInstances");
                        // 如果是多实例节点  总实例数-1 act_hi_varinst
                        wfFlowableTaskMapper.updateHisVariable(taskEntity.getProcessInstanceId(), "nrOfInstances");
//                        // 删除生成的多实例数 记录act_ru_variable
//                        wfFlowableTaskMapper.deleteRuVariableForExecutionIdAndName(taskEntity.getExecutionId(), "loopCounter");
//                        // 删除生成的处理人信息 记录act_ru_variable
//                        wfFlowableTaskMapper.deleteRuVariableForExecutionIdAndName(taskEntity.getExecutionId(), "assignee");
//                        // 删除生成的多实例数 act_hi_varinst
//                        wfFlowableTaskMapper.deleteHisVariableForExecutionIdAndName(taskEntity.getExecutionId(), "loopCounter");
//                        // 删除生成的处理人信息 act_hi_varinst
//                        wfFlowableTaskMapper.deleteHisVariableForExecutionIdAndName(taskEntity.getExecutionId(), "assignee");
                    }
                }
                // 删除历史活动绑定的任务信息 act_hi_actinst
                wfActHiActinstMapper.deleteToHiActinstForTaskId(taskEntity.getId());
                // 删除活动绑定的任务信息  act_ru_actinst
                wfActRuActinstMapper.deleteToRuActinstForTaskId(taskEntity.getId());
                // 删除运行中的   act_ru_identitylink
                wfFlowableTaskMapper.deleteRuIdentityLinkForTaskId(taskEntity.getId());
                // 删除历史表中   act_hi_identitylink数据
                wfFlowableTaskMapper.deleteHiIdentityLinkForTaskId(taskEntity.getId());
                // 删除运行中的任务 act_ru_task
                wfFlowableTaskMapper.deleteTaskForTaskId(taskEntity.getId());
                // 删除历史的任务  act_hi_taskinst
                wfFlowableTaskMapper.deleteHisTaskForTaskId(taskEntity.getId());
                // 删除与当前任务执行有关的参数   act_ru_variable
                wfFlowableTaskMapper.deleteRuVariableForExecutionId(taskEntity.getExecutionId());
                // 删除与当前历史任务执行有关的参数 act_hi_varinst
                wfFlowableTaskMapper.deleteHiVariableForExecutionId(taskEntity.getExecutionId());
                // 删除运行中的执行实例   act_ru_execution
                wfFlowableTaskMapper.deleteExecutionForExecutionId(taskEntity.getExecutionId());
                // 删除表单内容
                wfTaskFormService.delTaskFormByTaskId(taskEntity.getId());
            } else if (bo.getProcessStatus().equals(ProcessStatus.COMPLETED.getStatus())) {
                // 删除已完成的任务
                historyService.deleteHistoricTaskInstance(bo.getTaskId());
                // 删除历史活动绑定的任务信息
                wfActHiActinstMapper.deleteToHiActinstForTaskId(bo.getTaskId());
                // 删除历史意见信息
                wfCommentInfoService.deleteToCommentForTaskId(bo.getTaskId());
                // 删除表单内容
                wfTaskFormService.delTaskFormByTaskId(bo.getTaskId());
            }
            return true;
        }
        return false;
    }

    /**
     * @return boolean 处理成功返回 true，未传实例列表时返回 false
     * @description 逐条判断流程实例是否仍在运行，未结束的先终止，随后统一批量删除历史实例与关联业务数据
     * @params [bo] 任务参数，含 procInsIdList
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean abolishProcess(WfTaskBo bo) {
        // 校验流程是否结束
        if (CollUtil.isNotEmpty(bo.getProcInsIdList())) {
            for (String procInsId : bo.getProcInsIdList()) {
                long activeInsCount =
                        runtimeService
                                .createProcessInstanceQuery()
                                .processInstanceId(procInsId)
                                .active()
                                .count();
                //流程未结束
                if (activeInsCount > 0) {
                    //终止流程
                    wfStopProcInstService.stopProcess(bo);
                }
            }
            //在进行删除流程
            String[] ids = (String[]) bo.getProcInsIdList().toArray(new String[bo.getProcInsIdList().size()]);
            this.deleteProcessByIds(ids);
            return true;
        }
        return false;
    }
}
