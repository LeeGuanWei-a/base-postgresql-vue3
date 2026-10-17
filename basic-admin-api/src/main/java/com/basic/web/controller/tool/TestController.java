package com.basic.web.controller.tool;

import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.R;
import com.basic.common.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * springdoc-openapi 用户测试方法（替代原 springfox）
 *
 * @author basic
 */
@Tag(name = "用户信息管理")
@PreAuthorize("@ss.hasPermi('system:user:list')")
@RestController
@RequestMapping("/test/user")
public class TestController extends BaseController
{
    private final static Map<Integer, UserEntity> users = new LinkedHashMap<Integer, UserEntity>();
    {
        users.put(1, new UserEntity(1, "admin", "admin123", "15888888888"));
        users.put(2, new UserEntity(2, "ry", "admin123", "15666666666"));
    }

    /**
     * @return R<List<UserEntity>> 全部用户列表
     * @description 返回内存 Map 中预置的用户列表，仅用于接口联调演示
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    public R<List<UserEntity>> userList()
    {
        List<UserEntity> userList = new ArrayList<UserEntity>(users.values());
        return R.ok(userList);
    }

    /**
     * @return R<UserEntity> 匹配到的用户，用户不存在时返回失败提示
     * @description 按用户 id 从内存 Map 中取用户，不存在时返回「用户不存在」
     * @params [userId] 用户 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary = "获取用户详细")
    @Parameter(name = "userId", description = "用户ID", required = true, in = ParameterIn.PATH)
    @GetMapping("/{userId}")
    public R<UserEntity> getUser(@PathVariable Integer userId)
    {
        if (!users.isEmpty() && users.containsKey(userId))
        {
            return R.ok(users.get(userId));
        }
        else
        {
            return R.fail("用户不存在");
        }
    }

    /**
     * @return R<String> 保存成功返回 ok，用户 id 为空时返回失败提示
     * @description 把传入的用户放进内存 Map，同 id 会覆盖；用户 id 为空时拒绝保存
     * @params [user] 待新增的用户，userId 必填
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary = "新增用户")
    @Parameters({
        @Parameter(name = "userId", description = "用户id", in = ParameterIn.QUERY),
        @Parameter(name = "username", description = "用户名称", in = ParameterIn.QUERY),
        @Parameter(name = "password", description = "用户密码", in = ParameterIn.QUERY),
        @Parameter(name = "mobile", description = "用户手机", in = ParameterIn.QUERY)
    })
    @PostMapping("/save")
    public R<String> save(UserEntity user)
    {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId()))
        {
            return R.fail("用户ID不能为空");
        }
        users.put(user.getUserId(), user);
        return R.ok();
    }

    /**
     * @return R<String> 更新成功返回 ok，用户不存在或 id 为空时返回失败提示
     * @description 用户必须已存在才允许更新：先校验 id 非空与存在性，再覆盖内存 Map 中的记录
     * @params [user] 待更新的用户，userId 必填且须已存在
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary = "更新用户")
    @PutMapping("/update")
    public R<String> update(@RequestBody UserEntity user)
    {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId()))
        {
            return R.fail("用户ID不能为空");
        }
        if (users.isEmpty() || !users.containsKey(user.getUserId()))
        {
            return R.fail("用户不存在");
        }
        users.remove(user.getUserId());
        users.put(user.getUserId(), user);
        return R.ok();
    }

    /**
     * @return R<String> 删除成功返回 ok，用户不存在时返回失败提示
     * @description 按用户 id 从内存 Map 中移除用户，不存在时返回「用户不存在」
     * @params [userId] 用户 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary = "删除用户信息")
    @Parameter(name = "userId", description = "用户ID", required = true, in = ParameterIn.PATH)
    @DeleteMapping("/{userId}")
    public R<String> delete(@PathVariable Integer userId)
    {
        if (!users.isEmpty() && users.containsKey(userId))
        {
            users.remove(userId);
            return R.ok();
        }
        else
        {
            return R.fail("用户不存在");
        }
    }
}

@Schema(description = "用户实体")
class UserEntity
{
    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "用户名称")
    private String username;

    @Schema(description = "用户密码")
    private String password;

    @Schema(description = "用户手机")
    private String mobile;

    /**
     * @description 无参构造，供 JSON 反序列化与框架实例化使用
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public UserEntity()
    {

    }

    /**
     * @description 全参构造，一次性初始化用户的四个字段
     * @params [userId, username, password, mobile] userId 用户 id；username 用户名称；password 密码；mobile 手机号
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public UserEntity(Integer userId, String username, String password, String mobile)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.mobile = mobile;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

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

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }
}
