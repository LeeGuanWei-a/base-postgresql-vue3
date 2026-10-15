package com.basic.converter;

import com.basic.flowable.utils.ExtansionPropertiesUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverterContext;
import org.flowable.editor.language.json.converter.UserTaskJsonConverter;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author yanbin
 * @description TODO (flowable拓展属性配置)--》前端设置的拓展属性在这里配置保存  前端配置flowableDescriptor.json
 * @date 2023/10/18
 */
public class BruceUserTaskJsonConverter extends UserTaskJsonConverter {
    /**
     * 固定值，身份存储
     */
    public static final String ASSIGNEE_TYPE = "assigneeType";
    /**
     * 表达式json
     */
    public static final String IDM_AUTHORITY = "idmAuthority";
    /**
     * 处理人json
     */
    public static final String IDM_ASSIGNEE = "idmAssignee";
    /**
     * 候选组JSON
     */
    public static final String IDM_CANDIDATE_GROUPS = "idmCandidateGroups";
    /**
     * 候选人员JSON
     */
    public static final String IDM_CANDIDATE_USERS = "idmCandidateUsers";
    /**
     * 是否编辑
     */
    public static final String IS_EDITDATA = "isEditData";
    /**
     * 节点类型
     */
    public static final String NODE_TYPE = "nodeType";
    /**
     * 下一条线
     */
    public static final String NEXT_SEQUENCE_FLOW_LABEL = "nextSequenceFlow";
    /**
     * 下一用户
     */
    public static final String NEXT_USER_LABEL = "nextUser";
    /**
     * 表单数据
     */
    public static final String FORM_DATA_LABEL = "formData";
    /**
     * 是否自动跳过
     */
    public static final String IS_SKIP = "isSkip";
    /**
     * 交替节点
     */
    public static final String IS_ROLE_REPLACE = "isRoleReplace";
    /**
     * 是否需要二次审批
     */
    public static final String IS_EXAMINED = "isExamined";
    /**
     * 强制撤回
     */
    public static final String IS_REVOKE = "isRevoke";
    /**
     * 按钮权限
     */
    public static final String IDM_PERMISSION = "idmPermission";
    /**
     * 人员筛选
     */
    public static final String IS_USER_SCREEN = "isUserScreen";
    /**
     * 组织筛选
     */
    public static final String IS_ORG_SCREEN = "isOrgScreen";

    /**
     * 部门筛选or组织筛选
     */
    public static final String IS_DEPT_OR_ORG = "isDeptOrOrg";
    /**
     * 处理人为空跳过
     */
    public static final String IS_NULL_SKIP = "isNullSkip";

    static void customFillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap, Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_TASK_USER, BruceUserTaskJsonConverter.class);
    }

    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(UserTask.class, BruceUserTaskJsonConverter.class);
    }

    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement, BpmnJsonConverterContext converterContext) {
        super.convertElementToJson(propertiesNode, baseElement, converterContext);
        if (baseElement instanceof UserTask) {
            if (!CollectionUtils.isEmpty(baseElement.getExtensionElements())) {
                baseElement.getExtensionElements().forEach((s, elements) -> elements.forEach(extensionElement -> {
                    if (ASSIGNEE_TYPE.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IDM_ASSIGNEE.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IDM_CANDIDATE_GROUPS.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IDM_PERMISSION.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IDM_CANDIDATE_USERS.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IDM_AUTHORITY.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IS_DEPT_OR_ORG.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IS_USER_SCREEN.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IS_ORG_SCREEN.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IS_EDITDATA.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IS_SKIP.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IS_NULL_SKIP.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IS_ROLE_REPLACE.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IS_EXAMINED.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (IS_REVOKE.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (NODE_TYPE.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (NEXT_SEQUENCE_FLOW_LABEL.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (NEXT_USER_LABEL.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                    if (FORM_DATA_LABEL.equals(extensionElement.getName())) {
                        this.setPropertyValue(extensionElement.getName(), extensionElement.getElementText(), propertiesNode);
                    }
                }));
            }
        }
    }

    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap, BpmnJsonConverterContext converterContex) {
        FlowElement flowElement = super.convertJsonToElement(elementNode, modelNode, shapeMap, converterContex);
        this.addExtansionPropertiesElement(flowElement, elementNode);
        return flowElement;
    }

    private void addExtansionPropertiesElement(FlowElement flowElement, JsonNode elementNode) {
        if (flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, ASSIGNEE_TYPE);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IDM_ASSIGNEE);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IDM_AUTHORITY);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IDM_CANDIDATE_GROUPS);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IDM_CANDIDATE_USERS);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IDM_PERMISSION);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IS_EDITDATA);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IS_SKIP);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IS_USER_SCREEN);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IS_DEPT_OR_ORG);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IS_ORG_SCREEN);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IS_ROLE_REPLACE);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IS_EXAMINED);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IS_NULL_SKIP);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, IS_REVOKE);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, NODE_TYPE);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, NEXT_SEQUENCE_FLOW_LABEL);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, NEXT_USER_LABEL);
            ExtansionPropertiesUtil.addExtansionPropertiesElement(elementNode, userTask, FORM_DATA_LABEL);
        }
    }

//
//    private void addExtansionPropertiesElement(JsonNode objectNode, UserTask userTask, String name) {
//        JsonNode expansionNode = this.getProperty(name, objectNode);
//        if (expansionNode instanceof TextNode){
//            if (expansionNode != null && StringUtils.isNotBlank(expansionNode.asText())){
//                ExtensionElement extensionElement = new ExtensionElement();
//                extensionElement.setName(name);
//                extensionElement.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
//                extensionElement.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
//                extensionElement.setElementText(expansionNode.asText());
//                userTask.addExtensionElement(extensionElement);
//            }
//        }
//    }

//    @Override
//    protected void addExtensionElement(String name, String elementText, UserTask task) {
//        ExtensionElement extensionElement = new ExtensionElement();
//        extensionElement.setNamespace(NAMESPACE);
//        extensionElement.setNamespacePrefix("flowable");
//        extensionElement.setName(name);
//        extensionElement.setElementText(elementText);
//        task.addExtensionElement(extensionElement);
//    }
}
