package com.basic.workflow.service.wfProcessInfo.detail;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.basic.common.exception.ServiceException;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.WfDeployForm;
import com.basic.workflow.domain.WfTaskForm;
import com.basic.workflow.domain.vo.WfDetailVo;
import com.basic.workflow.domain.vo.WfTaskFormVo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.dom4j.DocumentException;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yanbin
 * @description TODO (Flowable流程流转历史详情查询)
 * @date 2024/03/04
 */
@Service
public class WfProcessDetailService extends WfProcessInfoBase {
    @Autowired private WfProcessCommentService wfProcessCommentService;
    @Autowired private WfHistoryProcNodeService wfHistoryProcNodeService;
    @Autowired private WfProcessCurrentTaskService wfProcessCurrentTaskService;
    @Autowired private WfProcessFlowViewerService wfProcessFlowViewerService;
    @Autowired private WfProcessHiFormService wfProcessHiFormService;

    /**
     * @return com.basic.workflow.domain.vo.WfDetailVo
     * @description TODO (流程详情信息)
     * @params [procInsId, taskId]
     * @author yanbin
     * @date 2024/3/4 10:41
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfDetailVo queryProcessDetail(String procInsId, String taskId)
            throws DocumentException {
        WfDetailVo detailVo = new WfDetailVo();
        if (procInsId == null) {
            return detailVo;
        }
        // 获取流程实例
        HistoricProcessInstance historicProcIns =
                historyService
                        .createHistoricProcessInstanceQuery()
                        .processInstanceId(procInsId)
                        .includeProcessVariables()
                        .singleResult();
        // 流程与业务解耦后，不再按 businessKey 兜底查询
        if (StringUtils.isNull(historicProcIns)) {
            throw new ServiceException("流程不存在！");
        }
        procInsId = historicProcIns.getId();
        if (StringUtils.isNotBlank(taskId)) {
            HistoricTaskInstance taskIns =
                    historyService
                            .createHistoricTaskInstanceQuery()
                            .taskId(taskId)
                            .includeIdentityLinks()
                            .includeProcessVariables()
                            .includeTaskLocalVariables()
                            .singleResult();
            if (taskIns == null) {
                throw new ServiceException("没有可办理的任务！");
            }
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task != null) {
                // 获取当前任务流程表单信息
                detailVo.setTaskFormData(currTaskFormData(historicProcIns.getDeploymentId(), taskId));
                // TODO 获取当前任务信息
                detailVo.setCurrentTask(wfProcessCurrentTaskService.getCurrentTaskInfo(taskId));
            }
        }
        // TODO 流程定义id
        String procDefId = historicProcIns.getProcessDefinitionId();
        // bpmnModel  xml
        // 改图之后流程图发生变化,流程图拿旧的
        ExtendProcinst extendProcinst =
                extendProcinstService.selectProcInstanId(historicProcIns.getId());
        if (extendProcinst != null && !extendProcinst.getProcDefId().equals(procDefId)) {
            procDefId = extendProcinst.getProcDefId();
        }
        // 获取Bpmn模型信息
        InputStream inputStream = repositoryService.getProcessModel(procDefId);
        String bpmnXmlStr = StrUtil.utf8Str(IoUtil.readBytes(inputStream, false));
        detailVo.setBpmnXml(ModelUtils.addAttributeWithXmlDocument(bpmnXmlStr));
        // TODO 获取历史任务信息列表
        detailVo.setHistoryProcNodeList(
                wfHistoryProcNodeService.historyProcNodeList(historicProcIns.getId()));
        // TODO 获取历史流程表单信息
        // detailVo.setProcessFormList(wfProcessHiFormService.processFormList(historicProcIns.getId()));
        // TODO 获取流程执行过程
        detailVo.setFlowViewer(wfProcessFlowViewerService.getFlowViewer(procInsId));
        // TODO 获取流程状态,eg:结束还是进行中
        detailVo.setProcessStatus(this.checkFlowIsEnd(procInsId));
        // TODO 获取当前任务数量
        detailVo.setCurrentTaskNum(wfProcessCurrentTaskService.getCurrentTaskNum(procInsId));
        // TODO 获取当前节点的操作权限
        detailVo.setPermission(wfProcessCurrentTaskService.getPermission(taskId));
        // TODO 流转记录 意见
        detailVo.setCommentInfo(wfProcessCommentService.getProcessCommentInfo(procInsId));
        return detailVo;
    }

    /**
     * @return com.basic.workflow.domain.vo.WfTaskFormVo
     * @description TODO (根据部署deployID和taskId获取当前任务的表单)
     * @params [deployId, taskId]
     * @author yanbin
     * @date 2024/2/28 15:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfTaskFormVo currTaskFormData(String deployId, String taskId) {
        WfTaskFormVo wfTaskFormVo = new WfTaskFormVo();
        // 获取当前任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 根据部署deployId和activityId查询节点表单
        WfDeployForm wfDeployForm =
                wfDeployFormService.selectFormByDeployIdAndActId(deployId, task.getTaskDefinitionKey());
        // TODO  下面判断是进行是否进行了指定审批人的操作->>>流程图改变，导致流程定义发生改变，获取自定义表单通过拓展表获取
        ExtendProcinst exportProcinst =
                extendProcinstService.selectProcInstanId(task.getProcessInstanceId());
        if (exportProcinst.equals(task.getProcessDefinitionId())) {
            System.out.println("流程定义未发生改变");
        } else {
            ProcessDefinition processDefinition =
                    repositoryService.getProcessDefinition(exportProcinst.getProcDefId());
            wfDeployForm =
                    wfDeployFormService.selectFormByDeployIdAndActId(
                            processDefinition.getDeploymentId(), task.getTaskDefinitionKey());
        }
        if (wfDeployForm != null) {
            List<WfTaskForm> wfTaskFormList =
                    wfTaskFormService.selectFormJsonDataByProcInsIdAndActIdToList(
                            task.getProcessInstanceId(), task.getTaskDefinitionKey());
            //            wfTaskFormVo.setFormContent(wfTaskFormList.get(0).getFormContent());
            if (CollectionUtils.isNotEmpty(wfTaskFormList)) {
                wfTaskFormVo.setFormContent(wfTaskFormList.get(0).getFormContent());
            }
            wfTaskFormVo.setId(wfDeployForm.getDeployId());
            wfTaskFormVo.setDesignerContent(wfDeployForm.getContent());
            // 这里还有一个返回表单内容
            return wfTaskFormVo;
        }
        return null;
    }

    /**
     * @return java.lang.String
     * @description TODO (根据实例id判断流程是否已经结束)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/4 10:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String checkFlowIsEnd(String procInsId) {
        String data = "running";
        if (StringUtils.isNotBlank(procInsId)) {
            // 查询条件 -- 通过流程实例查询历史信息
            HistoricProcessInstance hpi =
                    processEngine
                            .getHistoryService()
                            .createHistoricProcessInstanceQuery()
                            .processInstanceId(procInsId)
                            .singleResult();
            if (hpi != null && hpi.getEndTime() != null) {
                data = "finish";
            }
        }
        return data;
    }
}
