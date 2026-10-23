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
import cn.hutool.core.collection.CollUtil;
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
 * @description 流程历史表单服务，按已完成的开始节点与用户任务取出节点表单内容与设计器渲染内容
 * @author yanbin
 * @date 2024/03/04
 */
@Service
public class WfProcessHiFormService extends WfProcessInfoBase {
    /**
     * @return List<WfTaskFormVo> 历史表单视图列表，按活动开始时间升序
     * @description 取流程已完成的开始节点与用户任务活动，逐个定位节点表单并收集表单内容与设计器渲染内容，节点未绑表单时跳过
     * @params [procInsId] 流程实例 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskFormVo> processFormList(String procInsId) {
        // 获取流程实例
        HistoricProcessInstance historicTaskInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(procInsId).singleResult();
        // 获取流程活动信息
        List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(procInsId).finished()
                .activityTypes(CollUtil.newHashSet(BpmnXMLConstants.ELEMENT_EVENT_START,
                        BpmnXMLConstants.ELEMENT_TASK_USER))
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        // 获取Bpmn模型信息
        InputStream inputStream =
                repositoryService.getProcessModel(historicTaskInstance.getProcessDefinitionId());
        String bpmnXmlStr = StrUtil.utf8Str(IoUtil.readBytes(inputStream, false));
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(bpmnXmlStr);
        List<WfTaskFormVo> wfTaskFormVoList = new ArrayList<>();
        // 获取流程中已经进行的formId
        for (HistoricActivityInstance activityInstance : activityInstanceList) {
            // 获取当前节点流程元素信息
            FlowElement flowElement = ModelUtils.getFlowElementById(bpmnModel, activityInstance.getActivityId());
            // 获取当前节点表单Key
            String formKey = ModelUtils.getFormKey(flowElement);
            if (formKey == null) {
                continue;
            }
            // 获取编辑器展示内容
            WfDeployForm wfDeployForm = wfDeployFormService.selectFormByDeployIdAndActId
                    (historicTaskInstance.getDeploymentId(), activityInstance.getActivityId());
            ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(historicTaskInstance.getId());
            //如果当前的定义id和启动时的定义id相同,则不考虑进行了指定审批改图的操作
            //注意：extendProcinst 可能为空（历史数据/手工删过拓展表），必须判空，否则整个流程详情 500
            if (extendProcinst == null
                    || historicTaskInstance.getProcessDefinitionId().equals(extendProcinst.getProcDefId())) {
                // 流程定义未变化，沿用当前部署的关联表单
            } else {
                // 再改图之后 获取自定义表单要拿旧的定义id
                ProcessDefinition processDefinition = repositoryService.getProcessDefinition(extendProcinst.getProcDefId());
                if (processDefinition != null) {
                    wfDeployForm = wfDeployFormService.selectFormByDeployIdAndActId
                            (processDefinition.getDeploymentId(), activityInstance.getActivityId());
                }
            }
            // 节点没绑表单（或部署已被清理）时直接跳过，避免下面 wfDeployForm.getContent() 空指针
            if (wfDeployForm == null) {
                continue;
            }
            // 开始节点的表单内容---->发起节点表单，始终为一条
            if (activityInstance.getActivityType().equals("startEvent")) {
                WfTaskFormVo wfTaskFormVo = new WfTaskFormVo();
                //获取审批表单内容
                List<WfTaskForm> wfTaskFormList = wfTaskFormService.selectFormJsonDataByProcInsIdAndActIdToList
                        (procInsId, activityInstance.getActivityId());
                if (CollUtil.isNotEmpty(wfTaskFormList)) {
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
                // 根据activityId查询节点下所有已经完成的任务
                List<HistoricTaskInstance> historicTaskInstanceList = historyService
                        .createHistoricTaskInstanceQuery()
                        .taskDefinitionKey(activityInstance.getActivityId())
                        .processInstanceId(procInsId)
                        .orderByHistoricTaskInstanceStartTime().asc()
                        .finished()
                        .list();
                for (HistoricTaskInstance taskInstance : historicTaskInstanceList) {
                    // 根据任务id获取所有的表单内容
                    List<WfTaskForm> wfTaskFormList = wfTaskFormService.
                            selectFormJsonDataByTaskIdAndActIdToList(taskInstance.getId(), activityInstance.getActivityId());
                    if (CollUtil.isNotEmpty(wfTaskFormList) && wfTaskFormList.size() > 0) {
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
     * @return List<WfDeployForm> 该部署下关联的部署表单列表，流程定义与发起时不一致时返回 null
     * @description 校验收起的流程定义与当前流程定义一致后，按部署 id 查询部署关联的表单信息
     * @params [procDefId, procInsId] 流程定义 id；流程实例 id
     * @author liguanwei
     * @date 2026/10/17 20:52
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
