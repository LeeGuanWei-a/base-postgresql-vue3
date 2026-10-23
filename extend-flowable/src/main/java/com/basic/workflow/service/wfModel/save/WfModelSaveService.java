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
 * @description 流程模型保存服务，负责新建与更新模型的名称、分类和扩展信息，不涉及设计器 XML 内容
 * @author yanbin
 * @date 2024/03/05
 */
@Service
public class WfModelSaveService extends WfModelInfoBase {
    /**
     * @return Model 新建的流程模型对象
     * @description 用模型业务对象创建新流程模型，写入基本信息与扩展信息后保存
     * @params [modelBo] 模型业务对象，含模型名称、标识、分类与描述
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return Object 固定返回 null，更新结果无需返回给调用方
     * @description 按模型主键更新模型名称、分类与扩展信息，模型不存在时抛出业务异常
     * @params [modelBo] 模型业务对象，含模型主键与待更新字段
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return String 序列化后的模型扩展信息 JSON
     * @description 按需填充描述与创建人后，把模型扩展信息序列化成 JSON
     * @params [metaInfo, description] 模型扩展信息对象；模型描述，为空时保留原有描述
     * @author liguanwei
     * @date 2026/10/17 20:52
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
