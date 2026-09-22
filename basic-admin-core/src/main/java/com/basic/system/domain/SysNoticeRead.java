package com.basic.system.domain;

import java.util.Date;
import com.basic.common.core.domain.BaseEntity;

/**
 * 公告已读记录 sys_notice_read
 *
 * 用于 RuoYi-Vue3 首页 HeaderNotice（listTop/markRead/markReadAll）。
 */
public class SysNoticeRead extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long readId;

    /** 公告ID */
    private Long noticeId;

    /** 用户ID */
    private Long userId;

    /** 阅读时间 */
    private Date readTime;

    public Long getReadId() {
        return readId;
    }

    public void setReadId(Long readId) {
        this.readId = readId;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }
}
