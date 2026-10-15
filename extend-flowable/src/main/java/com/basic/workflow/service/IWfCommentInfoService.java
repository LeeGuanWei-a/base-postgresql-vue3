package com.basic.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.workflow.domain.WfCommentInfo;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (flowable流转/流程流转记录service接口)
 * @date 2024/01/17
 */
public interface IWfCommentInfoService extends IService<WfCommentInfo> {
    /**
     * @return void
     * @description TODO (意见保存 记录流转)1)
     * @params [taskId, userId, type, message]
     * @author yanbin
     * @date 2024/1/17 14:12
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void addCommentInfo(String taskId, String userId, String type, String message);

    /**
     * @return void
     * @description TODO (意见保存 记录流转2)
     * @params [wfCommentInfo]
     * @author yanbin
     * @date 2024/2/20 11:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void addCommentInfo(WfCommentInfo wfCommentInfo);

    /**
     * @return void
     * @description TODO (删除历史意见)
     * @params [procInsIds]
     * @author yanbin
     * @date 2024/1/17 17:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void delProcessComment(List<String> procInsIds);

    /**
     * @return java.util.List<java.lang.String>
     * @description TODO (查找节点任务审批记录)
     * @params [procInsId, activityId]
     * @author yanbin
     * @date 2024/1/19 14:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<String> selectProcInsIdActivity(String procInsId, String activityId);

    /**
     * @return java.util.List<com.basic.workflow.domain.WfCommentInfo>
     * @description TODO (根据实例id查询历史意见信息)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/2/21 17:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCommentInfo> selectProcessCommentForProcInsId(String procInsId);

    /**
     * @return void
     * @description TODO (根据人物id修改意见信息)
     * @params [taskId, currentUserId, comment]
     * @author yanbin
     * @date 2024/2/21 17:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void updateTaskComment(String taskId, String currentUserId, String comment);

    /**
     * @return void
     * @description TODO (根据taskId删除意见信息)
     * @params [taskId]
     * @author yanbin
     * @date 2024/2/22 10:49
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteToCommentForTaskId(String taskId);

    /**
     * @return java.util.List<com.basic.workflow.domain.WfCommentInfo>
     * @description TODO (根据任务id获取意见信息)
     * @params [taskId]
     * @author yanbin
     * @date 2024/2/23 9:04
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCommentInfo> selectTaskIdForComment(String taskId);
}
