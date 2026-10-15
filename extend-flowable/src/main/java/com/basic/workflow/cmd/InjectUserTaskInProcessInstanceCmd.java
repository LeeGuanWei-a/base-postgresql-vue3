package com.basic.workflow.cmd;

import com.basic.workflow.builder.DynamicUserTaskBuilder;
import lombok.AllArgsConstructor;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.dynamic.BaseDynamicSubProcessInjectUtil;
import org.flowable.engine.impl.persistence.entity.DeploymentEntity;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (新增任务节点) 当前节点处理下一任务节点
 * @date 2023/11/27 8:32
 */
public class InjectUserTaskInProcessInstanceCmd extends AbstractDynamicInjectionCmd implements Command<Void> {

    protected String taskId;
    protected DynamicUserTaskBuilder dynamicUserTaskBuilder;

    public InjectUserTaskInProcessInstanceCmd(String currentTaskId, DynamicUserTaskBuilder dynamicUserTaskBuilder) {
        this.taskId = currentTaskId;
        this.dynamicUserTaskBuilder = dynamicUserTaskBuilder;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        TaskEntity task = CommandContextUtil.getProcessEngineConfiguration().getTaskServiceConfiguration().getTaskService().getTask(taskId);
        createDerivedProcessDefinitionForProcessInstance(commandContext, task.getProcessInstanceId(), task.getTaskDefinitionKey());
        return null;
    }

    /**
     * @return void
     * @description TODO (添加新的节点任务模块)
     * @params [commandContext, process, currentFlowElement, bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity]
     * @author yanbin
     * @date 2023/11/24 16:51
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    protected void updateBpmnProcess(CommandContext commandContext, Process process, FlowElement currentFlowElement,
                                     BpmnModel bpmnModel, ProcessDefinitionEntity originalProcessDefinitionEntity, DeploymentEntity newDeploymentEntity) {
        //新建节点
        UserTask userTask = new UserTask();
        if (dynamicUserTaskBuilder.getId() != null) {
            userTask.setId(dynamicUserTaskBuilder.getId());
        } else {
            userTask.setId(dynamicUserTaskBuilder.nextTaskId(process.getFlowElementMap()));
        }
        dynamicUserTaskBuilder.setDynamicTaskId(userTask.getId());

        userTask.setName(dynamicUserTaskBuilder.getName());
        userTask.setAssignee(dynamicUserTaskBuilder.getAssignee());
        process.addFlowElement(userTask);

        //找到当前节点的出口
        List<SequenceFlow> outgoingFlows = ((FlowNode) currentFlowElement).getOutgoingFlows();
        //设置新增节点的出口
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            process.removeFlowElement(outgoingFlow.getId());
            outgoingFlow.setSourceRef(userTask.getId());
            process.addFlowElement(outgoingFlow);
        }
        //创建当前节点与新建节点的连线
        SequenceFlow flowToUserTask = new SequenceFlow(currentFlowElement.getId(), userTask.getId());
        flowToUserTask.setId(dynamicUserTaskBuilder.nextFlowId(process.getFlowElementMap()));
        process.addFlowElement(flowToUserTask);

        //自动布局
        new BpmnAutoLayout(bpmnModel).execute();
        BaseDynamicSubProcessInjectUtil.processFlowElements(commandContext, process, bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity);
    }
}
