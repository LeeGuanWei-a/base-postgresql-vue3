package com.basic.workflow.service.wfModel.save;

import com.basic.common.exception.ServiceException;
import com.basic.common.utils.JsonUtils;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.domain.dto.WfMetaInfoDto;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import com.basic.workflow.service.wfModel.validate.WfProcessComplianceValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.repository.Model;
import org.flowable.ui.common.util.XmlUtil;
import org.flowable.ui.modeler.service.ConverterContext;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (Flowable保存流程模型信息，保存设计器XMl) (工作流流程保存实现类) 这里运用了flowable-ui
 * @date 2024/03/05
 */
@Service
public class WfModelSaveXmlService extends WfModelInfoBase {
    @Autowired
    private ModelService modelService;
    @Autowired
    protected BpmnXMLConverter bpmnXMLConverter;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    protected BpmnJsonConverter bpmnJsonConverter;
    /** 保存前的流程合规校验器 */
    @Autowired
    private WfProcessComplianceValidator processComplianceValidator;
    private static final String BPMN_EXTENSION = ".bpmn";
    private static final String BPMN20_XML_EXTENSION = ".bpmn20.xml";
    private static final Logger log = LoggerFactory.getLogger(WfModelSaveXmlService.class);

    /**
     * @return boolean
     * @description TODO (流程保存)
     * @params [modelBo]
     * @author yanbin
     * @date 2023/11/1 10:33
     * @remark (改代码时写明修改时间修改人修改原因)
     * 2024-3-18 LJY update: WfModelBo新增processName用于保存模型名称，这里的modelName模型名称填的是key，被占用所以用不了
     */
    public boolean save(WfModelBo modelBo) {
        //TODO 1. 传入的modelId
        String modelId = modelBo.getModelId();
        //TODO 2.传入的modelName
        String fileName = modelBo.getModelName();
        //TODO 3. 传入的model
        Model processModel = repositoryService.getModel(modelId);
        //TODO 3.1 解析model
        ByteArrayInputStream modelStream = new ByteArrayInputStream(modelBo.getBpmnXml().getBytes());
        // TODO 3.2 modelKey
        String modelKey = processModel.getKey() + BPMN_EXTENSION;
        if (StringUtils.isBlank(fileName)) {
            fileName = processModel.getKey() + BPMN_EXTENSION;
        }
        if (fileName != null
                && (fileName.endsWith(BPMN_EXTENSION) || fileName.endsWith(BPMN20_XML_EXTENSION))) {
            try {
                // TODO 4. modeler ui  xml工具类方法
                XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
                InputStreamReader xmlIn = new InputStreamReader(modelStream, StandardCharsets.UTF_8);
                XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
                // TODO 5. 转为bpmnModel
                BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(xtr);
                // TODO 5.1 修复扩展元素长文本被截断的问题（详见方法注释）
                repairExtensionElementText(modelBo.getBpmnXml(), bpmnModel);
                // TODO 5.2 保存前合规校验：不通过直接抛异常阻断保存（事务回滚），
                //          并把问题清单返回前端，避免"带病流程"进库后到发起流程时才连环报错
                List<String> complianceProblems = processComplianceValidator.validate(bpmnModel);
                if (!complianceProblems.isEmpty()) {
                    throw new ServiceException(WfProcessComplianceValidator.toMessage(complianceProblems));
                }
                bpmnModel.getMainProcess().setId(modelKey);
                bpmnModel.setTargetNamespace(BaseBpmnJsonConverter.NAMESPACE);
                // TODO 6.1 判断参数null或者其size等于0
                if (CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
                    throw new RuntimeException("请重新操作！");
                }
                // TODO 7. modeler ui 的方法
                //  说明：原实现调用 ModelService.getModelsByModelType(...) 预加载 DMN 决策表模型。
                //  该调用会访问 flowable-ui-modeler 的 ACT_DE_MODEL 表，而本项目只依赖
                //  flowable-ui-modeler-logic、并未创建该表，导致保存流程时抛
                //  relation "act_de_model" does not exist。
                //  本项目不使用 DMN 决策表，故不再预加载；ConverterContext 仍保留，
                //  用于 BPMN 与编辑器 JSON 的互转。
                ConverterContext converterContext = new ConverterContext(modelService, objectMapper);
                // TODO 8. 转json    改的原生方法--->拓展内容
                if(!Strings.isNullOrEmpty(modelBo.getProcessName())){
                    bpmnModel.getMainProcess().setName(modelBo.getProcessName());
                }
                ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel, converterContext);
                // TODO 9. xml前端传的
                String bpm = modelNode.toString();
                // TODO 10.保存
                Model newModel;
                String processName = bpmnModel.getMainProcess().getName();
                if (Boolean.TRUE.equals(modelBo.getNewVersion())) {
                    newModel = repositoryService.newModel();
                    //model名称
                    if(Strings.isNullOrEmpty(modelBo.getProcessName())){
                        newModel.setName(processName);
                    }else{
                        newModel.setName(modelBo.getProcessName());
                    }
                    //model key
                    newModel.setKey(processModel.getKey());
                    // 分类
                    newModel.setCategory(processModel.getCategory());
                    // 模型扩展信息
                    WfMetaInfoDto metaInfoDto = JsonUtils.parseObject(processModel.getMetaInfo(), WfMetaInfoDto.class);
                    // TODO  提取自定义表单id
                    if (modelNode.get("childShapes").get(0).get("properties").get("formkeydefinition") != null) {
                        Long formId = modelNode.get("childShapes").get(0).get("properties").get("formkeydefinition").asLong();
                        metaInfoDto.setFormId(formId);
                    }else{
                        metaInfoDto.setFormId(null);
                    }
                    String metaInfo = JsonUtils.toJsonString(metaInfoDto);
                    newModel.setMetaInfo(metaInfo);
                    //版本
                    newModel.setVersion(processModel.getVersion() + 1);
                    // 所属系统
//                    newModel.setTenantId("");
                } else {
                    newModel = processModel;
                    // 设置流程名称
                    newModel.setName(processName);
                }
                //TODO 11. 保存流程模型
                repositoryService.saveModel(newModel);
                // TODO 12. 保存 BPMN XML
                byte[] bpmnXmlBytes = StringUtils.getBytes(bpm, StandardCharsets.UTF_8);
                repositoryService.addModelEditorSource(newModel.getId(), bpmnXmlBytes);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 修复扩展元素长文本被截断的问题。
     *
     * <p>背景：Flowable 的 org.flowable.bpmn.converter.util.BpmnXMLUtil#parseExtensionElement
     * 对每个 CHARACTERS / CDATA 事件调用的是 extensionElement.setElementText(text)（覆盖赋值），
     * 而不是把多段文本拼接起来。当扩展元素内容是「纯文本」且文本中含有 ']' 时
     * （例如 idmCandidateUsers 里存放的候选人 JSON 含 "roles":[]），
     * JDK 的 StAX 解析器会把这整段文本拆成多个 CHARACTERS 事件（实测拆成 2 段），
     * 于是只有最后一段被保留 —— 候选人 JSON 只剩尾部一截，
     * 表现为「节点上选了候选人，保存后重新打开候选人是空的」。</p>
     *
     * <p>修复方式：转换完成后，用 dom4j 从「前端原样提交的 XML」里取出扩展元素的完整文本
     * （dom4j 会把所有文本 / CDATA 片段拼接起来，不受 StAX 分片影响），
     * 再按 userTask 的 activityId + 扩展元素名写回 BpmnModel。</p>
     *
     * @param bpmnXml   前端提交的原始 BPMN XML
     * @param bpmnModel Flowable 解析后的模型（其中的扩展元素文本可能已被截断）
     */
    private void repairExtensionElementText(String bpmnXml, BpmnModel bpmnModel) {
        if (StringUtils.isBlank(bpmnXml) || bpmnModel == null) {
            return;
        }
        Map<String, Map<String, String>> fullTextMap = parseExtensionElementText(bpmnXml);
        if (fullTextMap.isEmpty()) {
            return;
        }
        Collection<UserTask> userTasks = ModelUtils.getAllUserTaskEvent(bpmnModel);
        int repaired = 0;
        for (UserTask userTask : userTasks) {
            Map<String, String> textMap = fullTextMap.get(userTask.getId());
            if (textMap == null || userTask.getExtensionElements() == null) {
                continue;
            }
            for (Map.Entry<String, List<ExtensionElement>> extEntry : userTask.getExtensionElements().entrySet()) {
                // 扩展元素在 Map 里的 key 可能是「名字」也可能是「前缀:名字」，统一按 localName 比对
                String fullText = textMap.get(localName(extEntry.getKey()));
                if (fullText == null) {
                    continue;
                }
                for (ExtensionElement element : extEntry.getValue()) {
                    if (!fullText.equals(element.getElementText())) {
                        element.setElementText(fullText);
                        repaired++;
                    }
                }
            }
        }
        if (repaired > 0) {
            log.warn("流程模型扩展元素文本被 Flowable 解析器截断，已用原始 XML 修复 {} 处", repaired);
        }
    }

    /**
     * 用 dom4j 解析原始 XML，取出每个 userTask 下所有扩展元素的完整文本。
     *
     * <p>注意：这里刻意不使用 XPath（selectNodes / selectSingleNode）。
     * dom4j 的 XPath 实现依赖 jaxen，而运行环境里没有 jaxen，
     * 一旦调用会抛 NoClassDefFoundError: org/jaxen/JaxenException 导致保存流程 500。
     * 因此改用 elements() 逐层遍历，只依赖 dom4j 自身。</p>
     *
     * @param bpmnXml 原始 BPMN XML
     * @return key = userTask 的 id，value = (扩展元素名 -> 完整文本)
     */
    private Map<String, Map<String, String>> parseExtensionElementText(String bpmnXml) {
        Map<String, Map<String, String>> result = new HashMap<>(16);
        try {
            Document document = DocumentHelper.parseText(bpmnXml);
            collectUserTaskExtensionText(document.getRootElement(), result);
        } catch (Exception e) {
            // 解析失败不能影响正常保存流程，退回 Flowable 自带解析结果
            log.warn("解析原始 BPMN XML 扩展元素失败，跳过截断修复：{}", e.getMessage());
        }
        return result;
    }

    /**
     * 递归查找 userTask，并收集其扩展元素文本（不使用 XPath）。
     */
    private void collectUserTaskExtensionText(Element element, Map<String, Map<String, String>> result) {
        if (element == null) {
            return;
        }
        if ("userTask".equals(localName(element.getName()))) {
            collectExtensionTextOfUserTask(element, result);
            // userTask 内部不会再有 userTask，无需继续下钻
            return;
        }
        for (Object childObj : element.elements()) {
            if (childObj instanceof Element) {
                collectUserTaskExtensionText((Element) childObj, result);
            }
        }
    }

    /**
     * 取单个 userTask 下 extensionElements 里每个扩展元素的完整文本。
     */
    private void collectExtensionTextOfUserTask(Element userTask, Map<String, Map<String, String>> result) {
        String activityId = userTask.attributeValue("id");
        if (StringUtils.isBlank(activityId)) {
            return;
        }
        Map<String, String> textMap = new HashMap<>(16);
        for (Object childObj : userTask.elements()) {
            if (!(childObj instanceof Element)) {
                continue;
            }
            Element child = (Element) childObj;
            if (!"extensionElements".equals(localName(child.getName()))) {
                continue;
            }
            for (Object extObj : child.elements()) {
                if (!(extObj instanceof Element)) {
                    continue;
                }
                Element extensionElement = (Element) extObj;
                String text = extensionElement.getText();
                if (StringUtils.isNotBlank(text)) {
                    textMap.put(localName(extensionElement.getName()), text.trim());
                }
            }
        }
        if (!textMap.isEmpty()) {
            result.put(activityId, textMap);
        }
    }

    /**
     * 去掉元素名上的命名空间前缀。
     * dom4j 是否 namespace aware 会决定 getName() 里带不带前缀，这里统一兜底。
     */
    private String localName(String name) {
        if (name == null) {
            return null;
        }
        int index = name.indexOf(':');
        return index < 0 ? name : name.substring(index + 1);
    }
}
