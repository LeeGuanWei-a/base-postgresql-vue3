package com.basic.common.core.domain.model;

/**
 * 用户登录对象
 * 
 * @author basic
 */
public class LoginBody
{
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 唯一标识(行为验证码模式下,存验证码 id;兼容老 kaptcha 模式)
     */
    private String uuid;

    /**
     * 行为验证码滑动轨迹数据(tianai-captcha 的 ImageCaptchaTrack JSON 字符串,4 种类型都用这个字段)
     */
    private String captchaData;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public String getCaptchaData()
    {
        return captchaData;
    }

    public void setCaptchaData(String captchaData)
    {
        this.captchaData = captchaData;
    }
}
