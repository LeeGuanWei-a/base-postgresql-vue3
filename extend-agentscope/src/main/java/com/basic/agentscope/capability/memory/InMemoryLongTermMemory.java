package com.basic.agentscope.capability.memory;

import io.agentscope.core.memory.LongTermMemory;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存版长期记忆（{@link LongTermMemory} 实现）。
 *
 * <p>AgentScope 2.x 的 {@code LongTermMemory} 只有两个方法：</p>
 * <ul>
 *   <li>{@code record(List<Msg>)} —— 把一轮对话「存档」；</li>
 *   <li>{@code retrieve(Msg)} —— 按提问内容「检索」相关存档，返回一段文本。</li>
 * </ul>
 *
 * <p>框架会在 {@code PreCallEvent} 时自动调用 {@code retrieve}（把结果包进
 * {@code <long_term_memory>} 段落注入上下文），在 {@code PostCallEvent} 时
 * 自动调用 {@code record}（归档整轮上下文）。</p>
 *
 * <p>本实现把记忆放在 {@link ConcurrentHashMap} 里，线程安全；
 * 每条记忆带一个自增序号，保证展示顺序稳定。</p>
 */
public class InMemoryLongTermMemory implements LongTermMemory {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 记忆条目：id → 条目 */
    private final Map<String, MemoryEntry> entries = new ConcurrentHashMap<>();

    /** 自增序号（用于排序与生成 id） */
    private final AtomicLong seq = new AtomicLong();

    @Override
    public Mono<Void> record(List<Msg> msgs) {
        return Mono.fromRunnable(() -> {
            if (msgs == null || msgs.isEmpty()) {
                return;
            }
            for (Msg msg : msgs) {
                String text = extractText(msg);
                if (text == null || text.isBlank()) {
                    continue;
                }
                long no = seq.incrementAndGet();
                String id = "ltm-" + no;
                entries.put(id, new MemoryEntry(id, no, resolveRole(msg), text, LocalDateTime.now()));
            }
        });
    }

    @Override
    public Mono<String> retrieve(Msg queryMsg) {
        return Mono.fromSupplier(() -> {
            String query = queryMsg == null ? "" : extractText(queryMsg);
            return search(query, 3);
        });
    }

    // ------------------------------------------------------------------ 业务方法

    /**
     * 按关键字检索记忆，返回可直接注入上下文的文本。
     *
     * @param query 查询文本
     * @param limit 最多返回几条
     * @return 命中的记忆摘要；没有命中时返回空串（框架会自动忽略空内容）
     */
    public String search(String query, int limit) {
        List<MemoryEntry> all = snapshot();
        if (all.isEmpty()) {
            return "";
        }
        Set<String> tokens = tokenize(query);
        List<MemoryEntry> matched = new ArrayList<>();
        for (MemoryEntry entry : all) {
            if (tokens.isEmpty()) {
                continue;
            }
            String haystack = entry.text().toLowerCase();
            for (String token : tokens) {
                if (haystack.contains(token)) {
                    matched.add(entry);
                    break;
                }
            }
        }
        if (matched.isEmpty()) {
            return "";
        }
        // 命中的按时间倒序（最近的优先），最多 limit 条
        matched.sort(Comparator.comparingLong(MemoryEntry::no).reversed());
        StringBuilder sb = new StringBuilder();
        int count = Math.min(limit, matched.size());
        for (int i = 0; i < count; i++) {
            MemoryEntry entry = matched.get(i);
            sb.append("- [").append(entry.role()).append(" @ ").append(entry.time()).append("] ")
                    .append(truncate(entry.text(), 200)).append('\n');
        }
        return sb.toString();
    }

    /**
     * 返回最近的若干条记忆（没有关键字命中时的"浏览"兜底）。
     */
    public String recent(int limit) {
        List<MemoryEntry> all = snapshot();
        if (all.isEmpty()) {
            return "";
        }
        all.sort(Comparator.comparingLong(MemoryEntry::no).reversed());
        StringBuilder sb = new StringBuilder();
        int count = Math.min(limit, all.size());
        for (int i = 0; i < count; i++) {
            MemoryEntry entry = all.get(i);
            sb.append("- [").append(entry.role()).append(" @ ").append(entry.time()).append("] ")
                    .append(truncate(entry.text(), 200)).append('\n');
        }
        return sb.toString();
    }

    /**
     * 当前记忆条数。
     */
    public int size() {
        return entries.size();
    }

    /**
     * 全部记忆快照（按写入顺序）。
     */
    public List<MemoryEntry> snapshot() {
        List<MemoryEntry> all = new ArrayList<>(entries.values());
        all.sort(Comparator.comparingLong(MemoryEntry::no));
        return all;
    }

    /**
     * 清空记忆。
     */
    public void clear() {
        entries.clear();
    }

    // ------------------------------------------------------------------ 内部方法

    /**
     * 取消息里的纯文本。
     */
    private String extractText(Msg msg) {
        if (msg == null) {
            return "";
        }
        try {
            return msg.getTextContent();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 解析角色名，取不到时统一记为 UNKNOWN。
     */
    private String resolveRole(Msg msg) {
        MsgRole role = msg.getRole();
        return role == null ? "UNKNOWN" : role.name();
    }

    /**
     * 截断长文本。
     */
    static String truncate(String text, int max) {
        if (text == null) {
            return "";
        }
        String t = text.replaceAll("\\s+", " ").trim();
        return t.length() <= max ? t : t.substring(0, max) + "...";
    }

    /**
     * 极简分词：英文/数字按非字母数字切分；中文切成 2-gram。
     *
     * <p>目的是让"用户""记忆"这类中文关键词也能命中。</p>
     */
    static Set<String> tokenize(String text) {
        Set<String> tokens = new LinkedHashSet<>();
        if (text == null || text.isBlank()) {
            return tokens;
        }
        String lower = text.toLowerCase();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                buffer.append(c);
            } else {
                flush(buffer, tokens);
            }
        }
        flush(buffer, tokens);
        return tokens;
    }

    private static void flush(StringBuilder buffer, Set<String> tokens) {
        if (buffer.isEmpty()) {
            return;
        }
        String segment = buffer.toString();
        buffer.setLength(0);
        boolean cjk = false;
        for (int i = 0; i < segment.length(); i++) {
            char c = segment.charAt(i);
            if (c >= 0x4E00 && c <= 0x9FFF) {
                cjk = true;
                break;
            }
        }
        if (!cjk) {
            tokens.add(segment);
            return;
        }
        // 中文：切成 2-gram
        for (int i = 0; i + 1 < segment.length(); i++) {
            tokens.add(segment.substring(i, i + 2));
        }
        if (segment.length() == 1) {
            tokens.add(segment);
        }
    }

    /**
     * 一条长期记忆。
     *
     * @param id      记忆 ID
     * @param no      自增序号
     * @param role    来源角色
     * @param text    记忆正文
     * @param createdAt 写入时间
     */
    public record MemoryEntry(String id, long no, String role, String text, LocalDateTime createdAt) {

        public String time() {
            return createdAt == null ? "-" : createdAt.format(FMT);
        }
    }
}
