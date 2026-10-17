package com.basic.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.mapper.IWfExtendProcinstMapper;
import com.basic.workflow.service.IWfExtendProcinstService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description 流程启动实例扩展记录的业务实现，按流程实例维护发起时保存的业务扩展信息
 * @author yanbin
 * @date 2023/10/26
 */
@Service
public class IWfExtendProcinstServiceImpl
        extends ServiceImpl<IWfExtendProcinstMapper, ExtendProcinst>
        implements IWfExtendProcinstService {
    /**
     * @description 补全流程实例扩展记录的主键与创建、更新时间后按存在与否做保存或更新
     * @params [extendProcinst] 待保存的流程实例扩展记录
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return ExtendProcinst 该流程实例对应的扩展记录，查不到时返回 null
     * @description 按流程实例id查询流程启动时保存的业务扩展记录
     * @params [processInstanceId] 流程实例id
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @description 按流程实例id集合查出对应的扩展记录主键后批量删除，没有记录时不做操作
     * @params [procInsIds] 要清理的流程实例id集合
     * @author liguanwei
     * @date 2026/10/17 20:52
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
