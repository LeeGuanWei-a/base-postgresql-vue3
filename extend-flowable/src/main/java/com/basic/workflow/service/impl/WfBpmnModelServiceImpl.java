package com.basic.workflow.service.impl;

import com.basic.workflow.customCache.CustomDeploymentCache;
import com.basic.workflow.service.IWfBpmnModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yanbin
 * @description TODO (工作流bpmnModel实现类)--活动实现类
 * @date 2023/10/25
 */
@Service
public class WfBpmnModelServiceImpl implements IWfBpmnModelService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private CustomDeploymentCache customDeploymentCache;

    /**
     * @return org.flowable.bpmn.model.BpmnModel
     * @description TODO (获取bpmnModel)
     * @params [processDefId]
     * @author yanbin
     * @date 2023/10/25 10:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public BpmnModel getBpmnModelByProcessDefId(String procDefId) {
        BpmnModel bpmnModel = null;
        if (customDeploymentCache.contains(procDefId)) {
            ProcessDefinitionCacheEntry processDefinitionCacheEntry =
                    customDeploymentCache.get(procDefId);
            if (processDefinitionCacheEntry != null) {
                bpmnModel = processDefinitionCacheEntry.getBpmnModel();
            }
        } else {
            bpmnModel = repositoryService.getBpmnModel(procDefId);
        }
        return bpmnModel;
    }

    /**
     * @return org.flowable.bpmn.model.StartEvent
     * @description TODO (获取start节点)
     * @params [process]
     * @author yanbin
     * @date 2024/1/19 15:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public StartEvent findStartFlowElement(Process process) {
        List<StartEvent> startEvents = process.findFlowElementsOfType(StartEvent.class);
        if (CollectionUtils.isNotEmpty(startEvents)) {
            return startEvents.get(0);
        }
        return null;
    }

    /**
     * @return java.util.List<org.flowable.bpmn.model.EndEvent>
     * @description TODO 获取end节点)
     * @params [procDefId]
     * @author yanbin
     * @date 2024/1/23 11:45
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<EndEvent> findEndFlowElement(String procDefId) {
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(procDefId);
        if (bpmnModel != null) {
            Process process = bpmnModel.getMainProcess();
            return process.findFlowElementsOfType(EndEvent.class);
        } else {
            return null;
        }
    }

    /**
     * @return org.flowable.bpmn.model.FlowNode
     * @description TODO (查找节点)
     * @params [procDefId, activityId]
     * @author yanbin
     * @date 2024/1/2 16:29
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public FlowNode findFlowNodeByActivityId(String procDefId, String activityId) {
        FlowNode activity = null;
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(procDefId);
        List<Process> processes = bpmnModel.getProcesses();
        for (Process process : processes) {
            FlowElement flowElement = process.getFlowElementMap().get(activityId);
            if (flowElement != null) {
                activity = (FlowNode) flowElement;
                break;
            }
        }
        return activity;
    }

    /**
     * @return boolean
     * @description TODO (判断节点是不是子流程的节点)
     * @params [procDefId, activityId]
     * @author yanbin
     * @date 2024/1/2 17:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public boolean checkActivitySubprocessByActivityId(String procDefId, String activityId) {
        boolean flag = true;
        List<FlowNode> activities = this.findFlowNodesByActivityId(procDefId, activityId);
        if (CollectionUtils.isNotEmpty(activities)) {
            flag = false;
        }
        return flag;
    }

    /**
     * @return java.util.List<org.flowable.bpmn.model.FlowNode>
     * @description TODO (获取activityId)
     * @params [procDefId, activityId]
     * @author yanbin
     * @date 2024/1/2 17:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<FlowNode> findFlowNodesByActivityId(String procDefId, String activityId) {
        List<FlowNode> activities = new ArrayList<>();
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(procDefId);
        List<Process> processes = bpmnModel.getProcesses();
        for (Process process : processes) {
            FlowElement flowElement = process.getFlowElement(activityId);
            if (flowElement != null) {
                FlowNode flowNode = (FlowNode) flowElement;
                activities.add(flowNode);
            }
        }
        return activities;
    }

    /**
     * @return org.flowable.bpmn.model.Activity
     * @description TODO (查找节点信息根据名称)---主要用于查找提交人
     * @params [processDefId, name]
     * @author yanbin
     * @date 2024/1/19 15:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Activity findActivityByName(String processDefId, String name) {
        Activity activity = null;
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(processDefId);
        Process process = bpmnModel.getMainProcess();
        Collection<FlowElement> list = process.getFlowElements();
        for (FlowElement f : list) {
            if (StringUtils.isNotBlank(name)) {
                if (name.equals(f.getName())) {
                    activity = (Activity) f;
                    break;
                }
            }
        }
        return activity;
    }

    /**
     * @return java.util.List<org.flowable.bpmn.model.Activity>
     * @description TODO (通过流程定义id获取节点)
     * @params [processDefId, activityId]
     * @author yanbin
     * @date 2024/1/23 11:46
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<Activity> findActivityByActivityId(String processDefId, String activityId) {
        List<Activity> activities = new ArrayList<>();
        BpmnModel bpmnModel = this.getBpmnModelByProcessDefId(processDefId);
        List<Process> processes = bpmnModel.getProcesses();
        for (Process process : processes) {
            FlowElement flowElement = process.getFlowElement(activityId);
            if (flowElement != null) {
                Activity activity = (Activity) flowElement;
                activities.add(activity);
            }
        }
        return activities;
    }

    /**
     * @return org.flowable.bpmn.model.UserTask
     * @description TODO (根据流程定义id和activityId查找用户任务)
     * @params [procDefId, activityId]
     * @author yanbin
     * @date 2024/2/23 9:29
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public UserTask findUserTaskForProcDefIdAndKey(String procDefId, String activityId) {
        FlowNode flowNode = this.findFlowNodeByActivityId(procDefId, activityId);
        if (flowNode.getClass() == UserTask.class) {
            UserTask userTask = (UserTask) flowNode;
            return userTask;
        }
        return null;
    }
}
