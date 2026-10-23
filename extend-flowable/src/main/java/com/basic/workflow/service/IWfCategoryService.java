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

    /**
     * @return WfCategoryVo 分类详情，查不到时返回 null
     * @description 按分类主键查询流程分类详情
     * @params [categoryId] 流程分类主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfCategoryVo queryById(Long categoryId);

    /**
     * @return WfCategoryVo 分类详情，查不到时返回 null
     * @description 按分类编码查询流程分类详情
     * @params [code] 流程分类编码
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfCategoryVo queryByCode(String code);

    /**
     * @return List<WfCategoryVo> 符合条件且不分级的流程分类列表
     * @description 按传入条件查询流程分类列表，结果保持扁平不再分级
     * @params [wfCategory] 流程分类查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCategoryVo> queryPageList(WfCategory wfCategory);

    /**
     * @return List<WfCategoryVo> 补齐了子分类的父级分类列表
     * @description 为传入的父级分类逐个装配其下级分类
     * @params [parentList] 待装配子分类的父级分类列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCategoryVo> queryPageChildrenList(List<WfCategoryVo> parentList);

    /**
     * @description 以父级分类为根递归查询并拼装其下所有子孙分类，结果写回父级分类的子项列表
     * @params [parentId, item, childrenList] parentId 父级分类主键；item 当前处理的分类节点；childrenList 装配结果的分类集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void packagePageChildren(Long parentId, WfCategoryVo item, List<WfCategoryVo> childrenList);

    /**
     * @return List<WfCategoryVo> 符合条件的流程分类列表
     * @description 按条件查询流程分类列表，不分页
     * @params [category] 流程分类查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCategoryVo> queryList(WfCategory category);

    /**
     * @description 新增流程分类，写入前校验分类编码是否重复
     * @params [category] 待新增的流程分类
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void insertCategory(WfCategory category);

    /**
     * @description 更新流程分类，写入前校验分类编码是否与其他记录重复
     * @params [category] 待更新的流程分类
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void updateCategory(WfCategory category);

    /**
     * @return int 实际删除的分类数量
     * @description 校验分类是否已被流程或子分类占用后批量删除，不合法时拒绝删除
     * @params [ids, isValid] ids 待删除的分类主键集合；isValid 是否执行占用校验，为 false 时跳过校验
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    int deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * @return boolean 分类编码唯一时返回 true
     * @description 校验流程分类编码在库中是否唯一，更新时排除自身
     * @params [category] 待校验的流程分类
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean checkCategoryCodeUnique(WfCategory category);

    /**
     * @return WfCategoryVo 分类信息，含拼装好的路由地址
     * @description 按分类编码取出分类详情并转换为前端可直接使用的路由地址
     * @params [code] 流程分类编码
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfCategoryVo getCategoryToUrl(String code);

    /**
     * @return WfCategory 匹配到的流程分类，查不到时返回 null
     * @description 按企业与申请类型查询流程分类，企业业务定制部分已桩化处理
     * @params [orgId, productApply] orgId 企业id；productApply 申请类型编码
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfCategory selectWfCategoryByOrgIdAndCode(Long orgId, String productApply);

    /**
     * @return List<WfCategoryVo> 供下拉选择使用的流程分类列表
     * @description 按条件查询流程分类，输出前端下拉选项需要的分类信息
     * @params [wfCategory] 流程分类查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCategoryVo> selectCategoryOption(WfCategory wfCategory);

    /**
     * @return List<WfCategoryVo> 组装完成的顶级分类列表，子分类挂在 children 下
     * @description 把扁平分类列表按父子关系组装成树，只返回顶级节点并递归填充子节点
     * @params [all] 待组装的扁平分类列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCategoryVo> buildCategoryTree(List<WfCategoryVo> all);
}
