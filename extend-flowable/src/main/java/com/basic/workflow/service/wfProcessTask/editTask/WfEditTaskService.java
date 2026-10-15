package com.basic.workflow.service.wfProcessTask.editTask;

import com.basic.flowable.common.enums.FlowComment;
import com.basic.flowable.utils.UUIDGenerator;
import com.basic.workflow.cmd.AddHisCommentCmd;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.vo.CommentVo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (Flowable修改任务信息)--->处理人，意见等
 * @date 2024/03/01
 */
@Service
public class WfEditTaskService extends WfProcessTaskBase {
    /**
     * @return boolean
     * @description TODO (修改审批意见)
     * @params [bo]
     * @author yanbin
     * @date 2024/3/1 17:11
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean updateTaskComment(WfTaskBo bo) {
        CommentVo comment = new CommentVo(bo.getTaskId(), bo.getCurrentUserId(),
                bo.getProcInsId(), FlowComment.NORMAL.getType(), bo.getComment());
        //TODO 修改Flowable表的意见信息
        //删除原有的意见信息
        wfFlowableTaskMapper.deleteHistorcComment(bo.getTaskId());
        //添加新的意见信息
        managementService.executeCommand(
                new AddHisCommentCmd(
                        comment.getTaskId(),
                        comment.getUserId(),
                        comment.getProcessInstanceId(),
                        comment.getType(),
                        comment.getMessage()));
        //TODO  修改拓展表的意见信息
        wfCommentInfoService.updateTaskComment(bo.getTaskId(), bo.getCurrentUserId(), bo.getComment());
        return true;
    }

    /**
     * @return boolean
     * @description TODO (变更处理人)
     * @params [bo]
     * @author yanbin
     * @date 2024/3/1 17:12
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean editProcessTaskAssignee(WfTaskBo bo) {
        String processStatus = bo.getProcessStatus();
        if (processStatus != null) {
            //TODO 任务状态
            if (processStatus.equals("running")) {
                Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
                List<HistoricIdentityLink> historicIdentityLinkList =
                        historyService.getHistoricIdentityLinksForTask(task.getId());
                for (HistoricIdentityLink link : historicIdentityLinkList) {
                    //TODO 候选人员
                    if (link.getType().equals("candidate") && link.getUserId() != null) {
                        //删除候选人员
                        taskService.deleteCandidateUser(task.getId(), link.getUserId());
                    }
                    //TODO 候选角色
                    else if (link.getType().equals("candidate") && link.getGroupId() != null) {
                        //删除候选角色组
                        taskService.deleteCandidateGroup(task.getId(), link.getGroupId());
                    } //TODO 任务处理人
                    else if (link.getType().equals("assignee") && link.getUserId() != null) {
                        //将任务处理人 设置为0这个人是不存在的
                        taskService.setAssignee(task.getId(), "0");
                    }
                }
                //TODO 这里直接设置任务的处理人是选中的这个人
                taskService.setAssignee(task.getId(), bo.getAssignee());
                return true;
            } else if (processStatus.equals("completed")) {
                HistoricTaskInstance historicTaskInstance = historyService
                        .createHistoricTaskInstanceQuery().taskId(bo.getTaskId()).singleResult();
                List<HistoricIdentityLink> historicIdentityLinkList =
                        historyService.getHistoricIdentityLinksForTask(historicTaskInstance.getId());
                for (HistoricIdentityLink historicIdentityLink : historicIdentityLinkList) {
                    //删除历史处理人信息
                    wfFlowableTaskMapper.deleteHistorcIdentityLink(historicIdentityLink.getTaskId());
                }
                //添加新的处理人信息
                wfFlowableTaskMapper.inserHistorcIdentityLink(UUIDGenerator.generate(), historicTaskInstance.getId(), bo.getAssignee(), "assignee");
                //将任务的处理人变更
                wfFlowableTaskMapper.updateHistorcTask(historicTaskInstance.getId(), bo.getAssignee());
                return true;
            }
        }
        return false;
    }

    /**
     * @return int
     * @description TODO (修改附件信息)
     * @params [taskBo]
     * @author yanbin
     * @date 2024/2/23 11:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean saveFileToModel(WfTaskBo taskBo) {
        wfFlowableTaskService.saveFileToModel(taskBo, taskBo.getTargetKey());
        return true;
    }

    /**修改表单内容*/
}
