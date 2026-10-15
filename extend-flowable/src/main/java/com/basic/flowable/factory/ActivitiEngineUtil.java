package com.basic.flowable.factory;

import lombok.Data;
import org.activiti.engine.*;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;

/**
 * @author yanbin
 * @description TODO (activity  引擎注入封装 api)
 * @date 2023/10/19
 */
@Data
public class ActivitiEngineUtil {

    //流程引擎配置
    protected ProcessEngineConfigurationImpl processEngineConfiguration;
    //流程引擎
    protected ProcessEngine engine;
    //流程存储服务
    protected RepositoryService repositoryService;
    //运行时服务
    protected RuntimeService runtimeService;
    //任务服务
    protected TaskService taskService;
    //历史服务
    protected HistoryService historyService;
    //管理服务
    protected ManagementService managementService;

    /**
     * 初始化流程引擎及各种服务
     *
     * @param resource Activiti配置文件
     */
    public void loadActivitiConfigAndInitEngine(String resource) {
        //创建流程引擎配置
        this.processEngineConfiguration
                = (ProcessEngineConfigurationImpl) ProcessEngineConfiguration.createProcessEngineConfigurationFromResource(resource);
        //创建流程引擎
        this.engine = processEngineConfiguration.buildProcessEngine();
        //获取流程存储服务
        this.repositoryService = engine.getRepositoryService();
        //获取运行时服务
        this.runtimeService = engine.getRuntimeService();
        //获取任务服务
        this.taskService = engine.getTaskService();
        //获取历史服务
        this.historyService = engine.getHistoryService();
        //获取管理服务
        this.managementService = engine.getManagementService();
    }

    /**
     * 部署单个流程
     *
     * @param resource 单个流程XML文件地址
     * @return
     */
    public ProcessDefinition deployByClasspathResource(String resource) {
        //部署流程
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(resource).deploy();
        //查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        return processDefinition;
    }

    /**
     * 部署多个流程
     *
     * @param resources 多个流程XML文件地址
     * @return
     */
    public ProcessDefinition deployByClasspathResource(String... resources) {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        //部署流程
        for (String resource : resources) {
            deploymentBuilder.addClasspathResource(resource);
        }
        Deployment deployment = deploymentBuilder.deploy();
        //查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        return processDefinition;
    }

    /**
     * 关闭流程引擎
     */
    public void closeEngine() {
        engine.close();
    }
}
