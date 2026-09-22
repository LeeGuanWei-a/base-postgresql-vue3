package com.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.basic.common.annotation.Excel;
import com.basic.common.core.domain.YjyBaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 示例代码对象 sys_demo
 * 
 * @author basic
 * @date 2022-06-22
 */
public class Demo extends YjyBaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id",type = IdType.AUTO)
    private Long id;

    /** 名字 */
    @Excel(name = "名字")
    private String name;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .toString();
    }
}
