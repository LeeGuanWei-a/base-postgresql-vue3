package com.basic.workflow.domain.bo;


import com.basic.common.core.validate.AddGroup;
import com.basic.common.core.validate.EditGroup;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程设计业务对象，封装流程名称、流程分类与流程定义 XML
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
@Schema(description = "流程设计业务对象")
public class WfDesignerBo {

    /**
     * 流程名称
     */
    @NotNull(message = "流程名称", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "流程名称")
    private String name;

    /**
     * 流程分类
     */
    @NotBlank(message = "流程分类", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "流程分类")
    private String category;

    /**
     * XML字符串
     */
    @NotBlank(message = "XML字符串", groups = {AddGroup.class, EditGroup.class})
    @Schema(description = "XML字符串")
    private String xml;
}
