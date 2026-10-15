package com.basic.workflow.service.wfProcessInfo.detail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.basic.common.core.domain.entity.SysUser;
import com.basic.common.utils.StringUtils;
import com.basic.constant.ExtensionConstant;
import com.basic.flowable.common.constant.TaskConstants;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.vo.WfProcNodeVo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;

/**
 * @author yanbin
 * @description TODO (根据实例procInsId查询历史任务信息)
 * @date 2024/03/04
 */
@Service
public class WfHistoryProcNodeService extends WfProcessInfoBase {

  /**
   * @return java.util.List<com.basic.workflow.domain.vo.WfProcNodeVo>
   * @description TODO （获取历史任务信息列表 已经完成的节点信息
   * @params [procInsId]
   * @author yanbin
   * @date 2024/3/4 10:43
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public List<WfProcNodeVo> historyProcNodeList(String procInsId) {
    // 获取流程实例
    HistoricProcessInstance historicProcIns = historyService.createHistoricProcessInstanceQuery()
        .processInstanceId(procInsId).includeProcessVariables().singleResult();
    List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
        .processInstanceId(procInsId).activityTypes(
            CollUtil.newHashSet(BpmnXMLConstants.ELEMENT_EVENT_START,
                BpmnXMLConstants.ELEMENT_EVENT_END, BpmnXMLConstants.ELEMENT_TASK_USER))
        .orderByHistoricActivityInstanceStartTime().desc().orderByHistoricActivityInstanceEndTime()
        .desc().list();
    List<Comment> commentList = taskService.getProcessInstanceComments(procInsId);
    List<WfProcNodeVo> elementVoList = new ArrayList<>();
    for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
      WfProcNodeVo elementVo = new WfProcNodeVo();
      elementVo.setProcDefId(activityInstance.getProcessDefinitionId());
      elementVo.setActivityId(activityInstance.getActivityId());
      elementVo.setActivityName(activityInstance.getActivityName());
      //获取bpmnModel
      BpmnModel bpmnModel = repositoryService.getBpmnModel(
          activityInstance.getProcessDefinitionId());
      //获取process
      Process process = bpmnModel.getProcesses().get(0);
      //获取UserTask
      FlowElement flowElement = process.getFlowElement(activityInstance.getActivityId());
      UserTask userTask = null;
      //筛选用户节点
      if (flowElement.getClass() == UserTask.class) {
        userTask = (UserTask) process.getFlowElement(activityInstance.getActivityId());
        //添加发起人的信息---默认提交人跳过节点的提交人信息
        if (StringUtils.isNotBlank(userTask.getSkipExpression()) && userTask.getSkipExpression()
            .equals("${initiator == ''}")) {
          ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(
              activityInstance.getProcessInstanceId());
          if (extendProcinst != null) {
            elementVo.setAssigneeId(Long.valueOf(extendProcinst.getCurrentUserCode()));
            elementVo.setAssigneeName(extendProcinst.getCurrentName());
            elementVo.setCandidate(extendProcinst.getCurrentName());
          }
        }
      }

      elementVo.setActivityType(activityInstance.getActivityType());
      elementVo.setCreateTime(activityInstance.getStartTime());
      elementVo.setEndTime(activityInstance.getEndTime());
      //耗时
      if (ObjectUtil.isNotNull(activityInstance.getDurationInMillis())) {
        elementVo.setDuration(DateUtil.formatBetween(activityInstance.getDurationInMillis(),
            BetweenFormatter.Level.SECOND));
      }
      //开始节点
      if (BpmnXMLConstants.ELEMENT_EVENT_START.equals(activityInstance.getActivityType())) {
        if (ObjectUtil.isNotNull(historicProcIns)) {
          Long userId = Long.parseLong(historicProcIns.getStartUserId());
          elementVo.setAssigneeName(sysUserService.selectNickNameById(userId));
          elementVo.setAssigneeId(userId);
        }

      } else if (BpmnXMLConstants.ELEMENT_TASK_USER.equals(activityInstance.getActivityType())) {
        if (StringUtils.isNotBlank(activityInstance.getAssignee())) {
          String assignee = activityInstance.getAssignee();
          // UEL 表达式占位符（如 ${assignee}）或多实例表达式，不是真实用户ID，直接跳过
          if (assignee.startsWith("${") || !StringUtils.isNumeric(assignee)) {
            // 不设置 assigneeId/Name，由下方 candidate 流程补充展示
          } else {
            Long userId = Long.parseLong(assignee);
            SysUser sysUser = sysUserService.selectUserById(userId);
            if (sysUser != null) {
              String nickName = sysUser.getNickName();
              elementVo.setAssigneeId(userId);
              elementVo.setAssigneeName(nickName);
            }
          }
        }
        // 展示审批人员
        List<HistoricIdentityLink> linksForTask = historyService.getHistoricIdentityLinksForTask(
            activityInstance.getTaskId());
        StringBuilder userBuilder = new StringBuilder();
        StringBuilder userIdBuilder = new StringBuilder();
        for (HistoricIdentityLink identityLink : linksForTask) {
          //获取任务状态
          //TODO 这里区分委派,转办, 设置操作人和候选人
          if ("candidate".equals(identityLink.getType())) {
            // 判断选择的节点候选人员
            if (StringUtils.isNotBlank(identityLink.getUserId())) {
              String candUserId = identityLink.getUserId();
              // 跳过 UEL 表达式占位符（多实例等场景），不是真实用户ID
              if (!candUserId.startsWith("${") && StringUtils.isNumeric(candUserId)) {
                Long userId = Long.parseLong(candUserId);
                SysUser sysUser = sysUserService.selectUserById(userId);
                if (sysUser != null) {
                  String nickName = sysUser.getNickName();
                  userBuilder.append(nickName).append(",");
                  userIdBuilder.append(userId).append(",");
                }
              }
            }
            // 阶段选择的候选角色
            if (StringUtils.isNotBlank(identityLink.getGroupId())) {
              if (StringUtils.isNotBlank(identityLink.getGroupId())) {
                //查找任务
                HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery()
                    .taskId(identityLink.getTaskId()).singleResult();
                //查找节点
                FlowNode flowNode = wfBpmnModelService.findFlowNodeByActivityId(
                    task.getProcessDefinitionId(), task.getTaskDefinitionKey());
                if (flowNode != null && flowNode.getClass() == UserTask.class
                    && identityLink.getGroupId() != null) {
                  ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(
                      task.getProcessInstanceId());
                  //TODO 这里不使用最初始的定义id，因为可能目前节点是已经变化的，已经不需要组织筛选或者其他筛选，但是选择了的话可能会导致人员信息错误
                  //人员筛选 拓展
                  Boolean isUserScreen = Boolean.valueOf(
                      extensionElementsService.getCustomPropertyText(task.getTaskDefinitionKey(),
                          task.getProcessDefinitionId(), ExtensionConstant.IS_USER_SCREEN));
                  //组织筛选 拓展
                  Boolean isOrgScreen = Boolean.valueOf(
                      extensionElementsService.getCustomPropertyText(task.getTaskDefinitionKey(),
                          task.getProcessDefinitionId(), ExtensionConstant.IS_ORG_SCREEN));
                  //组织筛选还是部门筛选
                  String isDeptOrOrg = extensionElementsService.getCustomPropertyText(
                      task.getTaskDefinitionKey(), task.getProcessDefinitionId(),
                      ExtensionConstant.IS_DEPT_OR_ORG);
                  //交替节点
                  Boolean isRoleReplace = Boolean.valueOf(
                      extensionElementsService.getCustomPropertyText(task.getTaskDefinitionKey(),
                          task.getProcessDefinitionId(), ExtensionConstant.IS_ROLE_REPLACE));
                  //TODO 人员筛选 / 组织筛选（process 业务定制已去除，stub 化）
                  if (isUserScreen || isOrgScreen) {
                    // 简化：直接用 groupId 自身作为 userId 展示
                    userIdBuilder.append(identityLink.getGroupId()).append(",");
                  } else {
                    Long roleId = Long.parseLong(StringUtils.stripStart(identityLink.getGroupId(),
                        TaskConstants.ROLE_GROUP_PREFIX));
                    List<SysUser> userList = sysUserService.selectRoleToUser(roleId);
                    for (SysUser user : userList) {
                      userBuilder.append(user.getNickName()).append(",");
                      userIdBuilder.append(user.getUserId()).append(",");
                    }
                  }
                } else {
                  Long roleId = Long.parseLong(StringUtils.stripStart(identityLink.getGroupId(),
                      TaskConstants.ROLE_GROUP_PREFIX));
                  List<SysUser> userList = sysUserService.selectRoleToUser(roleId);
                  for (SysUser user : userList) {
                    userBuilder.append(user.getNickName()).append(",");
                    userIdBuilder.append(user.getUserId()).append(",");
                  }
                }
              }
            }
          }
        }
        if (StringUtils.isNotBlank(userBuilder)) {
          elementVo.setCandidate(userBuilder.substring(0, userBuilder.length() - 1));
          elementVo.setUserIdStr(userIdBuilder.substring(0, userIdBuilder.length() - 1));
        }
        // 获取意见评论内容
        if (CollUtil.isNotEmpty(commentList)) {
          List<Comment> comments = new ArrayList<>();
          List<Map<String, Object>> signList = new ArrayList<>();
          for (Comment comment : commentList) {
            Map<String, Object> map = new HashMap();
            //把加签人员审批的意见也放入
            boolean flag = this.checkTaskIsSign(comment.getTaskId(),
                activityInstance.getProcessInstanceId(), activityInstance.getTaskId());
            if (flag) {
              if (StringUtils.isNotEmpty(comment.getUserId())) {
                Long userId = Long.parseLong(comment.getUserId());
                SysUser sysUser = sysUserService.selectUserById(userId);
                if (sysUser != null) {
                  String nickName = sysUser.getNickName();
                  //将审批人的id替换成审批人的姓名---区别这条意见是哪个人审批
                  comment.setUserId(nickName);
                  map.put("user", nickName);
                }
                map.put("commentId", comment.getId());
                map.put("activityId", activityInstance.getActivityId());

              }
              signList.add(map);
              comments.add(comment);
            }
            //普通审批
            if (comment.getTaskId().equals(activityInstance.getTaskId())) {
              if (StringUtils.isNotEmpty(comment.getUserId())) {
                Long userId = Long.parseLong(comment.getUserId());
                SysUser sysUser = sysUserService.selectUserById(userId);
                if (sysUser != null) {
                  String nickName = sysUser.getNickName();
                  comment.setUserId(nickName);
                }
              }
              comments.add(comment);
            }
          }
          //加签人员
          if (StringUtils.isNotEmpty(signList)) {
            elementVo.setSignUser(signList);
          }
          elementVo.setCommentList(comments);
        }
      }
      elementVoList.add(elementVo);
    }
    return elementVoList;
  }
}
