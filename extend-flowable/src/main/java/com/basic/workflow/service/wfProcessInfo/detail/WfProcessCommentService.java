package com.basic.workflow.service.wfProcessInfo.detail;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.basic.common.core.domain.entity.SysUser;
import com.basic.common.utils.DateUtils;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.WfCommentInfo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import java.util.List;
import java.util.Objects;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;

/**
 * @author yanbin
 * @description TODO (Flowable流程历史意见信息)
 * @date 2024/03/04
 */
@Service
public class WfProcessCommentService extends WfProcessInfoBase {
    /**
     * @return java.util.List<com.basic.workflow.domain.WfCommentInfo>
     * @description TODO 根据实例procInsId获取流程流转记录(审批意见)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/4 10:50
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfCommentInfo> getProcessCommentInfo(String procInsId) {
        if (StringUtils.isBlank(procInsId)) {
            throw new RuntimeException("相关流程查询失败");
        }
        List<WfCommentInfo> wfCommentInfoList = wfCommentInfoService.selectProcessCommentForProcInsId(procInsId);
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        if (historicProcessInstance == null) {
            throw new RuntimeException("未查询到相关流程信息");
        }
        for (WfCommentInfo wfCommentInfo : wfCommentInfoList) {
            //TODO  查找节点
            FlowNode flowNode = wfBpmnModelService.findFlowNodeByActivityId(historicProcessInstance.getProcessDefinitionId(), wfCommentInfo.getActivityId());
            if (flowNode != null) {
                if (flowNode.getClass() == StartEvent.class) {
                    wfCommentInfo.setActivityType("startEvent");
                } else if (flowNode.getClass() == UserTask.class) {
                    wfCommentInfo.setActivityType("userTask");
                }
            }
            //TODO 设置处理人信息
            if (StringUtils.isNotEmpty(wfCommentInfo.getUserId())){
                wfCommentInfo.setUserName(sysUserService.selectNickNameById(Long.valueOf(wfCommentInfo.getUserId())));
            }
            //TODO 计算耗时
            if (StringUtils.isNotBlank(wfCommentInfo.getTaskId())) {
                HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(wfCommentInfo.getTaskId()).singleResult();
                if (historicTaskInstance != null && Objects.nonNull(
                    historicTaskInstance.getEndTime())) {
                    wfCommentInfo.setEndTime(historicTaskInstance.getEndTime());
                    wfCommentInfo.setDuration(DateUtils.getDatePoor(historicTaskInstance.getEndTime(), historicTaskInstance.getStartTime()));
                }
            }
        }
        //TODO 判断流程是否结束
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(procInsId);
        if (processInstance != null) {
            //TODO 添加待审核节点
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
            if (CollectionUtils.isNotEmpty(taskList)) {
                for (Task task : taskList) {
                    WfCommentInfo wfCommentInfo = new WfCommentInfo();
                    UserTask userTask = wfBpmnModelService.findUserTaskForProcDefIdAndKey(extendProcinst.getProcDefId(), task.getTaskDefinitionKey());
                    wfCommentInfo.setType("100");
                    wfCommentInfo.setActivityId(task.getTaskDefinitionKey());
                    wfCommentInfo.setActivityName(task.getName());
                    if (StringUtils.isNotBlank(task.getAssignee())) {
                        wfCommentInfo.setUserName(sysUserService.selectNickNameById(Long.valueOf(task.getAssignee())));
                    } else if (CollectionUtils.isNotEmpty(userTask.getCandidateUsers())) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String userId : userTask.getCandidateUsers()) {
                            try {
                                stringBuilder.append(sysUserService.selectNickNameById(Long.valueOf(userId))).append(",");
                            } catch (NumberFormatException e) {
                            }
                        }
                        wfCommentInfo.setUserName(stringBuilder.toString().substring(0, stringBuilder.length() - 1));
                    } else if (CollectionUtils.isNotEmpty(userTask.getCandidateGroups())) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String groupId : userTask.getCandidateGroups()) {
                            List<SysUser> sysUsers = sysUserService.selectRoleToUser(Long.valueOf(groupId));
                            for (SysUser sysUser : sysUsers) {
                                stringBuilder.append(sysUser.getNickName()).append(",");
                            }
                        }
                        wfCommentInfo.setUserName(stringBuilder.toString().substring(0, stringBuilder.length() - 1));
                    }
                    wfCommentInfo.setCreateTime(task.getCreateTime());
                    wfCommentInfoList.add(wfCommentInfo);
                }
            }
        }
        //TODO 添加结束节点信息
        else {
            BpmnModel bpmnModel = wfBpmnModelService.getBpmnModelByProcessDefId(historicProcessInstance.getProcessDefinitionId());
            EndEvent endEvent = ModelUtils.getEndEvent(bpmnModel);
            //查找节点活动
            HistoricActivityInstance historicActivityInstance = historyService.createHistoricActivityInstanceQuery().activityId(endEvent.getId()).processInstanceId(historicProcessInstance.getId()).singleResult();
            WfCommentInfo wfCommentInfo = new WfCommentInfo();
            wfCommentInfo.setActivityType("endEvent");
            wfCommentInfo.setActivityName("结束");
            if (StringUtils.isNotBlank(endEvent.getName())) {
                wfCommentInfo.setActivityName(endEvent.getName());
            }
            //结束节点为null可能是在定时任务中产生(目前遇到的是这样)
            if (historicActivityInstance != null) {
                wfCommentInfo.setActivityId(endEvent.getId());
                wfCommentInfo.setCreateTime(historicActivityInstance.getStartTime());
            }
            wfCommentInfo.setEndTime(historicProcessInstance.getEndTime());
            wfCommentInfo.setType("13");
            wfCommentInfoList.add(wfCommentInfo);
        }
        return wfCommentInfoList;
    }
}
