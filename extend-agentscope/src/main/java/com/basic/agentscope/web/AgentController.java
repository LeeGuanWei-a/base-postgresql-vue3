package com.basic.agentscope.web;

import com.basic.common.annotation.Anonymous;
import com.basic.common.core.domain.AjaxResult;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.Version;
import io.agentscope.core.event.AgentEvent;
import io.agentscope.core.message.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * AgentScope Java 2.x 集成端到端验证接口。
 *
 * <p>Agent 由 {@code io.agentscope.spring.boot.AgentscopeAutoConfiguration}
 * 自动装配（bean 名 {@code agentscopeReActAgent}）；OpenAI 兼容模型由
 * {@code io.agentscope.spring.boot.openai.OpenAIAutoConfiguration} 自动装配。</p>
 *
 * <p>使用前请在 application.yml / application-dev.yml 配置：</p>
 * <pre>
 * agentscope:
 *   openai:
 *     base-url: https://api.deepseek.com/v1
 *     api-key: sk-xxxx
 *     model-name: deepseek-chat
 *   agent:
 *     name: basic-assistant
 *     sys-prompt: 你是企业级 Java 基础平台的智能助手
 * </pre>
 */
@RestController
@RequestMapping("/agentscope")
public class AgentController {

    /**
     * ⚠️ 必须带 {@code @Qualifier}：本模块内存在多个 {@code ReActAgent} 类型的 Bean
     * （默认助手 Agent + 各能力专属 Agent），按类型注入会歧义导致启动失败。
     */
    @Autowired
    @Qualifier("basicAssistantAgent")
    private ReActAgent agentscopeReActAgent;
    /**
     * 健康检查：确认 AgentScope Bean 已加载（匿名可访问，便于探活）
     */
    @Anonymous
    @GetMapping("/ping")
    public AjaxResult ping() {
        return AjaxResult.success("AgentScope Java v" + Version.VERSION + " ready");
    }

    /**
     * 同步调用示例 —— ReActAgent.call(String) 返回 Mono<Msg>
     */
    @GetMapping("/chat")
    public AjaxResult chat(@RequestParam(defaultValue = "用一句话介绍 Spring Boot 4") String prompt) {
        Msg reply = agentscopeReActAgent.call(prompt).block(Duration.ofSeconds(60));
        return AjaxResult.success("ok", reply == null ? "" : reply.getTextContent());
    }

    /**
     * 流式（SSE）调用示例 —— ReActAgent.streamEvents(String) 返回 Flux<AgentEvent>
     */
    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<AgentEvent> stream(@RequestParam(defaultValue = "讲个笑话") String prompt) {
        return agentscopeReActAgent.streamEvents(prompt);
    }
}
