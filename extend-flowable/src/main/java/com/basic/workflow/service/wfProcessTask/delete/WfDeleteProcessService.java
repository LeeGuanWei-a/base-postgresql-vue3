package com.basic.workflow.service.wfProcessTask.delete;

import com.basic.common.exception.ServiceException;
import com.basic.flowable.common.enums.ProcessStatus;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import com.basic.workflow.service.wfProcessTask.stop.WfStopProcInstService;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author yanbin
 * @description TODO (Flowable删除流程实例，删除所选任务)
 * @date 2024/03/01
 */
@Service
public class WfDeleteProcessService extends WfProcessTaskBase {
    @Autowired
    private WfStopProcInstService wfStopProcInstService;

    /**
     * @return java.lang.Boolean
     * @description TODO (删除流程实例)
     * @params [instanceIds]
     * @author yanbin
     * @date 2024/3/1 16:58
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
     * @return boolean
     * @description TODO (删除所选任务)
     * @params [bo]
     * @author yanbin
     * @date 2024/3/1 17:03
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
                    //TODO 判断是否是串行并行 true是顺序执行 false不是
                    Boolean isSequential = extensionElementsService.getMulTIinstSequentialForKey(taskEntity.getProcessDefinitionId()
                            , taskEntity.getTaskDefinitionKey());
                    //TODO 顺序执行 一条一条的生成多实例
                    if (isSequential) {
                        //TODO 修改参数表act_ru_variable ,act_hi_varinst
                        //TODO  如果是多实例节点  总实例数-1 act_ru_variable
                        wfFlowableTaskMapper.updateRuVariable(taskEntity.getProcessInstanceId(), "nrOfInstances");
                        //TODO  还未完成的实例数-1 act_ru_variable
                        wfFlowableTaskMapper.updateRuVariable(taskEntity.getProcessInstanceId(), "nrOfActiveInstances");
                        //TODO  如果是多实例节点  总实例数-1 act_hi_varinst
                        wfFlowableTaskMapper.updateHisVariable(taskEntity.getProcessInstanceId(), "nrOfInstances");
                        //TODO  还未完成的实例数-1 act_hi_varinst
                        wfFlowableTaskMapper.updateHisVariable(taskEntity.getProcessInstanceId(), "nrOfActiveInstances");
                    }
                    //TODO 非一条一条生成
                    else {
                        //TODO  如果是多实例节点  总实例数-1 act_ru_variable
                        wfFlowableTaskMapper.updateRuVariable(taskEntity.getProcessInstanceId(), "nrOfInstances");
                        //TODO  如果是多实例节点  总实例数-1 act_hi_varinst
                        wfFlowableTaskMapper.updateHisVariable(taskEntity.getProcessInstanceId(), "nrOfInstances");
//                        //TODO  删除生成的多实例数 记录act_ru_variable
//                        wfFlowableTaskMapper.deleteRuVariableForExecutionIdAndName(taskEntity.getExecutionId(), "loopCounter");
//                        //TODO  删除生成的处理人信息 记录act_ru_variable
//                        wfFlowableTaskMapper.deleteRuVariableForExecutionIdAndName(taskEntity.getExecutionId(), "assignee");
//                        //TODO  删除生成的多实例数 act_hi_varinst
//                        wfFlowableTaskMapper.deleteHisVariableForExecutionIdAndName(taskEntity.getExecutionId(), "loopCounter");
//                        //TODO  删除生成的处理人信息 act_hi_varinst
//                        wfFlowableTaskMapper.deleteHisVariableForExecutionIdAndName(taskEntity.getExecutionId(), "assignee");
                    }
                }
                //TODO 删除历史活动绑定的任务信息 act_hi_actinst
                wfActHiActinstMapper.deleteToHiActinstForTaskId(taskEntity.getId());
                //TODO 删除活动绑定的任务信息  act_ru_actinst
                wfActRuActinstMapper.deleteToRuActinstForTaskId(taskEntity.getId());
                //TODO 删除运行中的   act_ru_identitylink
                wfFlowableTaskMapper.deleteRuIdentityLinkForTaskId(taskEntity.getId());
                //TODO 删除历史表中   act_hi_identitylink数据
                wfFlowableTaskMapper.deleteHiIdentityLinkForTaskId(taskEntity.getId());
                //TODO 删除运行中的任务 act_ru_task
                wfFlowableTaskMapper.deleteTaskForTaskId(taskEntity.getId());
                //TODO 删除历史的任务  act_hi_taskinst
                wfFlowableTaskMapper.deleteHisTaskForTaskId(taskEntity.getId());
                //TODO 删除与当前任务执行有关的参数   act_ru_variable
                wfFlowableTaskMapper.deleteRuVariableForExecutionId(taskEntity.getExecutionId());
                //TODO 删除与当前历史任务执行有关的参数 act_hi_varinst
                wfFlowableTaskMapper.deleteHiVariableForExecutionId(taskEntity.getExecutionId());
                //TODO 删除运行中的执行实例   act_ru_execution
                wfFlowableTaskMapper.deleteExecutionForExecutionId(taskEntity.getExecutionId());
                //TODO 删除表单内容
                wfTaskFormService.delTaskFormByTaskId(taskEntity.getId());
            } else if (bo.getProcessStatus().equals(ProcessStatus.COMPLETED.getStatus())) {
                //TODO 删除已完成的任务
                historyService.deleteHistoricTaskInstance(bo.getTaskId());
                //TODO 删除历史活动绑定的任务信息
                wfActHiActinstMapper.deleteToHiActinstForTaskId(bo.getTaskId());
                //TODO 删除历史意见信息
                wfCommentInfoService.deleteToCommentForTaskId(bo.getTaskId());
                //TODO 删除表单内容
                wfTaskFormService.delTaskFormByTaskId(bo.getTaskId());
            }
            return true;
        }
        return false;
    }

    /**
     * @return boolean
     * @description TODO (废除所选流程)
     * @params [bo]
     * @author yanbin
     * @date 2024/2/22 9:00
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean abolishProcess(WfTaskBo bo) {
        // 校验流程是否结束
        if (CollectionUtils.isNotEmpty(bo.getProcInsIdList())) {
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
