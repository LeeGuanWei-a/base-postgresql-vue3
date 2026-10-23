# extend-alipay

支付宝渠道集成模块。基于 [alipay-sdk-java 4.40.1044.ALL](https://github.com/alipay/alipay-sdk-java-all) 官方全量 OpenAPI SDK。

## 状态

**POC 阶段**。已实现：
- 多应用配置（公钥模式 + 证书模式）
- 连通性验证端点 `GET /alipay/health/{appId}?outTradeNo=xxx`

待实现：
- [ ] 支付下单（网页 / APP / 小程序 / 当面付 / 周期扣款）
- [ ] 退款 + 部分退款 + 退款查询
- [ ] 异步通知（验签 + 幂等 + 入账）
- [ ] 对账文件下载与解析
- [ ] 商家券 / 商家会员
- [ ] 支付宝小程序登录与 unionid 体系

## 配置示例

```yaml
basic:
  alipay:
    gateway: https://openapi.alipay.com/gateway.do
    sign-type: RSA2
    apps:
      - app-id: 2021000000000000
        name: 主站
        private-key: ${ALIPAY_MAIN_PRIVATE_KEY}
        alipay-public-key: ${ALIPAY_MAIN_PUBLIC_KEY}
        notify-url: https://your.domain/alipay/notify/main
        return-url: https://your.domain/alipay/return/main
      # 证书模式：填齐三个 cert-path 即自动切换
      - app-id: 2021000000000001
        name: 主站(证书)
        private-key: ${ALIPAY_MAIN_PRIVATE_KEY}
        app-cert-path: /opt/certs/appCertPublicKey.crt
        alipay-cert-path: /opt/certs/alipayCertPublicKey_RSA2.crt
        alipay-root-cert-path: /opt/certs/alipayRootCert.crt
        notify-url: https://your.domain/alipay/notify/main-cert
```

## 与 WxJava 关键差异

| 维度 | alipay-sdk-java | WxJava |
|---|---|---|
| 序列化 | fastjson 1.2.83 | Gson |
| 数字签名 | RSA2 私钥签名 + 支付宝公钥验签 | SHA1/RSA 自有签名 |
| 异步通知 | POST 表单，关键字段验签后入库 | POST XML，先解密再验签 |
| 客户端复用 | `AlipayClient` 线程安全，单例即可 | `WxCpService` 同样线程安全 |

## 依赖冲突处理

`alipay-sdk-java` 默认带：
- `fastjson 1.2.83_noneautotype`：保留，**别盲目 exclude**，SDK 内部在用
- `bcprov-jdk15on 1.62`（2019）：已 exclude，统一用 `bcprov-jdk18on`
- `commons-logging` / `dom4j`：已 exclude
