package com.basic.flowable.core;

import lombok.Data;

import java.util.List;
import java.util.Map;


/**
 * @description 表单属性对象，承载表单的标题、模型、尺寸、标签布局、校验规则与表单项配置
 * @author yanbin
 * @date 2023/10/19
 */
@Data
public class FormConf {

    /**
     * 标题
     */
    private String title;
    /**
     * 表单名
     */
    private String formRef;
    /**
     * 表单模型
     */
    private String formModel;
    /**
     * 表单尺寸
     */
    private String size;
    /**
     * 标签对齐
     */
    private String labelPosition;
    /**
     * 标签宽度
     */
    private Integer labelWidth;
    /**
     * 校验模型
     */
    private String formRules;
    /**
     * 栅格间隔
     */
    private Integer gutter;
    /**
     * 禁用表单
     */
    private Boolean disabled = false;
    /**
     * 栅格占据的列数
     */
    private Integer span;
    /**
     * 表单按钮
     */
    private Boolean formBtns = true;
    /**
     * 表单项
     */
    private List<Map<String, Object>> fields;
}
