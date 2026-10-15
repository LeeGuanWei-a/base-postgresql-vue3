package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.basic.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author yanbin
 * @description TODO (流程意见对象 流程流转记录表 wf_comment_info)
 * @date 2024/01/17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wf_comment_info")
public class WfCommentInfo extends BaseEntity {
    /**
     * 主键
     */
    @TableId(value = "id")
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

    public WfCommentInfo() {

    }

    public WfCommentInfo(String type, String userId,
                         String procInsId, String message, String activityId, String activityName) {
        this.type = type;
        this.userId = userId;
        this.procInsId = procInsId;
        this.message = message;
        this.activityId = activityId;
        this.activityName = activityName;
    }

    public WfCommentInfo(String userId, String prcInsId, String type, String message) {
        this.type = type;
        this.userId = userId;
        this.message = message;
        this.procInsId = prcInsId;
    }
}
