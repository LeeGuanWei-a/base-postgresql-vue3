package com.basic.workflow.domain.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author yanbin
 * @description TODO (流程表单视图对象VO )
 * @date 2024/1/16 10:37
 */
@Data
@ApiModel(value = "WfFormVo", description = "流程表单视图对象")
public class WfFormTemplateVo {

    private static final long serialVersionUID = 1L;

    /**
     * 表单主键
     */
    @ApiModelProperty(value = "表单主键 ")
    private String formId;

    /**
     * 表单名称
     */
    @ApiModelProperty(value = "表单名称 ")
    private String formName;

    /**
     * 表单内容
     */
    @ApiModelProperty(value = "表单内容 ")
    private String content;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注 ")
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createName;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者")
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 模板示例图片
     */
    @ApiModelProperty(value = "模板示例图片")
    private String imgUrl;
}
