package com.basic.flowable.utils;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.basic.common.utils.StringUtils;
import com.basic.workflow.domain.vo.FlowElementVo;
import org.dom4j.*;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.util.io.StringStreamSource;

import java.util.*;

/**
 * @description 流程模型工具类，负责 BPMN 的 XML 与模型对象互转、会签节点判断以及节点与表单元素提取
 * @author yanbin
 * @date 2024/1/8
 */
@Slf4j
public class ModelUtils {

    private static final BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();

    /**
     * @return BpmnModel 解析得到的流程模型对象
     * @description 把 BPMN 的 XML 字符串解析成流程模型对象
     * @params [xml] BPMN 流程定义的 XML 字符串
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static BpmnModel getBpmnModel(String xml) {
        return bpmnXMLConverter.convertToBpmnModel(new StringStreamSource(xml), false, false);
    }

    /**
     * @return boolean 串行多实例且完成条件要求全票通过时返回 true
     * @description 判断用户任务是否为顺序会签节点，即串行多实例且完成条件为全票通过
     * @params [userTask] 待判断的用户任务节点
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @return List<String> 用户任务上固定值指派表达式中的变量名集合
     * @description 解析 BPMN 的 XML，收集指派类型为固定值的用户任务表单变量名
     * @params [xml] BPMN 流程定义的 XML 字符串
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return String 补上网关标记属性后的 BPMN XML
     * @description 给排他网关对应的图形节点补上 isMarkerVisible 属性，使流程图显示网关叉号标记
     * @params [xmlStr] 原始 BPMN 流程定义的 XML 字符串
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return String 流程模型的 XML 字符串
     * @description 把流程模型对象序列化为 UTF-8 编码的 XML 字符串，已标记废弃
     * @params [bpmnModel] 流程模型对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Deprecated
    public static String getBpmnXmlStr(BpmnModel bpmnModel) {
        return StrUtil.utf8Str(getBpmnXml(bpmnModel));
    }

    /**
     * @return byte[] 流程模型的 XML 字节数组
     * @description 把流程模型对象序列化为 BPMN 的 XML 字节数组，已标记废弃
     * @params [bpmnModel] 流程模型对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Deprecated
    public static byte[] getBpmnXml(BpmnModel bpmnModel) {
        return bpmnXMLConverter.convertToXML(bpmnModel);
    }

    /**
     * @return List<SequenceFlow> 该节点的入口连线集合，非节点类型时返回空集合
     * @description 取流程节点的入口连线，元素不是流程节点时返回空集合
     * @params [source] 起始节点
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return List<SequenceFlow> 该节点的出口连线集合，非节点类型时返回空集合
     * @description 取流程节点的出口连线，元素不是流程节点时返回空集合
     * @params [source] 起始节点
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return StartEvent 开始节点，主流程没有开始事件时返回 null
     * @description 先取主流程的初始元素，不是开始事件时再遍历全部流程元素查找
     * @params [model] 流程模型对象
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return StartEvent 集合中的第一个开始节点，不存在时返回 null
     * @description 遍历流程元素集合，返回其中第一个开始事件
     * @params [flowElements] 流程元素集合
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return EndEvent 结束节点，不存在时返回 null
     * @description 取主流程的全部元素并查找其中的结束事件
     * @params [model] 流程模型对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static EndEvent getEndEvent(BpmnModel model) {
        Process process = model.getMainProcess();
        return getEndEvent(process.getFlowElements());
    }

    /**
     * @return EndEvent 集合中的第一个结束节点，不存在时返回 null
     * @description 遍历流程元素集合，返回其中第一个结束事件
     * @params [flowElements] 流程元素集合
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return UserTask 指定键的用户任务节点，不存在或不是用户任务时返回 null
     * @description 按节点键从主流程中取出用户任务节点
     * @params [model, taskKey] 流程模型对象；用户任务节点键
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return FlowElement 指定主键的流程元素，不存在时返回 null
     * @description 按元素主键从主流程中取出流程元素
     * @params [model, flowElementId] 流程模型对象；流程元素主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static FlowElement getFlowElementById(BpmnModel model, String flowElementId) {
        Process process = model.getMainProcess();
        return process.getFlowElement(flowElementId);
    }

    /**
     * @return String 元素绑定的表单键，元素为空或类型不支持时返回 null
     * @description 读取开始事件或用户任务绑定的表单键
     * @params [flowElement] 流程元素
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return String 属性值，开始节点或属性不存在时返回 null
     * @description 读取开始节点上指定扩展属性的值
     * @params [model, name] 流程模型对象；扩展属性名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String getStartEventAttributeValue(BpmnModel model, String name) {
        StartEvent startEvent = getStartEvent(model);
        return getElementAttributeValue(startEvent, name);
    }

    /**
     * @return String 属性值，结束节点或属性不存在时返回 null
     * @description 读取结束节点上指定扩展属性的值
     * @params [model, name] 流程模型对象；扩展属性名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String getEndEventAttributeValue(BpmnModel model, String name) {
        EndEvent endEvent = getEndEvent(model);
        return getElementAttributeValue(endEvent, name);
    }

    /**
     * @return String 属性值，用户任务或属性不存在时返回 null
     * @description 读取指定用户任务节点上扩展属性的值
     * @params [model, taskKey, name] 流程模型对象；用户任务节点键；扩展属性名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String getUserTaskAttributeValue(BpmnModel model, String taskKey, String name) {
        UserTask userTask = getUserTaskByKey(model, taskKey);
        return getElementAttributeValue(userTask, name);
    }

    /**
     * @return String 属性的值，元素为空或属性不存在时返回 null
     * @description 读取流程元素上指定扩展属性的第一个取值
     * @params [baseElement, name] 流程元素；扩展属性名
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return boolean 用户任务配置了多实例会签时返回 true
     * @description 判断指定用户任务是否配置了多实例会签
     * @params [model, taskKey] 流程模型对象；用户任务节点键
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return Collection<UserTask> 主流程及其子流程中的全部用户任务节点
     * @description 收集主流程内所有用户任务节点
     * @params [model] 流程模型对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static Collection<UserTask> getAllUserTaskEvent(BpmnModel model) {
        Process process = model.getMainProcess();
        Collection<FlowElement> flowElements = process.getFlowElements();
        return getAllUserTaskEvent(flowElements, null);
    }


    /**
     * @return Collection<UserTask> 收集到的用户任务集合，包含子流程内的用户任务
     * @description 递归遍历流程元素收集全部用户任务，并深入子流程继续查找
     * @params [flowElements, allElements] 待遍历的流程元素集合；承载结果的集合，为空时新建
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @description 从开始节点递归展开整条流程的流转节点，并把平铺结果打印到控制台
     * @params [bpmnModel] 流程模型对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
        log.debug("{}", flowElementVoList);
    }

    /**
     * @description 递归把流程节点树按节点类型平铺到结果列表，开始事件、用户任务、网关、连线分别收集
     * @params [flowStartElement, result] 当前流程节点树；平铺后的结果列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private static void handleTreeToList(FlowElementVo flowStartElement, List<Map<String, Object>> result) {
        Map<String, Object> map = new HashMap<>();
        List<FlowElementVo> list = flowStartElement.getFlowElementVoList();
        if (CollUtil.isNotEmpty(list)) {
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

    /**
     * @return List<List<String>> 始终返回 null，当前实现未填充结果
     * @description 按节点类型遍历流程元素，尝试收集开始节点出线与用户任务之间的流转关系
     * @params [activityId, flowElements, flowElementVoList] 节点类型标识，为 StartEvent 时从开始节点开始处理；流程元素集合；承载流转关系的结果列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private static List<List<String>> handleFlowElement(String activityId, Collection<FlowElement> flowElements, List<List<String>> flowElementVoList) {
        if (CollUtil.isNotEmpty(flowElements)) {
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
     * @return FlowElementVo 以该节点为根的流转树，找不到对应节点时返回 null
     * @description 从指定节点递归展开出线与后继节点，构造流程图渲染用的树形结构
     * @params [activityId, flowElements, flowElementVo] 节点类型标识或节点主键；流程元素集合；承载当前节点信息的对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static FlowElementVo getFlowElement(String activityId, Collection<FlowElement> flowElements, FlowElementVo flowElementVo) {
        if (CollUtil.isNotEmpty(flowElements)) {
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


    /**
     * @return FlowElement 第一条出线的目标节点，没有出线时返回 null
     * @description 取指定节点的第一条出线目标，作为流程图渲染时的下一个节点
     * @params [bpmnModel, activityId] 流程模型对象；当前节点的定义键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
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
     * @return List<UserTask> 起始节点之后可达的用户任务列表
     * @description 查找起始节点之后的下一个用户任务，委托带连线状态的重载实现
     * @params [source] 起始节点
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<UserTask> findNextUserTasks(FlowElement source) {
        return findNextUserTasks(source, null, null);
    }

    /**
     * @return List<UserTask> 沿出线收集到的用户任务列表
     * @description 沿出口连线递归向下查找用户任务，遇到用户任务即收集，跳过已走过的连线以避免环路
     * @params [source, hasSequenceFlow, userTaskList] 起始节点；已走过的连线主键集合，用于避免环路；承载结果的用户任务列表
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return boolean 目标节点可顺序到达时返回 true
     * @description 沿入口连线反向递归判断目标节点是否顺序可达，遇到并行网关即判定不可达
     * @params [source, target, visitedElements] 当前节点；目标节点；已走过的连线主键集合，用于避免环路
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return boolean 流程元素位于事件子流程内时返回 true
     * @description 逐层向上查找父容器，判断流程元素是否位于事件子流程内
     * @params [flowElement] 流程元素
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return String 上一个用户任务的主键，上一节点是开始节点时返回 startEvent
     * @description 沿入口连线向前追溯，取距离最近的一个用户任务节点主键
     * @params [flowNode, bpmnModel] 当前流程节点；流程模型对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String getLastUserTask(FlowNode flowNode, BpmnModel bpmnModel) {
        SequenceFlow sequenceFlow = flowNode.getIncomingFlows().get(0);
        //获取上一个节点的activityId
        String sourceRef = sequenceFlow.getSourceRef();
        //获取上一节点的信息
        FlowElement sourceFlowElement = sequenceFlow.getSourceFlowElement();
        // TODO 以下判断不全，有需要在加，缺子流程判断情况
        // 上一节点是用户任务
        if (sourceFlowElement instanceof UserTask) {
            return sourceFlowElement.getId();
        }
        // 上一节点是网关
        else if (sourceFlowElement instanceof ExclusiveGateway) {
            FlowNode lastFlowNode = (FlowNode) bpmnModel.getFlowElement(sourceRef);
            return getLastUserTask(lastFlowNode, bpmnModel);
        }
        // 上一节点是开始节点
        else if (sourceFlowElement instanceof StartEvent) {
            return "startEvent";
        }
        return null;
    }

}
