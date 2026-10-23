package com.basic.agentscope.capability.interrupt;

import com.basic.agentscope.dto.AgentChatRequest;
import com.basic.agentscope.dto.AgentChatResponse;
import com.basic.common.core.domain.AjaxResult;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 中断运行能力专属控制器 —— 路径 {@code /agentscope/interrupt}。
 *
 * <p>三个接口：</p>
 * <ol>
 *   <li>{@code POST /agentscope/interrupt/chat} —— 发起一次（可能很慢的）对话，
 *       期间该会话处于"运行中"状态，可以被 stop 打断；</li>
 *   <li>{@code POST /agentscope/interrupt/stop?sessionId=xxx} —— 打断该会话正在运行的调用；</li>
 *   <li>{@code GET /agentscope/interrupt/status?sessionId=xxx} —— 查询运行状态。</li>
 * </ol>
 */
@RestController
@RequestMapping("/agentscope/interrupt")
public class InterruptCapabilityController {

    /** 同步调用的超时 */
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(120);

    /**
     * 该能力专属 Agent。
     *
     * <p>⚠️ 本模块存在多个 {@code ReActAgent} 类型的 Bean，
     * 按类型注入会歧义 → 启动失败，因此必须带 {@code @Qualifier}。</p>
     */
    @Autowired
    @Qualifier(InterruptCapability.AGENT_BEAN)
    private ReActAgent agent;

    @Autowired
    private InterruptCapability interruptCapability;

    /**
     * 发起一次对话（运行期间可被 stop 打断）。
     */
    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody(required = false) AgentChatRequest request) {
        if (request == null || request.getPrompt() == null || request.getPrompt().isBlank()) {
            return AjaxResult.error("prompt 不能为空");
        }
        String sessionId = (request.getSessionId() == null || request.getSessionId().isBlank())
                ? InterruptCapability.DEFAULT_SESSION_ID
                : request.getSessionId().trim();

        // 可选：调整慢任务耗时（options.seconds）
        if (request.getOptions() != null && request.getOptions().get("seconds") != null) {
            try {
                int seconds = Integer.parseInt(String.valueOf(request.getOptions().get("seconds")));
                if (interruptCapability.interruptTools() != null) {
                    interruptCapability.interruptTools().setDelaySeconds(seconds);
                }
            } catch (NumberFormatException ignored) {
                // 忽略非法值，用默认耗时
            }
        }

        interruptCapability.registerSession(sessionId, agent);
        interruptCapability.markRunning(sessionId, true);

        AgentChatResponse response = new AgentChatResponse();
        response.setCapabilityId("interrupt");
        response.setSessionId(sessionId);
        response.setAgentName(agent.getName());
        response.setModelName(agent.getModel() == null ? "-" : agent.getModel().getModelName());

        long start = System.currentTimeMillis();
        try {
            RuntimeContext ctx = RuntimeContext.builder()
                    .sessionId(sessionId)
                    .userId(InterruptCapability.DEMO_USER_ID)
                    .build();
            Msg reply = agent.call(request.getPrompt(), ctx).block(DEFAULT_TIMEOUT);
            String text = reply == null ? "" : reply.getTextContent();
            response.setText(text);
            interruptCapability.recordResult(sessionId, text, null);
        } catch (Exception e) {
            // 被中断 / 超时 / 其它异常
            String message = rootMessage(e);
            response.setText("任务已被中断或异常终止：" + message);
            interruptCapability.recordResult(sessionId, null, message);
        } finally {
            interruptCapability.markRunning(sessionId, false);
        }
        response.setElapsedMs(System.currentTimeMillis() - start);
        response.getExtra().put("interruptCount", interruptCapability.interruptCount(sessionId));
        response.getExtra().put("running", false);
        return AjaxResult.success(response);
    }

    /**
     * 打断某个会话正在运行的调用。
     */
    @PostMapping("/stop")
    public AjaxResult stop(@RequestParam("sessionId") String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return AjaxResult.error("sessionId 不能为空");
        }
        boolean running = interruptCapability.isRunning(sessionId);
        boolean dispatched = interruptCapability.interrupt(sessionId);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sessionId", sessionId);
        data.put("wasRunning", running);
        data.put("interrupted", dispatched);
        data.put("interruptCount", interruptCapability.interruptCount(sessionId));
        data.put("message", dispatched
                ? "已向该会话下发中断指令" + (running ? "（任务正在运行）" : "（当前没有正在运行的任务）")
                : "未找到该会话的受控 Agent，无法下发中断指令");
        return dispatched ? AjaxResult.success(data) : AjaxResult.error("中断失败：未找到该会话", data);
    }

    /**
     * 查询某个会话的运行状态。
     */
    @GetMapping("/status")
    public AjaxResult status(@RequestParam("sessionId") String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return AjaxResult.error("sessionId 不能为空");
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("sessionId", sessionId);
        data.put("running", interruptCapability.isRunning(sessionId));
        data.put("controlled", interruptCapability.agentOf(sessionId) != null);
        data.put("interruptCount", interruptCapability.interruptCount(sessionId));
        data.put("lastResult", interruptCapability.lastResult(sessionId));
        data.put("lastError", interruptCapability.lastError(sessionId));
        return AjaxResult.success(data);
    }

    /**
     * 取最底层的异常信息。
     */
    private String rootMessage(Throwable e) {
        Throwable cur = e;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        String msg = cur.getMessage();
        return (msg == null || msg.isBlank()) ? cur.getClass().getSimpleName() : msg;
    }
}
