package com.basic.agentscope.capability.tasklist;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.state.AgentState;
import io.agentscope.core.state.Task;
import io.agentscope.core.state.TaskContextState;
import io.agentscope.core.tool.Toolkit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 能力：<b>任务清单（Task List）</b>。
 *
 * <p>演示 AgentScope 的"长任务拆解与进度跟踪"能力：</p>
 * <ol>
 *   <li>{@code ReActAgent.builder().enableTaskList(true)} 会给 Agent 挂上内置的
 *       {@code todo_write} 工具（{@code io.agentscope.core.tool.builtin.TodoTools}）；</li>
 *   <li>模型把复杂需求拆成若干任务并调用该工具维护清单；</li>
 *   <li>任务清单保存在 <b>AgentState</b> 里（{@code AgentState#getTasksContext()}），
 *       可以随会话持久化，也可以通过 {@code detail()} 暴露给前端查看。</li>
 * </ol>
 *
 * <p>本能力刻意只保留 {@code todo_write} 一个工具，
 * 以便把"任务清单"这条链路单独跑通、看清每一步的产物。</p>
 */
@Configuration
public class TaskListCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "taskListCapabilityAgent";

    /** 内置任务清单工具名（AgentScope 内部 {@code TodoTools} 注册） */
    private static final String TODO_TOOL = "todo_write";

    /** 默认会话 ID：detail 接口只能读到"默认会话"的状态，聊天时带上同名 sessionId 即可看到同一份清单 */
    private static final String DEFAULT_SESSION_ID = "task-list-default";

    /** 该能力专属的 Agent（detail 里读取任务清单状态用） */
    private ReActAgent agent;

    @Bean(AGENT_BEAN)
    public ReActAgent taskListCapabilityAgent(ModelResolver modelResolver) {
        // 能力专属 Toolkit（普通对象，不注册为 Spring Bean，避免与自动装配的 Toolkit 同类型歧义）
        Toolkit toolkit = new Toolkit();

        this.agent = ReActAgent.builder()
                .name("task-list-agent")
                .description("把复杂需求拆解成可执行任务清单并跟踪进度的智能体")
                .sysPrompt("""
                        你是一名专业的项目拆解助手。
                        请把用户的需求拆解成 3~6 个可执行任务，并通过 todo_write 工具维护任务清单：
                        1. 每个任务写清楚"要做什么"，不要写空泛的目标；
                        2. todo_write 一次给出完整清单（该工具是整体覆盖，不是增量合并）；
                        3. 正在做的任务标记为 in_progress，做完的标记为 completed；
                        4. 最后用中文简要说明拆解结果。
                        """)
                .model(modelResolver.resolve())
                .toolkit(toolkit)
                .maxIters(5)
                .defaultSessionId(DEFAULT_SESSION_ID)
                .enableTaskList(true)
                .build();
        return this.agent;
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "task-list",
                "任务清单",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "Agent 把复杂需求拆成任务清单，通过内置 todo_write 工具维护，并把清单状态保存在 AgentState 里随时可查",
                "List",
                null,
                true,
                true,
                null,
                "/agentscope/capability/task-list/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "要拆解的需求", "帮我把『用户中心重构』拆成任务清单"),
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
        detail.put("taskListEnabled", true);
        detail.put("todoTool", TODO_TOOL);
        detail.put("maxIters", agent == null ? 5 : agent.getMaxIters());
        detail.put("agentName", agent == null ? "task-list-agent" : agent.getName());

        List<Map<String, Object>> tasks = new ArrayList<>();
        String note = null;
        try {
            // 注意：AgentScope 2.0.4 里无参的 getAgentState() 已废弃，
            // 这里用它的等价写法 getAgentState(sessionId, userId) 读取默认会话的状态
            String sessionId = (agent == null) ? null : agent.getDefaultSessionId();
            if (sessionId == null || sessionId.isBlank()) {
                note = "Agent 没有设置 defaultSessionId，无法定位任务清单所在会话。";
            } else {
                AgentState state = (agent == null) ? null : agent.getAgentState(sessionId, null);
                if (state == null) {
                    note = "当前 Agent 没有可用的 AgentState（getAgentState() 返回 null），任务清单暂不可读。";
                } else {
                    TaskContextState taskContext = state.getTasksContext();
                    if (taskContext == null) {
                        note = "当前会话还没有任务上下文（AgentState.getTasksContext() 为 null），"
                                + "调用一次 todo_write 之后即可看到任务清单。";
                    } else {
                        for (Task task : taskContext.getTasks()) {
                            Map<String, Object> item = new LinkedHashMap<>();
                            item.put("id", task.getId());
                            item.put("subject", task.getSubject());
                            item.put("description", task.getDescription());
                            item.put("state", task.getState() == null ? null : task.getState().name());
                            item.put("owner", task.getOwner());
                            item.put("createdAt", task.getCreatedAt());
                            tasks.add(item);
                        }
                        note = "共 " + tasks.size() + " 个任务（状态来自 AgentState.getTasksContext()，会话 " + sessionId + "）。";
                    }
                }
            }
        } catch (Exception e) {
            note = "读取任务清单状态失败：" + e.getMessage();
        }
        detail.put("sessionId", DEFAULT_SESSION_ID);
        detail.put("taskList", tasks);
        detail.put("taskCount", tasks.size());
        detail.put("note", note);
        detail.put("mockNote", "Mock 模型只能合成字符串型参数，todo_write 的 todos 是数组型参数，"
                + "因此 Mock 模式下工具调用会返回参数校验错误、清单保持为空；接入真实模型后即可写入真实任务。");
        return detail;
    }
}
