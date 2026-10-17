package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.basic.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程表单实体，对应表 wf_form，保存自定义表单的名称、内容与编码
 * @author yanbin
 * @date 2023/11/3 10:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "流程表单对象")
@TableName("wf_form")
public class WfForm extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 表单主键
     */
    @TableId(value = "form_id", type = IdType.ASSIGN_UUID)
    @Schema(description = "表单主键id")
    private String formId;
    /**
     * 表单名称
     */
    @Schema(description = "表单名称")
    private String formName;
    /**
     * 表单内容
     */
    @Schema(description = "表单内容")
    private String content;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 标识
     */
    @Schema(description = "标识")
    private String code;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private String status;

    /**
     * 表单类型
     */
    @Schema(description = "表单类型")
    private String type;

    /**
     * 分类id
     */
    @Schema(description = "分类id")
    private String categoryId;
}
