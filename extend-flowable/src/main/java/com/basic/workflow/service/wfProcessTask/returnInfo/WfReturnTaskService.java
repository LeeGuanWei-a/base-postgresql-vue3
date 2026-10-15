package com.basic.workflow.service.wfProcessTask.returnInfo;

import cn.hutool.core.util.ObjectUtil;
import com.basic.common.core.domain.entity.SysUser;
import com.basic.common.utils.StringUtils;
import com.basic.constant.FlowConstant;
import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.flowable.common.enums.ProcessStatus;
import com.basic.flowable.flow.FlowableUtils;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import com.basic.workflow.service.wfProcessTask.delete.WfDeleteProcessService;
import com.basic.workflow.service.wfProcessTask.reject.WfRejectTaskService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yanbin
 * @description TODO (Flowable退回任务)
 * @date 2024/03/01
 */
@Service
public class WfReturnTaskService extends WfProcessTaskBase {

    @Autowired
    private WfRejectTaskService wfRejectTaskService;
    @Autowired
    private WfDeleteProcessService wfDeleteProcessService;
    /**
     * @return boolean
     * @description TODO (退回任务)
     * @params [bo]
     * @author yanbin
     * @date 2024/3/1 17:25
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean taskReturn(WfTaskBo bo) {
        //TODO 1. 当前任务 task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        if (ObjectUtil.isNull(task)) {
            throw new RuntimeException("获取任务信息异常！");
        }
        if (task.isSuspended()) {
            throw new RuntimeException("任务处于挂起状态");
        }
        //TODO 2.设置当前节点处理人
        task.setAssignee(bo.getCurrentUserId());
        taskService.saveTask(task);
        // 取"目标节点最近一次已完成的任务"，用于给退回后新建的任务还原处理人/候选人。
        // 注意：目标节点在历史里可能没有任务记录（例如从未真正生成过任务、或任务被删过），
        // 原来的写法直接 .list().get(0) 会抛 IndexOutOfBoundsException（用户看到 500）。
        // 这里先查再判空，下面使用点也加了 null 保护。
        List<HistoricTaskInstance> historicTaskList = historyService.createHistoricTaskInstanceQuery()
            .taskDefinitionKey(bo.getTargetKey()).processInstanceId(bo.getProcInsId())
            .orderByTaskCreateTime().desc().list();
        HistoricTaskInstance historicTaskInstance =
            historicTaskList.isEmpty() ? null : historicTaskList.get(0);
        //TODO 3. 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        //TODO 4. 获取流程模型信息bpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        //TODO 5. 获取当前任务节点元素
        FlowElement source = ModelUtils.getFlowElementById(bpmnModel, task.getTaskDefinitionKey());
        //TODO 6. 获取跳转的节点元素
        // 必须先校验目标节点：targetKey 为空或指向模型中不存在的节点时，
        // 下面的 getFlowElementById 会返回 null，最终在 isSequentialReachable 里抛
        // NullPointerException（接口 500，前端只看到"未知异常"）。
        if (StringUtils.isBlank(bo.getTargetKey())) {
            throw new com.basic.common.exception.ServiceException("请先选择要退回的节点");
        }
        FlowElement target = ModelUtils.getFlowElementById(bpmnModel, bo.getTargetKey());
        if (target == null) {
            throw new com.basic.common.exception.ServiceException(
                    "要退回的节点不存在，请重新选择（节点标识：" + bo.getTargetKey() + "）");
        }
        //TODO 7. 从当前节点向前扫描，判断当前节点与目标节点是否属于串行，若目标节点是在并行网关上或非同一路线上，不可跳转
        boolean isSequential = ModelUtils.isSequentialReachable(source, target, new HashSet<>());
        if (!isSequential) {
            throw new RuntimeException("当前节点相对于目标节点，不属于串行关系，无法回退");
        }
        //TODO 8. 获取所有正常进行的任务节点 Key，这些任务不能直接使用，需要找出其中需要退回的任务
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        //TODO 8.1 key集合
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        //TODO 9. 需退回任务列表
        List<String> currentIds = new ArrayList<>();
        //TODO 9.1 通过父级网关的出口连线，结合 runTaskList 比对，获取需要退回的任务
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(target, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> currentIds.add(item.getId()));
        //TODO 10. 循环获取那些需要被退回的节点的ID，用来设置驳回原因
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        //TODO 11.设置回退意见
        for (String currentTaskId : currentTaskIds) {
            // TODO 11.1 添加审批意见
            this.addComment(currentTaskId, bo.getCurrentUserId(), task.getProcessInstanceId(), FlowComment.REBACK.getType(), bo.getComment());
        }
        //TODO 12 保存表单内容
        this.saveTaskForForm(task.getId(), bo.getProcInsId(), bo.getCurrentUserId(), task.getTaskDefinitionKey(), bo.getFormData());
        //TODO 13.处理提交人节点
        FlowNode distActivity = wfBpmnModelService.findFlowNodeByActivityId(processDefinition.getId(), bo.getTargetKey());
        if (distActivity != null) {
            UserTask userTask = (UserTask) distActivity;
            if (StringUtils.isNotBlank(userTask.getSkipExpression()) && userTask.getSkipExpression().equals("${initiator == ''}")) {
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(bo.getProcInsId()).singleResult();
                runtimeService.setVariable(bo.getProcInsId(), FlowConstant.FLOW_SUBMITTER_VAR, processInstance.getStartUserId());
            }
        }
        try {
            //TODO 14.1设置流程状态为已退回的状态
            runtimeService.setVariable(task.getProcessInstanceId(), ProcessConstants.PROCESS_STATUS_KEY, ProcessStatus.RETURN.getStatus());
            //TODO 14.2   1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetKey 跳转到的节点(1)
            runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId()).moveActivityIdsToSingleActivityId(currentIds, bo.getTargetKey()).changeState();
        } catch (FlowableObjectNotFoundException e) {
            throw new RuntimeException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new RuntimeException("无法取消或开始活动");
        }
        //TODO 15.修改处理人节点的处理人为发起人
        if (distActivity != null) {
            UserTask userTask = (UserTask) distActivity;
            //TODO 14.1 选择流程发起人
            if (StringUtils.isNotBlank(userTask.getSkipExpression()) && userTask.getSkipExpression().equals("${initiator == ''}")) {
                // TODO 14.2 获取流程实例
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(bo.getProcInsId()).singleResult();
                // TODO 14.3 查询任务
                Task task1 = taskService.createTaskQuery().processInstanceId(bo.getProcInsId()).singleResult();
                // TODO 14.4 设置处理人是发起人
                task1.setAssignee(processInstance.getStartUserId());
                // TODO 14.5 保存任务
                taskService.saveTask(task1);
            }
            //TODO 查看当前跳转的节点生成的任务是否存在处理人-----如果不存在处理人，则根据任务最新完成的候选人赋值
            else {
                if (target instanceof UserTask) {
                    List<Task> taskList = taskService.createTaskQuery()
                        .processInstanceId(bo.getProcInsId()).list();
                    List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(bo.getProcInsId()).taskDefinitionKey(bo.getTargetKey())
                        .orderByHistoricTaskInstanceEndTime().finished().desc().list();
                    HistoricTaskInstance historicTask = historicTasks.get(0);
                    List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery()
                        .executionId(historicTask.getExecutionId())
                        .taskDefinitionKey(bo.getTargetKey()).orderByHistoricTaskInstanceEndTime()
                        .finished().asc().list();
                    List<String> assigneeList = new ArrayList<>();
                    for (HistoricTaskInstance tasks : hisTaskList) {
                        if (StringUtils.isNotBlank(tasks.getAssignee())) {
                            assigneeList.add(tasks.getAssignee());
                        }
                    }
                    List<HistoricIdentityLink> historyIdentityLinkList = historyService.getHistoricIdentityLinksForTask(
                        historicTask.getId());
                    historyIdentityLinkList.stream()
                        .filter(link -> link.getType().equals(IdentityLinkType.CANDIDATE))
                        .forEach(link -> {
                            if (link.getUserId() != null && !assigneeList.contains(
                                link.getUserId())) {
                                assigneeList.add(link.getUserId());
                            }
                        });
                    if (assigneeList.size() > 0) {
                        Task nowTask = taskList.get(0);
                        List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(
                            nowTask.getId());
                        //TODO 如果当前任务的处理人和候选处理人均为空的情况
                        if (identityLinkList.size() == 0 && StringUtils.isBlank(
                            nowTask.getAssignee())) {
                            //TODO 任务是一条，候选人也是一条
                            if (taskList.size() == 1 && assigneeList.size() == 1) {
                                taskService.setAssignee(nowTask.getId(), assigneeList.get(0));
                            } else {
                                taskList.forEach(item -> {
                                    if (StringUtils.isBlank(task.getAssignee())) {
                                        assigneeList.forEach(userId -> {
                                            taskService.addCandidateUser(item.getId(), userId);
                                        });
                                    }
                                });
                            }
                        }
                    } else {
                        //TODO 历史数据可能会导致为空，那么就根据下一级传入赋值
                        if (StringUtils.isNotBlank(bo.getNextUserIds())) {
                            List<String> userIdList = Arrays.asList(bo.getNextUserIds().split(","));
                            taskList.forEach(item -> {
                                if (StringUtils.isBlank(task.getAssignee())) {
                                    userIdList.forEach(userId -> {
                                        taskService.addCandidateUser(item.getId(), userId);
                                    });
                                }
                            });
                        }
                    }
                }
            }
        }
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(bo.getProcInsId())
            .list();
        if (taskList.size() > 0) {
            Task newTask = taskList.get(0);
            List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(
                newTask.getId());
            //TODO 如果当前节点的处理人和候选人都为空
            // historicTaskInstance 为 null 时表示目标节点没有历史任务可参照，直接跳过还原逻辑
            // （新任务仍会按流程模型上的 candidateUsers / assignee 配置生成，不会有副作用）
            if (historicTaskInstance != null
                && (identityLinkList.size() == 0 || StringUtils.isBlank(newTask.getAssignee()))) {
                List<HistoricIdentityLink> historyIdentityLinkList = historyService.getHistoricIdentityLinksForTask(
                    historicTaskInstance.getId());
                if (historyIdentityLinkList.size() > 0) {
                    List<String> userIdList = new ArrayList<>();
                    for (HistoricIdentityLink link : historyIdentityLinkList) {
                        //TODO 候选人员
                        if (link.getType().equals("candidate") && link.getUserId() != null) {
                            if (!userIdList.contains(link.getUserId())) {
                                userIdList.add(link.getUserId());
                            }
                        }
                        //TODO 候选角色
                        else if (link.getType().equals("candidate") && link.getGroupId() != null) {
                            List<SysUser> userList = sysUserService.selectRoleToUser(
                                Long.valueOf(link.getGroupId()));
                            userList.forEach(user -> {
                                if (!userIdList.contains(user.getUserId())) {
                                    userIdList.add(user.getUserId().toString());
                                }
                            });
                        } //TODO 任务处理人
                        else if (link.getType().equals("assignee") && link.getUserId() != null) {
                            if (!userIdList.contains(link.getUserId())) {
                                userIdList.add(link.getUserId());
                            }
                        }
                    }
                    for (Task task1 : taskList) {
                        userIdList.forEach(userId -> {
                            taskService.addCandidateUser(task1.getId(), userId);
                        });
                    }
                } else {
                    taskService.setAssignee(newTask.getId(), historicTaskInstance.getAssignee());
                }
            }

            //TODO 如果传了这个，那么需要删除多余的会签任务
            if (StringUtils.isNotBlank(bo.getMultiInstance())) {
                if (taskList.size() > 1 && bo.getMultiInstance().equals("ParallelMultiInstance")) {
                    taskList.forEach(t -> {
                        if (!task.getId().equals(t.getId())) {
                            WfTaskBo bo2 = new WfTaskBo();
                            bo2.setProcessStatus("running");
                            bo2.setProcInsId(t.getProcessInstanceId());
                            bo2.setTaskId(t.getId());
                            wfDeleteProcessService.deleteProcessTask(bo2);
                        }
                    });
                    taskService.setAssignee(task.getId(), null);
                }
            }
        }

        // TODO 16.保存上传的附件
        if (StringUtils.isNotEmpty(bo.getFileList())) {
            wfFlowableTaskService.saveFileToModel(bo, task.getTaskDefinitionKey());
        }
        // 设置任务节点名称
        bo.setTaskName(task.getName());
        // TODO 17. 处理抄送用户
        if (!wfCopyService.makeCopy(bo)) {
            throw new RuntimeException("抄送任务失败");
        }
        return isSequential;
    }

    /**
     * @return java.util.List<org.flowable.bpmn.model.FlowElement>
     * @description TODO (获取所有可回退的节点)
     * @params [bo]
     * @author yanbin
     * @date 2023/11/3 10:39
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<FlowElement> findReturnTaskList(WfTaskBo bo) {
        // TODO 1.当前任务 task
        Task task = taskService.createTaskQuery().taskId(bo.getTaskId()).singleResult();
        // TODO 2. 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        //TODO 3.获取流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        // TODO 4. 获取流程模型信息bpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        // TODO 5. 查询历史节点实例
        List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(task.getProcessInstanceId()).activityType(BpmnXMLConstants.ELEMENT_TASK_USER).finished().orderByHistoricActivityInstanceEndTime().asc().list();
        // TODO 6.获取活动id即activityId集合
        List<String> activityIdList = activityInstanceList.stream().map(HistoricActivityInstance::getActivityId).filter(activityId -> !StringUtils.equals(activityId, task.getTaskDefinitionKey())).distinct().collect(Collectors.toList());
        // TODO 7. 获取当前任务节点元素
        FlowElement source = ModelUtils.getFlowElementById(bpmnModel, task.getTaskDefinitionKey());
        List<FlowElement> elementList = new ArrayList<>();
        // TODO 8.筛选节点信息
        for (String activityId : activityIdList) {
            FlowElement target = ModelUtils.getFlowElementById(bpmnModel, activityId);
            // 改为检查目标节点是否能到达当前节点（反向验证）
            boolean isUpstream = ModelUtils.isSequentialReachable(target, source, new HashSet<>());
            if (isUpstream) {
                elementList.add(target);
            }
        }
        return elementList;
    }

    /**
     * @return java.lang.Boolean
     * @description TODO (默认驳回到上一节点)
     * @params [taskBo]
     * @author yanbin
     * @date 2024/5/29 8:46
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public Boolean rejectLastTask(WfTaskBo taskBo) {
        Task task = taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(taskBo.getProcInsId());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(extendProcinst.getProcDefId());
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        //TODO 获取上一节点的id
        String targetKey = ModelUtils.getLastUserTask(flowNode, bpmnModel);
        if (StringUtils.isNotBlank(targetKey)) {
            //TODO 这里我考虑可以直接判断为拒绝整条流程
            if (targetKey.equals("startEvent")) {
//                wfRejectTaskService.taskReject(taskBo);
                throw new RuntimeException("上一节点为开始节点，不允许进行驳回操作!");
            }
            //TODO  执行退回操作
            taskBo.setTargetKey(targetKey);
            this.taskReturn(taskBo);
        }
        return true;
    }
}
