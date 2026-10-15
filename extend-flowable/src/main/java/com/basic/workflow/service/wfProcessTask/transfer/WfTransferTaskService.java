package com.basic.workflow.service.wfProcessTask.transfer;

import cn.hutool.core.util.ObjectUtil;
import com.basic.common.exception.ServiceException;
import com.basic.common.utils.StringUtils;
import com.basic.constant.FlowConstant;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yanbin
 * @description TODO ( Flowable转办任务)
 * @date 2024/03/01
 */
@Service
public class WfTransferTaskService extends WfProcessTaskBase {
    /**
     * @return boolean
     * @description TODO (转办任务)
     * @params [bo]
     * @author yanbin
     * @date 2024/3/1 17:19
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean transferTask(WfTaskBo bo) {
        // TODO 1. 当前任务 task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        if (ObjectUtil.isEmpty(task)) {
            throw new ServiceException("获取任务失败！");
        }
        // TODO 2.1 添加审批意见message
        // currentUserName 可能为空（调用方没传）：StringBuilder(null) 会抛 NPE，这里兜底
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
                task.getId(), bo.getCurrentUserId(), task.getProcessInstanceId(), FlowComment.TRANSFER.getType(), commentBuilder.toString());
        //TODO 2.3 保存表单内容
        this.saveTaskForForm(task.getId(), bo.getProcInsId(), bo.getCurrentUserId(),
                task.getTaskDefinitionKey(), bo.getFormData());
        // TODO 3.1 设置拥有者为当前登录人   act_ru_task
        taskService.setOwner(bo.getTaskId(), bo.getCurrentUserId());
        // TODO 3.2 转办任务  设置处理人是选中的待处理人
        taskService.setAssignee(bo.getTaskId(), bo.getAssignee());
        //TODO 3.3更新任务的描述  修改为转办任务
        wfFlowableTaskMapper.updateTaskDescription(task.getId(), FlowConstant.TRANSFER);
        // TODO 4.保存上传的附件
        if (StringUtils.isNotEmpty(bo.getFileList())) {
            wfFlowableTaskService.saveFileToModel(bo, task.getTaskDefinitionKey());
        }
        // 设置任务节点名称
        bo.setTaskName(task.getName());
        // TODO 5.处理抄送用户
        if (!this.makeCopy(bo)) {
            throw new RuntimeException("抄送任务失败");
        }
        return true;
    }
}
