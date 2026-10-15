package com.basic.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 基本实体类
 * 从 process 同步，extend-flowable 的 ExtendProcinst 等实体继承此类
 *
 * @author yanbin
 * @date 2023-10-26
 * @description 原版使用 @Data，已改为手写 getter/setter 以避免 basic-common 引入 lombok 依赖
 */
public abstract class BaseModel {

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;

    /** 创建人（库中列名为 create_by，与 RuoYi 规范及其余 mapper 保持一致） */
    @TableField("create_by")
    protected String creator;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;

    /** 更新人（库中列名为 update_by） */
    @TableField("update_by")
    protected String updator;

    /** 删除标识 0 已删除 1 未删除 */
    protected Integer delFlag = 1;

    /** 查询关键字 临时变量 */
    @TableField(exist = false)
    protected String keyword;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
