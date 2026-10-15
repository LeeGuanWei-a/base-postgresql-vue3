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
 * @description TODO (流程表单对象 wf_form)
 * @date 2023/11/3 10:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "WfForm", description = "流程表单对象")
@TableName("wf_form")
public class WfForm extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 表单主键
     */
    @TableId(value = "form_id")
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
     * 标识
     */
    @ApiModelProperty(value = "标识")
    private String code;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String status;

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
