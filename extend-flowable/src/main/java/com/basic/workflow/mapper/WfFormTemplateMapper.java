package com.basic.workflow.mapper;


import com.basic.common.core.mapper.BaseMapperPlus;
import com.basic.workflow.domain.WfForm;
import com.basic.workflow.domain.WfFormTemplate;
import com.basic.workflow.domain.vo.WfFormTemplateVo;
import com.basic.workflow.domain.vo.WfFormVo;

import java.util.List;

/**
 * @description 流程表单模板的数据访问接口，提供按条件查询表单模板列表等方法
 * @author liangjiayong
 * @date 2024-3-13 15:48
 */
public interface WfFormTemplateMapper extends BaseMapperPlus<WfFormTemplateMapper, WfFormTemplate, WfFormTemplateVo> {

    /**
     * @return List<WfFormTemplateVo> 匹配条件的表单模板集合
     * @description 按条件查询流程表单模板列表
     * @params [formTemplate] 表单模板查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfFormTemplateVo> selectFormTemplateVoList(WfFormTemplate formTemplate);
}
