package com.basic.agentscope.capability.failover;

import com.basic.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 故障转移演示开关 —— 让前端可以在"主模型正常"与"主模型故障"两种模式之间切换。
 *
 * <p>这不是能力本身（能力是 {@link FailoverCapability}），而是它的辅助操作入口：</p>
 * <ul>
 *   <li>{@code GET  /agentscope/failover/status}：查看当前模式与故障转移计数；</li>
 *   <li>{@code POST /agentscope/failover/primary-failure?enabled=true|false}：开关故障注入。</li>
 * </ul>
 */
@RestController
@RequestMapping("/agentscope/failover")
public class FailoverSimulationController {

    @Autowired
    @Qualifier("failoverCapability")
    private FailoverCapability capability;

    /**
     * 当前故障转移状态。
     */
    @GetMapping("/status")
    public AjaxResult status() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("primaryFailureInjected", capability.isPrimaryFailureInjected());
        data.put("detail", capability.detail());
        return AjaxResult.success(data);
    }

    /**
     * 开关"主模型故障注入"。
     */
    @PostMapping("/primary-failure")
    public AjaxResult primaryFailure(@RequestParam(value = "enabled", defaultValue = "true") boolean enabled) {
        boolean ok = capability.setPrimaryFailureInjected(enabled);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("primaryFailureInjected", capability.isPrimaryFailureInjected());
        data.put("changed", ok);
        data.put("hint", enabled
                ? "已开启故障注入：调用该能力将真实演示「主模型失败 → 切备用 → 备用也失败 → 最终报错」"
                : "已关闭故障注入：主模型将使用 ModelResolver 提供的真实模型正常应答");
        return AjaxResult.success(data);
    }
}
