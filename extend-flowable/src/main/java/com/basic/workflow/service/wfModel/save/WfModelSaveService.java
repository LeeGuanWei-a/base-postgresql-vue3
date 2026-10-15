package com.basic.workflow.service.wfModel.save;

import cn.hutool.core.util.ObjectUtil;
import com.basic.common.utils.JsonUtils;
import com.basic.common.utils.SecurityUtils;
import com.basic.common.utils.StringUtils;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.domain.dto.WfMetaInfoDto;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import org.flowable.engine.repository.Model;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yanbin
 * @description TODO (Flowable流程模型保存 修改) 这里仅保存流程模型，而不保存和修改设计器XMl内容
 * @date 2024/03/05
 */
@Service
public class WfModelSaveService extends WfModelInfoBase {
    /**
     * @return java.lang.Object
     * @description TODO (新增模型信息)
     * @params [modelBo]
     * @author yanbin
     * @date 2023/11/30 13:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public Model insertModel(WfModelBo modelBo) {
        Model model = repositoryService.newModel();
        model.setName(modelBo.getModelName());
        model.setKey(modelBo.getModelKey());
        model.setCategory(modelBo.getCategory());
        String metaInfo = buildMetaInfo(new WfMetaInfoDto(), modelBo.getDescription());
        model.setMetaInfo(metaInfo);
        // 保存流程模型
        repositoryService.saveModel(model);
        return model;
    }

    /**
     * @return java.lang.Object
     * @description TODO (修改模型信息)
     * @params [modelBo]
     * @author yanbin
     * @date 2023/11/30 13:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public Object updateModel(WfModelBo modelBo) {
        // 根据模型Key查询模型信息
        Model model = repositoryService.getModel(modelBo.getModelId());
        if (ObjectUtil.isNull(model)) {
            throw new RuntimeException("流程模型不存在！");
        }
        model.setName(modelBo.getModelName());
        model.setCategory(modelBo.getCategory());
        WfMetaInfoDto metaInfoDto = JsonUtils.parseObject(model.getMetaInfo(), WfMetaInfoDto.class);
        String metaInfo = buildMetaInfo(metaInfoDto, modelBo.getDescription());
        model.setMetaInfo(metaInfo);
        // 保存流程模型
        repositoryService.saveModel(model);
        return null;
    }
    /**
     * @return java.lang.String
     * @description TODO (构建模型扩展信息)
     * @params [metaInfo, description]
     * @author yanbin
     * @date 2024/1/11 9:55
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private String buildMetaInfo(WfMetaInfoDto metaInfo, String description) {
        // 只有非空，才进行设置，避免更新时的覆盖
        if (StringUtils.isNotEmpty(description)) {
            metaInfo.setDescription(description);
        }
        if (StringUtils.isNotEmpty(metaInfo.getCreateUser())) {
            metaInfo.setCreateUser(SecurityUtils.getLoginUser().getUsername());
        }
        return JsonUtils.toJsonString(metaInfo);
    }
}
