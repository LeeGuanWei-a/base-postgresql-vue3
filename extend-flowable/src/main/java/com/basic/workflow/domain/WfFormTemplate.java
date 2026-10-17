package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.basic.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程表单模板对象，对应表 wf_form_template，保存表单名称、表单内容与示例图片
 * @author yanbin
 * @date 2023/11/3 10:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "流程表单模板对象")
@TableName("wf_form_template")
public class WfFormTemplate extends BaseEntity {
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
     * 模板示例图片
     */
    @Schema(description = "模板示例图片")
    private String imgUrl;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @Schema(description = "删除标志")
    private String delFlag;
}
