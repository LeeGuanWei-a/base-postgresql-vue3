package com.basic.workflow.service.wfModel.query;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.utils.JsonUtils;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.common.enums.FormType;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.domain.dto.WfMetaInfoDto;
import com.basic.workflow.domain.vo.WfFormVo;
import com.basic.workflow.domain.vo.WfModelVo;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import com.basic.workflow.service.wfModel.queryXml.WfModelQueryXmlService;
import org.dom4j.DocumentException;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description 流程模型查询服务，分页与不分页查询最新版本或历史版本模型，并标注部署、挂起与待重新部署状态
 * @author yanbin
 * @date 2024/03/05
 */
@Service
public class WfModelQueryService extends WfModelInfoBase {
    @Autowired
    private WfModelQueryXmlService wfModelQueryXmlService;

    /**
     * @return TableDataInfo<WfModelVo> 流程模型分页列表
     * @description 按模型key、名称与分类分批查询最新版本流程模型并分页返回，同时标注部署、挂起与待重新部署状态
     * @params [modelBo, pageQuery] 流程模型查询条件，分类支持逗号分隔多个编码；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public TableDataInfo<WfModelVo> list(WfModelBo modelBo, PageQuery pageQuery) {
        ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion().orderByCreateTime().desc();
        // 构建查询条件
        if (StringUtils.isNotBlank(modelBo.getModelKey())) {
            modelQuery.modelKey(modelBo.getModelKey());
        }
        if (StringUtils.isNotBlank(modelBo.getModelName())) {
            modelQuery.modelNameLike("%" + modelBo.getModelName() + "%");
        }
        //        if (StringUtils.isNotBlank(modelBo.getCategory())) {
//            modelQuery.modelCategory(modelBo.getCategory());
//        }

        // 执行查询
        long pageTotal = modelQuery.count();
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<Model> modelList = new ArrayList<>();
        if (StringUtils.isNotBlank(modelBo.getCategory())) {
//            modelQuery.modelCategory(modelBo.getCategory());
            List<String> categoryList = Arrays.asList(modelBo.getCategory().split(","));
            for (String code : categoryList) {
                modelQuery.modelCategory(code);
                List<Model> list2 = modelQuery.list();
                modelList.addAll(list2);
            }
        } else {
            modelList = modelQuery.listPage(offset, pageQuery.getPageSize());
        }
        pageTotal = modelQuery.count() > modelList.size() ? modelQuery.count() : modelList.size();
//        List<Model> modelList = modelQuery.listPage(offset, pageQuery.getPageSize());
        List<WfModelVo> modelVoList = new ArrayList<>(modelList.size());
        modelList.forEach(
                model -> {
                    WfModelVo modelVo = new WfModelVo();
                    modelVo.setModelId(model.getId());
                    modelVo.setModelName(model.getName());
                    modelVo.setModelKey(model.getKey());
                    modelVo.setCategory(model.getCategory());
                    modelVo.setCreateTime(model.getCreateTime());
                    modelVo.setVersion(model.getVersion());
                    modelVo.setUpdateTime(model.getLastUpdateTime());
                    modelVo.setDeploy(StringUtils.isNotEmpty(model.getDeploymentId()));
                    if (StringUtils.isNotEmpty(model.getDeploymentId())) {
                        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(model.getDeploymentId()).singleResult();
                        if (processDefinition.isSuspended()) { //是否挂起
                            modelVo.setSuspended(true); //挂起中
                        } else {
                            modelVo.setSuspended(false); //激活中
                        }
                        // 是否待重新部署：bpmn 的最后修改时间晚于当前部署时间
                        // 用 deploymentTime 作为比对基准（同一 deployment 内 processDefinition 共享部署时间）
                        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(model.getDeploymentId()).singleResult();
                        if (deployment != null && model.getLastUpdateTime() != null && deployment.getDeploymentTime() != null) {
                            modelVo.setPendingRedeploy(model.getLastUpdateTime().after(deployment.getDeploymentTime()));
                        } else {
                            modelVo.setPendingRedeploy(false);
                        }
                    } else {
                        // 没部署过，自然不存在「待重新部署」状态
                        modelVo.setPendingRedeploy(false);
                    }
                    WfMetaInfoDto metaInfo = JsonUtils.parseObject(model.getMetaInfo(), WfMetaInfoDto.class);
                    if (metaInfo != null) {
                        modelVo.setDescription(metaInfo.getDescription());
                        modelVo.setFormType(metaInfo.getFormType());
                        modelVo.setFormId(metaInfo.getFormId());
                    }
                    modelVoList.add(modelVo);
                });
        Page<WfModelVo> page = new Page<>();
        page.setRecords(modelVoList);
        page.setTotal(pageTotal);
        return TableDataInfo.build(page);
    }

    /**
     * @return List<WfModelVo> 最新版本的流程模型列表，不分页
     * @description 按模型key、名称与分类查询最新版本流程模型列表，不分页
     * @params [modelBo] 流程模型查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfModelVo> list(WfModelBo modelBo) {
        ModelQuery modelQuery =
                repositoryService.createModelQuery().latestVersion().orderByCreateTime().desc();
        // 构建查询条件
        if (StringUtils.isNotBlank(modelBo.getModelKey())) {
            modelQuery.modelKey(modelBo.getModelKey());
        }
        if (StringUtils.isNotBlank(modelBo.getModelName())) {
            modelQuery.modelNameLike("%" + modelBo.getModelName() + "%");
        }
        if (StringUtils.isNotBlank(modelBo.getCategory())) {
            modelQuery.modelCategory(modelBo.getCategory());
        }
        List<Model> modelList = modelQuery.list();
        List<WfModelVo> modelVoList = new ArrayList<>(modelList.size());
        modelList.forEach(
                model -> {
                    WfModelVo modelVo = new WfModelVo();
                    modelVo.setModelId(model.getId());
                    modelVo.setModelName(model.getName());
                    modelVo.setModelKey(model.getKey());
                    modelVo.setCategory(model.getCategory());
                    modelVo.setCreateTime(model.getCreateTime());
                    modelVo.setVersion(model.getVersion());
                    WfMetaInfoDto metaInfo = JsonUtils.parseObject(model.getMetaInfo(), WfMetaInfoDto.class);
                    if (metaInfo != null) {
                        modelVo.setDescription(metaInfo.getDescription());
                        modelVo.setFormType(metaInfo.getFormType());
                        modelVo.setFormId(metaInfo.getFormId());
                    }
                    modelVoList.add(modelVo);
                });
        return modelVoList;
    }

    /**
     * @return TableDataInfo<WfModelVo> 历史版本流程模型分页列表
     * @description 按模型key分版本倒序查询历史模型并分页返回，跳过最新版那一页偏移
     * @params [modelBo, pageQuery] 流程模型查询条件，使用其中的流程模型key；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public TableDataInfo<WfModelVo> historyList(WfModelBo modelBo, PageQuery pageQuery) {
        ModelQuery modelQuery =
                repositoryService
                        .createModelQuery()
                        .modelKey(modelBo.getModelKey())
                        .orderByModelVersion()
                        .desc();
        // 执行查询（不显示最新版，-1）
        long pageTotal = modelQuery.count() - 1;
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }
        // offset+1，去掉最新版
        int offset = 1 + pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<Model> modelList = modelQuery.listPage(offset, pageQuery.getPageSize());
        List<WfModelVo> modelVoList = new ArrayList<>(modelList.size());
        modelList.forEach(
                model -> {
                    WfModelVo modelVo = new WfModelVo();
                    modelVo.setModelId(model.getId());
                    modelVo.setModelName(model.getName());
                    modelVo.setModelKey(model.getKey());
                    modelVo.setCategory(model.getCategory());
                    modelVo.setCreateTime(model.getCreateTime());
                    modelVo.setVersion(model.getVersion());
                    WfMetaInfoDto metaInfo = JsonUtils.parseObject(model.getMetaInfo(), WfMetaInfoDto.class);
                    if (metaInfo != null) {
                        modelVo.setDescription(metaInfo.getDescription());
                        modelVo.setFormType(metaInfo.getFormType());
                        modelVo.setFormId(metaInfo.getFormId());
                    }
                    modelVoList.add(modelVo);
                });
        Page<WfModelVo> page = new Page<>();
        page.setRecords(modelVoList);
        page.setTotal(pageTotal);
        return TableDataInfo.build(page);
    }

    /**
     * @return WfModelVo 流程模型详情，含 BPMN XML 与流程表单内容
     * @description 按模型主键取流程模型基础信息、BPMN XML 与元数据，表单类型为流程表单时再带出表单内容，模型不存在抛 RuntimeException
     * @params [modelId] 模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfModelVo getModel(String modelId) throws IOException, DocumentException {
        // 获取流程模型
        Model model = repositoryService.getModel(modelId);
        if (ObjectUtil.isNull(model)) {
            throw new RuntimeException("流程模型不存在！");
        }
        // 获取流程图
        String bpmnXml = wfModelQueryXmlService.queryBpmnXmlById(modelId);
        WfModelVo modelVo = new WfModelVo();
        modelVo.setModelId(model.getId());
        modelVo.setModelName(model.getName());
        modelVo.setModelKey(model.getKey());
        modelVo.setCategory(model.getCategory());
        modelVo.setCreateTime(model.getCreateTime());
        modelVo.setVersion(model.getVersion());
        modelVo.setBpmnXml(bpmnXml);
        WfMetaInfoDto metaInfo = JsonUtils.parseObject(model.getMetaInfo(), WfMetaInfoDto.class);
        if (metaInfo != null) {
            modelVo.setDescription(metaInfo.getDescription());
            modelVo.setFormType(metaInfo.getFormType());
            modelVo.setFormId(metaInfo.getFormId());
            if (FormType.PROCESS.getType().equals(metaInfo.getFormType())) {
                WfFormVo wfFormVo = wfFormService.queryById(metaInfo.getFormId());
                modelVo.setContent(wfFormVo.getContent());
            }
        }
        return modelVo;
    }
}
