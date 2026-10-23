package com.basic.workflow.mapper;

import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.workflow.domain.vo.WfTaskVo;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @description 工作流流程实例 Mapper 接口，提供流程与任务的多条件查询及回滚时的历史数据修正
 * @author yanbin
 * @date 2024/01/22
 */
@Mapper
@Repository
public interface WfFlowableProcessMapper {

  /**
   * @return List<Map<String, Object>> 当前用户参与过的流程实例清单
   * @description 按业务传入的用户 ID 查询该用户所有参与过的流程实例
   * @params [processQuery] 流程查询条件，含用户、分类与分页参数
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<Map<String, Object>> selectProcInsList(@Param("params") ProcessQuery processQuery);

  /**
   * @return List<WfTaskVo> 待审核任务列表
   * @description 查询所有待审核的运行态任务，按查询条件过滤
   * @params [processQuery] 流程查询条件
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskVo> selectProcessAllRuTask(ProcessQuery processQuery);

  /**
   * @return List<WfTaskVo> 当前用户的待办任务列表
   * @description 查询当前用户权限范围内可办理的待办任务，按查询条件过滤
   * @params [processQuery] 流程查询条件
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskVo> selectTodoTaskList(ProcessQuery processQuery);

  /**
   * @return List<WfTaskVo> 已完结任务列表
   * @description 查询流程已完结的历史任务，按查询条件过滤
   * @params [processQuery] 流程查询条件
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskVo> selectProcessAllHiTask(ProcessQuery processQuery);

  /**
   * @return List<WfTaskVo> 当前用户的已办任务列表
   * @description 查询当前用户已办理完成的任务，按查询条件过滤
   * @params [processQuery] 流程查询条件
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskVo> selectFinishTaskList(ProcessQuery processQuery);

  /**
   * @return List<WfTaskVo> 当前用户发起的流程列表
   * @description 查询当前用户自己发起过的流程实例，即我发起的流程
   * @params [processQuery] 流程查询条件
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskVo> selectOwnProcessList(ProcessQuery processQuery);

  /**
   * @return Integer 待办任务数量，无数据时为零
   * @description 统计当前用户的待办任务条数，供前端角标展示
   * @params [processQuery] 流程查询条件
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Integer selectTodoTaskCount(ProcessQuery processQuery);

  /**
   * @description 按任务 ID 与节点 ID 删除对应的历史活动实例记录
   * @params [taskId, activityId] 任务 ID；节点（活动）ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHisActivityInstance(@Param("taskId") String taskId,
      @Param("activityId") String activityId);

  /**
   * @description 按历史任务主键把该任务的执行实例 ID 更新为指定值，用于回滚后修正执行归属
   * @params [id, executionId] 历史任务主键；目标执行实例 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHisTaskByIdToExecutionId(@Param("id") String id,
      @Param("executionId") String executionId);

  /**
   * @description 按历史活动实例主键把该活动记录的执行实例 ID 更新为指定值，用于回滚后修正执行归属
   * @params [id, executionId] 历史活动实例主键；目标执行实例 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHisActByIdToExecutionId(@Param("id") String id,
      @Param("executionId") String executionId);

  /**
   * @return Object 历史变量值，未查到返回 null
   * @description 按执行实例 ID 与变量名查询历史变量记录的值
   * @params [executionId, key] 执行实例 ID；变量名
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Object selectActHiVariableById(@Param("executionId") String executionId,
      @Param("key") String key);

  /**
   * @return List<String> 未结束的流程实例 ID 列表
   * @description 按流程定义分类查询仍在运行的流程实例 ID
   * @params [category] 流程定义分类数组，为空时不做分类过滤
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<String> selectProcInsIdRunningByCategory(@Param("category") String[] category);

  /**
   * @description 按主键更新运行时定时作业的到期时间，供延长或缩短超时任务使用
   * @params [id, format] 定时作业主键；新的到期时间文本，按 timestamp 转换后写入
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateTimeJob(@Param("id") String id,@Param("dueDate") String format);
}
