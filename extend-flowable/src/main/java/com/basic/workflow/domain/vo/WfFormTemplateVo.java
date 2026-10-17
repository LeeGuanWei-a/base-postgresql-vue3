package com.basic.workflow.domain.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程表单视图对象，承载表单主键、名称、内容及创建人时间等信息
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
@Schema(description = "流程表单视图对象")
public class WfFormTemplateVo {

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
     * 创建人
     */
    @Schema(description = "创建人")
    private String createName;

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
     * 模板示例图片
     */
    @Schema(description = "模板示例图片")
    private String imgUrl;
}
