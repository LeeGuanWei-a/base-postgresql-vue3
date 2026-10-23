package com.basic.workflow.dynamic;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.impl.persistence.entity.DeploymentEntity;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.impl.persistence.entity.ResourceEntity;
import org.flowable.engine.impl.persistence.entity.ResourceEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.form.api.FormDefinition;
import org.flowable.form.api.FormDeployment;
import org.flowable.form.api.FormRepositoryService;

import java.io.InputStream;
import java.util.List;

public class BaseDynamicSubProcessInjectUtil {
    /**
     * @description 递归遍历流程元素，遇到子流程继续下钻，把各用户任务引用的表单资源补挂到新部署中
     * @params [commandContext, process, bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity] 当前命令上下文；流程或子流程容器；流程模型；原流程定义实体；待写入资源的新部署实体
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static void processFlowElements(CommandContext commandContext, FlowElementsContainer process, BpmnModel bpmnModel, ProcessDefinitionEntity originalProcessDefinitionEntity, DeploymentEntity newDeploymentEntity) {
        for (FlowElement flowElement : process.getFlowElements()) {
            processUserTask(flowElement, originalProcessDefinitionEntity, newDeploymentEntity, commandContext);
            if ((flowElement instanceof SubProcess)) {
                processFlowElements(commandContext, (SubProcess)flowElement, bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity);
            }
        }
    }

    /**
     * @description 把用户任务 formKey 引用的表单定义资源读出来，复制到新部署中，非用户任务直接跳过
     * @params [flowElement, originalProcessDefinitionEntity, newDeploymentEntity, commandContext] 待检查的流程元素，仅处理用户任务；原流程定义实体，用于定位其部署；新部署实体，表单资源写入目标；当前命令上下文
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected static void processUserTask(FlowElement flowElement, ProcessDefinition originalProcessDefinitionEntity, DeploymentEntity newDeploymentEntity, CommandContext commandContext) {
        if ((flowElement instanceof UserTask)) {
            FormRepositoryService formRepositoryService = CommandContextUtil.getFormRepositoryService(commandContext);
            if (formRepositoryService != null) {
                UserTask userTask = (UserTask)flowElement;
                if (StringUtils.isNotEmpty(userTask.getFormKey())) {

                    Deployment deployment = CommandContextUtil.getDeploymentEntityManager(commandContext).findById(originalProcessDefinitionEntity.getDeploymentId());
                    List<FormDeployment> formDeployments = formRepositoryService.createDeploymentQuery().parentDeploymentId(deployment.getId()).list();
                    if ((formDeployments != null) && (formDeployments.size() > 0)) {
                        FormDefinition formDefinition = (FormDefinition)formRepositoryService.createDeploymentQuery().formDefinitionKey(userTask.getFormKey()).deploymentId(((FormDeployment)formDeployments.get(0)).getId()).singleResult();
                        if (formDefinition != null) {
                            String name = formDefinition.getName();
                            InputStream inputStream = formRepositoryService.getFormDefinitionResource(formDefinition.getId());
                            addResource(commandContext, newDeploymentEntity, name, IoUtil.readInputStream(inputStream, name));
                            IoUtil.closeSilently(inputStream);
                        }
                    }
                }
            }
        }
    }

    /**
     * @description 向部署中插入一条资源记录，同名资源已存在时跳过
     * @params [commandContext, deploymentEntity, resourceName, bytes] 当前命令上下文；目标部署实体；资源名称；资源内容
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static void addResource(CommandContext commandContext, DeploymentEntity deploymentEntity, String resourceName, byte[] bytes) {
        if (!deploymentEntity.getResources().containsKey(resourceName)) {
            ResourceEntityManager resourceEntityManager =  CommandContextUtil.getResourceEntityManager(commandContext);
            ResourceEntity resourceEntity = resourceEntityManager.create();
            resourceEntity.setDeploymentId(deploymentEntity.getId());
            resourceEntity.setName(resourceName);
            resourceEntity.setBytes(bytes);
            resourceEntityManager.insert(resourceEntity);
            deploymentEntity.addResource(resourceEntity);
        }
    }
}
