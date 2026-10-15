package com.basic.workflow.service;


import com.basic.workflow.domain.WfExpression;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (流程达式Service接口)
 * @date 2023/11/3 10:37
 */
public interface IWfExpressionService {
    /**
     * @description TODO (查询流程达式)
     * @params
     * @return
     * @author yanbin
     * @date 2024/1/23 11:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
//    public WfExpression selectWfExpressionById(Long id);

    /**
     * @return java.util.List<com.basic.workflow.domain.WfExpression>
     * @description TODO (查询流程表达式列表)
     * @params [wfExpression]
     * @author yanbin
     * @date 2024/1/23 11:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfExpression> selectWfExpressionList(WfExpression wfExpression);

    /**
     * @return int
     * @description TODO (新增流程达式)
     * @params [wfExpression]  流程表达式
     * @author yanbin
     * @date 2024/1/23 11:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int insertWfExpression(WfExpression wfExpression);

    /**
     * @return int
     * @description TODO (修改流程达式)
     * @params [wfExpression]
     * @author yanbin
     * @date 2024/1/23 11:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int updateWfExpression(WfExpression wfExpression);

    /**
     * @return int
     * @description TODO (批量删除流程达式)
     * @params [ids]
     * @author yanbin
     * @date 2024/1/23 11:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int deleteWfExpressionByIds(Long[] ids);

    /**
     * @return int
     * @description TODO (删除流程达式信息)
     * @params [id]
     * @author yanbin
     * @date 2024/1/23 11:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int deleteWfExpressionById(Long id);

    /**
     * @return com.basic.workflow.domain.WfExpression
     * @description TODO (获取流程达式详细信息)
     * @params [id]
     * @author yanbin
     * @date 2024/1/23 11:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfExpression selectWfExpressionById(Long id);
}
