package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.basic.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @description 流程意见实体，对应表 wf_comment_info，保存流程流转中的审批意见与处理记录
 * @author yanbin
 * @date 2024/01/17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_comment_info")
public class WfCommentInfo extends BaseEntity {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 类型
     */
    private String type;
    /**
     * 处理人id
     */
    private String userId;
    /**
     * 审批时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 活动id
     */
    private String activityId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 实例id
     */
    private String procInsId;
    /**
     * 动作
     */
    private String action;
    /**
     * 审批意见
     */
    private String message;
    /**
     * 删除标识
     */
    private String delFlag;
    /**
     * 处理人名称
     */
    @TableField(exist = false)
    private String userName;
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date endTime;
    /**
     * 耗时
     */
    @TableField(exist = false)
    private String duration;

    /**
     * 节点类型
     */
    @TableField(exist = false)
    private String activityType;

    /**
     * @description 构造带任务 ID 的流转意见，审批时间初始化为当前时刻
     * @params [taskId, userId, procInsId, type, message, activityId, activityName] 任务 ID；处理人 ID；流程实例 ID；意见类型编码，取值见 FlowComment；审批意见内容；节点（活动）ID；节点（活动）名称
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfCommentInfo(String taskId, String userId, String procInsId, String type,
                         String message, String activityId, String activityName) {
        this.taskId = taskId;
        this.userId = userId;
        this.procInsId = procInsId;
        this.message = message;
        this.type = type;
        this.activityId = activityId;
        this.activityName = activityName;
        this.time = new Date();
    }

    /**
     * @description 构造空的流转意见对象，字段由调用方通过 setter 填充
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfCommentInfo() {

    }

    /**
     * @description 构造不带任务 ID 的流转意见，审批时间由调用方赋值
     * @params [type, userId, procInsId, message, activityId, activityName] 意见类型编码，取值见 FlowComment；处理人 ID；流程实例 ID；审批意见内容；节点（活动）ID；节点（活动）名称
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfCommentInfo(String type, String userId,
                         String procInsId, String message, String activityId, String activityName) {
        this.type = type;
        this.userId = userId;
        this.procInsId = procInsId;
        this.message = message;
        this.activityId = activityId;
        this.activityName = activityName;
    }

    /**
     * @description 构造仅含处理人、实例、类型与意见的简化流转意见
     * @params [userId, prcInsId, type, message] 处理人 ID；流程实例 ID；意见类型编码，取值见 FlowComment；审批意见内容
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfCommentInfo(String userId, String prcInsId, String type, String message) {
        this.type = type;
        this.userId = userId;
        this.message = message;
        this.procInsId = prcInsId;
    }
}
