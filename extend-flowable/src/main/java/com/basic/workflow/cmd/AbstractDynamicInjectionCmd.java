package com.basic.workflow.cmd;


import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.impl.EngineDeployer;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.util.io.BytesStreamSource;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.persistence.entity.*;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import org.flowable.job.service.impl.DeadLetterJobQueryImpl;
import org.flowable.job.service.impl.persistence.entity.JobEntity;
import org.flowable.job.service.impl.persistence.entity.TimerJobEntity;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDynamicInjectionCmd {

    protected void createDerivedProcessDefinitionForTask(CommandContext commandContext, String taskId, FlowElement currentFlowElement) {
        TaskEntity taskEntity = CommandContextUtil.getTaskService(commandContext).getTask(taskId);
//        TaskEntity taskEntity = commandContext.getTaskEntityManager().findById(taskId);
        ProcessInstance processInstance = CommandContextUtil.getExecutionEntityManager().findById(taskEntity.getProcessInstanceId());
        createDerivedProcessDefinition(commandContext, processInstance, currentFlowElement);
    }

    protected void createDerivedProcessDefinitionForProcessInstance(CommandContext commandContext, String processInstanceId, String currentTaskKey) {
        ProcessInstance processInstance = CommandContextUtil.getExecutionEntityManager().findById(processInstanceId);
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processInstance.getProcessDefinitionId());
        FlowElement currentFlowElement = bpmnModel.getFlowElement(currentTaskKey);
        createDerivedProcessDefinition(commandContext, processInstance, currentFlowElement);
    }

    protected void createDerivedProcessDefinition(CommandContext commandContext, ProcessInstance processInstance, FlowElement currentFlowElement) {
        ProcessDefinitionEntity originalProcessDefinitionEntity = (ProcessDefinitionEntity)CommandContextUtil.getProcessDefinitionEntityManager().findById(processInstance.getProcessDefinitionId());
        DeploymentEntity deploymentEntity = createDerivedDeployment(commandContext, originalProcessDefinitionEntity);
        BpmnModel bpmnModel = createBpmnModel(commandContext, originalProcessDefinitionEntity, deploymentEntity, currentFlowElement);
        storeBpmnModelAsByteArray(commandContext, bpmnModel, deploymentEntity, originalProcessDefinitionEntity.getResourceName());
        ProcessDefinitionEntity derivedProcessDefinitionEntity = deployDerivedDeploymentEntity(commandContext, deploymentEntity, originalProcessDefinitionEntity);
        updateExecutions(commandContext, derivedProcessDefinitionEntity, (ExecutionEntity)processInstance, bpmnModel);
    }

    protected DeploymentEntity createDerivedDeployment(CommandContext commandContext, ProcessDefinitionEntity processDefinitionEntitty) {
        DeploymentEntityManager deploymentEntityManager = CommandContextUtil.getDeploymentEntityManager();
        DeploymentEntity deploymentEntity = (DeploymentEntity)deploymentEntityManager.findById(processDefinitionEntitty.getDeploymentId());

        DeploymentEntity newDeploymentEntity = (DeploymentEntity)deploymentEntityManager.create();
        newDeploymentEntity.setName(deploymentEntity.getName());
        newDeploymentEntity.setDeploymentTime(new Date());
        newDeploymentEntity.setCategory(deploymentEntity.getCategory());
        newDeploymentEntity.setKey(deploymentEntity.getKey());
        newDeploymentEntity.setTenantId(deploymentEntity.getTenantId());

        deploymentEntityManager.insert(newDeploymentEntity);
        return newDeploymentEntity;
    }

    protected void storeBpmnModelAsByteArray(CommandContext commandContext, BpmnModel bpmnModel, DeploymentEntity deploymentEntity, String resourceName) {
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] bytes = bpmnXMLConverter.convertToXML(bpmnModel);
        addResource(commandContext, deploymentEntity, resourceName, bytes);
    }

    protected ProcessDefinitionEntity deployDerivedDeploymentEntity(CommandContext commandContext, DeploymentEntity deploymentEntity, ProcessDefinitionEntity originalProcessDefinitionEntity) {
        Map<String, Object> deploymentSettings = new HashMap();
        deploymentSettings.put("isDerivedDeployment", Boolean.valueOf(true));
        deploymentSettings.put("derivedProcessDefinitionId", originalProcessDefinitionEntity.getId());
        deploymentEntity.setNew(true);
        List<EngineDeployer> deployers = CommandContextUtil.getProcessEngineConfiguration().getDeploymentManager().getDeployers();
        for (EngineDeployer engineDeployer : deployers) {
            engineDeployer.deploy(deploymentEntity, deploymentSettings);
        }
        return (ProcessDefinitionEntity)deploymentEntity.getDeployedArtifacts(ProcessDefinitionEntity.class).get(0);
    }

    protected BpmnModel createBpmnModel(CommandContext commandContext, ProcessDefinitionEntity originalProcessDefinitionEntity, DeploymentEntity newDeploymentEntity, FlowElement currentFlowElement) {

        ResourceEntity originalBpmnResource = CommandContextUtil.getResourceEntityManager().findResourceByDeploymentIdAndResourceName(originalProcessDefinitionEntity.getDeploymentId(), originalProcessDefinitionEntity.getResourceName());
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(new BytesStreamSource(originalBpmnResource.getBytes()), false, false);

        Process process = bpmnModel.getProcessById(originalProcessDefinitionEntity.getKey());
        updateBpmnProcess(commandContext, process, currentFlowElement, bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity);
        return bpmnModel;
    }

    protected abstract void updateBpmnProcess(CommandContext paramCommandContext, Process paramProcess, FlowElement currentFlowElement, BpmnModel paramBpmnModel, ProcessDefinitionEntity paramProcessDefinitionEntity, DeploymentEntity paramDeploymentEntity);

    protected void updateExecutions(CommandContext commandContext, ProcessDefinitionEntity processDefinitionEntity, ExecutionEntity processInstance, BpmnModel bpmnModel) {
        String previousProcessDefinitionId = processInstance.getProcessDefinitionId();
        processInstance.setProcessDefinitionId(processDefinitionEntity.getId());
        processInstance.setProcessDefinitionVersion(processDefinitionEntity.getVersion());
//        TaskEntity taskEntity = CommandContextUtil.get;

        List<TaskEntity> currentTasks = CommandContextUtil.getTaskService().findTasksByProcessInstanceId(processInstance.getId());
        for (TaskEntity currentTask : currentTasks) {
            currentTask.setProcessDefinitionId(processDefinitionEntity.getId());
        }
        List<JobEntity> currentJobs = CommandContextUtil.getJobService().findJobsByProcessInstanceId(processInstance.getId());
//        List<JobEntity> currentJobs = CommandContextUtil.getJobEntityManager().findJobsByProcessInstanceId(processInstance.getId());
        for (JobEntity currentJob : currentJobs) {
            currentJob.setProcessDefinitionId(processDefinitionEntity.getId());
        }

        List<TimerJobEntity> currentTimerJobs = CommandContextUtil.getTimerJobService().findTimerJobsByProcessInstanceId(processInstance.getId());
        for (TimerJobEntity currentTimerJob : currentTimerJobs) {
            currentTimerJob.setProcessDefinitionId(processDefinitionEntity.getId());
        }
//        List<SuspendedJobEntity> currentSuspendedJobs = CommandContextUtil.getSuspendedJobEntityManager().findJobsByProcessInstanceId(processInstance.getId());
//        for (SuspendedJobEntity currentSuspendedJob : currentSuspendedJobs) {
//            currentSuspendedJob.setProcessDefinitionId(processDefinitionEntity.getId());
//        }

        DeadLetterJobQueryImpl deadLetterJobQuery = new DeadLetterJobQueryImpl();
        deadLetterJobQuery.processInstanceId(processInstance.getId());

/*
        List<DeadLetterJobEntity> currentDeadLetterJobs = (List<DeadLetterJobEntity>)commandContext.getDeadLetterJobEntityManager().findJobsByQueryCriteria(deadLetterJobQuery, new Page(0,1000));
        for (DeadLetterJobEntity currentDeadLetterJob : currentDeadLetterJobs) {
            currentDeadLetterJob.setProcessDefinitionId(processDefinitionEntity.getId());
        }
*/

        List<IdentityLinkEntity> identityLinks = CommandContextUtil.getIdentityLinkService().findIdentityLinksByProcessDefinitionId(previousProcessDefinitionId);;
        for (IdentityLinkEntity identityLinkEntity : identityLinks) {
            identityLinkEntity.setProcessDefId(processDefinitionEntity.getId());
        }

        updateProcessDefinitionIdInHistory(commandContext, processDefinitionEntity, processInstance);

        List<ExecutionEntity> childExecutions = CommandContextUtil.getExecutionEntityManager().findChildExecutionsByProcessInstanceId(processInstance.getId());
        for (ExecutionEntity childExecution : childExecutions) {
            childExecution.setProcessDefinitionId(processDefinitionEntity.getId());
            childExecution.setProcessDefinitionVersion(processDefinitionEntity.getVersion());
        }
    }

    public void updateProcessDefinitionIdInHistory(CommandContext commandContext, ProcessDefinitionEntity processDefinitionEntity, ExecutionEntity processInstance) {
        if (CommandContextUtil.getHistoryManager().isHistoryEnabled()) {
            HistoricProcessInstanceEntity historicProcessInstance = CommandContextUtil.getHistoricProcessInstanceEntityManager().findById(processInstance.getId());
            historicProcessInstance.setProcessDefinitionId(processDefinitionEntity.getId());
            CommandContextUtil.getHistoricProcessInstanceEntityManager().update(historicProcessInstance);

            HistoryService historyService = CommandContextUtil.getProcessEngineConfiguration().getHistoryService();
            List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstance.getId()).list();
            if (historicTasks != null) {
                for (HistoricTaskInstance historicTaskInstance : historicTasks) {
                    HistoricTaskInstanceEntity taskEntity = (HistoricTaskInstanceEntity) historicTaskInstance;
                    taskEntity.setProcessDefinitionId(processDefinitionEntity.getId());
                    CommandContextUtil.getHistoricTaskService(commandContext).updateHistoricTask(taskEntity, true);
//                    CommandContextUtil.getHistoricTaskInstanceEntityManager().update(taskEntity, true);
                }
            }

            List<HistoricActivityInstance> historicActivities = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).list();
            if (historicActivities != null) {
                for (HistoricActivityInstance historicActivityInstance : historicActivities) {
                    HistoricActivityInstanceEntity activityEntity = (HistoricActivityInstanceEntity) historicActivityInstance;
                    activityEntity.setProcessDefinitionId(processDefinitionEntity.getId());
                    CommandContextUtil.getHistoricActivityInstanceEntityManager().update(activityEntity);
                }
            }
        }
    }

    private void addResource(CommandContext commandContext, DeploymentEntity deploymentEntity, String resourceName, byte[] bytes) {
        if (!deploymentEntity.getResources().containsKey(resourceName)) {
            ResourceEntityManager resourceEntityManager = CommandContextUtil.getResourceEntityManager();
            ResourceEntity resourceEntity = resourceEntityManager.create();
            resourceEntity.setDeploymentId(deploymentEntity.getId());
            resourceEntity.setName(resourceName);
            resourceEntity.setBytes(bytes);
            resourceEntityManager.insert(resourceEntity);
            deploymentEntity.addResource(resourceEntity);
        }
    }

}
