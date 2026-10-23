package com.basic.agentscope.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一的 Agent 对话响应体。
 */
public class AgentChatResponse {

    /** 能力 id */
    private String capabilityId;

    /** 模型最终文本回答 */
    private String text;

    /** 本次使用的会话 ID（能力涉及会话时有值） */
    private String sessionId;

    /** Agent 名称 */
    private String agentName;

    /** 实际使用的模型名 */
    private String modelName;

    /** 是否走了 Mock 模型（true 表示未配置真实 key，仅验证链路） */
    private boolean mock;

    /** 耗时（毫秒） */
    private long elapsedMs;

    /** 工具调用轨迹（能力涉及工具时有值） */
    private List<Map<String, Object>> toolCalls;

    /** 结构化输出（能力涉及结构化输出时有值） */
    private Object structured;

    /** 扩展数据 */
    private Map<String, Object> extra = new LinkedHashMap<>();

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public boolean isMock() {
        return mock;
    }

    public void setMock(boolean mock) {
        this.mock = mock;
    }

    public long getElapsedMs() {
        return elapsedMs;
    }

    public void setElapsedMs(long elapsedMs) {
        this.elapsedMs = elapsedMs;
    }

    public List<Map<String, Object>> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<Map<String, Object>> toolCalls) {
        this.toolCalls = toolCalls;
    }

    public Object getStructured() {
        return structured;
    }

    public void setStructured(Object structured) {
        this.structured = structured;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
