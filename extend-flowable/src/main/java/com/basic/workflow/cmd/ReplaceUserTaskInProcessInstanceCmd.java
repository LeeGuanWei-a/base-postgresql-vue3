package com.basic.workflow.cmd;

import cn.hutool.core.collection.CollUtil;
import com.basic.common.utils.StringUtils;
import com.basic.common.utils.bean.BeanUtils;
import com.basic.workflow.builder.DynamicUserTaskBuilder;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO    (替换任务节点) 当前节点处理下一任务节点
 * @date 2023/11/27
 */
public class ReplaceUserTaskInProcessInstanceCmd extends AbstractDynamicInjectionCmd implements Command<Void> {
    protected String taskId;
    protected DynamicUserTaskBuilder dynamicUserTaskBuilder;
    protected FlowElement nextFlowElement; // 下一节点flow

    public ReplaceUserTaskInProcessInstanceCmd(String taskId, DynamicUserTaskBuilder dynamicUserTaskBuilder, FlowElement nextFlowElement) {
        this.dynamicUserTaskBuilder = dynamicUserTaskBuilder;
        this.taskId = taskId;
        this.nextFlowElement = nextFlowElement;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        TaskEntity task = CommandContextUtil.getProcessEngineConfiguration().getTaskServiceConfiguration().getTaskService().getTask(taskId);
        createDerivedProcessDefinitionForProcessInstance(commandContext, task.getProcessInstanceId(), task.getTaskDefinitionKey());
        return null;
    }

    @Override
    protected void updateBpmnProcess(CommandContext commandContext, Process process, FlowElement flowElement, BpmnModel bpmnModel,
                                     ProcessDefinitionEntity processDefinitionEntity, DeploymentEntity deploymentEntity) {
        UserTask userTask = new UserTask();
        //TODO 1. 继承当前节点原有的属性
        BeanUtils.copyProperties(nextFlowElement, userTask);
        if (dynamicUserTaskBuilder.getId() != null) {
            userTask.setId(dynamicUserTaskBuilder.getId());
        } else {
            userTask.setId(dynamicUserTaskBuilder.nextTaskId(process.getFlowElementMap()));
        }
        dynamicUserTaskBuilder.setDynamicTaskId(userTask.getId());
        userTask.setName(dynamicUserTaskBuilder.getName());
        //TODO 2.处理传的参数
        Map<String, Object> result = this.handleDataType();
        //TODO 3.原本的处理人
        List<String> originalUserList = (List<String>) dynamicUserTaskBuilder.getArgument().get("originalUserList");
        //TODO 4.指定的审批人
        List<String> appointUserList = (List<String>) dynamicUserTaskBuilder.getArgument().get("appointUserList");
        //TODO 5.处理修改模式和多实例配置
        userTask = this.handleMultiInstancePattern(result, appointUserList, originalUserList, userTask);
        //TODO 6. 删除原有节点
        process.removeFlowElement(nextFlowElement.getId());
        //TODO 7. 添加新节点
        process.addFlowElement(userTask);
        //自动布局(此处布局之后会改变xml流程图展示不同)
        new BpmnAutoLayout(bpmnModel).execute();
        BaseDynamicSubProcessInjectUtil.processFlowElements(commandContext, process, bpmnModel, processDefinitionEntity, deploymentEntity);
    }

    /**
     * @return org.flowable.bpmn.model.UserTask
     * @description TODO (人员多实例配置)
     * @params [result, appointUserList指定的审批人, originalUserList原本的审批人, userTask]
     * @author yanbin
     * @date 2024/1/10 17:00
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private UserTask handleMultiInstancePattern(Map<String, Object> result, List<String> appointUserList,
                                                List<String> originalUserList, UserTask userTask) {
        //审批模式  追加替换
        String pattern = result.get("pattern").toString();
        //多实例类型
        String multiInstance = result.get("multiInstance").toString();
        //审批模式 追加替换
        if (StringUtils.isNotEmpty(pattern)) {
            //追加
            if (pattern.equals("addition")) {
                //原本绑定的人员
                for (String userId : appointUserList) {
                    if (!originalUserList.contains(userId)) {
                        originalUserList.add(userId);
                        //候选角色组设为null
                        userTask.setCandidateGroups(null);
                    }
                }
            }
            //替换
            else if (pattern.equals("replace")) {
                //将原来的变成指定的人员
                originalUserList = appointUserList;
                //候选角色组设为null
                userTask.setCandidateGroups(null);
            }
            userTask.setCandidateUsers(originalUserList);
        }
        //多实例配置
        if (StringUtils.isNotBlank(multiInstance)) {
            //获取节点的多实例属性
            MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = userTask.getLoopCharacteristics();
            //若节点无多实例属性 则新增多实例属性
            if (multiInstanceLoopCharacteristics == null) {
                multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
            }
            //串行并行
            Boolean isSequential = Boolean.valueOf(result.get("isSequential").toString());
            //设置串行并行
            multiInstanceLoopCharacteristics.setSequential(isSequential);
            //设置完成条件  --- 即区别会签，或签
            multiInstanceLoopCharacteristics.setCompletionCondition(multiInstance);
            // 配置会签集合变量名称.
            multiInstanceLoopCharacteristics.setCollectionString("${multiInstanceHandler.getUserIds(execution)}");
            multiInstanceLoopCharacteristics.setInputDataItem("${multiInstanceHandler.getUserIds(execution)}");
            // 配置会签集合遍历名称.
            multiInstanceLoopCharacteristics.setElementVariable("assignee");
            //设置多实例集合遍历处理人
            userTask.setAssignee("${assignee}");
            userTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
            //设置了多实例，获取拓展属性，将多实例监听器中的判断 固定值和身份存储修改
            if (CollUtil.isNotEmpty(userTask.getExtensionElements())) {
                //获取userTask上的拓展信息assigneeType
                List<ExtensionElement> extensionElement = userTask.getExtensionElements().get("assigneeType");
                //设置一个参数，不调用原生的固定值和身份存储的方法
                extensionElement.get(0).setElementText("3");
            }
        }
        return userTask;
    }


    /**
     * @return java.util.List<java.lang.String>
     * @description TODO (处理传递参数，整理参数)
     * @params [wfTaskBo] dataType
     * @author yanbin
     * @date 2023/11/22 14:23
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private Map<String, Object> handleDataType() {
        Map<String, Object> result = new HashMap<>();
        //多实例配置
        String multiInstance = dynamicUserTaskBuilder.getArgument().get("multiInstance").toString();
        if (StringUtils.isNotBlank(multiInstance)) {
            //串行并行
            Boolean isSequential = Boolean.valueOf(dynamicUserTaskBuilder.getArgument().get("isSequential").toString());
            result.put("isSequential", isSequential);
            //设置完成条件， 即区别会签或签 ---即是完成条件
            if (multiInstance.equals("SequentialMultiInstance")) {
                //会签
                result.put("multiInstance", "${nrOfCompletedInstances >= nrOfInstances}");
            } else if (multiInstance.equals("ParallelMultiInstance")) {
                //或签
                result.put("multiInstance", "${nrOfCompletedInstances > 0}");
            }
        }
        //原本的审批人员
        List<String> originalUserList = (List<String>) dynamicUserTaskBuilder.getArgument().get("originalUserList");
        //选择的审批人员
        List<String> appointUserList = (List<String>) dynamicUserTaskBuilder.getArgument().get("appointUserList");
        //处理模式
        String pattern = dynamicUserTaskBuilder.getArgument().get("pattern").toString();
        result.put("appointUserList", appointUserList);
        result.put("originalUserList", originalUserList);
        result.put("pattern", pattern);
        return result;
    }
}
