package com.basic.system.service;

import java.util.List;
import java.util.Map;
import com.basic.system.domain.SysNotice;

/**
 * 公告已读 服务层
 */
public interface ISysNoticeReadService
{
    /**
     * 取最近 N 条公告（含当前用户的已读标记）
     */
    public List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit);

    /**
     * 单条标记已读（idempotent：已读过则忽略）
     */
    public void markRead(Long noticeId, Long userId);

    /**
     * 批量标记已读
     */
    public void markReadBatch(Long userId, Long[] noticeIds);

    /**
     * 查询某条公告的已读用户列表
     */
    public List<Map<String, Object>> selectReadUsersByNoticeId(Long noticeId, String searchValue);

    /**
     * 公告删除时级联清理已读记录
     */
    public void deleteByNoticeIds(Long[] noticeIds);
}
