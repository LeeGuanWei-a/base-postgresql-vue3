package com.basic.flowable.flow;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;


/**
 * @description 流程引擎配置类，负责设置流程图节点、连线与标注使用的中文字体
 * @author yanbin
 * @date 2023/10/19
 */
@Configuration
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    /**
     * @description 把流程图的节点字体、连线标签字体与标注字体统一设置为宋体，避免中文显示为方块
     * @params [engineConfiguration] Spring 管理的流程引擎配置对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.setActivityFontName("宋体");
        engineConfiguration.setLabelFontName("宋体");
        engineConfiguration.setAnnotationFontName("宋体");

    }
}

