package com.basic.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basic.workflow.domain.WfTaskForm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 流程流转表单 Mapper 接口，按任务、节点或流程实例查询各节点提交的表单数据
 * @author yanbin
 * @date 2024/02/28
 */
public interface WfTaskFormMapper extends BaseMapper<WfTaskForm> {

  /**
   * @return List<WfTaskForm> 该任务在该节点提交的表单记录，按提交时间升序，无记录时为空列表
   * @description 按任务 id + 节点 id 查询该次任务提交的表单记录
   * @params [taskId, activityId] taskId 任务 id；activityId 节点定义 key
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectFormJsonDataByTaskIdAndActIdToList(@Param("taskId") String taskId,
      @Param("activityId") String activityId);

  /**
   * @return List<WfTaskForm> 该流程实例在该节点提交过的表单记录，按提交时间升序
   * @description 按流程实例 id + 节点 id 查询该节点历史上所有提交的表单记录
   * @params [procInsId, activityId] procInsId 流程实例 id；activityId 节点定义 key
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectFormJsonDataByProcInsIdAndActIdToList(@Param("procInsId") String procInsId,
      @Param("activityId") String activityId);

  /**
   * @return List<WfTaskForm> 该流程实例下所有表单提交记录，按提交时间升序
   * @description 按流程实例 id 查询整个流程的表单数据，用于审批详情页回显各节点提交内容
   * @params [procInsId] 流程实例 id
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectFormDataByProcInsIdToList(@Param("procInsId") String procInsId);

  /**
   * @return WfTaskForm 该节点最近一次提交的表单记录，无记录时返回 null
   * @description 按流程实例 id + 节点 id 取该节点最新一条表单 json（按提交时间倒序取第一条）
   * @params [procInsId, activityId] procInsId 流程实例 id；activityId 节点定义 key
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskForm selectFormJsonByProcInsIdAndActId(@Param("procInsId") String procInsId,
      @Param("activityId") String activityId);

  /**
   * @return WfTaskForm 命中的表单记录（调用方主要取其中的流程实例 id），无命中时返回 null
   * @description 按字段名 + 字段值 + 节点集合反查表单记录；字段值用 PostgreSQL jsonb 运算符从 form_content 中提取，
   * 不再依赖 field / field_value 两列历史数据
   * @params [field, fieldValue, activityIdList] field 表单字段名；fieldValue 期望的字段值；activityIdList 参与匹配的节点 key 集合
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskForm selectFormDataByFieldAndFieldValueAndActId(@Param("field") String field, @Param("fieldValue") String fieldValue, @Param("activityIdList") String[] activityIdList);
}
