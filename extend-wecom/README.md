# extend-wecom

企业微信 / 公众号 / 小程序 渠道集成模块。基于 [WxJava 4.8.6.B](https://github.com/binarywang/WxJava) 核心包，不使用 spring-boot-starter（starter 只支持 Spring Boot 2.x）。

## 状态

**POC 阶段**。已实现：
- 多自建应用配置加载（`basic.wecom.corp-id` + `basic.wecom.apps[]`）
- access_token Redis 持久化（`RedisWxCpConfigStorage`）
- 连通性验证端点 `GET /wecom/health/{agentId}`

待实现（按业务优先级排）：
- [ ] 通讯录同步（成员/部门/标签 → sys_user / sys_dept）
- [ ] 消息推送（应用消息 / 群机器人 / 模板卡片）
- [ ] 回调入口（数据回调 / 指令回调，含验签 + 5 秒响应 + 异步入库）
- [ ] OAuth2 扫码登录
- [ ] 审批数据拉取与展示（oa/getapprovalinfo 等）
- [ ] 客户联系 SCRM（外部联系人 / 客户群 / 欢迎语）

## 与 Keycloak 的关系

不直接复用 Keycloak 联邦。本模块只负责把企微数据同步到 `sys_user` / `sys_dept`，
身份认证仍走 Keycloak。企微 OAuth2 登录产出临时凭证换取 Keycloak token 的逻辑放在后续单独设计。

## 配置示例

```yaml
basic:
  wecom:
    corp-id: ww1234567890abcdef
    contacts-secret: ${WECOM_CONTACTS_SECRET:}    # 通讯录同步专用 secret
    apps:
      - agent-id: 1000001
        name: 公告推送
        secret: ${WECOM_APP1_SECRET}
      - agent-id: 1000002
        name: 审批回调
        secret: ${WECOM_APP2_SECRET}
```

## Redis Key 规划

- `wecom:token:cp:{corpId}:{agentId}` — access_token，按 corp+agent 隔离
- 后续会增加：`wecom:msg:{msgId}` 消息回查、`wecom:callback:idem:{msgId}` 回调幂等

## 不依赖 starter 的原因

WxJava 的 `wx-java-cp-spring-boot-starter` 编译时锁 SB 2.5.15。SB 4.1.1 下：
- 自动配置类不会被 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 识别
- `spring-data-redis 2.3.3` 会与项目自带的 4.x 冲突

所以走"手写 Configuration + 自实现 ConfigStorage"路线，~50 行代码完成同样的事情。
