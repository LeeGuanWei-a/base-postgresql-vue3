package com.basic.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basic.workflow.domain.WfTaskForm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (流程流转表单Mapper)
 * @date 2024/02/28
 */
public interface WfTaskFormMapper extends BaseMapper<WfTaskForm> {

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (根据任务taskid获取所有的表单内容)
   * @params [taskId, activityId]
   * @author yanbin
   * @date 2024/4/15 14:26
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectFormJsonDataByTaskIdAndActIdToList(@Param("taskId") String taskId,
      @Param("activityId") String activityId);

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (根据procInsId和actId查找流程表单dataList)
   * @params [procInsId, activityId]
   * @author yanbin
   * @date 2024/4/18 16:13
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectFormJsonDataByProcInsIdAndActIdToList(@Param("procInsId") String procInsId,
      @Param("activityId") String activityId);

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (根据实例id查询流程审批表单data)
   * @params [procInsId]
   * @author yanbin
   * @date 2024/4/18 16:36
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectFormDataByProcInsIdToList(@Param("procInsId") String procInsId);

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (查询保险单编号、借款合同编号的所有数据)
   * @params [procInsId]
   * @author yanbin
   * @date 2024/4/18 16:36
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  List<WfTaskForm> selectGuaranteeContractNoAndLoanContractNo();

  /**
   * @return com.basic.workflow.domain.WfTaskForm
   * @description TODO (查询字段详情)
   * @params wfTaskForm
   * @author yanbin
   * @date 2024/12/31 11:17
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskForm selectTaskFormInfo(WfTaskForm wfTaskForm);

  /**
   * @return com.basic.workflow.domain.WfTaskForm
   * @description TODO (根据实例id和actid获取表单json)
   * @params procInsId activityId
   * @author yanbin
   * @date 2025/1/6 11:42
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskForm selectFormJsonByProcInsIdAndActId(@Param("procInsId") String procInsId,
      @Param("activityId") String activityId);

  /**
   * @return com.basic.workflow.domain.WfTaskForm
   * @description TODO (根据字段值和字段名，节点获取信息，主要获取的是实例id)
   * @params field
   * fieldValue
   * activityIdList
   * @author yanbin
   * @date 2025/4/10 11:59
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  WfTaskForm selectFormDataByFieldAndFieldValueAndActId(@Param("field") String field, @Param("fieldValue") String fieldValue, @Param("activityIdList") String[] activityIdList);
}
