package com.basic.workflow.domain.vo;

import lombok.Data;

/**
 * @description 自定义表单组件字段视图对象，返回组件的标签与对应取值
 * @author yanbin
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