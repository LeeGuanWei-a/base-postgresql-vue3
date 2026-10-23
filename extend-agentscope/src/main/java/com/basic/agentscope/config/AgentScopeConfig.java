package com.basic.agentscope.config;

import com.basic.agentscope.tool.PlatformTools;
import com.basic.agentscope.tool.SystemTools;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.spring.boot.properties.AgentProperties;
import io.agentscope.spring.boot.properties.AgentscopeProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AgentScope Java 2.x 业务侧总装配。
 *
 * <h3>为什么这里要自己声明 Agent / Toolkit？</h3>
 *
 * <p>官方 starter 的 {@code AgentscopeAutoConfiguration} 里有三个 Bean：</p>
 * <ul>
 *   <li>{@code agentscopeMemory} —— {@code @Scope("prototype")}</li>
 *   <li>{@code agentscopeToolkit} —— {@code @Scope("prototype")}</li>
 *   <li>{@code agentscopeReActAgent} —— 带 {@code @ConditionalOnMissingBean(ReActAgent.class)}</li>
 * </ul>
 *
 * <p>本模块为每个能力都声明了专属的 {@code ReActAgent}，而
 * <b>用户 Bean 总是先于自动配置注册</b>，于是：</p>
 * <ol>
 *   <li>starter 的 {@code agentscopeReActAgent} 被 {@code @ConditionalOnMissingBean} 顶掉 → 官方默认 Agent 消失；</li>
 *   <li>starter 的 Toolkit 是 <b>prototype 作用域</b>，每次注入都是新实例 →
 *       在 A 处注册的工具在 B 处不可见，工具永远注册不上去。</li>
 * </ol>
 *
 * <p>因此这里显式提供：</p>
 * <ul>
 *   <li>{@code businessToolkit} —— <b>单例</b>共享 Toolkit，工具注册对所有 Agent 生效；</li>
 *   <li>{@code basicAssistantAgent} —— 等价于官方默认 Agent 的替代品，
 *       复用 {@code agentscope.agent.*} 配置（name / sys-prompt / max-iters）。</li>
 * </ul>
 *
 * <p>⚠️ 本模块内存在<b>多个</b> {@code ReActAgent} 类型的 Bean，
 * 任何地方注入 {@code ReActAgent} 都<b>必须</b>带 {@code @Qualifier}。</p>
 */
@Configuration
public class AgentScopeConfig {

    /**
     * 业务共享 Toolkit（单例）。
     *
     * <p>声明它会顶掉 starter 的 prototype 版本 {@code agentscopeToolkit}（同类型），
     * 这正是我们想要的：工具必须对所有 Agent 可见。</p>
     */
    @Bean("businessToolkit")
    public Toolkit businessToolkit(PlatformTools platformTools, SystemTools systemTools) {
        Toolkit toolkit = new Toolkit();
        // 工具方法需标注 @Tool(name = "...", description = "...")，
        // 框架会自动生成 JSON Schema 交给模型做 function calling。
        toolkit.registerTool(platformTools);
        toolkit.registerTool(systemTools);
        return toolkit;
    }

    /**
     * 默认业务助手 Agent —— 官方 {@code agentscopeReActAgent} 的替代品。
     */
    @Bean("basicAssistantAgent")
    public ReActAgent basicAssistantAgent(ModelResolver modelResolver,
                                          AgentscopeProperties properties,
                                          @Qualifier("businessToolkit") Toolkit toolkit) {
        AgentProperties agent = properties.getAgent();
        int maxIters = agent.getMaxIters() > 0 ? agent.getMaxIters() : 10;
        return ReActAgent.builder()
                .name(agent.getName() == null || agent.getName().isBlank() ? "basic-assistant" : agent.getName())
                .sysPrompt(agent.getSysPrompt())
                .model(modelResolver.resolve())
                .toolkit(toolkit)
                .maxIters(maxIters)
                .build();
    }
}
