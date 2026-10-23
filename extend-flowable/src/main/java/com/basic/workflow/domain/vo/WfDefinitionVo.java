package com.basic.workflow.domain.vo;


import lombok.Data;

import java.util.Date;


/**
 * @description 流程定义视图对象，输出流程定义的标识、名称、Key 与分类编码等展示信息
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
public class WfDefinitionVo {

    private static final long serialVersionUID = 1L;

    /**
     * 流程定义ID
     */

    private String definitionId;

    /**
     * 流程名称
     */

    private String processName;

    /**
     * 流程Key
     */

    private String processKey;

    /**
     * 分类编码
     */

    private String category;

    /**
     * 版本
     */

    private Integer version;

    /**
     * 表单ID
     */

    private Long formId;

    /**
     * 表单名称
     */

    private String formName;

    /**
     * 部署ID
     */

    private String deploymentId;

    /**
     * 流程是否暂停（true:挂起 false:激活 ）
     */

    private Boolean suspended;

    /**
     * 部署时间
     */

    private Date deploymentTime;
}
