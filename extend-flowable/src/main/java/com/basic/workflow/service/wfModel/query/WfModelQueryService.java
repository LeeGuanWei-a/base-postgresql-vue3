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
 * @author yanbin
 * @description TODO (Flowable流程模型查询)
 * @date 2024/03/05
 */
@Service
public class WfModelQueryService extends WfModelInfoBase {
    @Autowired
    private WfModelQueryXmlService wfModelQueryXmlService;

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfModelVo>
     * @description TODO (查询流程模型列表) 分页
     * @params [modelBo, pageQuery]
     * @author yanbin
     * @date 2023/11/30 13:55
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
     * @return java.util.List<com.basic.workflow.domain.vo.WfModelVo>
     * @description TODO (查询流程模型列表)不分页
     * @params [modelBo]
     * @author yanbin
     * @date 2023/11/30 13:55
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
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfModelVo>
     * @description TODO (查询流程模型列表)  历史模型
     * @params [modelBo, pageQuery]
     * @author yanbin
     * @date 2023/11/30 13:56
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
     * @return com.basic.workflow.domain.vo.WfModelVo
     * @description TODO (获取流程模型详细信息)
     * @params [modelId]
     * @author yanbin
     * @date 2023/11/17 16:45
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
