package com.basic.agentscope.capability.channel;

import com.basic.agentscope.config.AgentScopePropertiesConfig;
import com.basic.agentscope.config.ModelResolver;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 渠道接入服务 —— 维护"IM 渠道 ↔ Agent 会话"的绑定关系，并提供可真实跑通的回调入口。
 *
 * <p><b>能力边界</b>：本分支不引入 {@code agentscope-extensions-channel-*} 扩展包，
 * 因此只提供：</p>
 * <ol>
 *   <li>渠道绑定注册表（内存）：渠道类型 + webhook 路径 + 绑定的 sessionId；</li>
 *   <li>一个模拟真实 IM 回调的入口：收到文本 → 调用 Agent → 返回 Agent 的回答；</li>
 *   <li>状态面板：已注册渠道、webhook 地址、累计收发消息数。</li>
 * </ol>
 *
 * <p>真实企微 / 钉钉 / 飞书接入需要：引入对应扩展包 + 配置 IM 凭证 + 实现签名校验与消息体转换。</p>
 */
@Service
public class ChannelService implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(ChannelService.class);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 可直接复制到 application.yml 的配置示例 */
    private static final String YAML_EXAMPLE = """
            agentscope:
              channel:
                # 总开关：true 才会响应渠道回调
                enabled: true
                # 渠道默认会话 ID
                default-session-id: channel-default
                # 单次消息调用 Agent 的超时（秒）
                timeout-seconds: 120
                # 预置渠道绑定（启动时自动注册）
                channels:
                  - type: wecom
                    session-id: sess-wecom
                    description: 企业微信（模拟）
                  - type: dingtalk
                    session-id: sess-dingtalk
                    description: 钉钉（模拟）
            """;

    private final AgentScopePropertiesConfig properties;

    /**
     * ⚠️ 必须带 {@code @Qualifier}：本模块内存在多个 {@code ReActAgent} 类型的 Bean。
     */
    private final ReActAgent assistantAgent;

    private final ModelResolver modelResolver;

    /** 服务端口，用于拼接 webhook 地址 */
    @Value("${server.port:8080}")
    private String serverPort;

    /** 应用上下文路径 */
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /** 渠道类型 → 绑定关系 */
    private final Map<String, ChannelBinding> bindings = new ConcurrentHashMap<>();

    /** 运行时开关覆盖：null = 跟随配置 */
    private volatile Boolean runtimeEnabled;

    public ChannelService(AgentScopePropertiesConfig properties,
                          @Qualifier("basicAssistantAgent") ReActAgent assistantAgent,
                          ModelResolver modelResolver) {
        this.properties = properties;
        this.assistantAgent = assistantAgent;
        this.modelResolver = modelResolver;
    }

    @Override
    public void afterPropertiesSet() {
        registerConfiguredChannels();
    }

    // ------------------------------------------------------------------ 开关

    public boolean isEnabled() {
        Boolean override = runtimeEnabled;
        if (override != null) {
            return override;
        }
        return properties.getChannel() != null && properties.getChannel().isEnabled();
    }

    public void enable() {
        runtimeEnabled = Boolean.TRUE;
        registerConfiguredChannels();
    }

    public void disable() {
        runtimeEnabled = Boolean.FALSE;
    }

    /**
     * 依赖是否就绪：开关打开且至少注册了一个渠道。
     */
    public boolean isReady() {
        return isEnabled() && !bindings.isEmpty();
    }

    public Map<String, Object> switchInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("enabled", isEnabled());
        info.put("source", runtimeEnabled == null ? "配置文件 agentscope.channel.enabled" : "运行时内存开关");
        info.put("persistent", runtimeEnabled == null);
        info.put("note", runtimeEnabled == null
                ? "当前由配置项 agentscope.channel.enabled 控制，重启后保持不变"
                : "当前为运行时内存开关，重启后恢复为配置项 agentscope.channel.enabled 的取值");
        return info;
    }

    // ------------------------------------------------------------------ 渠道注册表

    /**
     * 注册（或覆盖）一个渠道绑定。
     */
    public ChannelBinding register(String channelType, String sessionId, String description, String source) {
        String type = channelType == null ? "" : channelType.trim().toLowerCase();
        if (type.isEmpty()) {
            throw new IllegalArgumentException("渠道类型不能为空");
        }
        ChannelBinding binding = new ChannelBinding();
        binding.setChannelType(type);
        binding.setSessionId(sessionId == null || sessionId.isBlank() ? defaultSessionId() : sessionId);
        binding.setDescription(description);
        binding.setSource(source);
        binding.setRegisteredAt(LocalDateTime.now().format(FMT));
        binding.setWebhookUrl(buildWebhookUrl(type));
        bindings.put(type, binding);
        return binding;
    }

    /**
     * 注销一个渠道绑定。
     */
    public boolean unregister(String channelType) {
        if (channelType == null) {
            return false;
        }
        return bindings.remove(channelType.trim().toLowerCase()) != null;
    }

    /**
     * 取渠道绑定（不存在则按需自动注册，保证回调入口永远可用）。
     */
    public ChannelBinding resolveBinding(String channelType) {
        String type = channelType == null ? "" : channelType.trim().toLowerCase();
        if (type.isEmpty()) {
            throw new IllegalArgumentException("渠道类型不能为空");
        }
        ChannelBinding binding = bindings.get(type);
        if (binding == null) {
            binding = register(type, defaultSessionId(), "首次回调时自动注册", "auto");
        }
        return binding;
    }

    /**
     * 已注册渠道列表。
     */
    public List<ChannelBinding> listBindings() {
        return new ArrayList<>(bindings.values());
    }

    // ------------------------------------------------------------------ 消息处理

    /**
     * 处理一条渠道消息：调用 Agent 并返回回答。
     *
     * @return 处理结果（含回复文本、耗时、会话 ID 等）
     */
    public Map<String, Object> handleMessage(String channelType, String text, String userId, String sessionId) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!isEnabled()) {
            result.put("success", false);
            result.put("message", "渠道能力未启用：请设置 agentscope.channel.enabled=true 或调用 POST /agentscope/channel/enable");
            return result;
        }
        if (text == null || text.isBlank()) {
            result.put("success", false);
            result.put("message", "text 不能为空");
            return result;
        }
        ChannelBinding binding = resolveBinding(channelType);
        String sid = sessionId == null || sessionId.isBlank() ? binding.getSessionId() : sessionId;
        long start = System.currentTimeMillis();
        try {
            RuntimeContext ctx = RuntimeContext.builder()
                    .sessionId(sid)
                    .userId(userId == null || userId.isBlank() ? "channel-user" : userId)
                    .build();
            Msg reply = assistantAgent.call(text, ctx).block(Duration.ofSeconds(timeoutSeconds()));
            String answer = reply == null ? "" : reply.getTextContent();
            binding.markMessage(answer);
            result.put("success", true);
            result.put("channelType", binding.getChannelType());
            result.put("sessionId", sid);
            result.put("reply", answer);
            result.put("agentName", assistantAgent.getName());
            result.put("model", modelResolver.resolve() == null ? "-" : modelResolver.resolve().getModelName());
            result.put("mock", modelResolver.isMock());
            result.put("elapsedMs", System.currentTimeMillis() - start);
            result.put("receivedCount", binding.getReceivedCount());
            result.put("sentCount", binding.getSentCount());
        } catch (Exception e) {
            log.warn("渠道[{}]消息处理失败", channelType, e);
            binding.markMessage(null);
            result.put("success", false);
            result.put("message", "消息处理失败：" + rootMessage(e));
            result.put("channelType", binding.getChannelType());
            result.put("sessionId", sid);
            result.put("elapsedMs", System.currentTimeMillis() - start);
        }
        return result;
    }

    // ------------------------------------------------------------------ 状态

    /**
     * 重新加载配置中的预置渠道绑定。
     */
    public Map<String, Object> reload() {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!isEnabled()) {
            result.put("success", false);
            result.put("message", "渠道能力未启用：请先设置 agentscope.channel.enabled=true 或调用 POST /agentscope/channel/enable");
            return result;
        }
        bindings.clear();
        registerConfiguredChannels();
        result.put("success", true);
        result.put("message", "已重新加载渠道绑定，共 " + bindings.size() + " 个");
        result.put("channels", channelViews());
        return result;
    }

    /**
     * 状态面板数据（与能力 detail() 一致）。
     */
    public Map<String, Object> status() {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("capability", "channel");
        detail.put("capabilityName", "渠道接入");
        detail.put("ready", isReady());
        detail.put("switch", switchInfo());

        List<Map<String, Object>> dependencies = new ArrayList<>();
        dependencies.add(dependency("agentscope-extensions-channel-* 扩展包", false,
                "提供企微/钉钉/飞书等真实 IM 通道，当前分支未引入"));
        dependencies.add(dependency("企业 IM 凭证（corpusId / appKey / appSecret 等）", false,
                "真实 IM 回调需要平台应用凭证，当前未配置"));
        dependencies.add(dependency("可用的 Agent（basicAssistantAgent）", true,
                "复用模块内已装配的默认业务助手，Mock 模式下即可跑通全链路"));
        dependencies.add(dependency("出网回调地址（公网可访问）", false,
                "IM 平台需要能回调到本服务，请在网关/Nginx 上做转发"));
        detail.put("dependencies", dependencies);

        detail.put("channels", channelViews());
        detail.put("channelCount", bindings.size());
        detail.put("totalReceived", totalReceived());
        detail.put("totalSent", totalSent());
        detail.put("missing", missingItems());
        detail.put("configExample", YAML_EXAMPLE);
        detail.put("notes", List.of(
                "本分支只提供装配骨架与协议模拟：POST /agentscope/channel/webhook/{channelType} 收到文本后调用 Agent 并回包",
                "回调入口已标记 @Anonymous（匿名可访问），真实 IM 回调无需携带登录令牌，但生产环境务必在网关层加签名校验与限流",
                "真实 IM 接入需要引入 agentscope-extensions-channel-* 扩展包 + 配置企业 IM 凭证"));
        return detail;
    }

    // ------------------------------------------------------------------ 内部方法

    /**
     * 注册配置中的预置渠道。
     */
    private void registerConfiguredChannels() {
        List<AgentScopePropertiesConfig.ChannelConfig> configs = properties.getChannel().getChannels();
        if (configs == null) {
            return;
        }
        for (AgentScopePropertiesConfig.ChannelConfig config : configs) {
            if (config == null || config.getType() == null || config.getType().isBlank()) {
                continue;
            }
            register(config.getType(), config.getSessionId(), config.getDescription(), "config");
        }
    }

    private List<Map<String, Object>> channelViews() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ChannelBinding binding : bindings.values()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("channelType", binding.getChannelType());
            item.put("sessionId", binding.getSessionId());
            item.put("description", binding.getDescription());
            item.put("webhookUrl", binding.getWebhookUrl());
            item.put("source", binding.getSource());
            item.put("registeredAt", binding.getRegisteredAt());
            item.put("receivedCount", binding.getReceivedCount());
            item.put("sentCount", binding.getSentCount());
            item.put("lastMessageAt", binding.getLastMessageAt());
            item.put("lastReply", binding.getLastReply());
            list.add(item);
        }
        return list;
    }

    private long totalReceived() {
        long total = 0;
        for (ChannelBinding binding : bindings.values()) {
            total += binding.getReceivedCount();
        }
        return total;
    }

    private long totalSent() {
        long total = 0;
        for (ChannelBinding binding : bindings.values()) {
            total += binding.getSentCount();
        }
        return total;
    }

    private List<String> missingItems() {
        List<String> missing = new ArrayList<>();
        if (!isEnabled()) {
            missing.add("开关未打开：agentscope.channel.enabled=false");
        }
        if (bindings.isEmpty()) {
            missing.add("未注册任何渠道：调用 POST /agentscope/channel/binding 或配置 agentscope.channel.channels");
        }
        missing.add("真实 IM 扩展包未引入：需 agentscope-extensions-channel-* + 企业 IM 凭证");
        return missing;
    }

    private String buildWebhookUrl(String channelType) {
        String base = properties.getChannel().getBaseUrl();
        if (base == null || base.isBlank()) {
            base = "http://127.0.0.1:" + serverPort;
        }
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String ctx = contextPath == null ? "" : contextPath;
        return base + ctx + "/agentscope/channel/webhook/" + channelType;
    }

    private String defaultSessionId() {
        String sessionId = properties.getChannel().getDefaultSessionId();
        return sessionId == null || sessionId.isBlank() ? "channel-default" : sessionId;
    }

    private int timeoutSeconds() {
        int timeout = properties.getChannel().getTimeoutSeconds();
        return timeout > 0 ? timeout : 120;
    }

    private Map<String, Object> dependency(String name, boolean ready, String hint) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("ready", ready);
        item.put("hint", hint);
        return item;
    }

    private String rootMessage(Throwable e) {
        Throwable cur = e;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        String msg = cur.getMessage();
        return (msg == null || msg.isBlank()) ? cur.getClass().getSimpleName() : msg;
    }
}
