package com.basic.agentscope.capability.sandbox;

import com.basic.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 沙箱执行状态控制器。
 *
 * <ul>
 *   <li>{@code GET  /agentscope/sandbox/status} —— 启用状态 / 白名单 / 隔离方式</li>
 *   <li>{@code POST /agentscope/sandbox/execute} —— 执行一条白名单命令</li>
 *   <li>{@code POST /agentscope/sandbox/reload} —— 重新加载白名单配置</li>
 *   <li>{@code POST /agentscope/sandbox/enable} / {@code /disable} —— 内存开关</li>
 * </ul>
 */
@RestController
@RequestMapping("/agentscope/sandbox")
public class SandboxStatusController {

    private final SandboxService sandboxService;

    public SandboxStatusController(SandboxService sandboxService) {
        this.sandboxService = sandboxService;
    }

    /**
     * 状态面板（与能力 detail() 内容一致）。
     */
    @GetMapping("/status")
    public AjaxResult status() {
        return AjaxResult.success(sandboxService.status());
    }

    /**
     * 执行一条命令（仅白名单只读命令）。
     */
    @PostMapping("/execute")
    public AjaxResult execute(@RequestBody(required = false) Map<String, Object> body) {
        String command = null;
        if (body != null && body.get("command") != null) {
            command = String.valueOf(body.get("command"));
        }
        if (command == null || command.isBlank()) {
            return AjaxResult.error("command 不能为空");
        }
        Map<String, Object> result = sandboxService.execute(command);
        if (!Boolean.TRUE.equals(result.get("allowed"))) {
            return AjaxResult.error(String.valueOf(result.get("reason")), result);
        }
        return AjaxResult.success(result);
    }

    /**
     * 重新加载白名单配置。
     */
    @PostMapping("/reload")
    public AjaxResult reload() {
        Map<String, Object> result = sandboxService.reload();
        if (!Boolean.TRUE.equals(result.get("success"))) {
            return AjaxResult.error(String.valueOf(result.get("message")), result);
        }
        return AjaxResult.success(result);
    }

    /**
     * 内存启用（重启后失效，回到配置值 {@code agentscope.sandbox.enabled}）。
     */
    @PostMapping("/enable")
    public AjaxResult enable() {
        sandboxService.enable();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("switch", sandboxService.switchInfo());
        data.put("status", sandboxService.status());
        return AjaxResult.success(data);
    }

    /**
     * 内存禁用（重启后失效，回到配置值 {@code agentscope.sandbox.enabled}）。
     */
    @PostMapping("/disable")
    public AjaxResult disable() {
        sandboxService.disable();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("switch", sandboxService.switchInfo());
        data.put("status", sandboxService.status());
        return AjaxResult.success(data);
    }
}
