package com.basic.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.basic.flowable.factory.FlowServiceFactory;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.service.IWfBpmnModelService;
import cn.hutool.core.map.MapUtil;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description 工作流扩展属性服务，从 BPMN 模型中读取用户任务的多实例配置与自定义扩展属性
 * @author yanbin
 * @date 2024/01/17
 */
@Service
public class ExtensionElementsService extends FlowServiceFactory {
    @Autowired
    private IWfBpmnModelService wfBpmnModelService;

    /**
     * @return MultiInstanceLoopCharacteristics 该用户任务的多实例配置，非多实例节点返回 null
     * @description 取用户任务上的多实例循环特征（会签/或签配置）
     * @params [userTask] 目标用户任务节点
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public MultiInstanceLoopCharacteristics getMulTiInstForUserTask(UserTask userTask) {
        return userTask.getLoopCharacteristics();
    }


    /**
     * @return List<ExtensionElement> 该扩展属性的全部元素，节点上没有该扩展属性时返回 null
     * @description 按流程定义 id + 节点 id 找到用户任务，再取其指定名称的扩展元素列表
     * @params [activityId, procDefId, customPropertyName] 节点 id；流程定义 id；扩展元素名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<ExtensionElement> getCustomProperty(String activityId, String procDefId, String customPropertyName) {
        UserTask userTask = (UserTask) wfBpmnModelService.findFlowNodeByActivityId(procDefId, activityId);
        Map<String, List<ExtensionElement>> extensionElements = userTask.getExtensionElements();
        if (MapUtil.isNotEmpty(extensionElements)) {
            List<ExtensionElement> values = extensionElements.get(customPropertyName);
            if (CollUtil.isNotEmpty(values)) {
                return values;
            }
        }
        return null;
    }

    /**
     * @return String 该扩展元素的文本内容，节点上没有该扩展属性时返回 null
     * @description 取用户任务上指定扩展元素的文本值（只取第一个元素）
     * @params [activityId, procDefId, customPropertyName] 节点 id；流程定义 id；扩展元素名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String getCustomPropertyText(String activityId, String procDefId, String customPropertyName) {
        List<ExtensionElement> extensionElement = this.getCustomProperty(activityId, procDefId, customPropertyName);
        if (extensionElement != null) {
            return extensionElement.get(0).getElementText();
        }
        return null;
    }

    /**
     * @return String 该扩展元素的文本内容，节点上没有该扩展属性时返回 null
     * @description 直接从传入的用户任务对象上取指定扩展元素的文本值（只取第一个元素）
     * @params [userTask, customPropertyName] 目标用户任务节点；扩展元素名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String getUserTaskExtension(UserTask userTask, String customPropertyName) {
        Map<String, List<ExtensionElement>> extensionElements = userTask.getExtensionElements();
        if (MapUtil.isNotEmpty(extensionElements)) {
            List<ExtensionElement> values = extensionElements.get(customPropertyName);
            if (CollUtil.isNotEmpty(values)) {
                return values.get(0).getElementText();
            }
        }
        return null;
    }

    /**
     * @return Boolean true 表示顺序执行（串行会签），false 表示并行执行
     * @description 按流程定义 id 与节点 id 取用户任务的多实例配置，判断其是否为串行执行
     * @params [procDefId, activityId] 流程定义 id；用户任务节点 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public Boolean getMulTIinstSequentialForKey(String procDefId, String activityId) {
        //获取bpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);
        //获取userTask
        UserTask userTask = ModelUtils.getUserTaskByKey(bpmnModel, activityId);
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = this.getMulTiInstForUserTask(userTask);
        return multiInstanceLoopCharacteristics.isSequential();
    }
}
