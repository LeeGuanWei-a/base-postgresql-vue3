package com.basic.workflow.cmd.rollBack;

import lombok.Data;
import org.flowable.bpmn.model.*;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.*;

/**
 * @description 撤回策略的通用入参，封装历史任务、当前节点及其到下一节点之间的连线、网关与子流程信息
 * @author Zengjunlong
 * @date 17:55 2024/3/6
 */
@Data
public class RollbackParamsTemplate {

    /**
     *  回滚任务
     */
    private HistoricTaskInstance hisTask;

    /**
     *  当前任务节点
     */
    private UserTask currentTaskElement;

    /**
     *  当前节点到下一任务节点间的连线（不包含当前任务节点）
     */
    private Set<String> nextFlowIdList = new HashSet<>();

    /**
     *  当前任务节点到 下一节点 之间线条
     */
    private Map<String, SequenceFlow> outGoingMap = new HashMap<>();

    /**
     *  下一任务节点 集合
     */
    private List<UserTask> nextUserTaskList = new ArrayList<>();

    /**
     *  到下一任务节点 之间的网关集合
     */
    private Map<String, Gateway> gatewayMap = new HashMap<>();

    /**
     *  下一节点是否为 嵌入式子流程
     */
    private Map<String, SubProcess> subProcessMap = new HashMap<>();

    /**
     *  下一节点是否为 调用子流程
     */
    private Map<String, CallActivity> callActivityMap = new HashMap<>();

    /**
     *  当前 嵌入子流程
     */
    private SubProcess currentSubProcess;
}
