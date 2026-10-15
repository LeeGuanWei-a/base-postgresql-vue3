package com.basic.workflow.service.wfProcessInfo.detail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.basic.flowable.flow.FlowableUtils;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.domain.vo.WfViewerVo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yanbin
 * @description TODO (Flowable获取流程执行过程)
 * @date 2024/03/04
 */
@Service
public class WfProcessFlowViewerService extends WfProcessInfoBase {
    /**
     * @return com.basic.workflow.domain.vo.WfViewerVo
     * @description TODO (根据实例procInsId查询流程执行过程)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/4 10:46
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfViewerVo getFlowViewer(String procInsId) {
        // 获取流程实例
        HistoricProcessInstance historicProcIns =
                historyService
                        .createHistoricProcessInstanceQuery()
                        .processInstanceId(procInsId)
                        .includeProcessVariables()
                        .singleResult();
        // 获取Bpmn模型信息
        InputStream inputStream =
                repositoryService.getProcessModel(historicProcIns.getProcessDefinitionId());
        String bpmnXmlStr = StrUtil.utf8Str(IoUtil.readBytes(inputStream, false));
        BpmnModel bpmnModel = ModelUtils.getBpmnModel(bpmnXmlStr);
        // 构建查询条件
        HistoricActivityInstanceQuery query =
                historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId);
        List<HistoricActivityInstance> allActivityInstanceList = query.list();
        if (CollUtil.isEmpty(allActivityInstanceList)) {
            return new WfViewerVo();
        }
        // 查询所有已完成的元素
        List<HistoricActivityInstance> finishedElementList =
                allActivityInstanceList.stream()
                        .filter(item -> ObjectUtil.isNotNull(item.getEndTime()))
                        .collect(Collectors.toList());
        // 所有已完成的连线
        Set<String> finishedSequenceFlowSet = new HashSet<>();
        // 所有已完成的任务节点
        Set<String> finishedTaskSet = new HashSet<>();
        finishedElementList.forEach(
                item -> {
                    if (BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW.equals(item.getActivityType())) {
                        finishedSequenceFlowSet.add(item.getActivityId());
                    } else {
                        finishedTaskSet.add(item.getActivityId());
                    }
                });
        // 查询所有未结束的节点
        Set<String> unfinishedTaskSet =
                allActivityInstanceList.stream()
                        .filter(item -> ObjectUtil.isNull(item.getEndTime()))
                        .map(HistoricActivityInstance::getActivityId)
                        .collect(Collectors.toSet());
        // DFS 查询未通过的元素集合
        Set<String> rejectedSet =
                FlowableUtils.dfsFindRejects(
                        bpmnModel, unfinishedTaskSet, finishedSequenceFlowSet, finishedTaskSet);
        return new WfViewerVo(finishedTaskSet, finishedSequenceFlowSet, unfinishedTaskSet, rejectedSet);
    }
}
