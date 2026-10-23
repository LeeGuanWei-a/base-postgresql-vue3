package com.basic.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.mapper.IWfExtendProcinstMapper;
import com.basic.workflow.service.IWfExtendProcinstService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description 娴佺▼鍚姩瀹炰緥鎵╁睍璁板綍鐨勪笟鍔″疄鐜帮紝鎸夋祦绋嬪疄渚嬬淮鎶ゅ彂璧锋椂淇濆瓨鐨勪笟鍔℃墿灞曚俊鎭?
 * @author yanbin
 * @date 2023/10/26
 */
@Service
public class IWfExtendProcinstServiceImpl
        extends ServiceImpl<IWfExtendProcinstMapper, ExtendProcinst>
        implements IWfExtendProcinstService {
    /**
     * @description 琛ュ叏娴佺▼瀹炰緥鎵╁睍璁板綍鐨勪富閿笌鍒涘缓銆佹洿鏂版椂闂村悗鎸夊瓨鍦ㄤ笌鍚﹀仛淇濆瓨鎴栨洿鏂?
     * @params [extendProcinst] 寰呬繚瀛樼殑娴佺▼瀹炰緥鎵╁睍璁板綍
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public void saveExtendProcinstAndHis(ExtendProcinst extendProcinst) {
        if (StringUtils.isBlank(extendProcinst.getId())) {
            extendProcinst.setId(IdWorker.get32UUID());
        }
        extendProcinst.setCreateTime(new Date());
        extendProcinst.setUpdateTime(new Date());
        this.saveOrUpdate(extendProcinst);
    }

    /**
     * @return ExtendProcinst 璇ユ祦绋嬪疄渚嬪搴旂殑鎵╁睍璁板綍锛屾煡涓嶅埌鏃惰繑鍥?null
     * @description 鎸夋祦绋嬪疄渚媔d鏌ヨ娴佺▼鍚姩鏃朵繚瀛樼殑涓氬姟鎵╁睍璁板綍
     * @params [processInstanceId] 娴佺▼瀹炰緥id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public ExtendProcinst selectProcInstanId(String processInstanceId) {
        QueryWrapper<ExtendProcinst> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("proc_ins_id", processInstanceId);
        ExtendProcinst extendProcinst = baseMapper.selectOne(queryWrapper);
        return extendProcinst;
    }

    /**
     * @description 鎸夋祦绋嬪疄渚媔d闆嗗悎鏌ュ嚭瀵瑰簲鐨勬墿灞曡褰曚富閿悗鎵归噺鍒犻櫎锛屾病鏈夎褰曟椂涓嶅仛鎿嶄綔
     * @params [procInsIds] 瑕佹竻鐞嗙殑娴佺▼瀹炰緥id闆嗗悎
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public void delProcessExtend(List<String> procInsIds) {
        List<String> ids = new ArrayList<>();
        for (String procInsId : procInsIds) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("proc_ins_id", procInsId);
            List<ExtendProcinst> extendProcinst = baseMapper.selectList(queryWrapper);
            if (extendProcinst.size() > 0) {
                extendProcinst.forEach(extend -> {
                    ids.add(extend.getId());
                });
            }
        }
        if (ids.size() > 0) {
            //鎵ц鍒犻櫎
            baseMapper.deleteBatchIds(ids);
        }
    }
}
