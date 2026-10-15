package com.basic.workflow.service;

import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (bpmnModel 查询活动activity service)
 * @date 2023/10/25
 */
public interface IWfBpmnModelService {
    /**
     * @return org.flowable.bpmn.model.BpmnModel
     * @description TODO (通过流程定义id获取 BpmnModel)
     * @params [procDefId] 流程定义id
     * @author yanbin
     * @date 2024/1/16 11:19
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    BpmnModel getBpmnModelByProcessDefId(String procDefId);

    /**
     * @return org.flowable.bpmn.model.StartEvent
     * @description TODO (读取开始节点)
     * @params [mainProcess]
     * @author yanbin
     * @date 2023/11/17 16:33
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    StartEvent findStartFlowElement(Process mainProcess);

    /**
     * @return java.util.List<org.flowable.bpmn.model.EndEvent>
     * @description TODO (获取end节点)
     * @params [procDefId]
     * @author yanbin
     * @date 2024/1/15 14:22
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<EndEvent> findEndFlowElement(String procDefId);

    /**
     * @return org.flowable.bpmn.model.FlowNode
     * @description TODO (查找节点)
     * @params [procDefId流程定义id, activityId节点id]
     * @author yanbin
     * @date 2024/1/16 11:19
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    FlowNode findFlowNodeByActivityId(String procDefId, String activityId);

    /**
     * @return boolean
     * @description TODO (判断节点是不是子流程的节点)
     * @params [procDefId流程定义id, activityId节点id]
     * @author yanbin
     * @date 2024/1/16 11:19
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean checkActivitySubprocessByActivityId(String procDefId, String activityId);

    /**
     * @return org.flowable.bpmn.model.Activity
     * @description TODO (查找节点信息根据名称)---主要用于查找提交人
     * @params [processDefinitionId, flowSubmitter]
     * @author yanbin
     * @date 2024/1/23 11:44
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Activity findActivityByName(String processDefinitionId, String flowSubmitter);

    /**
     * @return java.util.List<org.flowable.bpmn.model.Activity>
     * @description TODO (通过流程定义id获取节点)
     * @params [processDefId, activityId]
     * @author yanbin
     * @date 2024/1/23 11:45
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<Activity> findActivityByActivityId(String processDefId, String activityId);

    /**
     * @return org.flowable.bpmn.model.UserTask
     * @description TODO (根据流程定义id和activityId查找用户任务)
     * @params [procDefId, activityId]
     * @author yanbin
     * @date 2024/2/23 9:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    UserTask findUserTaskForProcDefIdAndKey(String procDefId, String activityId);
}
