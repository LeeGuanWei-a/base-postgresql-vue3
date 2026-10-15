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
 * @author yanbin
 * @description TODO (工作流/流程实例mapper接口)
 * @date 2024/01/22
 */
@Mapper
@Repository
public interface WfFlowableProcessMapper {

  /**
   * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
   * @description TODO (根据业务传的用户id  获取当前用户所有参与过的流程)
   * @params [processQuery]
   * @author yanbin
   * @date 2024/1/22 14:27
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<Map<String, Object>> selectProcInsList(@Param("params") ProcessQuery processQuery);

  /**
   * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
   * @description TODO (查询待审核的任务)
   * @params [processQuery]
   * @author yanbin
   * @date 2024/5/10 16:26
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskVo> selectProcessAllRuTask(ProcessQuery processQuery);

  /**
   * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
   * @description TODO (获取代办任务)
   * @params [processQuery]
   * @author yanbin
   * @date 2024/5/10 16:26
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskVo> selectTodoTaskList(ProcessQuery processQuery);

  /**
   * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
   * @description TODO (查询已完结的任务)
   * @params [processQuery]
   * @author yanbin
   * @date 2024/5/29 11:18
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskVo> selectProcessAllHiTask(ProcessQuery processQuery);

  /**
   * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
   * @description TODO (查询已办任务)
   * @params [processQuery]
   * @author yanbin
   * @date 2024/5/29 11:18
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskVo> selectFinishTaskList(ProcessQuery processQuery);

  /**
   * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
   * @description TODO (查询我的流程 已发)
   * @params [processQuery]
   * @author yanbin
   * @date 2024/6/13 11:23
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskVo> selectOwnProcessList(ProcessQuery processQuery);

  /**
   * @return java.lang.Integer
   * @description TODO (查询待办任务数
   * @params processQuery
   * @author yanbin
   * @date 2025/7/21 10:01
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Integer selectTodoTaskCount(ProcessQuery processQuery);

  /**
   * @return void
   * @description TODO (根据任务id+节点id删除节点)
   * @params taskId activityId
   * @author yanbin
   * @date 2025/9/23 17:42
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void deleteHisActivityInstance(@Param("taskId") String taskId,
      @Param("activityId") String activityId);

  /**
   * @return void
   * @description TODO (根据历史任务id，更新执行实例id)
   * @params id executionId
   * @author yanbin
   * @date 2025/10/9 9:49
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHisTaskByIdToExecutionId(@Param("id") String id,
      @Param("executionId") String executionId);

  /**
   * @return void
   * @description TODO (根据历史节点id，更新执行实例id)
   * @params id executionId
   * @author yanbin
   * @date 2025/10/9 9:49
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  void updateHisActByIdToExecutionId(@Param("id") String id,
      @Param("executionId") String executionId);

  /**
   * @return java.lang.Object
   * @description TODO (根据执行实例id和key查询历史变量)
   * @params executionId key
   * @author yanbin
   * @date 2025/10/9 14:36
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Object selectActHiVariableById(@Param("executionId") String executionId,
      @Param("key") String key);

  /**
   * @return java.util.List<java.lang.String>
   * @description TODO (根据分类查询运行的实例id)
   * @params category
   * @author yanbin
   * @date 2025/10/20 11:35
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<String> selectProcInsIdRunningByCategory(@Param("category") String[] category);

  void updateTimeJob(@Param("id") String id,@Param("dueDate") String format);
}
