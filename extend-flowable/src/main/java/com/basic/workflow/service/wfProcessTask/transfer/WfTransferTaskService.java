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
 * @description 转办任务服务，把当前任务的处理人变更为指定人员，并记录转办意见、表单与附件
 * @author yanbin
 * @date 2024/03/01
 */
@Service
public class WfTransferTaskService extends WfProcessTaskBase {
    /**
     * @return boolean 转办成功返回 true
     * @description 把当前任务转办给指定处理人，先校验任务存在并记录「原处理人->新处理人」意见与表单，
     * 再把拥有者设为当前登录人、处理人换成目标人员并标记任务描述，最后保存附件与抄送
     * @params [bo] 任务业务对象，携带任务 ID、当前用户 ID、目标处理人、意见与表单附件数据
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean transferTask(WfTaskBo bo) {
        // 1. 当前任务 task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        if (ObjectUtil.isEmpty(task)) {
            throw new ServiceException("获取任务失败！");
        }
        // 2.1 添加审批意见message
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
        // 2.2 添加审批意见
        this.addComment(
                task.getId(), bo.getCurrentUserId(), task.getProcessInstanceId(), FlowComment.TRANSFER.getType(), commentBuilder.toString());
        // 2.3 保存表单内容
        this.saveTaskForForm(task.getId(), bo.getProcInsId(), bo.getCurrentUserId(),
                task.getTaskDefinitionKey(), bo.getFormData());
        // 3.1 设置拥有者为当前登录人   act_ru_task
        taskService.setOwner(bo.getTaskId(), bo.getCurrentUserId());
        // 3.2 转办任务  设置处理人是选中的待处理人
        taskService.setAssignee(bo.getTaskId(), bo.getAssignee());
        // 3.3更新任务的描述  修改为转办任务
        wfFlowableTaskMapper.updateTaskDescription(task.getId(), FlowConstant.TRANSFER);
        // 4.保存上传的附件
        if (StringUtils.isNotEmpty(bo.getFileList())) {
            wfFlowableTaskService.saveFileToModel(bo, task.getTaskDefinitionKey());
        }
        // 设置任务节点名称
        bo.setTaskName(task.getName());
        // 5.处理抄送用户
        if (!this.makeCopy(bo)) {
            throw new RuntimeException("抄送任务失败");
        }
        return true;
    }
}
