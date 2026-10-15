package com.basic.workflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yanbin
 * @description TODO (自定义表单渲染)
 * @date 2024/02/28
 */
@Data
@ApiModel(value = "WfTaskFormVo", description = "自定义表单渲染")
public class WfTaskFormVo implements Serializable {
    /**
     * 做区别的id,防止重复内容
     */
    private String id;

    /**
     * 编辑器渲染组件
     */
    private String designerContent;
    /**
     * 表单内容
     */
    private String formContent;
    /**
     * 处理人
     */
    private String assigneeName;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 完结时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishedTime;

}
