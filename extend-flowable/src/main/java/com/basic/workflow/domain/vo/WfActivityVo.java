package com.basic.workflow.domain.vo;

/**
 * @description 流程图用户任务节点视图对象，解析流程 XML 后记录活动标识、名称与状态
 * @author yanbin
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

    /**
     * @description 构造单个流程图节点记录，保存节点标识、名称与状态
     * @params [activityId, name, status] activityId 节点（活动）ID；name 节点名称；status 节点状态
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfActivityVo(String activityId, String name, String status) {
        this.activityId = activityId;
        this.name = name;
        this.status = status;
    }
}
