package com.basic.workflow.service.wfModel.delete;

import cn.hutool.core.util.ObjectUtil;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author yanbin
 * @description TODO (Flowable删除流程模型)
 * @date 2024/03/05
 */
@Service
public class WfModelDeleteService extends WfModelInfoBase {
    /**
     * @return java.lang.Object
     * @description TODO (删除流程模型)
     * @params [ids]
     * @author yanbin
     * @date 2023/11/30 13:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public Object deleteByIds(Collection<String> ids) {
        for (String id : ids) {
            Model model = repositoryService.createModelQuery().modelId(id).singleResult();
            if (ObjectUtil.isNull(model)) {
                throw new RuntimeException("流程模型不存在！");
            }
            String key = model.getKey();
            List<Model> modelList = repositoryService.createModelQuery().modelKey(key).list();
            //流程部署信息
            List<Deployment> deploymentList = repositoryService.createDeploymentQuery()
                    .deploymentKey(key).list();
            //删除流程部署信息 TODO 这里删除包括定义，部署信息，以及历史任务,历史实例信息
            deploymentList.forEach(deployment -> {
                //删除流程部署信息   TODO 这里加true的删除是和此部署有关的所有信息都删除
                repositoryService.deleteDeployment(deployment.getId(), true);
                //删除流程部署表单信息
                deployFormService.deleteDeployFormForDeployId(deployment.getId());
            });
            for (Model models : modelList) {
                //删除流程模型信息
                repositoryService.deleteModel(models.getId());
            }
        }
        return null;
    }
}
