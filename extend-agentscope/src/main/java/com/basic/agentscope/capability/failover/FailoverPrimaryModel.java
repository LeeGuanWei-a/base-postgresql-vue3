package com.basic.agentscope.capability.failover;

import io.agentscope.core.message.Msg;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.ModelException;
import io.agentscope.core.model.ToolSchema;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 主模型包装器 —— 在真实模型（来自 {@code ModelResolver#resolve()}）外面套一层"故障注入开关"。
 *
 * <p>为什么需要它？</p>
 * <ul>
 *   <li>默认的 Mock 模型<b>永远不会失败</b>，那样"主模型挂了 → 切备用"这一段就演示不出来；</li>
 *   <li>而如果把主模型直接写死成"永远失败"，就违背了"主模型用 {@code ModelResolver#resolve()}"的约定，
 *       也无法在真实模型接入后观察真实链路的故障转移行为。</li>
 * </ul>
 *
 * <p>所以这里保留真实模型作为 delegate，只在最外层提供"是否注入故障"的开关：</p>
 * <ul>
 *   <li>注入故障（默认开）：{@code stream(...)} 直接返回错误 → 真实走一遍 failover 链路；</li>
 *   <li>关闭注入：原样转发给 delegate → 观察"主模型正常"的情况。</li>
 * </ul>
 *
 * <p>⚠️ 本类是<b>普通对象</b>，不注册为 Spring Bean（原因同 {@link AlwaysFailingModel}）。</p>
 */
public class FailoverPrimaryModel implements Model {

    /** 真实模型（Mock 或真实 LLM），由 ModelResolver 提供 */
    private final Model delegate;

    /** 对外暴露的模型名 */
    private final String modelName;

    /** 是否注入"主模型故障" */
    private final AtomicBoolean failureInjected = new AtomicBoolean(true);

    public FailoverPrimaryModel(Model delegate) {
        this.delegate = delegate;
        this.modelName = delegate == null ? "primary-unknown" : delegate.getModelName();
    }

    @Override
    public Flux<ChatResponse> stream(List<Msg> msgs, List<ToolSchema> tools, GenerateOptions options) {
        if (failureInjected.get()) {
            return Flux.error(new ModelException(
                    "主模型模拟故障：注入 HTTP 503 Service Unavailable（" + modelName + " 不可用）"));
        }
        if (delegate == null) {
            return Flux.error(new ModelException("主模型未配置"));
        }
        return delegate.stream(msgs, tools, options);
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    /**
     * 真实模型（Mock / LLM）的名字。
     */
    public String getDelegateModelName() {
        return delegate == null ? "-" : delegate.getModelName();
    }

    /**
     * 当前是否处于故障注入状态。
     */
    public boolean isFailureInjected() {
        return failureInjected.get();
    }

    /**
     * 开关故障注入。
     */
    public void setFailureInjected(boolean injected) {
        failureInjected.set(injected);
    }
}
