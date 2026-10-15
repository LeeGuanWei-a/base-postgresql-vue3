package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.basic.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yanbin
 * @description TODO (流程抄送对象 wf_copy)
 * @date 2023/11/3 10:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "WfCopy", description = "流程抄送对象")
@TableName("wf_copy")
public class WfCopy extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 抄送主键
     */
    @ApiModelProperty(value = "抄送主键id")
    @TableId(value = "copy_id")
    private Long copyId;
    /**
     * 抄送标题
     */
    @ApiModelProperty(value = "抄送标题")
    private String title;
    /**
     * 流程主键
     */
    @ApiModelProperty(value = "流程主键id")
    private String processId;
    /**
     * 流程名称
     */
    @ApiModelProperty(value = "流程名称")
    private String processName;
    /**
     * 流程分类主键
     */
    @ApiModelProperty(value = "流程分类主键code")
    private String categoryId;
    /**
     * 部署主键
     */
    @ApiModelProperty(value = "部署主键id")
    private String deploymentId;
    /**
     * 流程实例主键
     */
    @ApiModelProperty(value = "流程实例主键id")
    private String instanceId;
    /**
     * 任务主键
     */
    @ApiModelProperty(value = "任务主键id")
    private String taskId;
    /**
     * 用户主键
     */
    @ApiModelProperty(value = "用户主键id")
    private String userId;
    /**
     * 发起人Id
     */
    @ApiModelProperty(value = "发起人Id")
    private String originatorId;
    /**
     * 发起人名称
     */
    @ApiModelProperty(value = "发起人名称")
    private String originatorName;

    /**
     * 状态（0未阅 1已阅）
     */
    @ApiModelProperty(value = "状态（0未阅 1已阅）")
    private String status;
    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动id")
    private String activityId;
}
