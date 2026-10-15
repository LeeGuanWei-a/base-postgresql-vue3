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
public class WfFormVo {

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
     * 标识
     */
    @ApiModelProperty(value = "标识")
    private String code;

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
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String status;
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
     * 表单类型
     */
    @ApiModelProperty(value = "表单类型")
    private String type;

    /**
     * 分类id
     */
    @ApiModelProperty(value = "分类id")
    private String categoryId;


    /**
     * 表单标识集合，用逗号隔开
     */
    @ApiModelProperty(value = "表单标识集合，用逗号隔开")
    private String codes;

    /**
     * 表单标识集合
     */
    @ApiModelProperty(value = "表单标识集合")
    private String[] codeList;
    /**
     * 排除字段，多个用逗号隔开
     */
    @ApiModelProperty(value = "排除字段，多个用逗号隔开")
    private String excludeField;
}
