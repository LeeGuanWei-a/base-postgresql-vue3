package com.basic.agentscope.capability.failover;

import io.agentscope.core.message.Msg;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.ModelException;
import io.agentscope.core.model.ToolSchema;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 永远失败的模型 —— 用于<b>真实演示</b>故障转移链路。
 *
 * <p>它实现了 AgentScope 的 {@link Model} SPI，{@code stream(...)} 直接返回
 * {@link Flux#error(Throwable)}，因此框架会真正走完
 * "主模型失败 → 触发 failover → 切备用模型 → 备用模型也失败 → 重试用尽 → 最终抛错"的全过程。</p>
 *
 * <p>⚠️ <b>铁律</b>：本类<b>绝不能注册为 Spring Bean</b>。它实现了 {@link Model} 接口，
 * 一旦成为 Bean 就会与 starter 自动装配的 {@code openAIChatModel} 形成同类型 Bean 歧义，
 * 导致 {@code AgentscopeAutoConfiguration} 按类型注入 {@code Model} 时启动失败。
 * 它只作为普通对象被 {@link FailoverCapability} 持有。</p>
 */
public class AlwaysFailingModel implements Model {

    /** 模型名（会出现在 failover 日志与前端详情里） */
    private final String modelName;

    /** 失败原因 */
    private final String reason;

    public AlwaysFailingModel(String modelName) {
        this(modelName, "模拟的模型不可用故障（HTTP 503 Service Unavailable）");
    }

    public AlwaysFailingModel(String modelName, String reason) {
        this.modelName = modelName;
        this.reason = reason;
    }

    @Override
    public Flux<ChatResponse> stream(List<Msg> msgs, List<ToolSchema> tools, GenerateOptions options) {
        return Flux.error(new ModelException(reason));
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    /**
     * 失败原因。
     */
    public String getReason() {
        return reason;
    }
}
