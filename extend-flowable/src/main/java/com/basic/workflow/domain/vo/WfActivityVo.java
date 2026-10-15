package com.basic.workflow.domain.vo;

/**
 * @author yanbin
 * @description TODO (工作流/解析xml流程图用户任务vo)
 * @date 2024/01/29
 */
public class WfActivityVo {
    /**
     * 活动id
     */
    private String activityId;
    /**
     * 活动名称
     */
    private String name;
    /**
     * 活动状态
     */
    private String status;

    public WfActivityVo(String activityId, String name, String status) {
        this.activityId = activityId;
        this.name = name;
        this.status = status;
    }
}
