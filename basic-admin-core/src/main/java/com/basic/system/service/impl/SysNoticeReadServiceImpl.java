package com.basic.system.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.basic.system.domain.SysNotice;
import com.basic.system.domain.SysNoticeRead;
import com.basic.system.mapper.SysNoticeMapper;
import com.basic.system.mapper.SysNoticeReadMapper;
import com.basic.system.service.ISysNoticeReadService;

/**
 * 公告已读 服务层实现
 */
@Service
public class SysNoticeReadServiceImpl implements ISysNoticeReadService
{
    @Autowired
    private SysNoticeMapper noticeMapper;

    @Autowired
    private SysNoticeReadMapper noticeReadMapper;

    @Override
    public List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit)
    {
        SysNotice query = new SysNotice();
        query.setStatus("0");
        List<SysNotice> list = noticeMapper.selectNoticeList(query);
        if (list == null || list.isEmpty()) {
            return list;
        }
        // 取最近 limit 条
        if (list.size() > limit) {
            list = list.subList(0, limit);
        }
        // 取这些公告中当前用户已读的 ID
        Set<Long> readIds = new HashSet<>();
        if (userId != null) {
            List<Long> readNoticeIds = noticeReadMapper.selectReadNoticeIds(userId);
            if (readNoticeIds != null) {
                readIds.addAll(readNoticeIds);
            }
        }
        for (SysNotice n : list) {
            n.setIsRead(readIds.contains(n.getNoticeId()));
        }
        return list;
    }

    @Override
    public void markRead(Long noticeId, Long userId)
    {
        if (noticeId == null || userId == null) {
            return;
        }
        SysNoticeRead r = new SysNoticeRead();
        r.setNoticeId(noticeId);
        r.setUserId(userId);
        try {
            noticeReadMapper.insertRead(r);
        } catch (Exception ignore) {
            // 唯一约束冲突 → 已存在，幂等
        }
    }

    @Override
    public void markReadBatch(Long userId, Long[] noticeIds)
    {
        if (userId == null || noticeIds == null || noticeIds.length == 0) {
            return;
        }
        List<SysNoticeRead> batch = new ArrayList<>(noticeIds.length);
        for (Long nid : noticeIds) {
            SysNoticeRead r = new SysNoticeRead();
            r.setNoticeId(nid);
            r.setUserId(userId);
            batch.add(r);
        }
        noticeReadMapper.batchInsertRead(batch);
    }

    @Override
    public List<Map<String, Object>> selectReadUsersByNoticeId(Long noticeId, String searchValue)
    {
        List<Map<String, Object>> all = noticeReadMapper.selectReadUsers(noticeId);
        if (all == null || searchValue == null || searchValue.isEmpty()) {
            return all;
        }
        String lower = searchValue.toLowerCase();
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> m : all) {
            Object un = m.get("userName");
            Object nn = m.get("nickName");
            boolean match = (un != null && un.toString().toLowerCase().contains(lower))
                         || (nn != null && nn.toString().toLowerCase().contains(lower));
            if (match) filtered.add(m);
        }
        return filtered;
    }

    @Override
    public void deleteByNoticeIds(Long[] noticeIds)
    {
        if (noticeIds == null || noticeIds.length == 0) {
            return;
        }
        noticeReadMapper.deleteByNoticeIds(noticeIds);
    }
}
