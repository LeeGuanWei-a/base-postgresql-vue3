package com.basic.workflow.service.wfModel.delete;

import com.basic.common.exception.ServiceException;
import com.basic.common.utils.StringUtils;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import org.flowable.engine.repository.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @description 流程模型删除服务，按模型主键删除模型记录及其对应的部署与部署表单关联
 * @author yanbin
 * @date 2024/03/05
 *
 * <p>语义修正：原实现按 {@code model.getKey()} 把「同 key 的所有版本」连同它们的部署一起删掉，
 * 前端只传了一个 modelId，实际删了一整族，属于误删风险。现在改为：</p>
 * <ul>
 *   <li>只删传入 modelId 对应的那一条模型记录；</li>
 *   <li>只删它自己那次部署（{@code act_re_deployment}），其它版本的部署/历史不动；</li>
 *   <li>该部署上的 {@code wf_deploy_form} 关联一并清理。</li>
 * </ul>
 * <p>如果产品上需要「删掉整条流程线」，请显式传多个 modelId。</p>
 */
@Service
public class WfModelDeleteService extends WfModelInfoBase {

    private static final Logger log = LoggerFactory.getLogger(WfModelDeleteService.class);

    /**
     * @return Object 删除结果，本项目约定返回 null
     * @description 按模型主键删除流程模型，只删该模型自己的那次部署及其部署表单关联，不波及其它版本的部署与历史
     * @params [ids] 要删除的模型主键集合，为空时直接返回
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public Object deleteByIds(Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        for (String id : ids) {
            Model model = repositoryService.createModelQuery().modelId(id).singleResult();
            if (model == null) {
                throw new ServiceException("流程模型不存在！modelId=" + id);
            }
            // 只删这个模型自己的那次部署（cascade=true 会连带它的流程定义/运行实例/历史）
            String deploymentId = model.getDeploymentId();
            if (StringUtils.isNotBlank(deploymentId)) {
                try {
                    repositoryService.deleteDeployment(deploymentId, true);
                } catch (Exception e) {
                    // 部署可能已经被别的入口删掉了，这里不让它阻断模型记录本身的删除
                    log.warn("删除模型 {} 的部署 {} 失败（可能已被删除）：{}", id, deploymentId, e.getMessage());
                }
                deployFormService.deleteDeployFormForDeployId(deploymentId);
            }
            repositoryService.deleteModel(model.getId());
            log.info("流程模型已删除：modelId={}, key={}, version={}", model.getId(), model.getKey(), model.getVersion());
        }
        return null;
    }
}
