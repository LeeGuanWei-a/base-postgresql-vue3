package com.basic.agentscope.capability.subagent;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.state.AgentState;
import io.agentscope.core.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.UUID;

/**
 * 子 Agent 的"工具外壳" —— 把子 Agent 包装成主 Agent 可以调用的一个工具。
 *
 * <p><b>为什么用这个降级方案？</b></p>
 * <p>AgentScope 2.0.4 提供了 {@code Toolkit.registration().subAgent(SubAgentProvider)}，
 * 会把子 Agent 包成框架内置的 {@code SubAgentTool}（一个 {@code AgentTool}）。
 * 但框架在执行工具前会用 <b>{@code ToolUseBlock.getContent()}</b>（模型给出的原始参数 JSON 文本）
 * 去校验参数，而 Mock 模型构造 {@code ToolUseBlock} 时只填了 {@code input}、没有填 {@code content}，
 * 于是内置 {@code SubAgentTool}（其 Schema 带 {@code required:[message]}）会被判为
 * "未找到必需的参数：message" 而无法执行。</p>
 * <p>因此这里改用普通的 {@code @Tool} 方法作为外壳：方法只声明一个由框架注入的
 * {@link AgentState} 参数（没有必填参数，Mock 与真实模型都能正常调用），
 * 内部真正驱动子 Agent 跑完它自己的 ReAct 循环，并把子 Agent 的回答作为工具结果返回给主 Agent。</p>
 *
 * <p>子 Agent 收到的任务 = 用户当前的问题（从 {@code AgentState} 的上下文里取最后一条用户消息），
 * 所以委派出去的是"真问题"，而不是模型编的参数。</p>
 */
public class SubAgentTools {

    private static final Logger log = LoggerFactory.getLogger(SubAgentTools.class);

    /** 调用子 Agent 的超时时间 */
    private static final Duration TIMEOUT = Duration.ofSeconds(90);

    /** 取不到用户问题时使用的兜底任务 */
    private static final String DEFAULT_TASK = "请统计平台里有多少用户、角色、字典与操作日志，并给出中文简报";

    /** 被委派的子 Agent */
    private final ReActAgent subAgent;

    public SubAgentTools(ReActAgent subAgent) {
        this.subAgent = subAgent;
    }

    /**
     * 把任务委派给子 Agent。
     *
     * @param agentState 框架注入的当前会话状态（{@code stateInjected = true}），
     *                   用来取出用户当前的问题作为委派任务
     * @return 子 Agent 的回答文本
     */
    @Tool(name = "delegate_report",
            description = "当需要查询平台真实数据（用户/字典/角色/操作日志/表行数）并产出一份数据报告时，"
                    + "调用本工具把任务委派给子 Agent 执行。子 Agent 会自己调用查询工具拿真实数据，"
                    + "并把报告返回给你，你只需复述结论。",
            readOnly = true,
            stateInjected = true)
    public String delegateReport(AgentState agentState) {
        String task = resolveTask(agentState);
        try {
            RuntimeContext ctx = RuntimeContext.builder()
                    .sessionId("sub-agent-" + UUID.randomUUID().toString().substring(0, 8))
                    .userId("demo-user")
                    .build();
            Msg reply = subAgent.call(task, ctx).block(TIMEOUT);
            return (reply == null) ? "(子 Agent 没有返回内容)" : reply.getTextContent();
        } catch (Exception e) {
            log.warn("子 Agent 执行失败", e);
            return "子 Agent 执行失败：" + e.getMessage();
        }
    }

    /**
     * 从会话上下文里取用户当前的问题作为委派任务；取不到则用兜底任务。
     */
    private String resolveTask(AgentState agentState) {
        if (agentState != null && agentState.getContext() != null) {
            for (int i = agentState.getContext().size() - 1; i >= 0; i--) {
                Msg msg = agentState.getContext().get(i);
                if (msg.getRole() == MsgRole.USER) {
                    String text = msg.getTextContent();
                    if (text != null && !text.isBlank()) {
                        return text.trim();
                    }
                }
            }
        }
        return DEFAULT_TASK;
    }
}
