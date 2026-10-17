package com.basic.flowable.factory;

import lombok.Getter;
import org.flowable.engine.*;
import org.flowable.job.service.JobService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @description Flowable 引擎各 Service 的注入封装基类，供流程业务类继承后直接取用引擎 API
 * @author yanbin
 * @date 2023/10/19
 */
@Component
@Getter
public class FlowServiceFactory {
    /**
     * Flowable的资源管理类
     * 用于管理流程定义的生命周期、与流程定义相关的资源、以及对流程定义的查询操作
     */
    @Resource
    protected RepositoryService repositoryService;
    /**
     * Flowable的流程运行管理类
     * 用于管理流程实例的运行时状态和相关操作
     */
    @Resource
    protected RuntimeService runtimeService;
    /**它用于管理（创建，更新，删除，查询……）组与用户*/
    @Resource
    protected IdentityService identityService;
    /**
     * Flowable的任务管理类
     * 用于处理任务相关的操作和数据
     */
    @Resource
    protected TaskService taskService;
    /**
     * 用于处理表单相关的操作和数据
     */
    @Resource
    protected FormService formService;
    /**
     * Flowable的历史管理类
     * 用于处理流程历史的相关操作和数据
     */
    @Resource
    protected HistoryService historyService;
    /**
     * 用于管理和操作引擎的一些管理任务和操作。它提供了管理和监控流程实例、任务、作业等的功能
     */
    @Resource
    protected ManagementService managementService;
    /**
     * 负责与各个服务进行交互和管理流程的整个生命周期
     */
    @Qualifier("processEngine")
    @Resource
    protected ProcessEngine processEngine;
}
