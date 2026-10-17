package com.basic.workflow.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 启动流程实例的入参对象，包含流程定义标识、业务系统标识、发起人工号与流程变量
 * @author yanbin
 * @date 2023/10/24
 */
@Data
@Schema(description = "启动流程参数Vo")
public class StartProcessInstanceVo implements Serializable {

    /**
     * 流程定义key 必填
     */
    @Schema(description = "流程定义key", requiredMode = Schema.RequiredMode.REQUIRED)
    private String processDefinitionKey;
    /**
     * 业务系统标识 必填
     */
    @Schema(description = "业务系统标识")
    private String appSn;

    /**
     * 启动流程变量 选填
     */
    @Schema(description = "启动流程变量")
    private Map<String, Object> variables;
    /**
     * 申请人工号 必填
     */
    @Schema(description = "申请人工号  id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currentUserCode;
    /**
     * 申请人名称 必填
     */
    @Schema(description = "申请人名称 name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currentUserName;

    /**
     * 表单显示名称 必填
     */
    @Schema(description = "表单显示名称")
    private String formName;
    /**
     * 流程提交人工号 必填
     */
    @Schema(description = "流程提交人工号 通常和申请人工号一致即可", requiredMode = Schema.RequiredMode.REQUIRED)
    private String creator;

    /**
     * 老的流程实例id
     */
    @Schema(description = "老的流程实例id")
    private String oldProcessInstanceId;

    /**
     * 是否走底表数据
     */
    @Schema(description = "是否走底表数据")
    private boolean flowLevelFlag = true;
    /**
     * 表单数据
     */
    @Schema(description = "表单数据")
    private String formData;
    /**
     * modelId
     */
    @Schema(description = "modelId")
    private String modelId;
    /**
     * 抄送用户Id
     */
    @Schema(description = "抄送用户Id")
    private String copyUserIds;
    /**
     * 附件
     */
    @Schema(description = "附件列表")
    private List<Map<String, Object>> fileList;
    /**
     * 第一个节点为空所选的处理人ID
     */
    @Schema(description = "第一个节点为空所选的处理人ID")
    private List<String> startUserCodes;
    /**
     * 跳转指定的节点
     */
    @Schema(description = "跳转指定的节点")
    private String skipActivityId;
    /**
     * 流转意见
     */
    @Schema(description = "流转意见")
    private String comment;


    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getCurrentUserCode() {
        return currentUserCode;
    }

    public void setCurrentUserCode(String currentUserCode) {
        this.currentUserCode = currentUserCode;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
