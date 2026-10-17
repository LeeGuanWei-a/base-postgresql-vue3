package com.basic.workflow.service.wfProcessInfo.detail;

import cn.hutool.core.collection.CollUtil;
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
import lombok.extern.slf4j.Slf4j;
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
 * @description 流程审批意见服务，按流程实例汇总历史意见并补齐节点类型、处理人昵称与办理耗时
 * @author yanbin
 * @date 2024/03/04
 */
@Slf4j
@Service
public class WfProcessCommentService extends WfProcessInfoBase {
    /**
     * @return List<WfCommentInfo> 流程流转记录，含历史审批意见，以及未结束时追加的待办节点、已结束时追加的结束节点
     * @description 按流程实例id取出审批意见并补齐节点类型、处理人昵称与办理耗时；流程未结束时追加待办节点，已结束时追加结束节点
     * @params [procInsId] 流程实例id，为空或查不到流程时抛异常
     * @author liguanwei
     * @date 2026/10/17 20:52
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
            // 查找节点
            FlowNode flowNode = wfBpmnModelService.findFlowNodeByActivityId(historicProcessInstance.getProcessDefinitionId(), wfCommentInfo.getActivityId());
            if (flowNode != null) {
                if (flowNode.getClass() == StartEvent.class) {
                    wfCommentInfo.setActivityType("startEvent");
                } else if (flowNode.getClass() == UserTask.class) {
                    wfCommentInfo.setActivityType("userTask");
                }
            }
            // 设置处理人信息
            if (StringUtils.isNotEmpty(wfCommentInfo.getUserId())){
                wfCommentInfo.setUserName(sysUserService.selectNickNameById(Long.valueOf(wfCommentInfo.getUserId())));
            }
            // 计算耗时
            if (StringUtils.isNotBlank(wfCommentInfo.getTaskId())) {
                HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(wfCommentInfo.getTaskId()).singleResult();
                if (historicTaskInstance != null && Objects.nonNull(
                    historicTaskInstance.getEndTime())) {
                    wfCommentInfo.setEndTime(historicTaskInstance.getEndTime());
                    wfCommentInfo.setDuration(DateUtils.getDatePoor(historicTaskInstance.getEndTime(), historicTaskInstance.getStartTime()));
                }
            }
        }
        // 判断流程是否结束
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(procInsId);
        if (processInstance != null) {
            // 添加待审核节点
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
            if (CollUtil.isNotEmpty(taskList)) {
                for (Task task : taskList) {
                    WfCommentInfo wfCommentInfo = new WfCommentInfo();
                    UserTask userTask = wfBpmnModelService.findUserTaskForProcDefIdAndKey(extendProcinst.getProcDefId(), task.getTaskDefinitionKey());
                    wfCommentInfo.setType("100");
                    wfCommentInfo.setActivityId(task.getTaskDefinitionKey());
                    wfCommentInfo.setActivityName(task.getName());
                    if (StringUtils.isNotBlank(task.getAssignee())) {
                        wfCommentInfo.setUserName(sysUserService.selectNickNameById(Long.valueOf(task.getAssignee())));
                    } else if (userTask != null && CollUtil.isNotEmpty(userTask.getCandidateUsers())) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String userId : userTask.getCandidateUsers()) {
                            try {
                                stringBuilder.append(sysUserService.selectNickNameById(Long.valueOf(userId))).append(",");
                            } catch (NumberFormatException e) {
                                log.debug("候选人 id 不是数字，跳过：{}", userId);
                            }
                        }
                        // 去掉末尾逗号；全部候选人解析失败时结果为空串，直接 substring 会越界
                        if (stringBuilder.length() > 0) {
                            stringBuilder.setLength(stringBuilder.length() - 1);
                        }
                        wfCommentInfo.setUserName(stringBuilder.toString());
                    } else if (userTask != null && CollUtil.isNotEmpty(userTask.getCandidateGroups())) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String groupId : userTask.getCandidateGroups()) {
                            List<SysUser> sysUsers = sysUserService.selectRoleToUser(Long.valueOf(groupId));
                            for (SysUser sysUser : sysUsers) {
                                stringBuilder.append(sysUser.getNickName()).append(",");
                            }
                        }
                        // 同上：角色下没有用户时为空串，避免 substring 越界
                        if (stringBuilder.length() > 0) {
                            stringBuilder.setLength(stringBuilder.length() - 1);
                        }
                        wfCommentInfo.setUserName(stringBuilder.toString());
                    }
                    wfCommentInfo.setCreateTime(task.getCreateTime());
                    wfCommentInfoList.add(wfCommentInfo);
                }
            }
        }
        // 添加结束节点信息
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
