package com.basic.workflow.mapper;


import com.basic.common.core.mapper.BaseMapperPlus;
import com.basic.workflow.domain.WfForm;
import com.basic.workflow.domain.bo.WfFormBo;
import com.basic.workflow.domain.vo.WfFormVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (流程表单Mapper接口)
 * @date 2023/11/3 10:39
 */
public interface WfFormMapper extends BaseMapperPlus<WfFormMapper, WfForm, WfFormVo> {

    /**
     * @return com.basic.workflow.domain.vo.WfFormVo
     * @description TODO (查询流程部署关联表单信息)
     * @params [deployId, activityType]
     * @author yanbin
     * @date 2024/2/27 10:08
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfFormVo selectFormVoList(@Param("deployId") String deployId, @Param("activityType") String activityType);

    /**
     * @return java.util.List<com.basic.workflow.domain.WfForm>
     * @description TODO (查询自定义表单信息)
     * @params query
     * @author yanbin
     * @date 2024/12/6 9:02
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfForm> selectFormList(WfFormVo query);

    List<WfFormVo> selectWfFormVoList(WfFormBo bo);
}
