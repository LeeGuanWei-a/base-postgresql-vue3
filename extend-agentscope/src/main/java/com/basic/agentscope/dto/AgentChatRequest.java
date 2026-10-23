package com.basic.agentscope.dto;

import java.util.Map;

/**
 * 统一的 Agent 对话请求体。
 *
 * @param prompt    用户输入（必填）
 * @param sessionId 会话 ID；为空则由后端按能力语义决定（随机新建 / 使用默认值）
 * @param options   能力相关的可选参数
 */
public class AgentChatRequest {

    private String prompt;

    private String sessionId;

    private Map<String, Object> options;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    /**
     * 读取字符串型选项。
     */
    public String option(String key, String defaultValue) {
        if (options == null || options.get(key) == null) {
            return defaultValue;
        }
        Object v = options.get(key);
        return String.valueOf(v);
    }

    /**
     * 读取布尔型选项。
     */
    public boolean optionBoolean(String key, boolean defaultValue) {
        if (options == null || options.get(key) == null) {
            return defaultValue;
        }
        Object v = options.get(key);
        if (v instanceof Boolean b) {
            return b;
        }
        return Boolean.parseBoolean(String.valueOf(v));
    }
}
