package com.basic.agentscope.capability.channel;

import com.basic.common.annotation.Anonymous;
import com.basic.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 渠道接入状态控制器。
 *
 * <ul>
 *   <li>{@code GET    /agentscope/channel/status} —— 渠道清单 / webhook 地址 / 收发统计</li>
 *   <li>{@code POST   /agentscope/channel/reload} —— 重新加载配置中的渠道绑定</li>
 *   <li>{@code POST   /agentscope/channel/enable} / {@code /disable} —— 内存开关</li>
 *   <li>{@code POST   /agentscope/channel/binding} —— 注册渠道绑定</li>
 *   <li>{@code DELETE /agentscope/channel/binding/{channelType}} —— 注销渠道绑定</li>
 *   <li>{@code POST   /agentscope/channel/webhook/{channelType}} —— 渠道回调入口（真实跑通）</li>
 * </ul>
 */
@RestController
@RequestMapping("/agentscope/channel")
public class ChannelStatusController {

    private final ChannelService channelService;

    public ChannelStatusController(ChannelService channelService) {
        this.channelService = channelService;
    }

    /**
     * 状态面板（与能力 detail() 内容一致）。
     */
    @GetMapping("/status")
    public AjaxResult status() {
        return AjaxResult.success(channelService.status());
    }

    /**
     * 重新加载配置中的渠道绑定。
     */
    @PostMapping("/reload")
    public AjaxResult reload() {
        Map<String, Object> result = channelService.reload();
        if (!Boolean.TRUE.equals(result.get("success"))) {
            return AjaxResult.error(String.valueOf(result.get("message")), result);
        }
        return AjaxResult.success(result);
    }

    /**
     * 内存启用（重启后失效，回到配置值 {@code agentscope.channel.enabled}）。
     */
    @PostMapping("/enable")
    public AjaxResult enable() {
        channelService.enable();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("switch", channelService.switchInfo());
        data.put("status", channelService.status());
        return AjaxResult.success(data);
    }

    /**
     * 内存禁用（重启后失效，回到配置值 {@code agentscope.channel.enabled}）。
     */
    @PostMapping("/disable")
    public AjaxResult disable() {
        channelService.disable();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("switch", channelService.switchInfo());
        data.put("status", channelService.status());
        return AjaxResult.success(data);
    }

    /**
     * 注册渠道绑定。
     */
    @PostMapping("/binding")
    public AjaxResult register(@RequestBody(required = false) ChannelBinding binding) {
        if (binding == null || binding.getChannelType() == null || binding.getChannelType().isBlank()) {
            return AjaxResult.error("channelType 不能为空");
        }
        ChannelBinding registered = channelService.register(binding.getChannelType(),
                binding.getSessionId(), binding.getDescription(), "api");
        return AjaxResult.success(registered);
    }

    /**
     * 注销渠道绑定。
     */
    @DeleteMapping("/binding/{channelType}")
    public AjaxResult unregister(@PathVariable("channelType") String channelType) {
        boolean removed = channelService.unregister(channelType);
        if (!removed) {
            return AjaxResult.error("渠道未注册：" + channelType);
        }
        return AjaxResult.success("已注销渠道：" + channelType);
    }

    /**
     * 渠道回调入口 —— 模拟 IM 平台推送消息。
     *
     * <p>收到文本 → 调用 Agent → 返回 Agent 的回答，链路真实跑通。</p>
     *
     * <p>标注 {@code @Anonymous}：真实 IM 平台回调不携带登录令牌，需要匿名可访问；
     * 生产环境请在网关层补签名校验与限流。</p>
     */
    @Anonymous
    @PostMapping("/webhook/{channelType}")
    public AjaxResult webhook(@PathVariable("channelType") String channelType,
                              @RequestBody(required = false) ChannelWebhookRequest body,
                              @RequestParam(value = "text", required = false) String textParam) {
        String text = (body != null && body.getText() != null && !body.getText().isBlank())
                ? body.getText() : textParam;
        if (text == null || text.isBlank()) {
            return AjaxResult.error("text 不能为空");
        }
        String userId = body == null ? null : body.getUserId();
        String sessionId = body == null ? null : body.getSessionId();
        Map<String, Object> result = channelService.handleMessage(channelType, text, userId, sessionId);
        if (!Boolean.TRUE.equals(result.get("success"))) {
            return AjaxResult.error(String.valueOf(result.get("message")), result);
        }
        return AjaxResult.success(result);
    }
}
