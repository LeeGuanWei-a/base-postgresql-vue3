package com.basic.workflow.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.basic.common.exception.ServiceException;
import com.basic.common.utils.StringUtils;
import com.basic.workflow.domain.WfCategory;
import com.basic.workflow.domain.vo.WfCategoryVo;
import com.basic.workflow.mapper.WfCategoryMapper;
import com.basic.workflow.service.IWfCategoryService;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程分类 Service 业务层处理
 *
 * @author yanbin
 * @date 2023/11/3
 * @description 已去除 process 业务定制内容
 */
@RequiredArgsConstructor
@Service
public class WfCategoryServiceImpl extends WfProcessInfoBase implements IWfCategoryService {

    private final WfCategoryMapper baseMapper;

    /**
     * @return WfCategoryVo 分类视图对象，不存在时返回 null
     * @description 按分类主键查询流程分类详情
     * @params [categoryId] 流程分类主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfCategoryVo queryById(Long categoryId) {
        return baseMapper.selectVoById(categoryId);
    }

    /**
     * @return WfCategoryVo 分类视图对象，编码不存在时返回 null
     * @description 先按分类编码定位实体，再按实体主键查询分类视图详情
     * @params [code] 分类编码
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfCategoryVo queryByCode(String code) {
        WfCategory existing = baseMapper.selectOne(
                new LambdaQueryWrapper<WfCategory>().eq(WfCategory::getCode, code));
        return existing == null ? null : baseMapper.selectVoById(existing.getCategoryId());
    }

    /**
     * @return List<WfCategoryVo> 分类视图列表，无匹配数据时返回空列表
     * @description 按条件查询流程分类列表，直接复用列表查询的过滤逻辑，供分页接口使用
     * @params [wfCategory] 分类查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfCategoryVo> queryPageList(WfCategory wfCategory) {
        return queryList(wfCategory);
    }

    /**
     * @return List<WfCategoryVo> 所有父分类的子分类视图列表，父分类列表为空时返回空集合
     * @description 逐个父分类按 parent 字段查询其直接子分类，汇总后返回
     * @params [parentList] 父分类列表，为空时返回空集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfCategoryVo> queryPageChildrenList(List<WfCategoryVo> parentList) {
        if (parentList == null || parentList.isEmpty()) {
            return Collections.emptyList();
        }
        List<WfCategoryVo> result = new ArrayList<>();
        for (WfCategoryVo parent : parentList) {
            List<WfCategory> children = baseMapper.selectList(
                    new LambdaQueryWrapper<WfCategory>().eq(WfCategory::getParent, parent.getCategoryId()));
            for (WfCategory child : children) {
                WfCategoryVo childVo = baseMapper.selectVoById(child.getCategoryId());
                result.add(childVo);
            }
        }
        return result;
    }

    /**
     * @description 从候选子分类列表中筛出 parent 等于指定父分类主键的部分，挂到父分类视图对象的 children 上
     * @params [parentId, item, childrenList] 父分类主键；待填充 children 的父分类视图对象；候选子分类列表，为空时不做处理
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void packagePageChildren(Long parentId, WfCategoryVo item, List<WfCategoryVo> childrenList) {
        if (childrenList == null || item == null) {
            return;
        }
        List<WfCategoryVo> children = childrenList.stream()
                .filter(c -> parentId != null && parentId.equals(c.getParent()))
                .collect(Collectors.toList());
        item.setChildren(children);
    }

    /**
     * @return List<WfCategoryVo> 树形结构的顶级分类列表，入参为空时返回空列表
     * @description 把扁平分类列表转成树形结构，以 parent 为空或0的节点为顶级，再逐级递归填充 children
     * @params [all] 全部流程分类视图列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfCategoryVo> buildCategoryTree(List<WfCategoryVo> all) {
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }
        List<WfCategoryVo> roots = new ArrayList<>();
        for (WfCategoryVo vo : all) {
            Long parent = vo.getParent();
            if (parent == null || parent == 0L) {
                roots.add(vo);
            }
        }
        // 递归设置 children
        for (WfCategoryVo root : roots) {
            fillChildren(root, all);
        }
        return roots;
    }

    /**
     * @description 递归为分类节点填充下级 children，无子节点时不设置
     * @params [parent, all] 当前待填充子节点的父分类；全部流程分类视图列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void fillChildren(WfCategoryVo parent, List<WfCategoryVo> all) {
        List<WfCategoryVo> children = new ArrayList<>();
        for (WfCategoryVo vo : all) {
            if (parent.getCategoryId() != null && parent.getCategoryId().equals(vo.getParent())) {
                children.add(vo);
            }
        }
        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (WfCategoryVo child : children) {
                fillChildren(child, all);
            }
        }
    }

    /**
     * @return List<WfCategoryVo> 分类视图列表
     * @description 按条件查询流程分类列表，结果按分类主键升序
     * @params [category] 分类查询条件，传 null 时查全部
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfCategoryVo> queryList(WfCategory category) {
        LambdaQueryWrapper<WfCategory> wrapper = buildQueryWrapper(category);
        return baseMapper.selectVoList(wrapper);
    }

    /**
     * @return LambdaQueryWrapper<WfCategory> MyBatis-Plus 查询包装器
     * @description 构建分类列表查询条件，支持按主键、名称、编码过滤，并固定按主键升序
     * @params [category] 分类查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private LambdaQueryWrapper<WfCategory> buildQueryWrapper(WfCategory category) {
        LambdaQueryWrapper<WfCategory> wrapper = Wrappers.lambdaQuery();
        if (category == null) {
            return wrapper.orderByAsc(WfCategory::getCategoryId);
        }
        wrapper.eq(ObjectUtil.isNotNull(category.getCategoryId()), WfCategory::getCategoryId, category.getCategoryId());
        wrapper.like(StringUtils.isNotBlank(category.getCategoryName()), WfCategory::getCategoryName, category.getCategoryName());
        wrapper.like(StringUtils.isNotBlank(category.getCode()), WfCategory::getCode, category.getCode());
        wrapper.orderByAsc(WfCategory::getCategoryId);
        return wrapper;
    }

    /**
     * @description 新增流程分类，直接插入传入的分类实体
     * @params [category] 待新增的分类实体
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void insertCategory(WfCategory category) {
        baseMapper.insert(category);
    }

    /**
     * @description 按实体主键更新流程分类
     * @params [category] 待更新的分类实体，需带主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void updateCategory(WfCategory category) {
        baseMapper.updateById(category);
    }

    /**
     * @return int 实际删除（逻辑删除）的记录数
     * @description 批量删除流程分类：实体带 @TableLogic，实际是把 del_flag 置为 2 的逻辑删除；
     * isValid 为 true 时先校验是否存在下级分类，存在则拒绝删除，避免产生孤儿分类
     * @params [ids, isValid] 待删除的分类主键集合，为空时返回0；是否启用下级分类校验
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public int deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        if (Boolean.TRUE.equals(isValid)) {
            Long childCount = baseMapper.selectCount(
                    new LambdaQueryWrapper<WfCategory>().in(WfCategory::getParent, ids));
            if (childCount != null && childCount > 0) {
                throw new ServiceException("存在下级分类，不允许删除");
            }
        }
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * @return boolean 编码未被占用返回 true
     * @description 按编码查询是否已有分类，更新场景自动排除自身主键，判定编码唯一性
     * @params [category] 待校验的分类实体，新增时不带主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public boolean checkCategoryCodeUnique(WfCategory category) {
        Long categoryId = category.getCategoryId() == null ? -1L : category.getCategoryId();
        WfCategory existing = baseMapper.selectOne(
                new LambdaQueryWrapper<WfCategory>().eq(WfCategory::getCode, category.getCode()));
        return existing == null || existing.getCategoryId().equals(categoryId);
    }

    /**
     * @return WfCategoryVo 分类视图对象，编码不存在时返回 null
     * @description 按分类编码取分类详情，供调用方读取分类绑定的路由地址
     * @params [code] 分类编码
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfCategoryVo getCategoryToUrl(String code) {
        WfCategoryVo vo = queryByCode(code);
        return vo;
    }

    /**
     * @return WfCategory 匹配的分类实体，不存在时返回 null
     * @description 按编码查询流程分类，当前实现忽略机构 id，仅用编码过滤
     * @params [orgId, productApply] 机构 id，当前实现未参与过滤；分类编码
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfCategory selectWfCategoryByOrgIdAndCode(Long orgId, String productApply) {
        // process 业务定制 stub 化
        return baseMapper.selectOne(
                new LambdaQueryWrapper<WfCategory>().eq(WfCategory::getCode, productApply));
    }

    /**
     * @return List<WfCategoryVo> 分类视图列表
     * @description 查询分类下拉选项数据，复用列表查询的过滤条件
     * @params [wfCategory] 分类查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfCategoryVo> selectCategoryOption(WfCategory wfCategory) {
        return queryList(wfCategory);
    }
}
