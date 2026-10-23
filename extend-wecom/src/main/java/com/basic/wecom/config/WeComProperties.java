package com.basic.wecom.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 企业微信集成配置（多应用支持）。
 * <p>
 * 与 WxJava 保持最小耦合：只把 corpid / 每个应用的 agentId+secret 读进来，
 * 实际 WxCpService 的装配放在 WeComServiceFactory。
 *
 * <pre>
 * basic:
 *   wecom:
 *     corp-id: ww1234567890abcdef
 *     contacts-secret: xxx   # 通讯录同步专用 secret（可选）
 *     apps:
 *       - agent-id: 1000001
 *         name: 公告推送
 *         secret: xxx
 *       - agent-id: 1000002
 *         name: 审批回调
 *         secret: xxx
 * </pre>
 */
@ConfigurationProperties(prefix = "basic.wecom")
public class WeComProperties {

    /** 企业 corpid */
    private String corpId;

    /**
     * 通讯录管理 secret（可选）。
     * 单独配置因为它的权限范围与"自建应用"不同，
     * 通常放在"管理组"或独立 secret。
     */
    private String contactsSecret;

    /** 自建应用列表（一个 corp 可建多个 agent） */
    private List<App> apps;

    /**
     * 获取企业 corpId。
     *
     * @return 企业 ID（"我的企业 > 企业信息"页可查）
     */
    public String getCorpId() {
        return corpId;
    }

    /**
     * 设置企业 corpId。
     *
     * @param corpId 企业 ID
     */
    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    /**
     * 获取通讯录同步专用 secret。
     *
     * @return 通讯录 API 用的密钥（不同于自建应用 secret）
     */
    public String getContactsSecret() {
        return contactsSecret;
    }

    /**
     * 设置通讯录同步专用 secret。
     *
     * @param contactsSecret 通讯录 API 用的密钥
     */
    public void setContactsSecret(String contactsSecret) {
        this.contactsSecret = contactsSecret;
    }

    /**
     * 获取自建应用列表。
     *
     * @return 自建应用配置列表，可为空（空表示企微相关功能不可用）
     */
    public List<App> getApps() {
        return apps;
    }

    /**
     * 设置自建应用列表。
     *
     * @param apps 自建应用配置列表
     */
    public void setApps(List<App> apps) {
        this.apps = apps;
    }

    /**
     * 自建应用配置：每个自建应用对应一对 (agentId, secret)。
     * <p>
     * 一个企业可以建多个自建应用，每个应用的 secret 独立，
     * 权限范围也由后台"可见范围"决定。
     */
    public static class App {
        /** 自建应用 agentid */
        private Long agentId;

        /** 应用名称（仅作展示用） */
        private String name;

        /** 应用 secret */
        private String secret;

        /**
         * 获取应用 agentId。
         *
         * @return 自建应用 ID（在企业微信管理后台"应用详情"页可查）
         */
        public Long getAgentId() {
            return agentId;
        }

        /**
         * 设置应用 agentId。
         *
         * @param agentId 自建应用 ID
         */
        public void setAgentId(Long agentId) {
            this.agentId = agentId;
        }

        /**
         * 获取应用名称。
         *
         * @return 展示用名称（不影响调用）
         */
        public String getName() {
            return name;
        }

        /**
         * 设置应用名称。
         *
         * @param name 展示用名称
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * 获取应用 secret。
         *
         * @return 调用 API 用的密钥（敏感信息）
         */
        public String getSecret() {
            return secret;
        }

        /**
         * 设置应用 secret。
         *
         * @param secret 调用 API 用的密钥，建议通过环境变量注入
         */
        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}
