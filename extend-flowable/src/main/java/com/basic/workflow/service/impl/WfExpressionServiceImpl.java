package com.basic.workflow.service.impl;

import com.basic.common.utils.DateUtils;
import com.basic.workflow.domain.WfExpression;
import com.basic.workflow.mapper.WfExpressionMapper;
import com.basic.workflow.service.IWfExpressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @description 流程表达式服务实现，负责表达式的增删改查与导出所需的数据组装
 * @author yanbin
 * @date 2023/11/3 10:39
 */
@Service
public class WfExpressionServiceImpl implements IWfExpressionService {
    @Autowired
    private WfExpressionMapper wfExpressionMapper;

    /**
     * @return WfExpression 流程表达式实体，不存在时返回 null
     * @description 按主键查询单条流程表达式
     * @params [id] 表达式主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfExpression selectWfExpressionById(Long id) {
        return wfExpressionMapper.selectWfExpressionById(id);
    }

    /**
     * @return List<WfExpression> 匹配查询条件的流程表达式列表
     * @description 按查询条件查询流程表达式列表
     * @params [wfExpression] 流程表达式查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfExpression> selectWfExpressionList(WfExpression wfExpression) {
        return wfExpressionMapper.selectWfExpressionList(wfExpression);
    }

    /**
     * @return int 插入成功的记录数
     * @description 补上创建时间后新增一条流程表达式
     * @params [wfExpression] 待新增的流程表达式实体
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public int insertWfExpression(WfExpression wfExpression) {
        wfExpression.setCreateTime(DateUtils.getNowDate());
        return wfExpressionMapper.insertWfExpression(wfExpression);
    }

    /**
     * @return int 更新成功的记录数
     * @description 补上更新时间后按主键修改流程表达式
     * @params [wfExpression] 待修改的流程表达式实体
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public int updateWfExpression(WfExpression wfExpression) {
        wfExpression.setUpdateTime(DateUtils.getNowDate());
        return wfExpressionMapper.updateWfExpression(wfExpression);
    }

    /**
     * @return int 删除成功的记录数
     * @description 按主键数组批量删除流程表达式
     * @params [ids] 待删除的表达式主键数组
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public int deleteWfExpressionByIds(Long[] ids) {
        return wfExpressionMapper.deleteWfExpressionByIds(ids);
    }
    /**
     * @return int 删除成功的记录数
     * @description 按主键删除单条流程表达式
     * @params [id] 待删除的表达式主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public int deleteWfExpressionById(Long id) {
        return wfExpressionMapper.deleteWfExpressionById(id);
    }
}
