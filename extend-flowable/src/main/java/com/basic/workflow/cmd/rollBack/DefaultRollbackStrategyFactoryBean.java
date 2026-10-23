package com.basic.workflow.cmd.rollBack;


import com.basic.workflow.cmd.rollBack.impl.*;
import org.flowable.bpmn.model.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @description 撤回策略工厂，解析历史任务所在流程图，按当前节点与后继节点场景创建对应的回滚策略
 * @author Zengjunlong
 * @date 17:53 2024/3/6
 */
@Component
public class DefaultRollbackStrategyFactoryBean implements RollbackStrategyFactory {

    private Logger LOGGER = LoggerFactory.getLogger(DefaultRollbackStrategyFactoryBean.class);

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;


    /**
     * @return RollbackOperateStrategy 匹配该历史任务场景的回滚策略，无法识别场景时返回 null
     * @description 解析历史任务所在的流程图，取当前节点与后继节点信息后创建对应的回滚策略
     * @params [hisTask] 待回退的历史任务
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public RollbackOperateStrategy createStrategy(HistoricTaskInstance hisTask) {

        BpmnModel bpmnModel = repositoryService.getBpmnModel(hisTask.getProcessDefinitionId());

        RollbackParamsTemplate template = new RollbackParamsTemplate();

        template.setHisTask(hisTask);
        // 获取当前任务的节点信息
        getThisUserTask(template, bpmnModel, hisTask);
        // 获取下一节点信息
        getNextElementInfo(template, bpmnModel);
        // 创建策略
        RollbackOperateStrategy strategy = createStrategyInstance(template);

        return strategy;
    }

    /**
     * @return boolean 会签节点上仍有未完成任务时返回 true
     * @description 判断当前节点是否会签，且流程实例中该节点仍存在未完成任务
     * @params [template] 回滚参数模板
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public boolean currentMultiInstanceTaskUnfinished(RollbackParamsTemplate template) {

        if (template.getCurrentTaskElement().getLoopCharacteristics() == null) {
            LOGGER.info("当前任务节点不是会签节点");
            return false;
        }

        long count = taskService.createTaskQuery()
                .processInstanceId(template.getHisTask().getProcessInstanceId())
                .taskDefinitionKey(template.getHisTask().getTaskDefinitionKey())
                .count();
        if (count > 0) {
            LOGGER.info("具有未完成当前节点任务");
            return true;
        }

        return false;
    }

    /**
     * @return RollbackOperateStrategy 按场景创建的回滚策略，场景未匹配时返回 null
     * @description 依据当前节点是否会签、会签是否已完成以及后继节点类型选择对应的回滚策略实现
     * @params [template] 回滚参数模板
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private RollbackOperateStrategy createStrategyInstance(RollbackParamsTemplate template) {

        // 处理正在执行会签节点
        if (currentMultiInstanceTaskUnfinished(template)) {
            LOGGER.info("-撤回 正在执行会签 策略");
            return new ActiveMultiInstanceTaskRollbackOperateStrategy(template);
        }

        // 默认节点处理策略
        if (template.getCurrentTaskElement().getLoopCharacteristics() == null
                && template.getGatewayMap().isEmpty()
                && !template.getNextUserTaskList().isEmpty()) {
            LOGGER.info("-撤回 普通任务 策略");
            return new NextDefaultUserTaskRollbackOperateStrategy(template);
        }

        // 下一节点 嵌入式子流程
        if (template.getCurrentTaskElement().getLoopCharacteristics() == null
                && template.getGatewayMap().isEmpty()
                && !template.getSubProcessMap().isEmpty()) {
            LOGGER.info("-撤回 嵌入式子流程 策略");
            return new NextSubProcessRollbackOperateStrategy(template);
        }

        // 下一节点 调用式子流程
        if (template.getCurrentTaskElement().getLoopCharacteristics() == null
                && template.getGatewayMap().isEmpty()
                && !template.getCallActivityMap().isEmpty()) {
            LOGGER.info("-撤回 调用式子流程 策略");
            return new NextCallActivityRollbackOperateStrategy(template);
        }

        // 下一节点 网关,多级网关
        if (template.getCurrentTaskElement().getLoopCharacteristics() == null
                && !template.getGatewayMap().isEmpty()) {
            LOGGER.info("-撤回 网关, 多级网关 策略");
            return new DefaultTaskNextGatewayRollbackOperateStrategy(template);
        }

        // 会签已完成
        if (template.getCurrentTaskElement().getLoopCharacteristics() != null) {

            if ( template.getGatewayMap().isEmpty()
                    && !template.getNextUserTaskList().isEmpty()) {
                LOGGER.info("-撤回 已完成会签,下一节点普通任务 策略");
                return new CompletedMultiInstanceTaskAndNextDefaultTaskRollbackOperateStrategy(template);
            }else{
                LOGGER.info("-撤回 已完成会签,下一节点网关任务 策略");
                return new CompletedMultiInstanceTaskAndNextDefaultGateWayTaskRollbackOperateStrategy(template);
            }


        }


        return null;
    }


    /**
     * @description 按当前任务所在容器确定起算位置，再收集其出口连线上的后继节点信息
     * @params [template, bpmnModel] 回滚参数模板；流程模型对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void getNextElementInfo(RollbackParamsTemplate template, BpmnModel bpmnModel) {

        if (null != template.getCurrentSubProcess()) {
            LOGGER.info("当前任务存在于 SubProcess");
            getNextElementInfo(template, template.getCurrentSubProcess(), template.getCurrentTaskElement().getOutgoingFlows());
            return;
        }
        LOGGER.info("当前任务存在于 bpmnModel");
        // 主线流程图
        getNextElementInfo(template, bpmnModel.getMainProcess(), template.getCurrentTaskElement().getOutgoingFlows());
    }

    /**
     * @description 遍历出口连线并记入模板，按用户任务、网关、子流程、调用活动分类收集，遇网关继续向下递归
     * @params [template, flowElementsContainer, outgoingFlows] 回滚参数模板；当前节点所在的流程容器；待遍历的出口连线集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void getNextElementInfo(RollbackParamsTemplate template, FlowElementsContainer flowElementsContainer, List<SequenceFlow> outgoingFlows) {

        for (SequenceFlow flow : outgoingFlows) {

            template.getNextFlowIdList().add(flow.getId());
            template.getOutGoingMap().put(flow.getId(), flow);

            // 下一节点
            FlowElement flowElement = flowElementsContainer.getFlowElement(flow.getTargetRef());
            template.getNextFlowIdList().add(flowElement.getId());

            if (flowElement instanceof UserTask) {
                LOGGER.info("下一节点：UserTask");
                template.getNextUserTaskList().add((UserTask) flowElement);
            } else if (flowElement instanceof Gateway) {
                LOGGER.info("下一节点：Gateway");
                Gateway gateway = ((Gateway) flowElement);
                template.getGatewayMap().put(gateway.getId(), gateway);
                getNextElementInfo(template, flowElementsContainer, gateway.getOutgoingFlows());
            } else if (flowElement instanceof SubProcess) {
                LOGGER.info("下一节点：SubProcess");
                SubProcess subProcess = (SubProcess) flowElement;
                template.getSubProcessMap().put(subProcess.getId(), subProcess);
            } else if (flowElement instanceof CallActivity) {
                LOGGER.info("下一节点：CallActivity");
                CallActivity callActivity = (CallActivity) flowElement;
                template.getCallActivityMap().put(callActivity.getId(), callActivity);
            }
        }
    }

    /**
     * @description 定位历史任务对应的用户任务节点，主流程找不到时再深入嵌入式子流程查找
     * @params [template, bpmnModel, hisTask] 回滚参数模板；流程模型对象；待回退的历史任务
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void getThisUserTask(RollbackParamsTemplate template, BpmnModel bpmnModel, HistoricTaskInstance hisTask) {

        FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement(hisTask.getTaskDefinitionKey());
        if (null != flowElement && flowElement instanceof UserTask) {
            LOGGER.info("获取撤回任务节点");
            template.setCurrentTaskElement((UserTask) flowElement);
            return;
        }

        for (FlowElement item : bpmnModel.getMainProcess().getFlowElements()) {
            if (item instanceof SubProcess) {
                flowElement = ((SubProcess) item).getFlowElement(hisTask.getTaskDefinitionKey());
                if (null != flowElement) {
                    LOGGER.info("当前节点存在于嵌入式子流程");
                    template.setCurrentTaskElement((UserTask) flowElement);
                    template.setCurrentSubProcess((SubProcess) item);
                    return;
                }
            }
        }

        LOGGER.error("没有获取撤回任务节点");


        // TODO 嵌入子流程 场景
    }


}
