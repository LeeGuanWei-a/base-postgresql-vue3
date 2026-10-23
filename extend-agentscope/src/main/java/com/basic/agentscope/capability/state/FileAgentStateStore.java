package com.basic.agentscope.capability.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.agentscope.core.state.AgentState;
import io.agentscope.core.state.AgentStateStore;
import io.agentscope.core.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 基于本地文件的 Agent 状态存储 —— 把 AgentScope 的会话状态落盘成 JSON。
 *
 * <h3>目录结构</h3>
 * <pre>
 * {root}/{userId}/{sessionId}/{stateName}.json      单个状态（框架用的是 agent_state）
 * {root}/{userId}/{sessionId}/{stateName}.jsonl     状态列表（每行一个 JSON）
 * </pre>
 *
 * <h3>序列化策略（可 round-trip）</h3>
 * <ol>
 *   <li>状态是 {@link AgentState} 时，优先用框架自带的 {@code toJson()} / {@code fromJsonString()}，
 *       保证上下文消息、权限、任务等复杂结构<b>原样还原</b>；</li>
 *   <li>其它 {@link State} 实现，用 Jackson 2（{@code com.fasterxml.jackson.databind.ObjectMapper}）序列化；</li>
 *   <li>若某种状态 Jackson 序列化失败，降级为 <b>包络格式</b>
 *       {@code {"__fallback":true,"type":"...","text":"..."}}，保证"写得出、读不炸"。</li>
 * </ol>
 *
 * <p>⚠️ 本类是<b>普通对象</b>，由能力类自己持有，<b>不注册为 Spring Bean</b>
 * （它不是 Model / Toolkit / Memory 类型，注册与否都不冲突，但保持"能力自持"的风格一致）。</p>
 */
public class FileAgentStateStore implements AgentStateStore {

    private static final Logger log = LoggerFactory.getLogger(FileAgentStateStore.class);

    /** 单个状态文件后缀 */
    private static final String SINGLE_SUFFIX = ".json";

    /** 状态列表文件后缀 */
    private static final String LIST_SUFFIX = ".jsonl";

    /** 降级包络的标记字段 */
    private static final String FALLBACK_FLAG = "__fallback";

    /** 状态根目录 */
    private final Path rootDirectory;

    /** Jackson 2 序列化器（注意：不能用 tools.jackson，框架内部用的是 Jackson 2） */
    private final ObjectMapper mapper = new ObjectMapper();

    public FileAgentStateStore(Path rootDirectory) {
        this.rootDirectory = rootDirectory.toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("创建状态存储目录失败：" + this.rootDirectory, e);
        }
    }

    // ------------------------------------------------------------------ 写入

    @Override
    public void save(String userId, String sessionId, String stateName, State state) {
        if (state == null) {
            return;
        }
        Path file = stateFile(userId, sessionId, stateName);
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, serialize(state), StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 状态落盘失败不应该让整轮对话失败，只记录告警
            log.warn("保存状态失败 userId={} sessionId={} state={}", userId, sessionId, stateName, e);
        }
    }

    @Override
    public void save(String userId, String sessionId, String stateName, List<? extends State> states) {
        if (states == null || states.isEmpty()) {
            return;
        }
        Path file = stateListFile(userId, sessionId, stateName);
        try {
            Files.createDirectories(file.getParent());
            StringBuilder sb = new StringBuilder();
            for (State state : states) {
                if (state == null) {
                    continue;
                }
                sb.append(serialize(state)).append('\n');
            }
            Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("保存状态列表失败 userId={} sessionId={} state={}", userId, sessionId, stateName, e);
        }
    }

    // ------------------------------------------------------------------ 读取

    @Override
    public <T extends State> Optional<T> get(String userId, String sessionId, String stateName, Class<T> clazz) {
        Path file = stateFile(userId, sessionId, stateName);
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            return Optional.ofNullable(deserialize(json, clazz));
        } catch (Exception e) {
            log.warn("读取状态失败 userId={} sessionId={} state={}", userId, sessionId, stateName, e);
            return Optional.empty();
        }
    }

    @Override
    public <T extends State> List<T> getList(String userId, String sessionId, String stateName, Class<T> clazz) {
        Path file = stateListFile(userId, sessionId, stateName);
        if (!Files.exists(file)) {
            return List.of();
        }
        List<T> result = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line == null || line.isBlank()) {
                    continue;
                }
                T state = deserialize(line, clazz);
                if (state != null) {
                    result.add(state);
                }
            }
        } catch (Exception e) {
            log.warn("读取状态列表失败 userId={} sessionId={} state={}", userId, sessionId, stateName, e);
        }
        return result;
    }

    // ------------------------------------------------------------------ 存在性 / 删除

    @Override
    public boolean exists(String userId, String sessionId) {
        return Files.isDirectory(sessionDir(userId, sessionId));
    }

    @Override
    public void delete(String userId, String sessionId) {
        Path dir = sessionDir(userId, sessionId);
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    log.warn("删除状态文件失败: {}", p, e);
                }
            });
        } catch (IOException e) {
            log.warn("删除会话目录失败 userId={} sessionId={}", userId, sessionId, e);
        }
    }

    /**
     * 删除单个状态（框架接口里这个 3 参版本默认是空实现，这里补上真正的删除）。
     */
    @Override
    public void delete(String userId, String sessionId, String stateName) {
        try {
            Files.deleteIfExists(stateFile(userId, sessionId, stateName));
            Files.deleteIfExists(stateListFile(userId, sessionId, stateName));
        } catch (IOException e) {
            log.warn("删除状态失败 userId={} sessionId={} state={}", userId, sessionId, stateName, e);
        }
    }

    @Override
    public Set<String> listSessionIds(String userId) {
        Path userDir = userDir(userId);
        if (!Files.isDirectory(userDir)) {
            return Set.of();
        }
        try (Stream<Path> children = Files.list(userDir)) {
            return children.filter(Files::isDirectory)
                    .map(p -> p.getFileName().toString())
                    .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
        } catch (IOException e) {
            log.warn("列出会话失败 userId={}", userId, e);
            return Set.of();
        }
    }

    // ------------------------------------------------------------------ 序列化 / 反序列化

    /**
     * 状态 → JSON 字符串。
     */
    private String serialize(State state) {
        // 1) AgentState 用框架自带的 JSON（可完整 round-trip）
        if (state instanceof AgentState agentState) {
            try {
                return agentState.toJson();
            } catch (Exception e) {
                log.warn("AgentState.toJson 失败，降级为 Jackson 序列化", e);
            }
        }
        // 2) 通用 State：Jackson 2
        try {
            return mapper.writeValueAsString(state);
        } catch (Exception e) {
            log.warn("状态序列化失败，降级为原始文本包络 type={}", state.getClass().getName(), e);
            Map<String, Object> envelope = new LinkedHashMap<>();
            envelope.put(FALLBACK_FLAG, Boolean.TRUE);
            envelope.put("type", state.getClass().getName());
            envelope.put("text", String.valueOf(state));
            try {
                return mapper.writeValueAsString(envelope);
            } catch (Exception ex) {
                return "{}";
            }
        }
    }

    /**
     * JSON 字符串 → 状态。
     */
    @SuppressWarnings("unchecked")
    private <T extends State> T deserialize(String json, Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        // 1) AgentState 用框架自带的反序列化
        if (AgentState.class.equals(clazz)) {
            try {
                return clazz.cast(AgentState.fromJsonString(json));
            } catch (Exception e) {
                log.warn("AgentState.fromJsonString 失败，降级为 Jackson 反序列化", e);
            }
        }
        // 2) 通用 State：Jackson 2
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.warn("状态反序列化失败（降级重试）: {}", clazz.getName(), e);
        }
        // 3) 降级：先读成 Map，再尝试 convertValue
        try {
            Map<String, Object> map = mapper.readValue(json, Map.class);
            if (Boolean.TRUE.equals(map.get(FALLBACK_FLAG))) {
                log.warn("状态为降级包络，无法还原为 {}，返回空", clazz.getName());
                return null;
            }
            return mapper.convertValue(map, clazz);
        } catch (Exception e) {
            log.warn("状态降级反序列化失败: {}", clazz.getName(), e);
            return null;
        }
    }

    // ------------------------------------------------------------------ 路径工具

    private Path userDir(String userId) {
        return rootDirectory.resolve(safeSegment(userId));
    }

    private Path sessionDir(String userId, String sessionId) {
        return userDir(userId).resolve(safeSegment(sessionId));
    }

    private Path stateFile(String userId, String sessionId, String stateName) {
        return sessionDir(userId, sessionId).resolve(safeSegment(stateName) + SINGLE_SUFFIX);
    }

    private Path stateListFile(String userId, String sessionId, String stateName) {
        return sessionDir(userId, sessionId).resolve(safeSegment(stateName) + LIST_SUFFIX);
    }

    /**
     * 路径片段净化：去掉非法字符，避免目录穿越。
     */
    private String safeSegment(String raw) {
        if (raw == null || raw.isBlank()) {
            return "unknown";
        }
        String cleaned = raw.trim().replaceAll("[^A-Za-z0-9._-]", "_");
        if (cleaned.isBlank() || ".".equals(cleaned) || "..".equals(cleaned)) {
            return "unknown";
        }
        return cleaned.length() > 64 ? cleaned.substring(0, 64) : cleaned;
    }

    // ------------------------------------------------------------------ 对外查询

    /**
     * 状态根目录。
     */
    public Path getRootDirectory() {
        return rootDirectory;
    }

    /**
     * 已落盘的状态文件数量（.json + .jsonl）。
     */
    public int countStateFiles() {
        if (!Files.isDirectory(rootDirectory)) {
            return 0;
        }
        try (Stream<Path> walk = Files.walk(rootDirectory)) {
            return (int) walk.filter(Files::isRegularFile)
                    .filter(p -> {
                        String name = p.getFileName().toString();
                        return name.endsWith(SINGLE_SUFFIX) || name.endsWith(LIST_SUFFIX);
                    })
                    .count();
        } catch (IOException e) {
            log.warn("统计状态文件失败", e);
            return 0;
        }
    }

    /**
     * 已持久化的用户 ID 列表。
     */
    public List<String> listUserIds() {
        if (!Files.isDirectory(rootDirectory)) {
            return List.of();
        }
        try (Stream<Path> children = Files.list(rootDirectory)) {
            return children.filter(Files::isDirectory)
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .toList();
        } catch (IOException e) {
            log.warn("列出用户目录失败", e);
            return List.of();
        }
    }

    /**
     * 某个会话下已保存的状态名列表。
     */
    public List<String> listStateNames(String userId, String sessionId) {
        Path dir = sessionDir(userId, sessionId);
        if (!Files.isDirectory(dir)) {
            return List.of();
        }
        try (Stream<Path> children = Files.list(dir)) {
            return children.filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .map(n -> n.endsWith(LIST_SUFFIX)
                            ? n.substring(0, n.length() - LIST_SUFFIX.length())
                            : n.endsWith(SINGLE_SUFFIX) ? n.substring(0, n.length() - SINGLE_SUFFIX.length()) : n)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            log.warn("列出状态名失败 userId={} sessionId={}", userId, sessionId, e);
            return List.of();
        }
    }
}
