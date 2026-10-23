package com.basic.agentscope.capability.hook;

import io.agentscope.core.hook.Hook;
import io.agentscope.core.hook.HookEvent;
import io.agentscope.core.hook.HookEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 时序审计 Hook —— <b>旁路</b>监听 ReAct 循环中发生的每一个生命周期事件。
 *
 * <p>AgentScope 的 {@link Hook} 会在以下时机回调：</p>
 * <ul>
 *   <li>{@code PRE_CALL} / {@code POST_CALL}：一次用户输入的前后；</li>
 *   <li>{@code PRE_REASONING} / {@code POST_REASONING}：一次"模型思考"的前后；</li>
 *   <li>{@code PRE_ACTING} / {@code POST_ACTING}：一次"工具执行"的前后；</li>
 *   <li>{@code PRE_SUMMARY} / {@code POST_SUMMARY}：一次"总结"的前后；</li>
 *   <li>{@code ERROR}：链路抛异常时。</li>
 * </ul>
 *
 * <p>本 Hook <b>只观察不拦截</b>：原样把事件返回给框架，不改变任何执行流程，
 * 因此可以安全地挂在生产 Agent 上做审计（耗时、次数、失败定位）。</p>
 *
 * <p>记录内容：事件类型、时间戳、<b>耗时</b>（POST_* 与对应 PRE_* 配对相减）、所属 Agent。</p>
 *
 * <p>⚠️ 本类是<b>普通对象</b>，由能力类自己持有，<b>不注册为 Spring Bean</b>。</p>
 *
 * <p>ℹ️ AgentScope 2.0.4 把整个 {@code io.agentscope.core.hook} 包标记为
 * {@code @Deprecated(forRemoval = true)}（新链路推荐用 tracing / recorder），
 * 本能力按需求仍然演示 Hook 机制，故显式抑制该告警。</p>
 */
@SuppressWarnings("removal")
public class TimingAuditHook implements Hook {

    private static final Logger log = LoggerFactory.getLogger(TimingAuditHook.class);

    /** 内存中保留的最大事件条数（超出后丢弃最旧的，防止长时间运行内存膨胀） */
    public static final int MAX_RECORDS = 200;

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    /** 事件明细（事件类型 / 时间戳 / 耗时） */
    private final List<Map<String, Object>> records = Collections.synchronizedList(new ArrayList<>());

    /** PRE_* 事件的时间戳，用于和对应的 POST_* 配对算出耗时 */
    private final Map<String, Long> pendingPhases = new ConcurrentHashMap<>();

    /** 事件总数（不受 {@link #MAX_RECORDS} 限制） */
    private final AtomicLong totalEvents = new AtomicLong();

    /** 各事件类型的发生次数 */
    private final Map<String, AtomicLong> typeCounter = new ConcurrentHashMap<>();

    @Override
    public <T extends HookEvent> Mono<T> onEvent(T event) {
        if (event == null) {
            return Mono.empty();
        }
        try {
            record(event);
        } catch (Exception e) {
            // 审计逻辑绝不能影响主流程
            log.warn("[TimingAuditHook] 记录事件失败：{}", e.getMessage());
        }
        return Mono.just(event);
    }

    /**
     * 记录一条事件。
     */
    private void record(HookEvent event) {
        long seq = totalEvents.incrementAndGet();
        HookEventType type = event.getType();
        String typeName = type == null ? "UNKNOWN" : type.name();
        long rawTs = event.getTimestamp();
        long ts = normalizeTimestamp(rawTs);

        typeCounter.computeIfAbsent(typeName, k -> new AtomicLong()).incrementAndGet();

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("seq", seq);
        item.put("eventType", typeName);
        item.put("eventClass", event.getClass().getSimpleName());
        item.put("timestamp", rawTs);
        item.put("time", formatTime(ts));
        item.put("agent", agentName(event));
        item.put("durationMs", resolveDuration(type, ts));
        records.add(item);

        while (records.size() > MAX_RECORDS) {
            records.remove(0);
        }
    }

    /**
     * 配对 PRE_* / POST_* 事件，算出本阶段的耗时（毫秒）。
     *
     * @return 耗时；无法配对时返回 null
     */
    private Long resolveDuration(HookEventType type, long ts) {
        if (type == null) {
            return null;
        }
        String name = type.name();
        String phase = phaseOf(name);
        if (name.startsWith("PRE_")) {
            pendingPhases.put(phase, ts);
            return null;
        }
        if (name.startsWith("POST_")) {
            Long start = pendingPhases.remove(phase);
            return start == null ? null : Math.max(0L, ts - start);
        }
        return null;
    }

    /**
     * 把事件类型归到"阶段"：CALL / REASONING / ACTING / SUMMARY。
     */
    private String phaseOf(String eventTypeName) {
        if (eventTypeName.endsWith("CALL")) {
            return "CALL";
        }
        if (eventTypeName.endsWith("REASONING")) {
            return "REASONING";
        }
        if (eventTypeName.endsWith("ACTING")) {
            return "ACTING";
        }
        if (eventTypeName.endsWith("SUMMARY")) {
            return "SUMMARY";
        }
        return eventTypeName;
    }

    /**
     * 时间戳归一化到毫秒（框架可能是秒，也可能是毫秒）。
     */
    private long normalizeTimestamp(long raw) {
        if (raw <= 0) {
            return System.currentTimeMillis();
        }
        if (raw < 100_000_000_000L) {
            // 秒 → 毫秒
            return raw * 1000L;
        }
        return raw;
    }

    private String formatTime(long epochMilli) {
        try {
            return TIME_FORMATTER.format(Instant.ofEpochMilli(epochMilli));
        } catch (Exception e) {
            return String.valueOf(epochMilli);
        }
    }

    private String agentName(HookEvent event) {
        try {
            if (event.getAgent() == null) {
                return "-";
            }
            String name = event.getAgent().getName();
            return name == null || name.isBlank() ? "-" : name;
        } catch (Exception e) {
            return "-";
        }
    }

    // ------------------------------------------------------------------ 对外查询

    /**
     * 最近 N 条事件记录（新的在后）。
     */
    public List<Map<String, Object>> recent(int limit) {
        List<Map<String, Object>> snapshot;
        synchronized (records) {
            snapshot = new ArrayList<>(records);
        }
        if (limit <= 0 || snapshot.size() <= limit) {
            return snapshot;
        }
        return new ArrayList<>(snapshot.subList(snapshot.size() - limit, snapshot.size()));
    }

    /**
     * 事件总数。
     */
    public long totalEvents() {
        return totalEvents.get();
    }

    /**
     * 各事件类型的发生次数。
     */
    public Map<String, Long> stats() {
        Map<String, Long> result = new LinkedHashMap<>();
        typeCounter.forEach((k, v) -> result.put(k, v.get()));
        return result;
    }
}
