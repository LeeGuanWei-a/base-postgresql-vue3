package com.basic.workflow.domain.bo;


import com.basic.common.core.validate.AddGroup;
import com.basic.common.core.validate.EditGroup;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程模型业务对象，承载模型的标识、名称、分类等参数，用于模型新增与修改时的校验入参
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
@Schema(description = "流程模型对象")
public class WfModelBo {
    /**
     * 模型主键
     */
    @NotNull(message = "模型主键不能为空", groups = {EditGroup.class})
    @Schema(description = "模型主键id")
    private String modelId;
    /**
     * 模型名称
     */
    @NotNull(message = "模型名称不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "模型名称")
    private String modelName;
    /**
     * 模型Key
     */
    @NotNull(message = "模型Key不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "模型Key")
    private String modelKey;
    /**
     * 流程分类
     */
//    @NotBlank(message = "流程分类不能为空", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "流程分类code")
    private String category;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;
    /**
     * 表单类型
     */
    @Schema(description = "表单类型")
    private Integer formType;
    /**
     * 表单主键
     */
    @Schema(description = "表单主键id")
    private String formId;
    /**
     * 流程xml
     */
    @Schema(description = "流程xml")
    private String bpmnXml;
    /**
     * 是否保存为新版本
     */
    @Schema(description = "是否保存为新版本")
    private Boolean newVersion;

    /**
     * 模型名称（在modelName被占用的时候使用）
     */
    @Schema(description = "模型名称")
    private String processName;
}
