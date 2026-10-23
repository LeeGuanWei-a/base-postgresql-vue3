package com.basic.workflow.domain.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程表单视图对象，记录表单主键、名称、标识、内容、类型与分类等表单信息
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
@Schema(description = "流程表单视图对象")
public class WfFormVo {

    private static final long serialVersionUID = 1L;

    /**
     * 表单主键
     */
    @Schema(description = "表单主键 ")
    private String formId;

    /**
     * 表单名称
     */
    @Schema(description = "表单名称 ")
    private String formName;

    /**
     * 标识
     */
    @Schema(description = "标识")
    private String code;

    /**
     * 表单内容
     */
    @Schema(description = "表单内容 ")
    private String content;

    /**
     * 备注
     */
    @Schema(description = "备注 ")
    private String remark;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private String status;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;
    /**
     * 更新者
     */
    @Schema(description = "更新者")
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;

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


    /**
     * 表单标识集合，用逗号隔开
     */
    @Schema(description = "表单标识集合，用逗号隔开")
    private String codes;

    /**
     * 表单标识集合
     */
    @Schema(description = "表单标识集合")
    private String[] codeList;
    /**
     * 排除字段，多个用逗号隔开
     */
    @Schema(description = "排除字段，多个用逗号隔开")
    private String excludeField;
}
