package com.basic.workflow.service.impl;

import com.basic.workflow.customCache.CustomDeploymentCache;
import com.basic.workflow.service.IWfBpmnModelService;
import cn.hutool.core.collection.CollUtil;
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
 * @description BPMN 模型服务实现，从部署缓存或仓储服务获取流程模型并解析其中的元素与节点
 * @author yanbin
 * @date 2023/10/25
 */
@Service
public class WfBpmnModelServiceImpl implements IWfBpmnModelService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private CustomDeploymentCache customDeploymentCache;

    /**
     * @return BpmnModel 流程定义对应的 BPMN 模型，取不到时返回 null
     * @description 优先从自定义部署缓存取流程定义的 BPMN 模型，缓存未命中时回源仓储服务加载
     * @params [procDefId] 流程定义 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return StartEvent 主流程中的第一个开始事件，没有时返回 null
     * @description 取流程定义主流程中的第一个开始事件
     * @params [process] 流程定义主流程对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public StartEvent findStartFlowElement(Process process) {
        List<StartEvent> startEvents = process.findFlowElementsOfType(StartEvent.class);
        if (CollUtil.isNotEmpty(startEvents)) {
            return startEvents.get(0);
        }
        return null;
    }

    /**
     * @return List<EndEvent> 主流程中的全部结束事件，模型取不到时返回 null
     * @description 按流程定义 ID 查出主流程中的全部结束事件
     * @params [procDefId] 流程定义 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return FlowNode 命中的流程节点，所有流程都未命中时返回 null
     * @description 遍历所有流程的节点索引，按活动 ID 查找对应的流程节点
     * @params [procDefId, activityId] 流程定义 ID；节点（活动）ID
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return boolean 未查到对应流程节点返回 true，查到则返回 false
     * @description 按活动 ID 判断是否查不到该流程节点，即不属于子流程内的节点
     * @params [procDefId, activityId] 流程定义 ID；节点（活动）ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public boolean checkActivitySubprocessByActivityId(String procDefId, String activityId) {
        boolean flag = true;
        List<FlowNode> activities = this.findFlowNodesByActivityId(procDefId, activityId);
        if (CollUtil.isNotEmpty(activities)) {
            flag = false;
        }
        return flag;
    }

    /**
     * @return List<FlowNode> 各流程中按活动 ID 命中的流程节点集合，未命中时为空集合
     * @description 按活动 ID 在所有流程中收集命中的流程节点
     * @params [procDefId, activityId] 流程定义 ID；节点（活动）ID
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return Activity 名称匹配的活动节点，未匹配到时返回 null
     * @description 按节点名称在流程定义主流程中查找活动节点，主要用于查找提交人所在节点
     * @params [processDefId, name] 流程定义 ID；节点名称
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return List<Activity> 各流程中按活动 ID 命中的活动节点集合，未命中时为空集合
     * @description 按流程定义 ID 与活动 ID 在各流程中查找活动节点
     * @params [processDefId, activityId] 流程定义 ID；节点（活动）ID
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return UserTask 命中的用户任务节点，节点不是用户任务时返回 null
     * @description 按流程定义 ID 与活动 ID 查找用户任务节点
     * @params [procDefId, activityId] 流程定义 ID；节点（活动）ID
     * @author liguanwei
     * @date 2026/10/17 20:52
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
