package com.basic.agentscope.capability.scheduler;

import com.basic.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 调度器状态控制器。
 *
 * <ul>
 *   <li>{@code GET    /agentscope/scheduler/status} —— 任务列表 + 最近一次执行结果</li>
 *   <li>{@code POST   /agentscope/scheduler/task} —— 新增（或覆盖）调度任务</li>
 *   <li>{@code DELETE /agentscope/scheduler/task/{name}} —— 删除调度任务</li>
 *   <li>{@code POST   /agentscope/scheduler/task/{name}/run} —— 立即执行一次（验证链路）</li>
 *   <li>{@code POST   /agentscope/scheduler/reload} —— 重新加载配置中的任务</li>
 *   <li>{@code POST   /agentscope/scheduler/enable} / {@code /disable} —— 内存开关</li>
 * </ul>
 */
@RestController
@RequestMapping("/agentscope/scheduler")
public class SchedulerStatusController {

    private final SchedulerService schedulerService;

    public SchedulerStatusController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    /**
     * 状态面板（与能力 detail() 内容一致）。
     */
    @GetMapping("/status")
    public AjaxResult status() {
        return AjaxResult.success(schedulerService.status());
    }

    /**
     * 新增（或覆盖）一个调度任务。
     */
    @PostMapping("/task")
    public AjaxResult addTask(@RequestBody(required = false) ScheduledTask task) {
        if (task == null) {
            return AjaxResult.error("请求体不能为空");
        }
        try {
            ScheduledTask registered = schedulerService.register(task.getName(), task.getCron(),
                    task.getPrompt(), task.getSessionId());
            return AjaxResult.success(registered);
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 删除一个调度任务。
     */
    @DeleteMapping("/task/{name}")
    public AjaxResult removeTask(@PathVariable("name") String name) {
        boolean removed = schedulerService.remove(name);
        if (!removed) {
            return AjaxResult.error("任务不存在：" + name);
        }
        return AjaxResult.success("已删除任务：" + name);
    }

    /**
     * 立即执行一次任务（用于验证"调度 → Agent"链路）。
     */
    @PostMapping("/task/{name}/run")
    public AjaxResult runTask(@PathVariable("name") String name) {
        Map<String, Object> result = schedulerService.runNow(name);
        if (!Boolean.TRUE.equals(result.get("success"))) {
            return AjaxResult.error(String.valueOf(result.get("message")), result);
        }
        return AjaxResult.success(result);
    }

    /**
     * 重新加载配置中的预置任务。
     */
    @PostMapping("/reload")
    public AjaxResult reload() {
        Map<String, Object> result = schedulerService.reload();
        if (!Boolean.TRUE.equals(result.get("success"))) {
            return AjaxResult.error(String.valueOf(result.get("message")), result);
        }
        return AjaxResult.success(result);
    }

    /**
     * 内存启用（重启后失效，回到配置值 {@code agentscope.scheduler.enabled}）。
     */
    @PostMapping("/enable")
    public AjaxResult enable() {
        schedulerService.enable();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("switch", schedulerService.switchInfo());
        data.put("status", schedulerService.status());
        return AjaxResult.success(data);
    }

    /**
     * 内存禁用（重启后失效，回到配置值 {@code agentscope.scheduler.enabled}）。
     */
    @PostMapping("/disable")
    public AjaxResult disable() {
        schedulerService.disable();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("switch", schedulerService.switchInfo());
        data.put("status", schedulerService.status());
        return AjaxResult.success(data);
    }
}
