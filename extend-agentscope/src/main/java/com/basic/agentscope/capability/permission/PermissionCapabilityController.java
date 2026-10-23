package com.basic.agentscope.capability.permission;

import com.basic.common.core.domain.AjaxResult;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.permission.PermissionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限能力专属控制器 —— 路径 {@code /agentscope/permission}。
 *
 * <p>提供"查看当前权限模式 / 运行时切换权限模式"两个接口。</p>
 */
@RestController
@RequestMapping("/agentscope/permission")
public class PermissionCapabilityController {

    /**
     * 该能力专属 Agent。
     *
     * <p>⚠️ 本模块存在多个 {@code ReActAgent} 类型的 Bean，
     * 按类型注入会歧义 → 启动失败，因此必须带 {@code @Qualifier}。</p>
     */
    @Autowired
    @Qualifier(PermissionCapability.AGENT_BEAN)
    private ReActAgent agent;

    @Autowired
    private PermissionCapability permissionCapability;

    /**
     * 查询当前权限模式。
     *
     * @param sessionId 会话 ID；为空则查默认会话
     */
    @GetMapping("/mode")
    public AjaxResult mode(@RequestParam(value = "sessionId", required = false) String sessionId) {
        String sid = normalize(sessionId);
        PermissionMode mode = permissionCapability.currentMode(sid);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sessionId", sid);
        data.put("mode", mode.name());
        data.put("stopOnReject", true);
        data.put("availableModes", List.of(
                PermissionMode.DEFAULT.name(),
                PermissionMode.ACCEPT_EDITS.name(),
                PermissionMode.EXPLORE.name(),
                PermissionMode.BYPASS.name(),
                PermissionMode.DONT_ASK.name()));
        return AjaxResult.success(data);
    }

    /**
     * 切换权限模式。
     *
     * <p>模式既可以放在 query 参数 {@code mode} 上，也可以放在 JSON body 里。</p>
     */
    @PostMapping("/mode")
    public AjaxResult switchMode(@RequestParam(value = "mode", required = false) String modeParam,
                                 @RequestBody(required = false) PermissionModeRequest request) {
        String raw = modeParam;
        String sessionId = request == null ? null : request.getSessionId();
        if ((raw == null || raw.isBlank()) && request != null) {
            raw = request.getMode();
        }
        if (raw == null || raw.isBlank()) {
            return AjaxResult.error("mode 不能为空，可选值："
                    + "DEFAULT / ACCEPT_EDITS / EXPLORE / BYPASS / DONT_ASK");
        }
        PermissionMode mode;
        try {
            mode = PermissionMode.fromString(raw.trim().toUpperCase());
        } catch (Exception e) {
            return AjaxResult.error("非法的权限模式：" + raw);
        }
        String sid = normalize(sessionId);
        PermissionMode previous = permissionCapability.currentMode(sid);
        // ⚠️ AgentScope 的签名是 (userId, sessionId, mode)，不是 (sessionId, userId, mode)
        agent.setPermissionMode(PermissionCapability.DEMO_USER_ID, sid, mode);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sessionId", sid);
        data.put("mode", mode.name());
        data.put("previousMode", previous.name());
        return AjaxResult.success("权限模式已切换为 " + mode.name(), data);
    }

    /**
     * 归一化会话 ID。
     */
    private String normalize(String sessionId) {
        return (sessionId == null || sessionId.isBlank())
                ? PermissionCapability.DEFAULT_SESSION_ID
                : sessionId.trim();
    }
}
