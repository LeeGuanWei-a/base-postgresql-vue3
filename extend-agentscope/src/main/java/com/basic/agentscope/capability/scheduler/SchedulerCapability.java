package com.basic.agentscope.capability.scheduler;

import com.basic.agentscope.capability.CapabilityCategory;
import com.basic.agentscope.capability.CapabilityInfo;
import com.basic.agentscope.capability.CapabilityProvider;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * 能力：<b>调度器</b>（外部依赖型）。
 *
 * <p>按 cron 定时触发 Agent 执行 prompt。本实现为<b>进程内调度</b>：
 * 用 JDK 自带 {@code ScheduledExecutorService} + 自实现的简化 cron 解析器，
 * <b>不</b>在 {@code extend-agentscope} 引入 Quartz 依赖（避免与 {@code extend-quartz} 模块耦合）。</p>
 *
 * <p>生产建议：引入 {@code agentscope-extensions-scheduler-quartz}，与项目已有 Quartz 打通。</p>
 *
 * <p>⚠️ <b>不创建 Agent Bean</b>：调度任务复用模块内已装配的 {@code basicAssistantAgent}，
 * 通用接口 {@code /agentscope/capability/scheduler/chat} 会返回"能力不可用（缺少 Agent Bean）"。</p>
 */
@Configuration
public class SchedulerCapability implements CapabilityProvider {

    /** 外部依赖说明 */
    public static final String REQUIRES = "需要引入 agentscope-extensions-scheduler-quartz 扩展包，并与项目已有 Quartz 打通";

    private final SchedulerService schedulerService;

    public SchedulerCapability(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Override
    public CapabilityInfo descriptor() {
        return new CapabilityInfo(
                "scheduler",
                "调度器",
                CapabilityCategory.EXTERNAL.getCode(),
                CapabilityCategory.EXTERNAL.getLabel(),
                "按 cron 定时触发 Agent 执行 prompt，进程内调度骨架（可对接项目已有 Quartz）",
                "Timer",
                REQUIRES,
                schedulerService.isReady(),
                false,
                null,
                "/agentscope/capability/scheduler/detail",
                List.of(
                        CapabilityInfo.ParamInfo.required("prompt", "定时任务内容", "汇总昨日业务数据"),
                        CapabilityInfo.ParamInfo.text("cron", "cron 表达式（5 或 6 段）", "0 0 9 * * ?"),
                        CapabilityInfo.ParamInfo.text("sessionId", "会话 ID（可留空）", "")
                ));
    }

    @Override
    public String agentBeanName() {
        // 调度任务复用 basicAssistantAgent，不新建 Agent Bean
        return null;
    }

    @Override
    public Map<String, Object> detail() {
        return schedulerService.status();
    }
}
