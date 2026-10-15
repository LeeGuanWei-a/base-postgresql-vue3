package com.basic.workflow.service.impl;

import com.basic.common.utils.DateUtils;
import com.basic.workflow.domain.WfExpression;
import com.basic.workflow.mapper.WfExpressionMapper;
import com.basic.workflow.service.IWfExpressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author yanbin
 * @description TODO (流程表达式Service业务层处理)
 * @date 2023/11/3 10:39
 */
@Service
public class WfExpressionServiceImpl implements IWfExpressionService {
    @Autowired
    private WfExpressionMapper wfExpressionMapper;

    /**
     * @return com.basic.workflow.domain.WfExpression
     * @description TODO (查询流程表达式)
     * @params [id]
     * @author yanbin
     * @date 2024/1/23 11:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfExpression selectWfExpressionById(Long id) {
        return wfExpressionMapper.selectWfExpressionById(id);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.WfExpression>
     * @description TODO (查询流程表达式列表)
     * @params [wfExpression]
     * @author yanbin
     * @date 2024/1/23 11:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfExpression> selectWfExpressionList(WfExpression wfExpression) {
        return wfExpressionMapper.selectWfExpressionList(wfExpression);
    }

    /**
     * @return int
     * @description TODO (新增流程表达式)
     * @params [wfExpression]
     * @author yanbin
     * @date 2024/1/23 11:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public int insertWfExpression(WfExpression wfExpression) {
        wfExpression.setCreateTime(DateUtils.getNowDate());
        return wfExpressionMapper.insertWfExpression(wfExpression);
    }

    /**
     * @description TODO (修改流程表达式)
     * @params [wfExpression]
     * @return int
     * @author yanbin
     * @date 2024/1/23 11:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public int updateWfExpression(WfExpression wfExpression) {
        wfExpression.setUpdateTime(DateUtils.getNowDate());
        return wfExpressionMapper.updateWfExpression(wfExpression);
    }

    /**
     * @description TODO (批量删除流程表达式)
     * @params [ids]
     * @return int
     * @author yanbin
     * @date 2024/1/23 11:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public int deleteWfExpressionByIds(Long[] ids) {
        return wfExpressionMapper.deleteWfExpressionByIds(ids);
    }
    /**
     * @description TODO (删除流程表达式信息)
     * @params [id]
     * @return int
     * @author yanbin
     * @date 2024/1/23 11:48
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public int deleteWfExpressionById(Long id) {
        return wfExpressionMapper.deleteWfExpressionById(id);
    }
}
