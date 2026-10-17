package com.basic.workflow.service.wfProcessTask.skip;

import com.basic.common.utils.StringUtils;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

/**
 * @description 任务跳转服务，把流程实例从当前节点直接迁移到指定节点并记录跳转意见
 * @author yanbin
 * @date 2024/05/28
 */
@Service
public class WfSkipTaskService extends WfProcessTaskBase {
    /**
     * @return Boolean 跳转成功返回 true，未指定目标节点或流程实例不存在时返回 false
     * @description 把流程实例从当前节点直接迁移到指定节点，随后生成跳转审批意见、保存自定义表单与附件并处理抄送
     * @params [taskBo] 任务参数，含 procInsId、taskId（发起时可空）、skipActivityId、意见与表单数据
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public Boolean skipUserTask(WfTaskBo taskBo) {
        if (StringUtils.isBlank(taskBo.getSkipActivityId())) {
            return false;
        }
        // 1.获取实例信息
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(taskBo.getProcInsId()).singleResult();
        Task task = null;
        // 这里发生只有发起的时候没有taskId的情况
        if (StringUtils.isNotBlank(taskBo.getTaskId())) {
            // 2.获取当前任务信息
            task = taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        } else {
            task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list().get(0);
        }
        // 发起流程时直接结束
        if (processInstance == null) {
            return false;
        }
        // 3.执行跳转
        runtimeService.createChangeActivityStateBuilder().processInstanceId(processInstance.getId()).moveActivityIdTo(task.getTaskDefinitionKey(), taskBo.getSkipActivityId()).changeState();
        // 4.生成审批意见
        this.addComment(task.getId(), taskBo.getCurrentUserId(), taskBo.getProcInsId(), FlowComment.SKIP.getType(), taskBo.getComment());
        // 5.保存自定义表单内容
        this.saveTaskForForm(taskBo.getTaskId(), taskBo.getProcInsId(), taskBo.getCurrentUserId(), task.getTaskDefinitionKey(), taskBo.getFormData());
        // 6.保存上传的附件
        if (StringUtils.isNotEmpty(taskBo.getFileList())) {
            wfFlowableTaskService.saveFileToModel(taskBo, task.getTaskDefinitionKey());
        }
        // 7.处理抄送用户
        if (!this.makeCopy(taskBo)) {
            throw new RuntimeException("抄送任务失败");
        }
        return true;
    }
}
