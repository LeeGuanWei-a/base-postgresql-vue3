package com.basic.workflow.domain.vo;

import lombok.Data;

/**
 * @author yanbin
 * @description TODO (自定义表单组件字段返回)
 * @date 2024/12/06
 */
@Data
public class WfFormFieldVo {
    /**
     * 组件标签值
     */
    private String label;
    /**
     * 组件字段值
     */
    private String value;
}