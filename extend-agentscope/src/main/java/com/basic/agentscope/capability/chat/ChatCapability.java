package com.basic.agentscope.capability.chat;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import io.agentscope.core.ReActAgent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础对话能力 —— 复用平台默认智能体 {@code basicAssistantAgent}。
 *
 * <p>该智能体由 {@code com.basic.agentscope.config.AgentScopeConfig} 装配,
 * 参数取自 {@code application.yml} 的 {@code agentscope.agent.*}:名称 / 系统提示词 / 最大迭代轮次。</p>
 *
 * <p>注意:这里<strong>不</strong>再声明 {@code ReActAgent} Bean,
 * 否则会覆盖 starter 自动装配的默认智能体(见《详细设计》§4.2 循环依赖与自动装配抑制)。</p>
 */
@Configuration
public class ChatCapability implements CapabilityProvider {

    /** 复用平台默认智能体 Bean */
    public static final String AGENT_BEAN = "basicAssistantAgent";

    private final ReActAgent agent;

    public ChatCapability(@Qualifier(AGENT_BEAN) ReActAgent agent) {
        this.agent = agent;
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "chat",
                "基础对话",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "平台默认 ReAct 智能体:读取 agentscope.agent.* 配置(名称 / 系统提示词 / 最大迭代轮次),"
                        + "支持多轮会话记忆,不含业务工具。",
                "ChatDotRound",
                null,
                true,
                true,
                null,
                "/agentscope/capability/chat/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "你好，请介绍一下你自己。"),
                        CapabilityInfo.ParamInfo.text("sessionId", "会话 ID（留空=新会话）", "")));
    }

    @Override
    public String agentBeanName() {
        return AGENT_BEAN;
    }

    @Override
    public Map<String, Object> detail() {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("agentBean", AGENT_BEAN);
        detail.put("agentName", agent == null ? "-" : agent.getName());
        detail.put("maxIters", agent == null ? null : agent.getMaxIters());
        detail.put("modelName", agent == null || agent.getModel() == null ? "-" : agent.getModel().getModelName());
        detail.put("来源", "agentscope.agent.* 配置 + 显式装配的 basicAssistantAgent");
        detail.put("说明", "系统提示词与最大迭代轮次由 application.yml 的 agentscope.agent.* 决定");
        return detail;
    }
}
