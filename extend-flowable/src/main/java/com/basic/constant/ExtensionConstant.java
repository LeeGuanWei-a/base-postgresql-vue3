package com.basic.constant;

/**
 * @author yanbin
 * @description TODO (工作流userTask拓展属性  常量)
 * @date 2024/01/17
 */
public class ExtensionConstant {
    /**
     * 人员选择类型 0：固定值，1：身份存储
     */
    public static final String ASSIGNEE_TYPE = "assigneeType";
    /**
     * 候选角色组
     */
    public static final String IDM_CANDIDATE_GROUPS = "idmCandidateGroups";
    /**
     * 候选人员组
     */
    public static final String IDM_CANDIDATE_USERS = "idmCandidateUsers";
    /**
     * 是否编辑
     */
    public static final String IS_EDITDATA = "isEditData";
    /**
     * 节点类型
     */
    public static final String NODE_TYPE = "nodeType";
    /**
     * 是否自动跳过
     */
    public static final String IS_SKIP = "isSkip";
    /**
     * 是否交替节点
     */
    public static final String IS_ROLE_REPLACE = "isRoleReplace";
    /**
     * 审批配置,开启后当前审批人处理相同节点只需一次
     */
    public static final String IS_EXAMINED = "isExamined";
    /**
     * 强制撤回 ，下一节点已有人审批但未流转 可以强制撤回
     */
    public static final String IS_REVOKE = "isRevoke";
    /**
     * 按钮授权功能
     */
    public static final String IDM_PERMISSION = "idmPermission";
    /**
     * 人员筛选
     */
    public static final String IS_USER_SCREEN = "isUserScreen";
    /**
     * 组织筛选
     */
    public static final String IS_ORG_SCREEN = "isOrgScreen";
    /**
     * 部门筛选or组织筛选
     */
    public static final String IS_DEPT_OR_ORG = "isDeptOrOrg";
    /**
     * 处理人为空跳过
     */
    public static final String IS_NULL_SKIP = "isNullSkip";
}
