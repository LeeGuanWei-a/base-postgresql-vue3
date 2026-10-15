package com.basic.flowable.config;

import com.basic.converter.CustomBpmnJsonConverter;
import org.springframework.context.annotation.Bean;

/**
 * @author yanbin
 * @description TODO (/)
 * @date 2023/10/19
 */
public class FlowExtensionConfig {
  @Bean
  public CustomBpmnJsonConverter createCustomBpmnJsonConverter() {
    return new CustomBpmnJsonConverter();
  }
}
