package com.basic.workflow.service.impl;

import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.domain.vo.StartProcessInstanceVo;
import com.basic.workflow.domain.vo.WfModelVo;
import com.basic.workflow.service.IWfModelService;
import com.basic.workflow.service.wfModel.base.WfModelInfoServiceBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.flowable.engine.repository.Model;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (工作流流程模型管理Service实现类)
 * @date 2023/11/3 10:39
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class WfModelServiceImpl extends WfModelInfoServiceBase implements IWfModelService {
    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfModelVo>
     * @description TODO (查询流程模型列表) 分页
     * @params [modelBo, pageQuery]
     * @author yanbin
     * @date 2023/11/30 13:55
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public TableDataInfo<WfModelVo> list(WfModelBo modelBo, PageQuery pageQuery) {
        return wfModelQueryService.list(modelBo, pageQuery);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfModelVo>
     * @description TODO (流程启动列表)
     * @params [modelBo, pageQuery]
     * @author yanbin
     * @date 2023/11/10 10:12
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfModelVo> listModelLaunch(WfModelBo modelBo) {
        return wfQueryModelStartService.listModelLaunch(modelBo);
    }


    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfModelVo>
     * @description TODO (查询流程模型列表)不分页
     * @params [modelBo]
     * @author yanbin
     * @date 2023/11/30 13:55
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfModelVo> list(WfModelBo modelBo) {
        return wfModelQueryService.list(modelBo);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfModelVo>
     * @description TODO (查询流程模型列表)  历史模型
     * @params [modelBo, pageQuery]
     * @author yanbin
     * @date 2023/11/30 13:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public TableDataInfo<WfModelVo> historyList(WfModelBo modelBo, PageQuery pageQuery) {
        return wfModelQueryService.historyList(modelBo, pageQuery);
    }

    /**
     * @return com.basic.workflow.domain.vo.WfModelVo
     * @description TODO (获取流程模型详细信息)
     * @params [modelId]
     * @author yanbin
     * @date 2023/11/17 16:45
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfModelVo getModel(String modelId) throws IOException, DocumentException {
        return wfModelQueryService.getModel(modelId);
    }

    /**
     * @return java.lang.String
     * @description TODO (预览xml流程图)
     * @params [modelId]
     * @author yanbin
     * @date 2023/11/8 11:50
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public String queryBpmnXmlById(String modelId) throws IOException, DocumentException {
        return wfModelQueryXmlService.queryBpmnXmlById(modelId);
    }


    /**
     * @return java.lang.Object
     * @description TODO (新增模型信息)
     * @params [modelBo]
     * @author yanbin
     * @date 2023/11/30 13:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Model insertModel(WfModelBo modelBo) {
        return wfModelSaveService.insertModel(modelBo);
    }

    /**
     * @return java.lang.Object
     * @description TODO (修改模型信息)
     * @params [modelBo]
     * @author yanbin
     * @date 2023/11/30 13:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object updateModel(WfModelBo modelBo) {
        return wfModelSaveService.updateModel(modelBo);
    }

    /**
     * @return java.lang.Boolean
     * @description TODO (保存流程模型信息)
     * @params [modelBo]
     * @author yanbin
     * @date 2024/3/5 9:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveModel(WfModelBo modelBo) {
        return wfModelSaveXmlService.save(modelBo);
    }

    /**
     * @return java.lang.Object
     * @description TODO (设为最新流程模型)
     * @params [modelId]
     * @author yanbin
     * @date 2023/11/30 13:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object latestModel(String modelId) {
        return wfModelActivateService.latestModel(modelId);
    }

    /**
     * @return java.lang.Object
     * @description TODO (删除流程模型)
     * @params [ids]
     * @author yanbin
     * @date 2023/11/30 13:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object deleteByIds(Collection<String> ids) {
        return wfModelDeleteService.deleteByIds(ids);
    }

    /**
     * @return boolean
     * @description TODO (部署流程模型)
     * @params [wfModelBo]
     * @author yanbin
     * @date 2023/11/30 13:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deployModel(WfModelBo wfModelBo) throws JsonProcessingException {
        return wfModelDeployService.deployModel(wfModelBo);
    }


    /**
     * @return com.basic.flowable.tool.ReturnVo<org.flowable.engine.runtime.ProcessInstance>
     * @description TODO (流程启动/启动方法选择Id启动 使用repositoryService获取到流程定义对象，然后再获取到流程id)
     * @params [startProcessInstanceVo]
     * @author yanbin
     * @date 2023/10/25 15:36
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public AjaxResult startFormFlow(StartProcessInstanceVo startProcessInstanceVo) {
        return wfModelStartService.startFormFlow(startProcessInstanceVo);
    }


    /**
     * @return void
     * @description TODO (流程模型挂起(停用))
     * @params [wfModelBo]
     * @author DYH
     * @date 2023/11/9 10:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional
    public void suspendedProcessDefinition(WfModelBo wfModelBo) {
        wfModelActivateService.suspendedProcessDefinition(wfModelBo);
    }

    /**
     * @return void
     * @description TODO (激活流程模型)
     * @params [wfModelBo]
     * @author DYH
     * @date 2023/11/9 11:16
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional
    public void activateProcessDefinition(WfModelBo wfModelBo) {
        wfModelActivateService.activateProcessDefinition(wfModelBo);
    }

    /**
     * @return java.lang.String
     * @description TODO (工作流流程模型管理/根据流程分类code获取流程信息)
     * @params [code]
     * @author yanbin
     * @date 2023/12/27 15:32
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Map<String, Object> getCategoryToModel(String code) {
        Map<String, Object> result = new HashMap<>();
        Model model = repositoryService.createModelQuery().modelCategory(code).latestVersion().singleResult();
        if (model.getDeploymentId() == null) {
            return null;
        }
        result.put("modelKey", model.getKey());
        result.put("deployId", model.getDeploymentId());
        result.put("modelId", model.getId());
        return result;
    }

}
