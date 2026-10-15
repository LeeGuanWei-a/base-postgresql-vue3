package com.basic.flowable.common.enums;


import com.basic.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yanbin
 * @description TODO (流程审批状态)
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
