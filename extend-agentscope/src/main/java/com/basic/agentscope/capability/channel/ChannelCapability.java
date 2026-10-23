package com.basic.agentscope.capability.channel;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * 能力：<b>渠道接入</b>（外部依赖型）。
 *
 * <p>企微 / 钉钉 / 飞书等 IM 通道在 AgentScope 生态里由扩展包提供，
 * 本分支<b>不引入</b>那些扩展 jar，因此本能力提供"装配骨架 + 状态面板"：</p>
 * <ul>
 *   <li>渠道绑定注册表（渠道类型 + webhook 路径 + 绑定的 sessionId）；</li>
 *   <li>可真实跑通的回调入口：{@code POST /agentscope/channel/webhook/{channelType}}；</li>
 *   <li>状态面板：已注册渠道、webhook 地址、累计收发消息数。</li>
 * </ul>
 *
 * <p>⚠️ <b>不创建 Agent Bean</b>：渠道能力的 Agent 复用模块内已装配的
 * {@code basicAssistantAgent}，本身不再声明 Agent Bean，
 * 因此通用接口 {@code /agentscope/capability/channel/chat} 会返回"能力不可用（缺少 Agent Bean）"。</p>
 */
@Configuration
public class ChannelCapability implements CapabilityProvider {

    /** 外部依赖说明 */
    public static final String REQUIRES = "需要引入 agentscope-extensions-channel-* 扩展包 + 企业 IM 凭证";

    private final ChannelService channelService;

    public ChannelCapability(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "channel",
                "渠道接入",
                CapabilityCategory.EXTERNAL.getCode(),
                CapabilityCategory.EXTERNAL.getLabel(),
                "把 Agent 接入企微/钉钉/飞书等 IM 渠道，本分支提供绑定骨架与回调协议模拟",
                "ChatRound",
                REQUIRES,
                channelService.isReady(),
                false,
                null,
                "/agentscope/capability/channel/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("text", "用户消息", "你好，帮我查一下系统用户数"),
                        CapabilityInfo.ParamInfo.text("channelType", "渠道类型", "wecom"),
                        CapabilityInfo.ParamInfo.text("sessionId", "会话 ID（可留空）", "")
                ));
    }

    @Override
    public String agentBeanName() {
        // 复用 basicAssistantAgent，不新建 Agent Bean
        return null;
    }

    @Override
    public Map<String, Object> detail() {
        return channelService.status();
    }
}
