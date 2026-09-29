package com.basic.framework.config;

import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * yauaa UserAgentAnalyzer 单例配置
 *
 * <p>yauaa 的 UserAgentAnalyzer 创建非常耗时（要加载 100+ 个 yaml 规则文件、
 * 构建 20 万条哈希规则），所以必须做成单例 Bean 全局共用，绝对不能在
 * 每次请求时都 new 一个，否则每次都要白白浪费 0.5 秒左右。
 *
 * @author basic
 */
@Configuration
public class UserAgentConfig
{
    /**
     * 全局共用一个 UserAgentAnalyzer 实例。
     * UserAgentAnalyzer 是线程安全的，可以并发调用 parse()。
     */
    @Bean
    public UserAgentAnalyzer userAgentAnalyzer()
    {
        return UserAgentAnalyzer.newBuilder().build();
    }
}
