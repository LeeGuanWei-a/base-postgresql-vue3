package com.basic.agentscope.dto;

import java.util.List;

/**
 * 结构化输出的<b>目标 DTO</b> —— AgentScope 会按本类的字段生成 JSON Schema，
 * 把模型产出的 JSON 再反序列化成该类型的实例。
 *
 * <p>用法：{@code agent.call(prompt, AgentAnswer.class)}。</p>
 *
 * <p>⚠️ 必须是<b>普通 JavaBean</b>（只有 getter/setter）：
 * AgentScope 内部用 Jackson 2 的 {@code JsonNode} 处理结构化数据，
 * <b>不要加任何 Jackson 注解</b>（尤其不要用 {@code tools.jackson.*}），
 * 否则会出现注解版本不匹配的问题。</p>
 */
public class AgentAnswer {

    /** 结论标题（一句话概括本次回答） */
    private String title;

    /** 摘要正文（说明依据与结论） */
    private String summary;

    /** 关键词标签 */
    private List<String> tags;

    /** 置信度（0-100，数值越大越可信） */
    private int confidence;

    /** 建议的后续动作 */
    private List<String> actions;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }
}
