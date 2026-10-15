package com.basic.workflow.service;


import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.domain.vo.StartProcessInstanceVo;
import com.basic.workflow.domain.vo.WfModelVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.dom4j.DocumentException;
import org.flowable.engine.repository.Model;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (工作流model管理Service)
 * @date 2023/11/3 10:37
 */
public interface IWfModelService {
    /**
     * @description TODO (查询流程模型列表)
     * @params [modelBo, pageQuery]
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfModelVo>
     * @author yanbin
     * @date 2024/1/23 11:21
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<WfModelVo> list(WfModelBo modelBo, PageQuery pageQuery);

    /**
     * @description TODO (查询流程模型列表)不分页
     * @params [modelBo]
     * @return java.util.List<com.basic.workflow.domain.vo.WfModelVo>
     * @author yanbin
     * @date 2024/1/23 11:22
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfModelVo> list(WfModelBo modelBo);


    /**
     * @description TODO (查询流程模型列表)
     * @params [modelBo, pageQuery]
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfModelVo>
     * @author yanbin
     * @date 2024/1/23 11:22
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<WfModelVo> historyList(WfModelBo modelBo, PageQuery pageQuery);

    /**
     * @description TODO (查询流程模型详情信息)
     * @params [modelId]
     * @return com.basic.workflow.domain.vo.WfModelVo
     * @author yanbin
     * @date 2024/1/23 11:22
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfModelVo getModel(String modelId) throws IOException, DocumentException;

    /**
     * @description TODO (查询流程表单详细信息)
     * @params [modelId]
     * @return java.lang.String
     * @author yanbin
     * @date 2024/1/23 11:22
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    String queryBpmnXmlById(String modelId) throws IOException, DocumentException;

    /**
     * @description TODO (新增模型信息)
     * @params [modelBo]
     * @return java.lang.Object
     * @author yanbin
     * @date 2024/1/23 11:23
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Model insertModel(WfModelBo modelBo);

    /**
     * @description TODO (修改模型信息)
     * @params [modelBo]
     * @return java.lang.Object
     * @author yanbin
     * @date 2024/1/23 11:23
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Object updateModel(WfModelBo modelBo);

    /**
     * @description TODO (保存流程模型信息)
     * @params [modelBo]
     * @return java.lang.Boolean
     * @author yanbin
     * @date 2024/3/5 9:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Boolean saveModel(WfModelBo modelBo);

    /**
     * @description TODO (设为最新流程模型)
     * @params [modelId]
     * @return java.lang.Object
     * @author yanbin
     * @date 2024/1/23 11:23
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Object latestModel(String modelId);

    /**
     * @description TODO (删除流程模型)
     * @params [ids]
     * @return java.lang.Object
     * @author yanbin
     * @date 2024/1/23 11:23
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Object deleteByIds(Collection<String> ids);

    /**
     * @description TODO (部署流程模型)
     * @params [modelId]
     * @return boolean
     * @author yanbin
     * @date 2024/1/23 11:24
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean deployModel(WfModelBo modelId) throws JsonProcessingException;
    /**
     * @description TODO (根据modelkey启动)
     * @params [startProcessInstanceVo]
     * @return com.basic.flowable.tool.ReturnVo<org.flowable.engine.runtime.ProcessInstance>
     * @author yanbin
     * @date 2024/1/23 11:24
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    AjaxResult startFormFlow(StartProcessInstanceVo startProcessInstanceVo);

    /**
     * @return void
     * @description TODO (流程模型挂起(停用))
     * @params [wfModelBo]
     * @author DYH
     * @date 2023/11/9 10:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void suspendedProcessDefinition(WfModelBo wfModelBo);

    /**
     * @return void
     * @description TODO (激活流程模型)
     * @params [wfModelBo]
     * @author DYH
     * @date 2023/11/9 11:15
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void activateProcessDefinition(WfModelBo wfModelBo);

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfModelVo>
     * @description TODO (查询流程启动列表)
     * @params [modelBo]
     * @author yanbin
     * @date 2023/12/27 15:32
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfModelVo> listModelLaunch(WfModelBo modelBo);

    /**
     * @return java.lang.String
     * @description TODO (工作流流程模型管理/根据流程分类code获取流程信息)
     * @params [code]
     * @author yanbin
     * @date 2023/12/27 15:32
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Map<String, Object> getCategoryToModel(String code);
}
