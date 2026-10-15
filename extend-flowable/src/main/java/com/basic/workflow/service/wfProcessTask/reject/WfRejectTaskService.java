package com.basic.workflow.service.wfProcessTask.reject;

import cn.hutool.core.util.ObjectUtil;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.flowable.common.enums.ProcessStatus;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanbin
 * @description TODO (Flowable拒绝任务)
 * @date 2024/03/01
 */
@Service
public class WfRejectTaskService extends WfProcessTaskBase {
    /**
     * @return boolean
     * @description TODO (拒绝任务)
     * @params [taskBo]
     * @author yanbin
     * @date 2024/3/1 17:17
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean taskReject(WfTaskBo taskBo) {
        //TODO 1. 当前任务 task
        Task task = taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        if (ObjectUtil.isNull(task)) {
            throw new RuntimeException("获取任务信息异常！");
        }
        if (task.isSuspended()) {
            throw new RuntimeException("任务处于挂起状态");
        }
        //TODO 2.  获取流程实例
        ProcessInstance processInstance =
                runtimeService
                        .createProcessInstanceQuery()
                        .processInstanceId(taskBo.getProcInsId())
                        .singleResult();
        if (processInstance == null) {
            throw new RuntimeException("流程实例不存在，请确认！");
        }
        //TODO 3. 获取流程定义信息
        ProcessDefinition processDefinition =
                repositoryService
                        .createProcessDefinitionQuery()
                        .processDefinitionId(task.getProcessDefinitionId())
                        .singleResult();
        //TODO 4.1 添加审批意见
        this.addComment(
                taskBo.getTaskId(), taskBo.getCurrentUserId(), taskBo.getProcInsId(), FlowComment.REJECT.getType(), taskBo.getComment());
        //TODO 4.2 保存表单内容
        this.saveTaskForForm(task.getId(), taskBo.getProcInsId(), taskBo.getCurrentUserId(),
                task.getTaskDefinitionKey(), taskBo.getFormData());
        //TODO 5. 设置流程状态为已终结
        runtimeService.setVariable(
                processInstance.getId(),
                ProcessConstants.PROCESS_STATUS_KEY,
                ProcessStatus.REJECT.getStatus());
        //TODO 6. 获取所有节点信息
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        //TODO 6.1 获取结束节点
        EndEvent endEvent = ModelUtils.getEndEvent(bpmnModel);
        //TODO 6.2 获取正在运行的执行实例信息  Execution
        List<Execution> executions =
                runtimeService.createExecutionQuery().parentId(task.getProcessInstanceId()).list();
        //TODO 6.3 筛选出执行实例id
        List<String> executionIds =
                executions.stream().map(Execution::getId).collect(Collectors.toList());
        //TODO 6.4 终止流程
        runtimeService
                .createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveExecutionsToSingleActivityId(executionIds, endEvent.getId())
                .changeState();
        //TODO 7.保存上传的附件
        if (StringUtils.isNotEmpty(taskBo.getFileList())) {
            wfFlowableTaskService.saveFileToModel(taskBo, task.getTaskDefinitionKey());
        }
        //TODO 8. 处理抄送用户
        if (!this.makeCopy(taskBo)) {
            throw new RuntimeException("抄送任务失败");
        }
        return true;
    }
}
