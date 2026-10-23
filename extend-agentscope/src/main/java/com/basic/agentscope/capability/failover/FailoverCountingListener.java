package com.basic.agentscope.capability.failover;

import io.agentscope.core.agent.config.FailoverListener;
import io.agentscope.core.model.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 故障转移监听器 —— 统计"主模型失败并切换到备用模型"的次数与明细。
 *
 * <p>AgentScope 在主模型抛异常、准备切换到 {@code fallbackModel} 之前会回调
 * {@link FailoverListener#onFailover(Model, Throwable)}，这里把每次故障转移记录下来，
 * 前端就能在能力详情里看到"已经故障转移了几次、每次是哪个模型、报的什么错"。</p>
 *
 * <p>⚠️ 本类是<b>普通对象</b>，由能力类自己持有，不注册为 Spring Bean。</p>
 */
public class FailoverCountingListener implements FailoverListener {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /** 累计故障转移次数 */
    private final AtomicInteger failoverCount = new AtomicInteger();

    /** 故障转移明细 */
    private final List<Map<String, Object>> events = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void onFailover(Model model, Throwable error) {
        int seq = failoverCount.incrementAndGet();
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("seq", seq);
        item.put("failedModel", model == null ? "-" : model.getModelName());
        item.put("error", error == null ? "-" : String.valueOf(error.getMessage()));
        item.put("errorType", error == null ? "-" : error.getClass().getSimpleName());
        item.put("time", LocalDateTime.now().format(TIME_FORMATTER));
        events.add(item);
    }

    /**
     * 累计故障转移次数。
     */
    public int failoverCount() {
        return failoverCount.get();
    }

    /**
     * 最近 N 条故障转移明细。
     */
    public List<Map<String, Object>> recentEvents(int limit) {
        List<Map<String, Object>> snapshot;
        synchronized (events) {
            snapshot = new ArrayList<>(events);
        }
        if (limit <= 0 || snapshot.size() <= limit) {
            return snapshot;
        }
        return new ArrayList<>(snapshot.subList(snapshot.size() - limit, snapshot.size()));
    }
}
