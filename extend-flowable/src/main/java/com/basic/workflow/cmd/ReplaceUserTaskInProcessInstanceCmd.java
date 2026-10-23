package com.basic.workflow.cmd;

import cn.hutool.core.collection.CollUtil;
import com.basic.common.utils.StringUtils;
import com.basic.common.utils.bean.BeanUtils;
import com.basic.workflow.builder.DynamicUserTaskBuilder;
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
 * @description 动态替换任务节点的命令，在当前节点处注入新的用户任务并接续下一节点，产出派生的流程定义
 * @author yanbin
 * @date 2023/11/27
 */
public class ReplaceUserTaskInProcessInstanceCmd extends AbstractDynamicInjectionCmd implements Command<Void> {
    protected String taskId;
    protected DynamicUserTaskBuilder dynamicUserTaskBuilder;
    protected FlowElement nextFlowElement; // 下一节点flow

    /**
     * @description 绑定待替换任务、新节点参数与下一节点，供 execute 时定位流程实例
     * @params [taskId, dynamicUserTaskBuilder, nextFlowElement] 触发替换动作的当前任务 ID；新节点的人员与多实例参数；被替换掉的下一节点元素
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public ReplaceUserTaskInProcessInstanceCmd(String taskId, DynamicUserTaskBuilder dynamicUserTaskBuilder, FlowElement nextFlowElement) {
        this.dynamicUserTaskBuilder = dynamicUserTaskBuilder;
        this.taskId = taskId;
        this.nextFlowElement = nextFlowElement;
    }

    /**
     * @return Void 固定返回 null，动作结果体现在派生流程定义上
     * @description 以任务所属流程实例为目标，触发生成替换节点后的派生流程定义
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
     * @description 用新用户任务替换指定的下一节点，并按审批模式重建处理人与多实例配置
     * @params [commandContext, process, flowElement, bpmnModel, processDefinitionEntity, deploymentEntity] 命令上下文；待改写的流程；当前节点；待改写的流程模型；原流程定义；新建的部署
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    protected void updateBpmnProcess(CommandContext commandContext, Process process, FlowElement flowElement, BpmnModel bpmnModel,
                                     ProcessDefinitionEntity processDefinitionEntity, DeploymentEntity deploymentEntity) {
        UserTask userTask = new UserTask();
        // 1. 继承当前节点原有的属性
        BeanUtils.copyProperties(nextFlowElement, userTask);
        if (dynamicUserTaskBuilder.getId() != null) {
            userTask.setId(dynamicUserTaskBuilder.getId());
        } else {
            userTask.setId(dynamicUserTaskBuilder.nextTaskId(process.getFlowElementMap()));
        }
        dynamicUserTaskBuilder.setDynamicTaskId(userTask.getId());
        userTask.setName(dynamicUserTaskBuilder.getName());
        // 2.处理传的参数
        Map<String, Object> result = this.handleDataType();
        // 3.原本的处理人
        List<String> originalUserList = (List<String>) dynamicUserTaskBuilder.getArgument().get("originalUserList");
        // 4.指定的审批人
        List<String> appointUserList = (List<String>) dynamicUserTaskBuilder.getArgument().get("appointUserList");
        // 5.处理修改模式和多实例配置
        userTask = this.handleMultiInstancePattern(result, appointUserList, originalUserList, userTask);
        // 6. 删除原有节点
        process.removeFlowElement(nextFlowElement.getId());
        // 7. 添加新节点
        process.addFlowElement(userTask);
        // 说明：Flowable 8 已移除 org.flowable.bpmn.BpmnAutoLayout，不再重算 DI 布局；
        // 改图后的流程实例仍按派生流程定义正常流转，只是流程图里新节点没有图形坐标。
        BaseDynamicSubProcessInjectUtil.processFlowElements(commandContext, process, bpmnModel, processDefinitionEntity, deploymentEntity);
    }

    /**
     * @return UserTask 配好处理人与多实例属性的新用户任务
     * @description 按追加或替换两种审批模式重建节点处理人，并按需写入串行并行、完成条件与多实例集合变量
     * @params [result, appointUserList, originalUserList, userTask] 整理后的参数，含审批模式与多实例类型；指定的审批人；原本的审批人；待改写的用户任务
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return Map<String, Object> 整理后的参数，含多实例类型标识、完成条件、审批模式与两类审批人列表
     * @description 把构造时传入的动态任务参数整理成执行阶段直接可用的多实例完成条件、审批模式与人员列表
     * @author liguanwei
     * @date 2026/10/17 20:52
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
