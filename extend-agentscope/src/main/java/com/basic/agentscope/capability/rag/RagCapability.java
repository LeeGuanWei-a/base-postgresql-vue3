package com.basic.agentscope.capability.rag;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.rag.RAGMode;
import io.agentscope.core.rag.model.RetrieveConfig;
import io.agentscope.core.tool.Toolkit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 能力：<b>RAG 知识库（Retrieval-Augmented Generation）</b>。
 *
 * <p>演示 AgentScope 的 {@code Knowledge} 扩展点与 {@code RAGMode}：</p>
 * <ol>
 *   <li>{@code RAGMode.GENERIC} —— 框架在每次推理前自动调用
 *       {@code Knowledge.retrieve(query, config)}，把命中的知识包进
 *       {@code <retrieved_knowledge>} 段落注入上下文（框架托管的 RAG 管线）；</li>
 *   <li>同时注册 {@link RagTools} 工具，让 Agent 也可以主动检索知识库；</li>
 *   <li>知识库由 {@link SimpleKeywordKnowledge} 承载（内存版关键词检索），
 *       可通过 {@code /agentscope/rag/ingest} 动态灌入新知识。</li>
 * </ol>
 */
@Configuration
public class RagCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "ragCapabilityAgent";

    /** 检索模式：框架托管（每轮推理前自动检索并注入） */
    private static final RAGMode RAG = RAGMode.GENERIC;

    /** 检索参数：关键词打分未做标定，因此阈值取 0，靠排序控制质量 */
    private static final RetrieveConfig RETRIEVE_CONFIG = RetrieveConfig.builder()
            .limit(5)
            .scoreThreshold(0.0)
            .build();

    /** 知识库服务 */
    private KnowledgeService knowledgeService;

    /** 能力专属 Toolkit（普通对象，不注册为 Spring Bean，避免同类型歧义） */
    private Toolkit toolkit;

    @Bean(AGENT_BEAN)
    public ReActAgent ragCapabilityAgent(ModelResolver modelResolver, KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;

        this.toolkit = new Toolkit();
        this.toolkit.registerTool(new RagTools(knowledgeService));

        return ReActAgent.builder()
                .name("rag-agent")
                .description("带 RAG 知识库的智能体：回答前先检索平台知识库")
                .sysPrompt("""
                        你是企业级 Java 基础平台（RuoYi 风格）的知识库助手。
                        回答要求：
                        1. 涉及系统表结构、字典含义、平台能力清单等问题时，优先调用 search_knowledge 工具检索知识库；
                        2. 严格依据检索到的知识作答，不要编造表名、字段名；
                        3. 知识库里没有的内容，如实说明"知识库中暂无相关记录"；
                        4. 回答用中文，先给结论再列明细。
                        """)
                .model(modelResolver.resolve())
                .toolkit(this.toolkit)
                .knowledge(knowledgeService.getKnowledge())
                .ragMode(RAG)
                .retrieveConfig(RETRIEVE_CONFIG)
                .maxIters(5)
                .build();
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "rag",
                "RAG 知识库",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "RAG 检索增强：每轮推理前自动检索知识库并注入上下文，Agent 也可主动检索，支持动态灌入知识",
                "Search",
                null,
                true,
                true,
                null,
                "/agentscope/capability/rag/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "sys_user 表有哪些字段？"),
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
        detail.put("ragMode", RAG.name());
        detail.put("ragModeLabel", modeLabel(RAG));
        detail.put("knowledgeImpl", knowledgeService == null ? "-"
                : knowledgeService.getKnowledge().getClass().getSimpleName());
        detail.put("limit", RETRIEVE_CONFIG.getLimit());
        detail.put("scoreThreshold", RETRIEVE_CONFIG.getScoreThreshold());

        int count = knowledgeService == null ? 0 : knowledgeService.count();
        detail.put("knowledgeCount", count);

        List<Map<String, Object>> items = new ArrayList<>();
        if (knowledgeService != null) {
            items.addAll(knowledgeService.listKnowledge());
        }
        detail.put("knowledge", items);
        return detail;
    }

    /**
     * 检索模式的中文说明。
     */
    private String modeLabel(RAGMode mode) {
        return switch (mode) {
            case GENERIC -> "框架托管：每轮推理前自动检索知识库并注入上下文";
            case AGENTIC -> "模型自主：注册检索工具，由模型自己决定何时检索";
            case NONE -> "关闭：不使用知识库";
        };
    }
}
