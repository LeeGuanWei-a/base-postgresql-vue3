package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.basic.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程抄送对象，对应表 wf_copy，记录抄送标题、流程实例、任务与抄送人
 * @author yanbin
 * @date 2023/11/3 10:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "流程抄送对象")
@TableName("wf_copy")
public class WfCopy extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 抄送主键
     */
    @Schema(description = "抄送主键id")
    @TableId(value = "copy_id")
    private Long copyId;
    /**
     * 抄送标题
     */
    @Schema(description = "抄送标题")
    private String title;
    /**
     * 流程主键
     */
    @Schema(description = "流程主键id")
    private String processId;
    /**
     * 流程名称
     */
    @Schema(description = "流程名称")
    private String processName;
    /**
     * 流程分类主键
     */
    @Schema(description = "流程分类主键code")
    private String categoryId;
    /**
     * 部署主键
     */
    @Schema(description = "部署主键id")
    private String deploymentId;
    /**
     * 流程实例主键
     */
    @Schema(description = "流程实例主键id")
    private String instanceId;
    /**
     * 任务主键
     */
    @Schema(description = "任务主键id")
    private String taskId;
    /**
     * 用户主键
     */
    @Schema(description = "用户主键id")
    private String userId;
    /**
     * 发起人Id
     */
    @Schema(description = "发起人Id")
    private String originatorId;
    /**
     * 发起人名称
     */
    @Schema(description = "发起人名称")
    private String originatorName;

    /**
     * 状态（0未阅 1已阅）
     */
    @Schema(description = "状态（0未阅 1已阅）")
    private String status;
    /**
     * 活动id
     */
    @Schema(description = "活动id")
    private String activityId;
}
