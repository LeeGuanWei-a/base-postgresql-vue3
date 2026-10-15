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
 * @author yanbin
 * @description TODO (启动实例记录(历史启动))--》流程业务拓展
 * @date 2023/10/26
 */
@Service
public class IWfExtendProcinstServiceImpl
        extends ServiceImpl<IWfExtendProcinstMapper, ExtendProcinst>
        implements IWfExtendProcinstService {
    /**
     * @return void
     * @description TODO (保存流程实例扩展表信息)
     * @params [extendProcinst]
     * @author yanbin
     * @date 2023/11/1 10:32
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @return com.basic.workflow.domain.ExtendProcinst
     * @description TODO (查询 实例启动时保存信息)
     * @params [processInstanceId]
     * @author yanbin
     * @date 2023/11/17 16:33
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public ExtendProcinst selectProcInstanId(String processInstanceId) {
        QueryWrapper<ExtendProcinst> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("proc_ins_id", processInstanceId);
        ExtendProcinst extendProcinst = baseMapper.selectOne(queryWrapper);
        return extendProcinst;
    }

    /**
     * @return void
     * @description TODO (删除历史流程业务拓展信息)
     * @params [procInsIds]
     * @author yanbin
     * @date 2024/1/19 15:48
     * @remark (改代码时写明修改时间修改人修改原因)
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
            //执行删除
            baseMapper.deleteBatchIds(ids);
        }
    }
}
