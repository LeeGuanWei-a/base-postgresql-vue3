package com.basic.alipay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 支付宝连通性 POC 端点。
 * <p>
 * 验证目标：alipay-sdk-java 4.40.1044.ALL 在 Spring Boot 4.1.1 + JDK 21 下，
 * 能正常发请求 + 解析响应（哪怕返回"交易不存在"也说明链路通了）。
 * <p>
 * 调用方式：GET /alipay/health/{appId}?outTradeNo=任意字符串
 * - 200 但 tradeStatus 未填：链路通，但 outTradeNo 查不到
 * - 非 200：网络或签名问题
 */
@RestController
@RequestMapping("/alipay")
public class AlipayHealthController extends BaseController {

    private final Map<String, AlipayClient> alipayClientMap;

    /**
     * 构造器注入：装配好的 AlipayClient 池。
     *
     * @param alipayClientMap appId → AlipayClient 的 map（由 AlipayConfiguration 提供）
     */
    public AlipayHealthController(@Qualifier("alipayClientMap") Map<String, AlipayClient> alipayClientMap) {
        this.alipayClientMap = alipayClientMap;
    }

    /**
     * 支付宝连通性 POC 端点。
     * <p>
     * 流程：
     * <ol>
     *   <li>从 map 中取出对应 appId 的 AlipayClient</li>
     *   <li>构造 alipay.trade.query 请求（最轻只读接口）</li>
     *   <li>调用 execute，验证签名/网络/SDK 装配</li>
     *   <li>返回 code/msg/subCode/subMsg 给前端排查</li>
     * </ol>
     * <p>
     * 状态码语义：
     * <ul>
     *   <li>200 + code=40004：链路通，但 outTradeNo 不存在（业务正常）</li>
     *   <li>200 + code=10000：完整链路通了</li>
     *   <li>非 200：网络或签名问题</li>
     * </ul>
     *
     * @param appId      支付宝应用 AppID
     * @param outTradeNo 商户订单号（任意字符串即可，验证用）
     * @return AjaxResult 包装的诊断信息
     */
    @GetMapping("/health/{appId}")
    public AjaxResult health(@PathVariable String appId,
                             @RequestParam(defaultValue = "POC-NOT-EXIST") String outTradeNo) {
        AlipayClient client = alipayClientMap.get(appId);
        if (client == null) {
            return error("appId=" + appId + " 未配置，请检查 basic.alipay.apps");
        }
        try {
            AlipayTradeQueryRequest req = new AlipayTradeQueryRequest();
            req.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\"}");

            AlipayTradeQueryResponse resp = client.execute(req);
            // 链路通了即视为成功；业务上"ACQ.TRADE_NOT_EXIST" 也算正常响应
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("appId", appId);
            data.put("code", resp.getCode());
            data.put("msg", resp.getMsg() == null ? "" : resp.getMsg());
            data.put("subCode", resp.getSubCode() == null ? "" : resp.getSubCode());
            data.put("subMsg", resp.getSubMsg() == null ? "" : resp.getSubMsg());
            data.put("tradeStatus", resp.getTradeStatus() == null ? "(null)" : resp.getTradeStatus());
            return AjaxResult.success(data);
        } catch (AlipayApiException e) {
            return error("支付宝调用失败: " + e.getErrCode() + " " + e.getErrMsg());
        } catch (Exception e) {
            return error("支付宝调用异常: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
