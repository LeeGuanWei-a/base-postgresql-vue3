package com.basic.workflow.service.wfProcessInfo.flowNode;

import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yanbin
 * @description TODO (Flowable节点相关信息查询)
 * @date 2024/03/04
 */
@Service
public class WfProcessFlowNodeService extends WfProcessInfoBase {
    /**
     * @return java.util.List<org.flowable.bpmn.model.UserTask>
     * @description TODO (获取全部节点的详细内容)包含多实例节点
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/4 10:59
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
            if (CollectionUtils.isNotEmpty(flowElements)) {
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
     * @return java.util.List<org.flowable.bpmn.model.UserTask>
     * @description TODO (获取当前流程的全部多实例节点)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/4 10:58
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
