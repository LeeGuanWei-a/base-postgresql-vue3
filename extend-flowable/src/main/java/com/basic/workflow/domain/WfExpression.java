package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.basic.common.annotation.Excel;
import com.basic.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author yanbin
 * @description TODO (流程表达式对象 flw_expression )
 * @date 2024/1/16 10:37
 */
@ApiModel(value = "WfExpression", description = "流程表达式对象")
@Data
@TableName("flw_expression")
public class WfExpression extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 表单主键
     */
    @ApiModelProperty(value = "表达式主键id")
    private Long id;

    /**
     * 表达式名称
     */
    @Excel(name = "表达式名称")
    @ApiModelProperty(value = "表达式名称")
    private String name;

    /**
     * 表达式内容
     */
    @Excel(name = "表达式内容")
    @ApiModelProperty(value = "表达式内容")
    private String expression;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * 表达式类型
     */
    @ApiModelProperty(value = "表达式类型")
    private String type;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("expression", getExpression())
                .append("type", getType())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("createBy", getCreateBy())
                .append("updateBy", getUpdateBy())
                .append("status", getStatus())
                .append("remark", getRemark())
                .toString();
    }
}
