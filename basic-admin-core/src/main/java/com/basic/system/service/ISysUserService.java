package com.basic.system.service;

import java.util.List;
import com.basic.common.core.domain.entity.SysUser;

/**
 * 用户 业务层
 * 
 * @author basic
 */
public interface ISysUserService
{
    /**
     * @return List<SysUser> 匹配筛选条件的用户集合
     * @description 按用户对象携带的条件分页查询用户列表
     * @params [user] 用户查询条件，承载用户名、状态、部门等过滤字段
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<SysUser> selectUserList(SysUser user);

    /**
     * @return List<SysUser> 已分配该角色的用户集合
     * @description 按角色维度分页查询已分配到该角色的用户列表
     * @params [user] 用户查询条件，其中角色主键决定筛选范围
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<SysUser> selectAllocatedList(SysUser user);

    /**
     * @return List<SysUser> 未分配该角色的用户集合
     * @description 按角色维度分页查询尚未分配到该角色的用户列表
     * @params [user] 用户查询条件，其中角色主键决定排除范围
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<SysUser> selectUnallocatedList(SysUser user);

    /**
     * @return SysUser 匹配的用户对象，不存在时返回 null
     * @description 按用户名精确查询唯一的用户对象
     * @params [userName] 登录用户名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public SysUser selectUserByUserName(String userName);

    /**
     * @return SysUser 匹配的用户对象，不存在时返回 null
     * @description 按用户主键查询用户对象
     * @params [userId] 用户主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public SysUser selectUserById(Long userId);

    /**
     * @return String 该用户所属角色组名称，多个以逗号拼接
     * @description 查询指定用户名所属的角色组名称集合
     * @params [userName] 登录用户名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String selectUserRoleGroup(String userName);

    /**
     * @return String 该用户所属岗位组名称，多个以逗号拼接
     * @description 查询指定用户名所属的岗位组名称集合
     * @params [userName] 登录用户名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String selectUserPostGroup(String userName);

    /**
     * @return String 唯一性校验结果，框架约定的校验提示对象
     * @description 校验登录用户名在系统内是否唯一
     * @params [userName] 待校验的登录用户名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String checkUserNameUnique(String userName);

    /**
     * @return String 唯一性校验结果，框架约定的校验提示对象
     * @description 校验手机号在系统内是否唯一，只与用户主键不同的记录比较
     * @params [user] 携带手机号与主键的用户信息
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String checkPhoneUnique(SysUser user);

    /**
     * @return String 唯一性校验结果，框架约定的校验提示对象
     * @description 校验邮箱在系统内是否唯一，只与用户主键不同的记录比较
     * @params [user] 携带邮箱与主键的用户信息
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String checkEmailUnique(SysUser user);

    /**
     * @description 校验该用户是否允许被操作，不允许时直接抛出业务异常
     * @params [user] 待校验的用户信息
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void checkUserAllowed(SysUser user);

    /**
     * @description 校验当前操作人是否具备目标用户的数据权限，不通过时抛出业务异常
     * @params [userId] 目标用户主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void checkUserDataScope(Long userId);

    /**
     * @return int 写入成功的记录数
     * @description 新增用户并同步保存其岗位关联
     * @params [user] 待新增的用户信息，包含岗位主键集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int insertUser(SysUser user);

    /**
     * @return boolean 注册成功返回 true
     * @description 注册新用户，直接写入外部传入的账号与密码信息
     * @params [user] 待注册的用户信息
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean registerUser(SysUser user);

    /**
     * @return int 更新成功的记录数
     * @description 修改用户信息并重建其岗位与角色的关联关系
     * @params [user] 待修改的用户信息，包含岗位与角色主键集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int updateUser(SysUser user);

    /**
     * @description 覆盖用户与角色的授权关系，先清理原有关联再写入新角色
     * @params [userId, roleIds] 被授权的用户主键；授予的角色主键集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void insertUserAuth(Long userId, Long[] roleIds);

    /**
     * @return int 更新成功的记录数
     * @description 只更新用户的状态字段，不改动其它用户信息
     * @params [user] 携带用户主键与目标状态的用户信息
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int updateUserStatus(SysUser user);

    /**
     * @return int 更新成功的记录数
     * @description 更新用户的基本资料字段，不涉及角色与岗位关联
     * @params [user] 待修改的用户基本信息
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int updateUserProfile(SysUser user);

    /**
     * @return boolean 更新成功返回 true
     * @description 更新指定用户的头像地址
     * @params [userName, avatar] 登录用户名；头像访问地址
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean updateUserAvatar(String userName, String avatar);

    /**
     * @return int 更新成功的记录数
     * @description 按用户主键重置该用户的登录密码
     * @params [user] 携带用户主键与加密后新密码的用户信息
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int resetPwd(SysUser user);

    /**
     * @return int 更新成功的记录数
     * @description 按用户名重置该用户的登录密码
     * @params [userName, password] 登录用户名；加密后的新密码
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int resetUserPwd(String userName, String password);

    /**
     * @return int 删除成功的记录数
     * @description 按用户主键删除用户，并级联清理其角色与岗位关联
     * @params [userId] 要删除的用户主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int deleteUserById(Long userId);

    /**
     * @return int 删除成功的记录数
     * @description 按用户主键集合批量删除用户，并级联清理其角色与岗位关联
     * @params [userIds] 要删除的用户主键数组
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int deleteUserByIds(Long[] userIds);

    /**
     * @return String 导入结果提示，逐行说明成功、更新或失败信息
     * @description 批量导入用户数据，按是否允许更新决定已存在用户的新增还是覆盖
     * @params [userList, isUpdateSupport, operName] 待导入的用户数据列表；是否允许更新已存在用户；执行导入的操作人名称，写入用户创建者
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName);

    /**
     * @return String 用户昵称，用户不存在时返回 null
     * @description 按用户主键查询其昵称，默认实现先查用户再取昵称字段
     * @params [userId] 用户主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    default String selectNickNameById(Long userId) {
        SysUser u = selectUserById(userId);
        return u == null ? null : u.getNickName();
    }

    /**
     * @return List<SysUser> 该角色下的用户集合，默认实现返回空集合
     * @description 按角色主键查询归属于该角色的用户列表，默认实现不做过滤直接返回空集合
     * @params [roleId] 角色主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    default List<SysUser> selectRoleToUser(Long roleId) {
        return java.util.Collections.emptyList();
    }
}
