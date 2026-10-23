package com.basic.workflow.domain.bo;


import com.basic.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程表单业务对象，承载表单主键、名称、内容、类型与分类等新增修改参数
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "流程表单业务对象")
public class WfFormBo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 表单主键
     */
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
     * 状态
     */
    @Schema(description = "状态")
    private String status;

    /**
     * 标识
     */
    @Schema(description = "标识")
    private String code;

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
