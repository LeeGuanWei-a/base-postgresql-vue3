package com.basic.framework.config;

import cloud.tianai.captcha.common.AnyMap;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator;

/**
 * 按验证码类型区分容差的校验器
 *
 * tianai 默认容差是 0.02(百分比):
 *   - SLIDER  : 0.02 * 600px = 12px, 用户拖到缺口附近即可, 合理
 *   - ROTATE  : 0.02 * 360°  = 7.2°, 要求用户用滑块精确到 7 度以内, 极其困难
 *
 * 所以这里只把 ROTATE 放宽到 0.10(±36°), 其它类型保持默认值.
 */
public class TypeAwareImageCaptchaValidator extends SimpleImageCaptchaValidator
{
    /** 旋转验证码的容差(百分比). 0.10 => 约 ±36 度 */
    private static final float ROTATE_TOLERANT = 0.10f;

    public TypeAwareImageCaptchaValidator()
    {
        super();
    }

    public TypeAwareImageCaptchaValidator(float defaultTolerant)
    {
        super(defaultTolerant);
    }

    @Override
    public boolean doValidSliderCaptcha(ImageCaptchaTrack imageCaptchaTrack, AnyMap imageCaptchaValidData,
                                        Float tolerant, String type)
    {
        float effective = tolerant == null ? defaultTolerant : tolerant;
        if (CaptchaTypeConstant.ROTATE.equals(type) && effective < ROTATE_TOLERANT)
        {
            effective = ROTATE_TOLERANT;
        }
        return super.doValidSliderCaptcha(imageCaptchaTrack, imageCaptchaValidData, effective, type);
    }
}
