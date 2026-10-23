package com.basic.flowable.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.basic.common.core.domain.entity.SysRole;
import com.basic.common.core.domain.entity.SysUser;
import com.basic.common.utils.SecurityUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 工作流任务工具类，提供当前登录用户 ID、候选角色组等任务办理所需的用户信息
 * @author yanbin
 * @date 2024/1/8
 */
public class TaskUtils {
    public static String getUserId() {
        return String.valueOf(SecurityUtils.getLoginUser().getUserId());
    }

    /**
     * @return List<String> 当前登录用户的角色主键字符串集合
     * @description 取当前登录用户的角色主键，作为流程任务的候选组使用
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static List<String> getCandidateGroup() {
        List<String> list = new ArrayList<>();
        // 获取当前用户信息
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 获取当前用户的角色id
        List<Long> roleIds =
                user.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList());
        if (ObjectUtil.isNotNull(user)) {
            if (!roleIds.isEmpty()) {
                for (Long r : roleIds) {
                    list.add(r.toString());
                }
            }
        }
        return list;
    }

    /**
     * @return boolean 两个集合元素完全相同（忽略顺序）时返回 true
     * @description 忽略顺序判断两个集合的元素是否完全相同，任一方为 null 时按同一性判定
     * @params [list1, list2] 待比较的集合；待比较的集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static boolean checkListEqual(Collection list1, Collection list2) {
        if (list1 == null && list2 == null) {
            return true;
        } else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }
        // 元素相同即视为相等（忽略顺序）——原实现用 commons-collections 的
        // isEqualCollection，这里直接按 containsAll 实现，少一个依赖
        return list1.containsAll(list2) && list2.containsAll(list1);
    }

    /**
     * @return String 形如「x天y时」的停留时长
     * @description 按当前时间与任务发起时间计算任务已停留的天数与小时数
     * @params [createTime] 任务发起时间
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static String getStayTime(Date createTime) {
        //当前时间
        Date nowTime = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long begin = nowTime.getTime();
        //任务发起时间
        Long end = createTime.getTime();
        long timeLag = begin - end;
        //天
        long day = timeLag / (24 * 60 * 60 * 1000);
        //小时
        long hour = (timeLag / (60 * 60 * 1000) - day * 24);
        //分钟
        long minute = ((timeLag / (60 * 1000)) - day * 24 * 60 - hour * 60);
        //秒
        long s = (timeLag / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);
        return day + "天" + hour + "时";
    }
}
