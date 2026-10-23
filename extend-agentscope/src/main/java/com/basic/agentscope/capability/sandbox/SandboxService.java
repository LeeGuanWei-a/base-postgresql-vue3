package com.basic.agentscope.capability.sandbox;

import com.basic.agentscope.config.AgentScopePropertiesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 沙箱执行服务 —— 受限的命令执行骨架。
 *
 * <p><b>安全第一</b>：本实现<b>不会</b>执行任意系统命令，只允许白名单里的只读命令
 * （默认 {@code java -version} 与 {@code echo}），并且：</p>
 * <ul>
 *   <li>拒绝含 Shell 元字符（{@code ; & | > < ` $} 与换行）的命令；</li>
 *   <li>用 {@link ProcessBuilder} 直接启动进程（<b>不经过 shell</b>），参数按空白切分；</li>
 *   <li>带超时（默认 10 秒），超时强制销毁进程。</li>
 * </ul>
 *
 * <p><b>生产环境</b>：应接入 {@code agentscope-extensions-sandbox-*} 扩展包，
 * 由容器 / K8s / firecracker 做真正的隔离执行，本类只作为协议与策略说明的骨架。</p>
 */
@Service
public class SandboxService implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);

    /** 禁止出现的 Shell 元字符 */
    private static final char[] FORBIDDEN_CHARS = {';', '&', '|', '>', '<', '`', '$', '\n', '\r'};

    /** 可直接复制到 application.yml 的配置示例 */
    private static final String YAML_EXAMPLE = """
            agentscope:
              sandbox:
                # 总开关：true 才允许执行白名单命令
                enabled: true
                # 白名单命令（只读），按前缀匹配
                allowed-commands:
                  - java -version
                  - echo
                # 单条命令执行超时（秒）
                timeout-seconds: 10
                # 隔离方式：local-restricted（本地受限）/ container（已接入容器）
                isolation: local-restricted
            """;

    private final AgentScopePropertiesConfig properties;

    /** 运行时开关覆盖：null = 跟随配置 */
    private volatile Boolean runtimeEnabled;

    public SandboxService(AgentScopePropertiesConfig properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        log.info("沙箱执行能力初始化完成，白名单命令：{}", allowedCommands());
    }

    // ------------------------------------------------------------------ 开关

    public boolean isEnabled() {
        Boolean override = runtimeEnabled;
        if (override != null) {
            return override;
        }
        return properties.getSandbox() != null && properties.getSandbox().isEnabled();
    }

    public void enable() {
        runtimeEnabled = Boolean.TRUE;
    }

    public void disable() {
        runtimeEnabled = Boolean.FALSE;
    }

    /**
     * 依赖是否就绪：本地受限模式可用（开关打开）。
     */
    public boolean isReady() {
        return isEnabled();
    }

    public Map<String, Object> switchInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("enabled", isEnabled());
        info.put("source", runtimeEnabled == null ? "配置文件 agentscope.sandbox.enabled" : "运行时内存开关");
        info.put("persistent", runtimeEnabled == null);
        info.put("note", runtimeEnabled == null
                ? "当前由配置项 agentscope.sandbox.enabled 控制，重启后保持不变"
                : "当前为运行时内存开关，重启后恢复为配置项 agentscope.sandbox.enabled 的取值");
        return info;
    }

    // ------------------------------------------------------------------ 执行

    /**
     * 执行一条命令（受限）。
     *
     * @return 执行结果（包含是否放行、退出码、标准输出 / 错误、拒绝原因）
     */
    public Map<String, Object> execute(String command) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("command", command);
        result.put("isolation", isolation());
        if (!isEnabled()) {
            result.put("allowed", false);
            result.put("reason", "沙箱能力未启用：请设置 agentscope.sandbox.enabled=true 或调用 POST /agentscope/sandbox/enable");
            return result;
        }
        if (command == null || command.isBlank()) {
            result.put("allowed", false);
            result.put("reason", "command 不能为空");
            return result;
        }
        for (char c : FORBIDDEN_CHARS) {
            if (command.indexOf(c) >= 0) {
                result.put("allowed", false);
                result.put("reason", "命令包含被禁止的 Shell 元字符 '" + c + "'，沙箱只接受纯参数化命令");
                return result;
            }
        }
        List<String> tokens = tokenize(command);
        if (tokens.isEmpty()) {
            result.put("allowed", false);
            result.put("reason", "命令解析后为空");
            return result;
        }
        String rule = matchRule(tokens);
        if (rule == null) {
            result.put("allowed", false);
            result.put("reason", "命令不在白名单内，仅允许：" + String.join("、", allowedCommands())
                    + "；生产环境应接入 agentscope-extensions-sandbox-* 由容器/K8s 隔离执行");
            return result;
        }
        result.put("allowed", true);
        result.put("matchedRule", rule);

        long start = System.currentTimeMillis();
        Process process = null;
        try {
            List<String> launch = adaptCommand(tokens);
            ProcessBuilder builder = new ProcessBuilder(launch);
            builder.redirectErrorStream(true);
            process = builder.start();
            Process running = process;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Thread reader = new Thread(() -> {
                try (InputStream in = running.getInputStream()) {
                    in.transferTo(buffer);
                } catch (Exception ignored) {
                    // 进程结束后读取失败可忽略
                }
            }, "sandbox-reader");
            reader.setDaemon(true);
            reader.start();

            boolean finished = process.waitFor(timeoutSeconds(), TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                result.put("exitCode", -1);
                result.put("stdout", "");
                result.put("stderr", "");
                result.put("reason", "命令执行超时（" + timeoutSeconds() + " 秒）已被强制终止");
                result.put("elapsedMs", System.currentTimeMillis() - start);
                return result;
            }
            reader.join(500);
            result.put("exitCode", process.exitValue());
            result.put("stdout", decode(buffer.toByteArray()));
            result.put("stderr", "");
            result.put("elapsedMs", System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            log.warn("沙箱命令执行失败：{}", command, e);
            result.put("exitCode", -1);
            result.put("stdout", "");
            result.put("stderr", rootMessage(e));
            result.put("reason", "命令执行失败：" + rootMessage(e));
            result.put("elapsedMs", System.currentTimeMillis() - start);
            return result;
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    // ------------------------------------------------------------------ 状态

    /**
     * 重新加载白名单配置（不执行任何命令）。
     */
    public Map<String, Object> reload() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("message", "已重新加载白名单配置，共 " + allowedCommands().size() + " 条");
        result.put("allowedCommands", allowedCommands());
        result.put("isolation", isolation());
        return result;
    }

    /**
     * 状态面板数据（与能力 detail() 一致）。
     */
    public Map<String, Object> status() {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("capability", "sandbox");
        detail.put("capabilityName", "沙箱执行");
        detail.put("ready", isReady());
        detail.put("switch", switchInfo());

        List<Map<String, Object>> dependencies = new ArrayList<>();
        dependencies.add(dependency("本地受限执行（白名单 + 超时）", isEnabled(),
                "无需 Docker 即可跑通，但隔离级别低，仅限只读命令"));
        dependencies.add(dependency("agentscope-extensions-sandbox-* 扩展包", false,
                "提供容器 / K8s 级别的真沙箱，当前分支未引入"));
        dependencies.add(dependency("Docker / K8s 运行时", false,
                "真沙箱需要容器运行时，当前机器不可用"));
        detail.put("dependencies", dependencies);

        detail.put("isolation", isolation());
        detail.put("isolationLabel", "本地受限（白名单只读命令 + 超时强制终止）");
        detail.put("allowedCommands", allowedCommands());
        detail.put("executeEndpoint", "POST /agentscope/sandbox/execute");
        detail.put("missing", missingItems());
        detail.put("configExample", YAML_EXAMPLE);
        detail.put("notes", List.of(
                "出于安全考虑，本实现只放行白名单中的只读命令，其余一律拒绝并说明原因",
                "命令通过 ProcessBuilder 直接启动，不经过 shell，因此不支持管道 / 重定向 / 命令替换",
                "生产环境应接入 agentscope-extensions-sandbox-* 扩展包，由容器/K8s 隔离执行任意代码"));
        return detail;
    }

    // ------------------------------------------------------------------ 内部方法

    /**
     * 白名单命令列表。
     */
    public List<String> allowedCommands() {
        List<String> commands = properties.getSandbox().getAllowedCommands();
        return commands == null ? new ArrayList<>() : new ArrayList<>(commands);
    }

    /**
     * 当前隔离方式。
     */
    public String isolation() {
        String isolation = properties.getSandbox().getIsolation();
        return isolation == null || isolation.isBlank() ? "local-restricted" : isolation;
    }

    /**
     * 命中的白名单规则（未命中返回 null）。
     */
    private String matchRule(List<String> tokens) {
        for (String rule : allowedCommands()) {
            if (rule == null || rule.isBlank()) {
                continue;
            }
            List<String> ruleTokens = tokenize(rule.trim());
            if (ruleTokens.isEmpty() || ruleTokens.size() > tokens.size()) {
                continue;
            }
            boolean matched = true;
            for (int i = 0; i < ruleTokens.size(); i++) {
                if (!ruleTokens.get(i).equalsIgnoreCase(tokens.get(i))) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                return rule.trim();
            }
        }
        return null;
    }

    /**
     * 平台适配：Windows 下 {@code echo} 是 cmd 内建命令，不是可执行文件，
     * 这里转换成 {@code cmd /c echo}；其余命令原样执行。
     */
    private List<String> adaptCommand(List<String> tokens) {
        if (!tokens.isEmpty() && "echo".equalsIgnoreCase(tokens.get(0))
                && System.getProperty("os.name", "").toLowerCase().contains("windows")) {
            List<String> launch = new ArrayList<>();
            launch.add("cmd");
            launch.add("/c");
            launch.addAll(tokens);
            return launch;
        }
        return tokens;
    }

    /**
     * 简单分词：支持双引号 / 单引号包裹，不做变量展开。
     */
    private List<String> tokenize(String command) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuote = false;
        char quote = 0;
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            if (inQuote) {
                if (c == quote) {
                    inQuote = false;
                } else {
                    current.append(c);
                }
            } else if (c == '"' || c == '\'') {
                inQuote = true;
                quote = c;
            } else if (Character.isWhitespace(c)) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            tokens.add(current.toString());
        }
        return tokens;
    }

    /**
     * 解码进程输出：优先 UTF-8，出现乱码时回退 GBK（Windows 控制台默认编码）。
     */
    private String decode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        String text = new String(bytes, StandardCharsets.UTF_8);
        if (text.indexOf('\uFFFD') >= 0) {
            try {
                text = new String(bytes, Charset.forName("GBK"));
            } catch (Exception ignored) {
                // 保持 UTF-8 解码结果
            }
        }
        return text;
    }

    private int timeoutSeconds() {
        int timeout = properties.getSandbox().getTimeoutSeconds();
        return timeout > 0 ? timeout : 10;
    }

    private List<String> missingItems() {
        List<String> missing = new ArrayList<>();
        if (!isEnabled()) {
            missing.add("开关未打开：agentscope.sandbox.enabled=false");
        }
        if (allowedCommands().isEmpty()) {
            missing.add("白名单为空：agentscope.sandbox.allowed-commands");
        }
        missing.add("未接入容器隔离：需要 agentscope-extensions-sandbox-* 扩展包 + Docker/K8s 运行时");
        return missing;
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
