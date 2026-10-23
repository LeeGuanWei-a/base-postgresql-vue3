package com.basic.agentscope.capability.subagent;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import com.basic.agentscope.tool.PlatformTools;
import com.basic.agentscope.tool.SystemTools;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.model.ToolSchema;
import io.agentscope.core.tool.Toolkit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 能力：<b>子 Agent（Sub Agent）</b>。
 *
 * <p>演示"主 Agent 把活儿派给子 Agent"的编排方式：</p>
 * <ol>
 *   <li>先创建一个内部子 Agent（{@code report-sub-agent}，带平台数据查询工具），</li>
 *   <li>再把子 Agent 包装成一个工具 {@code delegate_report}（见 {@link SubAgentTools}）注册给主 Agent，</li>
 *   <li>主 Agent 调用该工具时，框架内部会真正驱动子 Agent 跑完它自己的 ReAct 循环
 *       （子 Agent 自己查 PostgreSQL 拿真实数据），并把子 Agent 的回答作为工具结果回灌给主 Agent。</li>
 * </ol>
 *
 * <p>⚠️ 子 Agent 只是普通对象，<b>不注册为 Spring Bean</b>：
 * 一来它不是独立能力，二来本模块已有多个 {@code ReActAgent} Bean，再加会加重注入歧义。</p>
 *
 * <p>📌 框架自带的 {@code Toolkit.registration().subAgent(SubAgentProvider)}
 * 在 AgentScope 2.0.4 里会把子 Agent 包成内置 {@code SubAgentTool}（{@code AgentTool}），
 * 其参数 Schema 带 {@code required}，而 Mock 模型构造工具调用时不填 {@code ToolUseBlock.content}，
 * 会被框架的参数校验拦下（"未找到必需的参数：message"）——
 * 所以这里采用 {@link SubAgentTools} 的降级实现，效果等价且 Mock / 真实模型都能跑通。</p>
 */
@Configuration
public class SubAgentCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "subAgentCapabilityAgent";

    /** 子 Agent 对外暴露成工具时的名字 */
    private static final String SUB_AGENT_TOOL = "delegate_report";

    /** 主 Agent */
    private ReActAgent agent;

    /** 内部子 Agent */
    private ReActAgent subAgent;

    @Bean(AGENT_BEAN)
    public ReActAgent subAgentCapabilityAgent(ModelResolver modelResolver,
                                               PlatformTools platformTools,
                                               SystemTools systemTools) {
        // 1) 子 Agent：自己带一套工具，能真正查库出数据
        Toolkit subToolkit = new Toolkit();
        subToolkit.registerTool(platformTools);
        subToolkit.registerTool(systemTools);

        this.subAgent = ReActAgent.builder()
                .name("report-sub-agent")
                .description("负责生成平台数据报告的子 Agent，可以查用户/字典/角色/操作日志/表行数")
                .sysPrompt("""
                        你是一个数据报告子 Agent。
                        接到主 Agent 的委派任务后，请调用工具查询平台的真实数据，然后用中文给出简明的数据报告：
                        1. 先给结论，再列关键明细；
                        2. 只能使用工具返回的真实数据，没有数据就如实说明；
                        3. 报告控制在 10 行以内。
                        """)
                .model(modelResolver.resolve())
                .toolkit(subToolkit)
                .maxIters(5)
                .build();

        // 2) 主 Agent：只暴露"把活儿派给子 Agent"这一个工具
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new SubAgentTools(this.subAgent));

        this.agent = ReActAgent.builder()
                .name("sub-agent-master")
                .description("负责编排与委派的主 Agent")
                .sysPrompt("""
                        你是一个编排型主 Agent。
                        遇到需要查数据、做报告、统计数量之类的任务时，不要自己回答，
                        请调用 delegate_report 工具把任务委派给子 Agent，由子 Agent 查询真实数据并出报告。
                        拿到子 Agent 的结果后，用中文向用户复述结论（先结论后明细）。
                        """)
                .model(modelResolver.resolve())
                .toolkit(toolkit)
                .maxIters(5)
                .build();
        return this.agent;
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "sub-agent",
                "子 Agent",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "主 Agent 把数据类任务委派给子 Agent：子 Agent 被注册成工具 delegate_report，由框架驱动它跑完自己的 ReAct 循环并回传真实数据",
                "Connection",
                null,
                true,
                true,
                null,
                "/agentscope/capability/sub-agent/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "要委派给子 Agent 的任务", "统计一下系统里有多少用户和角色"),
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
        detail.put("mainAgent", agent == null ? "sub-agent-master" : agent.getName());
        detail.put("subAgent", subAgent == null ? "report-sub-agent" : subAgent.getName());
        detail.put("subAgentTool", SUB_AGENT_TOOL);
        detail.put("mainAgentTools", toolNames(agent));
        detail.put("subAgentTools", toolNames(subAgent));
        detail.put("note", "主 Agent 的工具清单里只有 delegate_report —— 它就是子 Agent 的对外入口；"
                + "子 Agent 自己带平台数据查询工具，最终报告里的数据是数据库真实数据。");
        return detail;
    }

    /**
     * 取某个 Agent 的工具清单（名称 + 描述）。
     */
    private List<Map<String, Object>> toolNames(ReActAgent target) {
        List<Map<String, Object>> tools = new ArrayList<>();
        if (target == null || target.getToolkit() == null) {
            return tools;
        }
        for (ToolSchema schema : target.getToolkit().getToolSchemas()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", schema.getName());
            item.put("description", schema.getDescription());
            tools.add(item);
        }
        return tools;
    }
}
