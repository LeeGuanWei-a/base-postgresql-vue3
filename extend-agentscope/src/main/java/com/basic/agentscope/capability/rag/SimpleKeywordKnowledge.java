package com.basic.agentscope.capability.rag;

import io.agentscope.core.message.TextBlock;
import io.agentscope.core.rag.Knowledge;
import io.agentscope.core.rag.model.Document;
import io.agentscope.core.rag.model.DocumentMetadata;
import io.agentscope.core.rag.model.RetrieveConfig;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 关键词检索型知识库（{@link Knowledge} 实现）—— 纯内存版。
 *
 * <p>AgentScope 的 {@code Knowledge} 只有两个方法：</p>
 * <ul>
 *   <li>{@code addDocuments(List<Document>)} —— 往知识库里塞文档；</li>
 *   <li>{@code retrieve(String, RetrieveConfig)} —— 按查询串检索文档列表。</li>
 * </ul>
 *
 * <p>本实现不依赖向量库，采用<b>关键词命中打分</b>：</p>
 * <ol>
 *   <li>把查询串切成 token（英文整词 + 中文 2-gram）；</li>
 *   <li>逐条知识统计命中的 token 数量作为得分；</li>
 *   <li>按得分倒序、按 {@code RetrieveConfig.limit} 截断、
 *       并按 {@code RetrieveConfig.scoreThreshold} 过滤；</li>
 *   <li>一条都没命中时进入<b>浏览模式</b>：返回前 N 条知识，
 *       方便前端在没有关键词时也能看到知识库长什么样。</li>
 * </ol>
 */
public final class SimpleKeywordKnowledge implements Knowledge {

    /** 知识条目 */
    private final List<KnowledgeEntry> entries = new CopyOnWriteArrayList<>();

    /** 自增序号 */
    private final AtomicLong seq = new AtomicLong();

    public SimpleKeywordKnowledge() {
        loadBuiltInKnowledge();
    }

    // ------------------------------------------------------------------ Knowledge 接口

    @Override
    public Mono<Void> addDocuments(List<Document> documents) {
        return Mono.fromRunnable(() -> {
            if (documents == null || documents.isEmpty()) {
                return;
            }
            for (Document document : documents) {
                if (document == null || document.getMetadata() == null) {
                    continue;
                }
                DocumentMetadata metadata = document.getMetadata();
                String content = safeText(metadata);
                if (content == null || content.isBlank()) {
                    continue;
                }
                String docId = metadata.getDocId() == null ? null : metadata.getDocId();
                String id = docId == null || docId.isBlank() ? "kb-" + seq.incrementAndGet() : docId;
                String title = String.valueOf(metadata.getPayloadValue("title"));
                if ("null".equals(title) || title.isBlank()) {
                    title = id;
                }
                entries.add(new KnowledgeEntry(id, title, content, keywordsOf(metadata, title, content)));
            }
        });
    }

    @Override
    public Mono<List<Document>> retrieve(String query, RetrieveConfig config) {
        return Mono.fromSupplier(() -> doRetrieve(query, config));
    }

    // ------------------------------------------------------------------ 业务方法

    /**
     * 直接新增一条知识（绕过 {@link Document} 构造）。
     *
     * @param title    标题
     * @param content  正文
     * @param keywords 关键字，逗号分隔；为空时从标题/正文里自动抽取
     * @return 新知识的 ID
     */
    public String addEntry(String title, String content, String keywords) {
        String id = "kb-" + seq.incrementAndGet();
        String safeTitle = (title == null || title.isBlank()) ? id : title.trim();
        String safeContent = content == null ? "" : content;
        List<String> kw = splitKeywords(keywords);
        if (kw.isEmpty()) {
            kw = autoKeywords(safeTitle, safeContent);
        }
        entries.add(new KnowledgeEntry(id, safeTitle, safeContent, kw));
        return id;
    }

    /**
     * 全部知识快照。
     */
    public List<KnowledgeEntry> snapshot() {
        return new ArrayList<>(entries);
    }

    /**
     * 知识条数。
     */
    public int size() {
        return entries.size();
    }

    /**
     * 清空知识库。
     */
    public void clear() {
        entries.clear();
    }

    /**
     * 重新灌入平台内置知识。
     */
    public void reset() {
        entries.clear();
        loadBuiltInKnowledge();
    }

    // ------------------------------------------------------------------ 内部方法

    /**
     * 真正的检索逻辑。
     */
    private List<Document> doRetrieve(String query, RetrieveConfig config) {
        int limit = config == null || config.getLimit() <= 0 ? 5 : config.getLimit();
        double threshold = config == null ? 0.0 : config.getScoreThreshold();

        Set<String> tokens = tokenize(query);
        List<ScoredEntry> scored = new ArrayList<>();
        for (KnowledgeEntry entry : entries) {
            int hit = 0;
            String haystack = (entry.title() + " " + entry.content() + " " + String.join(" ", entry.keywords()))
                    .toLowerCase(Locale.ROOT);
            for (String token : tokens) {
                if (haystack.contains(token)) {
                    hit++;
                }
            }
            if (hit > 0) {
                double score = Math.min(1.0, (double) hit / Math.max(1, tokens.size()) + 0.3);
                if (score >= threshold) {
                    scored.add(new ScoredEntry(entry, score));
                }
            }
        }
        scored.sort((a, b) -> Double.compare(b.score(), a.score()));

        if (scored.isEmpty()) {
            // 浏览模式：没有命中关键字时返回前 N 条，方便前端预览知识库
            List<Document> fallback = new ArrayList<>();
            for (KnowledgeEntry entry : entries) {
                if (fallback.size() >= limit) {
                    break;
                }
                fallback.add(toDocument(entry, 0.0));
            }
            return fallback;
        }

        List<Document> result = new ArrayList<>();
        for (ScoredEntry scoredEntry : scored) {
            if (result.size() >= limit) {
                break;
            }
            result.add(toDocument(scoredEntry.entry(), scoredEntry.score()));
        }
        return result;
    }

    /**
     * 知识条目 → {@link Document}。
     */
    private Document toDocument(KnowledgeEntry entry, double score) {
        DocumentMetadata metadata = DocumentMetadata.builder()
                .content(TextBlock.builder().text(entry.content()).build())
                .docId(entry.id())
                .chunkId(entry.id() + "-0")
                .addPayload("title", entry.title())
                .addPayload("keywords", String.join(",", entry.keywords()))
                .build();
        Document document = new Document(metadata);
        document.setScore(score);
        return document;
    }

    /**
     * 取文档正文。
     */
    private String safeText(DocumentMetadata metadata) {
        try {
            String text = metadata.getContentText();
            if (text != null && !text.isBlank()) {
                return text;
            }
        } catch (Exception ignored) {
            // 非文本块，忽略
        }
        return "";
    }

    /**
     * 从 payload 里取关键字，取不到就自动抽取。
     */
    private List<String> keywordsOf(DocumentMetadata metadata, String title, String content) {
        Object raw = metadata.getPayloadValue("keywords");
        if (raw != null) {
            List<String> kw = splitKeywords(String.valueOf(raw));
            if (!kw.isEmpty()) {
                return kw;
            }
        }
        return autoKeywords(title, content);
    }

    /**
     * 逗号/顿号/换行分隔的关键字。
     */
    private List<String> splitKeywords(String raw) {
        List<String> result = new ArrayList<>();
        if (raw == null || raw.isBlank()) {
            return result;
        }
        for (String part : raw.split("[,，、;；\\s]+")) {
            String k = part.trim();
            if (!k.isEmpty() && !result.contains(k)) {
                result.add(k);
            }
        }
        return result;
    }

    /**
     * 自动抽取关键字（取中文 2-gram 里出现次数较多的几个）。
     */
    private List<String> autoKeywords(String title, String content) {
        Set<String> tokens = tokenize(title + " " + content);
        List<String> result = new ArrayList<>();
        for (String token : tokens) {
            if (result.size() >= 8) {
                break;
            }
            result.add(token);
        }
        return result;
    }

    /**
     * 极简分词：英文/数字整词，中文 2-gram。
     */
    static Set<String> tokenize(String text) {
        Set<String> tokens = new LinkedHashSet<>();
        if (text == null || text.isBlank()) {
            return tokens;
        }
        String lower = text.toLowerCase(Locale.ROOT);
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
        for (int i = 0; i + 1 < segment.length(); i++) {
            tokens.add(segment.substring(i, i + 2));
        }
        if (segment.length() == 1) {
            tokens.add(segment);
        }
    }

    /**
     * 灌入平台内置知识（系统表说明、字典含义、AgentScope 能力说明等）。
     */
    private void loadBuiltInKnowledge() {
        addEntry("系统用户表 sys_user",
                "sys_user 是系统用户表。关键字段：user_id 主键、user_name 登录名、nick_name 昵称、"
                        + "dept_id 部门、email 邮箱、status 状态（0 正常 / 1 停用）、del_flag 删除标记（0 存在 / 2 删除）。"
                        + "常用查询：按 user_name / nick_name 模糊查用户，或统计 sys_user 的行数。",
                "sys_user,系统用户,用户表,user_name,nick_name,dept_id,用户");

        addEntry("角色与菜单表 sys_role / sys_menu",
                "sys_role 保存角色（role_id、role_name 角色名、role_key 角色权限字符串、status 状态）；"
                        + "sys_menu 保存菜单与权限（menu_id、menu_name、perms 权限标识、menu_type 菜单类型）。"
                        + "角色权限划分就是「用户-角色-菜单」三级关联。",
                "sys_role,sys_menu,角色,菜单,权限,role_key,perms");

        addEntry("数据字典表 sys_dict_type / sys_dict_data",
                "sys_dict_type 是字典类型表（dict_type 类型编码、dict_name 类型名称）；"
                        + "sys_dict_data 是字典数据表（dict_label 显示标签、dict_value 存储值、list_class 样式、status 状态）。"
                        + "常见字典：sys_user_sex（性别）、sys_normal_disable（正常/停用）、sys_notice_type（通知类型）、"
                        + "sys_yes_no（是/否）。",
                "字典,dict,sys_dict_type,sys_dict_data,sys_user_sex,枚举,字典类型");

        addEntry("操作日志与登录日志",
                "sys_oper_log 记录操作日志（oper_id、title 模块标题、oper_name 操作人、oper_url 请求地址、"
                        + "status 操作状态、oper_time 操作时间）；sys_logininfor 记录登录日志（info_id、user_name、"
                        + "ipaddr、login_location、status 登录状态、login_time 登录时间）。",
                "日志,oper_log,logininfor,操作记录,登录日志,审计");

        addEntry("业务表行数统计白名单",
                "平台统计行数只允许白名单内的表：sys_user、sys_role、sys_dept、sys_post、sys_menu、sys_dict_type、"
                        + "sys_dict_data、sys_config、sys_notice、sys_oper_log、sys_logininfor、sys_job。"
                        + "其它表会被拒绝，防止模型拼出任意 SQL。",
                "行数,统计,count,白名单,表数量,多少");

        addEntry("AgentScope 能力清单",
                "本平台已接入的 AgentScope Java 2.0.4 能力：工具调用（tool）、多会话（session）、"
                        + "长期记忆（memory）、RAG 知识库（rag）、权限控制（permission）、中断运行（interrupt）等，"
                        + "统一由 CapabilityRegistry 注册，前端通过 /agentscope/capability/list 获取清单。",
                "agentscope,能力,清单,capability,能力注册表");

        addEntry("权限模式说明",
                "AgentScope 权限模式共有 5 种：DEFAULT（默认，未命中的工具需要询问）、ACCEPT_EDITS（自动接受编辑类操作）、"
                        + "EXPLORE（只读探索）、BYPASS（跳过所有检查）、DONT_ASK（无人应答时直接拒绝）。"
                        + "权限规则按工具名精确匹配，可配置 allow / deny / ask 三类规则。",
                "权限,permission,permissionmode,模式,allow,deny,ask");
    }

    // ------------------------------------------------------------------ 内部模型

    /**
     * 一条知识。
     *
     * @param id       知识 ID
     * @param title    标题
     * @param content  正文
     * @param keywords 关键字
     */
    public record KnowledgeEntry(String id, String title, String content, List<String> keywords) {
    }

    /**
     * 带分数的知识（检索时用）。
     */
    private record ScoredEntry(KnowledgeEntry entry, double score) {
    }
}
