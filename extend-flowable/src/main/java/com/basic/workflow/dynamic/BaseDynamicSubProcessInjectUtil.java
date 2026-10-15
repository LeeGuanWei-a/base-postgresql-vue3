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
    public static void processFlowElements(CommandContext commandContext, FlowElementsContainer process, BpmnModel bpmnModel, ProcessDefinitionEntity originalProcessDefinitionEntity, DeploymentEntity newDeploymentEntity) {
        for (FlowElement flowElement : process.getFlowElements()) {
            processUserTask(flowElement, originalProcessDefinitionEntity, newDeploymentEntity, commandContext);
            if ((flowElement instanceof SubProcess)) {
                processFlowElements(commandContext, (SubProcess)flowElement, bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity);
            }
        }
    }

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
