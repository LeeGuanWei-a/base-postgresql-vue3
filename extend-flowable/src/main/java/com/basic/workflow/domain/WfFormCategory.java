package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.basic.common.annotation.Excel;
import com.basic.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * 自定义单分类对象 wf_form_category
 *
 * @author basic
 * @date 2024-03-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "自定义单分类对象")
@TableName("wf_form_category")
public class WfFormCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分类主键 */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String categoryName;

    /** 父级分类（没有父级则为0） */
    @Excel(name = "父级分类", readConverterExp = "没=有父级则为0")
    private String parent;

    /** 子类 */
    @TableField(exist = false)
    private List<WfFormCategory> children;
}
