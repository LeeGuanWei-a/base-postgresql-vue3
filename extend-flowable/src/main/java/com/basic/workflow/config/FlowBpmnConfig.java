package com.basic.workflow.config;

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
 * @description 流程引擎配置类，注册 BPMN 转换与校验、异步作业任务执行器等流程引擎依赖的基础 bean
 * @author yanbin
 * @date 2023/10/19
 *
 * <p>说明：这里不再注册任何 Jackson 2 的 {@code ObjectMapper} bean。
 * Spring Boot 4 的默认 JSON 栈是 Jackson 3（{@code tools.jackson.*}），
 * 项目里统一用 {@code com.basic.common.utils.JsonUtils}（内部封装 Jackson 3）。
 * 原来那个 {@code new com.fasterxml.jackson.databind.ObjectMapper()} bean
 * 是 Flowable 6.x + Spring Boot 2 时代的遗留，现已删除。</p>
 */
@Configuration
public class FlowBpmnConfig
        implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    /**
     * @return TaskExecutor 基于 SimpleAsyncTaskExecutor 的任务执行器
     * @description 创建流程引擎异步作业使用的任务执行器
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Process
    @Bean
    public TaskExecutor processTaskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    /**
     * @return ProcessValidatorFactory BPMN 校验器工厂实例
     * @description 创建 BPMN 模型校验器工厂，用于部署前的流程合法性校验
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Bean
    public ProcessValidatorFactory processValidator() {
        return new ProcessValidatorFactory();
    }

    /**
     * @return TypeConverter 默认的流程表达式类型转换器
     * @description 创建流程表达式求值时使用的类型转换器
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Bean
    public TypeConverter typeConverter() {
        return new TypeConverterImpl();
    }

    /**
     * @return BpmnXMLConverter BPMN 的 XML 与流程模型互转的转换器
     * @description 创建 BPMN 的 XML 与流程模型互转的转换器，流程模型保存与部署共用
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Bean
    public BpmnXMLConverter createBpmnXMLConverter() {
        return new BpmnXMLConverter();
    }

    /**
     * @return PropertySourcesPlaceholderConfigurer 忽略无法解析占位符的配置器
     * @description 创建占位符解析配置器，使 @Value 引用的配置缺失时不报错
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(true);
        return configurer;
    }

    /**
     * @description 引擎配置扩展回调，当前不做任何定制，沿用 Flowable 默认配置
     * @params [springProcessEngineConfiguration] Spring 环境下的流程引擎配置
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void configure(SpringProcessEngineConfiguration springProcessEngineConfiguration) {

    }
}
