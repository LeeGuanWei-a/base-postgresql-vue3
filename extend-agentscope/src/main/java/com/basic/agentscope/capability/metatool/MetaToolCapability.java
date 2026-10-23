package com.basic.agentscope.capability.metatool;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import com.basic.agentscope.tool.PlatformTools;
import com.basic.agentscope.tool.SystemTools;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.model.ToolSchema;
import io.agentscope.core.tool.Toolkit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 能力：<b>元工具（Meta Tool）</b>。
 *
 * <p>演示 AgentScope 的"工具治理"能力：</p>
 * <ol>
 *   <li>{@code ReActAgent.builder().enableMetaTool(true)} 会给 Toolkit 注册一个内置元工具
 *       {@code reset_equipped_tools}；</li>
 *   <li>该工具让模型自己在运行时"重置当前生效的工具集合 / 工具组"，
 *       而不是由开发者写死每轮对话能用哪些工具；</li>
 *   <li>它需要<b>带会话的运行时上下文</b>才能工作（{@code RuntimeContext} 里带 sessionId）。</li>
 * </ol>
 *
 * <p>本能力同时挂上平台数据查询工具，这样既能看到元工具存在，
 * 也能看到"普通工具 + 元工具"共存时模型的选择行为。</p>
 */
@Configuration
public class MetaToolCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "metaToolCapabilityAgent";

    /** AgentScope 内置元工具名（{@code Toolkit#registerMetaTool} 注册） */
    private static final String META_TOOL = "reset_equipped_tools";

    /** 该能力专属的 Agent */
    private ReActAgent agent;

    @Bean(AGENT_BEAN)
    public ReActAgent metaToolCapabilityAgent(ModelResolver modelResolver,
                                               PlatformTools platformTools,
                                               SystemTools systemTools) {
        // 能力专属 Toolkit（普通对象，不注册为 Spring Bean，避免与自动装配的 Toolkit 同类型歧义）
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(platformTools);
        toolkit.registerTool(systemTools);

        this.agent = ReActAgent.builder()
                .name("meta-tool-agent")
                .description("同时拥有业务工具与元工具（可自行重置生效工具集）的智能体")
                .sysPrompt("""
                        你是一个可以使用元工具治理自身工具集的智能体。
                        规则：
                        1. 需要平台真实数据（用户/字典/角色/操作日志/表行数）时，优先调用对应的业务工具；
                        2. 当发现当前可用工具不足以完成任务时，可以调用 reset_equipped_tools
                           重新选择要启用的工具/工具组，然后再继续干活；
                        3. 回答用中文，先给结论再列明细，不要编造工具没有返回的数据。
                        """)
                .model(modelResolver.resolve())
                .toolkit(toolkit)
                .maxIters(5)
                .enableMetaTool(true)
                .build();
        return this.agent;
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "meta-tool",
                "元工具",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "给 Agent 挂上内置元工具 reset_equipped_tools，让模型在运行时自己重置生效的工具集合与工具组",
                "Tools",
                null,
                true,
                true,
                null,
                "/agentscope/capability/meta-tool/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "系统里有哪些用户？需要时你可以自己切换工具"),
                        CapabilityInfo.ParamInfo.text("sessionId", "会话 ID（元工具需要会话上下文，建议填写）", "")
                ));
    }

    @Override
    public String agentBeanName() {
        return AGENT_BEAN;
    }

    @Override
    public Map<String, Object> detail() {
        Map<String, Object> detail = new LinkedHashMap<>();
        List<Map<String, Object>> tools = new ArrayList<>();
        boolean metaEnabled = false;
        if (agent != null && agent.getToolkit() != null) {
            for (ToolSchema schema : agent.getToolkit().getToolSchemas()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("name", schema.getName());
                item.put("description", schema.getDescription());
                item.put("meta", META_TOOL.equals(schema.getName()));
                tools.add(item);
                if (META_TOOL.equals(schema.getName())) {
                    metaEnabled = true;
                }
            }
            detail.put("toolCount", agent.getToolkit().getToolNames().size());
        } else {
            detail.put("toolCount", 0);
        }
        detail.put("metaToolEnabled", metaEnabled);
        detail.put("metaTool", META_TOOL);
        detail.put("tools", tools);
        detail.put("note", "元工具由 enableMetaTool(true) 自动注册，模型可在运行中调用它重置生效的工具集合；"
                + "该工具依赖会话上下文，请通过带 sessionId 的请求调用。");
        return detail;
    }
}
