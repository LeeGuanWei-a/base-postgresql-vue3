package com.basic.agentscope.capability.scheduler;

import com.basic.agentscope.config.AgentScopePropertiesConfig;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 调度器服务 —— 进程内调度任务注册中心 + 定时触发 Agent 的调度骨架。
 *
 * <p><b>为什么不引 Quartz</b>：本项目已有 {@code extend-quartz} 模块，
 * 但 {@code extend-agentscope} 模块<b>没有</b> Quartz 依赖；为避免模块耦合，
 * 这里用 JDK 自带的 {@link ScheduledExecutorService} + 自实现的简化 cron 解析器
 * （{@link CronExpression}）实现进程内调度。</p>
 *
 * <p><b>生产建议</b>：引入 {@code agentscope-extensions-scheduler-quartz}，
 * 与项目已有 Quartz 打通（任务持久化、集群、misfire 处理等）。</p>
 */
@Service
public class SchedulerService implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 执行结果摘要最大长度 */
    private static final int SUMMARY_LIMIT = 500;

    /** 可直接复制到 application.yml 的配置示例 */
    private static final String YAML_EXAMPLE = """
            agentscope:
              scheduler:
                # 总开关：true 才会真正按 cron 触发
                enabled: true
                # 调度时区
                timezone: Asia/Shanghai
                # 单次任务执行超时（秒）
                timeout-seconds: 120
                # 预置调度任务
                tasks:
                  - name: daily-report
                    # 6 段：秒 分 时 日 月 周；也支持 5 段（分 时 日 月 周）
                    cron: "0 0 9 * * ?"
                    prompt: 汇总昨日的业务数据并给出结论
                    session-id: sched-daily-report
            """;

    private final AgentScopePropertiesConfig properties;

    /**
     * ⚠️ 必须带 {@code @Qualifier}：本模块内存在多个 {@code ReActAgent} 类型的 Bean。
     */
    private final ReActAgent assistantAgent;

    /** 调度线程池（守护线程，随应用退出） */
    private final ScheduledExecutorService executor;

    /** 任务名 → 任务定义 */
    private final Map<String, ScheduledTask> tasks = new ConcurrentHashMap<>();

    /** 任务名 → 下一次执行的句柄 */
    private final Map<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

    /** 运行时开关覆盖：null = 跟随配置 */
    private volatile Boolean runtimeEnabled;

    public SchedulerService(AgentScopePropertiesConfig properties,
                             @Qualifier("basicAssistantAgent") ReActAgent assistantAgent) {
        this.properties = properties;
        this.assistantAgent = assistantAgent;
        AtomicInteger counter = new AtomicInteger(1);
        this.executor = Executors.newScheduledThreadPool(2, runnable -> {
            Thread thread = new Thread(runnable, "agentscope-scheduler-" + counter.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        });
    }

    @Override
    public void afterPropertiesSet() {
        registerConfiguredTasks();
    }

    @Override
    public void destroy() {
        futures.values().forEach(future -> future.cancel(false));
        futures.clear();
        executor.shutdownNow();
    }

    // ------------------------------------------------------------------ 开关

    public boolean isEnabled() {
        Boolean override = runtimeEnabled;
        if (override != null) {
            return override;
        }
        return properties.getScheduler() != null && properties.getScheduler().isEnabled();
    }

    public void enable() {
        runtimeEnabled = Boolean.TRUE;
        for (ScheduledTask task : tasks.values()) {
            schedule(task, true);
        }
    }

    public void disable() {
        runtimeEnabled = Boolean.FALSE;
        futures.values().forEach(future -> future.cancel(false));
        futures.clear();
        tasks.values().forEach(task -> task.setStatus("PAUSED"));
    }

    /**
     * 依赖是否就绪：进程内调度器已启用。
     */
    public boolean isReady() {
        return isEnabled();
    }

    public Map<String, Object> switchInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("enabled", isEnabled());
        info.put("source", runtimeEnabled == null ? "配置文件 agentscope.scheduler.enabled" : "运行时内存开关");
        info.put("persistent", runtimeEnabled == null);
        info.put("note", runtimeEnabled == null
                ? "当前由配置项 agentscope.scheduler.enabled 控制，重启后保持不变"
                : "当前为运行时内存开关，重启后恢复为配置项 agentscope.scheduler.enabled 的取值");
        return info;
    }

    // ------------------------------------------------------------------ 任务管理

    /**
     * 注册（或覆盖）一个调度任务。
     */
    public ScheduledTask register(String name, String cron, String prompt, String sessionId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("任务名不能为空");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("prompt 不能为空");
        }
        // 解析 cron，非法表达式直接抛出（由 Controller 转成友好提示）
        CronExpression expression = CronExpression.parse(cron);

        String taskName = name.trim();
        ScheduledTask task = new ScheduledTask();
        task.setName(taskName);
        task.setCron(expression.getExpression());
        task.setPrompt(prompt);
        task.setSessionId(sessionId == null || sessionId.isBlank() ? "sched-" + taskName : sessionId);
        tasks.put(taskName, task);
        schedule(task, true);
        return task;
    }

    /**
     * 删除一个调度任务。
     */
    public boolean remove(String name) {
        if (name == null) {
            return false;
        }
        ScheduledFuture<?> future = futures.remove(name.trim());
        if (future != null) {
            future.cancel(false);
        }
        return tasks.remove(name.trim()) != null;
    }

    /**
     * 任务列表。
     */
    public List<ScheduledTask> listTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * 立即执行一次任务（用于验证链路）。
     */
    public Map<String, Object> runNow(String name) {
        ScheduledTask task = tasks.get(name);
        if (task == null) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", false);
            result.put("message", "任务不存在：" + name);
            return result;
        }
        boolean ok = fire(task);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", ok);
        result.put("task", task.getName());
        result.put("lastResult", task.getLastResult());
        result.put("execCount", task.getExecCount());
        result.put("message", ok ? "任务已执行" : "任务执行失败，详见 lastResult");
        return result;
    }

    // ------------------------------------------------------------------ 状态

    /**
     * 重新加载配置中的预置任务。
     */
    public Map<String, Object> reload() {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!isEnabled()) {
            result.put("success", false);
            result.put("message", "调度器未启用：请先设置 agentscope.scheduler.enabled=true 或调用 POST /agentscope/scheduler/enable");
            return result;
        }
        futures.values().forEach(future -> future.cancel(false));
        futures.clear();
        tasks.clear();
        registerConfiguredTasks();
        result.put("success", true);
        result.put("message", "已重新加载调度任务，共 " + tasks.size() + " 个");
        result.put("tasks", taskViews());
        return result;
    }

    /**
     * 状态面板数据（与能力 detail() 一致）。
     */
    public Map<String, Object> status() {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("capability", "scheduler");
        detail.put("capabilityName", "调度器");
        detail.put("ready", isReady());
        detail.put("switch", switchInfo());

        List<Map<String, Object>> dependencies = new ArrayList<>();
        dependencies.add(dependency("JDK ScheduledExecutorService（进程内调度）", true,
                "无需额外依赖，任务随应用进程存活，重启后需要重新注册"));
        dependencies.add(dependency("agentscope-extensions-scheduler-quartz 扩展包", false,
                "提供与 Quartz 打通的任务持久化 / 集群 / misfire 处理，当前分支未引入"));
        dependencies.add(dependency("项目已有 Quartz（extend-quartz 模块）", false,
                "位于其它模块，extend-agentscope 不直接依赖，避免模块耦合"));
        detail.put("dependencies", dependencies);

        detail.put("tasks", taskViews());
        detail.put("taskCount", tasks.size());
        detail.put("isolation", "进程内调度（内存任务表 + 守护线程池）");
        detail.put("isolationLabel", "进程内调度：任务不持久化，重启后需重新注册；生产建议打通 Quartz");
        detail.put("cronSupport", "支持 5 段（分 时 日 月 周）或 6 段（秒 分 时 日 月 周）；支持 * ? 数字 区间 列表 */n 步长");
        detail.put("missing", missingItems());
        detail.put("configExample", YAML_EXAMPLE);
        detail.put("notes", List.of(
                "触发时调用模块内已装配的 basicAssistantAgent 执行 prompt，并记录结果摘要与执行次数",
                "新增任务：POST /agentscope/scheduler/task；删除任务：DELETE /agentscope/scheduler/task/{name}",
                "生产环境建议引入 agentscope-extensions-scheduler-quartz，与项目已有 Quartz 打通"));
        return detail;
    }

    // ------------------------------------------------------------------ 内部方法

    /**
     * 注册配置中的预置任务。
     */
    private void registerConfiguredTasks() {
        List<AgentScopePropertiesConfig.SchedulerTaskConfig> configs = properties.getScheduler().getTasks();
        if (configs == null) {
            return;
        }
        for (AgentScopePropertiesConfig.SchedulerTaskConfig config : configs) {
            if (config == null || config.getName() == null || config.getName().isBlank()) {
                continue;
            }
            try {
                register(config.getName(), config.getCron(), config.getPrompt(), config.getSessionId());
            } catch (Exception e) {
                log.warn("调度任务[{}]注册失败：{}", config.getName(), e.getMessage());
            }
        }
    }

    /**
     * 安排下一次执行。
     */
    private void schedule(ScheduledTask task, boolean replace) {
        if (replace) {
            ScheduledFuture<?> old = futures.remove(task.getName());
            if (old != null) {
                old.cancel(false);
            }
        }
        if (!isEnabled()) {
            task.setStatus("PAUSED");
            return;
        }
        try {
            CronExpression expression = CronExpression.parse(task.getCron());
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime next = expression.next(now);
            task.setNextFireTime(next.format(FMT));
            task.setStatus("ACTIVE");
            long delay = Math.max(0, Duration.between(now, next).toMillis());
            ScheduledFuture<?> future = executor.schedule(() -> fireAndReschedule(task), delay, TimeUnit.MILLISECONDS);
            futures.put(task.getName(), future);
        } catch (Exception e) {
            task.setStatus("ERROR");
            task.setNextFireTime(null);
            log.warn("调度任务[{}]排期失败：{}", task.getName(), e.getMessage());
        }
    }

    /**
     * 触发并重新排期。
     */
    private void fireAndReschedule(ScheduledTask task) {
        futures.remove(task.getName());
        try {
            fire(task);
        } finally {
            schedule(task, false);
        }
    }

    /**
     * 执行一次任务：调用 Agent 并记录结果。
     */
    private boolean fire(ScheduledTask task) {
        task.setLastFireTime(LocalDateTime.now().format(FMT));
        try {
            RuntimeContext ctx = RuntimeContext.builder()
                    .sessionId(task.getSessionId())
                    .userId("scheduler")
                    .build();
            Msg reply = assistantAgent.call(task.getPrompt(), ctx).block(Duration.ofSeconds(timeoutSeconds()));
            String text = reply == null ? "" : reply.getTextContent();
            task.setExecCount(task.getExecCount() + 1);
            task.setLastResult(truncate(text));
            return true;
        } catch (Exception e) {
            log.warn("调度任务[{}]执行失败", task.getName(), e);
            task.setErrorCount(task.getErrorCount() + 1);
            task.setLastResult("执行失败：" + rootMessage(e));
            return false;
        }
    }

    private List<Map<String, Object>> taskViews() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ScheduledTask task : tasks.values()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", task.getName());
            item.put("cron", task.getCron());
            item.put("prompt", task.getPrompt());
            item.put("sessionId", task.getSessionId());
            item.put("status", task.getStatus());
            item.put("nextFireTime", task.getNextFireTime());
            item.put("lastFireTime", task.getLastFireTime());
            item.put("lastResult", task.getLastResult());
            item.put("execCount", task.getExecCount());
            item.put("errorCount", task.getErrorCount());
            item.put("createdAt", task.getCreatedAt());
            list.add(item);
        }
        return list;
    }

    private List<String> missingItems() {
        List<String> missing = new ArrayList<>();
        if (!isEnabled()) {
            missing.add("开关未打开：agentscope.scheduler.enabled=false");
        }
        if (tasks.isEmpty()) {
            missing.add("没有调度任务：调用 POST /agentscope/scheduler/task 或配置 agentscope.scheduler.tasks");
        }
        missing.add("任务未持久化：生产环境建议引入 agentscope-extensions-scheduler-quartz 与项目 Quartz 打通");
        return missing;
    }

    private int timeoutSeconds() {
        int timeout = properties.getScheduler().getTimeoutSeconds();
        return timeout > 0 ? timeout : 120;
    }

    private String truncate(String text) {
        if (text == null) {
            return "";
        }
        return text.length() > SUMMARY_LIMIT ? text.substring(0, SUMMARY_LIMIT) + "…" : text;
    }

    private Map<String, Object> dependency(String name, boolean ready, String hint) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("ready", ready);
        item.put("hint", hint);
        return item;
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
