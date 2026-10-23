package com.basic.workflow.domain.bo;

import com.basic.common.core.domain.BaseEntity;
import com.basic.common.core.validate.AddGroup;
import com.basic.common.core.validate.EditGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程抄送业务对象，对应表 wf_copy，承载抄送记录的标题、流程、任务与接收人信息
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "流程抄送业务对象")
public class WfCopyBo extends BaseEntity {

    /**
     * 抄送主键
     */
    @Schema(description = "抄送主键id")
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
    @Schema(description = "流程分类主键id")
    private String categoryId;

    /**
     * 任务主键
     */
    @Schema(description = "任务主键id")
    private String taskId;

    /**
     * 用户主键
     */
    @NotNull(message = "用户主键不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "用户主键id", requiredMode = Schema.RequiredMode.REQUIRED)
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
     * 抄送状态
     */
    @Schema(description = "抄送状态")
    private String status;
}
