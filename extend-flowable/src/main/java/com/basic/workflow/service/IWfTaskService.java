package com.basic.workflow.service;

import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.dto.WfUserTaskDto;
import com.basic.workflow.domain.vo.WfTaskVo;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.io.InputStream;
import java.util.List;

/**
 * @author yanbin
 * @description TODO (工作流任务管理Service)
 * @date 2023/11/3 10:37
 */
public interface IWfTaskService {

  /**
   * @return com.basic.common.core.domain.AjaxResult
   * @description TODO (审批任务)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/1/23 11:04
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean complete(WfTaskBo taskBo) throws Exception;

  /**
   * @return boolean
   * @description TODO (拒绝任务)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/1/23 11:05
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean taskReject(WfTaskBo taskBo);

  /**
   * @return boolean
   * @description TODO (退回任务)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/1/23 11:05
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean taskReturn(WfTaskBo taskBo);

  /**
   * @return java.util.List<org.flowable.bpmn.model.FlowElement>
   * @description TODO (获取所有可回退的节点)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/1/23 11:05
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<FlowElement> findReturnTaskList(WfTaskBo taskBo);

  /**
   * @return boolean
   * @description TODO (认领/签收任务)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/1/23 11:06
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean claim(WfTaskBo taskBo);

  /**
   * @return boolean
   * @description TODO (取消认领/签收任务)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/1/23 11:06
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean unClaim(WfTaskBo taskBo);

  /**
   * @return boolean
   * @description TODO (委派任务)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/1/23 11:06
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean delegateTask(WfTaskBo taskBo);

  /**
   * @return boolean
   * @description TODO (转办任务)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/1/23 11:06
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean transferTask(WfTaskBo taskBo);

  /**
   * @return boolean
   * @description TODO (取消申请终止流程)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/1/23 11:06
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean stopProcess(WfTaskBo taskBo);

  /**
   * @return boolean
   * @description TODO (撤回流程)
   * @params [taskBo]
   * @author yanbin
   * @date 2024/1/23 11:07
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean revokeProcess(WfTaskBo taskBo);

  /**
   * @return java.io.InputStream
   * @description TODO (获取流程过程图)
   * @params [procInsId]
   * @author yanbin
   * @date 2024/1/23 11:07
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  InputStream diagram(String procInsId);


  /**
   * @return java.lang.Boolean
   * @description TODO (加签)
   * @params [bo, userCode 加签人员, sign 加签类型]
   * @author yanbin
   * @date 2024/3/7 17:25
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean addSignTask(WfTaskBo taskBo, List<String> userCode, String sign);

  /**
   * @return com.basic.workflow.domain.dto.WfUserTaskDto
   * @description TODO (下一节点内容)
   * @params [vo]
   * @author yanbin
   * @date 2024/1/23 11:08
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfUserTaskDto getNextFlowNode(WfTaskVo vo) throws Exception;

  /**
   * @return com.basic.workflow.domain.dto.WfUserTaskDto
   * @description TODO (获取当前节点用户信息)
   * @params [vo]
   * @author yanbin
   * @date 2024/1/23 11:08
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfUserTaskDto getCurrentTaskNode(WfTaskVo vo);

  /**
   * @return boolean
   * @description TODO (判断当前任务是不是多实例节点)
   * @params [task]
   * @author yanbin
   * @date 2024/1/23 11:08
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean checkMultiInstance(TaskEntity task);

  /**
   * @return java.lang.String
   * @description TODO (判断下一节点是什么类型   UserTask？EndEvent)
   * @params [wfTaskVo]
   * @author yanbin
   * @date 2024/1/23 11:08
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  String getNextNode(WfTaskVo wfTaskVo);

  /**
   * @return void
   * @description TODO (处理启动第一个任务(仅处理为表达式的节点)
   * @params [procInsId]
   * @author yanbin
   * @date 2024/2/19 14:06
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void handleFlowTask(String procInsId);

  /**
   * @return java.lang.Boolean
   * @description TODO (删除流程实例)
   * @params [procInsIds]
   * @author yanbin
   * @date 2024/1/17 17:47
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean deleteProcessByIds(String[] procInsIds);

  /**
   * @return void
   * @description TODO (处理下一节点是交替节点问题)
   * @params [procInsId]
   * @author yanbin
   * @date 2024/2/21 8:37
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void handleIsRoleReplace(String procInsId, String currentUserId);

  /**
   * @return boolean
   * @description TODO (变更处理人)
   * @params [bo]
   * @author yanbin
   * @date 2024/2/21 14:07
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean editProcessTaskAssignee(WfTaskBo bo);

  /**
   * @return boolean
   * @description TODO (修改审批意见)
   * @params [bo]
   * @author yanbin
   * @date 2024/2/21 17:47
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean updateTaskComment(WfTaskBo bo);

  /**
   * @return int
   * @description TODO (废除所选流程)
   * @params [bo]
   * @author yanbin
   * @date 2024/2/22 8:59
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean abolishProcess(WfTaskBo bo);

  /**
   * @return boolean
   * @description TODO (删除所选任务)
   * @params [bo]
   * @author yanbin
   * @date 2024/2/22 9:25
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean deleteProcessTask(WfTaskBo bo);

  /**
   * @return int
   * @description TODO (保存上传的附件)
   * @params [bo]
   * @author yanbin
   * @date 2024/2/23 11:27
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean saveFileToModel(WfTaskBo bo);

  /**
   * @return void
   * @description TODO (处理第一个节点为空且选择了审批人的节点)
   * @params [procInsId, startUserCodes]
   * @author yanbin
   * @date 2024/4/18 8:36
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void handleNotBlankTask(String procInsId, List<String> startUserCodes);

  /**
   * @return java.lang.Boolean
   * @description TODO (跳转任意节点)
   * @params [bo]
   * @author yanbin
   * @date 2024/5/28 15:37
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean skipUserTask(WfTaskBo bo);

  /**
   * @return java.lang.Boolean
   * @description TODO (默认驳回到上一节点)
   * @params [bo]
   * @author yanbin
   * @date 2024/5/29 8:46
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean rejectLastTask(WfTaskBo bo);

  /**
   * @return java.lang.Boolean
   * @description TODO (根据实例id判断流程是否存在)
   * @params [procInsId]
   * @author yanbin
   * @date 2024/6/4 10:35
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean checkProcessIsExistByProcInsId(String procInsId);

  /**
   * @return com.basic.workflow.domain.vo.WfTaskVo
   * @description TODO (判断当前用户是否是当前任务的处理人)
   * @params [bo]
   * @author yanbin
   * @date 2024/6/4 14:29
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskVo checkUserIsAuditByProcInsIdAndUserIdAndGroupId(WfTaskBo bo);

  /**
   * @return com.basic.workflow.domain.vo.WfTaskVo
   * @description TODO (判断当前用户是否是当前流程的发起人)
   * @params [bo]
   * @author yanbin
   * @date 2024/6/4 14:30
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskVo checkUserIsStartByProcInsIdAndUserIdAndGroupId(WfTaskBo bo);

  /**
   * @return java.lang.Object
   * @description TODO (完成任务不通过流转回顺序会签的当前处理人的上一个人(顺序会签))
   * @params bo
   * @author yanbin
   * @date 2025/9/28 8:56
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Object mulitiSequentialUnComplete(WfTaskBo bo);
}
