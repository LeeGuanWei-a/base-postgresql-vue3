package com.basic.agentscope.capability.mcp;

import com.basic.agentscope.config.AgentScopePropertiesConfig;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.core.tool.mcp.McpClientBuilder;
import io.agentscope.core.tool.mcp.McpClientWrapper;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
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
 * MCP 客户端服务 —— 管理一组 MCP Server，并把它们的工具注册进共享 Toolkit。
 *
 * <p><b>依赖</b>：需要真实可访问的 MCP Server（stdio / SSE / Streamable HTTP）。本机没有，
 * 因此默认关闭（{@code agentscope.mcp.enabled=false}），启动时<b>不会</b>发起任何连接。</p>
 *
 * <p><b>探测策略</b>：状态只依据配置判断（"未配置 / 已配置待连接 / 已注册 / 连接失败"），
 * 只有显式调用 {@code POST /agentscope/mcp/reload} 时才会真正去连接。</p>
 *
 * <p>注册链路（已用 javap 核实 AgentScope 2.0.4）：</p>
 * <pre>
 * Toolkit.registerMcpClient(McpClientWrapper)  →  内部先 initialize() 再 listTools() 注册工具
 * Toolkit.removeMcpClient(String)
 * Toolkit.closeMcpClients()
 * </pre>
 */
@Service
public class McpService implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(McpService.class);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 可直接复制到 application.yml 的配置示例 */
    private static final String YAML_EXAMPLE = """
            agentscope:
              mcp:
                # 总开关：true 才会真正连接 MCP Server
                enabled: true
                # 需要启用的 Server 名称，逗号分隔（与 clients[].name 对应）
                servers: demo-filesystem,demo-remote
                # 单个 Server 的连接超时（秒）
                timeout-seconds: 20
                # 每个 Server 的连接方式：stdio / sse / streamable-http
                clients:
                  - name: demo-filesystem
                    type: stdio
                    command: npx
                    args: ["-y", "@modelcontextprotocol/server-filesystem", "D:/temp"]
                    description: 本地文件系统示例
                  - name: demo-remote
                    type: streamable-http
                    url: http://127.0.0.1:8931/mcp
                    description: 远端 MCP Server 示例
            """;

    private final AgentScopePropertiesConfig properties;

    /** 业务共享 Toolkit（单例），MCP 工具注册后对所有 Agent 可见 */
    private final Toolkit toolkit;

    /** Server 名称 → 运行状态 */
    private final Map<String, McpServerState> states = new ConcurrentHashMap<>();

    /** 运行时开关覆盖：null = 跟随配置；true/false = 内存开关 */
    private volatile Boolean runtimeEnabled;

    public McpService(AgentScopePropertiesConfig properties,
                       @Qualifier("businessToolkit") Toolkit toolkit) {
        this.properties = properties;
        this.toolkit = toolkit;
    }

    @Override
    public void afterPropertiesSet() {
        // 启动时只做"配置探测"，不发起任何连接
        refreshConfiguredStates();
    }

    // ------------------------------------------------------------------ 开关

    /**
     * 当前是否启用（运行时覆盖优先于配置）。
     */
    public boolean isEnabled() {
        Boolean override = runtimeEnabled;
        if (override != null) {
            return override;
        }
        return properties.getMcp() != null && properties.getMcp().isEnabled();
    }

    /**
     * 内存启用（重启后失效，回到配置值）。
     */
    public void enable() {
        runtimeEnabled = Boolean.TRUE;
        refreshConfiguredStates();
    }

    /**
     * 内存禁用（重启后失效，回到配置值）。
     */
    public void disable() {
        runtimeEnabled = Boolean.FALSE;
    }

    /**
     * 依赖是否就绪：开关打开且至少有一个 Server 已成功注册。
     */
    public boolean isReady() {
        if (!isEnabled()) {
            return false;
        }
        for (McpServerState state : states.values()) {
            if (McpServerState.STATUS_REGISTERED.equals(state.getStatus())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 开关来源说明（供前端展示"重启后是否持久"）。
     */
    public Map<String, Object> switchInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("enabled", isEnabled());
        info.put("source", runtimeEnabled == null ? "配置文件 agentscope.mcp.enabled" : "运行时内存开关");
        info.put("persistent", runtimeEnabled == null);
        info.put("note", runtimeEnabled == null
                ? "当前由配置项 agentscope.mcp.enabled 控制，重启后保持不变"
                : "当前为运行时内存开关，重启后恢复为配置项 agentscope.mcp.enabled 的取值");
        return info;
    }

    // ------------------------------------------------------------------ 连接管理

    /**
     * 重新加载并连接全部已配置的 MCP Server。
     *
     * <p>失败不会抛出异常，而是逐个记录失败原因，方便前端直接展示。</p>
     *
     * @return 每个 Server 的加载结果
     */
    public Map<String, Object> reload() {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!isEnabled()) {
            result.put("success", false);
            result.put("message", "MCP 客户端未启用：请先设置 agentscope.mcp.enabled=true 或调用 POST /agentscope/mcp/enable");
            result.put("servers", serverViews());
            return result;
        }
        List<String> names = properties.getMcp().serverNames();
        if (names.isEmpty()) {
            result.put("success", false);
            result.put("message", "未配置任何 MCP Server：请填写 agentscope.mcp.servers（逗号分隔）");
            result.put("servers", serverViews());
            return result;
        }

        // 先关闭旧的 MCP 客户端，避免重复注册
        try {
            toolkit.closeMcpClients();
        } catch (Exception e) {
            log.warn("关闭旧的 MCP 客户端失败", e);
        }
        states.clear();
        refreshConfiguredStates();

        int ok = 0;
        for (String name : names) {
            McpServerState state = states.computeIfAbsent(name, McpServerState::new);
            AgentScopePropertiesConfig.McpServerConfig config = findClientConfig(name);
            if (config == null) {
                state.setStatus(McpServerState.STATUS_NOT_CONFIGURED);
                state.setMessage("缺少 agentscope.mcp.clients 中 name=" + name + " 的连接配置");
                continue;
            }
            state.setType(config.getType());
            state.setTarget(describeTarget(config));
            state.setStatus(McpServerState.STATUS_CONNECTING);
            state.setMessage("正在连接…");
            long start = System.currentTimeMillis();
            try {
                McpClientWrapper wrapper = buildClient(config);
                toolkit.registerMcpClient(wrapper).block(Duration.ofSeconds(timeoutSeconds()));
                List<McpSchema.Tool> tools = wrapper.listTools().block(Duration.ofSeconds(timeoutSeconds()));
                List<String> toolNames = new ArrayList<>();
                if (tools != null) {
                    for (McpSchema.Tool tool : tools) {
                        toolNames.add(tool.name());
                    }
                }
                state.setStatus(McpServerState.STATUS_REGISTERED);
                state.setMessage("已连接并注册 " + toolNames.size() + " 个工具");
                state.setToolNames(toolNames);
                state.setRegisteredAt(LocalDateTime.now().format(FMT));
                state.setElapsedMs(System.currentTimeMillis() - start);
                ok++;
            } catch (Exception e) {
                state.setStatus(McpServerState.STATUS_FAILED);
                state.setMessage("连接失败：" + rootMessage(e));
                state.setToolNames(new ArrayList<>());
                state.setElapsedMs(System.currentTimeMillis() - start);
                log.warn("MCP Server[{}]连接失败", name, e);
            }
        }
        result.put("success", ok > 0);
        result.put("message", ok > 0
                ? "成功连接 " + ok + " / " + names.size() + " 个 MCP Server"
                : "全部 " + names.size() + " 个 MCP Server 连接失败");
        result.put("servers", serverViews());
        return result;
    }

    /**
     * 已加载的 MCP Server 及其工具清单。
     */
    public List<Map<String, Object>> listTools() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (McpServerState state : states.values()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("server", state.getName());
            item.put("type", state.getType());
            item.put("status", state.getStatus());
            item.put("message", state.getMessage());
            item.put("tools", state.getToolNames());
            list.add(item);
        }
        return list;
    }

    /**
     * 状态面板数据（与能力 detail() 一致）。
     */
    public Map<String, Object> status() {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("capability", "mcp");
        detail.put("capabilityName", "MCP 客户端");
        detail.put("ready", isReady());
        detail.put("switch", switchInfo());

        List<Map<String, Object>> dependencies = new ArrayList<>();
        dependencies.add(dependency("MCP Server 进程 / 地址", isReady(),
                "需要可访问的 MCP Server（stdio 命令或 http(s) 地址）"));
        dependencies.add(dependency("AgentScope MCP SDK（io.modelcontextprotocol.sdk）", true,
                "已随 agentscope-core 传递依赖引入，无需额外加 jar"));
        dependencies.add(dependency("共享 Toolkit（businessToolkit）", true,
                "MCP 工具注册后对所有 Agent 可见"));
        detail.put("dependencies", dependencies);

        detail.put("configuredServers", properties.getMcp().serverNames());
        detail.put("servers", serverViews());
        detail.put("mcpTools", listTools());
        detail.put("registeredCount", registeredCount());
        detail.put("missing", missingItems());
        detail.put("configExample", YAML_EXAMPLE);
        detail.put("notes", List.of(
                "MCP 工具通过 Toolkit.registerMcpClient(McpClientWrapper) 注册，框架内部会先 initialize() 再 listTools()",
                "启动时不会主动连接，只有调用 POST /agentscope/mcp/reload 时才会真正握手",
                "关闭开关：POST /agentscope/mcp/disable（内存生效，重启后回到配置值）"));
        return detail;
    }

    // ------------------------------------------------------------------ 内部方法

    /**
     * 按配置刷新"已配置待连接"状态（不发起连接）。
     */
    private void refreshConfiguredStates() {
        List<String> names = properties.getMcp().serverNames();
        for (String name : names) {
            McpServerState state = states.computeIfAbsent(name, McpServerState::new);
            AgentScopePropertiesConfig.McpServerConfig config = findClientConfig(name);
            if (config == null) {
                if (!McpServerState.STATUS_REGISTERED.equals(state.getStatus())
                        && !McpServerState.STATUS_FAILED.equals(state.getStatus())) {
                    state.setStatus(McpServerState.STATUS_NOT_CONFIGURED);
                    state.setMessage("缺少 agentscope.mcp.clients 中 name=" + name + " 的连接配置");
                }
                continue;
            }
            state.setType(config.getType());
            state.setTarget(describeTarget(config));
            if (!McpServerState.STATUS_REGISTERED.equals(state.getStatus())
                    && !McpServerState.STATUS_FAILED.equals(state.getStatus())
                    && !McpServerState.STATUS_CONNECTING.equals(state.getStatus())) {
                state.setStatus(McpServerState.STATUS_CONFIGURED);
                state.setMessage("已配置，尚未连接（调用 POST /agentscope/mcp/reload 连接）");
            }
        }
    }

    private AgentScopePropertiesConfig.McpServerConfig findClientConfig(String name) {
        List<AgentScopePropertiesConfig.McpServerConfig> clients = properties.getMcp().getClients();
        if (clients == null) {
            return null;
        }
        for (AgentScopePropertiesConfig.McpServerConfig config : clients) {
            if (name.equals(config.getName())) {
                return config;
            }
        }
        return null;
    }

    /**
     * 构建 MCP 客户端（不同传输方式）。
     */
    private McpClientWrapper buildClient(AgentScopePropertiesConfig.McpServerConfig config) {
        String type = config.getType() == null ? "stdio" : config.getType().trim().toLowerCase();
        McpClientBuilder builder = McpClientBuilder.create(config.getName());
        switch (type) {
            case "stdio" -> {
                if (config.getCommand() == null || config.getCommand().isBlank()) {
                    throw new IllegalArgumentException("stdio 类型必须配置 command");
                }
                String[] args = config.getArgs() == null
                        ? new String[0]
                        : config.getArgs().toArray(new String[0]);
                builder.stdioTransport(config.getCommand(), args);
            }
            case "sse" -> {
                requireUrl(config);
                builder.sseTransport(config.getUrl());
            }
            case "streamable-http", "http", "streamablehttp" -> {
                requireUrl(config);
                builder.streamableHttpTransport(config.getUrl());
            }
            default -> throw new IllegalArgumentException("不支持的 MCP 传输类型：" + config.getType());
        }
        builder.timeout(Duration.ofSeconds(timeoutSeconds()));
        builder.initializationTimeout(Duration.ofSeconds(timeoutSeconds()));
        return builder.buildSync();
    }

    private void requireUrl(AgentScopePropertiesConfig.McpServerConfig config) {
        if (config.getUrl() == null || config.getUrl().isBlank()) {
            throw new IllegalArgumentException("http/sse 类型必须配置 url");
        }
    }

    private String describeTarget(AgentScopePropertiesConfig.McpServerConfig config) {
        String type = config.getType() == null ? "stdio" : config.getType();
        if ("stdio".equalsIgnoreCase(type)) {
            StringBuilder sb = new StringBuilder(String.valueOf(config.getCommand()));
            if (config.getArgs() != null) {
                for (String arg : config.getArgs()) {
                    sb.append(' ').append(arg);
                }
            }
            return sb.toString();
        }
        return String.valueOf(config.getUrl());
    }

    private int timeoutSeconds() {
        int timeout = properties.getMcp().getTimeoutSeconds();
        return timeout > 0 ? timeout : 20;
    }

    private int registeredCount() {
        int count = 0;
        for (McpServerState state : states.values()) {
            if (McpServerState.STATUS_REGISTERED.equals(state.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private List<String> missingItems() {
        List<String> missing = new ArrayList<>();
        if (!isEnabled()) {
            missing.add("开关未打开：agentscope.mcp.enabled=false");
        }
        List<String> names = properties.getMcp().serverNames();
        if (names.isEmpty()) {
            missing.add("未配置 Server：agentscope.mcp.servers 为空");
        }
        for (String name : names) {
            if (findClientConfig(name) == null) {
                missing.add("缺少 Server 连接配置：agentscope.mcp.clients 中没有 name=" + name);
            }
        }
        if (names.isEmpty() || registeredCount() == 0) {
            missing.add("尚未建立连接：调用 POST /agentscope/mcp/reload");
        }
        return missing;
    }

    private List<Map<String, Object>> serverViews() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<String> names = properties.getMcp().serverNames();
        for (String name : names) {
            McpServerState state = states.computeIfAbsent(name, McpServerState::new);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("server", name);
            item.put("type", state.getType());
            item.put("target", state.getTarget());
            item.put("status", state.getStatus());
            item.put("statusLabel", McpServerState.statusLabel(state.getStatus()));
            item.put("message", state.getMessage());
            item.put("tools", state.getToolNames());
            item.put("registeredAt", state.getRegisteredAt());
            item.put("elapsedMs", state.getElapsedMs());
            list.add(item);
        }
        return list;
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

    /**
     * 单个 MCP Server 的运行状态。
     */
    public static class McpServerState {

        /** 未配置 */
        public static final String STATUS_NOT_CONFIGURED = "NOT_CONFIGURED";

        /** 已配置待连接 */
        public static final String STATUS_CONFIGURED = "CONFIGURED";

        /** 连接中 */
        public static final String STATUS_CONNECTING = "CONNECTING";

        /** 已注册（工具已挂到 Toolkit） */
        public static final String STATUS_REGISTERED = "REGISTERED";

        /** 连接失败 */
        public static final String STATUS_FAILED = "FAILED";

        private final String name;

        private String type;

        private String target;

        private String status = STATUS_NOT_CONFIGURED;

        private String message = "未配置";

        private List<String> toolNames = new ArrayList<>();

        private String registeredAt;

        private long elapsedMs;

        public McpServerState(String name) {
            this.name = name;
        }

        public static String statusLabel(String status) {
            if (status == null) {
                return "未知";
            }
            return switch (status) {
                case STATUS_NOT_CONFIGURED -> "未配置";
                case STATUS_CONFIGURED -> "已配置待连接";
                case STATUS_CONNECTING -> "连接中";
                case STATUS_REGISTERED -> "已注册";
                case STATUS_FAILED -> "连接失败";
                default -> status;
            };
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<String> getToolNames() {
            return toolNames;
        }

        public void setToolNames(List<String> toolNames) {
            this.toolNames = toolNames;
        }

        public String getRegisteredAt() {
            return registeredAt;
        }

        public void setRegisteredAt(String registeredAt) {
            this.registeredAt = registeredAt;
        }

        public long getElapsedMs() {
            return elapsedMs;
        }

        public void setElapsedMs(long elapsedMs) {
            this.elapsedMs = elapsedMs;
        }
    }
}
