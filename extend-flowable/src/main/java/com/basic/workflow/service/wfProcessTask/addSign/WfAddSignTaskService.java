package com.basic.workflow.service.wfProcessTask.addSign;

import com.basic.common.utils.StringUtils;
import com.basic.constant.FlowConstant;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (Flowable加签任务)
 * @date 2024/03/01
 */
@Service
public class WfAddSignTaskService extends WfProcessTaskBase {

   /**
    * @description TODO (加签任务)
    * @params [bo, userCode, sign]
    * @return java.lang.Boolean
    * @author yanbin
    * @date 2024/3/7 17:24
    * @remark (改代码时写明修改时间修改人修改原因)
    */

    public Boolean addSignTask(WfTaskBo bo, List<String> userCode, String sign) {
        //TODO 1.获取当前任务
        TaskEntityImpl taskEntity =
                (TaskEntityImpl) taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        // 返回的加签成功msg
        String message = null;
        // 判断当前节点任务是否为空
        if (taskEntity != null) {
            //TODO 2. 如果是加签再加签
            String parentTaskId = taskEntity.getParentTaskId();
            if (StringUtils.isBlank(parentTaskId)) {
                //TODO 2.1 将加签操作人员设置为任务拥有者
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
                //TODO 2.2 设置任务为空执行者
                taskService.saveTask(taskEntity);
            }
            //TODO 3.添加加签数据
            this.createSignSubTasks(bo, userCode, taskEntity);
            //TODO 4. 添加审批意见
            String type = null;
            if (sign.equals("after")) {
                type = FlowComment.HJQ.getType();
                message = "后加签成功";
            } else if (sign.equals("before")) {
                type = FlowComment.QJQ.getType();
                message = "前加签成功";
            }
            //TODO 5.保存上传的附件
            if (StringUtils.isNotEmpty(bo.getFileList())) {
                wfFlowableTaskService.saveFileToModel(bo, taskEntity.getTaskDefinitionKey());
            }
            //TODO 6.添加审批意见
            this.addComment(bo.getTaskId(), bo.getCurrentUserId(), bo.getProcInsId(), type, bo.getComment());
            //TODO 7保存表单内容
            this.saveTaskForForm(taskEntity.getId(), bo.getProcInsId(), bo.getCurrentUserId(),
                    taskEntity.getTaskDefinitionKey(), bo.getFormData());
        } else {
            return false;
        }
        return true;
    }

    /**
     * @return void
     * @description TODO (创建加签子任务)
     * @params [bo, userCode, taskEntity]
     * @author yanbin
     * @date 2024/3/1 15:49
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void createSignSubTasks(WfTaskBo bo, List<String> userCode, TaskEntityImpl taskEntity) {
        if (CollectionUtils.isNotEmpty(userCode)) {
            //TODO 1.获取父任务id
            String parentTaskId = taskEntity.getParentTaskId();
            if (StringUtils.isBlank(parentTaskId)) {
                parentTaskId = taskEntity.getId();
            }
            String finalParentTaskId = parentTaskId;
            //TODO 2.创建被加签人的任务列表
            userCode.forEach(
                    code -> {
                        if (StringUtils.isNotBlank(code)) {
                            wfFlowableTaskService.createSubTask(bo.getCurrentUserId(), taskEntity, finalParentTaskId, code);
                        }
                    });
            String taskId = taskEntity.getId();
            if (StringUtils.isBlank(taskEntity.getParentTaskId())) {
                //TODO 3.创建加签人的任务并执行完毕
                Task task = wfFlowableTaskService.createSubTask(bo.getCurrentUserId(), taskEntity,
                        finalParentTaskId, bo.getCurrentUserId());
                taskId = task.getId();
            }
            Task taskInfo = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (null != taskInfo) {
                taskService.complete(taskId);
            }
            //TODO 4. 如果是候选人，需要删除运行时候选表种的数据。
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
