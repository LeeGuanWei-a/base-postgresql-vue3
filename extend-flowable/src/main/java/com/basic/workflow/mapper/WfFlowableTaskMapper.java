package com.basic.workflow.mapper;

import com.basic.workflow.domain.vo.TaskQueryParamsVo;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * @author yanbin
 * @description TODO (Task任务mapper接口)
 * @date 2023/10/26
 */
public interface WfFlowableTaskMapper {

  /**
   * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
   * @description TODO (查询代办)
   * @params [taskParams]
   * @author yanbin
   * @date 2024/1/16 10:45
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<Map<String, Object>> selectTaskModel(@Param("params") TaskQueryParamsVo taskParams);

  /**
   * @return void
   * @description TODO (根据taskID删除历史处理人信息)
   * @params [taskId]
   * @author yanbin
   * @date 2024/2/21 15:48
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHistorcIdentityLink(@Param("taskId") String taskId);

  /**
   * @return void
   * @description TODO (添加新的处理人信息)
   * @params [id, taskId, assignee, type]
   * @author yanbin
   * @date 2024/2/21 16:18
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void inserHistorcIdentityLink(@Param("id") String id,
      @Param("taskId") String taskId,
      @Param("userId") String assignee,
      @Param("type") String type);

  /**
   * @return void
   * @description TODO (根据taskID修改任务的处理人变更)
   * @params [taskId, assignee]
   * @author yanbin
   * @date 2024/2/21 16:18
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHistorcTask(@Param("taskId") String taskId, @Param("userId") String assignee);

  /**
   * @return void
   * @description TODO (根据taskID删除Flowable表的意见信息)
   * @params [taskId]
   * @author yanbin
   * @date 2024/2/22 8:43
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHistorcComment(@Param("taskId") String taskId);

  /**
   * @return void
   * @description TODO (根据taskId删除运行中的任务act_ru_task)
   * @params [taskId]
   * @author yanbin
   * @date 2024/2/22 11:46
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteTaskForTaskId(@Param("taskId") String taskId);

  /**
   * @return void
   * @description TODO (根据执行实例executionId删除运行中的执行实例act_ru_execution)
   * @params [executionId]
   * @author yanbin
   * @date 2024/2/22 11:46
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteExecutionForExecutionId(@Param("executionId") String executionId);

  /**
   * @return void
   * @description TODO (根据taskId删除运行中的act_ru_identitylink)
   * @params [taskId]
   * @author yanbin
   * @date 2024/2/22 11:46
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteRuIdentityLinkForTaskId(@Param("taskId") String taskId);

  /**
   * @return void
   * @description TODO (根据实例procInsId和指定字段名修改正常运行的参数表的数据)
   * @params [procInsId, name字段名称]
   * @author yanbin
   * @date 2024/2/22 13:51
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateRuVariable(@Param("procInsId") String procInsId, @Param("name") String name);

  /**
   * @return void
   * @description TODO (根据taskId删除历史的任务act_hi_taskinst)
   * @params [taskId]
   * @author yanbin
   * @date 2024/2/22 13:55
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHisTaskForTaskId(@Param("taskId") String taskId);

  /**
   * @return void
   * @description TODO (根据执行实例executionId删除与当前任务执行有关的参数act_ru_variable)
   * @params [executionId]
   * @author yanbin
   * @date 2024/2/22 13:59
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteRuVariableForExecutionId(@Param("executionId") String executionId);

  /**
   * @return void
   * @description TODO (根据执行实例executionId删除与当前历史任务执行有关的参数act_hi_varinst)
   * @params [executionId]
   * @author yanbin
   * @date 2024/2/22 13:59
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHiVariableForExecutionId(@Param("executionId") String executionId);

  /**
   * @return void
   * @description TODO (根据taskId删除历史表中act_hi_identitylink数据)
   * @params [taskId]
   * @author yanbin
   * @date 2024/2/22 14:02
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHiIdentityLinkForTaskId(@Param("taskId") String taskId);

  /**
   * @return void
   * @description TODO (根据执行实例executionId和指定字段名删除记录act_ru_variable)
   * @params [executionId, name]
   * @author yanbin
   * @date 2024/2/22 16:10
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteRuVariableForExecutionIdAndName(@Param("executionId") String executionId,
      @Param("name") String name);

  /**
   * @return void
   * @description TODO (根据实例procInsId和指定字段名修改历史运行的参数表的数据)
   * @params [procInsId, name]
   * @author yanbin
   * @date 2024/2/22 16:28
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHisVariable(@Param("procInsId") String procInsId, @Param("name") String name);

  /**
   * @return void
   * @description TODO (根据执行实例executionId和指定字段名删除记录act_hi_varinst)
   * @params [executionId, name]
   * @author yanbin
   * @date 2024/2/22 16:32
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHisVariableForExecutionIdAndName(@Param("executionId") String executionId,
      @Param("name") String name);

  /**
   * @return void
   * @description TODO (3更新任务的描述)
   * @params [taskId, description]
   * @author yanbin
   * @date 2024/3/8 10:27
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateTaskDescription(@Param("taskId") String taskId,
      @Param("description") String description);

  /**
   * @return void
   * @description TODO (更新流程定义的流程分类和部署id)
   * @params [procDefId, category,deploymentId]
   * @author yanbin
   * @date 2024/5/8 11:44
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateReProcDefCategoryByProcDefId(@Param("procDefId") String procDefId,
      @Param("category") String category,
      @Param("deploymentId") String deploymentId);

  /**
   * @return void
   * @description TODO (更新实例信息的流程定义)
   * @params [procInsId, procDefId]
   * @author yanbin
   * @date 2024/5/8 11:50
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHiProcInstByProcInsId(@Param("procInsId") String procInsId,
      @Param("procDefId") String procDefId);

  /**
   * @return void
   * @description TODO (修改act_hi_varinst的text和long_字段，)
   * @params procInsId key newCompleted
   * @author yanbin
   * @date 2025/9/28 13:58
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHisVariableTextByKeyAndValue(@Param("procInsId") String procInsId,
      @Param("key") String key, @Param("newCompleted") int newCompleted);

  /**
   * @return void
   * @description TODO (修改act_ru_variable的text_和long_字段)
   * @params procInsId key newCompleted
   * @author yanbin
   * @date 2025/9/28 13:58
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateRuVariableTextByKeyAndValue(@Param("procInsId") String procInsId,
      @Param("key") String key, @Param("newCompleted") int newCompleted);

  /**
   * @return void
   * @description TODO (跟新执行实例的parentId)
   * @params executionId id
   * @author yanbin
   * @date 2025/9/29 9:06
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateExecutionParentId(@Param("id") String id,@Param("parentId") String parentId);

  /**
   * @return void
   * @description TODO (根据执行实例id更新变量数据 历史,仅支持更新long和text)
   * @params executionId key value
   * @author yanbin
   * @date 2025/10/9 14:50
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHisVariableByExecutionIdToLongText(@Param("executionId") String executionId,
      @Param("key") String key, @Param("value") String value);
  /**
   * @description TODO (根据执行实例id更新变量数据 仅支持更新long和text)
   * @params id
  nrOfActiveInstances
  s
   * @return void
   * @author yanbin
   * @date 2025/10/9 15:05
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateRuVariableByExecutionIdToLongText(@Param("executionId") String executionId,
      @Param("key") String key, @Param("value") String value);
}
