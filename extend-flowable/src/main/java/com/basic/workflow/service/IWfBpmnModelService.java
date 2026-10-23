package com.basic.workflow.service;

import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;

import java.util.List;

/**
 * @description BPMN 模型查询服务接口，提供流程模型、开始节点与各类活动节点的读取能力
 * @author yanbin
 * @date 2023/10/25
 */
public interface IWfBpmnModelService {
    /**
     * @return BpmnModel 流程定义对应的 BPMN 模型
     * @description 按流程定义 id 读取并缓存 BPMN 模型
     * @params [procDefId] 流程定义id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    BpmnModel getBpmnModelByProcessDefId(String procDefId);

    /**
     * @return StartEvent 主流程中的开始节点
     * @description 在主流程的流程元素中查找开始节点
     * @params [mainProcess] 主流程定义对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    StartEvent findStartFlowElement(Process mainProcess);

    /**
     * @return List<EndEvent> 该流程定义中的所有结束节点
     * @description 按流程定义 id 收集其中的全部结束节点
     * @params [procDefId] 流程定义id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<EndEvent> findEndFlowElement(String procDefId);

    /**
     * @return FlowNode 匹配的流程节点，不存在时返回 null
     * @description 按流程定义 id 与节点 id 查找流程节点
     * @params [procDefId, activityId] 流程定义id；节点id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    FlowNode findFlowNodeByActivityId(String procDefId, String activityId);

    /**
     * @return boolean 节点位于子流程内返回 true
     * @description 判断给定节点是否属于子流程内部的节点
     * @params [procDefId, activityId] 流程定义id；节点id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean checkActivitySubprocessByActivityId(String procDefId, String activityId);

    /**
     * @return Activity 匹配的节点，不存在时返回 null
     * @description 按节点名称在流程定义中查找节点，主要用于定位提交人节点
     * @params [processDefinitionId, flowSubmitter] 流程定义id；节点名称，即提交人节点名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Activity findActivityByName(String processDefinitionId, String flowSubmitter);

    /**
     * @return List<Activity> 该节点 id 对应的全部节点
     * @description 按流程定义 id 与节点 id 获取节点信息
     * @params [processDefId, activityId] 流程定义id；节点id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<Activity> findActivityByActivityId(String processDefId, String activityId);

    /**
     * @return UserTask 匹配的用户任务，不存在时返回 null
     * @description 按流程定义 id 与节点 id 查找对应的用户任务
     * @params [procDefId, activityId] 流程定义id；节点id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    UserTask findUserTaskForProcDefIdAndKey(String procDefId, String activityId);
}
