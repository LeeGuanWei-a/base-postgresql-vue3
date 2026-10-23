package com.basic.wecom.controller;

import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 企微连通性 POC 端点。
 * <p>
 * 验证目标：WxJava 4.8.6.B 在 Spring Boot 4.1.1 + JDK 21 + 项目现有 Redis 下，
 * 能正常获取 access_token 并调通一个最简接口（获取部门列表）。
 * <p>
 * 调用方式：GET /wecom/health/{agentId}
 * 返回：{"code":200, "msg":"ok", "data":{...}}
 * - 200 但 data.deptList=[]：token 取到了，但企业里没建部门
 * - 200 且 data.deptList 非空：完整链路通了
 * - 非 200：失败，data 里有具体错误
 */
@RestController
@RequestMapping("/wecom")
public class WeComHealthController extends BaseController {

    private final Map<Long, WxCpService> wxCpServiceMap;

    /**
     * 构造器注入：装配好的 WxCpService 池。
     *
     * @param wxCpServiceMap agentId → WxCpService 的 map（由 WeComConfiguration 提供）
     */
    public WeComHealthController(@Qualifier("wxCpServiceMap") Map<Long, WxCpService> wxCpServiceMap) {
        this.wxCpServiceMap = wxCpServiceMap;
    }

    /**
     * 企微连通性 POC 端点。
     * <p>
     * 流程：
     * <ol>
     *   <li>从 map 中取出对应 agentId 的 WxCpService</li>
     *   <li>调 {@code getAccessToken()}——会触发 token 获取/刷新（走 Redis）</li>
     *   <li>调 {@code simpleList(1L)} 拉根部门下的子部门列表（最轻只读接口）</li>
     *   <li>返回 token 摘要 + 部门数量</li>
     * </ol>
     * <p>
     * 状态码语义：
     * <ul>
     *   <li>200 + deptCount=0：链路通，但企业里没建部门（很常见）</li>
     *   <li>200 + deptCount>0：完整链路通了</li>
     *   <li>非 200：失败，msg 里有具体错误（凭证错 / 权限错 / 网络错）</li>
     * </ul>
     *
     * @param agentId 自建应用 agentId
     * @return AjaxResult 包装的诊断信息
     */
    @GetMapping("/health/{agentId}")
    public AjaxResult health(@PathVariable Long agentId) {
        WxCpService service = wxCpServiceMap.get(agentId);
        if (service == null) {
            return error("agentId=" + agentId + " 未配置，请检查 basic.wecom.apps");
        }
        try {
            // getAccessToken() 会自动判断过期并刷新（走我们 RedisWxCpConfigStorage）
            String token = service.getAccessToken();
            // listByDepartment(1, true) 是最轻的只读接口：拉根部门下的子部门
            // 不传 fetchChild=true 时只拉一层，足够验证链路
            var deptList = service.getDepartmentService().simpleList(1L);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("corpId", service.getWxCpConfigStorage().getCorpId());
            data.put("agentId", agentId);
            data.put("token", token == null ? "(null)" : token.substring(0, Math.min(8, token.length())) + "...");
            data.put("deptCount", deptList == null ? 0 : deptList.size());
            return AjaxResult.success(data);
        } catch (WxErrorException e) {
            return error("企微调用失败: " + e.getError().getErrorCode() + " " + e.getError().getErrorMsg());
        } catch (Exception e) {
            return error("企微调用异常: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
