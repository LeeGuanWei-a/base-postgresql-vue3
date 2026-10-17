package com.basic.workflow.service.impl;

import com.basic.common.utils.StringUtils;
import com.basic.flowable.factory.FlowServiceFactory;
import com.basic.flowable.utils.ModelUtils;
import com.basic.flowable.utils.UUIDGenerator;
import com.basic.system.domain.SysFiles;
import com.basic.system.service.ISysFilesService;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.WfCategory;
import com.basic.workflow.domain.WfCommentInfo;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.vo.WfCategoryVo;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.mapper.WfActHiActinstMapper;
import com.basic.workflow.mapper.WfActRuActinstMapper;
import com.basic.workflow.service.IWfBpmnModelService;
import com.basic.workflow.service.IWfCategoryService;
import com.basic.workflow.service.IWfCommentInfoService;
import com.basic.workflow.service.IWfExtendProcinstService;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.UserTask;
import cn.hutool.core.collection.CollUtil;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ActivityInstanceEntity;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description Flowable 任务业务实现，负责任务查询、办理、加签转办与流程实例清理等引擎侧操作
 * @author yanbin
 * @date 2024/02/23
 */
@Service
public class WfFlowableTaskServiceImpl extends FlowServiceFactory {
    @Autowired
    private IWfCommentInfoService wfCommentInfoService;
    @Autowired
    private IWfBpmnModelService wfBpmnModelService;
    @Autowired
    private ExtensionElementsService extensionElementsService;
    @Autowired
    private ISysFilesService sysFilesService;
    @Autowired
    private WfActRuActinstMapper wfActRuActinstMapper;
    @Autowired
    private WfActHiActinstMapper wfActHiActinstMapper;
    @Autowired
    private IWfExtendProcinstService wfExtendProcinstService;
    @Autowired
    private IWfCategoryService wfCategoryService;

    /**
     * @return UserTask 该任务所处的用户任务节点定义，找不到时为 null
     * @description 由任务 ID 查历史任务，再按流程定义与节点标识取出对应的用户任务节点
     * @params [taskId] 任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public UserTask getUserTaskForTaskId(String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        return wfBpmnModelService.findUserTaskForProcDefIdAndKey(historicTaskInstance.getProcessDefinitionId(), historicTaskInstance.getTaskDefinitionKey());
    }


    /**
     * @return String 该任务最新一条审批意见的类型，无意见或类型为空时返回 null
     * @description 查出任务的审批意见，取第一条非空类型作为任务当前所处审批状态
     * @params [taskId] 任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String checkTaskRevokeFor(String taskId) {
        //获取审批意见
        List<WfCommentInfo> wfCommentInfo = wfCommentInfoService.selectTaskIdForComment(taskId);
        for (WfCommentInfo commentInfo : wfCommentInfo) {
            String type = commentInfo.getType();
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    /**
     * @return boolean true 表示该任务所在节点配置了多实例特性
     * @description 由任务 ID 取流程模型中的用户任务节点，判断其是否带多实例特性
     * @params [taskId] 任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean checkMultiInstanceForTaskId(String taskId) {
        //获取任务信息
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (historicTaskInstance == null) {
            return false;
        }
        //获取bpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicTaskInstance.getProcessDefinitionId());
        //获取userTask
        UserTask userTask = ModelUtils.getUserTaskByKey(bpmnModel, historicTaskInstance.getTaskDefinitionKey());
        if (userTask.hasMultiInstanceLoopCharacteristics()) {
            return true;
        }
        return false;
    }

    /**
     * @return String 多实例节点的会签或或签标识，非多实例节点返回 null
     * @description 取多实例节点的完成条件，条件为全部实例完成时判为会签，否则判为或签
     * @params [taskId] 任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String checkMultiInstanceToLoopType(String taskId) {
        //首先判断 当前任务是多实例任务
        if (this.checkMultiInstanceForTaskId(taskId)) {
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            UserTask userTask = wfBpmnModelService.findUserTaskForProcDefIdAndKey(historicTaskInstance.getProcessDefinitionId(), historicTaskInstance.getTaskDefinitionKey());
            if (userTask != null) {
                MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = extensionElementsService.getMulTiInstForUserTask(userTask);
                //获取多实例完成条件
                String completionCondition = multiInstanceLoopCharacteristics.getCompletionCondition();
                if (completionCondition.equals("${nrOfCompletedInstances >= nrOfInstances}")) {
                    return "会签";
                } else {
                    return "或签";
                }
            }
        }
        return null;
    }

    /**
     * @return boolean true 表示该会签节点按串行顺序依次执行
     * @description 会签节点才继续判断，按流程定义与节点标识取多实例是否串行执行，或签或非多实例返回 false
     * @params [taskId] 任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean checkMultiInstanceToSequentialForTaskId(String taskId) {
        if (this.checkMultiInstanceToLoopType(taskId).equals("会签")) {
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            return extensionElementsService.getMulTIinstSequentialForKey(historicTaskInstance.getProcessDefinitionId(), historicTaskInstance.getTaskDefinitionKey());
        }
        return false;
    }

    /**
     * @return TaskEntity 新建并已落库的子任务，父任务为空时返回 null
     * @description 以父任务的分类、租户、流程定义等信息复制出一个子任务，挂到指定父任务下并指定执行人
     * @params [currentUserId, taskEntity, finalParentTaskId, assignee] 创建人 ID，写为子任务 owner；父任务实体；最终父任务 ID；子任务执行人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public TaskEntity createSubTask(String currentUserId, TaskEntity taskEntity, String finalParentTaskId, String assignee) {
        TaskEntity task = null;
        if (taskEntity != null) {
            //生成子任务
            task = (TaskEntity) taskService.newTask(UUIDGenerator.generate());
            task.setCategory(taskEntity.getCategory());
            task.setDescription(taskEntity.getDescription());
            task.setTenantId(taskEntity.getTenantId());
            task.setOwner(currentUserId);
            task.setAssignee(assignee);
            task.setName(taskEntity.getName());
            task.setParentTaskId(finalParentTaskId);
            task.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
            task.setProcessInstanceId(taskEntity.getProcessInstanceId());
            task.setTaskDefinitionKey(taskEntity.getTaskDefinitionKey());
            task.setTaskDefinitionId(taskEntity.getTaskDefinitionId());
            task.setPriority(taskEntity.getPriority());
            task.setCreateTime(new Date());
            taskService.saveTask(task);
        }
        return task;
    }

    /**
     * @return TaskEntity 按历史任务信息新建并已落库的任务，入参为空时返回 null
     * @description 用历史任务的字段复制出一个新的运行态任务并保存，一般用于删掉旧任务后重建当前节点任务
     * @params [historicTaskInstance] 作为复制来源的历史任务
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public TaskEntity createNewTask(HistoricTaskInstance historicTaskInstance) {
        TaskEntity task = null;
        if (historicTaskInstance != null) {
            //生成子任务
            task = (TaskEntity) taskService.newTask(UUIDGenerator.generate());
            task.setCategory(historicTaskInstance.getCategory());
            task.setDescription(historicTaskInstance.getDescription());
            task.setTenantId(historicTaskInstance.getTenantId());
            task.setAssignee(historicTaskInstance.getAssignee());
            task.setName(historicTaskInstance.getName());
            task.setProcessDefinitionId(historicTaskInstance.getProcessDefinitionId());
            task.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());
            task.setTaskDefinitionKey(historicTaskInstance.getTaskDefinitionKey());
            task.setTaskDefinitionId(historicTaskInstance.getTaskDefinitionId());
            task.setPriority(historicTaskInstance.getPriority());
            task.setCreateTime(new Date());
            taskService.saveTask(task);
        }
        return task;
    }

    /**
     * @return TaskEntity 复用原任务 ID 的新任务对象，入参为空时返回 null
     * @description 沿用原任务 ID 与字段重建任务并替换执行人，避免退回提交人节点时把昵称写入身份表导致保存出错
     * @params [taskEntity, assignee] 作为复制来源的任务实体；新的任务执行人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public TaskEntity createNewTask(TaskEntity taskEntity, String assignee) {
        TaskEntity task = null;
        if (taskEntity != null) {
            //生成新任务
            task = (TaskEntity) taskService.newTask(taskEntity.getId());
            task.setCategory(taskEntity.getCategory());
            task.setDescription(taskEntity.getDescription());
            task.setTenantId(taskEntity.getTenantId());
            task.setAssignee(assignee);
            task.setOriginalPersistentState(taskEntity.getOriginalPersistentState());
            task.setFormKey(taskEntity.getFormKey());
            task.setName(taskEntity.getName());
            task.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
            task.setProcessInstanceId(taskEntity.getProcessInstanceId());
            task.setTaskDefinitionKey(taskEntity.getTaskDefinitionKey());
            task.setTaskDefinitionId(taskEntity.getTaskDefinitionId());
            task.setPriority(taskEntity.getPriority());
            task.setCreateTime(taskEntity.getCreateTime());
        }
        return task;
    }

    /**
     * @description 把任务提交时上传的附件逐个绑定到任务、流程实例、处理人与当前活动节点上
     * @params [taskBo, activityId] 任务业务参数，含附件列表与当前处理人信息；当前活动节点 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void saveFileToModel(WfTaskBo taskBo, String activityId) {
        //处理上传的文件
        for (Map<String, Object> file : taskBo.getFileList()) {
            SysFiles item = sysFilesService.getById(Long.parseLong(file.get("id").toString()));
            //任务id
            item.setTaskId(taskBo.getTaskId());
            //流程实例id
            item.setProcessInstanceId(taskBo.getProcInsId());
            //处理人id
            item.setUserId(Long.valueOf(taskBo.getCurrentUserId()));
            //处理人nickName
            item.setNickName(taskBo.getCurrentUserName());
            //活动id
            item.setActivityId(activityId);
            sysFilesService.saveOrUpdate(item);
        }
    }

    /**
     * @description 按任务 ID 与流程实例 ID 找出该任务之后的活动实例，删除其运行态与历史态记录
     * @params [taskId, procInsId] 任务 ID；流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void deleteTaskIdActivity(String taskId, String procInsId) {
        String tableName = managementService.getTableName(ActivityInstanceEntity.class);
        String sql = "select t.* from " + tableName + " t where t.TASK_ID_=#{taskId} and t.PROC_INST_ID_ = #{procInsId} " + " order by t.END_TIME_ ASC";
        List<ActivityInstance> disActivities = runtimeService.createNativeActivityInstanceQuery().sql(sql).parameter("taskId", taskId).parameter("procInsId", procInsId).list();
        //删除运行时和历史节点信息
        if (CollUtil.isNotEmpty(disActivities)) {
            ActivityInstance activityInstance = disActivities.get(0);
            sql = "select t.* from " + tableName + " t where t.TASK_ID_=#{taskId} and (t.END_TIME_ >= #{endTime} or t.END_TIME_ is null)";
            List<ActivityInstance> datas = runtimeService.createNativeActivityInstanceQuery().sql(sql).parameter("taskId", taskId).parameter("endTime", activityInstance.getEndTime()).list();
            List<String> runActivityIds = new ArrayList<>();
            if (CollUtil.isNotEmpty(datas)) {
                datas.forEach(ai -> runActivityIds.add(ai.getId()));
                wfActRuActinstMapper.deleteRunActinstsByIds(runActivityIds);
                wfActHiActinstMapper.deleteHisActinstsByIds(runActivityIds);
            }
        }
    }

    /**
     * @description 按流程实例 ID 与活动节点 ID 找出节点跳转后的活动实例，删除其运行态与历史态记录
     * @params [disActivityId, processInstanceId] 跳转到的活动节点 ID；流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void deleteProcInsIdActivity(String disActivityId, String processInstanceId) {
        String tableName = managementService.getTableName(ActivityInstanceEntity.class);
        String sql = "select t.* from " + tableName + " t where t.PROC_INST_ID_=#{processInstanceId} and t.ACT_ID_ = #{disActivityId} " + " order by t.END_TIME_ ASC";
        List<ActivityInstance> disActivities = runtimeService.createNativeActivityInstanceQuery().sql(sql).parameter("processInstanceId", processInstanceId).parameter("disActivityId", disActivityId).list();
        //删除运行时和历史节点信息
        if (CollUtil.isNotEmpty(disActivities)) {
            ActivityInstance activityInstance = disActivities.get(0);
            sql = "select t.* from " + tableName + " t where t.PROC_INST_ID_=#{processInstanceId} and (t.END_TIME_ >= #{endTime} or t.END_TIME_ is null)";
            List<ActivityInstance> datas = runtimeService.createNativeActivityInstanceQuery().sql(sql).parameter("processInstanceId", processInstanceId).parameter("endTime", activityInstance.getEndTime()).list();
            List<String> runActivityIds = new ArrayList<>();
            if (CollUtil.isNotEmpty(datas)) {
                datas.forEach(ai -> runActivityIds.add(ai.getId()));
                wfActRuActinstMapper.deleteRunActinstsByIds(runActivityIds);
                wfActHiActinstMapper.deleteHisActinstsByIds(runActivityIds);
            }
        }
    }

    /**
     * @return Boolean true 表示该流程实例存在历史记录
     * @description 按流程实例 ID 查历史流程实例，判断流程是否还存在，实例 ID 为空时直接判为不存在
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public Boolean checkProcessIsExistByProcInsId(String procInsId) {
        if (StringUtils.isBlank(procInsId)) {
            return false;
        }
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        if (historicProcessInstance != null) {
            return true;
        }
        return false;
    }

    /**
     * @return Boolean true 表示该任务所属流程实例存在历史记录
     * @description 由任务 ID 查历史任务，再用其流程实例 ID 查历史流程实例，判断流程是否还存在
     * @params [taskId] 任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public Boolean checkProcessIsExistByTaskId(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            return false;
        }
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult();
        if (historicProcessInstance != null) {
            return true;
        }
        return false;
    }

    /**
     * @return WfTaskVo 待办任务信息，含办理地址、任务 ID 与是否审批人标识；当前用户无权办理时返回 null
     * @description 按流程实例、当前用户及其角色查该用户待办的任务，命中则补上流程分类配置的办理地址并标记为审批人
     * @params [bo] 任务业务参数，含流程实例 ID、当前用户 ID 与当前用户角色 ID 集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfTaskVo checkUserIsAuditByProcInsIdAndUserIdAndGroupId(WfTaskBo bo) {
        if (StringUtils.isNotBlank(bo.getProcInsId()) && StringUtils.isNotBlank(bo.getCurrentUserId()) && CollUtil.isNotEmpty(bo.getCurrentGroupId())) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(bo.getProcInsId()).singleResult();
            String userId = bo.getCurrentUserId();
            if (processInstance != null) {
                WfTaskVo wfTaskVo = new WfTaskVo();
                // 查询条件 实例id，用户id，用户角色id  查询范围 处理人assignee/候选人/候选角色
                Task task = taskService.createTaskQuery().active().includeProcessVariables().processInstanceId(processInstance.getId()).taskCandidateGroupIn(bo.getCurrentGroupId()).taskCandidateOrAssigned(userId).singleResult();
                if (task != null) {
                    ExtendProcinst extendProcinst = wfExtendProcinstService.selectProcInstanId(processInstance.getId());
                    ProcessDefinition processDefinition =
                            repositoryService
                                    .createProcessDefinitionQuery()
                                    .processDefinitionId(extendProcinst.getProcDefId())
                                    .singleResult();
                    WfCategoryVo wfCategoryVo = wfCategoryService.queryByCode(processDefinition.getCategory());
                    wfTaskVo.setUrl(wfCategoryVo.getUrl());
                    wfTaskVo.setIsAuditUser(true);
                    wfTaskVo.setTaskId(task.getId());
                    return wfTaskVo;
                }
            }
        }
        return null;
    }

    /**
     * @return WfTaskVo 发起人标识信息；当前用户不是发起人时返回 null
     * @description 按流程实例与当前用户判断该用户是否为这次流程的发起人，是则标记发起人并回填流程实例 ID
     * @params [bo] 任务业务参数，含流程实例 ID 与当前用户 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfTaskVo checkUserIsStartByProcInsIdAndUserIdAndGroupId(WfTaskBo bo) {
        if (StringUtils.isNotBlank(bo.getProcInsId()) && StringUtils.isNotBlank(bo.getCurrentUserId())) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(bo.getProcInsId()).singleResult();
            if (processInstance != null) {
                WfTaskVo wfTaskVo = new WfTaskVo();
                if (bo.getCurrentUserId().equals(processInstance.getStartUserId())) {
                    wfTaskVo.setIsStartUser(true);
                    wfTaskVo.setProcInsId(processInstance.getId());
                }
            }
        }
        return null;
    }
}
