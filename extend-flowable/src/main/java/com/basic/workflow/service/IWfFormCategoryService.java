package com.basic.workflow.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.workflow.domain.WfFormCategory;

/**
 * 自定义单分类Service接口
 *
 * @author basic
 * @date 2024-03-27
 */
public interface IWfFormCategoryService extends IService<WfFormCategory>
{

    /**
     * 查询自定义单分类列表
     *
     * @param wfFormCategory 自定义单分类
     * @return 自定义单分类集合
     */
    public List<WfFormCategory> selectWfFormCategoryList(WfFormCategory wfFormCategory);

    /***
     * @description TODO (查询自定义单分类列表-子类)
     * @params [params]
     * @return java.util.List<com.basic.workflow.domain.WfFormCategory>
     * @author LiangJiaYong
     * @date 2024-3-28 8:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfFormCategory> selectWfFormCategoryChildrenList(WfFormCategory params);

    /**
     * 新增自定义单分类
     *
     * @param wfFormCategory 自定义单分类
     * @return 结果
     */
    public void insertWfFormCategory(WfFormCategory wfFormCategory);

    /**
     * 修改自定义单分类
     *
     * @param wfFormCategory 自定义单分类
     * @return 结果
     */
    public void updateWfFormCategory(WfFormCategory wfFormCategory);

    /**
     * 批量删除自定义单分类
     *
     * @param ids 需要删除的自定义单分类主键集合
     * @return 结果
     */
    public boolean deleteWfFormCategoryByIds(List<String> ids);
}
