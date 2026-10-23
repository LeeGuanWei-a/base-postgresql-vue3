package com.basic.agentscope.capability.memory;

import io.agentscope.core.agent.AgentBase;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.hook.Hook;
import io.agentscope.core.hook.HookEvent;
import io.agentscope.core.hook.PostCallEvent;
import io.agentscope.core.hook.PreCallEvent;
import io.agentscope.core.message.Msg;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 长期记忆归档 Hook —— 每次对话结束后把「用户输入 + 最终回答」写进长期记忆。
 *
 * <h3>为什么需要它？</h3>
 * <p>AgentScope 自带的 {@code StaticLongTermMemoryHook} 在归档时读的是
 * {@code agent.getAgentState()}，也就是<b>默认会话槽位</b>（userId 为空 + defaultSessionId）；
 * 而实际调用发生在 {@code (userId, sessionId)} 指定的槽位上，
 * 于是归档时拿到的上下文永远是空的（框架自身的限制）。</p>
 *
 * <p>本 Hook 通过响应式上下文里的 {@link AgentBase#CALL_SCOPE_KEY} 定位「本次调用」，
 * 用 {@link AgentBase#RUNTIME_CONTEXT_KEY} 拿到 {@link RuntimeContext}，
 * 从而把正确的输入消息与最终回答配对后归档，且天然支持并发调用。</p>
 */
public class MemoryArchiveHook implements Hook {

    /** 长期记忆实现 */
    private final InMemoryLongTermMemory longTermMemory;

    /** callScope → 本次调用的输入消息 */
    private final Map<Object, List<Msg>> pendingInputs = new ConcurrentHashMap<>();

    public MemoryArchiveHook(InMemoryLongTermMemory longTermMemory) {
        this.longTermMemory = longTermMemory;
    }

    @Override
    public <T extends HookEvent> Mono<T> onEvent(T event) {
        return Mono.deferContextual(context -> {
            Object scope = context.getOrDefault(AgentBase.CALL_SCOPE_KEY, null);
            Object key = scope == null ? AgentBase.RUNTIME_CONTEXT_KEY : scope;
            if (event instanceof PreCallEvent pre) {
                pendingInputs.put(key, new ArrayList<>(pre.getInputMessages()));
            } else if (event instanceof PostCallEvent post) {
                List<Msg> inputs = pendingInputs.remove(key);
                if (inputs != null && !inputs.isEmpty()) {
                    List<Msg> archive = new ArrayList<>(inputs);
                    if (post.getFinalMessage() != null) {
                        archive.add(post.getFinalMessage());
                    }
                    longTermMemory.recordSync(archive);
                }
            }
            return Mono.just(event);
        });
    }

    @Override
    public int priority() {
        // 排在框架内置的 StaticLongTermMemoryHook（50）之后
        return 60;
    }
}
