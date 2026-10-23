package com.basic.workflow.service.wfModel.base;

import com.basic.workflow.service.wfModel.delete.WfModelDeleteService;
import com.basic.workflow.service.wfModel.deploy.WfModelActivateService;
import com.basic.workflow.service.wfModel.deploy.WfModelDeployService;
import com.basic.workflow.service.wfModel.query.WfModelQueryService;
import com.basic.workflow.service.wfModel.queryXml.WfModelQueryXmlService;
import com.basic.workflow.service.wfModel.save.WfModelSaveService;
import com.basic.workflow.service.wfModel.save.WfModelSaveXmlService;
import com.basic.workflow.service.wfModel.start.WfModelStartService;
import com.basic.workflow.service.wfModel.start.WfQueryModelStartService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @description 流程模型处理抽象基类，统一注入模型的删除、部署、查询、保存与启动等子服务。
 * 子服务字段全部加 {@code @Lazy}，避免与子服务继承链（WfModelInfoBase → WfModel*Service → WfModelInfoBase）形成
 * Spring 启动期的 bean 创建循环。运行时第一次调用才解析真实依赖。
 * @author yanbin
 * @date 2024/03/05
 */
@Component
public abstract class WfModelInfoServiceBase extends WfModelInfoBase {
    @Resource
    @Lazy
    protected WfModelDeleteService wfModelDeleteService;
    @Resource
    @Lazy
    protected WfModelActivateService wfModelActivateService;
    @Resource
    @Lazy
    protected WfModelDeployService wfModelDeployService;
    @Resource
    @Lazy
    protected WfModelQueryService wfModelQueryService;
    @Resource
    @Lazy
    protected WfModelQueryXmlService wfModelQueryXmlService;
    @Resource
    @Lazy
    protected WfModelSaveService wfModelSaveService;
    @Resource
    @Lazy
    protected WfModelSaveXmlService wfModelSaveXmlService;
    @Resource
    @Lazy
    protected WfModelStartService wfModelStartService;
    @Resource
    @Lazy
    protected WfQueryModelStartService wfQueryModelStartService;
}
