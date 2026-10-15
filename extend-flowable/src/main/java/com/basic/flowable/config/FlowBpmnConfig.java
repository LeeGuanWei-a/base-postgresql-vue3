package com.basic.flowable.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.common.engine.impl.de.odysseus.el.misc.TypeConverter;
import org.flowable.common.engine.impl.de.odysseus.el.misc.TypeConverterImpl;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.spring.boot.process.Process;
import org.flowable.validation.ProcessValidatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * @author yanbin
 * @description TODO (bpmn)
 * @date 2023/10/19
 */
@Configuration
public class FlowBpmnConfig
        implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {
    @Process
    @Bean
    public TaskExecutor processTaskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public ProcessValidatorFactory processValidator() {
        return new ProcessValidatorFactory();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public TypeConverter typeConverter() {
        return new TypeConverterImpl();
    }

    /**
     * BpmnXMLConverter
     *
     * @return BpmnXMLConverter
     */
    @Bean
    public BpmnXMLConverter createBpmnXMLConverter() {
        return new BpmnXMLConverter();
    }

    /**
     * BpmnJsonConverter
     *
     * @return BpmnJsonConverter
     */
    //    @Bean
    //    public BpmnJsonConverter createBpmnJsonConverter() {
    //        return new BpmnJsonConverter();
    //    }

    /**
     * 在配置文件中如果没有字段，使用@Value的时候就会忽略掉，不会报错
     *
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(true);
        return configurer;
    }

    @Override
    public void configure(SpringProcessEngineConfiguration springProcessEngineConfiguration) {

    }
}
