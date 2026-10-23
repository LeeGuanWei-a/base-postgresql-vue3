package com.basic.workflow.cmd;

import com.basic.workflow.builder.DynamicUserTaskBuilder;
import com.basic.workflow.flow.SimpleBpmnAutoLayout;
import lombok.AllArgsConstructor;
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
 * @description 动态注入用户任务的命令，在当前任务之后新增一个任务节点并生成插入该节点后的派生流程定义
 * @author yanbin
 * @date 2023/11/27 8:32
 */
public class InjectUserTaskInProcessInstanceCmd extends AbstractDynamicInjectionCmd implements Command<Void> {

    protected String taskId;
    protected DynamicUserTaskBuilder dynamicUserTaskBuilder;

    /**
     * @description 绑定当前任务 ID 与新增节点构建参数，供 execute 时定位流程实例
     * @params [currentTaskId, dynamicUserTaskBuilder] currentTaskId 触发新增动作的当前任务 ID；dynamicUserTaskBuilder 新增节点的构建参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public InjectUserTaskInProcessInstanceCmd(String currentTaskId, DynamicUserTaskBuilder dynamicUserTaskBuilder) {
        this.taskId = currentTaskId;
        this.dynamicUserTaskBuilder = dynamicUserTaskBuilder;
    }

    /**
     * @return Void 固定返回 null
     * @description 按任务 ID 取出任务，以其所属流程实例为目标触发生成插入新节点后的派生流程定义
     * @params [commandContext] 命令上下文
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Void execute(CommandContext commandContext) {
        TaskEntity task = CommandContextUtil.getProcessEngineConfiguration().getTaskServiceConfiguration().getTaskService().getTask(taskId);
        createDerivedProcessDefinitionForProcessInstance(commandContext, task.getProcessInstanceId(), task.getTaskDefinitionKey());
        return null;
    }

    /**
     * @description 在流程定义中插入新的用户任务节点，把当前节点的出口改接到新节点并补一条连入连线，同时生成节点坐标
     * @params [commandContext, process, currentFlowElement, bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity] commandContext 命令上下文；process 目标流程；currentFlowElement 当前所在节点；bpmnModel 流程模型；originalProcessDefinitionEntity 原流程定义；newDeploymentEntity 新部署实体
     * @author liguanwei
     * @date 2026/10/17 20:52
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

        // 给新插入的节点补 DI 坐标（Flowable 8 没有 BpmnAutoLayout 了，自己做个「插到前驱右侧 + 右侧整体右移」的极简布局）
        SimpleBpmnAutoLayout.layoutInjectedNode(bpmnModel, currentFlowElement, userTask);

        BaseDynamicSubProcessInjectUtil.processFlowElements(commandContext, process, bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity);
    }
}
