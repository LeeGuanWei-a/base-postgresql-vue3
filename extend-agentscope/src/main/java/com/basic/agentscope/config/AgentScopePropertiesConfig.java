package com.basic.agentscope.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AgentScope 业务侧配置 —— 外部依赖型能力的开关与参数。
 *
 * <p>前缀统一为 {@code agentscope}，分组为 {@code mcp / channel / sandbox / scheduler}，
 * 因此 yml 中的配置键形如：</p>
 * <pre>
 * agentscope:
 *   mcp:
 *     enabled: false
 *     servers: demo-filesystem
 * </pre>
 *
 * <p>⚠️ 本类只承载<b>业务扩展</b>配置；starter 自带的 {@code agentscope.agent.*} /
 * {@code agentscope.model.*} 仍由 {@code io.agentscope.spring.boot.properties.AgentscopeProperties}
 * 负责，两者分组互不重叠。</p>
 *
 * <p>所有外部依赖型能力默认 {@code enabled=false}：依赖未就绪时不装配 Agent、不发起外部连接。</p>
 */
@Component
@ConfigurationProperties(prefix = "agentscope")
public class AgentScopePropertiesConfig {

    /** MCP 客户端配置 */
    private Mcp mcp = new Mcp();

    /** 渠道接入配置 */
    private Channel channel = new Channel();

    /** 沙箱执行配置 */
    private Sandbox sandbox = new Sandbox();

    /** 调度器配置 */
    private Scheduler scheduler = new Scheduler();

    public Mcp getMcp() {
        return mcp;
    }

    public void setMcp(Mcp mcp) {
        this.mcp = mcp;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Sandbox getSandbox() {
        return sandbox;
    }

    public void setSandbox(Sandbox sandbox) {
        this.sandbox = sandbox;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    // ------------------------------------------------------------------ MCP

    /**
     * MCP 客户端配置。
     */
    public static class Mcp {

        /** 总开关：默认关闭（本机没有 MCP Server） */
        private boolean enabled = false;

        /** 需要启用的 MCP Server 名称，逗号分隔；名称需与 {@code clients[].name} 对应 */
        private String servers = "";

        /** MCP Server 详细配置（地址 / 启动命令） */
        private List<McpServerConfig> clients = new ArrayList<>();

        /** 单个 Server 的连接 / 列举工具超时（秒） */
        private int timeoutSeconds = 20;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getServers() {
            return servers;
        }

        public void setServers(String servers) {
            this.servers = servers;
        }

        public List<McpServerConfig> getClients() {
            return clients;
        }

        public void setClients(List<McpServerConfig> clients) {
            this.clients = clients;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        /**
         * 逗号分隔的 Server 名称列表。
         */
        public List<String> serverNames() {
            List<String> names = new ArrayList<>();
            if (servers == null || servers.isBlank()) {
                return names;
            }
            for (String item : servers.split(",")) {
                String name = item.trim();
                if (!name.isEmpty() && !names.contains(name)) {
                    names.add(name);
                }
            }
            return names;
        }
    }

    /**
     * 单个 MCP Server 的连接配置。
     */
    public static class McpServerConfig {

        /** Server 名称（与 {@code agentscope.mcp.servers} 中的名称对应） */
        private String name;

        /** 传输方式：{@code stdio} / {@code sse} / {@code streamable-http} */
        private String type = "stdio";

        /** stdio 方式的可执行命令，例如 {@code npx} */
        private String command;

        /** stdio 方式的参数 */
        private List<String> args = new ArrayList<>();

        /** sse / streamable-http 方式的地址 */
        private String url;

        /** 备注说明 */
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public List<String> getArgs() {
            return args;
        }

        public void setArgs(List<String> args) {
            this.args = args;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    // ------------------------------------------------------------------ 渠道

    /**
     * 渠道接入配置。
     */
    public static class Channel {

        /** 总开关：默认关闭（真实 IM 接入需要扩展包 + 凭证） */
        private boolean enabled = false;

        /** 渠道默认会话 ID（未显式指定 sessionId 时使用） */
        private String defaultSessionId = "channel-default";

        /** 单次消息调用 Agent 的超时（秒） */
        private int timeoutSeconds = 120;

        /** 预置渠道绑定（启动时自动注册） */
        private List<ChannelConfig> channels = new ArrayList<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getDefaultSessionId() {
            return defaultSessionId;
        }

        public void setDefaultSessionId(String defaultSessionId) {
            this.defaultSessionId = defaultSessionId;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        public List<ChannelConfig> getChannels() {
            return channels;
        }

        public void setChannels(List<ChannelConfig> channels) {
            this.channels = channels;
        }
    }

    /**
     * 单个渠道绑定配置。
     */
    public static class ChannelConfig {

        /** 渠道类型，如 {@code wecom} / {@code dingtalk} / {@code feishu} */
        private String type;

        /** 该渠道绑定的会话 ID */
        private String sessionId;

        /** 备注说明 */
        private String description;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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
    }

    // ------------------------------------------------------------------ 沙箱

    /**
     * 沙箱执行配置。
     */
    public static class Sandbox {

        /** 总开关：默认关闭 */
        private boolean enabled = false;

        /** 白名单命令（只读命令），按前缀匹配 */
        private List<String> allowedCommands = new ArrayList<>(Arrays.asList("java -version", "echo"));

        /** 单条命令执行超时（秒） */
        private int timeoutSeconds = 10;

        /** 隔离方式：{@code local-restricted}（本地受限）/ {@code container}（已接入容器） */
        private String isolation = "local-restricted";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getAllowedCommands() {
            return allowedCommands;
        }

        public void setAllowedCommands(List<String> allowedCommands) {
            this.allowedCommands = allowedCommands;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        public String getIsolation() {
            return isolation;
        }

        public void setIsolation(String isolation) {
            this.isolation = isolation;
        }
    }

    // ------------------------------------------------------------------ 调度器

    /**
     * 调度器配置。
     */
    public static class Scheduler {

        /** 总开关：默认关闭 */
        private boolean enabled = false;

        /** 调度时区 */
        private String timezone = "Asia/Shanghai";

        /** 单次任务执行超时（秒） */
        private int timeoutSeconds = 120;

        /** 预置调度任务 */
        private List<SchedulerTaskConfig> tasks = new ArrayList<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        public List<SchedulerTaskConfig> getTasks() {
            return tasks;
        }

        public void setTasks(List<SchedulerTaskConfig> tasks) {
            this.tasks = tasks;
        }
    }

    /**
     * 单个调度任务配置。
     */
    public static class SchedulerTaskConfig {

        /** 任务名（唯一） */
        private String name;

        /** cron 表达式，支持 {@code 秒 分 时 日 月 周} 六段或 {@code 分 时 日 月 周} 五段 */
        private String cron;

        /** 触发时交给 Agent 的 prompt */
        private String prompt;

        /** 任务使用的会话 ID */
        private String sessionId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

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
    }
}
