package com.basic.flowable.common.enums;


import com.basic.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description 流程审批状态枚举，定义进行中、终止、已完成、退回、取消、删除等状态并支持按编码反查
 * @author yanbin
 * @date 2024/1/8
 */
@Getter
@AllArgsConstructor
public enum ProcessStatus {

    /**
     * 进行中（审批中）
     */
    RUNNING("running"),
    /**
     * 已终止(拒绝流程)
     */
    REJECT("reject"),
    /**
     * 已完成(正常完成流程)
     */
    COMPLETED("completed"),
    /**
     * 已退回(节点退回)
     */
    RETURN("return"),
    /**
     * 已取消(发起人取消流程申请)
     */
    CANCELED("canceled"),
    /**
     * 删除
     */
    DELETE("delete");

    private final String status;

    /**
     * 按状态编码反查对应的流程审批状态枚举，比较时忽略大小写。
     *
     * @param str 状态编码
     * @return 匹配的枚举项，入参为空或无匹配时返回null
     */
    public static ProcessStatus getProcessStatus(String str) {
        if (StringUtils.isNotBlank(str)) {
            for (ProcessStatus value : values()) {
                if (StringUtils.equalsIgnoreCase(str, value.getStatus())) {
                    return value;
                }
            }
        }
        return null;
    }
}
