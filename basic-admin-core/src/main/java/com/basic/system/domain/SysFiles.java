package com.basic.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.basic.common.annotation.Excel;
import com.basic.common.core.domain.BaseEntity;

/**
 * 附件对象 sys_files
 *
 * @author basic
 * @date 2022-08-30
 */
public class SysFiles extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 文件名 */
    @Excel(name = "文件名")
    private String name;

    /** 文件绝对路径 */
    @Excel(name = "文件绝对路径")
    private String absolutePath;

    /** 文件相对路径 */
    @Excel(name = "文件相对路径")
    private String relativePath;

    /** 文件大小  单位：字节 */
    @Excel(name = "文件大小")
    private Long size;

    /** 文件所属业务实体 */
    @Excel(name = "文件所属业务实体")
    private String identity;

    /** 删除标识 */
    @Excel(name = "删除标识")
    private String isDel="0";

    /** 备注 */
    private String remark;

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
    public void setAbsolutePath(String absolutePath)
    {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath()
    {
        return absolutePath;
    }
    public void setRelativePath(String relativePath)
    {
        this.relativePath = relativePath;
    }

    public String getRelativePath()
    {
        return relativePath;
    }
    public void setSize(Long size)
    {
        this.size = size;
    }

    public Long getSize()
    {
        return size;
    }
    public void setIdentity(String identity)
    {
        this.identity = identity;
    }

    public String getIdentity()
    {
        return identity;
    }
    public void setIsDel(String isDel)
    {
        this.isDel = isDel;
    }

    public String getIsDel()
    {
        return isDel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("absolutePath", getAbsolutePath())
                .append("relativePath", getRelativePath())
                .append("size", getSize())
                .append("identity", getIdentity())
                .append("remark", getRemark())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .append("isDel", getIsDel())
                .toString();
    }
}
