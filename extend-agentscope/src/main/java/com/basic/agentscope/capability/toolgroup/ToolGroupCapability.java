package com.basic.agentscope.capability.toolgroup;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import com.basic.agentscope.tool.PlatformTools;
import com.basic.agentscope.tool.SystemTools;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.model.ToolSchema;
import io.agentscope.core.tool.ToolGroup;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.core.tool.ToolkitConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 能力：<b>工具分组（Tool Group）</b>。
 *
 * <p>演示 AgentScope 的"工具分组与按组开关"能力：</p>
 * <ol>
 *   <li>{@code Toolkit#createToolGroup(name, description, active)} 建组；</li>
 *   <li>注册工具时带上组名（{@code toolkit.registration().group("platform").tool(platformTools).apply()}），
 *       工具就被挂进该组；</li>
 *   <li>组可以被"启用/禁用"，禁用后该组工具对模型不可见
 *       （{@code Toolkit#updateToolGroups(groups, activate)} /
 *       {@code Toolkit#setActiveGroups(groups)}）。</li>
 * </ol>
 *
 * <p>⚠️ 关于"开关"：AgentScope 2.0.4 的
 * {@code Toolkit.registration().enableTools(...)/disableTools(...)}
 * <b>只在注册 MCP 客户端时生效</b>（内部只被 MCP 分支消费），
 * 普通 {@code @Tool} 方法的开关要用<b>工具组</b>的启用/禁用来实现，
 * 因此本类用 {@code updateToolGroups} / {@code setActiveGroups} 实现开关。</p>
 *
 * <p>另外，禁用工具组需要 Toolkit 允许删除（{@code ToolkitConfig#allowToolDeletion}），
 * 否则框架会拒绝停用并打 WARN。</p>
 */
@Configuration
public class ToolGroupCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "toolGroupCapabilityAgent";

    /** 平台数据查询工具组 */
    public static final String GROUP_PLATFORM = "platform";

    /** 通用工具组 */
    public static final String GROUP_SYSTEM = "system";

    /** 全部工具组名 */
    private static final List<String> GROUP_NAMES = List.of(GROUP_PLATFORM, GROUP_SYSTEM);

    /** 该能力专属的 Agent */
    private ReActAgent agent;

    /** Agent 实际持有的 Toolkit（{@code build()} 内部会 copy 一份，必须以 Agent 的为准） */
    private Toolkit toolkit;

    @Bean(AGENT_BEAN)
    public ReActAgent toolGroupCapabilityAgent(ModelResolver modelResolver,
                                               PlatformTools platformTools,
                                               SystemTools systemTools) {
        // allowToolDeletion(true)：允许停用工具组（否则 updateToolGroups(groups, false) 会被忽略）
        Toolkit builderToolkit = new Toolkit(ToolkitConfig.builder().allowToolDeletion(true).build());

        // 1) 建两个工具组（第三个参数 true = 默认激活）
        builderToolkit.createToolGroup(GROUP_PLATFORM,
                "平台数据查询工具：系统用户、字典、角色、操作日志、业务表行数", true);
        builderToolkit.createToolGroup(GROUP_SYSTEM,
                "通用工具：服务器当前时间、两数求差、文本回显", true);

        // 2) 把工具挂进对应的组
        builderToolkit.registration().group(GROUP_PLATFORM).tool(platformTools).apply();
        builderToolkit.registration().group(GROUP_SYSTEM).tool(systemTools).apply();

        this.agent = ReActAgent.builder()
                .name("tool-group-agent")
                .description("按工具组组织工具、可按组开关的智能体")
                .sysPrompt("""
                        你是一个拥有两套工具组的智能体：
                        1. platform 组：查平台真实数据（用户、字典、角色、操作日志、表行数）；
                        2. system 组：通用工具（当前时间、两数求差、文本回显）。
                        需要数据时调用 platform 组的工具，需要时间/计算/回显时调用 system 组的工具。
                        如果某一组工具当前不可用（被停用了），请如实说明，不要凭空编造。
                        """)
                .model(modelResolver.resolve())
                .toolkit(builderToolkit)
                .maxIters(5)
                .build();

        // build() 内部对 Toolkit 做了 copy，Agent 持有的是副本，开关必须作用在副本上
        this.toolkit = this.agent.getToolkit();
        return this.agent;
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "tool-group",
                "工具分组",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "把平台数据工具与通用工具分成 platform / system 两个工具组，支持按组启用停用，模型只看得到当前生效的工具",
                "FolderOpened",
                null,
                true,
                true,
                null,
                "/agentscope/capability/tool-group/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "系统里有多少用户？现在时间是几点？"),
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
        detail.put("agentName", agent == null ? "tool-group-agent" : agent.getName());

        List<Map<String, Object>> groups = new ArrayList<>();
        for (String groupName : GROUP_NAMES) {
            Map<String, Object> item = new LinkedHashMap<>();
            ToolGroup group = (toolkit == null) ? null : toolkit.getToolGroup(groupName);
            item.put("name", groupName);
            item.put("description", group == null ? null : group.getDescription());
            item.put("active", group != null && group.isActive());
            item.put("tools", group == null ? List.of() : new ArrayList<>(group.getTools()));
            groups.add(item);
        }
        detail.put("groups", groups);
        detail.put("groupCount", groups.size());
        detail.put("activeGroups", toolkit == null ? List.of() : toolkit.getActiveGroups());

        // 当前对模型生效的工具名集合（= 激活组里的工具 + 未分组的工具）
        List<String> enabledTools = new ArrayList<>();
        if (toolkit != null) {
            for (ToolSchema schema : toolkit.getToolSchemas()) {
                enabledTools.add(schema.getName());
            }
        }
        detail.put("enabledTools", enabledTools);
        detail.put("enabledToolCount", enabledTools.size());
        detail.put("registeredTools", toolkit == null ? Set.of() : toolkit.getToolNames());
        detail.put("note", "工具组开关通过 POST /agentscope/tool-group/toggle 切换（参数 group / enabled）；"
                + "停用某组后，该组工具不会再出现在给模型的 JSON Schema 里。");
        return detail;
    }

    // ------------------------------------------------------------------ 工具组开关

    /**
     * 启用某个工具组。
     *
     * @param groupName 工具组名
     * @return 切换后生效的工具名集合
     */
    public synchronized Set<String> enableGroup(String groupName) {
        if (toolkit == null || !GROUP_NAMES.contains(groupName)) {
            return currentEnabledTools();
        }
        toolkit.updateToolGroups(List.of(groupName), true);
        return currentEnabledTools();
    }

    /**
     * 停用某个工具组（该组工具对模型不可见）。
     *
     * @param groupName 工具组名
     * @return 切换后生效的工具名集合
     */
    public synchronized Set<String> disableGroup(String groupName) {
        if (toolkit == null || !GROUP_NAMES.contains(groupName)) {
            return currentEnabledTools();
        }
        // 第二个参数 false = 停用（需要 ToolkitConfig.allowToolDeletion(true)）
        toolkit.updateToolGroups(List.of(groupName), false);
        return currentEnabledTools();
    }

    /**
     * 只保留指定工具组生效（其余组停用）。
     *
     * @param groupNames 需要激活的工具组名
     * @return 切换后生效的工具名集合
     */
    public synchronized Set<String> activateOnly(List<String> groupNames) {
        if (toolkit == null) {
            return currentEnabledTools();
        }
        List<String> target = (groupNames == null) ? List.of() : groupNames.stream()
                .filter(GROUP_NAMES::contains)
                .toList();
        for (String name : GROUP_NAMES) {
            toolkit.updateToolGroups(List.of(name), target.contains(name));
        }
        return currentEnabledTools();
    }

    /**
     * 当前对模型生效的工具名集合。
     */
    public synchronized Set<String> currentEnabledTools() {
        Set<String> names = new LinkedHashSet<>();
        if (toolkit == null) {
            return names;
        }
        for (ToolSchema schema : toolkit.getToolSchemas()) {
            names.add(schema.getName());
        }
        return names;
    }
}
