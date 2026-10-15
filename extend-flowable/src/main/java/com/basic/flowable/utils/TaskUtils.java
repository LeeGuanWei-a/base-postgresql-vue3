package com.basic.flowable.utils;

import cn.hutool.core.util.ObjectUtil;
import com.basic.common.core.domain.entity.SysRole;
import com.basic.common.core.domain.entity.SysUser;
import com.basic.common.utils.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanbin
 * @description TODO (工作流任务工具类)
 * @date 2024/1/8
 */
public class TaskUtils {
    public static String getUserId() {
        return String.valueOf(SecurityUtils.getLoginUser().getUserId());
    }

    /**
     * 获取用户组信息
     *
     * @return candidateGroup
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
     * @return boolean
     * @description TODO (判断两个list是否相同)
     * @params [list1, list2]
     * @author yanbin
     * @date 2024/1/8 14:24
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
        return CollectionUtils.isEqualCollection(list1, list2);
    }

    /**
     * @return java.lang.String
     * @description TODO (计算任务的停留时间)
     * @params [createTime]
     * @author yanbin
     * @date 2024/2/5 11:41
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
