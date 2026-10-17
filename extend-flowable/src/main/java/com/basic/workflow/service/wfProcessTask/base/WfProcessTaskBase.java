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
import jakarta.annotation.Resource;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import cn.hutool.core.collection.CollUtil;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

/**
 * @description 审批任务的抽象基类，集中注入任务、表单、意见、抄送与流程模型等服务并提供公用的流程操作方法
 * @author yanbin
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
     * @description 添加审批意见，任务存在时同时写入业务拓展表与引擎历史备注，任务为空时按流程实例补记
     * @params [taskId, userId, processInstanceId, type, message] 任务 ID，为空时按实例记录；处理人 ID；流程实例 ID；意见类型；意见内容
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @description 保存本次审批提交的表单内容，表单数据或实例为空、找不到部署表单时直接跳过；
     * 提交人自动跳过的节点改挂到开始节点上
     * @params [taskId, procInsId, userId, activityId, formData] 任务 ID；流程实例 ID；处理人 ID；节点（活动）ID；表单数据
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected void saveTaskForForm(String taskId, String procInsId, String userId, String activityId, String formData) {
        if (StringUtils.isBlank(formData) || StringUtils.isBlank(procInsId)) {
            // 本次没有提交表单数据（比如"转办/委派"只带意见），直接跳过
            return;
        }
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        //这里是获取旧的流程部署表单
        ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(procInsId);
        String procDefId = extendProcinst != null
                ? extendProcinst.getProcDefId()
                : (historicProcessInstance != null ? historicProcessInstance.getProcessDefinitionId() : null);
        if (StringUtils.isBlank(procDefId)) {
            return;
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
        if (processDefinition == null) {
            // 流程定义已被清理（例如流程清库），没有可关联的表单
            return;
        }
        WfDeployForm wfDeployForm = wfDeployFormService.selectFormByDeployIdAndActId(processDefinition.getDeploymentId(), activityId);
        if (wfDeployForm == null) {
            return;
        }
        WfTaskForm wfTaskForm = new WfTaskForm(taskId, formData, procInsId, userId, activityId, wfDeployForm.getFormId());
        HistoricTaskInstance historicTaskInstance = StringUtils.isBlank(taskId) ? null
                : historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (extendProcinst != null && historicTaskInstance != null) {
            //这里要判断是提交人自动跳过的节点
            UserTask userTask = wfBpmnModelService.findUserTaskForProcDefIdAndKey(procDefId, historicTaskInstance.getTaskDefinitionKey());
            if (userTask != null && StringUtils.isNotBlank(userTask.getSkipExpression())
                    && userTask.getSkipExpression().equals("${initiator == ''}")) {
                //获取bpmnModel
                BpmnModel bpmnModel = wfBpmnModelService.getBpmnModelByProcessDefId(procDefId);
                //查找开始节点
                StartEvent start = wfBpmnModelService.findStartFlowElement(bpmnModel.getMainProcess());
                if (start != null) {
                    wfTaskForm.setActivityId(start.getId());
                }
            }
        }
        wfTaskFormService.saveProcessForm(wfTaskForm);
    }

    /**
     * @return boolean 抄送生成成功返回 true
     * @description 按任务信息生成抄送记录，转发给抄送服务处理
     * @params [taskBo] 任务业务对象，携带流程实例与抄送人信息
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected boolean makeCopy(WfTaskBo taskBo) {
        return wfCopyService.makeCopy(taskBo);
    }


    /**
     * @return List<String> 该节点上已产生意见的任务 ID 集合
     * @description 按流程实例与节点 ID 查询该节点上已记录意见的任务 ID
     * @params [procInsId, activityId] 流程实例 ID；节点（活动）ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected List<String> selectProcInsIdActivity(String procInsId, String activityId) {
        return wfCommentInfoService.selectProcInsIdActivity(procInsId, activityId);
    }

    /**
     * @return boolean 该任务节点配置了多实例特性返回 true
     * @description 按任务的流程定义查出全部用户任务节点，判断当前节点是否配置多实例循环特性
     * @params [task] 待判断的任务实体
     * @author liguanwei
     * @date 2026/10/17 20:52
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
        if (CollUtil.isNotEmpty(userTasks)) {
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
