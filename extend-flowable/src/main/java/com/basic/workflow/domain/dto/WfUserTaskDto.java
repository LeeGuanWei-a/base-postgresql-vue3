package com.basic.workflow.domain.dto;

import com.basic.common.core.domain.entity.SysRole;
import com.basic.common.core.domain.entity.SysUser;
import lombok.Data;
import org.flowable.bpmn.model.UserTask;

import java.io.Serializable;
import java.util.List;

/**
 * @description 流程用户任务参数对象，描述任务节点的多实例类型、参数以及候选用户与候选角色等人员配置
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
public class WfUserTaskDto implements Serializable {
    /**
     * 多实例类型
     **/
    private String type;
    /**
     * 多实例参数
     **/
    private String vars;
    /**
     * 身份存储/固定值
     */
    private String dataType;
    /**
     * 候选用户
     */
    private List<SysUser> userList;
    /**
     * 候选角色
     */
    private List<SysRole> roleList;
    /**
     * 用户模块
     */
    private UserTask userTask;
    /**
     * 节点类型  网关，结束
     */
    private String nodeType;
    /**
     * 审批人assignee
     */
    private String assignee;
    /**
     * 模块id
     */
    private String activityId;
    /**
     * 模块名称
     */
    private String activityName;
    /**
     * 相同处理人自动跳过 false,true;
     */
    private String isSkip;
    /**
     * 是否是交替节点 false,true;
     */
    private String isRoleReplace;
    /**
     * 审批配置,开启后当前审批人处理相同节点只需一次
     */
    private String isExamined;

    /**
     * 组织筛选
     */
    private String isOrgScreen;
    /**
     * 人员筛选
     */
    private String isUserScreen;

    /**
     * 组织筛选还是部门筛选
     */
    private String isDeptOrOrg;

    /**
     * 为空跳过
     */
    private String isNullSkip;

    /**
     * 该节点允许的操作按钮：节点「授权功能」扩展（idmPermission）的原始 JSON 数组文本，
     * 例如 ["complete","reject","return","transfer","delegate","copy"]。
     * 前端据此动态渲染审批按钮；用字符串承载，由前端 JSON.parse。
     */
    private String permissionJson;

    /**
     * 是否是「顺序会签」节点（串行多实例 + 全票通过）。
     * 只有这种节点才支持「会签回退到上一个处理人」（同节点回退）。
     */
    private Boolean sequentialMultiInstance;
}

