package com.basic.workflow.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.basic.workflow.domain.WfCommentInfo;

import java.util.List;

/**
 * @description 流程流转记录服务接口，负责审批意见的写入、查询与批量清理
 * @author yanbin
 * @date 2024/01/17
 */
public interface IWfCommentInfoService extends IService<WfCommentInfo> {
    /**
     * @description 按任务、处理人、类型与意见内容补一条流转记录，用于记录审批动作
     * @params [taskId, userId, type, message] 任务 ID；处理人 ID；意见类型；意见内容
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void addCommentInfo(String taskId, String userId, String type, String message);

    /**
     * @description 直接把一条意见对象落库，用于已有完整意见数据的场景
     * @params [wfCommentInfo] 待保存的审批意见对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void addCommentInfo(WfCommentInfo wfCommentInfo);

    /**
     * @description 按流程实例 ID 集合批量删除这些实例下遗留的审批意见
     * @params [procInsIds] 要清理意见的流程实例 ID 集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void delProcessComment(List<String> procInsIds);

    /**
     * @return List<String> 该节点上的审批意见内容列表
     * @description 按流程实例与活动节点查该节点的任务审批记录内容
     * @params [procInsId, activityId] 流程实例 ID；活动节点 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<String> selectProcInsIdActivity(String procInsId, String activityId);

    /**
     * @return List<WfCommentInfo> 该流程实例的意见列表，无记录时为空列表
     * @description 按流程实例 ID 查询其历史审批意见
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCommentInfo> selectProcessCommentForProcInsId(String procInsId);

    /**
     * @description 按任务 ID 与处理人改写该任务的意见内容
     * @params [taskId, currentUserId, comment] 任务 ID；当前处理人 ID；新的意见内容
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void updateTaskComment(String taskId, String currentUserId, String comment);

    /**
     * @description 按任务 ID 删除该任务的审批意见
     * @params [taskId] 任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteToCommentForTaskId(String taskId);

    /**
     * @return List<WfCommentInfo> 该任务的意见列表，无记录时为空列表
     * @description 按任务 ID 查询该任务的审批意见
     * @params [taskId] 任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCommentInfo> selectTaskIdForComment(String taskId);
}
