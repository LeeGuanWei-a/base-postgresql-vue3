package com.basic.workflow.service;


import com.basic.workflow.domain.WfExpression;

import java.util.List;

/**
 * @description 流程表达式服务接口，提供流程条件表达式的查询与增删改操作
 * @author yanbin
 * @date 2023/11/3 10:37
 */
public interface IWfExpressionService {

    /**
     * @return List<WfExpression> 符合查询条件的流程表达式列表
     * @description 按查询条件筛选流程表达式列表
     * @params [wfExpression] 流程表达式查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfExpression> selectWfExpressionList(WfExpression wfExpression);

    /**
     * @return int 新增影响的行数
     * @description 新增一条流程表达式记录
     * @params [wfExpression] 待新增的流程表达式
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int insertWfExpression(WfExpression wfExpression);

    /**
     * @return int 更新影响的行数
     * @description 按主键更新流程表达式的字段
     * @params [wfExpression] 待更新的流程表达式
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int updateWfExpression(WfExpression wfExpression);

    /**
     * @return int 删除影响的行数
     * @description 按主键数组批量删除流程表达式
     * @params [ids] 待删除的流程表达式主键数组
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int deleteWfExpressionByIds(Long[] ids);

    /**
     * @return int 删除影响的行数
     * @description 按主键删除单条流程表达式
     * @params [id] 待删除的流程表达式主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public int deleteWfExpressionById(Long id);

    /**
     * @return WfExpression 该主键对应的流程表达式，不存在时为 null
     * @description 按主键查询流程表达式的详细信息
     * @params [id] 流程表达式主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfExpression selectWfExpressionById(Long id);
}
