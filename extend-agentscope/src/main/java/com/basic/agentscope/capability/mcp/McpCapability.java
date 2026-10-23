package com.basic.agentscope.capability.mcp;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * 能力：<b>MCP 客户端</b>（外部依赖型）。
 *
 * <p>MCP（Model Context Protocol）让 Agent 复用外部工具/数据源。本能力负责把配置的
 * MCP Server 接入到共享 Toolkit，并把 Server 清单、工具清单、连接状态暴露给前端。</p>
 *
 * <p>⚠️ <b>不创建 Agent Bean</b>：本机没有 MCP Server，依赖未就绪时不应该存在 Agent，
 * 因此 {@link #agentBeanName()} 返回 {@code null}，
 * 通用接口 {@code /agentscope/capability/mcp/chat} 会返回"能力不可用（缺少 Agent Bean）"。</p>
 *
 * <p>状态面板：{@code GET /agentscope/mcp/status}。</p>
 */
@Configuration
public class McpCapability implements CapabilityProvider {

    /** 外部依赖说明 */
    public static final String REQUIRES = "需要可访问的 MCP Server（stdio 命令 / SSE / Streamable HTTP 地址）";

    private final McpService mcpService;

    public McpCapability(McpService mcpService) {
        this.mcpService = mcpService;
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "mcp",
                "MCP 客户端",
                CapabilityCategory.EXTERNAL.getCode(),
                CapabilityCategory.EXTERNAL.getLabel(),
                "接入外部 MCP Server，把其工具挂到共享 Toolkit，实时探测连接就绪状态",
                "Cpu",
                REQUIRES,
                mcpService.isReady(),
                false,
                null,
                "/agentscope/capability/mcp/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "你的问题", "列出当前已接入的 MCP 工具"),
                        CapabilityInfo.ParamInfo.text("sessionId", "会话 ID（可留空）", "")
                ));
    }

    @Override
    public String agentBeanName() {
        // 依赖未就绪时不允许存在 Agent
        return null;
    }

    @Override
    public Map<String, Object> detail() {
        return mcpService.status();
    }
}
