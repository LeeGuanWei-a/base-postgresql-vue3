package com.basic.workflow.service.wfProcessTask.addSign;

import com.basic.common.utils.StringUtils;
import com.basic.constant.FlowConstant;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import cn.hutool.core.collection.CollUtil;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description 加签任务服务，按前加签或后加签的方式为当前任务创建被加签人的子任务并写入审批意见
 * @author yanbin
 * @date 2024/03/01
 */
@Service
public class WfAddSignTaskService extends WfProcessTaskBase {

    /**
     * @return Boolean 加签结果，任务不存在时返回 false
     * @description 把当前任务的受理人改成任务拥有者并置空执行人，按前后加签标识置上范围类型，再创建被加签人的子任务、写审批意见、保存附件与表单内容
     * @params [bo, userCode, sign] 任务操作参数，含任务 ID、流程实例 ID、当前用户、意见、附件与表单数据；被加签人的用户编码列表；加签位置，after 为后加签、before 为前加签
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */

    public Boolean addSignTask(WfTaskBo bo, List<String> userCode, String sign) {
        // 1.获取当前任务
        TaskEntityImpl taskEntity =
                (TaskEntityImpl) taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        // 返回的加签成功msg
        String message = null;
        // 判断当前节点任务是否为空
        if (taskEntity != null) {
            // 2. 如果是加签再加签
            String parentTaskId = taskEntity.getParentTaskId();
            if (StringUtils.isBlank(parentTaskId)) {
                // 2.1 将加签操作人员设置为任务拥有者
                taskEntity.setOwner(bo.getCurrentUserId());
                taskEntity.setAssignee(null);
                taskEntity.setCountEnabled(true);
                // 后加签
                if (sign.equals("after")) {
                    taskEntity.setScopeType(FlowConstant.AFTER_ADDSIGN);
                }
                // 前加签
                else if (sign.equals("before")) {
                    taskEntity.setScopeType(FlowConstant.BEFORE_ADDSIGN);
                }
                // 2.2 设置任务为空执行者
                taskService.saveTask(taskEntity);
            }
            // 3.添加加签数据
            this.createSignSubTasks(bo, userCode, taskEntity);
            // 4. 添加审批意见
            String type = null;
            if (sign.equals("after")) {
                type = FlowComment.HJQ.getType();
                message = "后加签成功";
            } else if (sign.equals("before")) {
                type = FlowComment.QJQ.getType();
                message = "前加签成功";
            }
            // 5.保存上传的附件
            if (StringUtils.isNotEmpty(bo.getFileList())) {
                wfFlowableTaskService.saveFileToModel(bo, taskEntity.getTaskDefinitionKey());
            }
            // 6.添加审批意见
            this.addComment(bo.getTaskId(), bo.getCurrentUserId(), bo.getProcInsId(), type, bo.getComment());
            // 7保存表单内容
            this.saveTaskForForm(taskEntity.getId(), bo.getProcInsId(), bo.getCurrentUserId(),
                    taskEntity.getTaskDefinitionKey(), bo.getFormData());
        } else {
            return false;
        }
        return true;
    }

    /**
     * @description 为每个被加签人创建子任务，首次加签时额外建一个当前用户的任务并直接办结，最后清掉当前用户在候选表里的数据
     * @params [bo, userCode, taskEntity] 任务操作参数，取当前用户与流程实例 ID；被加签人的用户编码列表，为空时不处理；被加签的当前任务实体
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void createSignSubTasks(WfTaskBo bo, List<String> userCode, TaskEntityImpl taskEntity) {
        if (CollUtil.isNotEmpty(userCode)) {
            // 1.获取父任务id
            String parentTaskId = taskEntity.getParentTaskId();
            if (StringUtils.isBlank(parentTaskId)) {
                parentTaskId = taskEntity.getId();
            }
            String finalParentTaskId = parentTaskId;
            // 2.创建被加签人的任务列表
            userCode.forEach(
                    code -> {
                        if (StringUtils.isNotBlank(code)) {
                            wfFlowableTaskService.createSubTask(bo.getCurrentUserId(), taskEntity, finalParentTaskId, code);
                        }
                    });
            String taskId = taskEntity.getId();
            if (StringUtils.isBlank(taskEntity.getParentTaskId())) {
                // 3.创建加签人的任务并执行完毕
                Task task = wfFlowableTaskService.createSubTask(bo.getCurrentUserId(), taskEntity,
                        finalParentTaskId, bo.getCurrentUserId());
                taskId = task.getId();
            }
            Task taskInfo = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (null != taskInfo) {
                taskService.complete(taskId);
            }
            // 4. 如果是候选人，需要删除运行时候选表种的数据。
            long candidateCount =
                    taskService
                            .createTaskQuery()
                            .taskId(parentTaskId)
                            .taskCandidateUser(bo.getCurrentUserId())
                            .count();
            List<Task> ss = taskService.createTaskQuery().processInstanceId(bo.getProcInsId()).list();
            if (candidateCount > 0) {
                taskService.deleteCandidateUser(parentTaskId, bo.getCurrentUserId());
            }
        }
    }
}
