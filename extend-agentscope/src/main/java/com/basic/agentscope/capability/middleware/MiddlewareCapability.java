package com.basic.agentscope.capability.middleware;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.tool.Toolkit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 能力：<b>Middleware 拦截</b>。
 *
 * <p>Middleware 串在 AgentScope 的调用链上，可以在"模型调用前"改写输入、
 * 在"模型调用外层"包裹执行。本能力用 {@link PromptGuardMiddleware} 实现两件事：</p>
 * <ol>
 *   <li>给系统提示词追加当前时间 / 会话等动态上下文；</li>
 *   <li>统计模型调用次数与耗时。</li>
 * </ol>
 *
 * <p>⚠️ 铁律：模块内存在多个 {@code ReActAgent} Bean，本类只<b>声明</b>不注入；
 * 任何地方注入 {@code ReActAgent} 都必须带 {@code @Qualifier}。</p>
 */
@Configuration
public class MiddlewareCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "middlewareCapabilityAgent";

    /** 提示词守卫（普通对象，由本能力持有，不注册为 Spring Bean） */
    private final PromptGuardMiddleware guard = new PromptGuardMiddleware();

    @Bean(AGENT_BEAN)
    public ReActAgent middlewareCapabilityAgent(ModelResolver modelResolver,
                                                @Qualifier("businessToolkit") Toolkit toolkit) {
        return ReActAgent.builder()
                .name("middleware-agent")
                .description("带 Middleware 拦截的智能体")
                .sysPrompt("""
                        你是一个演示 AgentScope Middleware 机制的智能助手。
                        你的系统提示词会被 Middleware 自动追加"当前时间 / 会话 ID"等运行时上下文，
                        请据此正常完成用户请求。
                        回答要求：
                        1. 需要数据时调用工具获取真实数据；
                        2. 回答用中文，简洁准确。
                        """)
                .model(modelResolver.resolve())
                .toolkit(toolkit)
                .middleware(guard)
                .maxIters(5)
                .build();
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "middleware",
                "Middleware 拦截",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "在调用链上串联 Middleware：改写系统提示词（注入当前时间/会话上下文）并统计模型调用次数与耗时",
                "SetUp",
                null,
                true,
                true,
                null,
                "/agentscope/capability/middleware/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "现在是几点？帮我看看系统里有哪些用户"),
                        CapabilityInfo.ParamInfo.text("sessionId", "会话 ID（可留空）", "")
                ));
    }

    @Override
    public String agentBeanName() {
        return AGENT_BEAN;
    }

    @Override
    public Map<String, Object> detail() {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("middlewareImplementation", PromptGuardMiddleware.class.getSimpleName());
        detail.put("modelCallCount", guard.modelCallCount());
        detail.put("averageElapsedMs", guard.averageElapsedMs());
        detail.put("totalElapsedMs", guard.totalElapsedMs());
        detail.put("maxElapsedMs", guard.maxElapsedMs());
        detail.put("lastModelName", guard.lastModelName());
        detail.put("lastSystemPrompt", guard.lastSystemPrompt());
        detail.put("intercepts", List.of("onSystemPrompt（改写系统提示词）", "onModelCall（统计次数/耗时）"));
        detail.put("agentBean", AGENT_BEAN);
        return detail;
    }
}
