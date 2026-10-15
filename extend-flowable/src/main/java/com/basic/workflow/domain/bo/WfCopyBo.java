package com.basic.workflow.domain.bo;

import com.basic.common.core.domain.BaseEntity;
import com.basic.common.core.validate.AddGroup;
import com.basic.common.core.validate.EditGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author yanbin
 * @description TODO (流程抄送业务对象 wf_copy)
 * @date 2024/1/16 10:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "WfCopyBo", description = "流程抄送业务对象")
public class WfCopyBo extends BaseEntity {

    /**
     * 抄送主键
     */
    @ApiModelProperty(value = "抄送主键id")
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
    @ApiModelProperty(value = "流程分类主键id")
    private String categoryId;

    /**
     * 任务主键
     */
    @ApiModelProperty(value = "任务主键id")
    private String taskId;

    /**
     * 用户主键
     */
    @NotNull(message = "用户主键不能为空", groups = {AddGroup.class, EditGroup.class})
    @ApiModelProperty(value = "用户主键id", required = true)
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
     * 抄送状态
     */
    @ApiModelProperty(value = "抄送状态")
    private String status;
}
