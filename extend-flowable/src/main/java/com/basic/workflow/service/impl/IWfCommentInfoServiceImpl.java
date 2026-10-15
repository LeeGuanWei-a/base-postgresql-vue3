package com.basic.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.workflow.domain.WfCommentInfo;
import com.basic.workflow.mapper.IWfCommentInfoMapper;
import com.basic.workflow.service.IWfBpmnModelService;
import com.basic.workflow.service.IWfCommentInfoService;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.engine.HistoryService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yanbin
 * @description TODO (flowable流转/流程流转记录service实现类)
 * @date 2024/01/17
 */
@Service
public class IWfCommentInfoServiceImpl extends ServiceImpl<IWfCommentInfoMapper, WfCommentInfo>
        implements IWfCommentInfoService {
    @Autowired
    private IWfBpmnModelService wfBpmnModelService;
    @Autowired
    private HistoryService historyService;

    /**
     * @return void
     * @description TODO (意见保存 记录流转)
     * @params [task, userId, type, message]
     * @author yanbin
     * @date 2024/1/17 14:12
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void addCommentInfo(String taskId, String userId, String type, String message) {
        //获取任务信息
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        //活动id
        String activityId = task.getTaskDefinitionKey();
        //实例id
        String procInsId = task.getProcessInstanceId();
        //获取活动信息
        FlowNode flowNode = wfBpmnModelService.findFlowNodeByActivityId(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        String activityName = flowNode.getName();
        //保存
        WfCommentInfo wfCommentInfo = new WfCommentInfo(task.getId(), userId, procInsId, type, message, activityId, activityName);
        this.saveOrUpdate(wfCommentInfo);
    }

    /**
     * @return void
     * @description TODO (意见保存 记录流转2)
     * @params [wfCommentInfo]
     * @author yanbin
     * @date 2024/2/20 13:51
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void addCommentInfo(WfCommentInfo wfCommentInfo) {
        wfCommentInfo.setTime(new Date());
        this.saveOrUpdate(wfCommentInfo);
    }

    /**
     * @return void
     * @description TODO (删除历史意见)
     * @params [procInsIds]
     * @author yanbin
     * @date 2024/1/17 17:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void delProcessComment(List<String> procInsIds) {
        List<String> ids = new ArrayList<>();
        for (String procInsId : procInsIds) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("proc_ins_id", procInsId);
            List<WfCommentInfo> wfCommentInfo = baseMapper.selectList(queryWrapper);
            if (wfCommentInfo.size() > 0) {
                wfCommentInfo.forEach(comment -> {
                    ids.add(comment.getId());
                });
            }
        }
        if (ids.size() > 0) {
            //物理删除，流程删除，意见也删除
            baseMapper.deleteBatchIds(ids);
        }
    }

    /**
     * @return java.util.List<java.lang.String>
     * @description TODO (查找节点任务审批记录)
     * @params [procInsId, activityId]
     * @author yanbin
     * @date 2024/1/19 14:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<String> selectProcInsIdActivity(String procInsId, String activityId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("proc_ins_id", procInsId);
        queryWrapper.eq("activity_id", activityId);
        queryWrapper.eq("type", FlowComment.NORMAL.getType());
        List<WfCommentInfo> wfCommentInfoList = baseMapper.selectList(queryWrapper);
        List<String> taskIdList = new ArrayList<>();
        if (wfCommentInfoList.size() > 0) {
            wfCommentInfoList.forEach(comment -> {
                taskIdList.add(comment.getTaskId());
            });
        }
        return taskIdList;
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.WfCommentInfo>
     * @description TODO (根据实例id查询历史意见信息)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/2/21 17:59
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfCommentInfo> selectProcessCommentForProcInsId(String procInsId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("proc_ins_id", procInsId);
        queryWrapper.orderByAsc("create_time");
        List<WfCommentInfo> wfCommentInfoList = baseMapper.selectList(queryWrapper);
        return wfCommentInfoList;
    }

    /**
     * @return void
     * @description TODO (根据任务taskId修改意见信息)
     * @params [taskId, currentUserId, comment]
     * @author yanbin
     * @date 2024/2/21 17:59
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void updateTaskComment(String taskId, String currentUserId, String comment) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("task_id", taskId);
        WfCommentInfo wfCommentInfo = baseMapper.selectOne(queryWrapper);
        //TODO 发生这种情况 只存在与发起人的节点
        if (wfCommentInfo == null) {
            return;
        }
        wfCommentInfo.setMessage(comment);
        wfCommentInfo.setUserId(currentUserId);
        this.saveOrUpdate(wfCommentInfo);
    }

    /**
     * @return void
     * @description TODO (根据taskId删除意见信息)
     * @params [taskId]
     * @author yanbin
     * @date 2024/2/22 10:50
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void deleteToCommentForTaskId(String taskId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("task_id", taskId);
        baseMapper.delete(queryWrapper);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.WfCommentInfo>
     * @description TODO (根据任务id获取意见信息)
     * @params [taskId]
     * @author yanbin
     * @date 2024/2/23 9:04
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfCommentInfo> selectTaskIdForComment(String taskId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("task_id", taskId);
        return baseMapper.selectList(queryWrapper);
    }
}
