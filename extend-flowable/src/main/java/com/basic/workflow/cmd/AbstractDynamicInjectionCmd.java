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

    /**
     * @description 按任务 ID 找到所属流程实例，为该实例生成改写后的派生流程定义
     * @params [commandContext, taskId, currentFlowElement] 命令上下文；当前任务 ID；插入或替换动作的参照节点
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected void createDerivedProcessDefinitionForTask(CommandContext commandContext, String taskId, FlowElement currentFlowElement) {
        TaskEntity taskEntity = CommandContextUtil.getTaskService(commandContext).getTask(taskId);
//        TaskEntity taskEntity = commandContext.getTaskEntityManager().findById(taskId);
        ProcessInstance processInstance = CommandContextUtil.getExecutionEntityManager().findById(taskEntity.getProcessInstanceId());
        createDerivedProcessDefinition(commandContext, processInstance, currentFlowElement);
    }

    /**
     * @description 按流程实例 ID 与当前节点 KEY 定位参照节点，为该实例生成派生流程定义
     * @params [commandContext, processInstanceId, currentTaskKey] 命令上下文；流程实例 ID；当前任务的节点定义 KEY
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected void createDerivedProcessDefinitionForProcessInstance(CommandContext commandContext, String processInstanceId, String currentTaskKey) {
        ProcessInstance processInstance = CommandContextUtil.getExecutionEntityManager().findById(processInstanceId);
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processInstance.getProcessDefinitionId());
        FlowElement currentFlowElement = bpmnModel.getFlowElement(currentTaskKey);
        createDerivedProcessDefinition(commandContext, processInstance, currentFlowElement);
    }

    /**
     * @description 派生流程定义的主流程：复制原部署、反序列化并改写 BPMN、重新部署，最后把实例及其运行态数据切到新定义
     * @params [commandContext, processInstance, currentFlowElement] 命令上下文；待切换的流程实例；改写动作的参照节点
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected void createDerivedProcessDefinition(CommandContext commandContext, ProcessInstance processInstance, FlowElement currentFlowElement) {
        ProcessDefinitionEntity originalProcessDefinitionEntity = (ProcessDefinitionEntity)CommandContextUtil.getProcessDefinitionEntityManager().findById(processInstance.getProcessDefinitionId());
        DeploymentEntity deploymentEntity = createDerivedDeployment(commandContext, originalProcessDefinitionEntity);
        BpmnModel bpmnModel = createBpmnModel(commandContext, originalProcessDefinitionEntity, deploymentEntity, currentFlowElement);
        storeBpmnModelAsByteArray(commandContext, bpmnModel, deploymentEntity, originalProcessDefinitionEntity.getResourceName());
        ProcessDefinitionEntity derivedProcessDefinitionEntity = deployDerivedDeploymentEntity(commandContext, deploymentEntity, originalProcessDefinitionEntity);
        updateExecutions(commandContext, derivedProcessDefinitionEntity, (ExecutionEntity)processInstance, bpmnModel);
    }

    /**
     * @return DeploymentEntity 新建的部署实体，改写后的 BPMN 资源会挂到它下面
     * @description 复制原部署的名称、分类、KEY 与租户信息，插入一条新的部署记录并返回
     * @params [commandContext, processDefinitionEntitty] 命令上下文；原流程定义
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
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

    /**
     * @description 把改写后的 BpmnModel 序列化成 XML 字节，沿用原资源名挂到新部署上
     * @params [commandContext, bpmnModel, deploymentEntity, resourceName] 命令上下文；改写后的流程模型；目标部署；资源名，沿用原流程定义的资源名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected void storeBpmnModelAsByteArray(CommandContext commandContext, BpmnModel bpmnModel, DeploymentEntity deploymentEntity, String resourceName) {
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        byte[] bytes = bpmnXMLConverter.convertToXML(bpmnModel);
        addResource(commandContext, deploymentEntity, resourceName, bytes);
    }

    /**
     * @return ProcessDefinitionEntity 新部署产出的流程定义实体
     * @description 以派生部署标记依次调用各部署器执行部署流程，从产出物中取出新的流程定义返回
     * @params [commandContext, deploymentEntity, originalProcessDefinitionEntity] 命令上下文；待部署的实体；原流程定义，其 ID 作为派生来源记录
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
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

    /**
     * @return BpmnModel 完成改写、待部署的流程模型
     * @description 从原流程定义的 BPMN 资源反序列化出流程模型，再交给子类的改写方法处理接口内容
     * @params [commandContext, originalProcessDefinitionEntity, newDeploymentEntity, currentFlowElement] 命令上下文；原流程定义；新建的部署；改写动作的参照节点
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected BpmnModel createBpmnModel(CommandContext commandContext, ProcessDefinitionEntity originalProcessDefinitionEntity, DeploymentEntity newDeploymentEntity, FlowElement currentFlowElement) {

        ResourceEntity originalBpmnResource = CommandContextUtil.getResourceEntityManager().findResourceByDeploymentIdAndResourceName(originalProcessDefinitionEntity.getDeploymentId(), originalProcessDefinitionEntity.getResourceName());
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(new BytesStreamSource(originalBpmnResource.getBytes()), false, false);

        Process process = bpmnModel.getProcessById(originalProcessDefinitionEntity.getKey());
        updateBpmnProcess(commandContext, process, currentFlowElement, bpmnModel, originalProcessDefinitionEntity, newDeploymentEntity);
        return bpmnModel;
    }

    /**
     * @description 模板方法，由子类在派生流程定义的 BPMN 模型上完成具体的节点插入、替换或删除改写
     * @params [paramCommandContext, paramProcess, currentFlowElement, paramBpmnModel, paramProcessDefinitionEntity, paramDeploymentEntity] 命令上下文；待改写的流程；改写动作的参照节点；待改写的流程模型；原流程定义；新建的部署
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected abstract void updateBpmnProcess(CommandContext paramCommandContext, Process paramProcess, FlowElement currentFlowElement, BpmnModel paramBpmnModel, ProcessDefinitionEntity paramProcessDefinitionEntity, DeploymentEntity paramDeploymentEntity);

    /**
     * @description 把流程实例及其子执行、运行时任务、各类作业、身份链接的流程定义 ID 与版本切到派生定义，并同步历史数据
     * @params [commandContext, processDefinitionEntity, processInstance, bpmnModel] 命令上下文；新的派生流程定义；流程实例；改写后的流程模型
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
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

    /**
     * @description 历史功能开启时，同步历史流程实例、历史任务与历史活动上的流程定义 ID；未开启则直接跳过
     * @params [commandContext, processDefinitionEntity, processInstance] 命令上下文；新的派生流程定义；流程实例
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
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

    /**
     * @description 新部署中缺少同名资源时，创建资源实体写入字节内容并登记到部署上，已存在则不再重复写入
     * @params [commandContext, deploymentEntity, resourceName, bytes] 命令上下文；目标部署；资源名；资源字节内容
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
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
