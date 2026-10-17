package com.basic.workflow.mapper;


import com.basic.common.core.mapper.BaseMapperPlus;
import com.basic.workflow.domain.WfForm;
import com.basic.workflow.domain.bo.WfFormBo;
import com.basic.workflow.domain.vo.WfFormVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 流程表单 Mapper 接口，提供部署关联表单查询、自定义表单条件查询与表单视图列表查询
 * @author yanbin
 * @date 2023/11/3 10:39
 */
public interface WfFormMapper extends BaseMapperPlus<WfFormMapper, WfForm, WfFormVo> {

    /**
     * @return WfFormVo 流程部署关联的表单信息
     * @description 按部署 ID 与节点类型查询流程部署关联的表单
     * @params [deployId, activityType] 部署 ID；节点类型，用于区分不同节点绑定的表单
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfFormVo selectFormVoList(@Param("deployId") String deployId, @Param("activityType") String activityType);

    /**
     * @return List<WfForm> 满足查询条件的自定义表单列表
     * @description 按查询条件查询自定义表单信息
     * @params [query] 表单查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfForm> selectFormList(WfFormVo query);

    /**
     * @return List<WfFormVo> 满足业务条件的表单视图列表
     * @description 按业务查询条件查询表单视图列表
     * @params [bo] 表单业务查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfFormVo> selectWfFormVoList(WfFormBo bo);
}
