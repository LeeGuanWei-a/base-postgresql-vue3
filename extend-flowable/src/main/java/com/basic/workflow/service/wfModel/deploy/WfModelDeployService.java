package com.basic.workflow.service.wfModel.deploy;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yanbin
 * @description TODO (Flowable流程模型部署)
 * @date 2024/03/05
 */
@Service
public class WfModelDeployService extends WfModelInfoBase {

  /**
   * @return boolean
   * @description TODO (部署流程模型)
   * @params [wfModelBo]
   * @author yanbin
   * @date 2023/11/30 13:57
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Transactional(rollbackFor = Exception.class)
  public boolean deployModel(WfModelBo wfModelBo) throws JsonProcessingException {
    String modelId = wfModelBo.getModelId();
    // 获取流程模型
    Model model = repositoryService.getModel(modelId);
    if (ObjectUtil.isNull(model)) {
      throw new RuntimeException("流程模型不存在！");
    }
    // 获取流程图
    byte[] bpmnBytes = repositoryService.getModelEditorSource(modelId);
    if (ArrayUtil.isEmpty(bpmnBytes)) {
      throw new RuntimeException("请先设计流程图！");
    }
    ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(StrUtil.utf8Str(bpmnBytes));
    // bpmnModel
    BpmnModel models = new BpmnJsonConverter().convertToBpmnModel(modelNode); // 转model
    //    bpmnModel转为byte
    byte[] bytes = new BpmnXMLConverter().convertToXML(models); // 转xml
    String bpmnXml = StrUtil.utf8Str(bytes);
    BpmnModel bpmnModel = ModelUtils.getBpmnModel(bpmnXml);
    String processName = model.getName() + ProcessConstants.SUFFIX;
    // 获取开始节点
    StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
    // 获取开始节点
    EndEvent endEvent = ModelUtils.getEndEvent(bpmnModel);
    if (ObjectUtil.isNull(startEvent)) {
      throw new RuntimeException("开始节点不存在，请检查流程设计是否有误！");
    }
    if (ObjectUtil.isNull(endEvent)) {
      throw new RuntimeException("结束节点不存在，请检查流程设计是否有误！");
    }
    //TODO  部署流程--- 租户id 默认给一个
    Deployment deployment = repositoryService
        .createDeployment()
        .name(model.getName())
        .key(model.getKey())
        .category(model.getCategory())
        .addBytes(processName, bytes)
        .deploy();
    ProcessDefinition procDef = repositoryService
        .createProcessDefinitionQuery()
        .deploymentId(deployment.getId())
        .singleResult();
    // 修改流程定义的分类，便于搜索流程
    repositoryService.setProcessDefinitionCategory(procDef.getId(), model.getCategory());
    // 保存部署和流程模型的关联关系
    model.setDeploymentId(deployment.getId());
    repositoryService.saveModel(model);
    //TODO 保存部署表单
    return deployFormService.saveInternalDeployForm(deployment.getId(), bpmnModel, model.getKey());
  }
}
