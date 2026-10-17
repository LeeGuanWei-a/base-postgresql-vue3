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
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @description 流程模型处理抽象基类，统一注入模型的删除、部署、查询、保存与启动等子服务
 * @author yanbin
 * @date 2024/03/05
 */
@Component
public abstract class WfModelInfoServiceBase extends WfModelInfoBase {
    @Resource
    protected WfModelDeleteService wfModelDeleteService;
    @Resource
    protected WfModelActivateService wfModelActivateService;
    @Resource
    protected WfModelDeployService wfModelDeployService;
    @Resource
    protected WfModelQueryService wfModelQueryService;
    @Resource
    protected WfModelQueryXmlService wfModelQueryXmlService;
    @Resource
    protected WfModelSaveService wfModelSaveService;
    @Resource
    protected WfModelSaveXmlService wfModelSaveXmlService;
    @Resource
    protected WfModelStartService wfModelStartService;
    @Resource
    protected WfQueryModelStartService wfQueryModelStartService;
}
