package com.basic.alipay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 支付宝集成配置（支持多应用：主站 + 小程序 + 第三方代理等）。
 * <p>
 * 区分两种证书模式：
 * <ul>
 *   <li>公钥模式（appCertPath = null）：用普通 RSA2 公私钥，适合自用</li>
 *   <li>证书模式：需要上传 app_cert.pem / alipay_cert.pem / alipay_root_cert.pem 到服务器</li>
 * </ul>
 *
 * <pre>
 * basic:
 *   alipay:
 *     gateway: https://openapi.alipay.com/gateway.do
 *     sign-type: RSA2
 *     apps:
 *       - app-id: 2021000000000000
 *         name: 主站
 *         private-key: ${ALIPAY_MAIN_PRIVATE_KEY}
 *         alipay-public-key: ${ALIPAY_MAIN_PUBLIC_KEY}
 *         app-cert-path: /opt/certs/main/appCertPublicKey.crt
 *         alipay-cert-path: /opt/certs/main/alipayCertPublicKey_RSA2.crt
 *         alipay-root-cert-path: /opt/certs/main/alipayRootCert.crt
 *         notify-url: https://your.domain/alipay/notify/main
 * </pre>
 */
@ConfigurationProperties(prefix = "basic.alipay")
public class AlipayProperties {

    /** 支付宝网关（沙箱用 https://openapi.alipaydev.com/gateway.do） */
    private String gateway = "https://openapi.alipay.com/gateway.do";

    /** 签名类型：RSA2 / RSA */
    private String signType = "RSA2";

    /** 字符集 */
    private String charset = "UTF-8";

    /** 应用列表 */
    private List<App> apps;

    /**
     * 获取支付宝网关地址。
     *
     * @return 网关 URL（生产/沙箱不同）
     */
    public String getGateway() {
        return gateway;
    }

    /**
     * 设置支付宝网关地址。
     *
     * @param gateway 网关 URL
     */
    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    /**
     * 获取签名类型。
     *
     * @return RSA2 或 RSA（推荐 RSA2）
     */
    public String getSignType() {
        return signType;
    }

    /**
     * 设置签名类型。
     *
     * @param signType RSA2 或 RSA
     */
    public void setSignType(String signType) {
        this.signType = signType;
    }

    /**
     * 获取字符集。
     *
     * @return 字符集（默认 UTF-8）
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 设置字符集。
     *
     * @param charset 字符集
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * 获取应用列表。
     *
     * @return 支付宝应用配置列表
     */
    public List<App> getApps() {
        return apps;
    }

    /**
     * 设置应用列表。
     *
     * @param apps 支付宝应用配置列表
     */
    public void setApps(List<App> apps) {
        this.apps = apps;
    }

    /**
     * 单个支付宝应用配置。
     * <p>
     * 支持两种模式：
     * <ul>
     *   <li>公钥模式：设置 {@link #alipayPublicKey}，其他 cert 路径留空</li>
     *   <li>证书模式：设置三个 cert 路径 + privateKey，alipayPublicKey 留空</li>
     * </ul>
     */
    public static class App {
        /** 应用 AppID（开放平台"应用信息"页可查） */
        private String appId;
        /** 应用名称（仅展示用） */
        private String name;
        /** 应用私钥（PKCS#8 或 PKCS#1，SDK 内部会处理） */
        private String privateKey;
        /** 支付宝公钥（公钥模式下必填） */
        private String alipayPublicKey;
        /** 应用公钥证书路径（证书模式必填） */
        private String appCertPath;
        /** 支付宝公钥证书路径（证书模式必填） */
        private String alipayCertPath;
        /** 支付宝根证书路径（证书模式必填） */
        private String alipayRootCertPath;
        /** 异步通知地址（支付完成后支付宝 POST 回调） */
        private String notifyUrl;
        /** 同步返回地址（前端支付完成后回跳） */
        private String returnUrl;

        /**
         * 获取应用 AppID。
         *
         * @return 16 位数字字符串
         */
        public String getAppId() { return appId; }
        /**
         * 设置应用 AppID。
         *
         * @param appId 16 位数字字符串
         */
        public void setAppId(String appId) { this.appId = appId; }
        /**
         * 获取应用名称。
         *
         * @return 展示用名称
         */
        public String getName() { return name; }
        /**
         * 设置应用名称。
         *
         * @param name 展示用名称
         */
        public void setName(String name) { this.name = name; }
        /**
         * 获取应用私钥。
         *
         * @return 私钥字符串（PKCS#8 格式）
         */
        public String getPrivateKey() { return privateKey; }
        /**
         * 设置应用私钥。
         *
         * @param privateKey 私钥字符串
         */
        public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }
        /**
         * 获取支付宝公钥。
         *
         * @return 公钥字符串（公钥模式必填）
         */
        public String getAlipayPublicKey() { return alipayPublicKey; }
        /**
         * 设置支付宝公钥。
         *
         * @param alipayPublicKey 公钥字符串
         */
        public void setAlipayPublicKey(String alipayPublicKey) { this.alipayPublicKey = alipayPublicKey; }
        /**
         * 获取应用公钥证书路径。
         *
         * @return 证书文件绝对路径（证书模式必填）
         */
        public String getAppCertPath() { return appCertPath; }
        /**
         * 设置应用公钥证书路径。
         *
         * @param appCertPath 证书文件绝对路径
         */
        public void setAppCertPath(String appCertPath) { this.appCertPath = appCertPath; }
        /**
         * 获取支付宝公钥证书路径。
         *
         * @return 证书文件绝对路径（证书模式必填）
         */
        public String getAlipayCertPath() { return alipayCertPath; }
        /**
         * 设置支付宝公钥证书路径。
         *
         * @param alipayCertPath 证书文件绝对路径
         */
        public void setAlipayCertPath(String alipayCertPath) { this.alipayCertPath = alipayCertPath; }
        /**
         * 获取支付宝根证书路径。
         *
         * @return 证书文件绝对路径（证书模式必填）
         */
        public String getAlipayRootCertPath() { return alipayRootCertPath; }
        /**
         * 设置支付宝根证书路径。
         *
         * @param alipayRootCertPath 证书文件绝对路径
         */
        public void setAlipayRootCertPath(String alipayRootCertPath) { this.alipayRootCertPath = alipayRootCertPath; }
        /**
         * 获取异步通知地址。
         *
         * @return 支付完成回调 URL
         */
        public String getNotifyUrl() { return notifyUrl; }
        /**
         * 设置异步通知地址。
         *
         * @param notifyUrl 支付完成回调 URL，必须公网可访问
         */
        public void setNotifyUrl(String notifyUrl) { this.notifyUrl = notifyUrl; }
        /**
         * 获取同步返回地址。
         *
         * @return 前端支付完成回跳 URL
         */
        public String getReturnUrl() { return returnUrl; }
        /**
         * 设置同步返回地址。
         *
         * @param returnUrl 前端支付完成回跳 URL
         */
        public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
    }
}
