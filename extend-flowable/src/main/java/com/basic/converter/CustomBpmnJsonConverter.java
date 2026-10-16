package com.basic.converter;

import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanbin
 * @description TODO (flowable   解析xml转换工具)
 * @date 2023/10/18
 */
@Configuration
public class CustomBpmnJsonConverter extends BpmnJsonConverter {
    static {
        BruceUserTaskJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
        BruceCallActivityJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
        BruceSubProcessJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
        // 边界事件转换器：仅替换 BaseElement -> JSON 方向的映射（XML → JSON）；
        // BPMN JSON → Java 方向仍走 Flowable 父类 BoundaryEventJsonConverter
        // （它已注册了 8 种 boundary event stencil）
        BruceBoundaryEventJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
    }
}
