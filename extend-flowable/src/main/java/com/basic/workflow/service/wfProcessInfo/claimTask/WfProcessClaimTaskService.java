package com.basic.workflow.service.wfProcessInfo.claimTask;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.flowable.utils.ProcessUtils;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yanbin
 * @description TODO (Flowable代签任务查询)
 * @date 2024/03/04
 */
@Service
public class WfProcessClaimTaskService extends WfProcessInfoBase {
    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO（查询待签任务列表)不分页
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/3/4 11:16
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public TableDataInfo<WfTaskVo> selectPageClaimProcessList(
            ProcessQuery processQuery, PageQuery pageQuery) {
        //用户id
        String userId = processQuery.getUserId();
        //用户角色组id
        List<String> groupId = processQuery.getGroupId();
        Page<WfTaskVo> page = new Page<>();
        TaskQuery taskQuery =
                taskService
                        .createTaskQuery()
                        .active()
                        .includeProcessVariables()
                        .taskCandidateUser(userId)
                        .taskCandidateGroupIn(groupId)
                        .orderByTaskCreateTime()
                        .desc();
        // 构建搜索条件
        ProcessUtils.buildProcessSearch(taskQuery, processQuery);
        page.setTotal(taskQuery.count());
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<Task> taskList = taskQuery.listPage(offset, pageQuery.getPageSize());
        List<WfTaskVo> flowList = new ArrayList<>();
        for (Task task : taskList) {
            WfTaskVo flowTask = new WfTaskVo();
            flowTask.setNoticeType("claiming");  //通知类型
            //当前节点activityId
            flowTask.setActivityId(task.getTaskDefinitionKey());
            // 当前流程信息
            flowTask.setTaskId(task.getId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
            flowTask.setCreateTime(task.getCreateTime());
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setTaskName(task.getName());
            // 流程定义信息
            ProcessDefinition pd =
                    repositoryService
                            .createProcessDefinitionQuery()
                            .processDefinitionId(task.getProcessDefinitionId())
                            .singleResult();
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());
            //查询业务拓展表
            flowTask = this.selectProcessOfExtendProcinst(flowTask, processQuery.getParams(), task.getProcessInstanceId());
            if (flowTask == null) {
                continue;
            }
            //流程发起的定义id
            flowTask.setProcDefId(task.getProcessDefinitionId());
            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance =
                    historyService
                            .createHistoricProcessInstanceQuery()
                            .processInstanceId(task.getProcessInstanceId())
                            .singleResult();
            //流程发起人id
            flowTask.setStartUserId(historicProcessInstance.getStartUserId());
            flowList.add(flowTask);
        }
        page.setRecords(flowList);
        return TableDataInfo.build(page);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO （查询待签任务列表)不分页
     * @params [processQuery]
     * @author yanbin
     * @date 2023/11/17 16:29
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectClaimProcessList(ProcessQuery processQuery) {
        String userId = processQuery.getUserId();
        List<String> groupId = processQuery.getGroupId();
        TaskQuery taskQuery =
                taskService
                        .createTaskQuery()
                        .active()
                        .includeProcessVariables()
                        .taskCandidateUser(userId)
                        .taskCandidateGroupIn(groupId)
                        .orderByTaskCreateTime()
                        .desc();
        // 构建搜索条件
        ProcessUtils.buildProcessSearch(taskQuery, processQuery);
        List<Task> taskList = taskQuery.list();
        List<WfTaskVo> flowList = new ArrayList<>();
        for (Task task : taskList) {
            WfTaskVo flowTask = new WfTaskVo();
            flowTask.setNoticeType("claiming");  //通知类型
            //当前节点activityId
            flowTask.setActivityId(task.getTaskDefinitionKey());
            // 当前流程信息
            flowTask.setTaskId(task.getId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
            flowTask.setCreateTime(task.getCreateTime());
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setTaskName(task.getName());
            // 流程定义信息
            ProcessDefinition pd =
                    repositoryService
                            .createProcessDefinitionQuery()
                            .processDefinitionId(task.getProcessDefinitionId())
                            .singleResult();
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());
            //查询业务拓展表
            flowTask = this.selectProcessOfExtendProcinst(flowTask, processQuery.getParams(), task.getProcessInstanceId());
            if (flowTask == null) {
                continue;
            }
            //流程发起的定义id
            flowTask.setProcDefId(task.getProcessDefinitionId());
            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance =
                    historyService
                            .createHistoricProcessInstanceQuery()
                            .processInstanceId(task.getProcessInstanceId())
                            .singleResult();
            //流程发起人id
            flowTask.setStartUserId(historicProcessInstance.getStartUserId());
            flowList.add(flowTask);
        }
        return flowList;
    }

}
