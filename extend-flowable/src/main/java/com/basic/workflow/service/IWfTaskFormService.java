package com.basic.workflow.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.basic.workflow.domain.WfTaskForm;

import java.util.List;

/**
 * @description 流程流转表单的服务接口，按流程实例与活动节点存取任务办理过程中提交的表单数据
 * @author yanbin
 * @date 2024/02/28
 */
public interface IWfTaskFormService extends IService<WfTaskForm> {

  /**
   * @return boolean 删除成功返回 true
   * @description 按流程实例主键批量删除该实例下各节点的流程表单
   * @params [procInsIds] 流程实例主键集合
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean delProcessForm(List<String> procInsIds);

  /**
   * @return WfTaskForm 开始节点上的表单内容，不存在时返回 null
   * @description 按流程实例与活动主键查询开始节点上的流程表单内容
   * @params [procInsId, activityId] 流程实例主键；活动节点主键
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskForm selectTaskFormByProcInsIdAndActId(String procInsId, String activityId);

  /**
   * @return List<WfTaskForm> 用户节点上填写的表单内容集合
   * @description 按流程实例与活动主键查询用户节点上的流程表单内容
   * @params [procInsId, activityId] 流程实例主键；活动节点主键
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> getTaskFormByProcInsIdAndActIdToList(String procInsId, String activityId);

  /**
   * @return List<WfTaskForm> 用户节点上填写的表单内容集合
   * @description 按任务主键与活动主键查询用户节点上填写的表单内容
   * @params [taskId, activityId] 任务主键；活动节点主键
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> getTaskFormByTaskIdAndActIdToList(String taskId, String activityId);

  /**
   * @return Boolean 删除成功返回 true
   * @description 按任务主键删除该任务填写的表单内容
   * @params [taskId] 任务主键
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean delTaskFormByTaskId(String taskId);

  /**
   * @return Boolean 保存成功返回 true
   * @description 保存任务填写的自定义表单数据
   * @params [wfTaskForm] 待保存的任务表单数据
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Boolean saveProcessForm(WfTaskForm wfTaskForm);

  /**
   * @description 直接保存自定义表单数据，不做额外的关联与校验处理
   * @params [wfTaskForm] 待保存的任务表单数据
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void saveFormData(WfTaskForm wfTaskForm);

  /**
   * @return List<WfTaskForm> 该节点上保存的表单数据集合
   * @description 按流程实例与活动主键查询该节点上保存的表单数据
   * @params [procInsId, activityId] 流程实例主键；活动节点主键
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectFormJsonDataByProcInsIdAndActIdToList(String procInsId, String activityId);

  /**
   * @return List<WfTaskForm> 该任务在各节点上保存的表单数据集合
   * @description 按任务主键与活动主键查询该任务保存的全部表单数据
   * @params [taskId, activityId] 任务主键；活动节点主键
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectFormJsonDataByTaskIdAndActIdToList(String taskId, String activityId);

  /**
   * @return boolean 删除成功返回 true
   * @description 按流程实例与活动主键删除该节点上的表单内容
   * @params [procInsId, activityId] 流程实例主键；活动节点主键
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  boolean delProcessFormByProcInsIdAndActId(String procInsId, String activityId);

  /**
   * @return List<WfTaskForm> 该实例下各节点保存的表单数据集合
   * @description 按流程实例主键查询其全部审批表单数据
   * @params [procInsId] 流程实例主键
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectFormDataByProcInsIdToList(String procInsId);

  /**
   * @return WfTaskForm 该节点的表单 JSON 数据，不存在时返回 null
   * @description 按流程实例与活动主键查询节点上的表单 JSON
   * @params [procInsId, activityId] 流程实例主键；活动节点主键
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskForm selectFormJsonByProcInsIdAndActId(String procInsId, String activityId);

  /**
   * @return WfTaskForm 匹配的表单数据，未匹配时返回 null
   * @description 按字段名、字段值与活动主键列表反查表单数据，主要用于取其中的流程实例主键
   * @params [field, fieldValue, activityIdList] 表单字段名；表单字段值；待匹配的活动节点主键数组
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskForm selectFormDataByFieldAndFieldValueAndActId(String field, String fieldValue, String[] activityIdList);
}
