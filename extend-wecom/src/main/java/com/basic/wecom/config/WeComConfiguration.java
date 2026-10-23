package com.basic.wecom.config;

import com.basic.common.core.redis.RedisCache;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 装配 WxCpService：每个自建应用一个实例，注入 Spring 容器。
 * <p>
 * 设计原则：
 * 1. 不使用 WxJava 的 spring-boot-starter（只支持 SB 2.x，SB 4 上自动配置不生效）
 * 2. 不在 @PostConstruct 里调任何企微接口，启动时不打网络
 * 3. access_token 真正使用时才懒加载（WxJava 内部已经做了过期检查）
 */
@Configuration
@EnableConfigurationProperties(WeComProperties.class)
public class WeComConfiguration {

    private static final Logger log = LoggerFactory.getLogger(WeComConfiguration.class);

    private final WeComProperties properties;
    private final RedisCache redisCache;

    /**
     * 构造器注入配置与 Redis 工具。
     *
     * @param properties 企微配置（来自 basic.wecom）
     * @param redisCache 项目现有 Redis 工具（用于 token 持久化）
     */
    public WeComConfiguration(WeComProperties properties, RedisCache redisCache) {
        this.properties = properties;
        this.redisCache = redisCache;
    }

    /**
     * 装配所有自建应用对应的 WxCpService，组成 agentId → Service 的 map。
     * <p>
     * 调用方按 agentId 取：
     * <pre>
     * &#64;Resource
     * private Map&lt;Long, WxCpService&gt; wxCpServiceMap;
     * // 或
     * &#64;Qualifier("wxCpServiceMap")
     * private Map&lt;Long, WxCpService&gt; wxCpServiceMap;
     * </pre>
     * <p>
     * 不使用 WxJava 的 spring-boot-starter（只支持 SB 2.x，SB 4 上自动配置不生效），
     * 所以这里手写装配逻辑。
     *
     * @return 以 agentId 为 key 的 WxCpService 池；若未配置则返回空 map
     */
    @Bean(name = "wxCpServiceMap")
    public Map<Long, WxCpService> wxCpServiceMap() {
        Map<Long, WxCpService> map = new HashMap<>();
        if (properties.getApps() == null) {
            log.warn("[wecom] basic.wecom.apps 未配置，企微相关功能将不可用");
            return map;
        }
        for (WeComProperties.App app : properties.getApps()) {
            RedisWxCpConfigStorage storage = new RedisWxCpConfigStorage(
                    redisCache, properties.getCorpId(), app.getAgentId());
            storage.setCorpSecret(app.getSecret());

            WxCpServiceImpl service = new WxCpServiceImpl();
            service.setWxCpConfigStorage(storage);
            map.put(app.getAgentId(), service);
            log.info("[wecom] 装配 agentId={} name={}", app.getAgentId(), app.getName());
        }
        return map;
    }
}
