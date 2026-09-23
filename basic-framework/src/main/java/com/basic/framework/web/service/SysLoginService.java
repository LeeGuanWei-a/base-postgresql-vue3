package com.basic.framework.web.service;

import javax.annotation.Resource;

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

    /**
     * 登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid)
    {
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        // 验证码开关
        if (captchaOnOff)
        {
            validateCaptcha(username, code, uuid);
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
     * 校验验证码
     *
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
            throw new CaptchaException();
        }
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
