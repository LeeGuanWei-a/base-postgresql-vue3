package com.basic.framework.web.service;

import jakarta.annotation.Resource;

import com.basic.common.utils.sign.RsaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.basic.common.constant.Constants;
import com.basic.common.core.domain.entity.SysUser;
import com.basic.common.core.domain.model.LoginUser;
import com.basic.common.core.redis.RedisCache;
import com.basic.common.exception.ServiceException;
import com.basic.common.exception.user.CaptchaException;
import com.basic.common.exception.user.CaptchaExpireException;
import com.basic.common.exception.user.UserPasswordNotMatchException;
import com.basic.common.utils.DateUtils;
import com.basic.common.utils.MessageUtils;
import com.basic.common.utils.SecurityUtils;
import com.basic.common.utils.StringUtils;
import com.basic.common.utils.ServletUtils;
import com.basic.common.utils.ip.IpUtils;
import com.basic.framework.manager.AsyncManager;
import com.basic.framework.manager.factory.AsyncFactory;
import com.basic.system.service.ISysConfigService;
import com.basic.system.service.ISysUserService;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;

/**
 * 登录校验方法
 * 
 * @author basic
 */
@Component
public class SysLoginService
{
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;
    
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private cloud.tianai.captcha.application.ImageCaptchaApplication imageCaptchaApplication;

    /**
     * 登录验证(行为验证码 tianai-captcha 版本)
     *
     * @param username    用户名
     * @param password    密码
     * @param code        行为验证码 id(由 /captchaImage 返回的 uuid;兼容老 kaptcha 接口,作为占位符)
     * @param uuid        行为验证码 id(同 code;保留旧字段兼容)
     * @param captchaData 行为验证码轨迹 JSON 字符串(ImageCaptchaTrack);4 种类型都用该字段
     * @return 登录成功后的 token
     */
    public String login(String username, String password, String code, String uuid, String captchaData)
    {
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        // 验证码开关
        if (captchaOnOff)
        {
            validateTianaiCaptcha(username, uuid, captchaData);
        }
        // 用户验证
        Authentication authentication = null;
        try
        {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            // 对密码进行rsa解密
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, RsaUtils.decryptByPrivateKey(password)));
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 锁屏解锁：校验当前用户的密码
     * 与登录流程共用 RSA 私钥解密 + AuthenticationManager 校验
     *
     * @param password RSA 加密后的密码
     */
    public void unlockScreen(String password)
    {
        // 当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null)
        {
            throw new ServiceException("登录用户为空，请重新登录");
        }
        String username = loginUser.getUsername();
        try
        {
            // 该方法会去调用 UserDetailsServiceImpl.loadUserByUsername
            // 对密码进行 rsa 解密
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, RsaUtils.decryptByPrivateKey(password)));
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
    }

    /**
     * 校验行为验证码(tianai-captcha)
     *
     * @param username    用户名(用于登录失败日志)
     * @param captchaId   验证码 id(由 /captchaImage 返回的 uuid)
     * @param captchaData 行为验证码轨迹数据 JSON 字符串(ImageCaptchaTrack 序列化结果)
     */
    public void validateTianaiCaptcha(String username, String captchaId, String captchaData)
    {
        if (StringUtils.isEmpty(captchaId))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, "行为验证码 id 缺失"));
            throw new CaptchaExpireException();
        }
        if (StringUtils.isEmpty(captchaData))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, "行为验证码轨迹缺失"));
            throw new CaptchaExpireException();
        }

        // /captcha/check 已经用 matching() 校验过一次(会消费验证码缓存), 并写下 verified 标记.
        // 这里若命中标记则直接放行并删除, 保证同一个验证码只能用一次.
        String verifiedKey = "captcha:verified:" + captchaId;
        try
        {
            Object verifiedFlag = redisCache.getCacheObject(verifiedKey);
            if (verifiedFlag != null)
            {
                redisCache.deleteObject(verifiedKey);
                return;
            }
        }
        catch (Exception ignore)
        {
            // redis 不可用时走下面的 matching 兜底
        }

        ImageCaptchaTrack track;
        try
        {
            track = jsonToTrack(captchaData);
        }
        catch (Exception e)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, "行为验证码轨迹解析失败"));
            throw new CaptchaException();
        }

        ApiResponse<?> result = imageCaptchaApplication.matching(captchaId, track);
        if (result == null || !result.isSuccess())
        {
            String msg = (result != null && result.getMsg() != null) ? result.getMsg() : MessageUtils.message("user.jcaptcha.error");
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, msg));
            throw new CaptchaException();
        }
    }

    /**
     * 把前端提交的轨迹 JSON 字符串反序列化为 ImageCaptchaTrack
     * 使用 Jackson(项目默认在 spring-web 里)以兼容 application/json 格式
     */
    private ImageCaptchaTrack jsonToTrack(String json) throws java.io.IOException
    {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        // startTime/stopTime 可能是 long 毫秒,也可能是 ISO 字符串
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(json, ImageCaptchaTrack.class);
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId)
    {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }
}
