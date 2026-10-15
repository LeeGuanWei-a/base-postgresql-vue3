package com.basic.workflow.mapper;


import com.basic.common.core.mapper.BaseMapperPlus;
import com.basic.workflow.domain.WfCategory;
import com.basic.workflow.domain.vo.WfCategoryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (流程分类Mapper接口)
 * @date 2023/11/3 10:39
 */
public interface WfCategoryMapper extends BaseMapperPlus<WfCategoryMapper, WfCategory, WfCategoryVo> {
    /**
     * @description TODO (查询列表)
     * @params [wfCategory]
     * @return java.util.List<com.basic.workflow.domain.vo.WfCategoryVo>
     * @author yanbin
     * @date 2024/1/16 10:42
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCategoryVo> queryCategoryList(WfCategory wfCategory);
    /**
     * @description TODO (根据 code获取路由地址)
     * @params [code]
     * @return com.basic.workflow.domain.vo.WfCategoryVo
     * @author yanbin
     * @date 2024/1/16 10:42
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfCategoryVo getCategoryToUrl(String code);
    /**
     * @description TODO (修改流程分类)
     * @params [update]
     * @return int
     * @author yanbin
     * @date 2024/1/16 10:42
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    int updateCategory(WfCategory update);

    /**
     * @return com.basic.workflow.domain.WfCategory
     * @description TODO (超市/根据企业和申请类型查流程)
     * @params [orgId, productApply]
     * @author LGW
     * @date 2023/12/5 17:53
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfCategory selectWfCategoryByOrgIdAndCode(@Param("orgId") Long orgId, @Param("code") String code);
}
