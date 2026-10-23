package com.basic.agentscope.capability.rag;

import io.agentscope.core.rag.model.Document;
import io.agentscope.core.rag.model.RetrieveConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * RAG 知识库服务 —— 对 {@link SimpleKeywordKnowledge} 的管理门面。
 *
 * <p>对外提供"新增知识 / 列出知识 / 清空知识 / 检索知识"四个动作，
 * 供演示页与 {@code RagCapabilityController} 使用。</p>
 *
 * <p>⚠️ 本类持有 {@link SimpleKeywordKnowledge} 实例（不是 Spring Bean 类型的冲突项），
 * 因此可以安全地注册为 {@code @Component}。</p>
 */
@Component
public class KnowledgeService {

    /** 底层知识库（内存版关键词检索） */
    private final SimpleKeywordKnowledge knowledge = new SimpleKeywordKnowledge();

    /**
     * 取底层 {@code Knowledge} 实例，交给 AgentScope 的 RAG 管线使用。
     */
    public SimpleKeywordKnowledge getKnowledge() {
        return knowledge;
    }

    /**
     * 新增一条知识。
     *
     * @param title    标题
     * @param content  正文
     * @param keywords 关键字（逗号分隔；可为空）
     * @return 新知识的 ID
     */
    public String addKnowledge(String title, String content, String keywords) {
        return knowledge.addEntry(title, content, keywords);
    }

    /**
     * 列出全部知识（正文截断，避免响应体过大）。
     */
    public List<Map<String, Object>> listKnowledge() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SimpleKeywordKnowledge.KnowledgeEntry entry : knowledge.snapshot()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", entry.id());
            item.put("title", entry.title());
            item.put("keywords", entry.keywords());
            item.put("summary", truncate(entry.content(), 200));
            list.add(item);
        }
        return list;
    }

    /**
     * 清空知识库（含平台内置知识）。
     */
    public void clear() {
        knowledge.clear();
    }

    /**
     * 清空后重新灌入平台内置知识。
     */
    public void reset() {
        knowledge.reset();
    }

    /**
     * 知识条数。
     */
    public int count() {
        return knowledge.size();
    }

    /**
     * 按关键字检索知识，返回可读文本。
     *
     * @param query 查询串
     * @param limit 最多返回几条
     */
    public String search(String query, int limit) {
        RetrieveConfig config = RetrieveConfig.builder()
                .limit(limit <= 0 ? 5 : limit)
                .scoreThreshold(0.0)
                .build();
        List<Document> documents = knowledge.retrieve(query, config).block();
        if (documents == null || documents.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Document document : documents) {
            String title = String.valueOf(document.getMetadata().getPayloadValue("title"));
            String text = document.getMetadata().getContentText();
            sb.append("· ").append("null".equals(title) ? document.getId() : title)
                    .append("：").append(truncate(text, 300)).append('\n');
        }
        return sb.toString();
    }

    /**
     * 截断长文本。
     */
    private String truncate(String text, int max) {
        if (text == null) {
            return "";
        }
        String t = text.replaceAll("\\s+", " ").trim();
        return t.length() <= max ? t : t.substring(0, max) + "...";
    }
}
