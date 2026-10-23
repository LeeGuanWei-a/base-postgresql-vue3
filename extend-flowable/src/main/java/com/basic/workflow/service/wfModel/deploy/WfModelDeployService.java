package com.basic.workflow.service.wfModel.deploy;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.basic.common.exception.ServiceException;
import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

/**
 * @description 流程模型部署服务，校验模型的开始与结束节点后把 BPMN XML 部署为流程定义
 * @author yanbin
 * @date 2024/03/05
 *
 * <p>改造说明：Flowable 8 不再提供 {@code flowable-json-converter}，原先
 * 「取模型 editorSource(JSON) → BpmnJsonConverter → BpmnXMLConverter → 部署」的链路
 * 在 8.0 上必然拿到 null。现改为模型里直接存 BPMN XML，部署时解析 XML 后原样部署。</p>
 */
@Service
public class WfModelDeployService extends WfModelInfoBase {

    /**
     * @return boolean 部署表单保存成功返回 true
     * @description 把模型里保存的 BPMN XML 校验开始与结束节点后部署为流程定义，回写分类与部署关联并保存部署表单
     * @params [wfModelBo] 流程模型参数，含 modelId
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deployModel(WfModelBo wfModelBo) {
        String modelId = wfModelBo.getModelId();
        if (StrUtil.isBlank(modelId)) {
            throw new ServiceException("流程模型ID不能为空");
        }
        // 获取流程模型
        Model model = repositoryService.getModel(modelId);
        if (ObjectUtil.isNull(model)) {
            throw new ServiceException("流程模型不存在！");
        }
        // 获取模型里保存的 BPMN XML
        byte[] bpmnBytes = repositoryService.getModelEditorSource(modelId);
        if (ArrayUtil.isEmpty(bpmnBytes)) {
            throw new ServiceException("请先设计流程图！");
        }
        String bpmnXml = StrUtil.utf8Str(bpmnBytes);
        if (looksLikeJsonModel(bpmnXml)) {
            throw new ServiceException("该模型存的是旧版设计器 JSON 格式，Flowable 8 已移除 JSON 转换器，"
                    + "请用 BPMN XML 重新保存后再部署");
        }
        // 解析 BpmnModel（XML 安全解析，禁 DTD/外部实体）
        BpmnModel bpmnModel = parseBpmnModel(bpmnXml);
        // 部署前校验开始/结束节点，避免把跑不起来的流程部署进去
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        EndEvent endEvent = ModelUtils.getEndEvent(bpmnModel);
        if (ObjectUtil.isNull(startEvent)) {
            throw new ServiceException("开始节点不存在，请检查流程设计是否有误！");
        }
        if (ObjectUtil.isNull(endEvent)) {
            throw new ServiceException("结束节点不存在，请检查流程设计是否有误！");
        }
        // 资源名必须以 .bpmn 结尾，否则 Flowable 不会把它当作流程定义解析
        String resourceName = model.getName() + ProcessConstants.SUFFIX;
        Deployment deployment = repositoryService
                .createDeployment()
                .name(model.getName())
                .key(model.getKey())
                .category(model.getCategory())
                .addBytes(resourceName, bpmnXml.getBytes(StandardCharsets.UTF_8))
                .deploy();
        ProcessDefinition procDef = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        if (procDef == null) {
            throw new ServiceException("部署完成但没有生成流程定义，请检查 BPMN XML 里 <process> 节点是否完整");
        }
        // 修改流程定义的分类，便于搜索流程
        repositoryService.setProcessDefinitionCategory(procDef.getId(), model.getCategory());
        // 保存部署和流程模型的关联关系
        model.setDeploymentId(deployment.getId());
        repositoryService.saveModel(model);
        // 保存部署表单（解析 BPMN 里各节点的 flowable:formKey，落到 wf_deploy_form）
        return deployFormService.saveInternalDeployForm(deployment.getId(), bpmnModel, model.getKey());
    }
}
