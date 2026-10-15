package com.basic.workflow.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.workflow.domain.WfForm;
import com.basic.workflow.domain.bo.WfFormBo;
import com.basic.workflow.domain.vo.WfFormVo;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (工作流表单管理Service)
 * @date 2023/11/3 10:37
 */
public interface IWfFormService extends IService<WfForm> {
    /**
     * @return com.basic.workflow.domain.vo.WfFormVo
     * @description TODO (查询流程表单)
     * @params [formId]
     * @author yanbin
     * @date 2024/2/26 11:07
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfFormVo queryById(Long formId);

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfFormVo>
     * @description TODO (查询流程表单列表)
     * @params [bo, pageQuery]
     * @author yanbin
     * @date 2024/2/26 11:06
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfFormVo> queryPageList(WfFormBo bo);

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfFormVo>
     * @description TODO (查询流程表单列表)
     * @params [bo]
     * @author yanbin
     * @date 2024/2/26 11:06
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfFormVo> queryList(WfFormBo bo);


    /**
     * @return boolean
     * @description TODO (保存流程表单)
     * @params [bo]
     * @author yanbin
     * @date 2024/2/26 11:05
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    String saveForm(WfFormBo bo);

    /**
     * @return java.lang.Boolean
     * @description TODO (流程表单/删除表单内容)
     * @params [formIds]
     * @author yanbin
     * @date 2024/2/26 16:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Boolean delForm(List<String> formIds);

    /**
     * @return boolean
     * @description TODO (检验表单标识是否存在)
     * @params [code]
     * @author yanbin
     * @date 2024/3/12 15:54
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean checkFormCodeUnique(String code);

    /**
     * @return com.basic.workflow.domain.vo.WfFormVo
     * @description TODO (根据code标识获取表单内容)
     * @params [code]
     * @author yanbin
     * @date 2024/3/12 15:59
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfFormVo getFormByCode(String code);

    /***
     * @description TODO (根据条件筛选指定code的自定义表单组件字段，返回需要的selectOption格式(“label”,“value”))
     * @params wfFormVo
     * @return java.lang.Object
     * @author yanbin
     * @date 2024/12/6 8:55
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Object getVFormWidgetList(WfFormVo wfFormVo);
}
