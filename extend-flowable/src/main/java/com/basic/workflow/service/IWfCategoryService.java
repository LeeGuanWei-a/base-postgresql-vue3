package com.basic.workflow.service;


import com.basic.workflow.domain.WfCategory;
import com.basic.workflow.domain.vo.WfCategoryVo;

import java.util.Collection;
import java.util.List;

/**
 * 流程分类 Service 接口
 *
 * @author yanbin
 * @date 2023/11/3
 * @description 已去除 process 业务定制内容
 */
public interface IWfCategoryService {

    WfCategoryVo queryById(Long categoryId);

    WfCategoryVo queryByCode(String code);

    /**
     * 查询所有符合条件的数据（不分级）
     */
    List<WfCategoryVo> queryPageList(WfCategory wfCategory);

    /**
     * 查询分类子类
     */
    List<WfCategoryVo> queryPageChildrenList(List<WfCategoryVo> parentList);

    /**
     * 递归拼装子项
     */
    void packagePageChildren(Long parentId, WfCategoryVo item, List<WfCategoryVo> childrenList);

    /**
     * 查询列表
     */
    List<WfCategoryVo> queryList(WfCategory category);

    /**
     * 新增流程分类
     */
    void insertCategory(WfCategory category);

    /**
     * 编辑流程分类
     */
    void updateCategory(WfCategory category);

    /**
     * 校验并删除数据
     */
    int deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 校验分类编码是否唯一
     */
    boolean checkCategoryCodeUnique(WfCategory category);

    /**
     * 根据 code 获取路由地址
     */
    WfCategoryVo getCategoryToUrl(String code);

    /**
     * 根据企业和申请类型查流程（process 业务定制，已 stub 化）
     */
    WfCategory selectWfCategoryByOrgIdAndCode(Long orgId, String productApply);

    /**
     * 查询分类流程信息
     */
    List<WfCategoryVo> selectCategoryOption(WfCategory wfCategory);

    /**
     * 把扁平分类列表转成树形（顶级 + 递归 children）
     */
    List<WfCategoryVo> buildCategoryTree(List<WfCategoryVo> all);
}
