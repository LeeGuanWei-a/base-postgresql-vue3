package com.basic.agentscope.capability.structured;

import com.basic.agentscope.config.ModelResolver;
import com.basic.agentscope.dto.AgentAnswer;
import com.basic.agentscope.dto.AgentChatRequest;
import com.basic.common.core.domain.AjaxResult;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 结构化输出专属控制器。
 *
 * <p>与通用能力控制器（{@code /agentscope/capability/{id}/chat}）的区别在于：
 * 这里调用的是 {@code agent.call(prompt, Target.class)} —— <b>{@code Class<?>} 重载</b>，
 * 框架会按目标类生成 JSON Schema 并把模型输出反序列化成强类型对象，
 * 因此响应里除了模型原文（{@code text}），还能给出结构化结果（{@code structured}）。</p>
 *
 * <p>⚠️ 注入 {@code ReActAgent} 必须带 {@code @Qualifier}：
 * 本模块存在多个同类型 Bean，按类型注入会歧义导致启动失败。</p>
 */
@RestController
@RequestMapping("/agentscope/structured")
public class StructuredController {

    private static final Logger log = LoggerFactory.getLogger(StructuredController.class);

    /** 同步调用的默认超时 */
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(120);

    /** 结构化输出能力专属的 Agent */
    private final ReActAgent agent;

    private final ModelResolver modelResolver;

    public StructuredController(@Qualifier(StructuredCapability.AGENT_BEAN) ReActAgent agent,
                               ModelResolver modelResolver) {
        this.agent = agent;
        this.modelResolver = modelResolver;
    }

    /**
     * 结构化调用：返回"模型原文 + 结构化对象"。
     *
     * @param request 请求体（prompt 必填，sessionId 可选）
     * @return {@code {code,msg,data:{text,structured,...},text,structured}}
     */
    @PostMapping("/call")
    public AjaxResult call(@RequestBody(required = false) AgentChatRequest request) {
        if (request == null || request.getPrompt() == null || request.getPrompt().isBlank()) {
            return AjaxResult.error("prompt 不能为空");
        }

        long start = System.currentTimeMillis();
        String sessionId = (request.getSessionId() == null || request.getSessionId().isBlank())
                ? "sess-" + UUID.randomUUID().toString().substring(0, 8)
                : request.getSessionId();

        String text;
        Object structured;
        try {
            // 注意：这里必须用 Class<?> 重载（结构化输出），而不是 call(prompt, RuntimeContext)
            Msg reply = agent.call(request.getPrompt(), AgentAnswer.class).block(DEFAULT_TIMEOUT);
            text = (reply == null) ? "" : reply.getTextContent();
            structured = extractStructured(reply, text);
        } catch (Exception e) {
            log.warn("结构化输出调用失败", e);
            return AjaxResult.error("结构化输出失败：" + rootMessage(e));
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("text", text);
        data.put("structured", structured);
        data.put("sessionId", sessionId);
        data.put("agentName", agent.getName());
        data.put("modelName", agent.getModel() == null ? "-" : agent.getModel().getModelName());
        data.put("mock", modelResolver.isMock());
        data.put("elapsedMs", System.currentTimeMillis() - start);

        // text / structured 同时放在顶层，前端两种取法都能用
        AjaxResult result = AjaxResult.success(data);
        result.put("text", text);
        result.put("structured", structured);
        return result;
    }

    /**
     * 取出结构化结果。
     *
     * <p>正常情况下 {@code reply.getStructuredData(AgentAnswer.class)} 会返回反序列化后的对象；
     * 若模型没有按 Schema 作答（框架没能解析出结构化数据），该方法会抛
     * {@code IllegalStateException}，此时退化为"把模型输出的 JSON 文本原样塞进去"。</p>
     */
    private Object extractStructured(Msg reply, String text) {
        if (reply == null) {
            return null;
        }
        try {
            if (reply.hasStructuredData()) {
                AgentAnswer answer = reply.getStructuredData(AgentAnswer.class);
                if (answer != null) {
                    return answer;
                }
            }
        } catch (Exception e) {
            log.warn("解析结构化输出失败，回退为模型原文", e);
        }
        return (text == null || text.isBlank()) ? null : text;
    }

    private String rootMessage(Throwable e) {
        Throwable cur = e;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        String msg = cur.getMessage();
        return (msg == null || msg.isBlank()) ? cur.getClass().getSimpleName() : msg;
    }
}
