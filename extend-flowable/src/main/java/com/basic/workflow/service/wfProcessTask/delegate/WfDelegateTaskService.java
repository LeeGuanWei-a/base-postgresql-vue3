package com.basic.workflow.service.wfProcessTask.delegate;

import cn.hutool.core.util.ObjectUtil;
import com.basic.common.exception.ServiceException;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yanbin
 * @description TODO (Flowable委派任务)
 * @date 2024/03/01
 */
@Service
public class WfDelegateTaskService extends WfProcessTaskBase {
    /**
     * @return boolean
     * @description TODO (委派任务)
     * @params [bo]
     * @author yanbin
     * @date 2024/3/1 16:53
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delegateTask(WfTaskBo bo) {
        // TODO 1. 当前任务 task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        if (ObjectUtil.isEmpty(task)) {
            throw new ServiceException("没有运行时的任务实例,请确认!");
        }
        // TODO 2.1 添加审批意见message
        // 同 WfTransferTaskService：currentUserName 为空时不能让 StringBuilder(null) 抛 NPE
        String currentUserName = ObjectUtil.isEmpty(bo.getCurrentUserName()) ? "" : bo.getCurrentUserName();
        StringBuilder commentBuilder =
                new StringBuilder(currentUserName).append("->");
        String nickName = sysUserService.selectNickNameById(Long.valueOf(bo.getAssignee()));
        if (StringUtils.isNotBlank(nickName)) {
            commentBuilder.append(nickName);
        } else {
            commentBuilder.append(bo.getAssignee());
        }
        if (StringUtils.isNotBlank(bo.getComment())) {
            commentBuilder.append(": ").append(bo.getComment());
        }
        // TODO 2.2 添加审批意见
        this.addComment(
                task.getId(), bo.getCurrentUserId(), task.getProcessInstanceId(), FlowComment.DELEGATE.getType(), commentBuilder.toString());
        //TODO 2.3 保存表单内容
        this.saveTaskForForm(task.getId(), bo.getProcInsId(), bo.getCurrentUserId(),
                task.getTaskDefinitionKey(), bo.getFormData());
        //TODO  进行其他操作导致owner有人员信息，需要清除
        taskService.setOwner(bo.getTaskId(), null);
        // TODO 3. 设置审批人就是当前处理人
        taskService.setAssignee(bo.getTaskId(), bo.getCurrentUserId());
        // TODO 4. 执行委派
        taskService.delegateTask(bo.getTaskId(), bo.getAssignee());
        // TODO 5.保存上传的附件
        if (StringUtils.isNotEmpty(bo.getFileList())) {
            wfFlowableTaskService.saveFileToModel(bo, task.getTaskDefinitionKey());
        }
        // 设置任务节点名称
        bo.setTaskName(task.getName());
        // TODO 6. 处理抄送用户
        if (!this.makeCopy(bo)) {
            throw new RuntimeException("抄送任务失败");
        }
        return true;
    }
}
