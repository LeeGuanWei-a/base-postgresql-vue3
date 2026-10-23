package com.basic.agentscope.capability.scheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 简化版 cron 表达式解析器（自实现，不引入 Quartz / Spring Scheduling 依赖）。
 *
 * <p><b>支持格式</b>：{@code 秒 分 时 日 月 周}（六段）或 {@code 分 时 日 月 周}（五段，五段时秒固定为 0）。</p>
 *
 * <p><b>支持的写法</b>：</p>
 * <ul>
 *   <li>{@code *} —— 任意；{@code ?} —— 等同于 {@code *}（Quartz 风格，用于"不指定"）</li>
 *   <li>{@code 5} —— 具体数字</li>
 *   <li>{@code *&#47;10} —— 步长</li>
 *   <li>{@code 1-5} —— 区间（可与步长组合：{@code 1-30/5}）</li>
 *   <li>{@code 1,3,5} —— 列表</li>
 *   <li>星期 / 月份英文名：{@code MON-FRI}、{@code JAN}（Quartz 风格）</li>
 * </ul>
 *
 * <p><b>日 / 周语义</b>（与 Quartz 一致）：日与周都限定时取<b>并集</b>；只限定其一时取该限定。</p>
 */
public class CronExpression {

    private static final int[] DOM_RANGES = new int[]{1, 31};

    private static final int[] MONTH_RANGES = new int[]{1, 12};

    /** 星期英文名（下标即 cron 星期值：0=周日） */
    private static final String[] DAY_NAMES = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

    /** 月份英文名（下标 + 1 即月份） */
    private static final String[] MONTH_NAMES = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    private final String expression;

    private final boolean[] seconds = new boolean[60];

    private final boolean[] minutes = new boolean[60];

    private final boolean[] hours = new boolean[24];

    private final boolean[] daysOfMonth = new boolean[32];

    private final boolean[] months = new boolean[13];

    /** 下标为 cron 星期值（0=周日 … 7=周日） */
    private final boolean[] daysOfWeek = new boolean[8];

    private final boolean domRestricted;

    private final boolean dowRestricted;

    private CronExpression(String expression, String[] fields) {
        this.expression = expression;
        parseField(fields[0], 0, 59, seconds, null);
        parseField(fields[1], 0, 59, minutes, null);
        parseField(fields[2], 0, 23, hours, null);
        parseField(fields[3], DOM_RANGES[0], DOM_RANGES[1], daysOfMonth, null);
        parseField(fields[4], MONTH_RANGES[0], MONTH_RANGES[1], months, MONTH_NAMES);
        parseField(fields[5], 0, 7, daysOfWeek, DAY_NAMES);
        // 星期 7 归一到 0（周日）
        if (daysOfWeek[7]) {
            daysOfWeek[0] = true;
        }
        this.domRestricted = !isAll(daysOfMonth, DOM_RANGES[0], DOM_RANGES[1]);
        this.dowRestricted = !isAll(daysOfWeek, 0, 6);
    }

    /**
     * 解析 cron 表达式。
     *
     * @param expression 五段或六段 cron 表达式
     * @throws IllegalArgumentException 表达式非法
     */
    public static CronExpression parse(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("cron 表达式不能为空");
        }
        String[] parts = expression.trim().split("\\s+");
        String[] fields;
        if (parts.length == 6) {
            fields = parts;
        } else if (parts.length == 5) {
            fields = new String[]{"0", parts[0], parts[1], parts[2], parts[3], parts[4]};
        } else {
            throw new IllegalArgumentException("cron 表达式必须是 5 段（分 时 日 月 周）或 6 段（秒 分 时 日 月 周）：" + expression);
        }
        try {
            return new CronExpression(expression.trim(), fields);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("cron 表达式包含非法数字：" + expression);
        }
    }

    /**
     * 计算严格晚于 {@code after} 的下一次执行时间。
     */
    public LocalDateTime next(LocalDateTime after) {
        LocalDateTime t = after.withNano(0).plusSeconds(1);
        for (int guard = 0; guard < 200_000; guard++) {
            if (!months[t.getMonthValue()]) {
                t = t.plusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
                continue;
            }
            if (!dayMatches(t)) {
                t = t.plusDays(1).with(LocalTime.MIN);
                continue;
            }
            if (!hours[t.getHour()]) {
                t = t.plusHours(1).withMinute(0).withSecond(0);
                continue;
            }
            if (!minutes[t.getMinute()]) {
                t = t.plusMinutes(1).withSecond(0);
                continue;
            }
            if (!seconds[t.getSecond()]) {
                t = t.plusSeconds(1);
                continue;
            }
            return t;
        }
        throw new IllegalStateException("cron 表达式无法计算下次执行时间（可能永不相邻）：" + expression);
    }

    /**
     * 距下次执行的毫秒数。
     */
    public long delayMillis(LocalDateTime from) {
        LocalDateTime next = next(from);
        long millis = Duration.between(from, next).toMillis();
        return Math.max(millis, 0);
    }

    /**
     * 原始表达式。
     */
    public String getExpression() {
        return expression;
    }

    // ------------------------------------------------------------------ 内部方法

    private boolean dayMatches(LocalDateTime t) {
        boolean domMatch = daysOfMonth[t.getDayOfMonth()];
        boolean dowMatch = daysOfWeek[cronDayOfWeek(t.toLocalDate())];
        if (domRestricted && dowRestricted) {
            return domMatch || dowMatch;
        }
        if (domRestricted) {
            return domMatch;
        }
        if (dowRestricted) {
            return dowMatch;
        }
        return true;
    }

    /**
     * LocalDate → cron 星期值（0=周日 … 6=周六）。
     */
    private int cronDayOfWeek(LocalDate date) {
        int javaDow = date.getDayOfWeek().getValue();
        return javaDow == 7 ? 0 : javaDow;
    }

    private static void parseField(String field, int min, int max, boolean[] target, String[] names) {
        String value = field == null ? "" : field.trim();
        if (value.isEmpty() || "*".equals(value) || "?".equals(value)) {
            fill(target, min, max);
            return;
        }
        for (String part : value.split(",")) {
            String item = part.trim();
            if (item.isEmpty()) {
                continue;
            }
            int step = 1;
            int slash = item.indexOf('/');
            if (slash >= 0) {
                step = parseNumber(item.substring(slash + 1));
                item = item.substring(0, slash).trim();
            }
            if (step <= 0) {
                step = 1;
            }
            int start;
            int end;
            if ("*".equals(item) || "?".equals(item)) {
                start = min;
                end = max;
            } else {
                int dash = item.indexOf('-');
                if (dash > 0) {
                    start = parseValue(item.substring(0, dash), names, min);
                    end = parseValue(item.substring(dash + 1), names, min);
                } else {
                    start = parseValue(item, names, min);
                    end = step > 1 ? max : start;
                }
            }
            if (start < min || start > max || end < min || end > max || end < start) {
                throw new IllegalArgumentException("cron 字段取值越界：" + field + "（允许 " + min + "-" + max + "）");
            }
            for (int v = start; v <= end; v += step) {
                target[v] = true;
            }
        }
        if (names != null && max == 7 && target[7]) {
            target[0] = true;
        }
    }

    private static int parseNumber(String text) {
        return Integer.parseInt(text.trim());
    }

    /**
     * 解析单个取值：支持数字，也支持星期 / 月份英文名（如 {@code MON-FRI}、{@code JAN}）。
     */
    private static int parseValue(String text, String[] names, int min) {
        String token = text.trim();
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                if (names[i].equalsIgnoreCase(token)) {
                    return min == 0 ? i : i + 1;
                }
            }
        }
        return parseNumber(token);
    }

    private static void fill(boolean[] target, int min, int max) {
        for (int i = min; i <= max; i++) {
            target[i] = true;
        }
    }

    private static boolean isAll(boolean[] target, int min, int max) {
        for (int i = min; i <= max; i++) {
            if (!target[i]) {
                return false;
            }
        }
        return true;
    }
}
