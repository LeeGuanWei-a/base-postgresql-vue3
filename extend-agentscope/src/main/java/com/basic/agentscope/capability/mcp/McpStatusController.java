package com.basic.agentscope.capability.mcp;

import com.basic.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MCP 客户端状态控制器。
 *
 * <ul>
 *   <li>{@code GET  /agentscope/mcp/status} —— 依赖就绪状态 / Server 清单 / 配置示例</li>
 *   <li>{@code POST /agentscope/mcp/reload} —— 重新连接并注册已配置的 MCP Server</li>
 *   <li>{@code POST /agentscope/mcp/enable} / {@code /disable} —— 内存开关</li>
 * </ul>
 */
@RestController
@RequestMapping("/agentscope/mcp")
public class McpStatusController {

    private final McpService mcpService;

    public McpStatusController(McpService mcpService) {
        this.mcpService = mcpService;
    }

    /**
     * 状态面板（与能力 detail() 内容一致）。
     */
    @GetMapping("/status")
    public AjaxResult status() {
        return AjaxResult.success(mcpService.status());
    }

    /**
     * 重新连接 / 注册全部已配置的 MCP Server。
     *
     * <p>连接失败不会抛 500 堆栈：每个 Server 的失败原因都记录在返回结果里。</p>
     */
    @PostMapping("/reload")
    public AjaxResult reload() {
        Map<String, Object> result = mcpService.reload();
        if (!Boolean.TRUE.equals(result.get("success"))) {
            return AjaxResult.error(String.valueOf(result.get("message")), result);
        }
        return AjaxResult.success(result);
    }

    /**
     * 内存启用（重启后失效，回到配置值 {@code agentscope.mcp.enabled}）。
     */
    @PostMapping("/enable")
    public AjaxResult enable() {
        mcpService.enable();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("switch", mcpService.switchInfo());
        data.put("status", mcpService.status());
        return AjaxResult.success(data);
    }

    /**
     * 内存禁用（重启后失效，回到配置值 {@code agentscope.mcp.enabled}）。
     */
    @PostMapping("/disable")
    public AjaxResult disable() {
        mcpService.disable();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("switch", mcpService.switchInfo());
        data.put("status", mcpService.status());
        return AjaxResult.success(data);
    }
}
