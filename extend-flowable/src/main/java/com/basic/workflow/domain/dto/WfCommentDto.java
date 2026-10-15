package com.basic.workflow.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanbin
 * @description TODO (流程意见dto)
 * @date 2024/1/16 10:37
 */
@Data
@Builder
public class WfCommentDto implements Serializable {

    /**
     * 意见类别 0 正常意见  1 退回意见 2 驳回意见
     */
    private String type;

    /**
     * 意见内容
     */
    private String comment;
}
