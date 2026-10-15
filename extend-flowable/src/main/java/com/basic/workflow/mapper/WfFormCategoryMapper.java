package com.basic.workflow.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basic.workflow.domain.WfFormCategory;

/**
 * 自定义单分类Mapper接口
 *
 * @author basic
 * @date 2024-03-27
 */
public interface WfFormCategoryMapper extends BaseMapper<WfFormCategory>
{

    /**
     * 查询自定义单分类列表
     *
     * @param wfFormCategory 自定义单分类
     * @return 自定义单分类集合
     */
    public List<WfFormCategory> selectWfFormCategoryList(WfFormCategory wfFormCategory);
}
