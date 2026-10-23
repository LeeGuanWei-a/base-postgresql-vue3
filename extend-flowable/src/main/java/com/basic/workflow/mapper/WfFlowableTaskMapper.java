package com.basic.workflow.mapper;

import com.basic.workflow.domain.vo.TaskQueryParamsVo;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * @description 任务 Mapper，直接读写 act_ru_task、act_hi_taskinst、act_hi_identitylink 等引擎表
 * @author yanbin
 * @date 2023/10/26
 */
public interface WfFlowableTaskMapper {

  /**
   * @return List<Map<String, Object>> 待办任务原始行，含任务 id、名称、办理人、候选人、流程名与发起人等字段
   * @description 关联 act_ru_task / act_ru_execution / tbl_flow_extend_procinst / act_ru_identitylink 查询待办任务，
   * 按 userCode 匹配「办理人是我」或「未签收且我在候选人/候选组中」；
   * 流程与业务解耦后引擎 BUSINESS_KEY_ 恒为空，故已移除原先的 BUSINESS_KEY_ 非空过滤
   * @params [taskParams] 查询条件，含 userCode（当前用户）、流程名、分类、状态等
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<Map<String, Object>> selectTaskModel(@Param("params") TaskQueryParamsVo taskParams);

  /**
   * @description 按任务 id 删除 act_hi_identitylink 中该任务的历史办理人、候选人、候选组关联记录
   * @params [taskId] 任务 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHistorcIdentityLink(@Param("taskId") String taskId);

  /**
   * @description 向 act_hi_identitylink 插入一条历史办理人关联记录，create_time_ 取数据库当前时间；
   * 某个入参为 null 时对应列不写入
   * @params [id, taskId, assignee, type] id 关联记录主键；taskId 任务 id；assignee 写入 user_id_ 的用户 id；type 关联类型（candidate / assignee 等）
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void inserHistorcIdentityLink(@Param("id") String id,
      @Param("taskId") String taskId,
      @Param("userId") String assignee,
      @Param("type") String type);

  /**
   * @description 按任务 id 把 act_hi_taskinst 中该历史任务的办理人（assignee_）改成指定用户
   * @params [taskId, assignee] taskId 历史任务 id；assignee 新办理人用户 id（对应 SQL 中的 #{userId}）
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHistorcTask(@Param("taskId") String taskId, @Param("userId") String assignee);

  /**
   * @description 按任务 id 删除 act_hi_comment 中该任务的审批意见，只删 action_ 为 AddComment 的记录，其它动作的评论保留
   * @params [taskId] 任务 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHistorcComment(@Param("taskId") String taskId);

  /**
   * @description 按任务 id 删除 act_ru_task 中运行中的该条待办任务，用于回退或清理时把任务从运行态表里拿掉
   * @params [taskId] 任务 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteTaskForTaskId(@Param("taskId") String taskId);

  /**
   * @description 按执行实例 id 删除 act_ru_execution 中运行中的该条执行实例
   * @params [executionId] 执行实例 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteExecutionForExecutionId(@Param("executionId") String executionId);

  /**
   * @description 按任务 id 删除 act_ru_identitylink 中该任务的运行态办理人、候选人关联
   * @params [taskId] 任务 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteRuIdentityLinkForTaskId(@Param("taskId") String taskId);

  /**
   * @description 按流程实例 id 与变量名，把 act_ru_variable 中该变量的 long_ 与 text_ 各自按整数减 1（PostgreSQL 的 ::integer 运算）
   * @params [procInsId, name] procInsId 流程实例 id；name 变量名
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateRuVariable(@Param("procInsId") String procInsId, @Param("name") String name);

  /**
   * @description 按任务 id 删除 act_hi_taskinst 中该条历史任务记录
   * @params [taskId] 历史任务 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHisTaskForTaskId(@Param("taskId") String taskId);

  /**
   * @description 按执行实例 id 删除 act_ru_variable 中挂在该执行实例上的全部运行态流程变量
   * @params [executionId] 执行实例 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteRuVariableForExecutionId(@Param("executionId") String executionId);

  /**
   * @description 按执行实例 id 删除 act_hi_varinst 中挂在该执行实例上的全部历史变量
   * @params [executionId] 执行实例 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHiVariableForExecutionId(@Param("executionId") String executionId);

  /**
   * @description 按任务 id 删除历史表 act_hi_identitylink 中该任务的历史办理人关联记录
   * @params [taskId] 任务 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHiIdentityLinkForTaskId(@Param("taskId") String taskId);

  /**
   * @description 按执行实例 id 与变量名精确删除 act_ru_variable 中指定的那条运行态变量记录
   * @params [executionId, name] executionId 执行实例 id；name 变量名
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteRuVariableForExecutionIdAndName(@Param("executionId") String executionId,
      @Param("name") String name);

  /**
   * @description 按流程实例 id 与变量名，把 act_hi_varinst 中该历史变量的 long_ 与 text_ 各自按整数减 1（PostgreSQL 的 ::integer 运算）
   * @params [procInsId, name] procInsId 流程实例 id；name 变量名
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHisVariable(@Param("procInsId") String procInsId, @Param("name") String name);

  /**
   * @description 按执行实例 id 与变量名精确删除 act_hi_varinst 中指定的那条历史变量记录
   * @params [executionId, name] executionId 执行实例 id；name 变量名
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHisVariableForExecutionIdAndName(@Param("executionId") String executionId,
      @Param("name") String name);

  /**
   * @description 按任务 id 更新 act_ru_task 中该运行态任务的描述（description_）
   * @params [taskId, description] taskId 任务 id；description 新的任务描述
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateTaskDescription(@Param("taskId") String taskId,
      @Param("description") String description);

  /**
   * @description 按流程定义 id 更新 act_re_procdef 的流程分类与部署 id；现有 SQL 的 set 子句用 and 连接两个赋值，实际只有 category_ 生效
   * @params [procDefId, category, deploymentId] procDefId 流程定义 id；category 流程分类编码；deploymentId 部署 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateReProcDefCategoryByProcDefId(@Param("procDefId") String procDefId,
      @Param("category") String category,
      @Param("deploymentId") String deploymentId);

  /**
   * @description 按流程实例 id 更新 act_hi_procinst 中该历史实例的流程定义 id（proc_def_id_），用于把实例挂到另一版流程定义上
   * @params [procInsId, procDefId] procInsId 流程实例 id；procDefId 新的流程定义 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHiProcInstByProcInsId(@Param("procInsId") String procInsId,
      @Param("procDefId") String procDefId);

  /**
   * @description 按流程实例 id 与变量名，把 act_hi_varinst 中该历史变量的 long_ 与 text_ 都刷成同一个整数 newCompleted
   * @params [procInsId, key, newCompleted] procInsId 流程实例 id；key 变量名；newCompleted 同时写入 long_ 与 text_ 的新值
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHisVariableTextByKeyAndValue(@Param("procInsId") String procInsId,
      @Param("key") String key, @Param("newCompleted") int newCompleted);

  /**
   * @description 按流程实例 id 与变量名，把 act_ru_variable 中该变量的 long_ 与 text_ 都刷成同一个整数 newCompleted
   * @params [procInsId, key, newCompleted] procInsId 流程实例 id；key 变量名；newCompleted 同时写入 long_ 与 text_ 的新值
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateRuVariableTextByKeyAndValue(@Param("procInsId") String procInsId,
      @Param("key") String key, @Param("newCompleted") int newCompleted);

  /**
   * @description 按执行实例 id 更新 act_ru_execution 的 parent_id_，用于修正执行树的父子关系
   * @params [id, parentId] id 执行实例 id；parentId 新的父执行实例 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateExecutionParentId(@Param("id") String id,@Param("parentId") String parentId);

  /**
   * @description 按执行实例 id 与变量名，把 act_hi_varinst 中该历史变量的 long_ 与 text_ 都刷成同一段文本 value，用于把变量值改成超长文本（TEXT 列）
   * @params [executionId, key, value] executionId 执行实例 id；key 变量名；value 要写入的文本值
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHisVariableByExecutionIdToLongText(@Param("executionId") String executionId,
      @Param("key") String key, @Param("value") String value);
  /**
   * @description 按执行实例 id 与变量名，把 act_ru_variable 中该运行态变量的 long_ 与 text_ 都刷成同一段文本 value，用于把变量值改成超长文本（TEXT 列）
   * @params [executionId, key, value] executionId 执行实例 id；key 变量名；value 要写入的文本值
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateRuVariableByExecutionIdToLongText(@Param("executionId") String executionId,
      @Param("key") String key, @Param("value") String value);
}
