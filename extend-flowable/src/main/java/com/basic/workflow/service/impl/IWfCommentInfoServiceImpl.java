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
 * @description 娴佺▼娴佽浆璁板綍涓氬姟瀹炵幇绫伙紝鎸変换鍔′笌鑺傜偣淇℃伅淇濆瓨瀹℃壒鎰忚骞剁淮鎶ゆ祦杞褰?
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
     * @description 鎸変换鍔D鏌ュ嚭鍘嗗彶浠诲姟涓庢椿鍔ㄤ俊鎭紝缁勮骞朵繚瀛樹竴鏉℃祦杞剰瑙佽褰?
     * @params [taskId, userId, type, message] 浠诲姟ID锛涙剰瑙佹坊鍔犱汉ID锛涙剰瑙佺被鍨嬬紪鐮侊紱鎰忚鍐呭
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public void addCommentInfo(String taskId, String userId, String type, String message) {
        //鑾峰彇浠诲姟淇℃伅
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        //娲诲姩id
        String activityId = task.getTaskDefinitionKey();
        //瀹炰緥id
        String procInsId = task.getProcessInstanceId();
        //鑾峰彇娲诲姩淇℃伅
        FlowNode flowNode = wfBpmnModelService.findFlowNodeByActivityId(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        String activityName = flowNode.getName();
        //淇濆瓨
        WfCommentInfo wfCommentInfo = new WfCommentInfo(task.getId(), userId, procInsId, type, message, activityId, activityName);
        this.saveOrUpdate(wfCommentInfo);
    }

    /**
     * @description 鐢ㄥ綋鍓嶆椂闂村埛鏂版剰瑙佺殑璁板綍鏃堕棿鍚庝繚瀛樻垨鏇存柊涓€鏉℃祦杞剰瑙?
     * @params [wfCommentInfo] 寰呬繚瀛樼殑鎰忚璁板綍
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public void addCommentInfo(WfCommentInfo wfCommentInfo) {
        wfCommentInfo.setTime(new Date());
        this.saveOrUpdate(wfCommentInfo);
    }

    /**
     * @description 鎸夋祦绋嬪疄渚婭D鍒嗘壒鏌ュ嚭鎰忚璁板綍涓婚敭鍚庣墿鐞嗗垹闄わ紝娴佺▼鍒犻櫎鏃跺悓姝ユ竻鐞嗘剰瑙?
     * @params [procInsIds] 瑕佹竻鐞嗘剰瑙佺殑娴佺▼瀹炰緥ID闆嗗悎
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
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
            //鐗╃悊鍒犻櫎锛屾祦绋嬪垹闄わ紝鎰忚涔熷垹闄?
            baseMapper.deleteBatchIds(ids);
        }
    }

    /**
     * @return List<String> 璇ユ祦绋嬪疄渚嬭鑺傜偣涓婃甯稿鎵规剰瑙佸搴旂殑浠诲姟ID鍒楄〃
     * @description 鎸夋祦绋嬪疄渚婭D涓庢椿鍔↖D鏌ュ嚭璇ヨ妭鐐圭被鍨嬩负姝ｅ父瀹℃壒鐨勬剰瑙侊紝鍙栧嚭浠诲姟ID鍒楄〃
     * @params [procInsId, activityId] 娴佺▼瀹炰緥ID锛涙祦绋嬭妭鐐规椿鍔↖D
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
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
     * @return List<WfCommentInfo> 璇ユ祦绋嬪疄渚嬩笅鐨勬剰瑙佽褰曪紝鎸夊垱寤烘椂闂村崌搴?
     * @description 鎸夋祦绋嬪疄渚婭D鏌ヨ鍘嗗彶鎰忚淇℃伅锛屾寜鍒涘缓鏃堕棿鍗囧簭杩斿洖
     * @params [procInsId] 娴佺▼瀹炰緥ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
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
     * @description 鎸変换鍔D鏌ュ嚭鎰忚璁板綍锛屽洖鍐欐剰瑙佸唴瀹逛笌褰撳墠鍔炵悊浜哄悗淇濆瓨锛岃褰曚笉瀛樺湪鏃剁洿鎺ヨ繑鍥?
     * @params [taskId, currentUserId, comment] 浠诲姟ID锛涘綋鍓嶅姙鐞嗕汉ID锛涙柊鐨勬剰瑙佸唴瀹?
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public void updateTaskComment(String taskId, String currentUserId, String comment) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("task_id", taskId);
        WfCommentInfo wfCommentInfo = baseMapper.selectOne(queryWrapper);
        // 鍙戠敓杩欑鎯呭喌 鍙瓨鍦ㄤ笌鍙戣捣浜虹殑鑺傜偣
        if (wfCommentInfo == null) {
            return;
        }
        wfCommentInfo.setMessage(comment);
        wfCommentInfo.setUserId(currentUserId);
        this.saveOrUpdate(wfCommentInfo);
    }

    /**
     * @description 鎸変换鍔D鐗╃悊鍒犻櫎璇ヤ换鍔″搴旂殑鎰忚淇℃伅
     * @params [taskId] 浠诲姟ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public void deleteToCommentForTaskId(String taskId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("task_id", taskId);
        baseMapper.delete(queryWrapper);
    }

    /**
     * @return List<WfCommentInfo> 璇ヤ换鍔′笅鐨勬剰瑙佽褰曞垪琛?
     * @description 鎸変换鍔D鏌ヨ璇ヤ换鍔″搴旂殑鎰忚淇℃伅
     * @params [taskId] 浠诲姟ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (鏀逛唬鐮佹椂鍐欐槑淇敼鏃堕棿淇敼浜轰慨鏀瑰師鍥?
     */
    @Override
    public List<WfCommentInfo> selectTaskIdForComment(String taskId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("task_id", taskId);
        return baseMapper.selectList(queryWrapper);
    }
}
