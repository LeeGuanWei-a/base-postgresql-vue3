package com.basic.agentscope.config;

import com.basic.agentscope.mock.MockChatModel;
import io.agentscope.core.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 模型解析器 —— 决定某次调用到底用<b>真实模型</b>还是 <b>Mock 模型</b>。
 *
 * <p>为什么不直接覆盖 starter 的 {@code Model} Bean？</p>
 * <ul>
 *   <li>starter 的 {@code AgentscopeAutoConfiguration} 会按类型注入 {@code Model}，
 *       一旦容器里出现第二个 {@code Model} 类型的 Bean 就会歧义 → 启动失败；</li>
 *   <li>因此 Mock 模型<b>不注册为 Spring Bean</b>，由本类持有并在需要时返回。</li>
 * </ul>
 *
 * <p>开关：{@code agentscope.mock.enabled}（环境变量 {@code AGENTSCOPE_MOCK_ENABLED}）。
 * 默认为 {@code true}，因为本分支默认场景是"还没有真实 API Key，先把链路跑通"。</p>
 */
@Component
public class ModelResolver {

    private static final Logger log = LoggerFactory.getLogger(ModelResolver.class);

    /** starter 自动装配的 OpenAI 兼容模型；未启用 OpenAI 扩展时为 null */
    @Autowired(required = false)
    @Qualifier("openAIChatModel")
    private Model openaiModel;

    @Value("${agentscope.mock.enabled:true}")
    private boolean mockEnabled;

    /** Mock 模型单例（非 Spring Bean，理由见类注释） */
    private final MockChatModel mockChatModel = new MockChatModel();

    /**
     * 解析默认模型。
     */
    public Model resolve() {
        return resolve(null);
    }

    /**
     * 按厂商解析模型。
     *
     * @param provider 厂商标识；{@code null} / {@code openai} / 其它值均落到默认逻辑
     */
    public Model resolve(String provider) {
        if (mockEnabled) {
            return mockChatModel;
        }
        if (openaiModel == null) {
            log.warn("未配置真实模型（agentscope.mock.enabled=false 且 openAIChatModel 不存在），回退到 Mock 模型");
            return mockChatModel;
        }
        return openaiModel;
    }

    /**
     * 当前是否处于 Mock 模式。
     */
    public boolean isMock() {
        return mockEnabled || openaiModel == null;
    }

    /**
     * Mock 模型实例（需要为特定能力定制行为时使用）。
     */
    public MockChatModel getMockChatModel() {
        return mockChatModel;
    }

    /**
     * 真实模型是否可用。
     */
    public boolean hasRealModel() {
        return openaiModel != null;
    }
}
