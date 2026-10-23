package com.basic.web.controller.common;

import com.basic.common.config.BasicConfig;
import com.basic.common.constant.Constants;
import com.basic.common.core.domain.AjaxResult;
import com.basic.system.service.ISysConfigService;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 行为验证码(tianai-captcha)生成接口
 *
 * 支持 3 种行为验证码类型:
 *   - SLIDER           滑动验证码(社区版内置模板)
 *   - ROTATE           旋转验证码(社区版内置模板)
 *   - WORD_IMAGE_CLICK 文字点选验证码(程序化生成)
 *
 * <p>CONCAT(滑动还原)已下线, 传入该类型会自动回退为 SLIDER</p>
 *
 * <p>前端 SDK 1.5.6 与本接口配套使用</p>
 *
 * @author basic
 */
@RestController
public class CaptchaController
{
    private static final Logger log = LoggerFactory.getLogger(CaptchaController.class);

    @Autowired
    private ImageCaptchaApplication imageCaptchaApplication;

    @Autowired
    private ISysConfigService configService;

    /**
     * 生成行为验证码
     *
     * <p>GET /captchaImage?type=SLIDER</p>
     * <p>type 可选值: SLIDER / ROTATE / WORD_IMAGE_CLICK;不传则使用 application.yml 中 captcha.default-type</p>
     *
     * <p>返回结构(兼容旧 captchaImage 接口字段):</p>
     * <pre>
     * {
     *   "captchaOnOff": true,             // 验证码总开关(sys_config.sys.account.captchaEnabled)
     *   "uuid": "&lt;captchaId&gt;",          // 行为验证码 id,前端登录时回传
     *   "img": null,                      // 兼容旧字段,行为验证码无单张图,填空
     *   "type": "SLIDER",                 // 当前类型
     *   "data": {                         // 行为验证码数据
     *     "id": "xxx",                    // 与 uuid 相同
     *     "type": "SLIDER",
     *     "backgroundImage": "data:image/png;base64,...",
     *     "templateImage": "data:image/png;base64,...",
     *     "backgroundImageWidth": 600,
     *     "backgroundImageHeight": 300,
     *     "templateImageWidth": 60,
     *     "templateImageHeight": 60,
     *     "data": null                    // 旋转/点选等类型会有附加数据
     *   }
     * }
     * </pre>
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCode(@RequestParam(value = "type", required = false) String type)
    {
        AjaxResult ajax = AjaxResult.success();
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        ajax.put("captchaOnOff", captchaOnOff);
        if (!captchaOnOff)
        {
            return ajax;
        }

        // 默认类型:application.yml 中 captcha.default-type,缺省 SLIDER
        if (type == null || type.trim().length() == 0)
        {
            type = BasicConfig.getCaptchaType();
        }
        // 只支持 3 种类型: SLIDER / ROTATE / WORD_IMAGE_CLICK
        // CONCAT(滑动还原)已移除: 前端渲染效果不理想, 请求到 CONCAT 时统一回退成 SLIDER
        if (!CaptchaTypeConstant.SLIDER.equals(type)
                && !CaptchaTypeConstant.ROTATE.equals(type)
                && !CaptchaTypeConstant.WORD_IMAGE_CLICK.equals(type))
        {
            type = CaptchaTypeConstant.SLIDER;
        }

        CaptchaResponse<ImageCaptchaVO> response = null;
        String triedType = type;
        while (response == null)
        {
            try
            {
                response = imageCaptchaApplication.generateCaptcha(triedType);
            }
            catch (Exception e)
            {
                // 资源模板缺失(社区版未提供 CONCAT/WORD_IMAGE_CLICK 模板),回退到 SLIDER
                if (!CaptchaTypeConstant.SLIDER.equals(triedType))
                {
                    log.warn("[captcha] 生成 {} 类型失败,回退到 SLIDER: {}", triedType, e.getMessage());
                    triedType = CaptchaTypeConstant.SLIDER;
                    continue;
                }
                return AjaxResult.error("验证码生成失败: " + e.getMessage());
            }
        }
        if (response == null || response.getCaptcha() == null)
        {
            return AjaxResult.error("验证码生成失败");
        }

        ImageCaptchaVO vo = response.getCaptcha();
        Map<String, Object> data = new HashMap<>(16);
        data.put("id", response.getId());
        // 用前端请求的类型回填(即使后端回退到 SLIDER 也告诉前端当前是 SLIDER)
        data.put("type", vo.getType() != null ? vo.getType() : CaptchaTypeConstant.SLIDER);
        data.put("backgroundImage", vo.getBackgroundImage());
        data.put("templateImage", vo.getTemplateImage());
        data.put("backgroundImageWidth", vo.getBackgroundImageWidth());
        data.put("backgroundImageHeight", vo.getBackgroundImageHeight());
        data.put("templateImageWidth", vo.getTemplateImageWidth());
        data.put("templateImageHeight", vo.getTemplateImageHeight());
        // 把 CustomData.viewData 中的字段(如 CONCAT 的 randomY)扁平化到 data 顶层, 方便前端渲染.
        // 注意: 绝对不能把 expand(点选正确答案坐标) 透给前端.
        Object genericData = vo.getData();
        if (genericData instanceof cloud.tianai.captcha.generator.common.model.dto.CustomData)
        {
            cloud.tianai.captcha.generator.common.model.dto.CustomData cd =
                    (cloud.tianai.captcha.generator.common.model.dto.CustomData) genericData;
            cloud.tianai.captcha.common.AnyMap viewData = cd.getViewData();
            if (viewData != null)
            {
                for (String key : viewData.keySet())
                {
                    data.put(key, viewData.get(key));
                }
            }
        }
        data.put("data", null);
        // 如果回退了,前端降级提示
        if (!triedType.equals(type))
        {
            data.put("fallback", true);
            data.put("requestedType", type);
            data.put("actualType", triedType);
        }

        // 兼容老前端字段
        ajax.put("uuid", response.getId());
        ajax.put("img", vo.getBackgroundImage());
        ajax.put("type", vo.getType() != null ? vo.getType() : CaptchaTypeConstant.SLIDER);
        ajax.put("data", data);
        if (!triedType.equals(type))
        {
            ajax.put("fallback", true);
        }
        return ajax;
    }
}
