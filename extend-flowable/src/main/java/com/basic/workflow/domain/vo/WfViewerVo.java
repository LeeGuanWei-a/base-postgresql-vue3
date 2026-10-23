package com.basic.workflow.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @description 任务追踪视图对象，记录流程实例已完成、待办、已拒绝的节点与已走过的连线集合
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WfViewerVo {

    /**
     * 获取流程实例的历史节点（去重）
     */
    private Set<String> finishedTaskSet;

    /**
     * 已完成
     */
    private Set<String> finishedSequenceFlowSet;

    /**
     * 获取流程实例当前正在待办的节点（去重）
     */
    private Set<String> unfinishedTaskSet;

    /**
     * 已拒绝
     */
    private Set<String> rejectedTaskSet;
}
