package com.basic.agentscope.capability.session;

import com.basic.common.core.domain.AjaxResult;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.state.AgentStateStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 多会话能力专属控制器 —— 路径 {@code /agentscope/session}。
 *
 * <p>通用控制器 {@code /agentscope/capability/session/chat} 只负责聊天，
 * 这里补充"会话治理"类接口：查看有哪些会话、清除某个会话的状态。</p>
 */
@RestController
@RequestMapping("/agentscope/session")
public class SessionCapabilityController {

    /**
     * 该能力专属 Agent。
     *
     * <p>⚠️ 本模块存在多个 {@code ReActAgent} 类型的 Bean，
     * 按类型注入会歧义 → 启动失败，因此必须带 {@code @Qualifier}。</p>
     */
    @Autowired
    @Qualifier(SessionCapability.AGENT_BEAN)
    private ReActAgent agent;

    @Autowired
    private SessionCapability sessionCapability;

    /**
     * 列出当前用户下的所有会话。
     */
    @GetMapping("/list")
    public AjaxResult list() {
        List<String> sessionIds = sessionCapability.listSessionIds();
        List<Map<String, Object>> sessions = new ArrayList<>();
        for (String sessionId : sessionIds) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("sessionId", sessionId);
            item.put("userId", SessionCapability.DEMO_USER_ID);
            item.put("isDefault", sessionId.equals(agent.getDefaultSessionId()));
            sessions.add(item);
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("defaultSessionId", agent.getDefaultSessionId());
        data.put("stateStoreType", agent.getStateStore().getClass().getSimpleName());
        data.put("sessionCount", sessions.size());
        data.put("sessions", sessions);
        return AjaxResult.success(data);
    }

    /**
     * 清除某个会话的状态（内存缓存 + 状态存储双清）。
     *
     * @param sessionId 会话 ID
     */
    @DeleteMapping("/{sessionId}")
    public AjaxResult clear(@PathVariable("sessionId") String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return AjaxResult.error("sessionId 不能为空");
        }
        // 1) 清掉 Agent 内部的状态缓存
        agent.clearStateCache(SessionCapability.DEMO_USER_ID, sessionId);
        // 2) 清掉状态存储里落盘的会话
        AgentStateStore store = agent.getStateStore();
        if (store != null) {
            store.delete(SessionCapability.DEMO_USER_ID, sessionId);
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sessionId", sessionId);
        data.put("cleared", true);
        data.put("remainingSessionCount", sessionCapability.listSessionIds().size());
        return AjaxResult.success("会话状态已清除", data);
    }
}
