package com.basic.agentscope.capability.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 调度任务 —— 任务名 + cron 表达式 + prompt + 下次执行时间 + 累计执行次数。
 */
public class ScheduledTask {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 任务名（唯一） */
    private String name;

    /** cron 表达式 */
    private String cron;

    /** 触发时交给 Agent 的 prompt */
    private String prompt;

    /** 任务使用的会话 ID */
    private String sessionId;

    /** 下次执行时间 */
    private String nextFireTime;

    /** 最近一次执行时间 */
    private String lastFireTime;

    /** 最近一次执行结果摘要 */
    private String lastResult;

    /** 累计执行次数 */
    private long execCount;

    /** 累计失败次数 */
    private long errorCount;

    /** 状态：ACTIVE / PAUSED */
    private String status = "ACTIVE";

    /** 创建时间 */
    private String createdAt;

    public ScheduledTask() {
        this.createdAt = LocalDateTime.now().format(FMT);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(String nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public String getLastFireTime() {
        return lastFireTime;
    }

    public void setLastFireTime(String lastFireTime) {
        this.lastFireTime = lastFireTime;
    }

    public String getLastResult() {
        return lastResult;
    }

    public void setLastResult(String lastResult) {
        this.lastResult = lastResult;
    }

    public long getExecCount() {
        return execCount;
    }

    public void setExecCount(long execCount) {
        this.execCount = execCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
