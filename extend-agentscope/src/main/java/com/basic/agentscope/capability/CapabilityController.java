package com.basic.agentscope.capability;

import com.basic.agentscope.config.ModelResolver;
import com.basic.agentscope.dto.AgentChatRequest;
import com.basic.agentscope.dto.AgentChatResponse;
import com.basic.common.core.domain.AjaxResult;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.message.ContentBlock;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.message.ToolResultBlock;
import io.agentscope.core.message.ToolUseBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 能力通用控制器 —— 所有"聊天型"能力共用的入口。
 *
 * <p>路径规则：{@code /agentscope/capability/{id}/chat|stream|detail}。</p>
 *
 * <p>设计意图：避免为每个能力都写一个几乎一样的 Controller。
 * 特殊能力（结构化输出、中断等）可以有自己的 Controller，
 * 同时仍通过 {@link CapabilityInfo#chatEndpoint()} 告诉前端该走哪个地址。</p>
 */
@RestController
@RequestMapping("/agentscope/capability")
public class CapabilityController {

    private static final Logger log = LoggerFactory.getLogger(CapabilityController.class);

    /** 同步调用的默认超时 */
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(120);

    @Autowired
    private CapabilityRegistry registry;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ModelResolver modelResolver;

    /**
     * 能力清单（前端左侧导航的数据源）。
     */
    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success(registry.list());
    }

    /**
     * 同步对话（POST，便于传复杂参数）。
     */
    @PostMapping("/{id}/chat")
    public AjaxResult chat(@PathVariable("id") String id,
                           @RequestBody(required = false) AgentChatRequest request) {
        return doChat(id, request);
    }

    /**
     * 同步对话（GET，便于浏览器直接点开）。
     */
    @GetMapping("/{id}/chat")
    public AjaxResult chatGet(@PathVariable("id") String id,
                              @RequestParam("prompt") String prompt,
                              @RequestParam(value = "sessionId", required = false) String sessionId) {
        AgentChatRequest request = new AgentChatRequest();
        request.setPrompt(prompt);
        request.setSessionId(sessionId);
        return doChat(id, request);
    }

    /**
     * SSE 流式对话。
     */
    @GetMapping(value = "/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AgentEvent> stream(@PathVariable("id") String id,
                                   @RequestParam("prompt") String prompt,
                                   @RequestParam(value = "sessionId", required = false) String sessionId) {
        CapabilityProvider provider = registry.getProvider(id);
        ReActAgent agent = resolveAgent(provider);
        if (agent == null) {
            return Flux.error(new IllegalStateException("能力不可用：" + id));
        }
        RuntimeContext ctx = buildRuntimeContext(sessionId);
        return agent.streamEvents(prompt, ctx);
    }

    /**
     * 能力详情（工具清单、会话列表、依赖状态等）。
     */
    @GetMapping("/{id}/detail")
    public AjaxResult detail(@PathVariable("id") String id) {
        CapabilityProvider provider = registry.getProvider(id);
        if (provider == null) {
            return registry.notFound(id);
        }
        Map<String, Object> detail = provider.detail();
        return AjaxResult.success(detail == null ? new LinkedHashMap<>() : detail);
    }

    // ------------------------------------------------------------------ 内部方法

    private AjaxResult doChat(String id, AgentChatRequest request) {
        CapabilityProvider provider = registry.getProvider(id);
        if (provider == null) {
            return registry.notFound(id);
        }
        ReActAgent agent = resolveAgent(provider);
        if (agent == null) {
            return AjaxResult.error("能力不可用（缺少 Agent Bean）：" + id);
        }
        if (request == null || request.getPrompt() == null || request.getPrompt().isBlank()) {
            return AjaxResult.error("prompt 不能为空");
        }

        long start = System.currentTimeMillis();
        String sessionId = (request.getSessionId() == null || request.getSessionId().isBlank())
                ? "sess-" + UUID.randomUUID().toString().substring(0, 8)
                : request.getSessionId();

        AgentChatResponse response = new AgentChatResponse();
        response.setCapabilityId(id);
        response.setSessionId(sessionId);

        try {
            RuntimeContext ctx = buildRuntimeContext(sessionId);
            Msg reply = agent.call(request.getPrompt(), ctx).block(DEFAULT_TIMEOUT);
            response.setText(reply == null ? "" : reply.getTextContent());
            response.setToolCalls(extractToolCalls(reply));
            response.setAgentName(agent.getName());
            response.setModelName(agent.getModel() == null ? "-" : agent.getModel().getModelName());
            response.setMock(modelResolver.isMock());
        } catch (Exception e) {
            log.warn("能力[{}]对话失败", id, e);
            return AjaxResult.error("对话失败：" + rootMessage(e));
        }
        response.setElapsedMs(System.currentTimeMillis() - start);
        return AjaxResult.success(response);
    }

    /**
     * 按 bean 名取 Agent —— 避免按类型注入导致歧义。
     */
    private ReActAgent resolveAgent(CapabilityProvider provider) {
        if (provider == null) {
            return null;
        }
        String beanName = provider.agentBeanName();
        if (beanName == null) {
            return null;
        }
        try {
            return applicationContext.getBean(beanName, ReActAgent.class);
        } catch (Exception e) {
            log.warn("获取能力 Agent Bean 失败: {}", beanName, e);
            return null;
        }
    }

    /**
     * 构造运行时上下文（承载 sessionId / userId）。
     */
    private RuntimeContext buildRuntimeContext(String sessionId) {
        return RuntimeContext.builder()
                .sessionId(sessionId)
                .userId("demo-user")
                .build();
    }

    /**
     * 从回复消息里抽取工具调用轨迹。
     *
     * <p>把 {@code ToolUseBlock}（调用）与 {@code ToolResultBlock}（返回）按 id 合并成一条记录，
     * 前端表格可以直接展示 name / input / output 三列。</p>
     */
    private List<Map<String, Object>> extractToolCalls(Msg reply) {
        List<Map<String, Object>> calls = new ArrayList<>();
        if (reply == null) {
            return calls;
        }
        Map<String, Map<String, Object>> byId = new LinkedHashMap<>();
        for (ToolUseBlock use : reply.getContentBlocks(ToolUseBlock.class)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("tool", use.getName());
            item.put("name", use.getName());
            item.put("input", use.getInput());
            item.put("output", null);
            item.put("type", "call");
            calls.add(item);
            if (use.getId() != null) {
                byId.put(use.getId(), item);
            }
        }
        for (ToolResultBlock result : reply.getContentBlocks(ToolResultBlock.class)) {
            String output = extractOutputText(result.getOutput());
            Map<String, Object> merged = (result.getId() == null) ? null : byId.get(result.getId());
            if (merged != null) {
                merged.put("output", output);
                merged.put("state", result.getState() == null ? null : result.getState().name());
            } else {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("tool", result.getName());
                item.put("name", result.getName());
                item.put("input", null);
                item.put("output", output);
                item.put("type", "result");
                item.put("state", result.getState() == null ? null : result.getState().name());
                calls.add(item);
            }
        }
        return calls;
    }

    /**
     * 把工具返回的 ContentBlock 列表转成可读文本。
     */
    private String extractOutputText(List<ContentBlock> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (ContentBlock block : blocks) {
            if (block instanceof TextBlock text) {
                sb.append(text.getText());
            } else {
                sb.append(block);
            }
        }
        return sb.toString();
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
