package com.basic.workflow.service.wfProcessTask.wfUserTask;

import com.basic.common.core.domain.entity.SysUser;
import com.basic.constant.ExtensionConstant;
import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.flow.FindNextNodeUtil;
import com.basic.workflow.domain.dto.WfUserTaskDto;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import org.flowable.bpmn.model.*;
import cn.hutool.core.collection.CollUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description 用户任务节点信息服务，读取当前任务所属节点的类型、处理人拓展配置与会签属性
 * @author yanbin
 * @date 2024/03/01
 */
@Service
public class WfUserTaskInfoService extends WfProcessTaskBase {
    /**
     * @return WfUserTaskDto 当前节点的详细信息，含节点类型、处理人拓展配置与会签属性
     * @description 按任务ID查出当前任务，读取所属节点的多实例特性与各类处理人拓展配置，装配成当前节点详细信息
     * @params [vo] 任务信息，只需传 taskId
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfUserTaskDto getCurrentTaskNode(WfTaskVo vo) {
        if (vo.getTaskId() == null) {
            return null;
        }
        // 1. 获取当前任务
        Task task = taskService.createTaskQuery().taskId(vo.getTaskId()).singleResult();
        WfUserTaskDto wfUserTaskDto = new WfUserTaskDto();
        if (Objects.nonNull(task)) {
            // 2.获取流程定义
            ProcessDefinition processDefinition = repositoryService
                    .createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId()).singleResult();
            // 3. 获取当前活动userTask模块
            UserTask currentUserTask = wfBpmnModelService.findUserTaskForProcDefIdAndKey(processDefinition.getId(),
                    task.getTaskDefinitionKey());
            wfUserTaskDto.setUserTask(currentUserTask);
            wfUserTaskDto.setActivityId(currentUserTask.getId());
            wfUserTaskDto.setActivityName(currentUserTask.getName());
            // 4.判断当前活动是否是多实例节点
            MultiInstanceLoopCharacteristics multiInstance = extensionElementsService.getMulTiInstForUserTask(currentUserTask);
            // 5.1 获取处理人拓展   获取是否自动跳过
            wfUserTaskDto.setIsSkip(extensionElementsService.getCustomPropertyText(currentUserTask.getId(),
                    processDefinition.getId(), ExtensionConstant.IS_SKIP));
            // 5.2获取处理人拓展     获取是否是交替节点
            wfUserTaskDto.setIsRoleReplace(extensionElementsService.getCustomPropertyText(currentUserTask.getId(),
                    processDefinition.getId(), ExtensionConstant.IS_ROLE_REPLACE));
            // 5.3获取处理人拓展      获取是否是组织筛选
            wfUserTaskDto.setIsOrgScreen(extensionElementsService.getCustomPropertyText(currentUserTask.getId(),
                    processDefinition.getId(), ExtensionConstant.IS_ORG_SCREEN));
            // 5.4获取处理人拓展      获取是否是人员筛选
            wfUserTaskDto.setIsUserScreen(extensionElementsService.getCustomPropertyText(currentUserTask.getId(),
                    processDefinition.getId(), ExtensionConstant.IS_USER_SCREEN));
            // 5.5获取userTask上的拓展信息assigneeType  区分固定值，身份存储
            //选择固定值还是身份存储
            String datatype = extensionElementsService.getCustomPropertyText(currentUserTask.getId(),
                    processDefinition.getId(), ExtensionConstant.ASSIGNEE_TYPE);
            // 5.6获取userTask上的拓展信息 isDeptOrOrg  区分是组织筛选还是部门筛选
            wfUserTaskDto.setIsDeptOrOrg(extensionElementsService.getCustomPropertyText(currentUserTask.getId(),
                    processDefinition.getId(), ExtensionConstant.IS_DEPT_OR_ORG));
            // 5.7获取UserTask上的拓展信息 isNullSkip  是否节点为空跳过
            wfUserTaskDto.setIsNullSkip(extensionElementsService.getCustomPropertyText(currentUserTask.getId(),
                    processDefinition.getId(), ExtensionConstant.IS_NULL_SKIP));
            // 6.1身份存储
            // 注意：datatype 是"节点是否配了 assigneeType 扩展"的读取结果，
            // 节点没配时这里是 null。原来直接 datatype.equals("1") 会 NPE，
            // 表现为「发起流程」在"处理人为空跳过"处理阶段直接失败。
            // 用 "1".equals(datatype) 做空安全比较：null 走 else（按固定值处理）。
            if ("1".equals(datatype)) {
                datatype = "身份存储";
                // 6.1.1 添加候选用户信息
                if (currentUserTask.getCandidateUsers().size() > 0) {
                    wfUserTaskDto.setUserList(new java.util.ArrayList<>());
                }
                // 6.1.2候选角色
                else if (currentUserTask.getCandidateGroups().size() > 0) {
                    wfUserTaskDto.setRoleList(new java.util.ArrayList<>());
                }
                // 6.1.2 任务有准确处理人
                else if (currentUserTask.getAssignee() != null) {
                    fillAssignee(wfUserTaskDto, currentUserTask.getAssignee());
                }
            }
            // 6.2固定值
            else {
                datatype = "固定值";
                // 6.2.1 判断这里选择固定值，且这个选择了表达式
                if (currentUserTask.getAssignee() != null) {
                    wfUserTaskDto.setAssignee(currentUserTask.getAssignee());
                }
            }
            wfUserTaskDto.setDataType(datatype);
            // 7. 会签节点
            if (Objects.nonNull(multiInstance)) {
                wfUserTaskDto.setVars(multiInstance.getInputDataItem());
                wfUserTaskDto.setType(ProcessConstants.PROCESS_MULTI_INSTANCE);
            } else {
                // 7.1 读取自定义节点属性 判断是否是否需要动态指定任务接收人员、组
                String userType = currentUserTask.getAttributeValue(ProcessConstants.NAMASPASE, ProcessConstants.PROCESS_CUSTOM_USER_TYPE);
                wfUserTaskDto.setVars(ProcessConstants.PROCESS_APPROVAL);
                wfUserTaskDto.setType(userType);
            }
        }
        return wfUserTaskDto;
    }

    /**
     * @return WfUserTaskDto 下一节点的详细信息，无用户任务时返回网关或结束节点类型
     * @description 按当前任务与流程变量推算出下一节点，逐个装配下一用户任务的处理人拓展配置与会签或签属性
     * @params [wfTaskVo] 任务信息，含 taskId、procInsId 与流程变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfUserTaskDto getNextFlowNode(WfTaskVo wfTaskVo) {
        // 1.下一节点类型
        String type = this.getNextNode(wfTaskVo);
        // 2. 获取当前节点并找到下一步节点
        Task task = taskService.createTaskQuery().taskId(wfTaskVo.getTaskId()).singleResult();
        WfUserTaskDto wfUserTaskDto = new WfUserTaskDto();
        //放入节点类型
        wfUserTaskDto.setNodeType(type);
        if (Objects.nonNull(task)) {
            // 3. 获取当前流程所有流程变量(网关节点时需要校验表达式)
            Map<String, Object> variables = wfTaskVo.getVariables();
            List<UserTask> nextUserTask = FindNextNodeUtil.getNextUserTasks(repositoryService, task, variables);
            if (CollUtil.isNotEmpty(nextUserTask)) {
                for (UserTask userTask : nextUserTask) {
                    // 3.1 获取多实例属性
                    MultiInstanceLoopCharacteristics multiInstance = extensionElementsService.getMulTiInstForUserTask(userTask);
                    wfUserTaskDto.setUserTask(userTask);
                    wfUserTaskDto.setActivityId(userTask.getId());
                    wfUserTaskDto.setActivityName(userTask.getName());
                    // 3.2 获取处理人拓展
                    wfUserTaskDto.setIsSkip(extensionElementsService
                            .getCustomPropertyText(userTask.getId(), task.getProcessDefinitionId(), ExtensionConstant.IS_SKIP));
                    // 3.2.1  获取审批配置 拓展
                    wfUserTaskDto.setIsExamined(extensionElementsService.getCustomPropertyText(userTask.getId(),
                            task.getProcessDefinitionId(), ExtensionConstant.IS_EXAMINED));
                    // 3.3 获取userTask上的拓展信息assigneeType
                    //选择固定值还是身份存储
                    String datatype = extensionElementsService
                            .getCustomPropertyText(userTask.getId(), task.getProcessDefinitionId(), ExtensionConstant.ASSIGNEE_TYPE);
                    // 3.3.1 身份存储
                    // 同 getCurrentTaskNode 里的问题：datatype 可能为 null（节点没配 assigneeType），
                    // 用 "1".equals(datatype) 做空安全比较，null 走 else 按固定值处理
                    if ("1".equals(datatype)) {
                        datatype = "身份存储";
                        // 3.3.1.1添加候选用户信息
                        if (userTask.getCandidateUsers().size() > 0) {
                            wfUserTaskDto.setUserList(new java.util.ArrayList<>());
                        }
                        // 3.3.1.2候选角色
                        else if (userTask.getCandidateGroups().size() > 0) {
                            wfUserTaskDto.setRoleList(new java.util.ArrayList<>());

                        }
                        // 3.3.1.3 处理人
                        else if (userTask.getAssignee() != null) {
                            fillAssignee(wfUserTaskDto, userTask.getAssignee());
                        }
                    }
                    // 3.3.2固定值
                    else {
                        datatype = "固定值";
                        // 3.3.2.1判断这里选择固定值，且这个选择了表达式
                        if (userTask.getAssignee() != null) {
                            wfUserTaskDto.setAssignee(userTask.getAssignee());
                        }
                    }
                    wfUserTaskDto.setDataType(datatype);
                    // 3.4  多实例节点
                    if (Objects.nonNull(multiInstance)) {
                        wfUserTaskDto.setVars(multiInstance.getInputDataItem());
                        // 3.4.1 或签属性（completionCondition 可能为空，做空安全比较）
                        if ("${nrOfCompletedInstances > 0}".equals(multiInstance.getCompletionCondition())) {
//                            wfUserTaskDto.setType(ProcessConstants.PROCESS_MULTI_INSTANCE);
                            wfUserTaskDto.setType("或签");
                        }
                        // 3.4.2 会签属性
                        //${nrOfCompletedInstances >= nrOfInstances}
                        else {
                            wfUserTaskDto.setType("会签");
                        }
                    }
                    // 3.5 自定义节点
                    else {
                        // 读取自定义节点属性 判断是否是否需要动态指定任务接收人员、组
                        String userType = userTask.getAttributeValue(ProcessConstants.NAMASPASE, ProcessConstants.PROCESS_CUSTOM_USER_TYPE);
                        wfUserTaskDto.setVars(ProcessConstants.PROCESS_APPROVAL);
                        wfUserTaskDto.setType(userType);
                    }
                }
            }
            else if(type.equals("ExclusiveGateway")) {
                wfUserTaskDto.setNodeType("ExclusiveGateway");
            }
            else {
                // 4. 下一节点没有用户任务，默认设置为结束节点
                wfUserTaskDto.setNodeType("EndEvent");
            }
        }
        return wfUserTaskDto;
    }

    /**
     * @description 按处理人标识填充节点处理人，纯数字按用户ID查询、否则按用户名查询，查不到时只记录处理人标识不报错，发起人表达式直接跳过
     * @params [dto, assignee] 待填充的节点信息；节点上配置的处理人标识
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void fillAssignee(WfUserTaskDto dto, String assignee) {
        if (assignee == null || assignee.isEmpty()) {
            return;
        }
        // 流程发起人：运行期由 ${starterCode} 解析，不用查人员
        if ("${starterCode}".equals(assignee)) {
            return;
        }
        dto.setAssignee(assignee);
        SysUser user = null;
        if (assignee.matches("\\d+")) {
            user = sysUserService.selectUserById(Long.valueOf(assignee));
        }
        if (user == null) {
            user = sysUserService.selectUserByUserName(assignee);
        }
        if (user == null) {
            return;
        }
        if (dto.getUserList() == null) {
            dto.setUserList(new java.util.ArrayList<>());
        }
        dto.getUserList().add(user);
    }

    /**
     * @return String 下一节点类型，如 UserTask、EndEvent、ExclusiveGateway、StartEvent
     * @description 取当前节点的出口连线，按目标元素类型判断下一节点是什么模块
     * @params [wfTaskVo] 任务信息，含 taskId 与 procInsId
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public String getNextNode(WfTaskVo wfTaskVo) {
        // 1.当前任务信息
        Task task = taskService.createTaskQuery().taskId(wfTaskVo.getTaskId()).singleResult();
        // 2.获取流程发布Id信息
        String definitionId = runtimeService.createProcessInstanceQuery()
                .processInstanceId(wfTaskVo.getProcInsId())
                .singleResult()
                .getProcessDefinitionId();
        // 3.获取bpmnModel对象
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
        // 4.传节点定义key 获取当前节点
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        // 5.输出连线
        List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();
        String type = null;
        // 6.遍历返回下一个节点信息
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            // 6.1类型自己判断
            FlowElement targetFlowElement = outgoingFlow.getTargetFlowElement();
            // 6.2用户任务
            if (targetFlowElement instanceof UserTask) {
                type = "UserTask";//用户模块
            } else if (targetFlowElement instanceof EndEvent) {
                type = "EndEvent";//结束节点
            } else if (targetFlowElement instanceof ExclusiveGateway) {
                type = "ExclusiveGateway"; //网关
            } else if (targetFlowElement instanceof StartEvent) {
                type = "StartEvent";//子任务的起点
            } else {
                type = "";
            }
        }
        return type;
    }
}
