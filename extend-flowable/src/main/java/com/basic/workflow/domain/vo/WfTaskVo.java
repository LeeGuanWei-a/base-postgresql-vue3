package com.basic.workflow.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.basic.workflow.domain.dto.WfCommentDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 工作流任务视图对象，返回任务、流程实例、办理人与审批意见等展示信息
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
@Schema(description = "工作流任务视图对象")
public class WfTaskVo implements Serializable {
    /**
     * 任务编号
     */
    @Schema(description = "任务编号id")
    private String taskId;
    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String taskName;
    /**
     * 任务Key
     */
    @Schema(description = "任务Key")
    private String taskDefKey;
    /**
     * 任务执行人Id
     */
    @Schema(description = "任务执行人Id")
    private String assigneeId;
    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    @Deprecated
    private String deptName;
    /**
     * 流程发起人部门名称
     */
    @Schema(description = "流程发起人部门名称")
    @Deprecated
    private String startDeptName;
    /**
     * 任务执行人名称
     */
    @Schema(description = "任务执行人名称")
    private String assigneeName;
    /**
     * 流程发起人Id
     */
    @Schema(description = "流程发起人Id")
    private String startUserId;
    /**
     * 流程发起人名称
     */
    @Schema(description = "流程发起人名称")
    private String startUserName;
    /**
     * 流程分类类型
     */
    @Schema(description = "流程分类类型")
    private String category;
    /**
     * 流程分类类型名称
     */
    @Schema(description = "流程类型名称")
    private String categoryName;
    /**
     * 流程变量信息
     */
    @Schema(description = "流程变量信息")
    private Object procVars;
    /**
     * 局部变量信息
     */
    @Schema(description = "局部变量信息")
    private Object taskLocalVars;
    /**
     * 流程部署编号
     */
    @Schema(description = "流程部署编号")
    private String deployId;
    /**
     * 流程定义ID
     */
    @Schema(description = "流程定义ID")
    private String procDefId;
    /**
     * 流程key
     */
    @Schema(description = "流程key")
    private String procDefKey;
    /**
     * 流程定义名称
     */
    @Schema(description = "流程定义名称")
    private String procDefName;
    /**
     * 流程定义内置使用版本
     */
    @Schema(description = "流程定义内置使用版本")
    private int procDefVersion;
    /**
     * 流程实例ID
     */
    @Schema(description = "流程实例ID")
    private String procInsId;
    /**
     * 历史流程实例ID
     */
    @Schema(description = "历史流程实例ID")
    private String hisProcInsId;
    /**
     * 任务耗时
     */
    @Schema(description = "任务耗时")
    private String duration;
    /**
     * 任务意见
     */
    @Schema(description = "任务意见")
    private WfCommentDto comment;
    /**
     * 任务意见
     */
    @Schema(description = "任务意见")
    private List<Comment> commentList;
    /**
     * 候选执行人的名称集合
     */
    @Schema(description = "候选执行人的名称集合")
    private List<String> candidateList;
    /**
     * 候选执行人
     */
    @Schema(description = "候选执行人")
    private String candidate;
    /**
     * 候选执行组
     */
    @Schema(description = "候选执行组")
    private String candidateGroup;
    /**
     * 任务创建时间  即开始时间
     */
    @Schema(description = "任务创建时间 即开始时间")
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private Date createTime;
    /**
     * 任务停留时间
     */
    @Schema(description = "任务停留时间")
    private String stayTime;

    /**
     * 任务完成时间
     */
    @Schema(description = "任务完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;
    /**
     * 流程变量
     */
    @Schema(description = "流程变量")
    private Map<String, Object> variables;
    /**
     * 流程状态
     */
    @Schema(description = "流程状态")
    private String processStatus;
    /**
     * 路由地址
     */
    @Schema(description = "路由地址")
    private String url;
    /**
     * 流程活动id 模块id
     */
    @Schema(description = "流程活动id")
    private String activityId;
    /**
     * 流程模型id
     */
    @Schema(description = "流程模型id")
    private String modelId;
    /**
     * 流程模型key
     */
    @Schema(description = "流程模型key")
    private String modelKey;
    /**
     * 启动节点activityId
     */
    @Schema(description = "启动节点activityId")
    private String startActivityId;
    /**
     * 流程发起人
     */
    @Schema(description = "流程发起人")
    private String startUser;
    /**
     * 正在运行的实例id
     */
    @Schema(description = "正在运行的实例id")
    private String executionId;
    /**
     * 任务Task列表
     */
    @Schema(description = "任务Task列表")
    private List<Task> taskList;
    /**
     * 业务系统标识 必填
     */
    @Schema(description = "业务系统标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appSn;
    /**
     * 通知类型,可能与抄送混为一谈    todo待办，own，已发，已办finished，抄送copy，已签claimed,待签claiming;
     */
    @Schema(description = "通知类型")
    private String noticeType;

    /**
     * 是否与发起人相同
     */
    @Schema(description = "是否与发起人相同")
    private Boolean isStartUser;
    /**
     * 是否是当前任务的审批人
     */
    @Schema(description = "是否是当前任务的审批人")
    private Boolean isAuditUser;
    /**
     * 展示内容
     */
    @Schema(description = "展示内容")
    private String content;
    /**
     * 父级procInsId
     */
    @Schema(description = "父级procInsId")
    private String applyProcInsId;
    /**
     * 是否已读
     */
    @TableField(exist = false)
    private Integer isRead;
    /**
     * 当前节点「授权功能」(idmPermission) 的原始 JSON 数组文本，
     * 例如 ["complete","reject","return","transfer","delegate","copy"]。
     * 列表与详情页都据此动态渲染审批按钮；由前端 JSON.parse。
     */
    @TableField(exist = false)
    private String permissionJson;
    /**
     * 当前节点是否是「顺序会签」（串行多实例 + 全票通过）。
     * 只有这种节点才显示「会签回退」（同节点回退到上一位处理人）。
     */
    @TableField(exist = false)
    private Boolean sequentialMultiInstance;
}
