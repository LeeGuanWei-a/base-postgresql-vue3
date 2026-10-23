package com.basic.agentscope.mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.agentscope.core.message.ContentBlock;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.message.ToolResultBlock;
import io.agentscope.core.message.ToolUseBlock;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.ToolSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Mock 模型 —— <b>没有真实 LLM API Key 也能把整条链路跑通</b>。
 *
 * <p>它实现了 AgentScope 的 {@link Model} SPI，行为刻意贴近真实模型的"两段式"：</p>
 * <ol>
 *   <li>第一次被调用：如果本次对话挂了工具，就<b>发起一次真实的工具调用</b>
 *       （返回 {@link ToolUseBlock}），从而驱动 AgentScope 的 ReAct 循环
 *       → 工具执行 → 工具结果回灌 → 第二次模型调用；</li>
 *   <li>第二次被调用：把工具结果汇总成最终文本回答。</li>
 * </ol>
 *
 * <p>这让「工具注册 → 模型决定调用 → 工具真实执行 → 结果回灌 → 最终回答」这条链路
 * 在没有任何外部依赖的情况下 100% 可验证，是本分支"先把链路跑通"策略的基石。</p>
 *
 * <p>结构化输出：若对话上下文里出现 JSON Schema 片段，会按 schema 合成一个合法 JSON 对象返回。</p>
 *
 * <p>⚠️ <b>本类不是 Spring Bean</b>。原因：它实现了 {@link Model} 接口，若注册为 Bean，
 * 会与 starter 自动装配的 {@code openAIChatModel} 形成同类型 Bean 歧义，
 * 导致 {@code AgentscopeAutoConfiguration} 按类型注入 {@code Model} 时启动失败。
 * 实例由 {@code ModelResolver} 持有。</p>
 */
public class MockChatModel implements Model {

    private static final Logger log = LoggerFactory.getLogger(MockChatModel.class);

    public static final String MODEL_NAME = "mock-agentscope";

    /**
     * 工具名 → 触发关键词。Mock 模型据此判断"用户这个问题该调哪个工具"。
     *
     * <p>新加工具时在这里补一条即可；没有配置的工具会退化为"描述匹配"策略。</p>
     */
    private static final Map<String, String[]> TOOL_KEYWORDS = Map.of(
            "query_sys_user", new String[]{"用户", "账号", "管理员", "user"},
            "query_dict_data", new String[]{"字典", "枚举", "dict"},
            "query_oper_log", new String[]{"日志", "操作记录", "oper", "log"},
            "query_sys_role", new String[]{"角色", "权限划分", "role"},
            "count_table_rows", new String[]{"多少", "数量", "行数", "统计", "count"},
            "current_datetime", new String[]{"几点", "当前时间", "今天", "日期", "time", "date"},
            "subtract_numbers", new String[]{"减", "差值", "subtract"},
            "echo_text", new String[]{"回显", "echo", "重复"});

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final AtomicLong seq = new AtomicLong();

    @Override
    public String getModelName() {
        return MODEL_NAME;
    }

    @Override
    public Flux<ChatResponse> stream(List<Msg> msgs, List<ToolSchema> tools, GenerateOptions options) {
        try {
            return Flux.just(respond(msgs, tools));
        } catch (Exception e) {
            log.warn("MockChatModel 生成响应失败", e);
            return Flux.just(textResponse("Mock 模型异常：" + e.getMessage()));
        }
    }

    private ChatResponse respond(List<Msg> msgs, List<ToolSchema> tools) {
        String userText = lastUserText(msgs);

        // 1) 结构化输出：上下文里带 JSON Schema 就直接合成 JSON
        String json = synthesizeStructuredJson(msgs, userText);
        if (json != null) {
            return ChatResponse.builder()
                    .id("mock-resp-" + seq.incrementAndGet())
                    .content(List.of(TextBlock.builder().text(json).build()))
                    .finishReason("stop")
                    .build();
        }

        // 2) 首次调用且挂了工具 → 发起真实工具调用，驱动 ReAct 循环
        boolean toolResultAlreadyPresent = hasToolResult(msgs);
        if (!toolResultAlreadyPresent && tools != null && !tools.isEmpty()) {
            ToolSchema schema = selectTool(tools, userText);
            ToolUseBlock use = ToolUseBlock.builder()
                    .id("mock-call-" + seq.incrementAndGet())
                    .name(schema.getName())
                    .input(generateArgs(schema, userText))
                    .build();
            return ChatResponse.builder()
                    .id("mock-resp-" + seq.get())
                    .content(List.of(use))
                    .finishReason("tool_calls")
                    .build();
        }

        // 3) 收尾：汇总工具结果
        return textResponse(buildFinalAnswer(userText, msgs));
    }

    private ChatResponse textResponse(String text) {
        return ChatResponse.builder()
                .id("mock-resp-" + seq.incrementAndGet())
                .content(List.of(TextBlock.builder().text(text).build()))
                .finishReason("stop")
                .build();
    }

    /**
     * 找出最后一次用户输入。
     */
    private String lastUserText(List<Msg> msgs) {
        if (msgs == null) {
            return "";
        }
        for (int i = msgs.size() - 1; i >= 0; i--) {
            Msg msg = msgs.get(i);
            if (msg.getRole() == MsgRole.USER) {
                return msg.getTextContent();
            }
        }
        return "";
    }

    /**
     * 判断上下文里是否已经有工具结果（说明是 ReAct 的第二轮）。
     */
    private boolean hasToolResult(List<Msg> msgs) {
        if (msgs == null) {
            return false;
        }
        for (Msg msg : msgs) {
            if (msg.hasContentBlocks(ToolResultBlock.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 依据工具的 JSON Schema 生成一组"像样"的入参。
     *
     * <p>为了让演示能真正查到数据，对常见参数名做了语义化默认值：</p>
     * <ul>
     *   <li>{@code keyword} / {@code userName} / {@code username} / {@code name} → {@code admin}</li>
     *   <li>{@code pageNum} → {@code 1}，{@code pageSize} → {@code 10}</li>
     *   <li>其余 string → {@code mock}</li>
     * </ul>
     */
    /**
     * 依据工具的 JSON Schema 生成一组"像样"的入参。
     *
     * <p>为了让演示能真正查到数据，对常见参数名做了语义化默认值：</p>
     * <ul>
     *   <li>{@code keyword} / {@code userName} / {@code username} / {@code name} → {@code admin}</li>
     *   <li>{@code pageNum} → {@code 1}，{@code pageSize} → {@code 10}</li>
     *   <li>其余 string → {@code mock}</li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> generateArgs(ToolSchema schema, String userText) {
        Map<String, Object> args = new LinkedHashMap<>();
        JsonNode params = null;
        try {
            if (schema.getParameters() != null) {
                params = MAPPER.readTree(MAPPER.writeValueAsString(schema.getParameters()));
            }
        } catch (Exception e) {
            log.debug("解析工具 schema 失败: {}", e.getMessage());
        }
        if (params == null || !params.has("properties")) {
            return args;
        }
        JsonNode properties = params.get("properties");
        properties.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode value = entry.getValue();
            String type = value.path("type").asText("string");
            args.put(key, defaultValueFor(key, type, userText));
        });
        return args;
    }

    private Object defaultValueFor(String key, String type, String userText) {
        String lower = key.toLowerCase();
        if ("integer".equals(type) || "number".equals(type)) {
            if (lower.contains("page") && lower.contains("size")) {
                return 10;
            }
            if (lower.contains("page") || lower.contains("num") || lower.contains("limit")) {
                return 1;
            }
            return 1;
        }
        if ("boolean".equals(type)) {
            return Boolean.FALSE;
        }
        if ("array".equals(type) || "object".equals(type)) {
            return null;
        }
        if (lower.contains("keyword") || lower.contains("username") || lower.contains("user_name")) {
            return "admin";
        }
        if (lower.contains("name")) {
            return "admin";
        }
        if (lower.contains("table")) {
            return "sys_user";
        }
        return "mock";
    }

    /**
     * 从候选工具里挑一个"最像"会被问到的工具。
     *
     * <p>策略（按优先级）：</p>
     * <ol>
     *   <li><b>关键词表</b>：工具名 → 触发关键词，命中即命中（最可靠）；</li>
     *   <li><b>描述匹配</b>：把用户问题做 2 字滑窗，看是否出现在工具描述里；</li>
     *   <li>都没有则取第一个工具。</li>
     * </ol>
     */
    private ToolSchema selectTool(List<ToolSchema> tools, String userText) {
        if (tools == null || tools.isEmpty()) {
            return null;
        }
        String question = userText == null ? "" : userText.toLowerCase();

        // 1) 关键词表
        for (ToolSchema tool : tools) {
            String[] keys = TOOL_KEYWORDS.get(tool.getName());
            if (keys == null) {
                continue;
            }
            for (String k : keys) {
                if (question.contains(k.toLowerCase())) {
                    log.debug("Mock 选择工具 {}（命中关键词 {}）", tool.getName(), k);
                    return tool;
                }
            }
        }

        // 2) 描述匹配
        ToolSchema best = tools.get(0);
        int bestScore = -1;
        for (ToolSchema tool : tools) {
            String haystack = ((tool.getName() == null ? "" : tool.getName()) + " "
                    + (tool.getDescription() == null ? "" : tool.getDescription())).toLowerCase();
            int score = 0;
            for (int i = 0; i + 2 <= question.length(); i++) {
                String w = question.substring(i, i + 2);
                if (haystack.contains(w)) {
                    score++;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                best = tool;
            }
        }
        return best;
    }

    /**
     * 若上下文里出现 JSON Schema，则按 schema 合成一个合法 JSON 对象。
     */
    private String synthesizeStructuredJson(List<Msg> msgs, String userText) {
        String schemaText = findJsonSchema(msgs);
        if (schemaText == null) {
            return null;
        }
        try {
            JsonNode schema = MAPPER.readTree(schemaText);
            JsonNode props = schema.path("properties");
            Map<String, Object> obj = new LinkedHashMap<>();
            if (props.isObject()) {
                props.fields().forEachRemaining(entry -> {
                    String key = entry.getKey();
                    JsonNode def = entry.getValue();
                    String type = def.path("type").asText("string");
                    obj.put(key, defaultValueFor(key, type, userText));
                });
            }
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            log.debug("合成结构化输出失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 在所有消息文本里找 JSON Schema 片段。
     */
    private String findJsonSchema(List<Msg> msgs) {
        if (msgs == null) {
            return null;
        }
        for (Msg msg : msgs) {
            for (ContentBlock block : msg.getContent()) {
                String text = block instanceof TextBlock tb ? tb.getText() : null;
                if (text == null) {
                    continue;
                }
                int idx = text.indexOf("{\"type\"");
                if (idx < 0) {
                    idx = text.indexOf("\"properties\"");
                }
                if (idx >= 0) {
                    String candidate = text.substring(idx);
                    int end = candidate.lastIndexOf('}');
                    if (end > 0) {
                        candidate = candidate.substring(0, end + 1);
                    }
                    if (candidate.contains("properties") || candidate.contains("\"type\"")) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 汇总最终回答：把工具执行结果拼进文本，便于前端直接看到"工具真的跑了"。
     */
    private String buildFinalAnswer(String userText, List<Msg> msgs) {
        List<String> toolLines = new ArrayList<>();
        if (msgs != null) {
            for (Msg msg : msgs) {
                for (ToolResultBlock trb : msg.getContentBlocks(ToolResultBlock.class)) {
                    toolLines.add("- " + trb.getName() + " → " + firstOutputText(trb));
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("【Mock 模型】已收到你的问题：").append(userText).append('\n');
        if (toolLines.isEmpty()) {
            sb.append("本次回答没有调用任何工具（当前 Agent 未注册工具或 Mock 模型直接作答）。");
        } else {
            sb.append("本次回答共调用 ").append(toolLines.size()).append(" 次工具：\n");
            toolLines.forEach(line -> sb.append(line).append('\n'));
            sb.append("以上均为工具的真实执行结果（Mock 模型负责决定调用哪个工具、如何组织回答）。");
        }
        return sb.toString();
    }

    private String firstOutputText(ToolResultBlock trb) {
        List<ContentBlock> output = trb.getOutput();
        if (output == null || output.isEmpty()) {
            return "(空)";
        }
        ContentBlock first = output.get(0);
        if (first instanceof TextBlock tb) {
            String t = tb.getText();
            return t == null ? "" : (t.length() > 500 ? t.substring(0, 500) + "..." : t);
        }
        return first.toString();
    }
}
