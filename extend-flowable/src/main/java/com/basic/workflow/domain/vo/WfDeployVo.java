package com.basic.workflow.domain.vo;


import lombok.Data;

import java.util.Date;

/**
 * @description 流程部署视图对象，承载流程定义、分类、版本、表单与部署信息
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
public class WfDeployVo {

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
     * 流程定义状态: 1:激活 , 2:中止
     */

    private Boolean suspended;

    /**
     * 部署时间
     */

    private Date deploymentTime;
}
