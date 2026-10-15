package com.basic.workflow.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basic.workflow.domain.WfFormCategory;
import com.basic.workflow.mapper.WfFormCategoryMapper;
import com.basic.workflow.service.IWfFormCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自定义单分类Service业务层处理
 *
 * @author basic
 * @date 2024-03-27
 */
@RequiredArgsConstructor
@Service
public class WfFormCategoryServiceImpl extends ServiceImpl<WfFormCategoryMapper, WfFormCategory> implements IWfFormCategoryService
{
    private final WfFormCategoryMapper baseMapper;

    /**
     * 查询自定义单分类列表
     *
     * @param wfFormCategory 自定义单分类
     * @return 自定义单分类
     */
    @Override
    public List<WfFormCategory> selectWfFormCategoryList(WfFormCategory wfFormCategory)
    {
        WfFormCategory params = new WfFormCategory();
        params.setParent("0");
        List<WfFormCategory> list = baseMapper.selectWfFormCategoryList(params);
        if(list.size()>0){
            for(WfFormCategory item : list){
                params.setParent(item.getId());
                List<WfFormCategory> children = selectWfFormCategoryChildrenList(params);
                item.setChildren(children);
            }
        }
        return list;
    }

    /***
     * @description TODO (查询自定义单分类列表-子类)
     * @params [params]
     * @return java.util.List<com.basic.workflow.domain.WfFormCategory>
     * @author LiangJiaYong
     * @date 2024-3-28 8:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfFormCategory> selectWfFormCategoryChildrenList(WfFormCategory params){
        List<WfFormCategory> list = baseMapper.selectWfFormCategoryList(params);
        if(list.size()>0){
            for(WfFormCategory item : list){
                WfFormCategory param = new WfFormCategory();
                param.setParent(item.getId());
                List<WfFormCategory> children = selectWfFormCategoryChildrenList(param);
                item.setChildren(children);
            }
        }
        return list;
    }

    /**
     * 新增自定义单分类
     *
     * @param wfFormCategory 自定义单分类
     * @return 结果
     */
    @Override
    public void insertWfFormCategory(WfFormCategory wfFormCategory)
    {
        this.save(wfFormCategory);
    }

    /**
     * 修改自定义单分类
     *
     * @param wfFormCategory 自定义单分类
     * @return 结果
     */
    @Override
    public void updateWfFormCategory(WfFormCategory wfFormCategory)
    {
        this.saveOrUpdate(wfFormCategory);
    }

    /**
     * 批量删除自定义单分类
     *
     * @param ids 需要删除的自定义单分类主键
     * @return 结果
     */
    @Override
    public boolean deleteWfFormCategoryByIds(List<String> ids)
    {
        if (CollectionUtils.isNotEmpty(ids)) {
            for (String id : ids) {
                baseMapper.deleteById(id);
            }
        }
        return true;
    }
}
