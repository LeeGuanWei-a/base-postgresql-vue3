package com.basic.workflow.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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

    @Override
    public WfCategoryVo queryById(Long categoryId) {
        return baseMapper.selectVoById(categoryId);
    }

    @Override
    public WfCategoryVo queryByCode(String code) {
        WfCategory existing = baseMapper.selectOne(
                new LambdaQueryWrapper<WfCategory>().eq(WfCategory::getCode, code));
        return existing == null ? null : baseMapper.selectVoById(existing.getCategoryId());
    }

    @Override
    public List<WfCategoryVo> queryPageList(WfCategory wfCategory) {
        return queryList(wfCategory);
    }

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
     * 把扁平分类列表转成树形（顶级 + 递归 children）
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

    @Override
    public List<WfCategoryVo> queryList(WfCategory category) {
        LambdaQueryWrapper<WfCategory> wrapper = buildQueryWrapper(category);
        return baseMapper.selectVoList(wrapper);
    }

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

    @Override
    public void insertCategory(WfCategory category) {
        baseMapper.insert(category);
    }

    @Override
    public void updateCategory(WfCategory category) {
        baseMapper.updateById(category);
    }

    @Override
    public int deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return baseMapper.deleteBatchIds(ids);
    }

    @Override
    public boolean checkCategoryCodeUnique(WfCategory category) {
        Long categoryId = category.getCategoryId() == null ? -1L : category.getCategoryId();
        WfCategory existing = baseMapper.selectOne(
                new LambdaQueryWrapper<WfCategory>().eq(WfCategory::getCode, category.getCode()));
        return existing == null || existing.getCategoryId().equals(categoryId);
    }

    @Override
    public WfCategoryVo getCategoryToUrl(String code) {
        WfCategoryVo vo = queryByCode(code);
        return vo;
    }

    @Override
    public WfCategory selectWfCategoryByOrgIdAndCode(Long orgId, String productApply) {
        // process 业务定制 stub 化
        return baseMapper.selectOne(
                new LambdaQueryWrapper<WfCategory>().eq(WfCategory::getCode, productApply));
    }

    @Override
    public List<WfCategoryVo> selectCategoryOption(WfCategory wfCategory) {
        return queryList(wfCategory);
    }
}
