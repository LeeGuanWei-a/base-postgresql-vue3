package com.basic.system.mapper;

import java.util.List;
import com.basic.system.domain.SysNotice;
import com.basic.system.domain.SysNoticeRead;

/**
 * 公告已读 数据层
 */
public interface SysNoticeReadMapper
{
    /**
     * 查询指定公告已读过的用户ID集合
     */
    public List<Long> selectReadUserIds(Long noticeId);

    /**
     * 查询指定用户已读的公告ID集合
     */
    public List<Long> selectReadNoticeIds(Long userId);

    /**
     * 标记已读（insert into sys_notice_read）
     */
    public int insertRead(SysNoticeRead record);

    /**
     * 批量标记已读
     */
    public int batchInsertRead(List<SysNoticeRead> records);

    /**
     * 根据公告ID删除已读记录（公告删除时级联清理）
     */
    public int deleteByNoticeId(Long noticeId);

    /**
     * 批量删除已读记录
     */
    public int deleteByNoticeIds(Long[] noticeIds);

    /**
     * 查询某条公告的已读用户列表（联合 sys_user 取 nick_name）
     * 返回 Map 列表（userId, userName, nickName, readTime），用 SysNoticeReadUser 接收
     */
    public List<java.util.Map<String, Object>> selectReadUsers(Long noticeId);
}
