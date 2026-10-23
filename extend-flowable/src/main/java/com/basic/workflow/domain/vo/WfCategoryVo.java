package com.basic.workflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程分类视图对象，输出分类的标识、名称、编码与父子层级等展示信息
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
@Schema(description = "流程分类视图对象")
public class WfCategoryVo {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID ")
    private Long categoryId;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称 ")
    private String categoryName;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称 ")
    private String name;

    /**
     * 分类编码
     */
    @Schema(description = "分类编码 ")
    private String code;

    /**
     * 备注
     */
    @Schema(description = "备注 ")
    private String remark;

    /**
     * 父级分类的id
     */
    @Schema(description = "父级分类的id ")
    private Long parent;

    /**
     * 子类列表
     */
    @Schema(description = "子类列表 ")
    private List<WfCategoryVo> children;

    /**
     * 路由地址，业务表单跳转
     */
    @Schema(description = "路由地址，业务表单跳转")
    private String url;
    /**
     * 分类对应流程xml
     */
    private String modelXml;

    /** 创建者 */
    @Schema(description = "创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    /** 更新者 */
    @Schema(description = "更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;
}
