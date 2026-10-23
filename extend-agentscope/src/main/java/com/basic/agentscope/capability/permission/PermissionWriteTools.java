package com.basic.agentscope.capability.permission;

import io.agentscope.core.tool.Tool;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 权限能力专用的「写操作」工具集 —— 用来演示<b>拒绝规则</b>。
 *
 * <p>⚠️ 这些工具<b>不会真的修改数据库</b>，只做「演练（dry-run）」：
 * 一旦权限规则放开，它们也只是回显将要执行的写操作，便于安全地演示。</p>
 *
 * <p>它们默认落在 {@code PermissionCapability} 的 deny 规则里，
 * 因此模型一旦尝试调用就会被权限引擎拦下。</p>
 */
public class PermissionWriteTools {

    /**
     * 冻结某个登录账户。
     *
     * @param userName 账户登录名
     * @return 演练结果
     */
    @Tool(name = "freeze_login_account",
            description = "当需要『冻结』『解冻』『锁死』某个后台登录账户时调用本工具，用于临时禁止该账户登录后台。"
                    + "参数 userName 是账户的登录名。")
    public Map<String, Object> freezeLoginAccount(String userName) {
        return dryRun("freeze_login_account", userName, "冻结登录账户");
    }

    /**
     * 重置某个登录账户的密码。
     *
     * @param userName 账户登录名
     * @return 演练结果
     */
    @Tool(name = "reset_login_secret",
            description = "当需要『重置密码』『找回密码』『修改口令』时调用本工具，用于给某个后台登录账户设置新的登录口令。"
                    + "参数 userName 是账户的登录名。")
    public Map<String, Object> resetLoginSecret(String userName) {
        return dryRun("reset_login_secret", userName, "重置登录口令");
    }

    /**
     * 构造演练结果（不落库）。
     */
    private Map<String, Object> dryRun(String action, String userName, String desc) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("action", action);
        result.put("target", userName);
        result.put("description", desc);
        result.put("dryRun", true);
        result.put("message", "【演练】已模拟执行 " + desc + "：" + userName + "（未真正写库）");
        return result;
    }
}
