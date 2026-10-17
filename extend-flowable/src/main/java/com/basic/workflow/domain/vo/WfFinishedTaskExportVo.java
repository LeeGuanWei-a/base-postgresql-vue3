package com.basic.workflow.domain.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
/**
 * @description 已办任务导出的数据对象，按导出口径平铺任务编号、流程名称与节点等字段
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
@NoArgsConstructor
public class WfFinishedTaskExportVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务编号
     */

    private String taskId;

    /**
     * 流程名称
     */

    private String procDefName;

    /**
     * 任务节点
     */

    private String taskName;

    /**
     * 流程版本
     */

    private int procDefVersion;

    /**
     * 流程发起人名称
     */

    private String startUserName;

    /**
     * 接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    private Date createTime;

    /**
     * 审批时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    private Date finishTime;

    /**
     * 任务耗时
     */

    private String duration;
}
