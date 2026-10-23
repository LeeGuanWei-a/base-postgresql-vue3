package com.basic.agentscope.capability.channel;

/**
 * 渠道回调请求体（模拟 IM 平台推送过来的文本消息）。
 *
 * <p>普通 JavaBean，可被 Jackson 2 正常序列化 / 反序列化。</p>
 */
public class ChannelWebhookRequest {

    /** 文本内容 */
    private String text;

    /** 发送者 ID */
    private String userId;

    /** 会话 ID（不传则使用渠道绑定的 sessionId） */
    private String sessionId;

    /** 平台侧的消息 ID */
    private String messageId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
