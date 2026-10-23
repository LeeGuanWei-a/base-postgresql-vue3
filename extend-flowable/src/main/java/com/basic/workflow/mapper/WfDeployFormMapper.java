package com.basic.workflow.mapper;


import com.basic.common.core.mapper.BaseMapperPlus;
import com.basic.workflow.domain.WfDeployForm;
import com.basic.workflow.domain.vo.WfDeployFormVo;
import org.apache.ibatis.annotations.Param;


/**
 * @description 流程部署与表单关联 Mapper，维护 wf_deploy_form 中部署与表单的映射记录
 * @author yanbin
 * @date 2023/11/3 10:39
 */
public interface WfDeployFormMapper extends BaseMapperPlus<WfDeployFormMapper, WfDeployForm, WfDeployFormVo> {
    /**
     * @description 按部署 id 删除 wf_deploy_form 中该次部署绑定的全部流程表单关联记录，流程删部署时一并清掉表单映射
     * @params [deployId] 流程部署 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteDeployFormForDeployId(@Param("deployId") String deployId);
}
