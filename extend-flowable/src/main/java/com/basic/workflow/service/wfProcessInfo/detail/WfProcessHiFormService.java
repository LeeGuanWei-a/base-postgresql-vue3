package com.basic.workflow.service.wfProcessInfo.detail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.basic.flowable.utils.ModelUtils;
import com.basic.flowable.utils.UUIDGenerator;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.WfDeployForm;
import com.basic.workflow.domain.WfTaskForm;
import com.basic.workflow.domain.vo.WfTaskFormVo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yanbin
 * @description TODO (flowable历史表单信息)
 * @date 2024/03/04
 */
@Service
public class WfProcessHiFormService extends WfProcessInfoBase {
    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskFormVo>
     * @description TODO (获取历史流程表单信息)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/4 10:45
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskFormVo> processFormList(String procInsId) {
        // TODO 获取流程实例
        HistoricProcessInstance historicTaskInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(procInsId).singleResult();
        //TODO 获取流程活动信息
        List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(procInsId).finished()
                .activityTypes(CollUtil.newHashSet(BpmnXMLConstants.ELEMENT_EVENT_START,
                        BpmnXMLConstants.ELEMENT_TASK_USER))
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        //TODO 获取Bpmn模型信息
        InputStream inputStream =
                repositoryService.getProcessModel(historicTaskInstance.getProcessDefinitionId());
        String bpmnXmlStr = StrUtil.utf8Str(IoUtil.readBytes(inputStream, false));
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(bpmnXmlStr);
        List<WfTaskFormVo> wfTaskFormVoList = new ArrayList<>();
        //TODO 获取流程中已经进行的formId
        for (HistoricActivityInstance activityInstance : activityInstanceList) {
            // 获取当前节点流程元素信息
            FlowElement flowElement = ModelUtils.getFlowElementById(bpmnModel, activityInstance.getActivityId());
            // 获取当前节点表单Key
            String formKey = ModelUtils.getFormKey(flowElement);
            if (formKey == null) {
                continue;
            }
            //TODO 获取编辑器展示内容
            WfDeployForm wfDeployForm = wfDeployFormService.selectFormByDeployIdAndActId
                    (historicTaskInstance.getDeploymentId(), activityInstance.getActivityId());
            ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(historicTaskInstance.getId());
            //如果当前的定义id和启动时的定义id相同,则不考虑进行了指定审批改图的操作
            if(historicTaskInstance.getProcessDefinitionId().equals(extendProcinst.getProcDefId())){
            }else {
                ///TODO 再改图之后 获取自定义表单要拿旧的定义id
                ProcessDefinition processDefinition = repositoryService.getProcessDefinition(extendProcinst.getProcDefId());
                wfDeployForm  = wfDeployFormService.selectFormByDeployIdAndActId
                        (processDefinition.getDeploymentId(), activityInstance.getActivityId());
            }
            //TODO 开始节点的表单内容---->发起节点表单，始终为一条
            if (activityInstance.getActivityType().equals("startEvent")) {
                WfTaskFormVo wfTaskFormVo = new WfTaskFormVo();
                //获取审批表单内容
                List<WfTaskForm> wfTaskFormList = wfTaskFormService.selectFormJsonDataByProcInsIdAndActIdToList
                        (procInsId, activityInstance.getActivityId());
                if (CollectionUtils.isNotEmpty(wfTaskFormList)) {
                    WfTaskForm wfTaskForm = wfTaskFormList.get(0);
                    wfTaskFormVo.setFormContent(wfTaskForm.getFormContent());
                    wfTaskFormVo.setDesignerContent(wfDeployForm.getContent());
                    wfTaskFormVo.setAssigneeName(sysUserService.selectNickNameById(Long.valueOf(wfTaskForm.getUserId())));
                    wfTaskFormVo.setTaskName(activityInstance.getActivityName());
                    wfTaskFormVo.setId(UUIDGenerator.generate());
                    wfTaskFormVo.setFinishedTime(activityInstance.getEndTime());
                    wfTaskFormVoList.add(wfTaskFormVo);
                }
            } else {
                //TODO  根据activityId查询节点下所有已经完成的任务
                List<HistoricTaskInstance> historicTaskInstanceList = historyService
                        .createHistoricTaskInstanceQuery()
                        .taskDefinitionKey(activityInstance.getActivityId())
                        .processInstanceId(procInsId)
                        .orderByHistoricTaskInstanceStartTime().asc()
                        .finished()
                        .list();
                for (HistoricTaskInstance taskInstance : historicTaskInstanceList) {
                    //TODO 根据任务id获取所有的表单内容
                    List<WfTaskForm> wfTaskFormList = wfTaskFormService.
                            selectFormJsonDataByTaskIdAndActIdToList(taskInstance.getId(), activityInstance.getActivityId());
                    if (CollectionUtils.isNotEmpty(wfTaskFormList) && wfTaskFormList.size() > 0) {
                        for (WfTaskForm wfTaskForm : wfTaskFormList) {
                            WfTaskFormVo wfTaskFormVo = new WfTaskFormVo();
                            wfTaskFormVo.setFormContent(wfTaskForm.getFormContent());  //表单数据data
                            wfTaskFormVo.setDesignerContent(wfDeployForm.getContent()); //表单设计器渲染内容
                            //任务处理人姓名
                            wfTaskFormVo.setAssigneeName(sysUserService.selectNickNameById(Long.valueOf(wfTaskForm.getUserId())));
                            //任务名称---节点名称
                            wfTaskFormVo.setTaskName(activityInstance.getActivityName());
                            // 任务完结时间
                            wfTaskFormVo.setFinishedTime(taskInstance.getEndTime());
                            //任务区别 taskForm主键
                            wfTaskFormVo.setId(wfTaskForm.getId());
                            //防止重复表单数据放入
                            if (wfTaskFormVoList.contains(wfTaskFormVo)) {
                                continue;
                            }
                            wfTaskFormVoList.add(wfTaskFormVo);
                        }
                    }
                }
            }
        }
        return wfTaskFormVoList;
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfDeployForm>
     * @description TODO (查询流程部署关联表单信息)
     * @params [definitionId]
     * @author yanbin
     * @date 2024/3/5 13:50
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfDeployForm> selectFormContent(String procDefId, String procInsId) {
        ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(procInsId);
        if (extendProcinst != null && procDefId.equals(extendProcinst.getProcDefId())) {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(procDefId).singleResult();
            List<WfDeployForm> deployFormVoList = wfDeployFormService
                    .selectDeployFormByDeployId(processDefinition.getDeploymentId());
            return deployFormVoList;
        }
        return null;
    }
}
