package com.basic.workflow.service.wfModel.queryXml;

import cn.hutool.core.util.StrUtil;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.dom4j.DocumentException;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author yanbin
 * @description TODO (FlowableXMl预览查询)
 * @date 2024/03/05
 */
@Service
public class WfModelQueryXmlService extends WfModelInfoBase {
    /**
     * @return java.lang.String
     * @description TODO (预览xml流程图)
     * @params [modelId]
     * @author yanbin
     * @date 2023/11/8 11:50
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String queryBpmnXmlById(String modelId) throws IOException, DocumentException {
        // 获取保存的json转为的Bytes
        byte[] Bytes = repositoryService.getModelEditorSource(modelId);
        if (Bytes == null) {
            return null;
        }
        // JSON
        ObjectNode modelNode =
                (ObjectNode) new ObjectMapper().readTree(StrUtil.utf8Str(Bytes)); // readTree(这里面是json)
        // bpmnModel
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode); // 转model
        //    bpmnModel转为byte
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model); // 转xml
        //    返回byte转为xml
        //网关X消失
        String xml = ModelUtils.addAttributeWithXmlDocument(StrUtil.utf8Str(bpmnBytes));
        return xml;
    }

}
