package com.basic.workflow.service;

import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.dto.WfUserTaskDto;
import com.basic.workflow.domain.vo.WfTaskVo;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.io.InputStream;
import java.util.List;

/**
 * @description 工作流任务管理服务接口，定义任务审批、驳回、转办、加签、撤回等操作
 * @author yanbin
 * @date 2023/11/3 10:37
 */
public interface IWfTaskService {

  /**
   * @return Boolean 审批是否成功
   * @description 审批通过当前任务，按任务参数把流程推进到下一节点
   * @params [taskBo] 任务参数，含任务ID、审批意见与流程变量
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean complete(WfTaskBo taskBo) throws Exception;

  /**
   * @return boolean 拒绝是否成功
   * @description 拒绝当前审批任务，按拒绝结果结束该任务的审批
   * @params [taskBo] 任务参数，含任务ID与拒绝意见
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean taskReject(WfTaskBo taskBo);

  /**
   * @return boolean 退回是否成功
   * @description 把当前任务退回到指定的历史节点
   * @params [taskBo] 任务参数，含任务ID与回退目标节点
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean taskReturn(WfTaskBo taskBo);

  /**
   * @return List<FlowElement> 可回退的历史节点集合
   * @description 查询当前任务可以回退到的历史节点列表
   * @params [taskBo] 任务参数，含任务ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<FlowElement> findReturnTaskList(WfTaskBo taskBo);

  /**
   * @return boolean 认领是否成功
   * @description 认领（签收）候选任务，把任务处理人置为当前用户
   * @params [taskBo] 任务参数，含任务ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean claim(WfTaskBo taskBo);

  /**
   * @return boolean 取消认领是否成功
   * @description 取消认领（签收）任务，清除该任务的处理人
   * @params [taskBo] 任务参数，含任务ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean unClaim(WfTaskBo taskBo);

  /**
   * @return boolean 委派是否成功
   * @description 把当前任务委派给指定处理人办理
   * @params [taskBo] 任务参数，含任务ID与被委派人
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean delegateTask(WfTaskBo taskBo);

  /**
   * @return boolean 转办是否成功
   * @description 把当前任务转办给指定处理人
   * @params [taskBo] 任务参数，含任务ID与接收人
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean transferTask(WfTaskBo taskBo);

  /**
   * @return boolean 终止是否成功
   * @description 撤销申请并终止整个流程实例
   * @params [taskBo] 任务参数，含流程实例ID与终止原因
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean stopProcess(WfTaskBo taskBo);

  /**
   * @return boolean 撤回是否成功
   * @description 撤回已提交的流程，把流程退回发起节点
   * @params [taskBo] 任务参数，含流程实例ID与任务信息
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean revokeProcess(WfTaskBo taskBo);

  /**
   * @return InputStream 流程图图片流
   * @description 生成流程实例的运行过程图并返回图片流
   * @params [procInsId] 流程实例ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  InputStream diagram(String procInsId);


  /**
   * @return Boolean 加签是否成功
   * @description 给当前任务按加签类型添加加签人员
   * @params [taskBo, userCode, sign] taskBo 任务参数；userCode 加签人员；sign 加签类型
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean addSignTask(WfTaskBo taskBo, List<String> userCode, String sign);

  /**
   * @return WfUserTaskDto 下一节点的用户任务信息
   * @description 计算当前任务流转到的下一节点及其处理人信息
   * @params [vo] 任务视图参数
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfUserTaskDto getNextFlowNode(WfTaskVo vo) throws Exception;

  /**
   * @return WfUserTaskDto 当前节点的用户任务信息
   * @description 获取当前节点的节点信息与处理人
   * @params [vo] 任务视图参数
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfUserTaskDto getCurrentTaskNode(WfTaskVo vo);

  /**
   * @return boolean 是否多实例节点
   * @description 判断当前任务是否属于多实例节点
   * @params [task] 流程引擎任务实体
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean checkMultiInstance(TaskEntity task);

  /**
   * @return String 下一节点类型标识，如 UserTask 或 EndEvent
   * @description 判断下一节点是什么类型
   * @params [wfTaskVo] 任务视图参数
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  String getNextNode(WfTaskVo wfTaskVo);

  /**
   * @description 处理流程启动后的第一个任务，仅处理处理人配置为表达式的节点
   * @params [procInsId] 流程实例ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void handleFlowTask(String procInsId);

  /**
   * @return Boolean 删除是否成功
   * @description 按流程实例ID数组批量删除流程实例
   * @params [procInsIds] 要删除的流程实例ID数组
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean deleteProcessByIds(String[] procInsIds);

  /**
   * @description 处理下一节点为交替节点时的处理人分配问题
   * @params [procInsId, currentUserId] procInsId 流程实例ID；currentUserId 当前用户ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void handleIsRoleReplace(String procInsId, String currentUserId);

  /**
   * @return boolean 变更是否成功
   * @description 变更流程任务的当前处理人
   * @params [bo] 任务参数，含任务ID与新处理人
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean editProcessTaskAssignee(WfTaskBo bo);

  /**
   * @return boolean 修改是否成功
   * @description 修改任务上已提交的审批意见
   * @params [bo] 任务参数，含任务ID与新的审批意见
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean updateTaskComment(WfTaskBo bo);

  /**
   * @return boolean 废除是否成功
   * @description 废除所选流程实例
   * @params [bo] 任务参数，含要废除的流程实例信息
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean abolishProcess(WfTaskBo bo);

  /**
   * @return boolean 删除是否成功
   * @description 删除所选的流程任务
   * @params [bo] 任务参数，含要删除的任务信息
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean deleteProcessTask(WfTaskBo bo);

  /**
   * @return boolean 保存是否成功
   * @description 保存流程审批时上传的附件
   * @params [bo] 任务参数，含附件与所属流程实例信息
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean saveFileToModel(WfTaskBo bo);

  /**
   * @description 处理第一个节点处理人为空且发起时已选定审批人的节点分配
   * @params [procInsId, startUserCodes] procInsId 流程实例ID；startUserCodes 发起时选定的审批人
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void handleNotBlankTask(String procInsId, List<String> startUserCodes);

  /**
   * @return Boolean 跳转是否成功
   * @description 把当前任务跳转到指定的流程节点
   * @params [bo] 任务参数，含任务ID与目标节点
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean skipUserTask(WfTaskBo bo);

  /**
   * @return Boolean 驳回是否成功
   * @description 默认把当前任务驳回到上一个节点
   * @params [bo] 任务参数，含任务ID与驳回意见
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean rejectLastTask(WfTaskBo bo);

  /**
   * @return Boolean 流程实例是否存在
   * @description 按流程实例ID判断该流程是否还存在
   * @params [procInsId] 流程实例ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean checkProcessIsExistByProcInsId(String procInsId);

  /**
   * @return WfTaskVo 当前用户待审批的任务视图对象
   * @description 判断当前用户是否为当前任务的处理人
   * @params [bo] 任务参数，含流程实例ID、用户ID与用户组ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskVo checkUserIsAuditByProcInsIdAndUserIdAndGroupId(WfTaskBo bo);

  /**
   * @return WfTaskVo 当前用户作为发起人的流程视图对象
   * @description 判断当前用户是否为当前流程的发起人
   * @params [bo] 任务参数，含流程实例ID、用户ID与用户组ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskVo checkUserIsStartByProcInsIdAndUserIdAndGroupId(WfTaskBo bo);

  /**
   * @return Object 退回结果
   * @description 顺序会签场景下完成任务不通过时，把任务流转回当前处理人的上一个人
   * @params [bo] 任务参数，含任务ID与审批意见
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Object mulitiSequentialUnComplete(WfTaskBo bo);
}
