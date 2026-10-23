package com.basic.agentscope.capability.session;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.state.AgentState;
import io.agentscope.core.state.AgentStateStore;
import io.agentscope.core.state.InMemoryAgentStateStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 能力：<b>多会话（Multi-Session）</b>。
 *
 * <p>演示 AgentScope 如何按 <b>sessionId</b> 隔离 Agent 状态：</p>
 * <ol>
 *   <li>Agent 绑定一个 {@link InMemoryAgentStateStore} 作为状态存储；</li>
 *   <li>状态存储的索引是 {@code (userId, sessionId)}，因此同一个 Agent
 *       在不同 sessionId 下拥有<b>完全独立的上下文</b>；</li>
 *   <li>调用时通过 {@code RuntimeContext} 传入 sessionId / userId，
 *       框架据此读写对应的状态槽位。</li>
 * </ol>
 *
 * <p>验证方式：用同一个 sessionId 连续问两次（第二次能引用第一次的内容），
 * 再换一个 sessionId 提问（答不出上一个会话的内容）→ 说明会话确实隔离。</p>
 */
@Configuration
public class SessionCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "sessionCapabilityAgent";

    /**
     * 演示用固定用户 ID —— 必须与通用控制器
     * {@code CapabilityController#buildRuntimeContext} 里的 userId 保持一致，
     * 否则状态会落到另一个用户槽位下，列不出来。
     */
    public static final String DEMO_USER_ID = "demo-user";

    /** 状态存储（内存版） */
    private InMemoryAgentStateStore stateStore;

    /** 该能力专属的 Agent（供 detail() 查询会话内的消息条数） */
    private ReActAgent agent;

    @Bean(AGENT_BEAN)
    public ReActAgent sessionCapabilityAgent(ModelResolver modelResolver) {
        this.stateStore = new InMemoryAgentStateStore();
        this.agent = ReActAgent.builder()
                .name("session-agent")
                .description("多会话隔离智能体：同一 Agent 按 sessionId 维护互相独立的上下文")
                .sysPrompt("""
                        你是企业级 Java 基础平台（RuoYi 风格）的多会话助手。
                        你会记住「当前会话」里的上下文，可以回答"我刚才说了什么""前面提到的那个功能"这类指代问题。
                        回答要求：
                        1. 只依据当前会话的上下文作答，不要编造其它会话的内容；
                        2. 回答用中文，简洁明了；
                        3. 若当前会话没有任何上下文，如实说明"这是新会话，我没有历史记录"。
                        """)
                .model(modelResolver.resolve())
                .stateStore(this.stateStore)
                .defaultSessionId("default")
                .maxIters(5)
                .build();
        return this.agent;
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "session",
                "多会话",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "按 sessionId 隔离 Agent 上下文：同一 Agent 在不同会话下记忆互不干扰，可列出/清除会话",
                "ChatLineRound",
                null,
                true,
                true,
                null,
                "/agentscope/capability/session/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "我叫小明，请记住我的名字"),
                        CapabilityInfo.ParamInfo.text("sessionId", "会话 ID（同一 ID 即同一会话）", "default")
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
        detail.put("defaultSessionId", agent == null ? "default" : agent.getDefaultSessionId());
        detail.put("stateStoreType", stateStore == null ? "-" : stateStore.getClass().getSimpleName());
        detail.put("currentUserId", DEMO_USER_ID);

        List<String> sessionIds = listSessionIds();
        detail.put("sessionCount", sessionIds.size());

        List<Map<String, Object>> sessions = new ArrayList<>();
        for (String sessionId : sessionIds) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("sessionId", sessionId);
            item.put("userId", DEMO_USER_ID);
            item.put("messageCount", countMessages(sessionId));
            item.put("isDefault", sessionId.equals(detail.get("defaultSessionId")));
            sessions.add(item);
        }
        detail.put("sessions", sessions);
        return detail;
    }

    /**
     * 列出当前用户下所有已建立状态的会话 ID。
     */
    public List<String> listSessionIds() {
        if (stateStore == null) {
            return new ArrayList<>();
        }
        Set<String> ids = stateStore.listSessionIds(DEMO_USER_ID);
        // TreeSet 保证前端展示顺序稳定
        return new ArrayList<>(new TreeSet<>(ids));
    }

    /**
     * 统计某个会话里已积累的消息条数（读缓存，不落盘）。
     */
    private int countMessages(String sessionId) {
        if (agent == null) {
            return 0;
        }
        try {
            AgentState state = agent.getAgentState(DEMO_USER_ID, sessionId);
            return state == null || state.getContext() == null ? 0 : state.getContext().size();
        } catch (Exception e) {
            return 0;
        }
    }
}
