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

    /** 流程任务id（PR2 增补：用于工作流模块引用） */
    private String taskId;

    /** 流程实例id（PR2 增补） */
    private String processInstanceId;

    /** 用户id（PR2 增补） */
    private Long userId;

    /** 用户昵称（PR2 增补） */
    private String nickName;

    /** 流程活动节点id（PR2 增补） */
    private String activityId;

    /** 访问URL（PR2 增补：相对地址或绝对地址） */
    private String url;

    /** 上传方式（PR2 增补：LOCAL / OSS 等） */
    private String uploadType;

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

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getProcessInstanceId() { return processInstanceId; }
    public void setProcessInstanceId(String processInstanceId) { this.processInstanceId = processInstanceId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getUploadType() { return uploadType; }
    public void setUploadType(String uploadType) { this.uploadType = uploadType; }

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
