package com.basic.flowable.flow;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @description 下一节点推导工具，按 BPMN 模型沿出线递归求值连线条件，得出当前任务之后可达的用户任务集合
 * @author yanbin
 * @date 2023/10/19
 */
public class FindNextNodeUtil {

    /**
     * @return List<UserTask> 当前任务之后按条件推导出的用户任务集合
     * @description 按任务所在流程定义取回 BPMN 模型，从当前节点沿出线递归推导后续用户任务
     * @params [repositoryService, task, map] 流程定义与模型查询服务；当前任务，用其任务定义 key 定位节点；流程变量，用于求值连线条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<UserTask> getNextUserTasks(RepositoryService repositoryService, org.flowable.task.api.Task task, Map<String, Object> map) {
        List<UserTask> data = new ArrayList<>();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        Process mainProcess = bpmnModel.getMainProcess();
        Collection<FlowElement> flowElements = mainProcess.getFlowElements();
        String key = task.getTaskDefinitionKey();
        FlowElement flowElement = bpmnModel.getFlowElement(key);
        next(flowElements, flowElement, map, data);
        return data;
    }

    /**
     * @description 沿出线递归推导后续节点，把条件成立且可达的用户任务收集到结果列表，遇到多实例子流程与调用活动时按其循环特征收集
     * @params [flowElements, flowElement, map, nextUser] 流程（含子流程）的全部节点集合；当前节点，出线为空的节点直接结束推导；流程变量，用于求值连线上的条件表达式；收集后续用户任务的结果列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static void next(Collection<FlowElement> flowElements, FlowElement flowElement, Map<String, Object> map, List<UserTask> nextUser) {
        //如果是结束节点
        if (flowElement instanceof EndEvent) {
            //如果是子任务的结束节点
            if (getSubProcess(flowElements, flowElement) != null) {
                flowElement = getSubProcess(flowElements, flowElement);
            }
        }
        //获取Task的出线信息--可以拥有多个
        List<SequenceFlow> outGoingFlows = null;
        if (flowElement instanceof Task) {
            outGoingFlows = ((Task) flowElement).getOutgoingFlows();
        } else if (flowElement instanceof Gateway) {
            outGoingFlows = ((Gateway) flowElement).getOutgoingFlows();
        } else if (flowElement instanceof StartEvent) {
            outGoingFlows = ((StartEvent) flowElement).getOutgoingFlows();
        } else if (flowElement instanceof SubProcess) {
            outGoingFlows = ((SubProcess) flowElement).getOutgoingFlows();
        } else if (flowElement instanceof CallActivity) {
            outGoingFlows = ((CallActivity) flowElement).getOutgoingFlows();
        }
        if (outGoingFlows != null && outGoingFlows.size() > 0) {
            //遍历所有的出线--找到可以正确执行的那一条
            for (SequenceFlow sequenceFlow : outGoingFlows) {
                //1.有表达式，且为true
                //2.无表达式
                String expression = sequenceFlow.getConditionExpression();
                if (expression == null || expressionResult(map, expression.substring(expression.lastIndexOf("{") + 1, expression.lastIndexOf("}")))) {
                    //出线的下一节点
                    String nextFlowElementID = sequenceFlow.getTargetRef();
                    if (checkSubProcess(nextFlowElementID, flowElements, nextUser)) {
                        continue;
                    }

                    //查询下一节点的信息
                    FlowElement nextFlowElement = getFlowElementById(nextFlowElementID, flowElements);
                    //调用流程
                    if (nextFlowElement instanceof CallActivity) {
                        CallActivity ca = (CallActivity) nextFlowElement;
                        if (ca.getLoopCharacteristics() != null) {
                            UserTask userTask = new UserTask();
                            userTask.setId(ca.getId());

                            userTask.setId(ca.getId());
                            userTask.setLoopCharacteristics(ca.getLoopCharacteristics());
                            userTask.setName(ca.getName());
                            nextUser.add(userTask);
                        }
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                    //用户任务
                    if (nextFlowElement instanceof UserTask) {
                        nextUser.add((UserTask) nextFlowElement);
                    }
                    //排他网关
                    else if (nextFlowElement instanceof ExclusiveGateway) {
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                    //并行网关
                    else if (nextFlowElement instanceof ParallelGateway) {
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                    //接收任务
                    else if (nextFlowElement instanceof ReceiveTask) {
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                    //服务任务
                    else if (nextFlowElement instanceof ServiceTask) {
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                    //子任务的起点
                    else if (nextFlowElement instanceof StartEvent) {
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                    //结束节点
                    else if (nextFlowElement instanceof EndEvent) {
                        next(flowElements, nextFlowElement, map, nextUser);
                    }
                }
            }
        }
    }

    /**
     * @return boolean 该节点是否为多实例子流程，是则已收集并返回 true
     * @description 判断目标节点是否是多实例子流程，若是则按其循环特征生成一个用户任务节点加入结果集合
     * @params [Id, flowElements, nextUser] 目标节点主键；流程（含子流程）的全部节点集合；收集后续用户任务的结果列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static boolean checkSubProcess(String Id, Collection<FlowElement> flowElements, List<UserTask> nextUser) {
        for (FlowElement flowElement1 : flowElements) {
            if (flowElement1 instanceof SubProcess && flowElement1.getId().equals(Id)) {

                SubProcess sp = (SubProcess) flowElement1;
                if (sp.getLoopCharacteristics() != null) {
                    String inputDataItem = sp.getLoopCharacteristics().getInputDataItem();
                    UserTask userTask = new UserTask();
                    userTask.setId(sp.getId());
                    userTask.setLoopCharacteristics(sp.getLoopCharacteristics());
                    userTask.setName(sp.getName());
                    nextUser.add(userTask);
                    return true;
                }
            }
        }

        return false;

    }

    /**
     * @return FlowElement 该节点所属的子流程，不在任何子流程内时返回 null
     * @description 逐个子流程比对内部节点，找出当前节点所属的子流程节点
     * @params [flowElements, flowElement] 全流程的节点集合；当前节点
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static FlowElement getSubProcess(Collection<FlowElement> flowElements, FlowElement flowElement) {
        for (FlowElement flowElement1 : flowElements) {
            if (flowElement1 instanceof SubProcess) {
                for (FlowElement flowElement2 : ((SubProcess) flowElement1).getFlowElements()) {
                    if (flowElement.equals(flowElement2)) {
                        return flowElement1;
                    }
                }
            }
        }
        return null;
    }


    /**
     * @return FlowElement 命中的流程节点，子流程则返回其开始节点，未命中返回 null
     * @description 按主键在流程及各级子流程中递归查找节点，命中子流程时改为返回其开始节点
     * @params [Id, flowElements] 节点主键；流程节点集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static FlowElement getFlowElementById(String Id, Collection<FlowElement> flowElements) {
        for (FlowElement flowElement : flowElements) {
            if (flowElement.getId().equals(Id)) {
                //如果是子任务，则查询出子任务的开始节点
                if (flowElement instanceof SubProcess) {
                    return getStartFlowElement(((SubProcess) flowElement).getFlowElements());
                }
                return flowElement;
            }
            if (flowElement instanceof SubProcess) {
                FlowElement flowElement1 = getFlowElementById(Id, ((SubProcess) flowElement).getFlowElements());
                if (flowElement1 != null) {
                    return flowElement1;
                }
            }
        }
        return null;
    }

    /**
     * @return FlowElement 首个开始事件节点，没有时返回 null
     * @description 在一组节点中挑出开始事件作为流程起点返回
     * @params [flowElements] 节点集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static FlowElement getStartFlowElement(Collection<FlowElement> flowElements) {
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof StartEvent) {
                return flowElement;
            }
        }
        return null;
    }

    /**
     * @return boolean 表达式求值结果，按布尔值解析
     * @description 用 Aviator 编译并执行连线上的 EL 表达式，把执行结果转成布尔值返回
     * @params [map, expression] 流程变量，作为表达式执行上下文；待求值的表达式文本
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static boolean expressionResult(Map<String, Object> map, String expression) {
        Expression exp = AviatorEvaluator.compile(expression);
        final Object execute = exp.execute(map);
        return Boolean.parseBoolean(String.valueOf(execute));
    }
}
