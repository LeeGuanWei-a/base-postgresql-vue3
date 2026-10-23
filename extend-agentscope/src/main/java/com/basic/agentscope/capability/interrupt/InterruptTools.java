package com.basic.agentscope.capability.interrupt;

import io.agentscope.core.tool.Tool;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 中断能力专用的「慢任务」工具 —— 让 Agent 的运行过程足够长，便于演示打断。
 *
 * <p>这个工具会阻塞若干秒（默认 5 秒），期间前端可以调用
 * {@code POST /agentscope/interrupt/stop} 把正在运行的 Agent 打断。</p>
 */
public class InterruptTools {

    /** 慢任务耗时（秒） */
    private volatile int delaySeconds = 5;

    /**
     * 设置慢任务耗时。
     */
    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = Math.min(Math.max(delaySeconds, 1), 60);
    }

    /**
     * 获取慢任务耗时。
     */
    public int getDelaySeconds() {
        return delaySeconds;
    }

    /**
     * 模拟一次耗时的平台扫描。
     *
     * @param keyword 扫描关键字
     * @return 扫描结果
     */
    @Tool(name = "slow_platform_scan",
            description = "当用户要求『做一次全量扫描』『跑一遍全量统计』『慢速巡检』时，调用本工具执行一次耗时的平台扫描。"
                    + "参数 keyword 是扫描关键字。扫描本身会阻塞若干秒，可用于演示运行中的任务被打断。",
            readOnly = true)
    public Map<String, Object> slowPlatformScan(String keyword) {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(delaySeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("keyword", keyword);
        result.put("scanned", true);
        result.put("elapsedMs", System.currentTimeMillis() - start);
        result.put("message", "扫描完成（关键字 " + keyword + "）");
        return result;
    }
}
