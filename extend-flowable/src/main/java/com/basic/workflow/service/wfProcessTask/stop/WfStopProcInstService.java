package com.basic.workflow.service.wfProcessTask.stop;

import com.basic.common.utils.StringUtils;
import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.flowable.common.enums.ProcessStatus;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.impl.identity.Authentication;
import cn.hutool.core.collection.CollUtil;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

/**
 * @description 流程终止服务，把流程实例的执行移至结束节点并写入已取消状态与终止意见
 * @author yanbin
 * @date 2024/03/01
 */
@Service
public class WfStopProcInstService extends WfProcessTaskBase {
    /**
     * @return boolean 流程实例全部处理过则返回 true
     * @description 逐个终止传入的流程实例：取出流程模型与结束节点，把执行实例移动到结束节点，写入已取消的流程状态并记录终止意见
     * @params [bo] 任务业务对象，含待终止的流程实例id集合、操作人与终止意见
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean stopProcess(WfTaskBo bo) {
        List<String> procInsIdList = bo.getProcInsIdList();
        if (StringUtils.isNotEmpty(procInsIdList)) {
            for (String procInsId : procInsIdList) {
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(procInsId).singleResult();
                if (processInstance == null) {
                    continue;
                }
                // 1.获取当前任务-----这里没有对当前任务进行complete是因为无法确定下一节点与当前节点之间是否存在了网关，若是有网关会导致参数问题（可以根据线获取）----所以直接是进行了取消
                List<Task> task = taskService.createTaskQuery().processInstanceId(procInsId).list();
                if (CollUtil.isEmpty(task)) {
                    throw new RuntimeException("流程未启动或已执行完成，取消申请失败");
                }
                if (StringUtils.isBlank(procInsId)) {
                    continue;
                }
                // 3.获取流程模型信息 bpmnModel
                BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
                if (Objects.nonNull(bpmnModel)) {
                    // 3.1 解析bpmnModel  即读取xml内容
                    Process process = bpmnModel.getMainProcess();
                    // 3.2 查找结束节点
                    List<EndEvent> endNodes = process.findFlowElementsOfType(EndEvent.class, false);
                    if (CollUtil.isNotEmpty(endNodes)) {
                        // 3.3 设置处理人
                        Authentication.setAuthenticatedUserId(bo.getCurrentUserId());
                        // 3.4获取当前流程最后一个节点
                        String endId = endNodes.get(0).getId();
                        // 3.5 获取当前执行实例信息
                        List<Execution> executions =
                                runtimeService
                                        .createExecutionQuery()
                                        .parentId(processInstance.getProcessInstanceId())
                                        .list();
                        List<String> executionIds = new ArrayList<>();
                        // 3.6 执行实例id集合
                        executions.forEach(execution -> executionIds.add(execution.getId()));
                        // 3.7设置流程状态为已取消申请
                        runtimeService.setVariable(
                                processInstance.getId(),
                                ProcessConstants.PROCESS_STATUS_KEY,
                                ProcessStatus.CANCELED.getStatus());
                        // 3.8 添加审批意见
                        this.addComment(null, bo.getCurrentUserId(), procInsId, FlowComment.STOP.getType(), bo.getComment());
                        // 3.9 变更流程为已结束状态
                        runtimeService
                                .createChangeActivityStateBuilder()
                                .moveExecutionsToSingleActivityId(executionIds, endId)
                                .changeState();
                    }
                }
            }
        } else {
            throw new RuntimeException("未选择取消的流程!");
        }
        return true;
    }
}
