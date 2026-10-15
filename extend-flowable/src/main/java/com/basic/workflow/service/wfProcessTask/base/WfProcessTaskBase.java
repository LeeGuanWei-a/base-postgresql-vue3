package com.basic.workflow.service.wfProcessTask.base;

import com.basic.common.utils.StringUtils;
import com.basic.flowable.factory.FlowServiceFactory;
import com.basic.flowable.utils.ModelUtils;
import com.basic.system.service.ISysRoleService;
import com.basic.system.service.ISysUserService;
import com.basic.workflow.cmd.AddHisCommentCmd;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.WfCommentInfo;
import com.basic.workflow.domain.WfDeployForm;
import com.basic.workflow.domain.WfTaskForm;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.vo.CommentVo;
import com.basic.workflow.mapper.WfActHiActinstMapper;
import com.basic.workflow.mapper.WfActRuActinstMapper;
import com.basic.workflow.mapper.WfFlowableProcessMapper;
import com.basic.workflow.mapper.WfFlowableTaskMapper;
import com.basic.workflow.service.IWfBpmnModelService;
import com.basic.workflow.service.IWfCommentInfoService;
import com.basic.workflow.service.IWfCopyService;
import com.basic.workflow.service.IWfDeployFormService;
import com.basic.workflow.service.IWfExtendProcinstService;
import com.basic.workflow.service.IWfTaskFormService;
import com.basic.workflow.service.impl.ExtensionElementsService;
import com.basic.workflow.service.impl.InjectUserTaskInProcessInstanceService;
import com.basic.workflow.service.impl.WfFlowableTaskServiceImpl;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

/**
 * @author yanbin
 * @description TODO (flowable审批任务抽象服务类)
 * @date 2024/03/01
 */
public abstract class WfProcessTaskBase extends FlowServiceFactory {
    @Resource
    protected IWfCommentInfoService wfCommentInfoService;
    @Resource
    protected IWfCopyService wfCopyService;
    @Resource
    protected WfFlowableTaskServiceImpl wfFlowableTaskService;
    @Resource
    protected IWfDeployFormService wfDeployFormService;
    @Resource
    protected IWfTaskFormService wfTaskFormService;
    @Resource
    protected IWfBpmnModelService wfBpmnModelService;
    @Resource
    protected ISysUserService sysUserService;
    @Resource
    protected InjectUserTaskInProcessInstanceService injectUserTaskInProcessInstanceService;
    @Resource
    protected WfActRuActinstMapper wfActRuActinstMapper;
    @Resource
    protected WfActHiActinstMapper wfActHiActinstMapper;
    @Resource
    protected WfFlowableTaskMapper wfFlowableTaskMapper;
    @Resource
    protected IWfExtendProcinstService extendProcinstService;
    @Resource
    protected ExtensionElementsService extensionElementsService;
    @Resource
    protected ISysRoleService sysRoleService;
    @Resource
    protected WfFlowableProcessMapper wfFlowableProcessMapper;

    /**
     * @return void
     * @description TODO (添加审批意见)
     * @params [taskId 任务id, userId处理人id, procInsId实例id, type意见类型, message 意见内容]
     * @author yanbin
     * @date 2024/3/1 15:40
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected void addComment(String taskId, String userId, String processInstanceId, String type, String message) {
        if (taskId != null) {
            //拓展表记录审批记录
            wfCommentInfoService.addCommentInfo(taskId, userId, type, message);
            // 添加备注
            CommentVo comment = new CommentVo(taskId, userId, processInstanceId, type, message);
            managementService.executeCommand(new AddHisCommentCmd(comment.getTaskId(), comment.getUserId(), comment.getProcessInstanceId(), comment.getType(), comment.getMessage()));
        } else {
            WfCommentInfo wfCommentInfo = new WfCommentInfo(userId, processInstanceId, type, message);
            wfCommentInfoService.saveOrUpdate(wfCommentInfo);
        }
    }

    /**
     * @return void
     * @description TODO (保存表单内容)
     * @params [id, procInsId, currentUserId, taskDefinitionKey, formData]
     * @author yanbin
     * @date 2024/3/1 15:43
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected void saveTaskForForm(String taskId, String procInsId, String userId, String activityId, String formData) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        //这里是获取旧的流程部署表单
        ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(procInsId);
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(extendProcinst.getProcDefId()).singleResult();
        WfDeployForm wfDeployForm = wfDeployFormService.selectFormByDeployIdAndActId(processDefinition.getDeploymentId(), activityId);
        if (wfDeployForm == null) {
            return;
        }
        WfTaskForm wfTaskForm = new WfTaskForm(taskId, formData, procInsId, userId, activityId, wfDeployForm.getFormId());
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (extendProcinst != null) {
            //这里要判断是提交人自动跳过的节点
            UserTask userTask = wfBpmnModelService.findUserTaskForProcDefIdAndKey(extendProcinst.getProcDefId(), historicTaskInstance.getTaskDefinitionKey());
            if (StringUtils.isNotBlank(userTask.getSkipExpression()) && userTask.getSkipExpression().equals("${initiator == ''}")) {
                //获取bpmnModel
                BpmnModel bpmnModel = wfBpmnModelService.getBpmnModelByProcessDefId(extendProcinst.getProcDefId());
                //查找开始节点
                StartEvent start = wfBpmnModelService.findStartFlowElement(bpmnModel.getMainProcess());
                wfTaskForm.setActivityId(start.getId());
            }
        }
        wfTaskFormService.saveProcessForm(wfTaskForm);

    }

    /**
     * @return boolean
     * @description TODO (处理抄送用户)
     * @params [taskBo]
     * @author yanbin
     * @date 2024/3/1 15:42
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected boolean makeCopy(WfTaskBo taskBo) {
        return wfCopyService.makeCopy(taskBo);
    }


    /**
     * @return java.util.List<java.lang.String>
     * @description TODO (根据实例procInsId和activityId查询任务id)
     * @params [procInsId, activityId]
     * @author yanbin
     * @date 2024/3/1 16:45
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected List<String> selectProcInsIdActivity(String procInsId, String activityId) {
        return wfCommentInfoService.selectProcInsIdActivity(procInsId, activityId);
    }

    /**
     * @return boolean
     * @description TODO (判断当前任务是不是多实例节点)
     * @params [task]
     * @author yanbin
     * @date 2023/11/16 9:10
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected boolean checkMultiInstance(TaskEntity task) {
        boolean flag = false;
        //流程定义id
        String processDefId = task.getProcessDefinitionId();
        //流程activityId
        String taskDefinitionKey = task.getTaskDefinitionKey();
        //获取bpmnModel
        BpmnModel bpmnModel = wfBpmnModelService.getBpmnModelByProcessDefId(processDefId);
        //获取usertask集合
        Collection<UserTask> userTasks = ModelUtils.getAllUserTaskEvent(bpmnModel);
        if (CollectionUtils.isNotEmpty(userTasks)) {
            for (UserTask userTask : userTasks) {
                if (userTask.getId().equals(taskDefinitionKey) && userTask.hasMultiInstanceLoopCharacteristics()) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
}
