package com.basic.workflow.domain.bo;


import com.basic.common.core.validate.AddGroup;
import com.basic.common.core.validate.EditGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yanbin
 * @description TODO (流程模型对象)
 * @date 2024/1/16 10:37
 */
@Data
@ApiModel(value = "WfModelBo", description = "流程模型对象")
public class WfModelBo {
    /**
     * 模型主键
     */
    @NotNull(message = "模型主键不能为空", groups = {EditGroup.class})
    @ApiModelProperty(value = "模型主键id")
    private String modelId;
    /**
     * 模型名称
     */
    @NotNull(message = "模型名称不能为空", groups = {AddGroup.class, EditGroup.class})
    @ApiModelProperty(value = "模型名称")
    private String modelName;
    /**
     * 模型Key
     */
    @NotNull(message = "模型Key不能为空", groups = {AddGroup.class, EditGroup.class})
    @ApiModelProperty(value = "模型Key")
    private String modelKey;
    /**
     * 流程分类
     */
//    @NotBlank(message = "流程分类不能为空", groups = {AddGroup.class, EditGroup.class})
    @ApiModelProperty(value = "流程分类code")
    private String category;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;
    /**
     * 表单类型
     */
    @ApiModelProperty(value = "表单类型")
    private Integer formType;
    /**
     * 表单主键
     */
    @ApiModelProperty(value = "表单主键id")
    private Long formId;
    /**
     * 流程xml
     */
    @ApiModelProperty(value = "流程xml")
    private String bpmnXml;
    /**
     * 是否保存为新版本
     */
    @ApiModelProperty(value = "是否保存为新版本")
    private Boolean newVersion;

    /**
     * 模型名称（在modelName被占用的时候使用）
     */
    @ApiModelProperty(value = "模型名称")
    private String processName;
}
