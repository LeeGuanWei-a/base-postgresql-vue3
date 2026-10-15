package com.basic.workflow.domain.dto;

import lombok.Data;

/**
 * @author yanbin
 * @description TODO (流程表单dto)
 * @date 2024/1/16 10:37
 */
@Data
public class WfMetaInfoDto {

    /**
     * 创建者（username）
     */
    private String createUser;

    /**
     * 流程描述
     */
    private String description;
    /**
     * 表单类型
     */
    private Integer formType;
    /**
     * 表单编号
     */
    private Long formId;

}
