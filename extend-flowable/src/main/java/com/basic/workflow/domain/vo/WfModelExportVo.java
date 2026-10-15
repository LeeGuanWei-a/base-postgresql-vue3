package com.basic.workflow.domain.vo;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yanbin
 * @description TODO (流程模型对象导出VO )
 * @date 2024/1/16 10:37
 */
@Data
@NoArgsConstructor
public class WfModelExportVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 模型ID
     */
    private String modelId;
    /**
     * 模型Key
     */
    private String modelKey;
    /**
     * 模型名称
     */

    private String modelName;
    /**
     * 分类编码
     */
    private String category;
    /**
     * 流程分类
     */
    private String categoryName;
    /**
     * 模型版本
     */
    private Integer version;
    /**
     * 模型描述
     */
    private String description;
    /**
     * 创建时间
     */
    private Date createTime;
}
