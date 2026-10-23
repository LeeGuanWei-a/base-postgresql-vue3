package com.basic.alipay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 装配 AlipayClient：每个 appId 一个实例。
 * <p>
 * 关键点：AlipayClient 是线程安全的，可以单例；构造时即决定"公钥模式"还是"证书模式"。
 * 启动时只读配置 + 加载证书，不发任何网络请求——这与 WxCpService 一样。
 */
@Configuration
@EnableConfigurationProperties(AlipayProperties.class)
public class AlipayConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AlipayConfiguration.class);

    private final AlipayProperties properties;

    /**
     * 构造器注入支付宝配置。
     *
     * @param properties 支付宝配置（来自 basic.alipay）
     */
    public AlipayConfiguration(AlipayProperties properties) {
        this.properties = properties;
    }

    /**
     * 装配所有 appId 对应的 AlipayClient，组成 appId → Client 的 map。
     * <p>
     * 关键点：AlipayClient 线程安全，可单例。
     * 每个 app 构造时即决定走"公钥模式"还是"证书模式"，
     * 二者不能混用——本方法按配置自动判断。
     *
     * @return 以 appId 为 key 的 AlipayClient 池
     * @throws Exception 构造证书模式时如果证书文件读取失败会抛异常
     */
    @Bean(name = "alipayClientMap")
    public Map<String, AlipayClient> alipayClientMap() throws Exception {
        Map<String, AlipayClient> map = new HashMap<>();
        if (properties.getApps() == null) {
            log.warn("[alipay] basic.alipay.apps 未配置，支付宝相关功能将不可用");
            return map;
        }
        for (AlipayProperties.App app : properties.getApps()) {
            AlipayClient client = buildClient(app);
            map.put(app.getAppId(), client);
            log.info("[alipay] 装配 appId={} name={} mode={}",
                    app.getAppId(), app.getName(),
                    isCertMode(app) ? "CERT" : "PUBLIC_KEY");
        }
        return map;
    }

    /**
     * 判断应用是否走证书模式。
     * <p>
     * 三个证书路径必须**全部**配置才进入证书模式，
     * 否则按公钥模式走（要求 alipayPublicKey 必填）。
     *
     * @param app 应用配置
     * @return true 表示证书模式
     */
    private boolean isCertMode(AlipayProperties.App app) {
        return app.getAppCertPath() != null
                && app.getAlipayCertPath() != null
                && app.getAlipayRootCertPath() != null;
    }

    /**
     * 根据配置构造对应的 AlipayClient。
     * <p>
     * 公钥模式用 {@link DefaultAlipayClient} 7 参构造；
     * 证书模式用 {@link CertAlipayRequest} + 3 参构造，SDK 内部加载并解析证书。
     *
     * @param app 应用配置
     * @return 装配好的 AlipayClient（线程安全）
     * @throws Exception 证书读取失败时抛出
     */
    private AlipayClient buildClient(AlipayProperties.App app) throws Exception {
        if (isCertMode(app)) {
            CertAlipayRequest req = new CertAlipayRequest();
            req.setServerUrl(properties.getGateway());
            req.setAppId(app.getAppId());
            req.setPrivateKey(app.getPrivateKey());
            req.setFormat("json");
            req.setCharset(properties.getCharset());
            req.setSignType(properties.getSignType());
            req.setCertPath(app.getAppCertPath());
            req.setAlipayPublicCertPath(app.getAlipayCertPath());
            req.setRootCertPath(app.getAlipayRootCertPath());
            return new DefaultAlipayClient(req);
        }
        // 公钥模式
        return new DefaultAlipayClient(
                properties.getGateway(),
                app.getAppId(),
                app.getPrivateKey(),
                "json",
                properties.getCharset(),
                app.getAlipayPublicKey(),
                properties.getSignType()
        );
    }
}
