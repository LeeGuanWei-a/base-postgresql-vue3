package com.basic.workflow.service.impl;

import com.basic.flowable.factory.FlowServiceFactory;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.service.IWfBpmnModelService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (工作流拓展属性)
 * @date 2024/01/17
 */
@Service
public class ExtensionElementsService extends FlowServiceFactory {
    @Autowired
    private IWfBpmnModelService wfBpmnModelService;

    /**
     * @return org.flowable.bpmn.model.MultiInstanceLoopCharacteristics
     * @description TODO (获取用户userTask上的多实例属性)
     * @params [userTask]
     * @author yanbin
     * @date 2024/1/17 11:01
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public MultiInstanceLoopCharacteristics getMulTiInstForUserTask(UserTask userTask) {
        return userTask.getLoopCharacteristics();
    }


    /**
     * @return java.util.List<org.flowable.bpmn.model.ExtensionElement>
     * @description TODO (获取某个拓展的拓展属性)
     * @params [activityId, procDefId, customPropertyName]
     * @author yanbin
     * @date 2024/1/17 11:15
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<ExtensionElement> getCustomProperty(String activityId, String procDefId, String customPropertyName) {
        UserTask userTask = (UserTask) wfBpmnModelService.findFlowNodeByActivityId(procDefId, activityId);
        Map<String, List<ExtensionElement>> extensionElements = userTask.getExtensionElements();
        if (MapUtils.isNotEmpty(extensionElements)) {
            List<ExtensionElement> values = extensionElements.get(customPropertyName);
            if (CollectionUtils.isNotEmpty(values)) {
                return values;
            }
        }
        return null;
    }

    /**
     * @return java.lang.String
     * @description TODO (获取userTask上的拓展信息  内容)
     * @params [extensionElements]
     * @author yanbin
     * @date 2024/1/17 11:18
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
     * @return java.lang.String
     * @description TODO (通过UserTask和拓展名获取拓展内容)
     * @params [userTask, customPropertyName]
     * @author yanbin
     * @date 2024/1/19 13:59
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String getUserTaskExtension(UserTask userTask, String customPropertyName) {
        Map<String, List<ExtensionElement>> extensionElements = userTask.getExtensionElements();
        if (MapUtils.isNotEmpty(extensionElements)) {
            List<ExtensionElement> values = extensionElements.get(customPropertyName);
            if (CollectionUtils.isNotEmpty(values)) {
                return values.get(0).getElementText();
            }
        }
        return null;
    }

    /**
     * @return java.lang.Boolean
     * @description TODO (判断是否是串行并行 true是顺序执行 false不是)
     * @params [procDefId, taskDefinitionKey]
     * @author yanbin
     * @date 2024/2/22 16:00
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
