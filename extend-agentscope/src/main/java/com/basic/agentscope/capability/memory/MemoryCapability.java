package com.basic.agentscope.capability.memory;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.memory.LongTermMemoryMode;
import io.agentscope.core.tool.Toolkit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 能力：<b>长期记忆（Long-Term Memory）</b>。
 *
 * <p>演示 AgentScope 的 {@code LongTermMemory} 扩展点：</p>
 * <ol>
 *   <li>{@code LongTermMemoryMode.STATIC_CONTROL} —— 框架在
 *       {@code PreCallEvent} 自动检索长期记忆并注入上下文，
 *       在 {@code PostCallEvent} 自动把整轮对话归档；</li>
 *   <li>额外注册一个 {@link MemoryTools} 工具，让 Agent 可以主动"回忆"。</li>
 * </ol>
 *
 * <p>本实现用 {@link InMemoryLongTermMemory}（内存版，线程安全）承载记忆，
 * 进程重启即清空，便于演示。</p>
 */
@Configuration
public class MemoryCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "memoryCapabilityAgent";

    /** 长期记忆模式：静态控制（框架自动检索 + 自动归档） */
    private static final LongTermMemoryMode MEMORY_MODE = LongTermMemoryMode.STATIC_CONTROL;

    /** 长期记忆实现 */
    private InMemoryLongTermMemory longTermMemory;

    /** 能力专属 Toolkit（普通对象，不注册为 Spring Bean，避免同类型歧义） */
    private Toolkit toolkit;

    @Bean(AGENT_BEAN)
    public ReActAgent memoryCapabilityAgent(ModelResolver modelResolver) {
        this.longTermMemory = new InMemoryLongTermMemory();

        this.toolkit = new Toolkit();
        this.toolkit.registerTool(new MemoryTools(this.longTermMemory));

        return ReActAgent.builder()
                .name("memory-agent")
                .description("具备长期记忆的智能体：跨轮次记住用户的关键信息")
                .sysPrompt("""
                        你是企业级 Java 基础平台（RuoYi 风格）的长期记忆助手。
                        你的对话会被自动归档到长期记忆里，下一轮开始前会自动检索相关记忆注入上下文。
                        回答要求：
                        1. 回答"我之前说过什么"这类问题时，必须调用 recall_long_term_memory 工具去回忆，不要凭空编造；
                        2. 回答用中文；
                        3. 记忆里没有的内容，如实说明"没有找到相关记录"。
                        """)
                .model(modelResolver.resolve())
                .toolkit(this.toolkit)
                .longTermMemory(this.longTermMemory)
                .longTermMemoryMode(MEMORY_MODE)
                // 框架内置的归档 Hook 读的是"默认会话槽位"，归档不到实际调用的上下文，
                // 这里补一个按调用归档的 Hook，保证每轮对话都会进长期记忆。
                .hook(new MemoryArchiveHook(this.longTermMemory))
                .maxIters(5)
                .build();
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "memory",
                "长期记忆",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "长期记忆：每轮对话自动归档，下次对话前自动检索并注入上下文，Agent 可主动回忆",
                "Notebook",
                null,
                true,
                true,
                null,
                "/agentscope/capability/memory/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "我最喜欢的框架是 Spring Boot，请记住"),
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
        detail.put("agentBean", AGENT_BEAN);
        detail.put("memoryMode", MEMORY_MODE.name());
        detail.put("memoryModeLabel", modeLabel(MEMORY_MODE));
        detail.put("memoryImpl", longTermMemory == null ? "-" : longTermMemory.getClass().getSimpleName());

        int count = longTermMemory == null ? 0 : longTermMemory.size();
        detail.put("entryCount", count);

        List<Map<String, Object>> items = new ArrayList<>();
        if (longTermMemory != null) {
            List<InMemoryLongTermMemory.MemoryEntry> snapshot = longTermMemory.snapshot();
            // 最多展示最近 20 条，摘要截断
            int from = Math.max(0, snapshot.size() - 20);
            for (InMemoryLongTermMemory.MemoryEntry entry : snapshot.subList(from, snapshot.size())) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", entry.id());
                item.put("role", entry.role());
                item.put("time", entry.time());
                item.put("summary", InMemoryLongTermMemory.truncate(entry.text(), 160));
                items.add(item);
            }
        }
        detail.put("entries", items);
        return detail;
    }

    /**
     * 记忆模式的中文说明。
     */
    private String modeLabel(LongTermMemoryMode mode) {
        return switch (mode) {
            case AGENT_CONTROL -> "Agent 自主控制：模型自行决定何时记录/检索（框架提供记忆工具）";
            case STATIC_CONTROL -> "框架静态控制：每次调用前自动检索、调用后自动归档";
            case BOTH -> "两者兼有：自动检索归档 + 模型也可主动调用记忆工具";
        };
    }
}
