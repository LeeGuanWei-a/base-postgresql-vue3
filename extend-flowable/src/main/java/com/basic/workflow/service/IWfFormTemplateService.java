package com.basic.workflow.service;


import com.baomidou.mybatisplus.spring.service.IService;
import com.basic.workflow.domain.WfFormTemplate;
import com.basic.workflow.domain.vo.WfFormTemplateVo;

import java.util.List;

/**
 * @description 流程表单模板服务接口，提供表单模板的增删改查与批量维护
 * @author liangjiayong
 * @date 2024-3-13 15:48
 */
public interface IWfFormTemplateService extends IService<WfFormTemplate> {
    /**
     * @return WfFormTemplateVo 表单模板视图对象，模板不存在时返回 null
     * @description 按主键查询流程表单模板并转换成视图对象
     * @params [formId] 表单模板主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfFormTemplateVo queryById(String formId);

    /**
     * @return List<WfFormTemplateVo> 表单模板视图对象列表
     * @description 按查询条件查询流程表单模板列表
     * @params [formTemplate] 表单模板查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfFormTemplateVo> queryPageList(WfFormTemplate formTemplate);

    /**
     * @return WfFormTemplate 保存后的表单模板对象
     * @description 保存流程表单模板并返回保存后的模板对象
     * @params [formTemplate] 待保存的表单模板
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfFormTemplate saveFormTemplate(WfFormTemplate formTemplate);

    /**
     * @return Boolean 是否删除成功
     * @description 按主键集合批量删除流程表单模板
     * @params [formIds] 需要删除的表单模板主键集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public Boolean delFormTemplate(List<String> formIds);
}
