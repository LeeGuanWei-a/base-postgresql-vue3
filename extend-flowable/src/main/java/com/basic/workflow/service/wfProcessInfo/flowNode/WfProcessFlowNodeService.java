package com.basic.workflow.service.wfProcessInfo.flowNode;

import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @description 流程节点查询服务，解析流程定义的 BPMN 模型，获取全部用户任务节点及多实例节点信息
 * @author yanbin
 * @date 2024/03/04
 */
@Service
public class WfProcessFlowNodeService extends WfProcessInfoBase {
    /**
     * @return List<UserTask> 该流程定义下全部用户任务节点，含多实例节点；无节点时为空列表
     * @description 按流程实例 id 找到流程定义，遍历其所有流程（含子流程）收集用户任务节点
     * @params [procInsId] 流程实例 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<UserTask> getAllFlowNode(String procInsId) {
        List<UserTask> userTaskList = new ArrayList<>();
        //获取流程发布Id信息
        String definitionId = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procInsId)
                .singleResult()
                .getProcessDefinitionId();
        //获取所有节点信息
        List<Process> processes = repositoryService.getBpmnModel(definitionId).getProcesses();
        for (Process process : processes) {
            Collection<FlowElement> flowElements = process.getFlowElements();
            if (CollUtil.isNotEmpty(flowElements)) {
                for (FlowElement flowElement : flowElements) {
                    //需求判断是拿用户模块还是什么
                    if (flowElement instanceof UserTask) {
                        userTaskList.add((UserTask) flowElement);
                    }
                }
            }

        }
        return userTaskList;
    }

    /**
     * @return List<UserTask> 配置了多实例（会签/或签）的用户任务节点，无多实例节点时为空列表
     * @description 在全部用户任务中筛出带多实例循环特征的节点
     * @params [procInsId] 流程实例 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<UserTask> getAllMultiInstanceNode(String procInsId) {
        List<UserTask> mulUserTaskList = new ArrayList<>();
        //获取当前流程的全部userTask模块
        List<UserTask> userTaskList = this.getAllFlowNode(procInsId);
        for (UserTask userTask : userTaskList) {
            //判断是否存在多实例
            MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = userTask.getLoopCharacteristics();
            //多实例不为空  则放入多实例的完成条件，根据完成条件判断是会签，或签
            if (multiInstanceLoopCharacteristics != null) {
                mulUserTaskList.add(userTask);
            }
        }
        return mulUserTaskList;
    }
}
