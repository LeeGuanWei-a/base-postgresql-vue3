package com.basic.workflow.service.wfModel.deploy;

import cn.hutool.core.util.ObjectUtil;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yanbin
 * @description TODO (Flowable流程设为最新、激活、停用)
 * @date 2024/03/05
 */
@Service
public class WfModelActivateService extends WfModelInfoBase {
    /**
     * @return java.lang.Object
     * @description TODO (设为最新流程模型)
     * @params [modelId]
     * @author yanbin
     * @date 2023/11/30 13:57
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
     * @return void
     * @description TODO (流程模型挂起(停用))
     * @params [wfModelBo]
     * @author yanbin
     * @date 2024/3/5 9:03
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
     * @return void
     * @description TODO (激活流程模型)
     * @params [wfModelBo]
     * @author yanbin
     * @date 2024/3/5 9:03
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
