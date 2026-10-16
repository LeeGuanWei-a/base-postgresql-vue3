package com.basic.converter;

import com.basic.flowable.common.constant.ProcessConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.BoundaryEvent;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BoundaryEventJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverterContext;
import org.flowable.editor.language.json.converter.util.JsonConverterUtil;

import java.util.Map;

/**
 * 边界事件（BoundaryEvent）自定义 JSON 转换器。
 *
 * <p>职责：在 Flowable 自带 BoundaryEvent 转换器基础上，把本项目在 modeler 里设置的
 * 「业务侧扩展属性」从 BPMN JSON 写回 {@link BoundaryEvent#getExtensionElements()}，
 * 并在反向转换时把它们再读出来。</p>
 *
 * <p>业务侧扩展属性命名空间为 {@code flowable}（继承自父类行为），形如：</p>
 * <ul>
 *   <li>{@link ProcessConstants#BOUNDARY_EXT_TYPE} — 边界事件类型简写（timer/error/...）</li>
 *   <li>{@link ProcessConstants#BOUNDARY_EXT_TIMER_DURATION} — 定时器时长</li>
 *   <li>{@link ProcessConstants#BOUNDARY_EXT_ERROR_CODE} — 错误码（业务侧 key）</li>
 *   <li>...</li>
 * </ul>
 *
 * <p>注意：Flowable 父类 BoundaryEventJsonConverter 自身已经处理了标准 bpmn 字段
 * （cancelActivity、attachedToRef、eventDefinitions、errorRef、signalRef、messageRef 等），
 * 本类只增量处理「业务侧扩展字段」。</p>
 *
 * <p>注意：BoundaryEvent 不是 Activity 子类，所以这里不调用 {@code ExtansionPropertiesUtil}
 * （它的签名是 {@code (JsonNode, Activity, String)}），由本类自己封装 addExtensionProperties。</p>
 *
 * @author integration-supplement
 */
public class BruceBoundaryEventJsonConverter extends BoundaryEventJsonConverter {

    /**
     * 需要读写扩展属性 key 集合（与 ProcessConstants.BOUNDARY_EXT_* 对齐）
     */
    private static final String[] BOUNDARY_EXT_KEYS = new String[]{
            ProcessConstants.BOUNDARY_EXT_TYPE,
            ProcessConstants.BOUNDARY_EXT_TIMER_DURATION,
            ProcessConstants.BOUNDARY_EXT_TIMER_CYCLE,
            ProcessConstants.BOUNDARY_EXT_TIMER_DATE,
            ProcessConstants.BOUNDARY_EXT_ERROR_CODE,
            ProcessConstants.BOUNDARY_EXT_SIGNAL_CODE,
            ProcessConstants.BOUNDARY_EXT_MESSAGE_ID,
            ProcessConstants.BOUNDARY_EXT_CANCEL_ACTIVITY
    };

    static void customFillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap,
                                Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        // 父类 BoundaryEventJsonConverter 已注册 8 种 boundary event stencil；
        // 这里只把 BaseElement -> BoundaryEvent 的反向映射改用本类即可（XML → JSON）。
        fillBpmnTypes(convertersToJsonMap);
    }

    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(BoundaryEvent.class, BruceBoundaryEventJsonConverter.class);
    }

    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement,
                                        BpmnJsonConverterContext converterContext) {
        super.convertElementToJson(propertiesNode, baseElement, converterContext);
        if (!(baseElement instanceof BoundaryEvent) || baseElement.getExtensionElements() == null
                || baseElement.getExtensionElements().isEmpty()) {
            return;
        }
        // 把 flowable 命名空间下的业务扩展属性写回 BPMN JSON，供 modeler 面板展示
        baseElement.getExtensionElements().forEach((namespace, elements) -> elements.forEach(extensionElement -> {
            String name = extensionElement.getName();
            for (String key : BOUNDARY_EXT_KEYS) {
                if (key.equals(name)) {
                    this.setPropertyValue(name, extensionElement.getElementText(), propertiesNode);
                    break;
                }
            }
        }));
    }

    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode,
                                               Map<String, JsonNode> shapeMap,
                                               BpmnJsonConverterContext converterContex) {
        FlowElement flowElement = super.convertJsonToElement(elementNode, modelNode, shapeMap, converterContex);
        addExtensionProperties(flowElement, elementNode);
        return flowElement;
    }

    /**
     * 把 BPMN JSON 上的业务扩展字段写入 BoundaryEvent 扩展元素。
     * 命名空间固定使用 flowable，与 BruceUserTaskJsonConverter / BruceCallActivityJsonConverter 保持一致。
     */
    private void addExtensionProperties(FlowElement flowElement, JsonNode elementNode) {
        if (!(flowElement instanceof BoundaryEvent)) {
            return;
        }
        BoundaryEvent boundaryEvent = (BoundaryEvent) flowElement;
        for (String key : BOUNDARY_EXT_KEYS) {
            JsonNode valueNode = JsonConverterUtil.getProperty(key, elementNode);
            if (valueNode != null && valueNode.isTextual() && !valueNode.asText().isEmpty()) {
                ExtensionElement extensionElement = new ExtensionElement();
                extensionElement.setName(key);
                extensionElement.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
                extensionElement.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
                extensionElement.setElementText(valueNode.asText());
                boundaryEvent.addExtensionElement(extensionElement);
            }
        }
    }
}
