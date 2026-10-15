package com.basic.workflow.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanbin
 * @description TODO (流程节点vo) 流程图中的节点
 * @date 2024/01/30
 */
@Data
public class FlowElementVo implements Serializable {

    /**
     * 节点类型
     */
    private String type;

    /**
     * 跳转条件
     */
    private String condition;

    /**
     * 当前节点所需审批人id
     */
    private List<Long> userIds;

    /**
     * 当前节点所需用户名称（多个用逗号隔开）
     */
    private String userName;

    /**
     * 节点名称
     */
    private String activityName;

    /**
     * 节点id
     */
    private String activityId;


    /**
     * 对应的下个节点列表
     */
    private List<FlowElementVo> flowElementVoList;
}

