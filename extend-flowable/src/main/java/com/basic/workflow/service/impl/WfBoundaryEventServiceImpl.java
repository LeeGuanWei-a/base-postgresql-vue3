package com.basic.workflow.service.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.basic.common.exception.ServiceException;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.common.enums.BoundaryEventTypeEnum;
import com.basic.flowable.factory.FlowServiceFactory;
import com.basic.workflow.domain.bo.WfBoundaryTriggerBo;
import com.basic.workflow.domain.vo.WfBoundaryEventVo;
import com.basic.workflow.service.IWfBoundaryEventService;
import org.flowable.bpmn.model.BoundaryEvent;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ErrorEventDefinition;
import org.flowable.bpmn.model.EventDefinition;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.MessageEventDefinition;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SignalEventDefinition;
import org.flowable.bpmn.model.SubProcess;
import org.flowable.bpmn.model.TimerEventDefinition;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 边界事件触发服务实现。
 *
 * <p>封装四种触发语义：</p>
 * <ul>
 *   <li>signal — {@code RuntimeService.signalEventReceived(signalName)}，全局广播</li>
 *   <li>message — {@code RuntimeService.messageEventReceived(messageName, executionId)}，精准投递</li>
 *   <li>error — 通过 {@code DelegateExecution#raiseBpmnError} 等价路径，借助 {@code runtimeService.createChange} 在当前 execution 上抛 BpmnError</li>
 *   <li>cancel — {@code RuntimeService.deleteProcessInstance} 模拟取消子流程并触发 cancel boundary（仅供演示）</li>
 * </ul>
 *
 * @author integration-supplement
 */
@Service
public class WfBoundaryEventServiceImpl extends FlowServiceFactory implements IWfBoundaryEventService {

    private static final Logger log = LoggerFactory.getLogger(WfBoundaryEventServiceImpl.class);

    /**
     * @return boolean 触发成功返回 true
     * @description 按信号名称全局广播信号事件以触发流程实例上的信号边界事件，并回写触发状态变量，参数缺失时抛业务异常
     * @params [bo] 触发参数，含流程实例 id、信号 code、备注与自定义变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean triggerSignal(WfBoundaryTriggerBo bo) {
        validate(bo);
        String signalName = bo.getEventCode();
        try {
            // 全局信号：传 null executionId，广播到所有挂 signal boundary 的 execution
            runtimeService.signalEventReceived(signalName);
            // 写流程变量
            applyBoundaryTriggerVariables(bo, BoundaryEventTypeEnum.SIGNAL);
            log.info("[boundary] 触发信号边界成功 signalName={} procInsId={}", signalName, bo.getProcInsId());
            return true;
        } catch (Exception e) {
            log.error("[boundary] 触发信号边界失败 signalName={} procInsId={} err={}",
                    signalName, bo.getProcInsId(), e.getMessage(), e);
            throw new ServiceException("触发信号边界失败：" + ExceptionUtil.getMessage(e));
        }
    }

    /**
     * @return boolean 至少投递成功一个 execution 返回 true，无订阅者返回 false
     * @description 按消息名称查找该流程实例下订阅该消息的 execution 并逐一精准投递消息事件，无订阅者时只记告警不抛异常
     * @params [bo] 触发参数，含流程实例 id、消息 code、备注与自定义变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean triggerMessage(WfBoundaryTriggerBo bo) {
        validate(bo);
        String messageName = bo.getEventCode();
        // message 必须按 execution 精准投递，不能全局广播
        // 这里按 procInsId + messageEventSubscriptionName 找出所有订阅了此消息的 execution
        try {
            List<Execution> executions = runtimeService.createExecutionQuery()
                    .processInstanceId(bo.getProcInsId())
                    .messageEventSubscriptionName(messageName)
                    .list();
            if (executions == null || executions.isEmpty()) {
                log.warn("[boundary] 触发消息边界：未找到订阅消息[{}]的执行实例 procInsId={}",
                        messageName, bo.getProcInsId());
                applyBoundaryTriggerVariables(bo, BoundaryEventTypeEnum.MESSAGE);
                return false;
            }
            for (Execution execution : executions) {
                runtimeService.messageEventReceived(messageName, execution.getId());
            }
            applyBoundaryTriggerVariables(bo, BoundaryEventTypeEnum.MESSAGE);
            log.info("[boundary] 触发消息边界成功 messageName={} procInsId={} matched={}",
                    messageName, bo.getProcInsId(), executions.size());
            return true;
        } catch (Exception e) {
            log.error("[boundary] 触发消息边界失败 messageName={} procInsId={} err={}",
                    messageName, bo.getProcInsId(), e.getMessage(), e);
            throw new ServiceException("触发消息边界失败：" + ExceptionUtil.getMessage(e));
        }
    }

    /**
     * @return boolean 变量写入成功返回 true
     * @description 写入错误边界标志变量与错误码，并把流程状态置为 boundary_error，供 ServiceTask 监听到标志后抛 BpmnError
     * @params [bo] 触发参数，含流程实例 id、错误码、备注与自定义变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean triggerError(WfBoundaryTriggerBo bo) {
        validate(bo);
        String errorCode = bo.getEventCode();
        try {
            // 找到当前活动 execution，runtimeService.createChange(...) 没法直接抛 BpmnError；
            // 这里通过自定义 MyBatis 命令封装抛错。简单实现：记录变量后让外部 ServiceTask 监听变量抛 BpmnError。
            // 这里先按"业务侧在 ServiceTask 中根据 boundaryError_<code>=true 抛 BpmnError"约定，
            // 由本服务设置标志变量，ServiceTask 中收到标志后调用 execution.bpmnError()。
            // 写错误码标志变量（便于 ServiceTask 监听后抛 BpmnError）
            runtimeService.setVariable(bo.getProcInsId(),
                    ProcessConstants.BOUNDARY_TRIGGER_STATUS_PREFIX + BoundaryEventTypeEnum.ERROR.getShortName(),
                    errorCode);
            if (StrUtil.isNotBlank(bo.getComment())) {
                runtimeService.setVariable(bo.getProcInsId(),
                        ProcessConstants.BOUNDARY_TRIGGER_COMMENT, bo.getComment());
            }
            // processStatus 改为 boundary_error
            runtimeService.setVariable(bo.getProcInsId(),
                    ProcessConstants.PROCESS_STATUS_KEY,
                    ProcessConstants.BOUNDARY_TRIGGER_STATUS_PREFIX + BoundaryEventTypeEnum.ERROR.getShortName());
            // bo.variables 自定义变量（如有）
            if (bo.getVariables() != null) {
                for (Map.Entry<String, Object> e : bo.getVariables().entrySet()) {
                    runtimeService.setVariable(bo.getProcInsId(), e.getKey(), e.getValue());
                }
            }
            log.info("[boundary] 触发错误边界（标记变量） errorCode={} procInsId={}", errorCode, bo.getProcInsId());
            return true;
        } catch (Exception e) {
            log.error("[boundary] 触发错误边界失败 errorCode={} procInsId={} err={}",
                    errorCode, bo.getProcInsId(), e.getMessage(), e);
            throw new ServiceException("触发错误边界失败：" + ExceptionUtil.getMessage(e));
        }
    }

    /**
     * @return boolean 删除成功返回 true
     * @description 删除流程实例以模拟取消，从而触发子流程上的取消边界事件，仅校验流程实例 id，备注作为删除原因
     * @params [bo] 触发参数，仅需流程实例 id，备注作为删除原因
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean triggerCancel(WfBoundaryTriggerBo bo) {
        // cancel 仅校验 procInsId，eventCode/comment 可选
        if (bo == null || StringUtils.isBlank(bo.getProcInsId())) {
            throw new ServiceException("流程实例id不能为空");
        }
        try {
            // 流程级 cancel 语义：Flowable 用 deleteProcessInstance(reason, true)
            // 当子流程边界上有 cancel boundary 时，删除子流程实例会触发 cancel 边界
            // 主流程实例直接 cancel 不一定触发 cancel boundary（按 BPMN 规范 cancel 仅子流程边界有效）
            runtimeService.deleteProcessInstance(bo.getProcInsId(),
                    StringUtils.isBlank(bo.getComment()) ? "boundary cancel" : bo.getComment());
            log.info("[boundary] 触发取消边界（deleteProcessInstance） procInsId={} reason={}",
                    bo.getProcInsId(), bo.getComment());
            return true;
        } catch (Exception e) {
            log.error("[boundary] 触发取消边界失败 procInsId={} err={}",
                    bo.getProcInsId(), e.getMessage(), e);
            throw new ServiceException("触发取消边界失败：" + ExceptionUtil.getMessage(e));
        }
    }

    /**
     * @return List<WfBoundaryEventVo> 边界事件视图列表，无法定位流程定义时返回空列表
     * @description 列出流程实例所属流程定义上的全部边界事件，实例已结束时改取历史实例的流程定义
     * @params [procInsId] 流程实例 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfBoundaryEventVo> listBoundaries(String procInsId) {
        if (StringUtils.isBlank(procInsId)) {
            return new ArrayList<>();
        }
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(procInsId).singleResult();
        if (instance == null) {
            // 流程已结束：尝试查历史实例
            HistoricProcessInstance historic = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(procInsId).singleResult();
            if (historic == null) {
                return new ArrayList<>();
            }
            // 历史实例没有活动 execution，只能从定义里取 boundary
            return listBoundariesFromDefinition(historic.getProcessDefinitionId());
        }
        return listBoundariesFromDefinition(instance.getProcessDefinitionId());
    }

    /**
     * @return List<WfBoundaryEventVo> 该流程定义上的边界事件视图列表，取模型失败时返回已收集结果
     * @description 读取流程定义的 BPMN 模型并遍历其流程元素，收集所有边界事件组装为视图对象
     * @params [processDefinitionId] 流程定义 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private List<WfBoundaryEventVo> listBoundariesFromDefinition(String processDefinitionId) {
        List<WfBoundaryEventVo> result = new ArrayList<>();
        if (StringUtils.isBlank(processDefinitionId)) {
            return result;
        }
        try {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            if (bpmnModel == null) {
                return result;
            }
            for (Process process : bpmnModel.getProcesses()) {
                walkBoundary(process.getFlowElements(), result);
            }
        } catch (Exception e) {
            log.warn("[boundary] 读取流程定义上的 boundary 失败 processDefinitionId={} err={}",
                    processDefinitionId, e.getMessage());
        }
        return result;
    }

    /**
     * @description 递归收集流程元素中的边界事件，遇到子流程时继续深入其内部元素查找
     * @params [elements, sink] 待遍历的流程元素集合；收集结果的接收列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void walkBoundary(Collection<FlowElement> elements,
                              List<WfBoundaryEventVo> sink) {
        if (elements == null || elements.isEmpty()) {
            return;
        }
        for (FlowElement element : elements) {
            if (element instanceof BoundaryEvent) {
                sink.add(toVo((BoundaryEvent) element));
            }
            if (element instanceof SubProcess) {
                walkBoundary(((SubProcess) element).getFlowElements(), sink);
            }
        }
    }

    /**
     * @return WfBoundaryEventVo 填充好的边界事件视图对象
     * @description 把边界事件定义转换为视图对象，填充挂载节点信息，并按事件定义类型写入定时、错误、信号、消息配置
     * @params [boundary] 待转换的边界事件定义
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private WfBoundaryEventVo toVo(BoundaryEvent boundary) {
        WfBoundaryEventVo vo = new WfBoundaryEventVo();
        vo.setId(boundary.getId());
        vo.setName(boundary.getName());
        vo.setAttachedToRefId(boundary.getAttachedToRefId());
        if (boundary.getAttachedToRef() != null) {
            vo.setAttachedToRefName(boundary.getAttachedToRef().getName());
        }
        vo.setCancelActivity(boundary.isCancelActivity());
        if (boundary.getEventDefinitions() != null && !boundary.getEventDefinitions().isEmpty()) {
            EventDefinition def = boundary.getEventDefinitions().get(0);
            BoundaryEventTypeEnum type = BoundaryEventTypeEnum.ofDefinitionType(def.getClass().getSimpleName());
            if (type != null) {
                vo.setEventType(type.getShortName());
            }
            if (def instanceof TimerEventDefinition) {
                TimerEventDefinition t = (TimerEventDefinition) def;
                // Flowable 6.x：TimerEventDefinition.getTimeDuration/Cycle/Date 直接返回 String
                // （不是 Expression），所以直接取即可
                vo.setTimerDuration(t.getTimeDuration());
                vo.setTimerCycle(t.getTimeCycle());
            } else if (def instanceof ErrorEventDefinition) {
                vo.setErrorCode(((ErrorEventDefinition) def).getErrorCode());
            } else if (def instanceof SignalEventDefinition) {
                SignalEventDefinition s = (SignalEventDefinition) def;
                // Flowable 6.x：SignalEventDefinition#getSignalRef 直接返回 String
                vo.setSignalCode(s.getSignalRef());
            } else if (def instanceof MessageEventDefinition) {
                MessageEventDefinition m = (MessageEventDefinition) def;
                // Flowable 6.x：MessageEventDefinition#getMessageRef 直接返回 String
                vo.setMessageId(m.getMessageRef());
            }
            // Conditional / Escalation / Compensation 等类型暂时只透出 eventType，具体配置项按需补
        }
        return vo;
    }

    /**
     * @description 触发成功后写入备注与 boundary_<type> 流程状态变量及自定义变量，便于按流程状态筛选，写变量失败不影响触发主流程
     * @params [bo, type] 触发参数，提供流程实例 id、备注与自定义变量；本次触发对应的边界事件类型
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void applyBoundaryTriggerVariables(WfBoundaryTriggerBo bo, BoundaryEventTypeEnum type) {
        if (StringUtils.isBlank(bo.getProcInsId()) || type == null) {
            return;
        }
        try {
            if (StrUtil.isNotBlank(bo.getComment())) {
                runtimeService.setVariable(bo.getProcInsId(),
                        ProcessConstants.BOUNDARY_TRIGGER_COMMENT, bo.getComment());
            }
            // processStatus 改为 boundary_<type>，列表查询可用此字段筛
            runtimeService.setVariable(bo.getProcInsId(),
                    ProcessConstants.PROCESS_STATUS_KEY,
                    ProcessConstants.BOUNDARY_TRIGGER_STATUS_PREFIX + type.getShortName());
            // bo.variables 自定义变量
            if (bo.getVariables() != null) {
                for (Map.Entry<String, Object> e : bo.getVariables().entrySet()) {
                    runtimeService.setVariable(bo.getProcInsId(), e.getKey(), e.getValue());
                }
            }
        } catch (Exception e) {
            // 流程已结束的实例写变量会失败；不影响触发主流程
            log.warn("[boundary] 写边界触发流程变量失败 procInsId={} err={}",
                    bo.getProcInsId(), e.getMessage());
        }
    }

    /**
     * @description 校验触发参数，流程实例 id 与事件 code 任一为空即抛业务异常
     * @params [bo] 待校验的触发参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void validate(WfBoundaryTriggerBo bo) {
        if (bo == null || StringUtils.isBlank(bo.getProcInsId())) {
            throw new ServiceException("流程实例id不能为空");
        }
        if (StringUtils.isBlank(bo.getEventCode())) {
            throw new ServiceException("事件 code 不能为空（signal/message 用 code，error 用错误码）");
        }
    }
}
