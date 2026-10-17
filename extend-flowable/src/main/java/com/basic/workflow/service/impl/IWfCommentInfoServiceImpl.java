package com.basic.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
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
 * @description 流程流转记录业务实现类，按任务与节点信息保存审批意见并维护流转记录
 * @author yanbin
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
     * @description 按任务ID查出历史任务与活动信息，组装并保存一条流转意见记录
     * @params [taskId, userId, type, message] 任务ID；意见添加人ID；意见类型编码；意见内容
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @description 用当前时间刷新意见的记录时间后保存或更新一条流转意见
     * @params [wfCommentInfo] 待保存的意见记录
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void addCommentInfo(WfCommentInfo wfCommentInfo) {
        wfCommentInfo.setTime(new Date());
        this.saveOrUpdate(wfCommentInfo);
    }

    /**
     * @description 按流程实例ID分批查出意见记录主键后物理删除，流程删除时同步清理意见
     * @params [procInsIds] 要清理意见的流程实例ID集合
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return List<String> 该流程实例该节点上正常审批意见对应的任务ID列表
     * @description 按流程实例ID与活动ID查出该节点类型为正常审批的意见，取出任务ID列表
     * @params [procInsId, activityId] 流程实例ID；流程节点活动ID
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return List<WfCommentInfo> 该流程实例下的意见记录，按创建时间升序
     * @description 按流程实例ID查询历史意见信息，按创建时间升序返回
     * @params [procInsId] 流程实例ID
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @description 按任务ID查出意见记录，回写意见内容与当前办理人后保存，记录不存在时直接返回
     * @params [taskId, currentUserId, comment] 任务ID；当前办理人ID；新的意见内容
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void updateTaskComment(String taskId, String currentUserId, String comment) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("task_id", taskId);
        WfCommentInfo wfCommentInfo = baseMapper.selectOne(queryWrapper);
        // 发生这种情况 只存在与发起人的节点
        if (wfCommentInfo == null) {
            return;
        }
        wfCommentInfo.setMessage(comment);
        wfCommentInfo.setUserId(currentUserId);
        this.saveOrUpdate(wfCommentInfo);
    }

    /**
     * @description 按任务ID物理删除该任务对应的意见信息
     * @params [taskId] 任务ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void deleteToCommentForTaskId(String taskId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("task_id", taskId);
        baseMapper.delete(queryWrapper);
    }

    /**
     * @return List<WfCommentInfo> 该任务下的意见记录列表
     * @description 按任务ID查询该任务对应的意见信息
     * @params [taskId] 任务ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfCommentInfo> selectTaskIdForComment(String taskId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("task_id", taskId);
        return baseMapper.selectList(queryWrapper);
    }
}
