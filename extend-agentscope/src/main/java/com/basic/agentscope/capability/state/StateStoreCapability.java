package com.basic.agentscope.capability.state;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.tool.Toolkit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 能力：<b>状态持久化</b>。
 *
 * <p>AgentScope 的 {@code ReActAgent} 把会话上下文、迭代次数、权限状态等放在
 * {@code AgentState} 里，并可通过 {@code stateStore(...)} 指定持久化位置。</p>
 *
 * <p>本能力用 {@link FileAgentStateStore}（本地文件 + JSON）实现，
 * 每轮对话结束后框架会自动把状态写到
 * {@code {dir}/{userId}/{sessionId}/agent_state.json}，
 * 于是"换个 sessionId 重开对话"仍能读到历史上下文。</p>
 *
 * <p>存储目录由配置项 {@code agentscope.state-store.dir} 指定，
 * 默认 {@code ${user.home}/.agentscope/rouyi-state}。</p>
 *
 * <p>⚠️ 铁律：模块内存在多个 {@code ReActAgent} Bean，本类只<b>声明</b>不注入；
 * 任何地方注入 {@code ReActAgent} 都必须带 {@code @Qualifier}。</p>
 */
@Configuration
public class StateStoreCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "stateStoreCapabilityAgent";

    /** 文件状态存储（普通对象，由本能力持有） */
    private final FileAgentStateStore stateStore;

    public StateStoreCapability(@Value("${agentscope.state-store.dir:}") String dir) {
        Path root = (dir == null || dir.isBlank())
                ? Paths.get(System.getProperty("user.home"), ".agentscope", "rouyi-state")
                : Paths.get(dir);
        this.stateStore = new FileAgentStateStore(root);
    }

    @Bean(AGENT_BEAN)
    public ReActAgent stateStoreCapabilityAgent(ModelResolver modelResolver,
                                                @Qualifier("businessToolkit") Toolkit toolkit) {
        return ReActAgent.builder()
                .name("state-store-agent")
                .description("带文件状态持久化的智能体")
                .sysPrompt("""
                        你是一个演示 AgentScope 状态持久化的智能助手。
                        本轮对话结束后，你的上下文会被自动落盘成 JSON 文件；
                        使用同一个 sessionId 再次提问时，你可以"想起"之前说过的话。
                        回答要求：
                        1. 需要数据时调用工具获取真实数据；
                        2. 回答用中文，简洁准确。
                        """)
                .model(modelResolver.resolve())
                .toolkit(toolkit)
                .stateStore(stateStore)
                .maxIters(5)
                .build();
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "state-store",
                "状态持久化",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "用 stateStore 把 AgentState 落盘成 JSON（{目录}/{用户}/{会话}/agent_state.json），会话上下文可跨进程恢复",
                "Coin",
                null,
                true,
                true,
                null,
                "/agentscope/capability/state-store/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "我叫什么名字？我之前说过什么？"),
                        CapabilityInfo.ParamInfo.text("sessionId", "会话 ID（相同 ID 可恢复历史上下文）", "demo-session-1")
                ));
    }

    @Override
    public String agentBeanName() {
        return AGENT_BEAN;
    }

    @Override
    public Map<String, Object> detail() {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("storeImplementation", FileAgentStateStore.class.getSimpleName());
        detail.put("directory", stateStore.getRootDirectory().toString());
        detail.put("stateFileCount", stateStore.countStateFiles());

        List<Map<String, Object>> users = new ArrayList<>();
        int sessionCount = 0;
        for (String userId : stateStore.listUserIds()) {
            Set<String> sessionIds = stateStore.listSessionIds(userId);
            List<Map<String, Object>> sessions = new ArrayList<>();
            for (String sessionId : sessionIds) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("sessionId", sessionId);
                item.put("stateNames", stateStore.listStateNames(userId, sessionId));
                sessions.add(item);
            }
            sessionCount += sessionIds.size();
            Map<String, Object> user = new LinkedHashMap<>();
            user.put("userId", userId);
            user.put("sessionCount", sessionIds.size());
            user.put("sessions", sessions);
            users.add(user);
        }
        detail.put("userCount", users.size());
        detail.put("sessionCount", sessionCount);
        detail.put("users", users);
        detail.put("agentBean", AGENT_BEAN);
        return detail;
    }
}
