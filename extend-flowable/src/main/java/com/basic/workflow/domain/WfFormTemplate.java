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
@ApiModel(value = "WfFormTemplate", description = "流程表单模板对象")
@TableName("wf_form_template")
public class WfFormTemplate extends BaseEntity {
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
     * 模板示例图片
     */
    @ApiModelProperty(value = "模板示例图片")
    private String imgUrl;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志")
    private String delFlag;
}
