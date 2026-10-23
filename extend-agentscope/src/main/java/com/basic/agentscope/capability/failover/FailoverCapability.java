package com.basic.agentscope.capability.failover;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import io.agentscope.core.ReActAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 能力：<b>模型故障转移</b>。
 *
 * <p>AgentScope 的 {@code ReActAgent} 支持 {@code fallbackModel} + {@code maxRetries}：
 * 主模型抛异常时自动重试，重试用尽后切换到备用模型；备用模型也失败则最终把异常抛给调用方。</p>
 *
 * <p>本能力把这条链路<b>真实跑出来</b>：</p>
 * <ol>
 *   <li>主模型 = {@code ModelResolver#resolve()} 提供的模型，外面套一层
 *       {@link FailoverPrimaryModel}（默认开启"故障注入"，让 Mock 模型也能触发故障）；</li>
 *   <li>备用模型 = {@link AlwaysFailingModel}（永远抛异常）；</li>
 *   <li>{@link FailoverCountingListener} 统计每次故障转移。</li>
 * </ol>
 *
 * <p>因此调用本能力时，你会看到完整链路：
 * <b>主模型失败 → 触发 failover → 切备用模型 → 备用也失败 → 重试用尽 → 最终报错</b>，
 * 而 {@code /agentscope/capability/failover/detail} 能看到每一次故障转移的明细。</p>
 *
 * <p>⚠️ 铁律：模块内存在多个 {@code ReActAgent} Bean，本类只<b>声明</b>不注入；
 * 任何地方注入 {@code ReActAgent} 都必须带 {@code @Qualifier}。</p>
 */
@Configuration
public class FailoverCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "failoverCapabilityAgent";

    /** 最大重试次数（主模型失败后重试几次再切备用模型） */
    private static final int MAX_RETRIES = 2;

    /** 故障转移监听器（普通对象，由本能力持有） */
    private final FailoverCountingListener failoverListener = new FailoverCountingListener();

    /** 主模型（包装了 ModelResolver 提供的模型） */
    private FailoverPrimaryModel primaryModel;

    /** 备用模型（永远失败） */
    private AlwaysFailingModel backupModel;

    /** 故障注入开关（Agent Bean 创建前也能改，创建时会同步给主模型） */
    private volatile boolean primaryFailureInjected = true;

    @Bean(AGENT_BEAN)
    public ReActAgent failoverCapabilityAgent(ModelResolver modelResolver,
                                               @Value("${agentscope.failover.simulate-primary-failure:true}")
                                               boolean simulatePrimaryFailure) {
        this.primaryFailureInjected = simulatePrimaryFailure;
        // 主模型：来自 ModelResolver（Mock / 真实 LLM），外面套一层故障注入开关
        this.primaryModel = new FailoverPrimaryModel(modelResolver.resolve());
        this.primaryModel.setFailureInjected(simulatePrimaryFailure);
        // 备用模型：永远抛异常，用来演示"切备用后依然失败"的最终报错
        this.backupModel = new AlwaysFailingModel("backup-always-fail");

        return ReActAgent.builder()
                .name("failover-agent")
                .description("演示模型故障转移：主模型失败 → 重试 → 切备用模型 → 备用也失败 → 最终报错")
                .sysPrompt("""
                        你是一个演示 AgentScope 模型故障转移机制的智能助手。
                        由于主模型被注入了 503 故障，本轮对话最终会以错误结束——
                        这正是要展示的现象：主模型失败 → 触发 failover → 切备用模型 → 备用也失败 → 重试用尽 → 最终报错。
                        故障转移的次数与明细可以在能力详情里看到。
                        """)
                .model(this.primaryModel)
                .fallbackModel(this.backupModel)
                .maxRetries(MAX_RETRIES)
                .failoverListener(this.failoverListener)
                .maxIters(3)
                .build();
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "failover",
                "模型故障转移",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "配置 fallbackModel 与 maxRetries：主模型失败自动重试并切换备用模型，备用也失败时最终报错（附故障转移计数）",
                "RefreshRight",
                null,
                true,
                true,
                null,
                "/agentscope/capability/failover/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题（会触发主模型故障）", "你好，测试一下故障转移"),
                        CapabilityInfo.ParamInfo.text("sessionId", "会话 ID（可留空）", "")
                ));
    }

    @Override
    public String agentBeanName() {
        return AGENT_BEAN;
    }

    @Override
    public Map<String, Object> detail() {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("primaryModel", primaryModel == null ? "-" : primaryModel.getModelName());
        detail.put("primaryRealModel", primaryModel == null ? "-" : primaryModel.getDelegateModelName());
        detail.put("primaryFailureInjected", primaryFailureInjected);
        detail.put("backupModel", backupModel == null ? "-" : backupModel.getModelName());
        detail.put("maxRetries", MAX_RETRIES);
        detail.put("failoverCount", failoverListener.failoverCount());
        detail.put("recentFailovers", failoverListener.recentEvents(10));
        detail.put("agentBean", AGENT_BEAN);
        return detail;
    }

    // ------------------------------------------------------------------ 故障注入开关

    /**
     * 是否处于"主模型故障注入"状态。
     */
    public boolean isPrimaryFailureInjected() {
        return primaryFailureInjected;
    }

    /**
     * 开关主模型故障注入（供演示接口调用）。
     */
    public boolean setPrimaryFailureInjected(boolean injected) {
        this.primaryFailureInjected = injected;
        if (primaryModel != null) {
            primaryModel.setFailureInjected(injected);
        }
        return true;
    }
}
