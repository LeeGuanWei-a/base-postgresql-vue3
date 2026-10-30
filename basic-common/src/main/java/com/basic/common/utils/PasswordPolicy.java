package com.basic.common.utils;

import java.util.regex.Pattern;

import com.basic.common.exception.ServiceException;

/**
 * 密码复杂度策略（硬编码，不依赖 sys_config 配置）
 *
 * <p>规则：</p>
 * <ul>
 *     <li>长度 8 ~ 15 位</li>
 *     <li>必须同时包含大写字母、小写字母、数字、特殊字符</li>
 *     <li>仅允许可打印 ASCII 字符（! ~ ~），即不允许空格、中文等非 ASCII 字符</li>
 * </ul>
 *
 * <p>前端 web-ui/src/utils/passwordRule.js 中的规则需与本类保持一致。</p>
 *
 * @author basic
 */
public final class PasswordPolicy
{
    /** 密码最小长度 */
    public static final int MIN_LENGTH = 8;

    /** 密码最大长度 */
    public static final int MAX_LENGTH = 15;

    /** 密码复杂度提示信息 */
    public static final String MESSAGE = "密码长度必须介于 8 和 15 之间，且必须同时包含大写字母、小写字母、数字和特殊字符";

    /** 密码为空时的提示信息 */
    public static final String EMPTY_MESSAGE = "密码不能为空";

    /**
     * 复杂度正则：
     * 四个先行断言分别要求至少含一个小写字母、一个大写字母、一个数字、一个非字母数字字符（即特殊字符），
     * [!-~] 限定只能使用可打印 ASCII 字符（不含空格）。
     */
    private static final Pattern PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9])[!-~]{8,15}$");

    private PasswordPolicy()
    {
    }

    /**
     * 密码是否满足复杂度要求
     *
     * @param password 明文密码
     * @return 满足返回 true
     */
    public static boolean isValid(String password)
    {
        return StringUtils.isNotEmpty(password) && PATTERN.matcher(password).matches();
    }

    /**
     * 校验密码复杂度，不通过时抛出业务异常
     *
     * @param password 明文密码
     */
    public static void check(String password)
    {
        if (StringUtils.isEmpty(password))
        {
            throw new ServiceException(EMPTY_MESSAGE);
        }
        if (!isValid(password))
        {
            throw new ServiceException(MESSAGE);
        }
    }
}
