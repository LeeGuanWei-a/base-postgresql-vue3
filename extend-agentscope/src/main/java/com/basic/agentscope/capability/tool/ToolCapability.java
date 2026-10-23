package com.basic.agentscope.capability.tool;

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
 * 能力：<b>工具调用（Tool Use）</b>。
 *
 * <p>演示 AgentScope 的 ReAct 循环如何工作：</p>
 * <ol>
 *   <li>模型的 JSON Schema 里包含已注册工具；</li>
 *   <li>模型决定调用某个工具 → 框架执行工具 → 结果回灌上下文；</li>
 *   <li>模型基于工具结果给出最终回答。</li>
 * </ol>
 *
 * <p>本能力注册的工具直接查 PostgreSQL，因此回答里带的是<b>真实业务数据</b>。</p>
 */
@Configuration
public class ToolCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "toolCapabilityAgent";

    /** 能力专属 Toolkit（普通对象，不注册为 Spring Bean，避免与自动装配的 Toolkit 同类型歧义） */
    private Toolkit toolkit;

    @Bean(AGENT_BEAN)
    public ReActAgent toolCapabilityAgent(ModelResolver modelResolver,
                                          PlatformTools platformTools,
                                          SystemTools systemTools) {
        this.toolkit = new Toolkit();
        this.toolkit.registerTool(platformTools);
        this.toolkit.registerTool(systemTools);

        return ReActAgent.builder()
                .name("tool-agent")
                .description("具备平台数据查询能力的智能体")
                .sysPrompt("""
                        你是企业级 Java 基础平台（RuoYi 风格）的智能助手。
                        你可以调用工具查询平台的真实数据（系统用户、字典、角色、操作日志、表行数、当前时间）。
                        回答要求：
                        1. 需要数据时必须调用工具，不要凭空编造；
                        2. 回答用中文，先给结论再列明细；
                        3. 工具返回为空时如实说明"没有查到"，不要猜测。
                        """)
                .model(modelResolver.resolve())
                .toolkit(this.toolkit)
                .maxIters(5)
                .build();
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "tool",
                "工具调用",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "ReAct 循环：模型自主决定调用业务工具（查用户/字典/角色/操作日志/表行数），基于真实数据作答",
                "MagicStick",
                null,
                true,
                true,
                null,
                "/agentscope/capability/tool/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "系统里有哪些用户？管理员是谁？"),
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
        List<Map<String, Object>> tools = new ArrayList<>();
        if (toolkit != null) {
            for (ToolSchema schema : toolkit.getToolSchemas()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("name", schema.getName());
                item.put("description", schema.getDescription());
                item.put("parameters", schema.getParameters());
                tools.add(item);
            }
            detail.put("toolCount", toolkit.getToolNames().size());
        }
        detail.put("tools", tools);
        detail.put("maxIters", 5);
        return detail;
    }
}
