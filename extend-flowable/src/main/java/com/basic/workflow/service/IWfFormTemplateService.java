package com.basic.workflow.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.workflow.domain.WfFormTemplate;
import com.basic.workflow.domain.vo.WfFormTemplateVo;

import java.util.List;

/**
 * @author liangjiayong
 * @description TODO (工作流表单模板管理Service)
 * @date 2024-3-13 15:48
 */
public interface IWfFormTemplateService extends IService<WfFormTemplate> {
    /***
     * @description TODO (查询流程表单模板)
     * @params [formId]
     * @return com.basic.workflow.domain.vo.WfFormTemplateVo
     * @author LiangJiaYong
     * @date 2024-3-13 16:00
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfFormTemplateVo queryById(Long formId);

    /***
     * @description TODO (查询流程表单模板列表)
     * @params [formTemplate]
     * @return java.util.List<com.basic.workflow.domain.vo.WfFormTemplateVo>
     * @author LiangJiaYong
     * @date 2024-3-13 15:54
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfFormTemplateVo> queryPageList(WfFormTemplate formTemplate);

    /***
     * @description TODO (保存流程表单模板)
     * @params [formTemplate]
     * @return void
     * @author LiangJiaYong
     * @date 2024-3-13 16:06
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfFormTemplate saveFormTemplate(WfFormTemplate formTemplate);

    /***
     * @description TODO (流程表单/删除表单模板)
     * @params [formIds]
     * @return java.lang.Boolean
     * @author LiangJiaYong
     * @date 2024-3-13 16:09
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public Boolean delFormTemplate(List<String> formIds);
}
