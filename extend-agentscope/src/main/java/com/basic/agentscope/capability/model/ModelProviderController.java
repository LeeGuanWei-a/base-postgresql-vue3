package com.basic.agentscope.capability.model;

import com.basic.agentscope.config.ModelResolver;
import com.basic.agentscope.dto.AgentChatRequest;
import com.basic.common.core.domain.AjaxResult;
import io.agentscope.core.message.ContentBlock;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import io.agentscope.extensions.model.openai.OpenAIChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 多厂商模型控制器 —— 演示"同一套代码切换不同厂商的 LLM"。
 *
 * <p>两个端点：</p>
 * <ul>
 *   <li>{@code GET  /agentscope/model/providers}：列出可用厂商，以及切换到该厂商需要改哪几个配置项；</li>
 *   <li>{@code POST /agentscope/model/chat}：用指定厂商的 {@code base-url} / {@code model-name} 走一次对话。</li>
 * </ul>
 *
 * <p>⚠️ 没有真实 API Key 时（{@code agentscope.mock.enabled=true}），请求仍然走
 * <b>Mock 模型</b>，响应里会明确标注 {@code mock=true}，
 * 不会真的访问外部厂商——避免演示时误发请求、产生费用。</p>
 */
@RestController
@RequestMapping("/agentscope/model")
public class ModelProviderController {

    /** 单次对话超时 */
    private static final Duration CALL_TIMEOUT = Duration.ofSeconds(60);

    @Autowired
    private ModelProviderRegistry registry;

    @Autowired
    private ModelResolver modelResolver;

    /**
     * 可用厂商清单 + 切换配置项说明。
     */
    @GetMapping("/providers")
    public AjaxResult providers() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("protocol", "OpenAI 兼容协议：POST {base-url}/chat/completions");
        data.put("configItems", List.of(
                "agentscope.openai.base-url",
                "agentscope.openai.api-key",
                "agentscope.openai.model-name"));
        data.put("mock", modelResolver.isMock());
        data.put("currentModel", currentModelName());
        data.put("providers", registry.toDetailList());
        return AjaxResult.success(data);
    }

    /**
     * 用指定厂商走一次对话。
     *
     * <p>请求体：{@code prompt} 必填；{@code options} 可带
     * {@code provider} / {@code baseUrl} / {@code modelName} / {@code apiKey}。</p>
     */
    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody(required = false) AgentChatRequest request) {
        if (request == null || request.getPrompt() == null || request.getPrompt().isBlank()) {
            return AjaxResult.error("prompt 不能为空");
        }
        String providerId = request.option("provider", "openai");
        ModelProviderRegistry.ProviderSpec spec = registry.get(providerId);
        String baseUrl = request.option("baseUrl", spec.baseUrl());
        String modelName = request.option("modelName", spec.modelName());
        String apiKey = request.option("apiKey", registry.resolveApiKey(spec));

        Model model;
        boolean mock;
        if (modelResolver.isMock()) {
            // 无真实 Key：统一走 Mock 模型，不访问外部厂商
            model = modelResolver.resolve();
            mock = true;
        } else if (apiKey == null || apiKey.isBlank()) {
            if ("openai".equalsIgnoreCase(spec.id())) {
                // 默认厂商：直接用 starter 已装配的真实模型
                model = modelResolver.resolve();
                mock = false;
            } else {
                return AjaxResult.error("厂商[" + spec.name() + "]缺少 API Key，"
                        + "请配置环境变量 " + spec.apiKeyEnv() + " 或在 options.apiKey 里传入");
            }
        } else {
            // 其它厂商：按 OpenAI 兼容协议就地构造一个模型（普通对象，不注册为 Spring Bean）
            model = OpenAIChatModel.builder()
                    .baseUrl(baseUrl)
                    .modelName(modelName)
                    .apiKey(apiKey)
                    .stream(false)
                    .build();
            mock = false;
        }

        long start = System.currentTimeMillis();
        String text;
        try {
            List<Msg> messages = List.of(
                    Msg.builder().role(MsgRole.USER).textContent(request.getPrompt()).build());
            text = model.stream(messages, List.of(), GenerateOptions.builder().build())
                    .map(ModelProviderController::responseText)
                    .filter(s -> s != null && !s.isEmpty())
                    .collectList()
                    .map(list -> String.join("", list))
                    .block(CALL_TIMEOUT);
        } catch (Exception e) {
            return AjaxResult.error("调用厂商[" + spec.name() + "]失败：" + rootMessage(e));
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("provider", spec.id());
        data.put("providerName", spec.name());
        data.put("baseUrl", baseUrl);
        data.put("modelName", modelName);
        data.put("actualModel", model.getModelName());
        data.put("mock", mock);
        data.put("mockHint", mock ? "当前为 Mock 模式，未真正访问该厂商；配置 API Key 后自动切换为真实调用" : null);
        data.put("configItems", spec.configItems());
        data.put("text", text);
        data.put("elapsedMs", System.currentTimeMillis() - start);
        return AjaxResult.success(data);
    }

    // ------------------------------------------------------------------ 内部方法

    /**
     * 把一次模型响应里的文本内容拼起来。
     */
    private static String responseText(ChatResponse response) {
        if (response == null || response.getContent() == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (ContentBlock block : response.getContent()) {
            if (block instanceof TextBlock textBlock) {
                sb.append(textBlock.getText() == null ? "" : textBlock.getText());
            }
        }
        return sb.toString();
    }

    private String currentModelName() {
        try {
            Model model = modelResolver.resolve();
            return model == null ? "-" : model.getModelName();
        } catch (Exception e) {
            return "-";
        }
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
