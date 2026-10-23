package com.basic.workflow.service.wfModel.queryXml;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.basic.common.exception.ServiceException;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @description 流程模型 XML 查询服务，按模型主键取出保存的 BPMN 2.0 XML 供前端设计器回显
 * @author yanbin
 * @date 2024/03/05
 *
 * <p>改造说明：Flowable 7 起官方移除了 {@code flowable-json-converter}（最后版本 6.8.1），
 * 8.0.0 里已经没有任何 {@code org.flowable.editor.language.json.converter} 实现。
 * 因此这里不再做「JSON 设计器模型 → BPMN」的转换，模型库里直接存 BPMN 2.0 XML，
 * 本方法原样返回存储的 XML 供前端设计器（bpmn-js）回显。</p>
 */
@Service
public class WfModelQueryXmlService extends WfModelInfoBase {

    /**
     * @return String 该模型保存的 BPMN XML，未保存过内容时返回 null
     * @description 取出模型编辑器里存的 BPMN XML 供前端设计器回显，遇到旧版设计器 JSON 或自定义属性缺失时分别抛错或补齐
     * @params [modelId] 流程模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String queryBpmnXmlById(String modelId) throws IOException, DocumentException {
        byte[] bytes = repositoryService.getModelEditorSource(modelId);
        if (ArrayUtil.isEmpty(bytes)) {
            return null;
        }
        String stored = StrUtil.utf8Str(bytes);
        if (WfModelInfoBase.looksLikeJsonModel(stored)) {
            throw new ServiceException("该流程模型存的是旧版设计器 JSON 格式，Flowable 8 已不再支持；"
                    + "请用 BPMN XML 重新保存一次模型");
        }
        // 网关等节点上自定义属性（如 flowable:assigneeType）的兜底修复
        return ModelUtils.addAttributeWithXmlDocument(stored);
    }
}
