package com.basic.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
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
     * @return List<WfFormCategory> 带子分类的顶级分类列表
     * @description 查询自定义表单分类树，先取顶级分类再逐个填充其子分类
     * @params [wfFormCategory] 自定义表单分类查询对象，当前实现未参与过滤条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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

    /**
     * @return List<WfFormCategory> 带子分类的分类列表，无下级时 children 为空
     * @description 按父分类 ID 递归查询子分类并逐级填充 children
     * @params [params] 携带父分类 ID 的查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @description 新增自定义表单分类，直接保存传入的分类对象
     * @params [wfFormCategory] 待新增的自定义表单分类
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void insertWfFormCategory(WfFormCategory wfFormCategory)
    {
        this.save(wfFormCategory);
    }

    /**
     * @description 修改自定义表单分类，主键已存在则更新、不存在则新增
     * @params [wfFormCategory] 待修改的自定义表单分类
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void updateWfFormCategory(WfFormCategory wfFormCategory)
    {
        this.saveOrUpdate(wfFormCategory);
    }

    /**
     * @return boolean 执行完成后固定返回 true
     * @description 按主键集合逐个删除自定义表单分类，集合为空时不做任何删除
     * @params [ids] 需要删除的自定义表单分类主键集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public boolean deleteWfFormCategoryByIds(List<String> ids)
    {
        if (CollUtil.isNotEmpty(ids)) {
            for (String id : ids) {
                baseMapper.deleteById(id);
            }
        }
        return true;
    }
}
