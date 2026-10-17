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
 * @description 拒绝任务服务，写入审批意见与表单数据后把流程状态置为已拒绝并驱动执行实例到结束节点
 * @author yanbin
 * @date 2024/03/01
 */
@Service
public class WfRejectTaskService extends WfProcessTaskBase {
    /**
     * @return boolean 拒绝成功返回 true
     * @description 拒绝当前任务：校验任务与流程实例存在且未挂起，写入审批意见与表单数据，把流程状态置为已拒绝并驱动执行实例跳到结束节点，同时保存附件与处理抄送
     * @params [taskBo] 任务业务对象，含任务id、流程实例id、处理人、审批意见、表单数据与附件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean taskReject(WfTaskBo taskBo) {
        // 1. 当前任务 task
        Task task = taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        if (ObjectUtil.isNull(task)) {
            throw new RuntimeException("获取任务信息异常！");
        }
        if (task.isSuspended()) {
            throw new RuntimeException("任务处于挂起状态");
        }
        // 2. 获取流程实例
        ProcessInstance processInstance =
                runtimeService
                        .createProcessInstanceQuery()
                        .processInstanceId(taskBo.getProcInsId())
                        .singleResult();
        if (processInstance == null) {
            throw new RuntimeException("流程实例不存在，请确认！");
        }
        // 3. 获取流程定义信息
        ProcessDefinition processDefinition =
                repositoryService
                        .createProcessDefinitionQuery()
                        .processDefinitionId(task.getProcessDefinitionId())
                        .singleResult();
        // 4.1 添加审批意见
        this.addComment(
                taskBo.getTaskId(), taskBo.getCurrentUserId(), taskBo.getProcInsId(), FlowComment.REJECT.getType(), taskBo.getComment());
        // 4.2 保存表单内容
        this.saveTaskForForm(task.getId(), taskBo.getProcInsId(), taskBo.getCurrentUserId(),
                task.getTaskDefinitionKey(), taskBo.getFormData());
        // 5. 设置流程状态为已终结
        runtimeService.setVariable(
                processInstance.getId(),
                ProcessConstants.PROCESS_STATUS_KEY,
                ProcessStatus.REJECT.getStatus());
        // 6. 获取所有节点信息
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        // 6.1 获取结束节点
        EndEvent endEvent = ModelUtils.getEndEvent(bpmnModel);
        // 6.2 获取正在运行的执行实例信息  Execution
        List<Execution> executions =
                runtimeService.createExecutionQuery().parentId(task.getProcessInstanceId()).list();
        // 6.3 筛选出执行实例id
        List<String> executionIds =
                executions.stream().map(Execution::getId).collect(Collectors.toList());
        // 6.4 终止流程
        runtimeService
                .createChangeActivityStateBuilder()
                .processInstanceId(task.getProcessInstanceId())
                .moveExecutionsToSingleActivityId(executionIds, endEvent.getId())
                .changeState();
        // 7.保存上传的附件
        if (StringUtils.isNotEmpty(taskBo.getFileList())) {
            wfFlowableTaskService.saveFileToModel(taskBo, task.getTaskDefinitionKey());
        }
        // 8. 处理抄送用户
        if (!this.makeCopy(taskBo)) {
            throw new RuntimeException("抄送任务失败");
        }
        return true;
    }
}
