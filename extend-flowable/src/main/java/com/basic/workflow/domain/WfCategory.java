package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.basic.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (流程分类对象 wf_category)
 * @date 2024/1/16 10:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "WfCategory", description = "流程分类对象")
@TableName("wf_category")
public class WfCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @TableId(value = "category_id")
    @ApiModelProperty(value = "分类ID ")
    private Long categoryId;
    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称 ")
//    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    /**
     * 分类编码
     */
    @ApiModelProperty(value = "分类编码 ")
//    @NotBlank(message = "分类编码不能为空")
    private String code;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注 ")
    private String remark;

    /**
     * 父级分类id
     */
    @ApiModelProperty(value = "父级分类id ")
    private Long parent;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除） ")
    @TableLogic
    private String delFlag = "0";

    @ApiModelProperty(value = "分类编码字符串")
    @TableField(exist = false)
    private List codes;

    /**
     * 跳转地址，业务表单
     */
    @ApiModelProperty(value = "跳转地址，业务表单")
//    @NotBlank(message = "跳转地址不能为空")
    private String url;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public List getCodes() {
        return codes;
    }

    public void setCodes(List codes) {
        this.codes = codes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
