package com.basic.workflow.mapper;


import com.basic.common.core.mapper.BaseMapperPlus;
import com.basic.workflow.domain.WfDeployForm;
import com.basic.workflow.domain.vo.WfDeployFormVo;
import org.apache.ibatis.annotations.Param;


/**
 * @author yanbin
 * @description TODO (流程实例关联表单Mapper接口)
 * @date 2023/11/3 10:39
 */
public interface WfDeployFormMapper extends BaseMapperPlus<WfDeployFormMapper, WfDeployForm, WfDeployFormVo> {
    /**
     * @return void
     * @description TODO (根据部署deployId删除流程部署表单信息)
     * @params [deployId]
     * @author yanbin
     * @date 2024/2/27 16:00
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteDeployFormForDeployId(@Param("deployId") String deployId);
}
