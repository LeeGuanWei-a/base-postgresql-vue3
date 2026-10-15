package com.basic.workflow.domain.bo;


import com.basic.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yanbin
 * @description TODO (流程表单业务对象)
 * @date 2024/1/16 10:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "WfFormBo", description = "流程表单业务对象")
public class WfFormBo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 表单主键
     */
    @ApiModelProperty(value = "表单主键id")
    private String formId;

    /**
     * 表单名称
     */
    @ApiModelProperty(value = "表单名称")
    private String formName;

    /**
     * 表单内容
     */
    @ApiModelProperty(value = "表单内容")
    private String content;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String status;

    /**
     * 标识
     */
    @ApiModelProperty(value = "标识")
    private String code;

    /**
     * 表单类型
     */
    @ApiModelProperty(value = "表单类型")
    private String type;

    /**
     * 分类id
     */
    @ApiModelProperty(value = "分类id")
    private String categoryId;
}
