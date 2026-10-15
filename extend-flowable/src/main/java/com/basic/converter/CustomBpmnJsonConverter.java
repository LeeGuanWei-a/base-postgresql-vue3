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
    }
}
