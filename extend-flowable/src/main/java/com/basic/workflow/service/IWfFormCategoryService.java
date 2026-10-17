package com.basic.workflow.service;

import java.util.List;
import com.baomidou.mybatisplus.spring.service.IService;
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
     * @return List<WfFormCategory> 自定义单分类集合
     * @description 按查询条件查询自定义单分类列表
     * @params [wfFormCategory] 自定义单分类查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfFormCategory> selectWfFormCategoryList(WfFormCategory wfFormCategory);

    /**
     * @return List<WfFormCategory> 子级自定义单分类集合
     * @description 按查询条件查询自定义单分类的子级列表
     * @params [params] 自定义单分类查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfFormCategory> selectWfFormCategoryChildrenList(WfFormCategory params);

    /**
     * @description 新增一条自定义单分类
     * @params [wfFormCategory] 待新增的自定义单分类
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void insertWfFormCategory(WfFormCategory wfFormCategory);

    /**
     * @description 按主键修改一条自定义单分类
     * @params [wfFormCategory] 待修改的自定义单分类
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void updateWfFormCategory(WfFormCategory wfFormCategory);

    /**
     * @return boolean 是否删除成功
     * @description 按主键集合批量删除自定义单分类
     * @params [ids] 需要删除的自定义单分类主键集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean deleteWfFormCategoryByIds(List<String> ids);
}
