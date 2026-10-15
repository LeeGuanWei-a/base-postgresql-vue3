package com.basic.workflow.service.wfModel.start;

import com.basic.common.utils.StringUtils;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.domain.vo.WfModelVo;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yanbin
 * @description TODO (Flowable可发起的流程查询)
 * @date 2024/03/05
 */
@Service
public class WfQueryModelStartService extends WfModelInfoBase {
    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfModelVo>
     * @description TODO (流程启动列表)
     * @params [modelBo, pageQuery]
     * @author yanbin
     * @date 2023/11/10 10:12
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfModelVo> listModelLaunch(WfModelBo modelBo) {
        ModelQuery modelQuery = repositoryService
                .createModelQuery()
                .latestVersion()
                .orderByCreateTime().desc();
        // 构建查询条件
        if (StringUtils.isNotBlank(modelBo.getModelKey())) {
            modelQuery.modelKey(modelBo.getModelKey());
        }
        //model名查询
        if (StringUtils.isNotBlank(modelBo.getModelName())) {
            modelQuery.modelNameLike("%" + modelBo.getModelName() + "%");
        }
        //model分类查询
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
                    modelVo.setUpdateTime(model.getLastUpdateTime());
                    modelVo.setDeploy(StringUtils.isNotEmpty(model.getDeploymentId()));
                    modelVo.setDeployId(model.getDeploymentId());
                    if (StringUtils.isNotEmpty(model.getDeploymentId())) {
                        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(model.getDeploymentId()).singleResult();
                        if (processDefinition.isSuspended()) { //是否挂起
                            modelVo.setSuspended(true); //挂起中
                        } else {
                            modelVo.setSuspended(false); //激活中
                        }
                        // 待重新部署判断：bpmn 最后修改时间晚于当前部署时间
                        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(model.getDeploymentId()).singleResult();
                        if (deployment != null && model.getLastUpdateTime() != null && deployment.getDeploymentTime() != null) {
                            modelVo.setPendingRedeploy(model.getLastUpdateTime().after(deployment.getDeploymentTime()));
                        } else {
                            modelVo.setPendingRedeploy(false);
                        }
                    } else {
                        modelVo.setPendingRedeploy(false);
                    }
                    //是否部署
                    if (modelVo.getDeploy()) {
                        modelVoList.add(modelVo);
                    }
                });
        return modelVoList;
    }

}
