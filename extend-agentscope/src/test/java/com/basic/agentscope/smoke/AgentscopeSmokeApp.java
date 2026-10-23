package com.basic.agentscope.smoke;

import com.basic.agentscope.web.AgentController;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.Version;
import io.agentscope.core.tool.Toolkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 端到端冒烟测试入口：只加载 extend-agentscope 模块自身，验证
 *   1. agentscope-spring-boot-starter 自动装配 ReActAgent Bean
 *   2. agentscope-openai-spring-boot-starter 自动装配 OpenAIChatModel
 *   3. 业务 Controller 能成功注入并响应
 */
@SpringBootApplication
@SuppressWarnings({"rawtypes", "unchecked"})
@ComponentScan(
        basePackages = {"com.basic.agentscope"},
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        AgentController.class,
                        com.basic.agentscope.config.AgentScopeConfig.class
                })
        }
)
public class AgentscopeSmokeApp implements CommandLineRunner {

    @Autowired
    private ReActAgent agent;

    @Autowired
    private Toolkit toolkit;

    public static void main(String[] args) {
        SpringApplication.run(AgentscopeSmokeApp.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println();
        System.out.println("=========================================================");
        System.out.println("  AgentScope 2.x 集成冒烟测试结果");
        System.out.println("=========================================================");
        System.out.println("  AgentScope version : " + Version.VERSION);
        System.out.println("  ReActAgent class   : " + agent.getClass().getName());
        System.out.println("  Model              : " + agent.getModel().getClass().getName());
        System.out.println("  Toolkit            : " + toolkit.getClass().getName());
        System.out.println("=========================================================");
    }
}
