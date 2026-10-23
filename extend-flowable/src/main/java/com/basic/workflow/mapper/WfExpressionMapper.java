package com.basic.workflow.mapper;

import com.basic.workflow.domain.WfExpression;

import java.util.List;


/**
 * @description 流程表达式 Mapper 接口，提供流程表达式的按主键查询、条件查询、新增、更新与删除
 * @author yanbin
 * @date 2023/11/3 10:39
 */
public interface WfExpressionMapper {
    /**
     * @return WfExpression 按主键查到的流程表达式，不存在时为 null
     * @description 按主键查询单条流程表达式
     * @params [id] 流程表达式主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfExpression selectWfExpressionById(Long id);

    /**
     * @return List<WfExpression> 符合条件的流程表达式列表
     * @description 按查询条件过滤查询流程表达式列表
     * @params [wfExpression] 流程表达式查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfExpression> selectWfExpressionList(WfExpression wfExpression);

    /**
     * @return int 新增成功的记录条数
     * @description 新增一条流程表达式
     * @params [wfExpression] 待新增的流程表达式
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int insertWfExpression(WfExpression wfExpression);

    /**
     * @return int 更新成功的记录条数
     * @description 按主键更新流程表达式
     * @params [WfExpression] 待更新的流程表达式，需携带主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int updateWfExpression(WfExpression WfExpression);

    /**
     * @return int 删除成功的记录条数
     * @description 按主键删除单条流程表达式
     * @params [id] 流程表达式主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int deleteWfExpressionById(Long id);

    /**
     * @return int 删除成功的记录条数
     * @description 按主键集合批量删除流程表达式
     * @params [ids] 需要删除的数据主键集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int deleteWfExpressionByIds(Long[] ids);
}
