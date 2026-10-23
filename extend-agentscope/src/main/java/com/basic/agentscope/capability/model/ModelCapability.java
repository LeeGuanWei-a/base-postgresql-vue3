package com.basic.agentscope.capability.model;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.model.Model;
import io.agentscope.core.tool.Toolkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 能力：<b>多厂商模型</b>。
 *
 * <p>AgentScope 的 OpenAI 扩展实现的是 <b>OpenAI 兼容协议</b>，所以 OpenAI / DeepSeek /
 * 通义千问 / Kimi / 智谱 / Ollama 全部可以用<b>同一套 Agent 代码</b>，
 * 只改三个配置项（{@code base-url} / {@code api-key} / {@code model-name}）即可切换。</p>
 *
 * <p>本能力提供两个入口：</p>
 * <ul>
 *   <li>{@code GET  /agentscope/model/providers}：厂商清单 + 各自要改的配置项；</li>
 *   <li>{@code POST /agentscope/model/chat}：用指定厂商走一次对话（无 Key 时仍走 Mock 模型）。</li>
 * </ul>
 *
 * <p>⚠️ 铁律：模块内存在多个 {@code ReActAgent} Bean，本类只<b>声明</b>不注入；
 * 任何地方注入 {@code ReActAgent} 都必须带 {@code @Qualifier}。</p>
 */
@Configuration
public class ModelCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "modelCapabilityAgent";

    /** 厂商清单 */
    @Autowired
    private ModelProviderRegistry registry;

    /** 模型解析器（Agent Bean 创建时回填，供 detail() 使用） */
    private ModelResolver modelResolver;

    @Bean(AGENT_BEAN)
    public ReActAgent modelCapabilityAgent(ModelResolver modelResolver,
                                           @Qualifier("businessToolkit") Toolkit toolkit) {
        this.modelResolver = modelResolver;
        return ReActAgent.builder()
                .name("model-agent")
                .description("演示同一套代码切换不同厂商模型（OpenAI 兼容协议）")
                .sysPrompt("""
                        你是一个演示"多厂商模型切换"的智能助手。
                        当前生效的模型由 ModelResolver 决定（无 Key 时为 Mock 模型）。
                        回答要求：
                        1. 简单直接地回答用户问题；
                        2. 若被问及当前使用的模型，如实说明模型名与是否为 Mock 模式。
                        """)
                .model(modelResolver.resolve())
                .toolkit(toolkit)
                .maxIters(5)
                .build();
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "model",
                "多厂商模型",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "OpenAI 兼容协议一键切换 OpenAI / DeepSeek / 通义千问 / Kimi / 智谱 / Ollama：只改 base-url、api-key、model-name 三项",
                "Cpu",
                null,
                true,
                true,
                "/agentscope/model/chat",
                "/agentscope/capability/model/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "用一句话介绍你自己"),
                        CapabilityInfo.ParamInfo.select("provider", "厂商", "openai",
                                List.of("openai", "deepseek", "qwen", "kimi", "zhipu", "ollama")),
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
        String currentModel = "-";
        boolean mock = true;
        if (modelResolver != null) {
            try {
                Model model = modelResolver.resolve();
                currentModel = model == null ? "-" : model.getModelName();
                mock = modelResolver.isMock();
            } catch (Exception e) {
                currentModel = "-";
            }
        }
        detail.put("currentModel", currentModel);
        detail.put("mock", mock);
        detail.put("protocol", "OpenAI 兼容协议：POST {base-url}/chat/completions");
        detail.put("configItems", List.of(
                "agentscope.openai.base-url",
                "agentscope.openai.api-key",
                "agentscope.openai.model-name"));
        detail.put("providers", registry.toDetailList());
        detail.put("providersEndpoint", "/agentscope/model/providers");
        detail.put("chatEndpoint", "/agentscope/model/chat");
        detail.put("agentBean", AGENT_BEAN);
        return detail;
    }
}
