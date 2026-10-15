package com.basic.workflow.mapper;


import com.basic.common.core.mapper.BaseMapperPlus;
import com.basic.workflow.domain.WfForm;
import com.basic.workflow.domain.WfFormTemplate;
import com.basic.workflow.domain.vo.WfFormTemplateVo;
import com.basic.workflow.domain.vo.WfFormVo;

import java.util.List;

/**
 * @author liangjiayong
 * @description TODO (流程表单模板Mapper接口)
 * @date 2024-3-13 15:48
 */
public interface WfFormTemplateMapper extends BaseMapperPlus<WfFormTemplateMapper, WfFormTemplate, WfFormTemplateVo> {

    /***
     * @description TODO (查询表单模板列表)
     * @params [formTemplate]
     * @return java.util.List<com.basic.workflow.domain.vo.WfFormTemplateVo>
     * @author LiangJiaYong
     * @date 2024-3-13 15:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfFormTemplateVo> selectFormTemplateVoList(WfFormTemplate formTemplate);
}
