package com.basic.flowable.utils;


import com.basic.common.utils.DateUtils;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.common.enums.ProcessStatus;
import com.basic.flowable.core.domain.ProcessQuery;
import org.flowable.common.engine.api.query.Query;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description 流程查询工具类，为流程定义、任务及历史实例查询对象追加通用过滤条件
 * @author yanbin
 * @date 2024/1/8
 */
public class ProcessUtils {
    /**
     * @description 按查询对象的实际类型分派到流程定义、任务、历史任务或历史流程实例的搜索条件构建方法
     * @params [query, process] query 待追加过滤条件的流程引擎查询对象；process 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static void buildProcessSearch(Query<?, ?> query, ProcessQuery process) {
        if (query instanceof ProcessDefinitionQuery) {
            buildProcessDefinitionSearch((ProcessDefinitionQuery) query, process);
        } else if (query instanceof TaskQuery) {
            buildTaskSearch((TaskQuery) query, process);
        } else if (query instanceof HistoricTaskInstanceQuery) {
            buildHistoricTaskInstanceSearch((HistoricTaskInstanceQuery) query, process);
        } else if (query instanceof HistoricProcessInstanceQuery) {
            buildHistoricProcessInstanceSearch((HistoricProcessInstanceQuery) query, process);
        }
    }

    /**
     * @description 按流程标识、流程名称、流程分类和挂起状态为流程定义查询追加模糊或精确过滤条件
     * @params [query, process] query 流程定义查询对象；process 流程查询条件，字段为空时跳过该条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static void buildProcessDefinitionSearch(ProcessDefinitionQuery query, ProcessQuery process) {
        // 流程标识
        if (StringUtils.isNotBlank(process.getProcessKey())) {
            query.processDefinitionKeyLike("%" + process.getProcessKey() + "%");
        }
        // 流程名称
        if (StringUtils.isNotBlank(process.getProcessName())) {
            query.processDefinitionNameLike("%" + process.getProcessName() + "%");
        }
        // 流程分类
        if (StringUtils.isNotBlank(process.getCategory())) {
            query.processDefinitionCategory(process.getCategory());
        }
        // 流程状态
        if (StringUtils.isNotBlank(process.getState())) {
            if (SuspensionState.ACTIVE.toString().equals(process.getState())) {
                query.active();
            } else if (SuspensionState.SUSPENDED.toString().equals(process.getState())) {
                query.suspended();
            }
        }
    }


    /**
     * @description 按流程标识、流程名称、流程分类以及任务创建时间区间为任务查询追加过滤条件
     * @params [query, process] query 任务查询对象；process 流程查询条件，含可选的开始与结束时间
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static void buildTaskSearch(TaskQuery query, ProcessQuery process) {
        Map<String, Object> params = process.getParams();
        List<String> category = new ArrayList<>();
        category.add(process.getCategory());
        //processKey查询
        if (StringUtils.isNotBlank(process.getProcessKey())) {
            query.processDefinitionKeyLike("%" + process.getProcessKey() + "%");
        }
        //processName查询
        if (StringUtils.isNotBlank(process.getProcessName())) {
            query.processDefinitionNameLike("%" + process.getProcessName() + "%");
        }
        //分类查询
        if (StringUtils.isNotBlank(process.getCategory())) {
            query.processCategoryIn(category);
        }
        //开始时间--结束时间
        if (params.get("beginTime") != null && params.get("endTime") != null) {
            query.taskCreatedAfter(DateUtils.parseDate(params.get("beginTime")));
            query.taskCreatedBefore(DateUtils.parseDate(params.get("endTime")));
        }
        //开始时间
        if (params.get("beginTime") != null) {
            query.taskCreatedAfter(DateUtils.parseDate(params.get("beginTime")));
        }
        //结束时间
        if (params.get("endTime") != null) {
            query.taskCreatedBefore(DateUtils.parseDate(params.get("endTime")));
        }
    }

    /**
     * @description 按流程标识、流程名称、流程实例id、候选人以及任务完成时间区间为历史任务查询追加过滤条件
     * @params [query, process] query 历史任务查询对象；process 流程查询条件，含可选的开始与结束时间
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private static void buildHistoricTaskInstanceSearch(HistoricTaskInstanceQuery query, ProcessQuery process) {
        Map<String, Object> params = process.getParams();
        //流程标识
        if (StringUtils.isNotBlank(process.getProcessKey())) {
            query.processDefinitionKeyLike("%" + process.getProcessKey() + "%");
        }
        //流程名称  这个是model名称
        if (StringUtils.isNotBlank(process.getProcessName())) {
            query.processDefinitionNameLike("%" + process.getProcessName() + "%");
        }
        //实例id查询
        if (StringUtils.isNotBlank(process.getProcInsId())) {
            query.processInstanceId(process.getProcInsId());
        }
        //代办人
        if (StringUtils.isNotBlank(process.getCandidateUser())) {
            query.taskCandidateUser(process.getCandidateUser());
        }
        //业务标识筛选（已做流程与业务解耦，不再按 businessKey 过滤）
        //开始时间---结束时间
        if (params.get("beginTime") != null && params.get("endTime") != null) {
            query.taskCompletedAfter(DateUtils.parseDate(params.get("beginTime")));
            query.taskCompletedBefore(DateUtils.parseDate(params.get("endTime")));
        }
    }

    /**
     * @description 按流程标识、流程名称、流程分类、发起人、流程实例id、运行状态和发起时间区间为历史流程实例查询追加过滤条件
     * @params [query, process] query 历史流程实例查询对象；process 流程查询条件，含可选的开始与结束时间
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static void buildHistoricProcessInstanceSearch(HistoricProcessInstanceQuery query, ProcessQuery process) {
        Map<String, Object> params = process.getParams();
        // 流程标识
        if (StringUtils.isNotBlank(process.getProcessKey())) {
            query.processDefinitionKey(process.getProcessKey());
        }
        // 流程名称
        if (StringUtils.isNotBlank(process.getProcessName())) {
            query.processDefinitionName(process.getProcessName());
        }
        // 流程分类
        if (StringUtils.isNotBlank(process.getCategory())) {
            query.processDefinitionCategory(process.getCategory());
        }
        //发起人/提交人
        if (StringUtils.isNotBlank(process.getStartUserId())) {
            query.startedBy(process.getStartUserId());
        }
        //根据指定实例id查询
        if (StringUtils.isNotBlank(process.getProcInsId())) {
            query.processInstanceId(process.getProcInsId());
        }
        //根据流程状态查询
        //  running   -> 只查未结束的实例（引擎级 unfinished()，兼容没有写流程状态变量的历史数据）
        //  completed -> 只查已结束的实例
        //  其它值    -> 忽略
        // 注意：原实现是 query.variableValueEquals(value)，语义是「任意变量等于该值」，
        //      既不准确也对历史数据不可靠（老流程没有流程状态变量），故改为按实例是否结束过滤。
        if (StringUtils.isNotBlank(process.getProcessStatus())) {
            if (ProcessStatus.RUNNING.getStatus().equalsIgnoreCase(process.getProcessStatus())) {
                query.unfinished();
            } else if (ProcessStatus.COMPLETED.getStatus().equalsIgnoreCase(process.getProcessStatus())) {
                query.finished();
            } else if (ProcessStatus.CANCELED.getStatus().equalsIgnoreCase(process.getProcessStatus())) {
                // 已终止：已结束，且流程状态变量为 canceled。
                // 终止（WfStopProcInstService#stopProcess）会 runtimeService.setVariable 写这个变量，
                // 所以历史变量里查得到。
                query.finished();
                query.variableValueEquals(ProcessConstants.PROCESS_STATUS_KEY,
                        ProcessStatus.CANCELED.getStatus());
            }
        }
        //根据业务id查询（已做流程与业务解耦，不再按 businessKey 过滤）
        //开始时间和结束时间
        if (params.get("beginTime") != null && params.get("endTime") != null) {
            query.startedAfter(DateUtils.parseDate(params.get("beginTime")));
            query.startedBefore(DateUtils.parseDate(params.get("endTime")));
        }
    }

}
