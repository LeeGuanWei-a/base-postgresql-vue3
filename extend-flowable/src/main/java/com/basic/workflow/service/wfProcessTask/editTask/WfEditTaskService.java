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
 * @description 任务信息修改服务，支持改审批意见与变更任务处理人
 * @author yanbin
 * @date 2024/03/01
 */
@Service
public class WfEditTaskService extends WfProcessTaskBase {
    /**
     * @return boolean 修改成功返回 true
     * @description 先删掉该任务在 Flowable 表中的历史意见，再通过自定义 cmd 写入新意见，并同步更新业务拓展表中的意见信息
     * @params [bo] 任务业务对象，提供任务id、处理人、流程实例id与新的审批意见
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean updateTaskComment(WfTaskBo bo) {
        CommentVo comment = new CommentVo(bo.getTaskId(), bo.getCurrentUserId(),
                bo.getProcInsId(), FlowComment.NORMAL.getType(), bo.getComment());
        // 修改Flowable表的意见信息
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
        // 修改拓展表的意见信息
        wfCommentInfoService.updateTaskComment(bo.getTaskId(), bo.getCurrentUserId(), bo.getComment());
        return true;
    }

    /**
     * @return boolean 变更成功返回 true，流程状态既非 running 也非 completed 时返回 false
     * @description 按流程状态分别变更处理人：运行中任务先清理候选人与候选组再重设处理人，已完成任务改写历史身份关联与历史任务的处理人
     * @params [bo] 任务业务对象，提供任务id、流程状态与新的处理人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean editProcessTaskAssignee(WfTaskBo bo) {
        String processStatus = bo.getProcessStatus();
        if (processStatus != null) {
            // 任务状态
            if (processStatus.equals("running")) {
                Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
                List<HistoricIdentityLink> historicIdentityLinkList =
                        historyService.getHistoricIdentityLinksForTask(task.getId());
                for (HistoricIdentityLink link : historicIdentityLinkList) {
                    // 候选人员
                    if (link.getType().equals("candidate") && link.getUserId() != null) {
                        //删除候选人员
                        taskService.deleteCandidateUser(task.getId(), link.getUserId());
                    }
                    // 候选角色
                    else if (link.getType().equals("candidate") && link.getGroupId() != null) {
                        //删除候选角色组
                        taskService.deleteCandidateGroup(task.getId(), link.getGroupId());
                    } // 任务处理人
                    else if (link.getType().equals("assignee") && link.getUserId() != null) {
                        //将任务处理人 设置为0这个人是不存在的
                        taskService.setAssignee(task.getId(), "0");
                    }
                }
                // 这里直接设置任务的处理人是选中的这个人
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
     * @return boolean 保存后固定返回 true
     * @description 把任务附件信息保存到指定节点的流程模型中
     * @params [taskBo] 任务业务对象，含附件列表与目标节点 key
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean saveFileToModel(WfTaskBo taskBo) {
        wfFlowableTaskService.saveFileToModel(taskBo, taskBo.getTargetKey());
        return true;
    }

    /**修改表单内容*/
}
