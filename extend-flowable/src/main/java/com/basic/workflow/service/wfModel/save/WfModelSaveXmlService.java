package com.basic.workflow.service.wfModel.save;

import com.basic.common.exception.ServiceException;
import com.basic.common.utils.JsonUtils;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.domain.dto.WfMetaInfoDto;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import com.basic.workflow.service.wfModel.validate.WfProcessComplianceValidator;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.repository.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description 流程模型保存服务，校验并规范化 BPMN XML 后写入模型的 editorSource
 * @author yanbin
 * @date 2024/03/05
 *
 * <p>改造说明：Flowable 7 起官方移除了 {@code flowable-json-converter}（最后版本 6.8.1），
 * 8.0.0 不再提供 JSON↔BPMN 转换能力。原实现「XML → BpmnModel → JSON 存 editorSource」在 8.0
 * 上会拿到 null 并 NPE。现改为：<b>editorSource 直接存规范化后的 BPMN 2.0 XML</b>。</p>
 *
 * <p>规范化做两件事，保证后续「按 modelKey 查流程定义 / 部署 / 发起」能串起来：</p>
 * <ol>
 *   <li>把 BPMN 的 {@code <process id>} 改成 {@code modelKey + ".bpmn"}，
 *       与 {@code WfModelStartService} 里 {@code key + SUFFIX} 的查询口径一致；</li>
 *   <li>把流程名写回 BPMN，避免部署出来的流程定义没名字。</li>
 * </ol>
 */
@Service
public class WfModelSaveXmlService extends WfModelInfoBase {

    private static final Logger log = LoggerFactory.getLogger(WfModelSaveXmlService.class);

    private static final String BPMN_EXTENSION = ".bpmn";

    /** 保存前的流程合规校验器 */
    @Autowired
    private WfProcessComplianceValidator processComplianceValidator;

    /**
     * @return boolean 保存成功返回 true
     * @description 保存流程模型：校验入参、解析并修复 BPMN XML、合规校验、规范化改写后落库并把 XML 写入 editorSource
     * @params [modelBo] 模型保存参数，含模型ID、BPMN XML、流程名与新版本标记
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean save(WfModelBo modelBo) {
        String modelId = modelBo.getModelId();
        if (StringUtils.isBlank(modelId)) {
            throw new ServiceException("流程模型ID不能为空");
        }
        Model processModel = repositoryService.getModel(modelId);
        if (processModel == null) {
            throw new ServiceException("流程模型不存在！");
        }
        String bpmnXml = modelBo.getBpmnXml();
        if (StringUtils.isBlank(bpmnXml)) {
            throw new ServiceException("流程图为空，请先设计流程图！");
        }
        if (looksLikeJsonModel(bpmnXml)) {
            throw new ServiceException("提交内容看起来是旧版设计器 JSON，本接口只接受 BPMN 2.0 XML");
        }
        // 1. 解析 XML（安全解析，禁 DTD/外部实体）
        BpmnModel bpmnModel = parseBpmnModel(bpmnXml);
        // 2. 修复扩展元素长文本被截断的问题（详见方法注释）
        repairExtensionElementText(bpmnXml, bpmnModel);
        // 3. 保存前合规校验：不通过直接抛异常阻断保存（事务回滚），
        //    避免"带病流程"进库后到发起流程时才连环报错
        List<String> complianceProblems = processComplianceValidator.validate(bpmnModel);
        if (!complianceProblems.isEmpty()) {
            throw new ServiceException(WfProcessComplianceValidator.toMessage(complianceProblems));
        }
        // 4. 流程名
        String processName = StringUtils.isNotBlank(modelBo.getProcessName())
                ? modelBo.getProcessName()
                : (StringUtils.isNotBlank(bpmnModel.getMainProcess().getName())
                    ? bpmnModel.getMainProcess().getName()
                    : processModel.getName());
        bpmnModel.getMainProcess().setName(processName);
        // 5. 流程定义 KEY = modelKey + ".bpmn"，发起流程时按这个口径查定义
        bpmnModel.getMainProcess().setId(processModel.getKey() + BPMN_EXTENSION);
        // 6. 规范化 XML
        byte[] bpmnXmlBytes = toBpmnXmlBytes(bpmnModel);
        // 7. 保存/新增模型记录
        Model newModel;
        if (Boolean.TRUE.equals(modelBo.getNewVersion())) {
            newModel = repositoryService.newModel();
            newModel.setName(processName);
            newModel.setKey(processModel.getKey());
            newModel.setCategory(processModel.getCategory());
            newModel.setVersion(processModel.getVersion() + 1);
            newModel.setMetaInfo(buildMetaInfo(processModel.getMetaInfo(), bpmnModel));
        } else {
            newModel = processModel;
            newModel.setName(processName);
            newModel.setMetaInfo(buildMetaInfo(processModel.getMetaInfo(), bpmnModel));
        }
        repositoryService.saveModel(newModel);
        // 8. 保存 BPMN XML（editorSource 里存的就是部署时用的那份 XML）
        repositoryService.addModelEditorSource(newModel.getId(), bpmnXmlBytes);
        log.info("流程模型保存成功：modelId={}, key={}, resourceSize={}B",
                newModel.getId(), newModel.getKey(), bpmnXmlBytes.length);
        return true;
    }

    /**
     * @return String 模型扩展信息 JSON，序列化失败时返回原 metaInfo
     * @description 解析旧的模型扩展信息并写入开始节点的表单 key，重组后序列化成 JSON
     * @params [oldMetaInfo, bpmnModel] oldMetaInfo 原扩展信息 JSON；bpmnModel 流程模型
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private String buildMetaInfo(String oldMetaInfo, BpmnModel bpmnModel) {
        WfMetaInfoDto metaInfoDto = new WfMetaInfoDto();
        if (StringUtils.isNotBlank(oldMetaInfo)) {
            try {
                WfMetaInfoDto parsed = JsonUtils.parseObject(oldMetaInfo, WfMetaInfoDto.class);
                if (parsed != null) {
                    metaInfoDto = parsed;
                }
            } catch (Exception e) {
                log.warn("流程模型 metaInfo 解析失败，将重建：{}", e.getMessage());
            }
        }
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        metaInfoDto.setFormId(startEvent == null ? null : startEvent.getFormKey());
        try {
            return JsonUtils.toJsonString(metaInfoDto);
        } catch (Exception e) {
            log.warn("流程模型 metaInfo 序列化失败，忽略：{}", e.getMessage());
            return oldMetaInfo;
        }
    }

    /**
     * @description 用 dom4j 从原始 XML 取出扩展元素的完整文本，回填被 StAX 分片截断的 BpmnModel 扩展元素文本
     * 背景：Flowable 的 BpmnXMLUtil#parseExtensionElement 对每段 CHARACTERS / CDATA 事件调用 setElementText 覆盖赋值，
     * 而不是拼接多段文本；当扩展元素内容是纯文本且含有 ']' 时（如 idmCandidateUsers 里存候选人 JSON 含 "roles":[]），
     * JDK 的 StAX 会把整段拆成多段 CHARACTERS 事件，只有最后一段被保留，表现为「节点选了候选人，保存后重新打开是空的」。
     * 修复：用 dom4j 从「前端原样提交的 XML」里取出完整文本（dom4j 会拼接所有文本/CDATA 片段），
     * 再按 userTask 的 activityId + 扩展元素名写回 BpmnModel。
     * @params [bpmnXml, bpmnModel] bpmnXml 前端提交的原始 BPMN XML；bpmnModel Flowable 解析后的模型
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @return Map<String, Map<String, String>> key 为 userTask 的 id，value 为扩展元素名到完整文本的映射
     * @description 用 dom4j 逐层遍历原始 XML，收集每个 userTask 下扩展元素的完整文本，避免使用依赖 jaxen 的 XPath
     * 注意：这里刻意不用 XPath（selectNodes / selectSingleNode）。dom4j 的 XPath 依赖 jaxen，运行环境没有 jaxen，
     * 一旦调用会抛 NoClassDefFoundError: org/jaxen/JaxenException，导致保存流程 500；因此改用 elements() 逐层遍历，只依赖 dom4j 自身。
     * @params [bpmnXml] 原始 BPMN XML
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @description 递归遍历 XML 元素查找 userTask，并把其扩展元素文本收集进结果集
     * @params [element, result] element 当前遍历到的 XML 元素；result 收集结果的映射
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @description 读取单个 userTask 下 extensionElements 中每个扩展元素的文本并写入结果集
     * @params [userTask, result] userTask 用户任务元素；result 收集结果的映射
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
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
     * @return String 去掉命名空间前缀后的元素名，入参为空时返回 null
     * @description 去掉元素名上可能存在的命名空间前缀
     * @params [name] 可能带命名空间前缀的元素名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private String localName(String name) {
        if (name == null) {
            return null;
        }
        int index = name.indexOf(':');
        return index < 0 ? name : name.substring(index + 1);
    }
}
