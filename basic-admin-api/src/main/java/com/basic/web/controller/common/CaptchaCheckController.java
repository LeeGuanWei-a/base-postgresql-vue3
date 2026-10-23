package com.basic.web.controller.common;

import com.basic.common.core.domain.AjaxResult;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 行为验证码(tianai-captcha)二次校验接口
 *
 * <p>POST /captcha/check</p>
 * <p>请求体:</p>
 * <pre>
 * {
 *   "id":   "验证码 id(由 /captchaImage 返回)",
 *   "data": { ...ImageCaptchaTrack 字段... }  // 滑动轨迹, 必须包含 trackList
 * }
 * </pre>
 *
 * <p>说明: 本接口仅供调试/独立校验场景使用, 登录主流程已经把校验合并到 /login, 见
 * {@code com.basic.framework.web.service.SysLoginService#login}</p>
 *
 * @author basic
 */
@RestController
public class CaptchaCheckController
{
    @Autowired
    private ImageCaptchaApplication imageCaptchaApplication;

    @Autowired
    private com.basic.common.core.redis.RedisCache redisCache;

    /** Redis key prefix for "this captcha id has already been verified" */
    public static final String VERIFIED_KEY_PREFIX = "captcha:verified:";

    /** How long the verified flag stays valid (seconds) */
    public static final int VERIFIED_EXPIRE_SECONDS = 120;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/captcha/check")
    public AjaxResult check(@RequestBody Map<String, Object> body)
    {
        if (body == null)
        {
            return AjaxResult.error("请求体为空");
        }
        Object idObj = body.get("id");
        Object dataObj = body.get("data");
        if (idObj == null || dataObj == null)
        {
            return AjaxResult.error("id 和 data 不能为空");
        }

        // 用 Jackson 把 Map 强转成 ImageCaptchaTrack (会处理 trackList 嵌套)
        // 注意: dataObj 本身可能是 Map (Spring 反序列化默认) 或 String (前端 JSON.stringify)
        ImageCaptchaTrack track;
        try
        {
            if (dataObj instanceof Map)
            {
                track = objectMapper.convertValue(dataObj, ImageCaptchaTrack.class);
            }
            else if (dataObj instanceof String)
            {
                track = objectMapper.readValue((String) dataObj, ImageCaptchaTrack.class);
            }
            else
            {
                return AjaxResult.error("data 格式错误, 需要 Map 或 JSON 字符串");
            }
        }
        catch (Exception e)
        {
            return AjaxResult.error("轨迹数据格式错误: " + e.getMessage());
        }

        if (track.getTrackList() == null || track.getTrackList().isEmpty())
        {
            return AjaxResult.error("trackList 不能为空");
        }
        if (track.getBgImageWidth() == null || track.getBgImageHeight() == null)
        {
            return AjaxResult.error("bgImageWidth / bgImageHeight 不能为空");
        }
        if (track.getStartTime() == null || track.getStopTime() == null)
        {
            return AjaxResult.error("startTime / stopTime 不能为空");
        }

        ApiResponse<?> result;
        try
        {
            result = imageCaptchaApplication.matching(String.valueOf(idObj), track);
        }
        catch (IllegalArgumentException iae)
        {
            return AjaxResult.error("校验参数错误: " + iae.getMessage());
        }
        if (result == null)
        {
            return AjaxResult.error("校验失败");
        }
        if (result.isSuccess())
        {
            // matching() consumes the captcha cache, so /login would fail if it tried matching again.
            // Write a short-lived "verified" flag so /login can accept this captcha id once.
            try
            {
                redisCache.setCacheObject(VERIFIED_KEY_PREFIX + String.valueOf(idObj), "1",
                        VERIFIED_EXPIRE_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
            }
            catch (Exception ignore)
            {
                // redis unavailable: just return success, /login will fall back to matching()
            }
            return AjaxResult.success("校验通过");
        }
        return AjaxResult.error(result.getMsg() == null ? "校验失败" : result.getMsg());
    }
}
