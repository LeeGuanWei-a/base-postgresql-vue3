package com.basic.workflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author yanbin
 * @description TODO (流程分类视图对象 vo)
 * @date 2024/1/16 10:37
 */
@Data
@ApiModel(value = "WfCategoryVo", description = "流程分类视图对象")
public class WfCategoryVo {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @ApiModelProperty(value = "分类ID ")
    private Long categoryId;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称 ")
    private String categoryName;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称 ")
    private String name;

    /**
     * 分类编码
     */
    @ApiModelProperty(value = "分类编码 ")
    private String code;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注 ")
    private String remark;

    /**
     * 父级分类的id
     */
    @ApiModelProperty(value = "父级分类的id ")
    private Long parent;

    /**
     * 子类列表
     */
    @ApiModelProperty(value = "子类列表 ")
    private List<WfCategoryVo> children;

    /**
     * 路由地址，业务表单跳转
     */
    @ApiModelProperty(value = "路由地址，业务表单跳转")
    private String url;
    /**
     * 分类对应流程xml
     */
    private String modelXml;

    /** 创建者 */
    @ApiModelProperty(value = "创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty(value = "更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
