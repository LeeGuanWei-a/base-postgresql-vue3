package com.basic.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.workflow.domain.WfTaskForm;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (流程流转表单service接口)
 * @date 2024/02/28
 */
public interface IWfTaskFormService extends IService<WfTaskForm> {

  /**
   * @return boolean
   * @description TODO (根据实例procInsId删除流程表单)
   * @params [procInsIds]
   * @author yanbin
   * @date 2024/2/28 16:20
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean delProcessForm(List<String> procInsIds);

  /**
   * @return com.basic.workflow.domain.WfTaskForm
   * @description TODO (根据流程实例procInsId和活动activityId获取流程表单内容)---开始节点
   * @params [procInsId, activityId]
   * @author yanbin
   * @date 2024/2/28 17:07
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskForm selectTaskFormByProcInsIdAndActId(String procInsId, String activityId);

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (根据流程实例procInsId和活动activityId获取流程表单内容)----用户节点List
   * @params [procInsId, activityId]
   * @author yanbin
   * @date 2024/2/28 17:12
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> getTaskFormByProcInsIdAndActIdToList(String procInsId, String activityId);

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (根据流程任务TaskId和活动activityId获取流程表单内容)----用户节点List)
   * @params [taskId, activityId]
   * @author yanbin
   * @date 2024/2/29 9:14
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> getTaskFormByTaskIdAndActIdToList(String taskId, String activityId);

  /**
   * @return boolean
   * @description TODO (根据任务taskId删除表单内容)
   * @params [taskId]
   * @author yanbin
   * @date 2024/3/1 17:05
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean delTaskFormByTaskId(String taskId);

  /**
   * @return Boolean
   * @description TODO (保存自定义表单数据)
   * @params [wfTaskForm]
   * @author yanbin
   * @date 2024/4/15 9:36
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean saveProcessForm(WfTaskForm wfTaskForm);

  /**
   * @return void
   * @description TODO (保存自定义表单数据----直接保存)
   * @params wfTaskForm
   * @author yanbin
   * @date 2024/12/16 14:23
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void saveFormData(WfTaskForm wfTaskForm);

  /**
   * @return com.basic.workflow.domain.WfTaskForm
   * @description TODO (查找流程审批表单data)根据实例procInsid和actid
   * @params [procInsId, activityId]
   * @author yanbin
   * @date 2024/4/15 14:18
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectFormJsonDataByProcInsIdAndActIdToList(String procInsId, String activityId);

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (根据任务taskid获取所有的表单内容)
   * @params [taskId, activityId]
   * @author yanbin
   * @date 2024/4/15 14:25
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectFormJsonDataByTaskIdAndActIdToList(String taskId, String activityId);

  /**
   * @return boolean
   * @description TODO (根据实例id和节点actId删除表单内容)
   * @params [procInsId, activityId]
   * @author yanbin
   * @date 2024/4/18 16:28
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean delProcessFormByProcInsIdAndActId(String procInsId, String activityId);

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (根据实例id查询流程审批表单data)
   * @params [procInsId]
   * @author yanbin
   * @date 2024/4/18 16:36
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectFormDataByProcInsIdToList(String procInsId);

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (/查询保险单编号、借款合同编号的所有数据)
   * @params []
   * @author lijingyuan
   * @date 2024/7/16 15:50
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public List<WfTaskForm> selectGuaranteeContractNoAndLoanContractNo();

  /**
   * @return com.basic.workflow.domain.WfTaskForm
   * @description TODO (根据实例id和actid获取表单json)
   * @params id startActivityId
   * @author yanbin
   * @date 2025/1/6 11:41
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskForm selectFormJsonByProcInsIdAndActId(String procInsId, String activityId);

  /**
   * @return com.basic.workflow.domain.WfTaskForm
   * @description TODO (根据字段值和字段名，节点获取信息，主要获取的是实例id)
   * @params field
   * fieldValue
   * activityIdList
   * @author yanbin
   * @date 2025/4/10 11:57
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskForm selectFormDataByFieldAndFieldValueAndActId(String field, String fieldValue, String[] activityIdList);
}
