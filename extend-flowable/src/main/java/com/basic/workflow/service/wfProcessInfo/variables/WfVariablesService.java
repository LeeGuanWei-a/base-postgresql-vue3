package com.basic.workflow.service.wfProcessInfo.variables;

import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description 流程变量查询服务，按任务或流程实例读取运行时与历史变量
 * @author yanbin
 * @date 2024/03/05
 */
@Service
public class WfVariablesService extends WfProcessInfoBase {
    /**
     * @return Map<String, Object> 该任务的流程变量，任务已结束时取历史变量，未结束时取运行时变量
     * @description 按任务 id 取流程变量：优先从历史任务带出流程变量，查不到再读运行时变量
     * @params [taskId] 任务 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public Map<String, Object> getTaskVariables(String taskId) {
        HistoricTaskInstance historicTaskInstance =
                historyService
                        .createHistoricTaskInstanceQuery()
                        .includeProcessVariables()
                        .finished()
                        .taskId(taskId)
                        .singleResult();
        if (Objects.nonNull(historicTaskInstance)) {
            return historicTaskInstance.getProcessVariables();
        }
        return taskService.getVariables(taskId);
    }

    /**
     * @return Map<String, Object> 该流程实例的全部流程变量，无变量时返回空 Map
     * @description 按流程实例 id 从历史变量表取出流程的所有变量
     * @params [procInsId] 流程实例 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public Map<String, Object> getProcessVariables(String procInsId) {
        List<HistoricVariableInstance> historicVariableInstance = historyService
                .createHistoricVariableInstanceQuery().processInstanceId(procInsId)
                .list();
        Map<String, Object> variables = new HashMap<>();
        for (HistoricVariableInstance variableInstance : historicVariableInstance) {
            variables.put(variableInstance.getVariableName(), variableInstance.getValue());
        }
        return variables;
    }
}
