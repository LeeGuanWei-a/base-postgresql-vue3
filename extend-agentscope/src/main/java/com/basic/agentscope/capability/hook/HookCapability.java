package com.basic.agentscope.capability.hook;

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
 * 能力：<b>Hook 事件拦截</b>。
 *
 * <p>AgentScope 的 {@code Hook} 是挂在 ReAct 循环生命周期上的<b>旁路回调</b>：
 * 调用前（PRE_*）、调用后（POST_*）、出错时（ERROR）都会回调一次。</p>
 *
 * <p>本能力通过 {@link TimingAuditHook} 把每一次事件（类型 / 时间戳 / 耗时）记到内存，
 * 前端通过 {@code /agentscope/capability/hook/detail} 就能看到"这一轮对话到底发生了什么"。</p>
 *
 * <p>⚠️ 铁律：模块内存在多个 {@code ReActAgent} Bean，本类只<b>声明</b>不注入；
 * 任何地方注入 {@code ReActAgent} 都必须带 {@code @Qualifier}。</p>
 */
@Configuration
public class HookCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "hookCapabilityAgent";

    /** 审计 Hook（普通对象，由本能力持有，不注册为 Spring Bean） */
    private final TimingAuditHook auditHook = new TimingAuditHook();

    @Bean(AGENT_BEAN)
    public ReActAgent hookCapabilityAgent(ModelResolver modelResolver,
                                          @Qualifier("businessToolkit") Toolkit toolkit) {
        return ReActAgent.builder()
                .name("hook-agent")
                .description("带 Hook 生命周期审计的智能体")
                .sysPrompt("""
                        你是一个演示 AgentScope Hook 机制的智能助手。
                        你的每一步（推理 / 工具调用 / 总结）都会被 Hook 记录，请正常完成用户请求。
                        回答要求：
                        1. 需要数据时调用工具获取真实数据；
                        2. 回答用中文，简洁准确。
                        """)
                .model(modelResolver.resolve())
                .toolkit(toolkit)
                .hook(auditHook)
                .maxIters(5)
                .build();
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "hook",
                "Hook 拦截",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "挂载 Hook 旁路监听 ReAct 生命周期（PRE/POST_CALL、REASONING、ACTING、SUMMARY），逐条记录事件类型、时间戳与阶段耗时",
                "Bell",
                null,
                true,
                true,
                null,
                "/agentscope/capability/hook/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "现在几点？系统里有哪些用户？"),
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
        detail.put("hookImplementation", TimingAuditHook.class.getSimpleName());
        detail.put("eventTotal", auditHook.totalEvents());
        detail.put("maxRecords", TimingAuditHook.MAX_RECORDS);
        detail.put("eventTypeStats", auditHook.stats());
        detail.put("recentEvents", auditHook.recent(20));
        detail.put("watchedPhases", List.of("PRE_CALL", "POST_CALL", "PRE_REASONING", "POST_REASONING",
                "PRE_ACTING", "POST_ACTING", "PRE_SUMMARY", "POST_SUMMARY", "ERROR"));
        detail.put("agentBean", AGENT_BEAN);
        return detail;
    }
}
