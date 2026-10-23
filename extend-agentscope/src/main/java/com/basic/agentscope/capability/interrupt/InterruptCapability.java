package com.basic.agentscope.capability.interrupt;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.tool.Toolkit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 能力：<b>中断运行（Interrupt / Human-in-the-loop）</b>。
 *
 * <p>演示 AgentScope 的运行期中断能力：</p>
 * <ol>
 *   <li>Agent 调用期间，框架按 {@code (userId, sessionId)} 登记「运行槽位」；</li>
 *   <li>调用 {@code agent.interrupt(userId, sessionId)} 即可把该槽位里正在跑的调用打断；</li>
 *   <li>本类维护 {@code sessionId → Agent} 的映射以及运行状态、中断次数，
 *       供前端轮询展示。</li>
 * </ol>
 *
 * <p>为了能稳定地看到"运行中被打断"，该 Agent 注册了一个会阻塞几秒的慢任务工具。</p>
 */
@Configuration
public class InterruptCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "interruptCapabilityAgent";

    /** 演示用固定用户 ID（与通用控制器 RuntimeContext.userId 一致） */
    public static final String DEMO_USER_ID = "demo-user";

    /** 默认会话 ID */
    public static final String DEFAULT_SESSION_ID = "default";

    /** sessionId → 受控的 Agent（任务要求：key 是 sessionId） */
    private final ConcurrentHashMap<String, ReActAgent> sessionAgents = new ConcurrentHashMap<>();

    /** sessionId → 是否正在运行 */
    private final ConcurrentHashMap<String, Boolean> runningFlags = new ConcurrentHashMap<>();

    /** sessionId → 中断次数 */
    private final ConcurrentHashMap<String, AtomicInteger> interruptCounts = new ConcurrentHashMap<>();

    /**
     * sessionId → 最近一次运行结果。
     *
     * <p>⚠️ {@code ConcurrentHashMap} 不允许 null 值，而"成功但文本为空"与"失败无文本"
     * 都会产生 null，因此这里统一用 {@link RunOutcome} 包装 —— 元素本身永不为 null，
     * 而"从未运行过"用 <b>没有这个 key</b> 来表达。</p>
     */
    private final ConcurrentHashMap<String, RunOutcome> lastOutcomes = new ConcurrentHashMap<>();

    /** 慢任务工具（可调耗时） */
    private InterruptTools interruptTools;

    @Bean(AGENT_BEAN)
    public ReActAgent interruptCapabilityAgent(ModelResolver modelResolver) {
        this.interruptTools = new InterruptTools();

        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(this.interruptTools);

        return ReActAgent.builder()
                .name("interrupt-agent")
                .description("可被中断的智能体：运行中的任务可随时被打断")
                .sysPrompt("""
                        你是企业级 Java 基础平台（RuoYi 风格）的巡检助手。
                        遇到『全量扫描』『跑一遍全量统计』这类耗时任务时，调用 slow_platform_scan 工具。
                        该工具执行期间用户可能随时中断任务，被中断时如实说明"任务已被中断"。
                        回答用中文。
                        """)
                .model(modelResolver.resolve())
                .toolkit(toolkit)
                .defaultSessionId(DEFAULT_SESSION_ID)
                .maxIters(5)
                .build();
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "interrupt",
                "中断运行",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "运行中断：可随时打断正在执行的 Agent 任务，实时查看运行状态与中断次数",
                "VideoPause",
                null,
                true,
                true,
                "/agentscope/interrupt/chat",
                "/agentscope/capability/interrupt/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "请做一次全量扫描"),
                        CapabilityInfo.ParamInfo.text("sessionId", "会话 ID（用于中断定位）", "default")
                ));
    }

    @Override
    public String agentBeanName() {
        return AGENT_BEAN;
    }

    @Override
    public Map<String, Object> detail() {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("agentBean", AGENT_BEAN);
        detail.put("defaultSessionId", DEFAULT_SESSION_ID);
        detail.put("currentUserId", DEMO_USER_ID);
        detail.put("slowTaskSeconds", interruptTools == null ? 5 : interruptTools.getDelaySeconds());

        List<Map<String, Object>> sessions = new ArrayList<>();
        int totalInterrupts = 0;
        for (Map.Entry<String, ReActAgent> entry : sessionAgents.entrySet()) {
            String sessionId = entry.getKey();
            AtomicInteger counter = interruptCounts.get(sessionId);
            int count = counter == null ? 0 : counter.get();
            totalInterrupts += count;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("sessionId", sessionId);
            item.put("agentName", entry.getValue() == null ? "-" : entry.getValue().getName());
            item.put("running", isRunning(sessionId));
            item.put("interruptCount", count);
            item.put("lastResult", lastResult(sessionId));
            item.put("lastError", lastError(sessionId));
            sessions.add(item);
        }
        detail.put("sessionCount", sessions.size());
        detail.put("totalInterruptCount", totalInterrupts);
        detail.put("sessions", sessions);
        return detail;
    }

    // ------------------------------------------------------------------ 运行期状态维护

    /**
     * 登记某个会话使用的 Agent。
     */
    public void registerSession(String sessionId, ReActAgent agent) {
        if (sessionId == null || sessionId.isBlank() || agent == null) {
            return;
        }
        sessionAgents.put(sessionId, agent);
        interruptCounts.computeIfAbsent(sessionId, k -> new AtomicInteger());
    }

    /**
     * 标记某个会话是否正在运行。
     */
    public void markRunning(String sessionId, boolean running) {
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }
        runningFlags.put(sessionId, running);
    }

    /**
     * 某个会话当前是否正在运行。
     */
    public boolean isRunning(String sessionId) {
        return Boolean.TRUE.equals(runningFlags.get(sessionId));
    }

    /**
     * 记录最近一次运行结果。
     */
    public void recordResult(String sessionId, String text, String error) {
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }
        lastOutcomes.put(sessionId, new RunOutcome(text, error));
    }

    /**
     * 某个会话的中断次数。
     */
    public int interruptCount(String sessionId) {
        AtomicInteger counter = interruptCounts.get(sessionId);
        return counter == null ? 0 : counter.get();
    }

    /**
     * 某个会话最近一次运行的文本结果（从未运行过时返回 null）。
     */
    public String lastResult(String sessionId) {
        RunOutcome outcome = lastOutcomes.get(sessionId);
        return outcome == null ? null : outcome.text();
    }

    /**
     * 某个会话最近一次运行的错误信息（从未运行过或成功时返回 null）。
     */
    public String lastError(String sessionId) {
        RunOutcome outcome = lastOutcomes.get(sessionId);
        return outcome == null ? null : outcome.error();
    }

    /**
     * 打断某个会话正在运行的调用。
     *
     * @param sessionId 会话 ID
     * @return 是否成功（会话已登记 → 触发中断）
     */
    public boolean interrupt(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return false;
        }
        ReActAgent agent = sessionAgents.get(sessionId);
        if (agent == null) {
            return false;
        }
        interruptCounts.computeIfAbsent(sessionId, k -> new AtomicInteger()).incrementAndGet();
        // ⚠️ AgentScope 的签名是 (userId, sessionId)，不是 (sessionId, userId)
        agent.interrupt(DEMO_USER_ID, sessionId);
        return true;
    }

    /**
     * 取某个会话的 Agent。
     */
    public ReActAgent agentOf(String sessionId) {
        return sessionAgents.get(sessionId);
    }

    /**
     * 慢任务工具（供控制器调整耗时）。
     */
    public InterruptTools interruptTools() {
        return interruptTools;
    }

    /**
     * 一次运行的结论：文本结果 + 错误信息。
     *
     * <p>两者允许为 null，但 <b>本对象本身永远不为 null</b>，因此可以安全地放进
     * {@link ConcurrentHashMap}（它不接受 null 值 / null 键）。</p>
     */
    public record RunOutcome(String text, String error) {
    }
}
