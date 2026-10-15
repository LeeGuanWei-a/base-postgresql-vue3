package com.basic.flowable.common.enums;

/**
 * @author yanbin
 * @description TODO (流程意见类型)
 * @date 2023/10/25
 */
public enum FlowComment {

    /**
     * 说明
     */
    NORMAL("1", "正常"),
    REBACK("2", "退回"),
    REJECT("3", "驳回"),
    DELEGATE("4", "委派"),
    TRANSFER("5", "转办"),
    STOP("6", "终止"),
    REVOKE("7", "撤回"),
    QJQ("8", "前加签"),
    HJQ("9", "后加签"),
    JQSP("10", "加签审批"),
    TJ("11", "提交"),
    SKIP("12", "跳转"),
    /**
     * 会签回退：顺序会签节点回退给上一位处理人（同节点）。
     * 与「驳回」语义完全不同，单独一个类型 —— 之前代码里硬编码成了 "3"（驳回），
     * 导致会签回退在审批记录里被显示成"驳回"。
     * 注意："13"（结束节点）和 "100"（待办节点）已被 WfProcessCommentService
     * 用作合成记录的伪类型，所以这里从 14 起。
     */
    HSH("14", "会签回退");

    /**
     * 类型
     */
    private final String type;

    /**
     * 说明
     */
    private final String remark;

    FlowComment(String type, String remark) {
        this.type = type;
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public String getRemark() {
        return remark;
    }
}
