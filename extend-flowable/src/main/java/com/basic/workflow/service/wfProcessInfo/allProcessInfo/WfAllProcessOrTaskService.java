package com.basic.workflow.service.wfProcessInfo.allProcessInfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.basic.common.core.domain.entity.SysUser;
import com.basic.common.utils.DateUtils;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.common.enums.ProcessStatus;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.vo.WfCategoryVo;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description 流程实例与任务查询服务，按用户维度查询历史流程实例并装配发起人、状态与当前任务等信息
 * @author yanbin
 * @date 2024/03/05
 */
@Service
public class WfAllProcessOrTaskService extends WfProcessInfoBase {
    /**
     * @return List<WfTaskVo> 历史流程实例列表，已装配发起人、状态、耗时、分类与当前任务等信息
     * @description 查询历史流程实例并区分管理员与普通用户，管理员取全部流程、普通用户只取本人参与过的流程，再逐条装配展示信息
     * @params [processQuery] 流程查询条件，params 中 role=admin 可额外按管理角色放行
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectHisProcInsProcess(ProcessQuery processQuery) {
        List<WfTaskVo> WfTaskVoList = new ArrayList<>();
        SysUser user = sysUserService.selectUserById(Long.valueOf(processQuery.getUserId()));
//        Set<String> roleIdList = new LinkedHashSet<String>();
//        roleIdList.addAll(processQuery.getGroupId());
        List<HistoricProcessInstance> historicProcessInstance = new ArrayList<>();
        //获取其他管理角色 传参 role:admin
        String role = null;
        if (StringUtils.isNotEmpty(processQuery.getParams()) && processQuery.getParams().containsKey("role")) {
            role = processQuery.getParams().get("role").toString();
        }
        //首先判断是否是管理员
        if (user.isAdmin() || (role != null && role.equals("admin"))) {
            //获取所有存在的流程实例
            historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processDefinitionCategory(processQuery.getCategory()).list();
        } else {
            //根据业务传的用户id  获取当前用户所有参与过的流程
            List<Map<String, Object>> hisProcInsList = wfFlowableProcessMapper.selectProcInsList(processQuery);
            if (hisProcInsList.size() > 0) {
                List<HistoricProcessInstance> finalHistoricProcessInstance = new ArrayList<>();
                //遍历查询，通过实例id获取实例 实体
                hisProcInsList.forEach(procIns -> {
                    HistoricProcessInstance historicTask = historyService.createHistoricProcessInstanceQuery().processInstanceId(procIns.get("id_").toString()).singleResult();
                    finalHistoricProcessInstance.add(historicTask);
                });
                historicProcessInstance = finalHistoricProcessInstance;
            }
            //注：不知道为什么involvedGroups不生效
//            historicProcessInstance = historyService
//                    .createHistoricProcessInstanceQuery()
//                    .processDefinitionCategory(processQuery.getCategory())
//                    .involvedGroups(roleIdList)
//                    .involvedUser(processQuery.getUserId())
//                    .list();
        }
        for (HistoricProcessInstance hisIns : historicProcessInstance) {
            WfTaskVo taskVo = new WfTaskVo();
            //启动activityId
            taskVo.setStartActivityId(hisIns.getStartActivityId());
            //流程发起人
            taskVo.setStartUser(hisIns.getStartUserId());
            if (hisIns.getStartUserId().equals(user.getUserId().toString())) {
                taskVo.setIsStartUser(true);
            } else {
                taskVo.setIsStartUser(false);
            }
            //获取流程状态
            HistoricVariableInstance processStatusVariable = historyService.createHistoricVariableInstanceQuery().processInstanceId(hisIns.getId()).variableName(ProcessConstants.PROCESS_STATUS_KEY).singleResult();
            String processStatus = null;
            if (ObjectUtil.isNotNull(processStatusVariable)) {
                processStatus = Convert.toStr(processStatusVariable.getValue());
            }
            // 兼容旧流程
            if (processStatus == null) {
                processStatus = ObjectUtil.isNull(hisIns.getEndTime()) ? ProcessStatus.RUNNING.getStatus() : ProcessStatus.COMPLETED.getStatus();
            }
            //流程状态
            taskVo.setProcessStatus(processStatus);
            //流程发起时间
            taskVo.setCreateTime(hisIns.getStartTime());
            //流程完结时间
            taskVo.setFinishTime(hisIns.getEndTime());
            //流程实例id
            taskVo.setProcInsId(hisIns.getId());
            // 计算耗时
            if (Objects.nonNull(hisIns.getEndTime())) {
                taskVo.setDuration(DateUtils.getDatePoor(hisIns.getEndTime(), hisIns.getStartTime()));
            } else {
                taskVo.setDuration(DateUtils.getDatePoor(DateUtils.getNowDate(), hisIns.getStartTime()));
            }
            // 流程部署实例信息
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(hisIns.getDeploymentId()).singleResult();
            //流程部署id
            taskVo.setDeployId(hisIns.getDeploymentId());
            //流程定义id
            taskVo.setProcDefId(hisIns.getProcessDefinitionId());
            taskVo.setProcDefName(hisIns.getProcessDefinitionName());
            //流程版本
            taskVo.setProcDefVersion(hisIns.getProcessDefinitionVersion());
            //流程业务关联查询
            ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(hisIns.getId());
            if (extendProcinst != null) {
                //业务系统类型
                taskVo.setAppSn(extendProcinst.getTenantId());
                //流程申请人name
                taskVo.setStartUserName(extendProcinst.getCurrentName());
            }
            //分类code
            taskVo.setCategory(deployment.getCategory());
            //分类名称
            taskVo.setCategoryName(wfCategoryService.queryByCode(deployment.getCategory()).getCategoryName());
            //路由跳转地址
            WfCategoryVo wfCategoryVo = wfCategoryService.getCategoryToUrl(deployment.getCategory());
            taskVo.setUrl(wfCategoryVo.getUrl());
            // 当前所处流程
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).includeIdentityLinks().list();
            if (CollUtil.isNotEmpty(taskList)) {
                taskVo.setTaskName(taskList.stream().map(Task::getName).filter(StringUtils::isNotEmpty).collect(Collectors.joining(",")));
                for (Task task : taskList) {
                    taskVo.setTaskId(task.getId());
                    taskVo.setIsAuditUser(false);
                    //当前节点activityId
                    taskVo.setActivityId(task.getTaskDefinitionKey());
                    //正在运行的实例id
                    taskVo.setExecutionId(task.getExecutionId());
                    // 展示审批人员
                    List<HistoricIdentityLink> historicIdentityLinkList = historyService.getHistoricIdentityLinksForTask(task.getId());
                    StringBuilder candidateBuilder = new StringBuilder();
                    StringBuilder groupBuilder = new StringBuilder();
                    for (HistoricIdentityLink link : historicIdentityLinkList) {
                        // 判断任务的处理人是否是当前用户
                        if (link.getUserId() != null && link.getUserId().equals(user.getUserId().toString())) {
                            taskVo.setIsAuditUser(true);
                        }
                        if (link.getGroupId() != null) {
                            List<SysUser> wfUserVos = sysUserService.selectRoleToUser(Long.valueOf(link.getGroupId()));
                            wfUserVos.forEach(item -> {
                                if (item.getUserId().equals(user.getUserId().toString())) {
                                    taskVo.setIsAuditUser(true);
                                }
                            });
                        }
                        //设置任务候选人
                        if (link.getType().equals("candidate") && link.getUserId() != null) {
                            candidateBuilder.append(link.getUserId()).append(",");
                        }
                        //设置候选角色
                        else if (link.getType().equals("candidate") && link.getGroupId() != null) {
                            groupBuilder.append(link.getGroupId()).append(",");
                        }
                        //设置任务处理人
                        else if (link.getType().equals("assignee")) {
                            taskVo.setAssigneeId(link.getUserId());
                        }
                    }
                    if (StringUtils.isNotBlank(candidateBuilder)) {
                        taskVo.setCandidate(candidateBuilder.substring(0, candidateBuilder.length() - 1));
                    }
                    if (StringUtils.isNotBlank(groupBuilder)) {
                        taskVo.setCandidateGroup(groupBuilder.substring(0, groupBuilder.length() - 1));
                    }
                }
            }
            WfTaskVoList.add(taskVo);
        }
        return WfTaskVoList;
    }
}
