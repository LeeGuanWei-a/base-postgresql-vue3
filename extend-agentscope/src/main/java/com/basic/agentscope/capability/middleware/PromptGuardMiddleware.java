package com.basic.agentscope.capability.middleware;

import io.agentscope.core.agent.Agent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.middleware.MiddlewareBase;
import io.agentscope.core.middleware.ModelCallInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * 提示词守卫 Middleware —— 在 AgentScope 的<b>调用链</b>上做拦截与增强。
 *
 * <p>与 {@code Hook} 的区别：</p>
 * <ul>
 *   <li>{@code Hook} 是<b>旁路</b>观察，拿到事件但不影响流程；</li>
 *   <li>{@code Middleware} 是<b>串联</b>在链路上，可以<b>改写输入</b>（{@code onSystemPrompt}）
 *       或<b>包裹调用</b>（{@code onModelCall} / {@code onReasoning} / {@code onActing}）。</li>
 * </ul>
 *
 * <p>本实现做两件事：</p>
 * <ol>
 *   <li>{@code onSystemPrompt}：给系统提示词追加"当前时间 / 会话 ID / 用户 ID"等动态上下文，
 *       让模型知道"现在是什么时候、这是哪个会话"；</li>
 *   <li>{@code onModelCall}：统计模型调用次数与每次调用的耗时（成功、失败都记）。</li>
 * </ol>
 *
 * <p>⚠️ 本类是<b>普通对象</b>，由能力类自己持有，<b>不注册为 Spring Bean</b>。</p>
 */
public class PromptGuardMiddleware implements MiddlewareBase {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 模型调用次数 */
    private final AtomicLong modelCallCount = new AtomicLong();

    /** 所有模型调用的累计耗时（毫秒） */
    private final AtomicLong totalElapsedMs = new AtomicLong();

    /** 单次模型调用的最大耗时（毫秒） */
    private final AtomicLong maxElapsedMs = new AtomicLong();

    /** 最后一次拼装后的系统提示词 */
    private volatile String lastSystemPrompt = "";

    /** 最后一次调用使用的模型名 */
    private volatile String lastModelName = "";

    /**
     * 改写系统提示词：追加运行时上下文。
     */
    @Override
    public Mono<String> onSystemPrompt(Agent agent, RuntimeContext ctx, String prompt) {
        String base = prompt == null ? "" : prompt;
        String enhanced = base + System.lineSeparator()
                + "[运行时上下文] 当前时间：" + LocalDateTime.now().format(TIME_FORMATTER)
                + "；会话 ID：" + safe(ctx == null ? null : ctx.getSessionId())
                + "；用户 ID：" + safe(ctx == null ? null : ctx.getUserId())
                + "。请基于以上真实时间与会话信息作答。";
        this.lastSystemPrompt = enhanced;
        return Mono.just(enhanced);
    }

    /**
     * 包裹模型调用：统计次数与耗时。
     */
    @Override
    public Flux<AgentEvent> onModelCall(Agent agent,
                                        RuntimeContext ctx,
                                        ModelCallInput input,
                                        Function<ModelCallInput, Flux<AgentEvent>> next) {
        long start = System.currentTimeMillis();
        modelCallCount.incrementAndGet();
        if (input != null && input.model() != null) {
            this.lastModelName = input.model().getModelName();
        }
        return next.apply(input)
                .doOnComplete(() -> recordElapsed(System.currentTimeMillis() - start))
                .doOnError(e -> recordElapsed(System.currentTimeMillis() - start));
    }

    private void recordElapsed(long elapsed) {
        totalElapsedMs.addAndGet(elapsed);
        maxElapsedMs.accumulateAndGet(elapsed, Math::max);
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    // ------------------------------------------------------------------ 对外查询

    /**
     * 模型调用次数。
     */
    public long modelCallCount() {
        return modelCallCount.get();
    }

    /**
     * 最后一次拼装后的系统提示词。
     */
    public String lastSystemPrompt() {
        return lastSystemPrompt;
    }

    /**
     * 平均耗时（毫秒）；未调用过返回 0。
     */
    public long averageElapsedMs() {
        long count = modelCallCount.get();
        return count == 0 ? 0L : totalElapsedMs.get() / count;
    }

    /**
     * 累计耗时（毫秒）。
     */
    public long totalElapsedMs() {
        return totalElapsedMs.get();
    }

    /**
     * 单次最大耗时（毫秒）。
     */
    public long maxElapsedMs() {
        return maxElapsedMs.get();
    }

    /**
     * 最后一次使用的模型名。
     */
    public String lastModelName() {
        return lastModelName;
    }
}
