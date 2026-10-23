package com.basic.agentscope.capability.sandbox;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * 能力：<b>沙箱执行</b>（外部依赖型）。
 *
 * <p>沙箱用于让 Agent 安全地执行代码 / 命令。本分支<b>没有</b>容器运行时，
 * 也不引入 {@code agentscope-extensions-sandbox-*} 扩展包，因此本能力提供
 * "执行请求的装配骨架 + 安全策略说明"：</p>
 * <ul>
 *   <li>只放行白名单中的只读命令（默认 {@code java -version} / {@code echo}）；</li>
 *   <li>白名单命令用 {@code ProcessBuilder} 真实执行（带超时）；</li>
 *   <li>其余命令一律拒绝，并提示"生产环境应接入 agentscope-extensions-sandbox-* 由容器/K8s 隔离执行"。</li>
 * </ul>
 *
 * <p>⚠️ <b>不创建 Agent Bean</b>：沙箱本身不产生 Agent，
 * 通用接口 {@code /agentscope/capability/sandbox/chat} 会返回"能力不可用（缺少 Agent Bean）"。</p>
 */
@Configuration
public class SandboxCapability implements CapabilityProvider {

    /** 外部依赖说明 */
    public static final String REQUIRES = "需要引入 agentscope-extensions-sandbox-* 扩展包 + Docker/K8s 隔离运行时";

    private final SandboxService sandboxService;

    public SandboxCapability(SandboxService sandboxService) {
        this.sandboxService = sandboxService;
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "sandbox",
                "沙箱执行",
                CapabilityCategory.EXTERNAL.getCode(),
                CapabilityCategory.EXTERNAL.getLabel(),
                "受限执行只读命令：白名单 + 超时 + 拒绝任意系统命令，并给出真沙箱接入方案",
                "Box",
                REQUIRES,
                sandboxService.isReady(),
                false,
                null,
                "/agentscope/capability/sandbox/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("command", "待执行命令", "java -version")
                ));
    }

    @Override
    public String agentBeanName() {
        // 沙箱不产生 Agent
        return null;
    }

    @Override
    public Map<String, Object> detail() {
        return sandboxService.status();
    }
}
