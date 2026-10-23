package com.basic.agentscope.capability.rag;

/**
 * 新增知识请求体。
 *
 * <p>⚠️ 必须是普通 JavaBean（getter/setter），不要用 {@code tools.jackson.*} 注解，
 * 否则 AgentScope 内部的 Jackson 2 无法序列化。</p>
 */
public class RagKnowledgeRequest {

    /** 标题 */
    private String title;

    /** 正文 */
    private String content;

    /** 关键字，逗号分隔 */
    private String keywords;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
