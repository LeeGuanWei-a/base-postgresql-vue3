package com.basic.agentscope.capability.memory;

import io.agentscope.core.tool.Tool;

/**
 * 长期记忆工具集 —— 让 Agent 主动"回忆"长期记忆里的内容。
 *
 * <p>AgentScope 自带的 {@code LongTermMemoryTools} 需要
 * {@code List<String>} 类型的参数，在演示环境里模型很难给出有效参数；
 * 这里自建一个更朴素、更好演示的版本：单个字符串关键字。</p>
 */
public class MemoryTools {

    /** 长期记忆实现 */
    private final InMemoryLongTermMemory longTermMemory;

    public MemoryTools(InMemoryLongTermMemory longTermMemory) {
        this.longTermMemory = longTermMemory;
    }

    /**
     * 按关键字回忆长期记忆。
     *
     * @param keyword 关键字；为空或没有命中时返回最近几条记忆
     * @return 记忆摘要文本
     */
    @Tool(name = "recall_long_term_memory",
            description = "当用户问『我之前说过什么』『你还记得我的什么信息』『之前聊过的事』时，"
                    + "调用本工具回忆长期记忆。参数 keyword 是要回忆的关键字；"
                    + "若没有命中任何记忆，本工具会返回最近几条记忆作为参考。",
            readOnly = true)
    public String recallLongTermMemory(String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        String hit = longTermMemory.search(kw, 3);
        if (!hit.isBlank()) {
            return "【长期记忆 · 命中 " + kw + "】\n" + hit;
        }
        String recent = longTermMemory.recent(3);
        if (recent.isBlank()) {
            return "【长期记忆】目前还没有任何记忆记录（这是第一次对话）。";
        }
        return "【长期记忆 · 未命中关键字 " + (kw.isEmpty() ? "(空)" : kw) + "，返回最近记录】\n" + recent;
    }
}
