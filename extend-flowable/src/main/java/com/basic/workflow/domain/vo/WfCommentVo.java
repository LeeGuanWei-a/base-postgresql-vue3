package com.basic.workflow.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author yanbin
 * @description TODO (流程批复视图对象VO )
 * @date 2024/1/16 10:37
 */
@Data
public class WfCommentVo {

    /**
     * 审批类别
     */
    private String type;

    /**
     * 批复内容
     */
    private String message;

    /**
     * 批复时间
     */
    private Date time;


}
