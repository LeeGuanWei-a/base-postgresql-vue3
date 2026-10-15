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
 * @description TODO (流程设计业务对象)
 * @date 2024/1/16 10:37
 */
@Data
@ApiModel(value = "WfDesignerBo", description = "流程设计业务对象")
public class WfDesignerBo {

    /**
     * 流程名称
     */
    @NotNull(message = "流程名称", groups = {AddGroup.class, EditGroup.class})
    @ApiModelProperty(value = "流程名称")
    private String name;

    /**
     * 流程分类
     */
    @NotBlank(message = "流程分类", groups = {AddGroup.class, EditGroup.class})
    @ApiModelProperty(value = "流程分类")
    private String category;

    /**
     * XML字符串
     */
    @NotBlank(message = "XML字符串", groups = {AddGroup.class, EditGroup.class})
    @ApiModelProperty(value = "XML字符串")
    private String xml;
}
