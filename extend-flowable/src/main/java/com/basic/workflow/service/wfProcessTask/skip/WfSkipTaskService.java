package com.basic.workflow.service.wfProcessTask.skip;

import com.basic.common.utils.StringUtils;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

/**
 * @author yanbin
 * @description TODO (跳转任意节点实现)
 * @date 2024/05/28
 */
@Service
public class WfSkipTaskService extends WfProcessTaskBase {
    /**
     * @return java.lang.Boolean
     * @description TODO (跳转任意节点)
     * @params [taskBo]
     * @author yanbin
     * @date 2024/5/28 15:38
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public Boolean skipUserTask(WfTaskBo taskBo) {
        if (StringUtils.isBlank(taskBo.getSkipActivityId())) {
            return false;
        }
        //TODO 1.获取实例信息
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(taskBo.getProcInsId()).singleResult();
        Task task = null;
        //TODO 这里发生只有发起的时候没有taskId的情况
        if (StringUtils.isNotBlank(taskBo.getTaskId())) {
            //TODO 2.获取当前任务信息
            task = taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        } else {
            task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list().get(0);
        }
        //TODO 发起流程时直接结束
        if (processInstance == null) {
            return false;
        }
        //TODO 3.执行跳转
        runtimeService.createChangeActivityStateBuilder().processInstanceId(processInstance.getId()).moveActivityIdTo(task.getTaskDefinitionKey(), taskBo.getSkipActivityId()).changeState();
        //TODO 4.生成审批意见
        this.addComment(task.getId(), taskBo.getCurrentUserId(), taskBo.getProcInsId(), FlowComment.SKIP.getType(), taskBo.getComment());
        //TODO 5.保存自定义表单内容
        this.saveTaskForForm(taskBo.getTaskId(), taskBo.getProcInsId(), taskBo.getCurrentUserId(), task.getTaskDefinitionKey(), taskBo.getFormData());
        //TODO 6.保存上传的附件
        if (StringUtils.isNotEmpty(taskBo.getFileList())) {
            wfFlowableTaskService.saveFileToModel(taskBo, task.getTaskDefinitionKey());
        }
        //TODO 7.处理抄送用户
        if (!this.makeCopy(taskBo)) {
            throw new RuntimeException("抄送任务失败");
        }
        return true;
    }
}
