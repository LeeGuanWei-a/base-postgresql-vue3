package com.basic.flowable.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.basic.common.utils.StringUtils;
import com.basic.workflow.domain.vo.FlowElementVo;
import org.apache.commons.collections4.CollectionUtils;
import org.dom4j.*;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.util.io.StringStreamSource;

import java.util.*;

/**
 * @author yanbin
 * @description TODO (流程model工具类)
 * @date 2024/1/8
 */
public class ModelUtils {

    private static final BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();

    /**
     * @return org.flowable.bpmn.model.BpmnModel
     * @description TODO (xml转bpmnModel对象)
     * @params [xml]
     * @author yanbin
     * @date 2024/1/19 15:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static BpmnModel getBpmnModel(String xml) {
        return bpmnXMLConverter.convertToBpmnModel(new StringStreamSource(xml), false, false);
    }

    /**
     * 判断用户任务是否是「顺序会签」节点：串行多实例 + 完成条件为全票通过。
     *
     * <p>判断条件与 WfCompleteTaskService#mulitiSequentialUnComplete 的前置校验保持一致 ——
     * 前后端/各处都用这一个方法，避免"按钮显示了但点下去被后端拒绝"。</p>
     */
    public static boolean isSequentialMultiInstance(UserTask userTask) {
        if (userTask == null || !userTask.hasMultiInstanceLoopCharacteristics()) {
            return false;
        }
        MultiInstanceLoopCharacteristics loop = userTask.getLoopCharacteristics();
        if (loop == null || !loop.isSequential()) {
            return false;
        }
        String completion = loop.getCompletionCondition();
        return completion != null
                && ("${nrOfCompletedInstances >= nrOfInstances}".equals(completion)
                    || completion.contains("nrOfCompletedInstances == nrOfInstances"));
    }

    /**
     * @return java.util.List<java.lang.String>
     * @description TODO (通过xml读取流程下的表达式)读取xml
     * @params [xml]
     * @author yanbin
     * @date 2023/11/17 16:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<String> doXmlToUserTask(String xml) throws DocumentException {
        List<String> list = new ArrayList<>();
        // 加载文档
        Document document = DocumentHelper.parseText(xml);
        // 获取文档根节点
        Element root = document.getRootElement();
        // 获得指定节点下面的子节点
        Element contactElem = root.element("process");//获取根节点下的process子节点
        if (contactElem == null) {
            // 不是标准 BPMN（没有 process 节点）时直接返回空列表，避免 NPE
            return list;
        }
        List<Element> contactList = contactElem.elements();   // 获取process子节点下的所有子节点
        contactList.forEach(e -> {
            //获取UserTask标签
            if ("userTask".equals(e.getName())) {
                List<Attribute> attributes = e.attributes();
                //userTask上标签内的所有子标签
                for (Attribute attribute : attributes) {
                    //只处理 userTask 上的 assignee 属性
                    if (!"assignee".equals(attribute.getName())) {
                        continue;
                    }
                    // userTask上的拓展
                    Element extensionElements = e.element("extensionElements");
                    if (extensionElements == null) {
                        continue;
                    }
                    //获取拓展上的assigneeType标签  区别选择的固定值或者身份存储
                    Element extension = extensionElements.element("assigneeType");
                    // 没有 assigneeType 的节点（只配了 assignee、没配"固定值/身份存储"）直接跳过。
                    // 原来这里直接 extension.getText()，一旦节点缺这个扩展就会 NPE，
                    // 表现为「发起流程」直接失败。
                    if (extension == null) {
                        continue;
                    }
                    //assignee上的表达式，仅在固定值时选择即 0
                    String assigneeTypeText = extension.getText();
                    if (assigneeTypeText != null && "0".equals(assigneeTypeText.trim())) {
                        //截取表达式${xxx}中的xxx
                        String expression = attribute.getValue();
                        int startIndex = expression.indexOf("{");
                        int endIndex = expression.indexOf("}");
                        if (startIndex != -1 && endIndex != -1) {
                            list.add(expression.substring(startIndex + 1, endIndex));
                        }
                    }
                }
            }
        });
        return list;
    }

    /**
     * @return java.lang.String
     * @description TODO (流程图显示网关X消失方法---XML)
     * @params [xmlStr]
     * @author yanbin
     * @date 2023/11/8 10:30
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String addAttributeWithXmlDocument(String xmlStr) throws DocumentException {
        // 加载文档
        Document document = DocumentHelper.parseText(xmlStr);
        // 获取文档根节点
        Element root = document.getRootElement();
        // 获得指定节点下面的子节点
        Element contactElem = root.element("process");//获取根节点下的process子节点
        Element diagram = root.element("BPMNDiagram"); //获取根节点下的BPMNDiagram
        // 缺 process 或 BPMNDiagram 时直接返回原文，避免 NPE。
        // 这个方法是"打开设计器"的读路径，一旦 NPE 就是整页打不开。
        if (contactElem == null || diagram == null) {
            return xmlStr;
        }
        List<Element> bPMNPlane = diagram.elements("BPMNPlane");//获取BPMNDiagram节点下的BPMNPlane节点
        List<Element> contactList = contactElem.elements(); // 获取process子节点下的所有子节点
        contactList.forEach(e -> {
            //筛选出process下的网关节点
            if ("exclusiveGateway".equals(e.getName())) {
                //获取网关的id
                String exclusiveGatewayId = e.attribute("id").getValue();
                bPMNPlane.forEach(el -> {
                    //获取bPMNPlane下的所有标签
                    List<Element> bPMNShape = el.elements();
                    bPMNShape.forEach(element -> {
                        //筛选 网关标签的id  等于  修饰 标签上的bpmnElement
                        if (exclusiveGatewayId.equals(element.attribute("bpmnElement").getValue())) {
                            //添加属性到修饰标签 isMarkerVisible=true显示网关X
                            element.addAttribute("isMarkerVisible", Boolean.toString(true));
                        }
                    });
                });
            }
        });
        return document.asXML();
    }

    /**
     * @return java.lang.String
     * @description TODO (bpmnModel转xml字符串)
     * @params [bpmnModel] bpmnModel对象
     * @author yanbin
     * @date 2024/1/19 15:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Deprecated
    public static String getBpmnXmlStr(BpmnModel bpmnModel) {
        return StrUtil.utf8Str(getBpmnXml(bpmnModel));
    }

    /**
     * @return byte[]
     * @description TODO (bpmnModel转xml对象)
     * @params [bpmnModel]
     * @author yanbin
     * @date 2024/1/19 15:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Deprecated
    public static byte[] getBpmnXml(BpmnModel bpmnModel) {
        return bpmnXMLConverter.convertToXML(bpmnModel);
    }

    /**
     * @return java.util.List<org.flowable.bpmn.model.SequenceFlow>
     * @description TODO (根据节点，获取入口连线)
     * @params [source] 起始节点
     * @author yanbin
     * @date 2024/1/19 15:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<SequenceFlow> getElementIncomingFlows(FlowElement source) {
        List<SequenceFlow> sequenceFlows = new ArrayList<>();
        if (source instanceof FlowNode) {
            sequenceFlows = ((FlowNode) source).getIncomingFlows();
        }
        return sequenceFlows;
    }

    /**
     * @return java.util.List<org.flowable.bpmn.model.SequenceFlow>
     * @description TODO ( 根据节点，获取出口连线)
     * @params [source] 起始节点
     * @author yanbin
     * @date 2024/1/19 15:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<SequenceFlow> getElementOutgoingFlows(FlowElement source) {
        List<SequenceFlow> sequenceFlows = new ArrayList<>();
        if (source instanceof FlowNode) {
            sequenceFlows = ((FlowNode) source).getOutgoingFlows();
        }
        return sequenceFlows;
    }

    /**
     * @return org.flowable.bpmn.model.StartEvent
     * @description TODO (获取开始节点）
     * @params [model] bpmnModel对象
     * @author yanbin
     * @date 2024/1/19 15:29
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static StartEvent getStartEvent(BpmnModel model) {
        Process process = model.getMainProcess();
        FlowElement startElement = process.getInitialFlowElement();
        if (startElement instanceof StartEvent) {
            return (StartEvent) startElement;
        }
        return getStartEvent(process.getFlowElements());
    }

    /**
     * @return org.flowable.bpmn.model.StartEvent
     * @description TODO (获取开始节点)
     * @params [flowElements] 流程元素集合
     * @author yanbin
     * @date 2024/1/19 15:29
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static StartEvent getStartEvent(Collection<FlowElement> flowElements) {
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof StartEvent) {
                return (StartEvent) flowElement;
            }
        }
        return null;
    }


    /**
     * @return org.flowable.bpmn.model.EndEvent
     * @description TODO (获取结束节点)
     * @params [model] bpmnModel对象
     * @author yanbin
     * @date 2024/1/19 15:29
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static EndEvent getEndEvent(BpmnModel model) {
        Process process = model.getMainProcess();
        return getEndEvent(process.getFlowElements());
    }

    /**
     * @return org.flowable.bpmn.model.EndEvent
     * @description TODO (获取结束节点)
     * @params [flowElements] 流程元素集合
     * @author yanbin
     * @date 2024/1/19 15:29
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static EndEvent getEndEvent(Collection<FlowElement> flowElements) {
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof EndEvent) {
                return (EndEvent) flowElement;
            }
        }
        return null;
    }

    /**
     * @return org.flowable.bpmn.model.UserTask
     * @description TODO (获取当前用户模块userTask)
     * @params [model, taskKey]
     * @author yanbin
     * @date 2024/1/17 10:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static UserTask getUserTaskByKey(BpmnModel model, String taskKey) {
        Process process = model.getMainProcess();
        FlowElement flowElement = process.getFlowElement(taskKey);
        if (flowElement instanceof UserTask) {
            return (UserTask) flowElement;
        }
        return null;
    }


    /**
     * @return org.flowable.bpmn.model.FlowElement
     * @description TODO (获取流程元素信息)
     * @params [model bpmnModel对象, flowElementId] 元素ID
     * @author yanbin
     * @date 2024/1/19 15:32
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static FlowElement getFlowElementById(BpmnModel model, String flowElementId) {
        Process process = model.getMainProcess();
        return process.getFlowElement(flowElementId);
    }

    /**
     * @return java.lang.String
     * @description TODO (获取元素表单Key（限开始节点和用户节点可用）)
     * @params [flowElement] 元素
     * @author yanbin
     * @date 2024/1/19 15:32
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String getFormKey(FlowElement flowElement) {
        if (flowElement != null) {
            if (flowElement instanceof StartEvent) {
                return ((StartEvent) flowElement).getFormKey();
            } else if (flowElement instanceof UserTask) {
                return ((UserTask) flowElement).getFormKey();
            }
        }
        return null;
    }


    /**
     * @return java.lang.String
     * @description TODO (获取开始节点属性值)
     * @params [model bpmnModel对象, name 属性名]
     * @author yanbin
     * @date 2024/1/19 15:32
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String getStartEventAttributeValue(BpmnModel model, String name) {
        StartEvent startEvent = getStartEvent(model);
        return getElementAttributeValue(startEvent, name);
    }

    /**
     * @return java.lang.String
     * @description TODO (获取结束节点属性值)
     * @params [model bpmnModel对象, name]  属性名
     * @author yanbin
     * @date 2024/1/19 15:33
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String getEndEventAttributeValue(BpmnModel model, String name) {
        EndEvent endEvent = getEndEvent(model);
        return getElementAttributeValue(endEvent, name);
    }

    /**
     * @return java.lang.String
     * @description TODO (获取用户任务节点属性值)
     * @params [model bpmnModel对象, taskKey 任务Key, name 属性名]
     * @author yanbin
     * @date 2024/1/19 15:33
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String getUserTaskAttributeValue(BpmnModel model, String taskKey, String name) {
        UserTask userTask = getUserTaskByKey(model, taskKey);
        return getElementAttributeValue(userTask, name);
    }

    /**
     * @return java.lang.String
     * @description TODO (获取元素属性值
     * @params [baseElement 流程元素, name 属性名]
     * @author yanbin
     * @date 2024/1/19 15:33
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String getElementAttributeValue(BaseElement baseElement, String name) {
        if (baseElement != null) {
            List<ExtensionAttribute> attributes = baseElement.getAttributes().get(name);
            if (attributes != null && !attributes.isEmpty()) {
                attributes.iterator().next().getValue();
                Iterator<ExtensionAttribute> attrIterator = attributes.iterator();
                if (attrIterator.hasNext()) {
                    ExtensionAttribute attribute = attrIterator.next();
                    return attribute.getValue();
                }
            }
        }
        return null;
    }

    /**
     * @return boolean
     * @description TODO (判断是否是多实例)
     * @params [model bpmnModel对象, taskKey 节点id]
     * @author yanbin
     * @date 2024/1/19 15:33
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static boolean isMultiInstance(BpmnModel model, String taskKey) {
        UserTask userTask = getUserTaskByKey(model, taskKey);
        if (ObjectUtil.isNotNull(userTask)) {
            return userTask.hasMultiInstanceLoopCharacteristics();
        }
        return false;
    }

    /**
     * @return java.util.Collection<org.flowable.bpmn.model.UserTask>
     * @description TODO (获取所有用户任务节点)
     * @params [model] bpmnModel对象
     * @author yanbin
     * @date 2024/1/19 15:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static Collection<UserTask> getAllUserTaskEvent(BpmnModel model) {
        Process process = model.getMainProcess();
        Collection<FlowElement> flowElements = process.getFlowElements();
        return getAllUserTaskEvent(flowElements, null);
    }


    /**
     * @return java.util.Collection<org.flowable.bpmn.model.UserTask>
     * @description TODO (获取所有用户任务节点)
     * @params [flowElements 流程元素集合, allElements 所有流程元素集合]
     * @author yanbin
     * @date 2024/1/19 15:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static Collection<UserTask> getAllUserTaskEvent(Collection<FlowElement> flowElements, Collection<UserTask> allElements) {
        allElements = allElements == null ? new ArrayList<>() : allElements;
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof UserTask) {
                allElements.add((UserTask) flowElement);
            }
            if (flowElement instanceof SubProcess) {
                // 继续深入子流程，进一步获取子流程
                allElements = getAllUserTaskEvent(((SubProcess) flowElement).getFlowElements(), allElements);
            }
        }
        return allElements;
    }

    /**
     * 获取整条流程的流转节点
     */
    public static void selectStartToEndUserTask(BpmnModel bpmnModel) {
        List<Process> processes = bpmnModel.getProcesses();
        FlowElementVo flowElementVo = new FlowElementVo();
        List<List<String>> flowElementVoList = new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Process process : processes) {
            Collection<FlowElement> flowElements = process.getFlowElements();
            //返回树结构
            FlowElementVo flowStartElement = getFlowElement("StartEvent", flowElements, flowElementVo);
            flowElementVoList = handleFlowElement("StartEvent", flowElements, flowElementVoList);
            handleTreeToList(flowStartElement, result);
            flowElementVo.setType("StartEvent");
        }
        System.out.println(flowElementVoList);
    }

    /**
     * @return void
     * @description TODO (树结构转list平铺)
     * @params [flowStartElement, result]
     * @author yanbin
     * @date 2024/1/31 11:33
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private static void handleTreeToList(FlowElementVo flowStartElement, List<Map<String, Object>> result) {
        Map<String, Object> map = new HashMap<>();
        List<FlowElementVo> list = flowStartElement.getFlowElementVoList();
        if (CollectionUtils.isNotEmpty(list)) {
            if (flowStartElement.getType().equals("StartEvent")) {
                map.put("type", "StartEvent");
                map.put("list", list);
                result.add(map);
                list.forEach(item -> {
                    handleTreeToList(item, result);
                });
            } else if (flowStartElement.getType().equals("UserTask")) {
                map.put("type", "UserTask");
                map.put("list", list);
                result.add(map);
                list.forEach(item -> {
                    handleTreeToList(item, result);
                });
            } else if (flowStartElement.getType().equals("ExclusiveGateway")) {
                map.put("type", "ExclusiveGateway");
                map.put("list", list);
                result.add(map);
                list.forEach(item -> {
                    handleTreeToList(item, result);
                });
            } else if (flowStartElement.getType().equals("SequenceFlow")) {
                map.put("type", "SequenceFlow");
                map.put("list", list);
                result.add(map);
                list.forEach(item -> {
                    handleTreeToList(item, result);
                });
            }
        }
    }

    private static List<List<String>> handleFlowElement(String activityId, Collection<FlowElement> flowElements, List<List<String>> flowElementVoList) {
        if (CollectionUtils.isNotEmpty(flowElements)) {
            //遍历所有节点
            for (FlowElement flowElement : flowElements) {
                //首先确定是开始节点
                if (activityId.equals("StartEvent")) {
                    //获取开始节点
                    if (flowElement instanceof StartEvent) {
                        StartEvent startEvent = (StartEvent) flowElement;
                        List<SequenceFlow> sequenceFlowList = startEvent.getOutgoingFlows();
                        List<String> list = new ArrayList<>();
                        sequenceFlowList.forEach(sequenceFlow -> {
                        });
                    }
                } else {
                    //用户任务
                    if (flowElement instanceof UserTask) {

                    }
                }
            }
        }
        return null;
    }

    /**
     * @return com.basic.workflow.domain.vo.FlowElementVo
     * @description TODO (采用递归算法，获取其下所有节点的分支连线) 迭代出树形结构
     * @params [nodeId 节点类型, flowElements flowElement列表, flowElementVo]
     * @author yanbin
     * @date 2024/1/30 14:04
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static FlowElementVo getFlowElement(String activityId, Collection<FlowElement> flowElements, FlowElementVo flowElementVo) {
        if (CollectionUtils.isNotEmpty(flowElements)) {
            for (FlowElement flowElement : flowElements) {
                if (activityId.equals("StartEvent")) {
                    //其开始节点
                    if (flowElement instanceof StartEvent) {
                        List<FlowElementVo> flowElementVoList = new ArrayList<>();
                        StartEvent startEvent = (StartEvent) flowElement;
                        flowElementVo.setType("StartEvent");
                        flowElementVo.setActivityName(startEvent.getName());
                        flowElementVo.setActivityId(startEvent.getId());
                        List<SequenceFlow> sequenceFlowList = startEvent.getOutgoingFlows();
                        sequenceFlowList.forEach(sequenceFlow -> {
                            FlowElementVo flowElementNext = getFlowElement(sequenceFlow.getId(), flowElements, flowElementVo);
                            flowElementVoList.add(flowElementNext);
                        });
                        flowElementVo.setFlowElementVoList(flowElementVoList);
                        return flowElementVo;
                    } else {
                        //如果没有开始节点则表示该流程不能可视化
                        return null;
                    }
                } else {
                    //用户任务
                    if (flowElement instanceof UserTask) {
                        UserTask userTask = (UserTask) flowElement;
                        if (activityId.equals(userTask.getId())) {
                            FlowElementVo flowElementUserTask = new FlowElementVo();
                            List<FlowElementVo> flowElementVoList = new ArrayList<>();
                            //获取基本属性
                            flowElementUserTask.setType("UserTask");
                            flowElementUserTask.setActivityName(userTask.getName());
                            flowElementUserTask.setActivityId(userTask.getId());
                            //获取下面的所有连线
                            List<SequenceFlow> sequenceFlowList = userTask.getOutgoingFlows();
                            sequenceFlowList.forEach(sequenceFlow -> {
                                FlowElementVo flowElementNext = getFlowElement(sequenceFlow.getId(), flowElements, flowElementVo);
                                flowElementVoList.add(flowElementNext);
                            });
                            flowElementUserTask.setFlowElementVoList(flowElementVoList);
                            return flowElementUserTask;
                        }
                    }
                    //网关模块
                    if (flowElement instanceof ExclusiveGateway) {
                        ExclusiveGateway exclusiveGateway = (ExclusiveGateway) flowElement;
                        if (activityId.equals(exclusiveGateway.getId())) {
                            FlowElementVo flowElementExclusiveGateway = new FlowElementVo();
                            List<FlowElementVo> flowElementVoList = new ArrayList<>();
                            flowElementExclusiveGateway.setType("ExclusiveGateway");
                            flowElementExclusiveGateway.setActivityName(exclusiveGateway.getName());
                            flowElementExclusiveGateway.setActivityId(exclusiveGateway.getId());
                            //获取下面的所有连线
                            List<SequenceFlow> sequenceFlowList = exclusiveGateway.getOutgoingFlows();
                            sequenceFlowList.forEach(sequenceFlow -> {
                                FlowElementVo flowElementNext = getFlowElement(sequenceFlow.getId(), flowElements, flowElementVo);
                                flowElementVoList.add(flowElementNext);
                            });
                            flowElementExclusiveGateway.setFlowElementVoList(flowElementVoList);
                            return flowElementExclusiveGateway;
                        }
                    }
                    //ParallelGateway网关
                    if (flowElement instanceof ParallelGateway) {
                        ParallelGateway parallelGateway = (ParallelGateway) flowElement;
                        return flowElementVo;
                    }
                    if (flowElement instanceof SequenceFlow) {
                        SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                        if (activityId.equals(sequenceFlow.getId())) {
                            FlowElementVo flowElementSequenceFlow = new FlowElementVo();
                            List<FlowElementVo> flowElementVoList = new ArrayList<>();
                            flowElementSequenceFlow.setType("SequenceFlow");
                            //获取基本属性
                            flowElementSequenceFlow.setActivityName(sequenceFlow.getName());
                            flowElementSequenceFlow.setActivityId(sequenceFlow.getId());
                            //获取连线跳转条件
                            flowElementSequenceFlow.setCondition(sequenceFlow.getConditionExpression());
                            String targetRefId = sequenceFlow.getTargetRef();
                            //获取连线的下个节点
                            FlowElementVo flowElementNext = getFlowElement(targetRefId, flowElements, flowElementSequenceFlow);
                            flowElementVoList.add(flowElementNext);
                            flowElementSequenceFlow.setFlowElementVoList(flowElementVoList);
                            return flowElementSequenceFlow;
                        }
                    }
                    //结束节点
                    if (flowElement instanceof EndEvent) {
                        EndEvent endEvent = (EndEvent) flowElement;
                        if (activityId.equals(endEvent.getId())) {
                            //表示为结束节点，不用再往下递归
                            FlowElementVo flowElementEndEvent = new FlowElementVo();
                            flowElementEndEvent.setType("EndEvent");
                            flowElementEndEvent.setActivityName(endEvent.getName());
                            flowElementEndEvent.setActivityId(endEvent.getId());
                            return flowElementEndEvent;
                        }
                    }
                }
            }
        }
        return null;
    }


    public static FlowElement getNextFlowNode(BpmnModel bpmnModel, String activityId) {
        // 传节点定义key 获取当前节点
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(activityId);
        // 输出连线
        List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();
        String type = null;
        Map<String, Object> map = new HashMap<>();
        // 遍历返回下一个节点信息
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            // 类型自己判断
            FlowElement targetFlowElement = outgoingFlow.getTargetFlowElement();
            map.put("flowElement", targetFlowElement);
            // 用户任务
            if (targetFlowElement instanceof UserTask) {
                type = "UserTask";//用户模块
            } else if (targetFlowElement instanceof EndEvent) {
                type = "EndEvent";//结束节点
            } else if (targetFlowElement instanceof ExclusiveGateway) {
                type = "ExclusiveGateway"; //网关
            } else if (targetFlowElement instanceof StartEvent) {
                type = "StartEvent";//子任务的起点
            } else {
                type = "";
            }
            return targetFlowElement;
        }
        return null;
    }

    /**
     * @return java.util.List<org.flowable.bpmn.model.UserTask>
     * @description TODO (查找起始节点下一个用户任务列表列表)
     * @params [source] 起始节点
     * @author yanbin
     * @date 2024/1/19 15:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<UserTask> findNextUserTasks(FlowElement source) {
        return findNextUserTasks(source, null, null);
    }

    /**
     * @return java.util.List<org.flowable.bpmn.model.UserTask>
     * @description TODO (查找起始节点下一个用户任务列表列表)
     * @params [source 起始节点, hasSequenceFlow已经经过的连线的 ID，用于判断线路是否重复, userTaskList 用户任务列表]
     * @author yanbin
     * @date 2024/1/19 15:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<UserTask> findNextUserTasks(FlowElement source, Set<String> hasSequenceFlow, List<UserTask> userTaskList) {
        hasSequenceFlow = Optional.ofNullable(hasSequenceFlow).orElse(new HashSet<>());
        userTaskList = Optional.ofNullable(userTaskList).orElse(new ArrayList<>());
        // 获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);
        if (!sequenceFlows.isEmpty()) {
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
                if (targetFlowElement instanceof UserTask) {
                    // 若节点为用户任务，加入到结果列表中
                    userTaskList.add((UserTask) targetFlowElement);
                } else {
                    // 若节点非用户任务，继续递归查找下一个节点
                    findNextUserTasks(targetFlowElement, hasSequenceFlow, userTaskList);
                }
            }
        }
        return userTaskList;
    }

    /**
     * @return boolean
     * @description TODO (迭代从后向前扫描，判断目标节点相对于当前节点是否是串行不存在直接回退到子流程中的情况，但存在从子流程出去到父流程情况)
     * @params [source起始节点, target目标节点, visitedElements已经经过的连线的 ID，用于判断线路是否重复]
     * @author yanbin
     * @date 2024/1/19 15:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static boolean isSequentialReachable(FlowElement source, FlowElement target, Set<String> visitedElements) {
        visitedElements = visitedElements == null ? new HashSet<>() : visitedElements;
        if (source instanceof StartEvent && isInEventSubprocess(source)) {
            return false;
        }

        // 根据类型，获取入口连线
        List<SequenceFlow> sequenceFlows = getElementIncomingFlows(source);
        if (sequenceFlows != null && sequenceFlows.size() > 0) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (visitedElements.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                visitedElements.add(sequenceFlow.getId());
                FlowElement sourceFlowElement = sequenceFlow.getSourceFlowElement();
                // 这条线路存在目标节点，这条线路完成，进入下个线路
                if (target.getId().equals(sourceFlowElement.getId())) {
                    continue;
                }
                // 如果目标节点为并行网关，则不继续
                if (sourceFlowElement instanceof ParallelGateway) {
                    return false;
                }
                // 否则就继续迭代
                boolean isSequential = isSequentialReachable(sourceFlowElement, target, visitedElements);
                if (!isSequential) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return boolean
     * @description TODO (是否是子流程节点)
     * @params [flowElement]
     * @author yanbin
     * @date 2024/1/19 15:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected static boolean isInEventSubprocess(FlowElement flowElement) {
        FlowElementsContainer flowElementsContainer = flowElement.getParentContainer();
        while (flowElementsContainer != null) {
            if (flowElementsContainer instanceof EventSubProcess) {
                return true;
            }

            if (flowElementsContainer instanceof FlowElement) {
                flowElementsContainer = ((FlowElement) flowElementsContainer).getParentContainer();
            } else {
                flowElementsContainer = null;
            }
        }
        return false;
    }

    /**
     * @return java.lang.String
     * @description TODO (获取上一用户节点id)
     * @params [flowNode, bpmnModel]
     * @author yanbin
     * @date 2024/5/29 9:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String getLastUserTask(FlowNode flowNode, BpmnModel bpmnModel) {
        SequenceFlow sequenceFlow = flowNode.getIncomingFlows().get(0);
        //获取上一个节点的activityId
        String sourceRef = sequenceFlow.getSourceRef();
        //获取上一节点的信息
        FlowElement sourceFlowElement = sequenceFlow.getSourceFlowElement();
        //TODO 以下判断不全，有需要在加，缺子流程判断情况
        //TODO 上一节点是用户任务
        if (sourceFlowElement instanceof UserTask) {
            return sourceFlowElement.getId();
        }
        //TODO 上一节点是网关
        else if (sourceFlowElement instanceof ExclusiveGateway) {
            FlowNode lastFlowNode = (FlowNode) bpmnModel.getFlowElement(sourceRef);
            return getLastUserTask(lastFlowNode, bpmnModel);
        }
        //TODO 上一节点是开始节点
        else if (sourceFlowElement instanceof StartEvent) {
            return "startEvent";
        }
        return null;
    }

}
