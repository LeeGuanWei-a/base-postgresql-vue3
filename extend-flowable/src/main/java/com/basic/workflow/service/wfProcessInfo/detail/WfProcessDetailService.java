package com.basic.workflow.service.wfProcessInfo.detail;

import cn.hutool.core.collection.CollUtil;
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
import org.dom4j.DocumentException;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description 流程详情查询服务，组装流程实例的 BPMN XML、历史节点、流程图与审批意见等详情
 * @author yanbin
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
     * @return WfDetailVo 流程详情，含 BPMN XML、历史节点、流程图执行过程、流程状态、可办理权限与审批意见
     * @description 按流程实例 ID 组装流程详情，传入任务 ID 时额外带上当前任务的表单与任务信息；实例不存在或任务已不可办理时抛出 ServiceException
     * @params [procInsId, taskId] 流程实例 ID，为空时直接返回空详情；当前任务 ID，可空
     * @author liguanwei
     * @date 2026/10/17 20:52
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
                // 获取当前任务信息
                detailVo.setCurrentTask(wfProcessCurrentTaskService.getCurrentTaskInfo(taskId));
            }
        }
        // 流程定义id
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
        // 获取历史任务信息列表
        detailVo.setHistoryProcNodeList(
                wfHistoryProcNodeService.historyProcNodeList(historicProcIns.getId()));
        // 获取历史流程表单信息
        // detailVo.setProcessFormList(wfProcessHiFormService.processFormList(historicProcIns.getId()));
        // 获取流程执行过程
        detailVo.setFlowViewer(wfProcessFlowViewerService.getFlowViewer(procInsId));
        // 获取流程状态,eg:结束还是进行中
        detailVo.setProcessStatus(this.checkFlowIsEnd(procInsId));
        // 获取当前任务数量
        detailVo.setCurrentTaskNum(wfProcessCurrentTaskService.getCurrentTaskNum(procInsId));
        // 获取当前节点的操作权限
        detailVo.setPermission(wfProcessCurrentTaskService.getPermission(taskId));
        // 流转记录 意见
        detailVo.setCommentInfo(wfProcessCommentService.getProcessCommentInfo(procInsId));
        return detailVo;
    }

    /**
     * @return WfTaskFormVo 当前任务的表单配置与已提交的表单内容，任务或表单不存在时返回 null
     * @description 按部署 ID 与任务 ID 取出节点表单，改过图时改用流程启动时记录的部署再查一次，并回填该节点已提交的表单内容
     * @params [deployId, taskId] 流程部署 ID；当前任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfTaskFormVo currTaskFormData(String deployId, String taskId) {
        WfTaskFormVo wfTaskFormVo = new WfTaskFormVo();
        // 获取当前任务（任务可能已被处理完，查不到就直接返回 null，避免 NPE）
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return null;
        }
        // 根据部署deployId和activityId查询节点表单
        WfDeployForm wfDeployForm =
                wfDeployFormService.selectFormByDeployIdAndActId(deployId, task.getTaskDefinitionKey());
        // 下面判断是否进行了指定审批人的操作->>>流程图改变，导致流程定义发生改变，获取自定义表单通过拓展表获取
        ExtendProcinst exportProcinst =
                extendProcinstService.selectProcInstanId(task.getProcessInstanceId());
        // 说明：原实现写成 exportProcinst.equals(processDefinitionId)，
        // 对象与字符串比较恒为 false（且 exportProcinst 为空时 NPE），永远走"改过图"的分支。
        // 正确判断是：启动时记录的流程定义 id 与当前任务所属流程定义 id 是否一致。
        if (exportProcinst == null
                || task.getProcessDefinitionId().equals(exportProcinst.getProcDefId())) {
            // 流程定义未发生改变，用当前任务的部署
        } else {
            ProcessDefinition processDefinition =
                    repositoryService.getProcessDefinition(exportProcinst.getProcDefId());
            if (processDefinition != null) {
                wfDeployForm =
                        wfDeployFormService.selectFormByDeployIdAndActId(
                                processDefinition.getDeploymentId(), task.getTaskDefinitionKey());
            }
        }
        if (wfDeployForm != null) {
            List<WfTaskForm> wfTaskFormList =
                    wfTaskFormService.selectFormJsonDataByProcInsIdAndActIdToList(
                            task.getProcessInstanceId(), task.getTaskDefinitionKey());
            if (CollUtil.isNotEmpty(wfTaskFormList)) {
                wfTaskFormVo.setFormContent(wfTaskFormList.get(0).getFormContent());
            }
            wfTaskFormVo.setId(wfDeployForm.getDeployId());
            wfTaskFormVo.setDesignerContent(wfDeployForm.getContent());
            return wfTaskFormVo;
        }
        return null;
    }

    /**
     * @return String 流程状态，已结束返回 finish，进行中或实例 ID 为空返回 running
     * @description 按流程实例 ID 查历史流程实例，有结束时间则判定流程已结束
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
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
