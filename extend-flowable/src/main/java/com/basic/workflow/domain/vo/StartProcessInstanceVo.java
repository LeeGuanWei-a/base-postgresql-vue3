package com.basic.workflow.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (/启动流程参数Vo)
 * @date 2023/10/24
 */
@Data
@ApiModel(value = "StartProcessInstanceVo", description = "启动流程参数Vo")
public class StartProcessInstanceVo implements Serializable {

    /**
     * 流程定义key 必填
     */
    @ApiModelProperty(value = "流程定义key", required = true)
    private String processDefinitionKey;
    /**
     * 业务系统标识 必填
     */
    @ApiModelProperty(value = "业务系统标识")
    private String appSn;

    /**
     * 启动流程变量 选填
     */
    @ApiModelProperty(value = "启动流程变量")
    private Map<String, Object> variables;
    /**
     * 申请人工号 必填
     */
    @ApiModelProperty(value = "申请人工号  id", required = true)
    private String currentUserCode;
    /**
     * 申请人名称 必填
     */
    @ApiModelProperty(value = "申请人名称 name", required = true)
    private String currentUserName;

    /**
     * 表单显示名称 必填
     */
    @ApiModelProperty(value = "表单显示名称")
    private String formName;
    /**
     * 流程提交人工号 必填
     */
    @ApiModelProperty(value = "流程提交人工号 通常和申请人工号一致即可", required = true)
    private String creator;

    /**
     * 老的流程实例id
     */
    @ApiModelProperty(value = "老的流程实例id")
    private String oldProcessInstanceId;

    /**
     * 是否走底表数据
     */
    @ApiModelProperty(value = "是否走底表数据")
    private boolean flowLevelFlag = true;
    /**
     * 表单数据
     */
    @ApiModelProperty(value = "表单数据")
    private String formData;
    /**
     * modelId
     */
    @ApiModelProperty(value = "modelId")
    private String modelId;
    /**
     * 抄送用户Id
     */
    @ApiModelProperty(value = "抄送用户Id")
    private String copyUserIds;
    /**
     * 附件
     */
    @ApiModelProperty(value = "附件列表")
    private List<Map<String, Object>> fileList;
    /**
     * 第一个节点为空所选的处理人ID
     */
    @ApiModelProperty(value = "第一个节点为空所选的处理人ID")
    private List<String> startUserCodes;
    /**
     * 跳转指定的节点
     */
    @ApiModelProperty(value = "跳转指定的节点")
    private String skipActivityId;
    /**
     * 流转意见
     */
    @ApiModelProperty(value = "流转意见")
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
