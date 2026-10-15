package com.basic.flowable.core.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (流程查询实体对象)
 * @date 2023/10/19
 */
@Data
@ApiModel(value = "ProcessQuery", description = "流程查询实体对象")
public class ProcessQuery {

    /**
     * 流程标识
     */
    @ApiModelProperty(value = "流程标识")
    private String processKey;
    /**
     * 当前用户id
     */
    @ApiModelProperty(value = "当前用户id", required = true)
    private String userId;
    /**
     * 当前用户角色id
     */
    @ApiModelProperty(value = "当前用户角色id")
    private List<String> groupId;

    /**
     * 流程名称
     */
    @ApiModelProperty(value = "流程名称")
    private String processName;

    /**
     * 流程分类
     */
    @ApiModelProperty(value = "流程分类")
    private String category;

    /**
     * 流程状态
     */
    @ApiModelProperty(value = "流程状态")
    private String processStatus;

    /**
     * 发起人id
     */
    @ApiModelProperty(value = "发起人id")
    private String startUserId;
    /**
     * 代办人id
     */
    @ApiModelProperty(value = "代办人id")
    private String candidateUser;
    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id")
    private String procInsId;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String state;

    /**
     * 请求参数
     */
    @ApiModelProperty(value = "请求参数(发起人startUserId，实例id procInsId，开始时间beginTime，结束时间endTime均放入params参数中查询，)")
    private Map<String, Object> params = new HashMap<>();
}
