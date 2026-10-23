package com.basic.agentscope.capability.permission;

/**
 * 切换权限模式的请求体。
 *
 * <p>⚠️ 必须是普通 JavaBean（getter/setter），不要用 {@code tools.jackson.*} 注解。</p>
 */
public class PermissionModeRequest {

    /** 目标权限模式：DEFAULT / ACCEPT_EDITS / EXPLORE / BYPASS / DONT_ASK */
    private String mode;

    /** 会话 ID；为空则使用默认会话 */
    private String sessionId;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
