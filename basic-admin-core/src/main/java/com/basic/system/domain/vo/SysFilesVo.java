package com.basic.system.domain.vo;

import java.util.Date;

/**
 * 返回前端SysFile文件上传组件所需数据格式
 *
 * @author DYH
 * @date 2023/12/21
 */
public class SysFilesVo {

    private Long id;
    private String url;
    private String name;
    private String picUrl;
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
