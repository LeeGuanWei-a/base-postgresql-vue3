package com.basic.workflow.domain.vo;


import lombok.Data;

import java.util.Date;


/**
 * @description 流程抄送视图对象，记录抄送标题、阅读状态、所属流程与发起人等抄送信息
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
public class WfCopyVo {

    private static final long serialVersionUID = 1L;

    /**
     * 抄送主键
     */

    private Long copyId;

    /**
     * 抄送标题
     */

    private String title;
    /**
     * 状态（0未阅 1已阅）
     */
    private String status;
    /**
     * 流程主键
     */

    private String processId;

    /**
     * 流程名称
     */

    private String processName;

    /**
     * 流程分类主键
     */

    private String categoryId;

    /**
     * 部署主键
     */

    private String deploymentId;

    /**
     * 流程实例主键
     */

    private String instanceId;

    /**
     * 任务主键
     */

    private String taskId;

    /**
     * 用户主键
     */

    private Long userId;

    /**
     * 发起人Id
     */

    private Long originatorId;

    /**
     * 发起人名称
     */

    private String originatorName;

    /**
     * 抄送时间（创建时间）
     */

    private Date createTime;
    /**
     * 通知类型
     */
    private String noticeType;


    /**
     * 跳转地址
     */
    private String url;
    /**
     * 展示内容
     */
    private String content;
}
