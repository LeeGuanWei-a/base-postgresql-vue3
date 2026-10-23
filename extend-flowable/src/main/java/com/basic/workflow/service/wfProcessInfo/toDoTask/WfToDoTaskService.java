package com.basic.workflow.service.wfProcessInfo.toDoTask;

import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.utils.PageUtils;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.flowable.utils.ProcessUtils;
import com.basic.workflow.domain.bo.WfCopyBo;
import com.basic.workflow.domain.vo.WfCopyVo;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import cn.hutool.core.collection.CollUtil;
import org.flowable.task.api.TaskQuery;
import org.springframework.stereotype.Service;

/**
 * @description 待办任务查询服务，分页查询用户可办理的待办任务并补充节点授权与会签标记
 * @author yanbin
 * @date 2024/03/04
 */
@Service
public class WfToDoTaskService extends WfProcessInfoBase {
    /**
     * @return List<WfTaskVo> 待办任务分页结果
     * @description 按查询条件分页查询用户待办任务，并为每行补充节点授权与是否顺序会签标记
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectPageTodoProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        //用户id
//        String userId = processQuery.getUserId();
//        //用户角色组id
//        List<String> groupId = processQuery.getGroupId();
//        Page<WfTaskVo> page = new Page<>();
//        TaskQuery taskQuery = taskService.createTaskQuery().active().includeProcessVariables().taskCandidateOrAssigned(userId).taskCandidateGroupIn(groupId).orderByTaskCreateTime().desc();
//        // 构建搜索条件
//        ProcessUtils.buildProcessSearch(taskQuery, processQuery);
//        page.setTotal(taskQuery.count());
//        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
//        List<Task> taskList = taskQuery.listPage(offset, pageQuery.getPageSize());
//        List<WfTaskVo> flowList = new ArrayList<>();
//        for (Task task : taskList) {
//            WfTaskVo flowTask = new WfTaskVo();
//            flowTask.setNoticeType("todo");  //通知类型
//            //当前节点activityId
//            flowTask.setActivityId(task.getTaskDefinitionKey());
//            // 当前流程信息
//            flowTask.setTaskId(task.getId());
//            //节点Key
//            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
//            //流程发起时间
//            flowTask.setCreateTime(task.getCreateTime());
//            //流程定义id
//            flowTask.setProcDefId(task.getProcessDefinitionId());
//            //任务名称
//            flowTask.setTaskName(task.getName());
//            // 流程定义信息
//            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
//            //流程定义名称
//            flowTask.setProcDefName(pd.getName());
//            //流程版本
//            flowTask.setProcDefVersion(pd.getVersion());
//            //流程实例id
//            flowTask.setProcInsId(task.getProcessInstanceId());
//            //查询业务拓展表
//            flowTask = this.selectProcessOfExtendProcinst(flowTask, processQuery.getParams(), task.getProcessInstanceId());
//            if (flowTask == null) {
//                continue;
//            }
//            //流程发起的定义id
//            flowTask.setProcDefId(task.getProcessDefinitionId());
//            // 流程发起人信息
//            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
//            //流程启动activityId
//            flowTask.setStartActivityId(historicProcessInstance.getStartActivityId());
//            if (historicProcessInstance != null) {
//                //设置发起人id
//                flowTask.setStartUserId(historicProcessInstance.getStartUserId());
//                //获取当前任务的活动节点  userTask
//                FlowNode flowNode = wfBpmnModelService.findFlowNodeByActivityId(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
//                if (flowNode.getClass() == UserTask.class) {
//                    //判断转办 委派 加签任务
//                    if ((task.getDescription() != null && task.getDescription().equals(FlowConstant.TRANSFER)) || (task.getScopeType() != null && ((task.getScopeType().equals(FlowConstant.AFTER_ADDSIGN)) || task.getScopeType().equals(FlowConstant.BEFORE_ADDSIGN))) || task.getDelegationState() != null) {
//                    } else {
//                        String assigneeType = extensionElementsService.getCustomPropertyText(task.getTaskDefinitionKey(), task.getProcessDefinitionId(), ExtensionConstant.ASSIGNEE_TYPE);
//                        // 获取拓展 是否需要筛选用户                      //判断是否需要筛选
//                        Boolean isUserScreen = Boolean.valueOf(extensionElementsService.getCustomPropertyText(task.getTaskDefinitionKey(), task.getProcessDefinitionId(), ExtensionConstant.IS_USER_SCREEN));
//                        // 获取拓展 是否需要组织筛选
//                        Boolean isOrgScreen = Boolean.valueOf(extensionElementsService.getCustomPropertyText(task.getTaskDefinitionKey(), task.getProcessDefinitionId(), ExtensionConstant.IS_ORG_SCREEN));
//                        // 获取拓展 是否组织架构筛选还是部门筛选
//                        String isDeptOrOrg = extensionElementsService.getCustomPropertyText(task.getTaskDefinitionKey(), task.getProcessDefinitionId(), ExtensionConstant.IS_DEPT_OR_ORG);
//                        // 只有身份存储才需要人员筛选， 固定值不需要，固定值是筛选人员放入任务审批
//                        if (assigneeType.equals("1")) {
//                            // 筛选条件:筛选出的人员和当前流程的发起人的公司是相同的
//                            if (isUserScreen) {
//                                //获取人员匹配   匹配成功就添加代办
//                                Boolean frame = Boolean.valueOf(extensionCandidateService.screenFlowTaskUser(historicProcessInstance.getStartUserId(), processQuery.getUserId()).get("frame").toString());
//                                if (!frame) {
//                                    continue;
//                                }
//                            }
//                            // 筛选条件:筛选出当前处理人和传入的orgId相同的人员  (只有身份存储需要筛选，固定值不需要筛选)
//                            else if (isOrgScreen) {
//                                //返回true说明这个人在这个组织中
//                                if (!extensionCandidateService.handleOrgCandidate(task.getProcessInstanceId(), userId, isDeptOrOrg)) {
//                                    continue;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            // 流程变量
//            flowTask.setProcVars(task.getProcessVariables());
//            flowList.add(flowTask);
//        }
//        page.setRecords(flowList);
        PageUtils.startPage();
        List<WfTaskVo> taskVoList = wfFlowableProcessMapper.selectTodoTaskList(processQuery);
        // 补充「节点授权功能 / 是否顺序会签」：前端据此动态渲染审批按钮组。
        // 两者都从引擎缓存的 BpmnModel 读，不产生额外的库查询（列表行数多也不会变慢）。
        for (WfTaskVo vo : taskVoList) {
            try {
                Object flowNode = wfBpmnModelService.findFlowNodeByActivityId(vo.getProcDefId(), vo.getActivityId());
                if (!(flowNode instanceof org.flowable.bpmn.model.UserTask)) {
                    continue;
                }
                vo.setPermissionJson(extensionElementsService.getCustomPropertyText(
                        vo.getActivityId(), vo.getProcDefId(), com.basic.constant.ExtensionConstant.IDM_PERMISSION));
                vo.setSequentialMultiInstance(
                        com.basic.flowable.utils.ModelUtils.isSequentialMultiInstance(
                                (org.flowable.bpmn.model.UserTask) flowNode));
            } catch (Exception e) {
                // 单个任务取不到就跳过，不能影响整个列表
            }
        }
        return taskVoList;
    }

    /**
     * @return List<WfTaskVo> 待办任务列表，不分页
     * @description 不分页查询用户待办任务，并为每行补充节点授权与是否顺序会签标记
     * @params [processQuery] 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectTodoProcessList(ProcessQuery processQuery) {
        //用户id
        String userId = processQuery.getUserId();
        //用户角色组id
        List<String> groupId = processQuery.getGroupId();
        TaskQuery taskQuery = taskService.createTaskQuery().active().includeProcessVariables().taskCandidateOrAssigned(userId).taskCandidateGroupIn(groupId).orderByTaskCreateTime().desc();
        // 构建搜索条件
        ProcessUtils.buildProcessSearch(taskQuery, processQuery);
//        List<Task> taskList = taskQuery.list();
//        List<WfTaskVo> taskVoList = new Arr ayList<>();
        // TODO sql查询 这个方法的下面需要修改   未完成
        List<WfTaskVo> taskVoList = wfFlowableProcessMapper.selectTodoTaskList(processQuery);
        // 补充「节点授权功能 / 是否顺序会签」：前端据此动态渲染审批按钮组。
        // 两者都从引擎缓存的 BpmnModel 读，不产生额外的库查询（列表行数多也不会变慢）。
        for (WfTaskVo vo : taskVoList) {
            try {
                Object flowNode = wfBpmnModelService.findFlowNodeByActivityId(vo.getProcDefId(), vo.getActivityId());
                if (!(flowNode instanceof org.flowable.bpmn.model.UserTask)) {
                    continue;
                }
                vo.setPermissionJson(extensionElementsService.getCustomPropertyText(
                        vo.getActivityId(), vo.getProcDefId(), com.basic.constant.ExtensionConstant.IDM_PERMISSION));
                vo.setSequentialMultiInstance(
                        com.basic.flowable.utils.ModelUtils.isSequentialMultiInstance(
                                (org.flowable.bpmn.model.UserTask) flowNode));
            } catch (Exception e) {
                // 单个任务取不到就跳过，不能影响整个列表
            }
        }
//        List<WfTaskVo> list = new ArrayList<>();
//        for (WfTaskVo wfTaskVo : taskVoList) {
//            // 流程发起人信息
//            HistoricProcessInstance historicProcessInstance =
//                    historyService
//                            .createHistoricProcessInstanceQuery()
//                            .processInstanceId(wfTaskVo.getProcInsId())
//                            .singleResult();
//            Task task = taskService.createTaskQuery().taskId(wfTaskVo.getTaskId()).singleResult();
//            //获取当前任务的活动节点  userTask
//            FlowNode flowNode = wfBpmnModelService.findFlowNodeByActivityId(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
//            if (flowNode.getClass() == UserTask.class) {
//                //判断转办 委派 加签任务
//                if ((task.getDescription() != null && task.getDescription().equals(FlowConstant.TRANSFER))
//                        || (task.getScopeType() != null &&
//                        ((task.getScopeType().equals(FlowConstant.AFTER_ADDSIGN)) ||
//                                task.getScopeType().equals(FlowConstant.BEFORE_ADDSIGN)))
//                        || task.getDelegationState() != null) {
//                } else {
//                    //获取拓展 是否需要筛选用户  //判断是否需要筛选
//                    Boolean isUserScreen = Boolean.valueOf(extensionElementsService.getCustomPropertyText
//                            (task.getTaskDefinitionKey(), task.getProcessDefinitionId(), ExtensionConstant.IS_USER_SCREEN));
//                    //获取拓展 是否需要组织筛选
//                    Boolean isOrgScreen = Boolean.valueOf(extensionElementsService.getCustomPropertyText(task.getTaskDefinitionKey(),
//                            task.getProcessDefinitionId(), ExtensionConstant.IS_ORG_SCREEN));
//                    // 获取拓展 是否组织架构筛选还是部门筛选
//                    String isDeptOrOrg = extensionElementsService.getCustomPropertyText(task.getTaskDefinitionKey(),
//                            task.getProcessDefinitionId(), ExtensionConstant.IS_DEPT_OR_ORG);
//                    if (isUserScreen) {
//                        //获取人员匹配   匹配成功就添加代办
//                        Boolean frame = Boolean.valueOf(extensionCandidateService
//                                .screenFlowTaskUser(historicProcessInstance.getStartUserId()
//                                        , processQuery.getUserId()).get("frame").toString());
//                        if (!frame) {
//                            continue;
//                        }
//                    }
//                    // 筛选条件:筛选出当前处理人和传入的orgId或deptId相同的人员  (只有身份存储需要筛选，固定值不需要筛选)
//                    else if (isOrgScreen) {
//                        //返回true说明这个人在这个组织中
//                        if (!extensionCandidateService.handleOrgCandidate
//                                (task.getProcessInstanceId(), userId, isDeptOrOrg)) {
//                            continue;
//                        }
//                    }
//                }
//            }
//            list.add(wfTaskVo);
//        }
        return taskVoList;
    }

    /**
     * @return TableDataInfo<Object> 待办任务与抄送信息合并后的分页结果
     * @description 合并当前用户的待办任务与抄送信息，按分页参数截取后返回
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public TableDataInfo<Object> getTaskTodoAndCopy(ProcessQuery processQuery, PageQuery pageQuery) throws ParseException {
        List<WfTaskVo> wfTaskVoList = this.selectTodoProcessList(processQuery);
        WfCopyBo wfCopyBo = new WfCopyBo();
        wfCopyBo.setUserId(processQuery.getUserId());
        wfCopyBo.setParams(processQuery.getParams());
        List<WfCopyVo> wfCopyList = wfCopyService.selectList(wfCopyBo);
        List<Object> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(wfTaskVoList)) {
            list.addAll(wfTaskVoList);
        }
        if (CollUtil.isNotEmpty(wfCopyList)) {
            list.addAll(wfCopyList);
        }
        return TableDataInfo.subList(list, pageQuery.getPageNum(), pageQuery.getPageSize());
    }
    /**
     * @return Integer 待办任务数量
     * @description 按查询条件统计当前用户的待办任务数
     * @params [processQuery] 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public Integer selectTodoTaskCount(ProcessQuery processQuery) {
        return wfFlowableProcessMapper.selectTodoTaskCount(processQuery);
    }
}
