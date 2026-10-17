package com.basic.workflow.mapper;


import com.basic.common.core.mapper.BaseMapperPlus;
import com.basic.workflow.domain.WfCategory;
import com.basic.workflow.domain.vo.WfCategoryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 流程分类的数据访问接口，提供分类列表查询、按编码取路由地址以及按主键更新等方法
 * @author yanbin
 * @date 2023/11/3 10:39
 */
public interface WfCategoryMapper extends BaseMapperPlus<WfCategoryMapper, WfCategory, WfCategoryVo> {
    /**
     * @return List<WfCategoryVo> 匹配查询条件的流程分类列表
     * @description 按流程分类查询条件筛选流程分类列表
     * @params [wfCategory] 流程分类查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCategoryVo> queryCategoryList(WfCategory wfCategory);
    /**
     * @return WfCategoryVo 该编码对应流程分类的路由地址信息，未匹配到时为 null
     * @description 按流程分类编码查询其路由地址信息
     * @params [code] 流程分类编码
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfCategoryVo getCategoryToUrl(String code);
    /**
     * @return int 更新影响的行数
     * @description 按主键更新流程分类的可变字段
     * @params [update] 待更新的流程分类数据
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    int updateCategory(WfCategory update);

    /**
     * @return WfCategory 该企业与申请类型对应的流程分类，未配置时为 null
     * @description 按企业 ID 与流程编码查询该企业配置的流程分类
     * @params [orgId, code] 企业 ID；流程编码（申请类型）
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfCategory selectWfCategoryByOrgIdAndCode(@Param("orgId") Long orgId, @Param("code") String code);
}
