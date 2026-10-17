package com.basic.workflow.service.wfModel.deploy;

import cn.hutool.core.util.ObjectUtil;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description 流程模型版本与状态服务，支持设为最新版本、激活与停用已部署流程
 * @author yanbin
 * @date 2024/03/05
 */
@Service
public class WfModelActivateService extends WfModelInfoBase {
    /**
     * @return Object 固定返回 null，操作结果由是否抛出异常体现
     * @description 把指定流程模型复制成新版本，版本号取当前最新版本加一，已是最高版本时抛出异常
     * @params [modelId] 流程模型 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public Object latestModel(String modelId) {
        // 获取流程模型
        Model model = repositoryService.getModel(modelId);
        if (ObjectUtil.isNull(model)) {
            throw new RuntimeException("流程模型不存在！");
        }
        Integer latestVersion =
                repositoryService
                        .createModelQuery()
                        .modelKey(model.getKey())
                        .latestVersion()
                        .singleResult()
                        .getVersion();
        if (model.getVersion().equals(latestVersion)) {
            throw new RuntimeException("当前版本已是最新版！");
        }
        // 获取 BPMN XML
        byte[] bpmnBytes = repositoryService.getModelEditorSource(modelId);
        Model newModel = repositoryService.newModel();
        newModel.setName(model.getName());
        newModel.setKey(model.getKey());
        newModel.setCategory(model.getCategory());
        newModel.setMetaInfo(model.getMetaInfo());
        newModel.setVersion(latestVersion + 1);
        // 保存流程模型
        repositoryService.saveModel(newModel);
        // 保存 BPMN XML
        repositoryService.addModelEditorSource(newModel.getId(), bpmnBytes);
        return null;
    }

    /**
     * @description 按模型对应部署查出流程定义，并将其挂起以停用该流程
     * @params [wfModelBo] 流程模型业务对象，提供模型 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional
    public void suspendedProcessDefinition(WfModelBo wfModelBo) {
        // 获取流程模型
        Model model = repositoryService.getModel(wfModelBo.getModelId());
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(model.getDeploymentId()).singleResult();
        //流程挂起
        repositoryService.suspendProcessDefinitionById(processDefinition.getId());
    }

    /**
     * @description 按模型对应部署查出流程定义，并将其激活以恢复使用
     * @params [wfModelBo] 流程模型业务对象，提供模型 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional
    public void activateProcessDefinition(WfModelBo wfModelBo) {
        // 获取流程模型
        Model model = repositoryService.getModel(wfModelBo.getModelId());
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(model.getDeploymentId()).singleResult();
        //流程激活
        repositoryService.activateProcessDefinitionById(processDefinition.getId());
    }
}
