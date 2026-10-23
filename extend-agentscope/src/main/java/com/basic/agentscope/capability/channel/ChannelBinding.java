package com.basic.agentscope.capability.channel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 渠道绑定 —— 一个 IM 渠道与 Agent 会话之间的绑定关系。
 *
 * <p>本分支只提供"装配骨架"：绑定信息由内存维护，真实 IM 的签名校验、消息体解析
 * 需要引入 {@code agentscope-extensions-channel-*} 扩展包后自行接管。</p>
 */
public class ChannelBinding {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 渠道类型，如 wecom / dingtalk / feishu */
    private String channelType;

    /** 绑定的会话 ID（Agent 会话上下文） */
    private String sessionId;

    /** 备注 */
    private String description;

    /** webhook 回调地址 */
    private String webhookUrl;

    /** 绑定来源：config / api / auto */
    private String source;

    /** 注册时间 */
    private String registeredAt;

    /** 累计收到的消息数 */
    private long receivedCount;

    /** 累计发出的回复数 */
    private long sentCount;

    /** 最近一次收到消息的时间 */
    private String lastMessageAt;

    /** 最近一次回复摘要 */
    private String lastReply;

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    public long getReceivedCount() {
        return receivedCount;
    }

    public void setReceivedCount(long receivedCount) {
        this.receivedCount = receivedCount;
    }

    public long getSentCount() {
        return sentCount;
    }

    public void setSentCount(long sentCount) {
        this.sentCount = sentCount;
    }

    public String getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(String lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public String getLastReply() {
        return lastReply;
    }

    public void setLastReply(String lastReply) {
        this.lastReply = lastReply;
    }

    /**
     * 记录一次消息往返。
     */
    public void markMessage(String reply) {
        this.receivedCount = this.receivedCount + 1;
        this.lastMessageAt = LocalDateTime.now().format(FMT);
        if (reply != null) {
            this.sentCount = this.sentCount + 1;
            this.lastReply = reply;
        }
    }
}
