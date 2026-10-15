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
 * @author yanbin
 * @description TODO (Flowable流程变量获取)
 * @date 2024/03/05
 */
@Service
public class WfVariablesService extends WfProcessInfoBase {
    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @description TODO (根据任务taskId获取当前任务的变量)
     * @params [taskId]
     * @author yanbin
     * @date 2023/11/3 10:41
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
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @description TODO (根据实例procInsId获取流程所有变量)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/5 15:28
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
