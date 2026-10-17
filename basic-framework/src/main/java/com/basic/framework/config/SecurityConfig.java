package com.basic.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;
import com.basic.framework.config.properties.PermitAllUrlProperties;
import com.basic.framework.security.filter.JwtAuthenticationTokenFilter;
import com.basic.framework.security.handle.AuthenticationEntryPointImpl;
import com.basic.framework.security.handle.LogoutSuccessHandlerImpl;

/**
 * spring security 配置
 *
 * <p>Spring Boot 3 + Spring Security 6 改造说明：
 *   - 移除 WebSecurityConfigurerAdapter（已废弃），改用 SecurityFilterChain Bean
 *   - 移除 EnableGlobalMethodSecurity，改用 EnableMethodSecurity
 *   - authorizeRequests() 改名为 authorizeHttpRequests()，antMatchers 改为 requestMatchers
 *   - 移除 and() 链式调用，改用 Lambda DSL
 * </p>
 *
 * @author basic
 */
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
public class SecurityConfig {

    /**
     * 认证失败处理类
     */
    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /**
     * 退出处理类
     */
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    /**
     * token认证过滤器
     */
    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    /**
     * 跨域过滤器
     */
    @Autowired
    private CorsFilter corsFilter;

    /**
     * 允许匿名访问的地址
     */
    @Autowired
    private PermitAllUrlProperties permitAllUrl;

    /**
     * @return AuthenticationManager Spring Security 的认证管理器实例
     * @description 从认证配置中取出容器已构建的 AuthenticationManager 并暴露为 Bean
     * @params [authenticationConfiguration] Spring Security 认证配置
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * @return SecurityFilterChain Spring Security 过滤器链
     * @description 关闭 csrf 与缓存标头、改用无状态 session，放行匿名地址并对剩余请求要求鉴权，同时挂载 JWT、跨域与退出过滤器
     * 常用请求匹配器：anyRequest 匹配所有路径；permitAll 任意访问；anonymous 匿名可访问；denyAll 拒绝所有用户；
     * authenticated 登录后可访问；rememberMe 允许 remember-me 登录用户访问；fullyAuthenticated 需完全认证（非 remember-me 自动登录）；
     * hasAuthority / hasAnyAuthority 按权限判定；hasRole / hasAnyRole 按角色判定；hasIpAddress 按请求 IP 判定；
     * access 用 SpringEl 表达式判定，表达式为 true 时可访问。
     * @params [httpSecurity] Spring Security 的 HttpSecurity 构建器
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // CSRF禁用，因为不使用session
                .csrf(csrf -> csrf.disable())
                // 禁用HTTP响应标头
                .headers(headers -> headers.cacheControl(cache -> cache.disable()).frameOptions(options -> options.sameOrigin()))
                // 认证失败处理类
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                // 基于token，所以不需要session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> {
                    // 注解标记允许匿名访问的url
                    if (permitAllUrl != null && permitAllUrl.getUrls() != null) {
                        permitAllUrl.getUrls().forEach(url -> requests.requestMatchers(url).permitAll());
                    }
                    // 对于登录login 注册register 验证码captchaImage 允许匿名访问
                    requests.requestMatchers("/login", "/register", "/captchaImage").permitAll()
                            // 静态资源，可匿名访问
                            .requestMatchers(HttpMethod.GET, "/", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/profile/**").permitAll()
                            // springdoc-openapi
                            .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/druid/**").permitAll()
                            // 除上面外的所有请求全部需要鉴权认证
                            .anyRequest().authenticated();
                })
                // 添加Logout filter
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler))
                // 添加JWT filter
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // 添加CORS filter
                .addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class)
                .addFilterBefore(corsFilter, LogoutFilter.class)
                .build();
    }

    /**
     * @return BCryptPasswordEncoder BCrypt 强散列加密器
     * @description 创建 BCrypt 强散列哈希加密实现并注册为 Bean
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
