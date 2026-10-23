package com.basic.flowable.config;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.HistoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable 8.0 引擎装配校验。
 * <p>
 * Flowable Spring Boot Starter 启动时会自动建表 + 装配 ProcessEngine
 * 及各 Service（Repository / Runtime / Task / History …）。
 * 这里只做一个启动期 sanity check，把各 Service 显式拿出来，
 * 一旦哪个 bean 没装配好，单元测试 / 启动日志会立刻爆错，
 * 避免到运行时再发现少 bean。
 *
 * @author basic
 */
@Slf4j
@Configuration
public class FlowableEngineConfig {

    /**
     * @return ApplicationStartupVerifier 启动校验占位 bean
     * @description 启动时显式注入各 Flowable Service 并打印引擎版本，任一 bean 未装配好就会启动失败，
     * 避免等到运行期才发现漏 bean；版本取静态字段 ProcessEngine.VERSION
     * @params [processEngine, repositoryService, runtimeService, taskService, historyService] 流程引擎与仓储/运行时/任务/历史服务
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Bean
    public ApplicationStartupVerifier flowableStartupVerifier(
            ProcessEngine processEngine,
            RepositoryService repositoryService,
            RuntimeService runtimeService,
            TaskService taskService,
            HistoryService historyService) {
        // Flowable 8.0 沿用 7.x 接口设计：版本是静态字段 ProcessEngine.VERSION，
        // 不是 getProcessEngineVersion()（那是 Activiti 5 / 早期 Flowable 的接口，已废）
        log.info("[Flowable] 引擎启动完成：processEngine={}, version={}",
                processEngine.getName(), ProcessEngine.VERSION);
        return new ApplicationStartupVerifier();
    }

    /**
     * 仅作为 sanity check 锚点存在，业务代码不依赖它。
     */
    public static class ApplicationStartupVerifier {
    }
}
