package com.basic.agentscope.tool;

import io.agentscope.core.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台数据查询工具集 —— 演示 AgentScope 的<b>工具调用（Tool Use）</b>能力。
 *
 * <p>这组工具直接用 {@link JdbcTemplate} 查 PostgreSQL，因此 Agent 的回答里
 * 携带的是<b>数据库真实数据</b>，而不是模型编造的内容。</p>
 *
 * <p>⚠️ 工具的 {@code description} 直接决定模型"要不要调、怎么调"，
 * 必须写清楚"什么时候用这个工具、参数含义、返回什么"。</p>
 */
@Component
public class PlatformTools {

    private static final Logger log = LoggerFactory.getLogger(PlatformTools.class);

    /** 允许统计行数的表白名单，防止模型拼出任意 SQL */
    private static final List<String> COUNTABLE_TABLES = List.of(
            "sys_user", "sys_role", "sys_dept", "sys_post", "sys_menu",
            "sys_dict_type", "sys_dict_data", "sys_config", "sys_notice",
            "sys_oper_log", "sys_logininfor", "sys_job");

    private final JdbcTemplate jdbcTemplate;

    public PlatformTools(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 按关键字模糊查询系统用户。
     *
     * @param keyword 用户名或昵称关键字；为空时返回前 10 个用户
     * @return 命中的用户列表（userId / userName / nickName / status / deptId）
     */
    @Tool(name = "query_sys_user",
            description = "当用户询问『有哪些用户』『某个用户是谁』『按用户名查用户』时，调用本工具查询系统用户表。"
                    + "参数 keyword 是用户名或昵称的关键字，支持模糊匹配；传空字符串则返回前 10 个用户。",
            readOnly = true)
    public List<Map<String, Object>> querySysUser(String keyword) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try {
            String kw = (keyword == null || keyword.isBlank()) ? "" : keyword.trim();
            String sql = """
                    SELECT user_id, user_name, nick_name, status, dept_id, email
                    FROM sys_user
                    WHERE del_flag = '0'
                      AND (? = '' OR user_name LIKE ? OR nick_name LIKE ?)
                    ORDER BY user_id
                    LIMIT 10
                    """;
            rows = jdbcTemplate.queryForList(sql, kw, "%" + kw + "%", "%" + kw + "%");
        } catch (Exception e) {
            log.warn("query_sys_user 执行失败", e);
        }
        return rows;
    }

    /**
     * 查询字典数据。
     *
     * @param dictType 字典类型，如 sys_user_sex；为空时返回全部字典类型
     * @return 字典条目列表
     */
    @Tool(name = "query_dict_data",
            description = "当用户询问『状态都有哪些』『字典含义』『有哪些枚举值』时，调用本工具查询系统字典表。"
                    + "参数 dictType 是字典类型标识（如 sys_user_sex、sys_normal_disable）；为空则返回所有字典数据。",
            readOnly = true)
    public List<Map<String, Object>> queryDictData(String dictType) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try {
            String type = (dictType == null) ? "" : dictType.trim();
            String sql = """
                    SELECT dict_type, dict_label, dict_value, list_class, status
                    FROM sys_dict_data
                    WHERE (? = '' OR dict_type = ?)
                    ORDER BY dict_type, dict_sort
                    LIMIT 20
                    """;
            rows = jdbcTemplate.queryForList(sql, type, type);
        } catch (Exception e) {
            log.warn("query_dict_data 执行失败", e);
        }
        return rows;
    }

    /**
     * 查询最近的操作日志。
     *
     * @param limit 返回条数，默认 10，最大 50
     * @return 操作日志列表
     */
    @Tool(name = "query_oper_log",
            description = "当用户询问『最近谁做了什么操作』『操作日志』『谁改过数据』时，调用本工具查询系统操作日志表。"
                    + "参数 limit 是返回条数，默认 10，最大 50。",
            readOnly = true)
    public List<Map<String, Object>> queryOperLog(int limit) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try {
            int n = Math.min(Math.max(limit <= 0 ? 10 : limit, 1), 50);
            rows = jdbcTemplate.queryForList(
                    "SELECT oper_id, title, oper_name, oper_url, status, oper_time FROM sys_oper_log ORDER BY oper_time DESC LIMIT ?",
                    n);
        } catch (Exception e) {
            log.warn("query_oper_log 执行失败", e);
        }
        return rows;
    }

    /**
     * 统计某张业务表的数据量。
     *
     * @param tableName 表名，只允许白名单内的表
     * @return 表名与行数
     */
    @Tool(name = "count_table_rows",
            description = "当用户询问『系统里有多少用户/角色/日志』这类总量问题时，调用本工具统计业务表行数。"
                    + "参数 tableName 是表名，可选值有：sys_user、sys_role、sys_dept、sys_post、sys_menu、"
                    + "sys_dict_type、sys_dict_data、sys_config、sys_notice、sys_oper_log、sys_logininfor、sys_job。",
            readOnly = true)
    public Map<String, Object> countTableRows(String tableName) {
        Map<String, Object> result = new LinkedHashMap<>();
        String name = (tableName == null) ? "" : tableName.trim().toLowerCase();
        result.put("table", name);
        if (!COUNTABLE_TABLES.contains(name)) {
            result.put("count", -1);
            result.put("message", "不支持统计该表，可选表：" + String.join("、", COUNTABLE_TABLES));
            return result;
        }
        try {
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + name, Long.class);
            result.put("count", count == null ? 0L : count);
        } catch (Exception e) {
            log.warn("count_table_rows 执行失败", e);
            result.put("count", -1);
            result.put("message", "查询失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 查询系统角色列表。
     *
     * @param roleName 角色名关键字，可为空
     * @return 角色列表
     */
    @Tool(name = "query_sys_role",
            description = "当用户询问『有哪些角色』『权限是怎么划分的』时，调用本工具查询系统角色表。"
                    + "参数 roleName 是角色名关键字，可为空表示全部。",
            readOnly = true)
    public List<Map<String, Object>> querySysRole(String roleName) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try {
            String kw = (roleName == null) ? "" : roleName.trim();
            rows = jdbcTemplate.queryForList(
                    "SELECT role_id, role_name, role_key, status FROM sys_role WHERE (? = '' OR role_name LIKE ?) ORDER BY role_id LIMIT 20",
                    kw, "%" + kw + "%");
        } catch (Exception e) {
            log.warn("query_sys_role 执行失败", e);
        }
        return rows;
    }
}
