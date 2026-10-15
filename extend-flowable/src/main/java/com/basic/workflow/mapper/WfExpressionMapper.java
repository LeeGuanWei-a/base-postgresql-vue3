package com.basic.workflow.mapper;

import com.basic.workflow.domain.WfExpression;

import java.util.List;


/**
 * @author yanbin
 * @description TODO (流程达式Mapper接口)
 * @date 2023/11/3 10:39
 */
public interface WfExpressionMapper {
    /**
     * @return com.basic.workflow.domain.WfExpression
     * @description TODO (查询流程表达式)
     * @params [id]
     * @author yanbin
     * @date 2024/1/23 10:59
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfExpression selectWfExpressionById(Long id);

    /**
     * @return java.util.List<com.basic.workflow.domain.WfExpression>
     * @description TODO (查询流程达式列表)
     * @params [wfExpression]
     * @author yanbin
     * @date 2024/1/23 10:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfExpression> selectWfExpressionList(WfExpression wfExpression);

    /**
     * @return int
     * @description TODO (新增流程达式)
     * @params [wfExpression]
     * @author yanbin
     * @date 2024/1/23 10:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int insertWfExpression(WfExpression wfExpression);

    /**
     * @return int
     * @description TODO (修改流程达式)
     * @params [WfExpression]
     * @author yanbin
     * @date 2024/1/23 10:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int updateWfExpression(WfExpression WfExpression);

    /**
     * @return int
     * @description TODO (删除流程达式)
     * @params [id] 流程达式主键
     * @author yanbin
     * @date 2024/1/23 10:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int deleteWfExpressionById(Long id);

    /**
     * @return int
     * @description TODO (批量删除流程达式)
     * @params [ids] 需要删除的数据主键集合
     * @author yanbin
     * @date 2024/1/23 10:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int deleteWfExpressionByIds(Long[] ids);
}
