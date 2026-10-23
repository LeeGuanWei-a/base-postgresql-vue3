package com.basic.agentscope.capability.rag;

import io.agentscope.core.tool.Tool;

/**
 * RAG 检索工具 —— 让 Agent 主动在知识库里检索。
 *
 * <p>AgentScope 自带的 {@code KnowledgeRetrievalTools} 只能在
 * {@code RAGMode.AGENTIC} 下注册，且参数是 {@code query + limit}；
 * 这里自建一个更直白的版本：单个关键字参数，便于演示。</p>
 */
public class RagTools {

    /** 知识库服务 */
    private final KnowledgeService knowledgeService;

    public RagTools(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    /**
     * 在知识库中检索知识片段。
     *
     * @param keyword 检索关键字；为空或未命中时返回知识库前几条
     * @return 命中的知识文本
     */
    @Tool(name = "search_knowledge",
            description = "当用户询问『平台有哪些表』『字典是什么意思』『这个功能怎么用』等"
                    + "属于平台知识体系的问题时，调用本工具在 RAG 知识库中检索相关知识。"
                    + "参数 keyword 是检索关键字；如果没有命中任何知识，本工具会返回知识库的部分内容作为参考。",
            readOnly = true)
    public String searchKnowledge(String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        String hit = knowledgeService.search(kw, 3);
        if (hit.isBlank()) {
            return "【RAG 知识库】关键字 " + (kw.isEmpty() ? "(空)" : kw) + " 未命中任何知识，知识库当前共 "
                    + knowledgeService.count() + " 条知识。";
        }
        return "【RAG 知识库 · 关键字 " + (kw.isEmpty() ? "(空)" : kw) + "】\n" + hit;
    }
}
