package com.basic.flowable.core.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description 流程查询实体对象，封装流程标识、当前用户及其角色、流程名称与分类等列表查询条件
 * @author yanbin
 * @date 2023/10/19
 */
@Data
@Schema(description = "流程查询实体对象")
public class ProcessQuery {

    /**
     * 流程标识
     */
    @Schema(description = "流程标识")
    private String processKey;
    /**
     * 当前用户id
     */
    @Schema(description = "当前用户id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;
    /**
     * 当前用户角色id
     */
    @Schema(description = "当前用户角色id")
    private List<String> groupId;

    /**
     * 流程名称
     */
    @Schema(description = "流程名称")
    private String processName;

    /**
     * 流程分类
     */
    @Schema(description = "流程分类")
    private String category;

    /**
     * 流程状态
     */
    @Schema(description = "流程状态")
    private String processStatus;

    /**
     * 发起人id
     */
    @Schema(description = "发起人id")
    private String startUserId;
    /**
     * 代办人id
     */
    @Schema(description = "代办人id")
    private String candidateUser;
    /**
     * 实例id
     */
    @Schema(description = "实例id")
    private String procInsId;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private String state;

    /**
     * 请求参数
     */
    @Schema(description = "请求参数(发起人startUserId，实例id procInsId，开始时间beginTime，结束时间endTime均放入params参数中查询，)")
    private Map<String, Object> params = new HashMap<>();
}
