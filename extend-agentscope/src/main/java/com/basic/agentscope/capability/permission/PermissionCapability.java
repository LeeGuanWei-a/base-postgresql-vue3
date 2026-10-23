package com.basic.agentscope.capability.permission;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import com.basic.agentscope.tool.PlatformTools;
import com.basic.agentscope.tool.SystemTools;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.permission.PermissionBehavior;
import io.agentscope.core.permission.PermissionContextState;
import io.agentscope.core.permission.PermissionMode;
import io.agentscope.core.permission.PermissionRule;
import io.agentscope.core.tool.Toolkit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 能力：<b>权限控制（Permission）</b>。
 *
 * <p>演示 AgentScope 的权限扩展点：</p>
 * <ol>
 *   <li>{@code PermissionContextState} —— 声明 allow / deny / ask 三类规则，
 *       规则<b>按工具名精确匹配</b>；</li>
 *   <li>{@code stopOnReject(true)} —— 工具调用被拒绝时立即停止 ReAct 循环；</li>
 *   <li>{@code setPermissionMode(userId, sessionId, mode)} —— 运行时切换权限模式
 *       （DEFAULT / ACCEPT_EDITS / EXPLORE / BYPASS / DONT_ASK）。</li>
 * </ol>
 *
 * <p>默认规则：<b>只读工具放行（ALLOW），写操作工具拒绝（DENY）</b>。
 * 由于权限引擎的判定顺序是 DENY → ASK → ALLOW，
 * 被拒的工具在 BYPASS 模式之外都会返回"权限不足"。</p>
 */
@Configuration
public class PermissionCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "permissionCapabilityAgent";

    /** 演示用固定用户 ID（与通用控制器 RuntimeContext.userId 一致） */
    public static final String DEMO_USER_ID = "demo-user";

    /** 默认会话 ID */
    public static final String DEFAULT_SESSION_ID = "default";

    /** 允许放行的只读工具 */
    private static final List<String> ALLOWED_TOOLS = List.of(
            "query_sys_user",
            "query_dict_data",
            "query_oper_log",
            "query_sys_role",
            "count_table_rows",
            "current_datetime",
            "subtract_numbers",
            "echo_text");

    /** 拒绝放行的写操作工具 */
    private static final List<String> DENIED_TOOLS = List.of(
            "freeze_login_account",
            "reset_login_secret");

    /** 规则来源标记 */
    private static final String RULE_SOURCE = "permission-capability";

    /** 能力专属 Toolkit（普通对象，不注册为 Spring Bean，避免同类型歧义） */
    private Toolkit toolkit;

    /** 该能力专属的 Agent（供 detail() 查询权限上下文与模式） */
    private ReActAgent agent;

    /** 装配给 Agent 的默认权限上下文（{@code ReActAgent#getPermissionContext()} 已废弃，改用本地副本） */
    private PermissionContextState permissionContext;

    @Bean(AGENT_BEAN)
    public ReActAgent permissionCapabilityAgent(ModelResolver modelResolver,
                                                 PlatformTools platformTools,
                                                 SystemTools systemTools) {
        this.toolkit = new Toolkit();
        this.toolkit.registerTool(platformTools);
        this.toolkit.registerTool(systemTools);
        this.toolkit.registerTool(new PermissionWriteTools());

        this.permissionContext = buildPermissionContext();

        this.agent = ReActAgent.builder()
                .name("permission-agent")
                .description("受权限规则约束的智能体：只读工具放行，写操作工具拒绝")
                .sysPrompt("""
                        你是企业级 Java 基础平台（RuoYi 风格）的受控助手。
                        你的工具调用受权限规则约束：
                        1. 查询类（只读）工具可以直接调用；
                        2. 写操作类工具（冻结账户、重置口令等）默认被拒绝，若被拒绝，请如实告知用户"权限不足，需要管理员授权"；
                        3. 回答用中文，先给结论再列明细。
                        """)
                .model(modelResolver.resolve())
                .toolkit(this.toolkit)
                .permissionContext(this.permissionContext)
                .stopOnReject(true)
                .defaultSessionId(DEFAULT_SESSION_ID)
                .maxIters(5)
                .build();
        return this.agent;
    }

    /**
     * 构造默认权限上下文：只读工具 ALLOW，写操作工具 DENY。
     */
    private PermissionContextState buildPermissionContext() {
        PermissionContextState.Builder builder = PermissionContextState.builder()
                .mode(PermissionMode.DEFAULT);
        for (String tool : ALLOWED_TOOLS) {
            builder.addAllowRule(tool, new PermissionRule(tool, null, PermissionBehavior.ALLOW, RULE_SOURCE));
        }
        for (String tool : DENIED_TOOLS) {
            builder.addDenyRule(tool, new PermissionRule(tool, null, PermissionBehavior.DENY, RULE_SOURCE));
        }
        return builder.build();
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "permission",
                "权限控制",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "权限引擎：按工具名配置 allow/deny/ask 规则，拒绝即停止 ReAct 循环，权限模式可运行时切换",
                "Lock",
                null,
                true,
                true,
                null,
                "/agentscope/capability/permission/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "系统里有哪些角色？"),
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
        detail.put("agentBean", AGENT_BEAN);
        detail.put("defaultSessionId", DEFAULT_SESSION_ID);
        detail.put("currentUserId", DEMO_USER_ID);
        detail.put("stopOnReject", true);
        detail.put("permissionMode", currentMode(DEFAULT_SESSION_ID));
        detail.put("permissionModeLabel", modeLabel(currentMode(DEFAULT_SESSION_ID)));

        PermissionContextState context = defaultContext();
        detail.put("allowRules", describeRules(context.getAllowRules()));
        detail.put("denyRules", describeRules(context.getDenyRules()));
        detail.put("askRules", describeRules(context.getAskRules()));
        detail.put("protectedTools", DENIED_TOOLS);
        detail.put("allowedTools", ALLOWED_TOOLS);
        detail.put("availableModes", List.of(
                PermissionMode.DEFAULT.name(),
                PermissionMode.ACCEPT_EDITS.name(),
                PermissionMode.EXPLORE.name(),
                PermissionMode.BYPASS.name(),
                PermissionMode.DONT_ASK.name()));
        return detail;
    }

    /**
     * 取默认权限上下文（build 时设置的那份）。
     */
    private PermissionContextState defaultContext() {
        return permissionContext == null ? buildPermissionContext() : permissionContext;
    }

    /**
     * 查询指定会话当前的权限模式。
     */
    public PermissionMode currentMode(String sessionId) {
        String sid = (sessionId == null || sessionId.isBlank()) ? DEFAULT_SESSION_ID : sessionId;
        PermissionMode mode = agent == null ? null : agent.getPermissionMode(DEMO_USER_ID, sid);
        return mode == null ? PermissionMode.DEFAULT : mode;
    }

    /**
     * 规则清单（扁平化，方便前端展示）。
     */
    private List<Map<String, Object>> describeRules(Map<String, List<PermissionRule>> rules) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (rules == null) {
            return list;
        }
        for (Map.Entry<String, List<PermissionRule>> entry : rules.entrySet()) {
            for (PermissionRule rule : entry.getValue()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("toolName", rule.toolName());
                item.put("behavior", rule.behavior() == null ? "-" : rule.behavior().name());
                item.put("ruleContent", rule.ruleContent() == null ? "(匹配全部入参)" : rule.ruleContent());
                item.put("source", rule.source());
                list.add(item);
            }
        }
        return list;
    }

    /**
     * 权限模式中文说明。
     */
    private String modeLabel(PermissionMode mode) {
        if (mode == null) {
            return "-";
        }
        return switch (mode) {
            case DEFAULT -> "默认：未命中规则的工具需要询问";
            case ACCEPT_EDITS -> "自动接受编辑类操作";
            case EXPLORE -> "只读探索：允许读、拒绝写";
            case BYPASS -> "绕过：跳过所有权限检查";
            case DONT_ASK -> "不询问：无人应答时直接拒绝";
        };
    }
}
