package com.basic.constant;

/**
 * @description 流程引擎通用常量，含发起人变量与节点名、节点自动跳过开关、实例挂起与激活状态以及加签、转办等操作类型标识
 * @author yanbin
 * @date 2023/10/24
 */
public class FlowConstant {

    /**
     * 提交人的变量名称
     */
    public static final String FLOW_SUBMITTER_VAR = "initiator";
    /**
     * 提交人节点名称
     */
    public static final String FLOW_SUBMITTER = "提交人";
    /**
     * 自动跳过节点设置属性
     */
    public static final String FLOWABLE_SKIP_EXPRESSION_ENABLED = "_FLOWABLE_SKIP_EXPRESSION_ENABLED";
    /**
     * 挂起状态
     */
    public static final int SUSPENSION_STATE = 2;
    /**
     * 激活状态
     */
    public static final int ACTIVATE_STATE = 1;
    /**
     * 后加签
     */
    public static final String AFTER_ADDSIGN = "after";
    /**
     * 前加签
     */
    public static final String BEFORE_ADDSIGN = "before";

    /**
     * 转办任务
     */
    public static final String TRANSFER = "transfer";
}
