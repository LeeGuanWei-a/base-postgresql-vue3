package com.basic.agentscope.capability.structured;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import com.basic.agentscope.config.ModelResolver;
import com.basic.agentscope.dto.AgentAnswer;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.tool.Toolkit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 能力：<b>结构化输出（Structured Output）</b>。
 *
 * <p>演示 AgentScope 的"让模型按指定 JSON Schema 作答"能力：</p>
 * <ol>
 *   <li>调用 {@code agent.call(prompt, Target.class)} 时，框架会根据目标类生成 JSON Schema，</li>
 *   <li>模型按 Schema 产出 JSON，框架负责把它反序列化成 {@link AgentAnswer} 实例；</li>
 *   <li>业务侧既能拿到模型的原始文本（{@code text}），也能拿到强类型对象（{@code structured}）。</li>
 * </ol>
 *
 * <p>该能力有<b>专属 Controller</b>：{@link StructuredController}
 * （{@code POST /agentscope/structured/call}），
 * 因为通用接口 {@code /agentscope/capability/{id}/chat} 只会返回纯文本，无法带出结构化对象。</p>
 */
@Configuration
public class StructuredCapability implements CapabilityProvider {

    public static final String AGENT_BEAN = "structuredCapabilityAgent";

    /** 目标 DTO 的全限定名（detail 里展示给前端） */
    private static final String TARGET_TYPE = "com.basic.agentscope.dto.AgentAnswer";

    /**
     * 目标 DTO 对应的 JSON Schema。
     *
     * <p>写进 sysPrompt 的目的有两个：</p>
     * <ol>
     *   <li>真实模型：把结构要求说清楚，模型更容易严格按 Schema 作答；</li>
     *   <li>Mock 模型：{@code MockChatModel} 会在上下文里发现 JSON Schema 并直接合成合法 JSON，
     *       于是这条链路在无 Key 环境下也能看到真正的结构化输出。</li>
     * </ol>
     */
    private static final String TARGET_JSON_SCHEMA = "{\"type\":\"object\",\"properties\":{"
            + "\"title\":{\"type\":\"string\"},"
            + "\"summary\":{\"type\":\"string\"},"
            + "\"tags\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}},"
            + "\"confidence\":{\"type\":\"integer\"},"
            + "\"actions\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}},"
            + "\"required\":[\"title\",\"summary\",\"tags\",\"confidence\",\"actions\"]}";

    /** 结构化输出专用的聊天地址 */
    public static final String CHAT_ENDPOINT = "/agentscope/structured/call";

    @Bean(AGENT_BEAN)
    public ReActAgent structuredCapabilityAgent(ModelResolver modelResolver) {
        // 能力专属 Toolkit（普通对象，不注册为 Spring Bean，避免与自动装配的 Toolkit 同类型歧义）
        Toolkit toolkit = new Toolkit();

        return ReActAgent.builder()
                .name("structured-agent")
                .description("按预定义 JSON Schema 输出结构化答案的智能体")
                .sysPrompt("""
                        你是一名严谨的信息整理助手。
                        你会收到一个"目标 JSON 结构"，必须严格按照该结构输出，不要有多余字段，也不要有额外解释文字。
                        输出要求：
                        1. title：一句话结论；
                        2. summary：结论的依据与展开说明；
                        3. tags：3~5 个关键词标签；
                        4. confidence：0-100 的整数置信度；
                        5. actions：建议的下一步动作清单。
                        你的最终回答必须是一个 JSON 对象，且严格符合下面这个 JSON Schema（Schema 之后不要再输出任何内容）：
                        """ + TARGET_JSON_SCHEMA)
                .model(modelResolver.resolve())
                .toolkit(toolkit)
                .maxIters(3)
                .build();
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "structured",
                "结构化输出",
                CapabilityCategory.CORE.getCode(),
                CapabilityCategory.CORE.getLabel(),
                "让模型按预定义 JSON Schema 作答，AgentScope 自动反序列化成 JavaBean（标题/摘要/标签/置信度/后续动作）",
                "Document",
                null,
                true,
                false,
                CHAT_ENDPOINT,
                "/agentscope/capability/structured/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "要结构化的问题", "总结一下系统里有哪些用户，并给出置信度"),
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
        detail.put("targetType", TARGET_TYPE);
        detail.put("jsonSchema", TARGET_JSON_SCHEMA);
        detail.put("chatEndpoint", CHAT_ENDPOINT);
        detail.put("fields", describeFields());
        detail.put("fieldCount", describeFields().size());
        return detail;
    }

    /**
     * 目标 DTO 的字段清单（字段名 + 类型 + 含义），供前端渲染表单/说明。
     */
    private List<Map<String, Object>> describeFields() {
        List<Map<String, Object>> fields = new ArrayList<>();
        fields.add(field("title", "String", "结论标题，一句话概括本次回答"));
        fields.add(field("summary", "String", "摘要正文，说明依据与展开结论"));
        fields.add(field("tags", "List<String>", "关键词标签（建议 3~5 个）"));
        fields.add(field("confidence", "int", "置信度 0-100 的整数，数值越大越可信"));
        fields.add(field("actions", "List<String>", "建议的后续动作清单"));
        return fields;
    }

    private Map<String, Object> field(String name, String type, String meaning) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("type", type);
        item.put("meaning", meaning);
        return item;
    }
}
