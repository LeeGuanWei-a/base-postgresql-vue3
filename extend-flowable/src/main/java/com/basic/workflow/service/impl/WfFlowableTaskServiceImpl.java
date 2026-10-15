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
import org.flowable.editor.language.json.converter.util.CollectionUtils;
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
 * @author yanbin
 * @description TODO (Flowable 任务类)
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
     * @return org.flowable.bpmn.model.UserTask
     * @description TODO (根据TaskId获取当前任务所处的UserTask)
     * @params [taskId]
     * @author yanbin
     * @date 2024/2/23 10:23
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public UserTask getUserTaskForTaskId(String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        return wfBpmnModelService.findUserTaskForProcDefIdAndKey(historicTaskInstance.getProcessDefinitionId(), historicTaskInstance.getTaskDefinitionKey());
    }


    /**
     * @return boolean
     * @description TODO (根据任务TaskId判断任务状态)
     * @params [taskId]
     * @author yanbin
     * @date 2024/2/23 8:56
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
     * @return boolean
     * @description TODO (根据taskId判断当前任务所在节点是否是多实例节点)
     * @params [taskId]
     * @author yanbin
     * @date 2024/2/23 9:15
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
     * @return java.lang.String
     * @description TODO (根据taskId判断当前任务所在节点多实例是会签还是或签)
     * @params [taskId]
     * @author yanbin
     * @date 2024/2/23 9:21
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
     * @return boolean
     * @description TODO (根据taskId判断任务是串行还是并行 true是顺序执行 false不是)---多实例节点会签节点
     * @params [taskId]
     * @author yanbin
     * @date 2024/2/23 9:41
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
     * @return void
     * @description TODO (创建子任务)
     * @params [taskEntity, finalParentTaskId, assignee(子任务的执行人)]
     * @author yanbin
     * @date 2023/11/3 17:16
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
     * @return org.flowable.task.service.impl.persistence.entity.TaskEntity
     * @description TODO (创建新任务)  一般用于将历史任务删除添加新任务
     * @params [historicTaskInstance]
     * @author yanbin
     * @date 2024/2/23 9:54
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
     * @return org.flowable.task.service.impl.persistence.entity.TaskEntity
     * @description TODO (退回提交人节点时  处理人不为id导致保存错误，因此新建新任务避免 identity表将NickName放入)
     * @params [taskEntity, assignee]
     * @author yanbin
     * @date 2024/3/13 9:57
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
     * @return void
     * @description TODO (保存上传的附件)   这里是流程保存附件，暂定
     * @params [taskBo]
     * @author yanbin
     * @date 2023/11/17 11:44
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
     * @return void
     * @description TODO  (删除历史节点信息) 根据实例id和任务id
     * @params [taskId 任务id, procInsId 实例id] act_ru_actinst act_hi_actinst
     * @author yanbin
     * @date 2024/1/18 15:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void deleteTaskIdActivity(String taskId, String procInsId) {
        String tableName = managementService.getTableName(ActivityInstanceEntity.class);
        String sql = "select t.* from " + tableName + " t where t.TASK_ID_=#{taskId} and t.PROC_INST_ID_ = #{procInsId} " + " order by t.END_TIME_ ASC";
        List<ActivityInstance> disActivities = runtimeService.createNativeActivityInstanceQuery().sql(sql).parameter("taskId", taskId).parameter("procInsId", procInsId).list();
        //删除运行时和历史节点信息
        if (CollectionUtils.isNotEmpty(disActivities)) {
            ActivityInstance activityInstance = disActivities.get(0);
            sql = "select t.* from " + tableName + " t where t.TASK_ID_=#{taskId} and (t.END_TIME_ >= #{endTime} or t.END_TIME_ is null)";
            List<ActivityInstance> datas = runtimeService.createNativeActivityInstanceQuery().sql(sql).parameter("taskId", taskId).parameter("endTime", activityInstance.getEndTime()).list();
            List<String> runActivityIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(datas)) {
                datas.forEach(ai -> runActivityIds.add(ai.getId()));
                wfActRuActinstMapper.deleteRunActinstsByIds(runActivityIds);
                wfActHiActinstMapper.deleteHisActinstsByIds(runActivityIds);
            }
        }
    }

    /**
     * @return void
     * @description TODO (删除跳转的历史节点信息)根据实例id和节点activityId
     * @params [activityId 跳转的节点id, procInsId 流程实例id]
     * @author yanbin
     * @date 2023/12/29 9:45
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void deleteProcInsIdActivity(String disActivityId, String processInstanceId) {
        String tableName = managementService.getTableName(ActivityInstanceEntity.class);
        String sql = "select t.* from " + tableName + " t where t.PROC_INST_ID_=#{processInstanceId} and t.ACT_ID_ = #{disActivityId} " + " order by t.END_TIME_ ASC";
        List<ActivityInstance> disActivities = runtimeService.createNativeActivityInstanceQuery().sql(sql).parameter("processInstanceId", processInstanceId).parameter("disActivityId", disActivityId).list();
        //删除运行时和历史节点信息
        if (CollectionUtils.isNotEmpty(disActivities)) {
            ActivityInstance activityInstance = disActivities.get(0);
            sql = "select t.* from " + tableName + " t where t.PROC_INST_ID_=#{processInstanceId} and (t.END_TIME_ >= #{endTime} or t.END_TIME_ is null)";
            List<ActivityInstance> datas = runtimeService.createNativeActivityInstanceQuery().sql(sql).parameter("processInstanceId", processInstanceId).parameter("endTime", activityInstance.getEndTime()).list();
            List<String> runActivityIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(datas)) {
                datas.forEach(ai -> runActivityIds.add(ai.getId()));
                wfActRuActinstMapper.deleteRunActinstsByIds(runActivityIds);
                wfActHiActinstMapper.deleteHisActinstsByIds(runActivityIds);
            }
        }
    }

    /**
     * @return java.lang.Boolean
     * @description TODO (根据实例id判断流程是否存在)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/6/4 10:41
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
     * @return java.lang.Boolean
     * @description TODO (根据任务id判断流程是否存在)
     * @params [taskId]
     * @author yanbin
     * @date 2024/6/4 10:42
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
     * @return com.basic.workflow.domain.vo.WfTaskVo
     * @description TODO (判断当前用户是否是当前任务的处理人)
     * @params [bo]
     * @author yanbin
     * @date 2024/6/4 14:32
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfTaskVo checkUserIsAuditByProcInsIdAndUserIdAndGroupId(WfTaskBo bo) {
        if (StringUtils.isNotBlank(bo.getProcInsId()) && StringUtils.isNotBlank(bo.getCurrentUserId()) && CollectionUtils.isNotEmpty(bo.getCurrentGroupId())) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(bo.getProcInsId()).singleResult();
            String userId = bo.getCurrentUserId();
            if (processInstance != null) {
                WfTaskVo wfTaskVo = new WfTaskVo();
                //TODO  查询条件 实例id，用户id，用户角色id  查询范围 处理人assignee/候选人/候选角色
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
     * @return com.basic.workflow.domain.vo.WfTaskVo
     * @description TODO (判断当前用户是否是当前流程的发起人)
     * @params [bo]
     * @author yanbin
     * @date 2024/6/4 14:33
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
