package com.basic.agentscope.capability;

import java.util.Map;

/**
 * 能力提供者 —— 每个能力实现一个，Spring 启动时由 {@link CapabilityRegistry} 统一收集。
 *
 * <p><b>实现约定</b>：实现类通常是一个 {@code @Configuration}，在 {@code @Bean} 方法里
 * 构建该能力专属的 {@code ReActAgent}，并通过 {@link #descriptor()} 把能力信息暴露出去。</p>
 *
 * <p>⚠️ <b>重要</b>：由于一个模块里会存在<b>多个</b> {@code ReActAgent} 类型的 Bean，
 * 任何地方注入 {@code ReActAgent} 都<b>必须</b>带 {@code @Qualifier("beanName")}，
 * 否则会触发按类型注入歧义导致启动失败。</p>
 */
public interface CapabilityProvider {

    /**
     * 该能力的描述信息。
     */
    CapabilityInfo descriptor();

    /**
     * 该能力专属的 Agent Bean 名（用于 {@code @Qualifier} 注入与按名取 Bean）。
     *
     * @return bean 名；无专属 Agent 时返回 null
     */
    default String agentBeanName() {
        return null;
    }

    /**
     * 该能力的"状态/详情"信息，供前端展示。
     *
     * <p>例如：工具能力返回已注册工具清单；会话能力返回会话列表；
     * 外部依赖型能力返回依赖是否就绪。</p>
     *
     * @return 详情数据；无则返回 null
     */
    default Map<String, Object> detail() {
        return null;
    }
}
