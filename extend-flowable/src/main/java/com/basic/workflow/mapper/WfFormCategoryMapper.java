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
     * @return List<WfFormCategory> 匹配查询条件的自定义单分类集合
     * @description 按查询条件筛选自定义单分类列表
     * @params [wfFormCategory] 自定义单分类查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfFormCategory> selectWfFormCategoryList(WfFormCategory wfFormCategory);
}
