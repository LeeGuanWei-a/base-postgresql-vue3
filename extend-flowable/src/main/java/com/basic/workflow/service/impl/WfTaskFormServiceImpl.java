package com.basic.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basic.common.utils.StringUtils;
import com.basic.workflow.domain.WfTaskForm;
import com.basic.workflow.mapper.WfTaskFormMapper;
import com.basic.workflow.service.IWfTaskFormService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author yanbin
 * @description TODO (流程流转表单实现类)
 * @date 2024/02/28
 */
@Service
public class WfTaskFormServiceImpl extends ServiceImpl<WfTaskFormMapper, WfTaskForm> implements
    IWfTaskFormService {

  /**
   * @return boolean
   * @description TODO (根据实例procInsId删除流程表单)
   * @params [procInsIds]
   * @author yanbin
   * @date 2024/2/28 16:21
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean delProcessForm(List<String> procInsIds) {
    List<String> ids = new ArrayList<>();
    for (String procInsId : procInsIds) {
      QueryWrapper queryWrapper = new QueryWrapper();
      queryWrapper.eq("proc_ins_id", procInsId);
      List<WfTaskForm> wfTaskFormList = baseMapper.selectList(queryWrapper);
      if (wfTaskFormList.size() > 0) {
        wfTaskFormList.forEach(wfTaskForm -> {
          ids.add(wfTaskForm.getId());
        });
      }
    }
    if (ids.size() > 0) {
      //执行删除
      baseMapper.deleteBatchIds(ids);
    }
    return true;
  }

  /**
   * @return com.basic.workflow.domain.WfTaskForm
   * @description TODO (根据流程实例procInsId和活动activityId获取流程表单内容)
   * @params [procInsId, activityId]
   * @author yanbin
   * @date 2024/2/28 17:09
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfTaskForm selectTaskFormByProcInsIdAndActId(String procInsId, String activityId) {
    QueryWrapper<WfTaskForm> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("proc_ins_id", procInsId);
    queryWrapper.eq("activity_id", activityId);
    return baseMapper.selectOne(queryWrapper);
  }

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (根据流程实例procInsId和活动activityId获取流程表单内容)----用户节点List
   * @params [procInsId, activityId]
   * @author yanbin
   * @date 2024/2/28 17:12
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public List<WfTaskForm> getTaskFormByProcInsIdAndActIdToList(String procInsId,
      String activityId) {
    QueryWrapper<WfTaskForm> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("proc_ins_id", procInsId);
    queryWrapper.eq("activity_id", activityId);
    return baseMapper.selectList(queryWrapper);
  }

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO(根据流程任务TaskId和活动activityId获取流程表单内容)----用户节点List)
   * @params [taskId, activityId]
   * @author yanbin
   * @date 2024/2/29 9:14
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public List<WfTaskForm> getTaskFormByTaskIdAndActIdToList(String taskId, String activityId) {
    if (taskId != null) {
      QueryWrapper<WfTaskForm> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("task_id", taskId);
      queryWrapper.eq("activity_id", activityId);
      List<WfTaskForm> wfTaskFormList = baseMapper.selectList(queryWrapper);
      return wfTaskFormList;
    }
    return null;
  }

  /**
   * @return void
   * @description TODO (根据任务taskId删除表单内容)
   * @params [taskId]
   * @author yanbin
   * @date 2024/3/1 17:05
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Boolean delTaskFormByTaskId(String taskId) {
    List<String> ids = new ArrayList<>();
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("task_id", taskId);
    List<WfTaskForm> wfTaskFormList = baseMapper.selectList(queryWrapper);
    if (wfTaskFormList.size() > 0) {
      wfTaskFormList.forEach(wfTaskForm -> {
        ids.add(wfTaskForm.getId());
      });
    }
    if (ids.size() > 0) {
      //执行删除
      baseMapper.deleteBatchIds(ids);
    }
    return true;
  }

  /**
   * @return Boolean
   * @description TODO (保存自定义表单内容)
   * @params [wfTaskForm]
   * @author yanbin
   * @date 2024/4/15 9:38
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Boolean saveProcessForm(WfTaskForm wfTaskForm) {
    //实例id不为空，走的流程表单
    if (StringUtils.isNotBlank(wfTaskForm.getProcInsId())) {
      //TODO 任务id不为空，走的流程审批表单
      if (StringUtils.isNotBlank(wfTaskForm.getTaskId())) {
        List<WfTaskForm> taskForm = this.selectFormJsonDataByProcInsIdAndActIdToList(
            wfTaskForm.getProcInsId(), wfTaskForm.getActivityId());
        //存在表单内容  则删除在保存
        if (taskForm != null && taskForm.size() > 0) {
          if (this.delProcessFormByProcInsIdAndActId(wfTaskForm.getProcInsId(),
              wfTaskForm.getActivityId())) {
            this.saveFormData(wfTaskForm);
          }
        } else {
          this.saveFormData(wfTaskForm);
        }
      }
      //TODO 申请表单
      else {
        List<WfTaskForm> startForm = this.selectFormJsonDataByProcInsIdAndActIdToList(
            wfTaskForm.getProcInsId(), wfTaskForm.getActivityId());
        if (startForm != null && startForm.size() > 0) {
          if (this.delProcessFormByProcInsIdAndActId(wfTaskForm.getProcInsId(),
              wfTaskForm.getActivityId())) {
            this.saveFormData(wfTaskForm);
          }
        } else {
          this.saveFormData(wfTaskForm);
        }
      }
    }
    //TODO 参数校验：流程与业务解耦后，表单必须绑定流程实例id
    else {
      throw new RuntimeException("参数有误!");
    }
    return true;
  }

  /**
   * @return boolean
   * @description TODO (根据实例id和节点actId删除表单内容)
   * @params [procInsId, activityId]
   * @author yanbin
   * @date 2024/4/18 16:26
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public boolean delProcessFormByProcInsIdAndActId(String procInsId, String activityId) {
    List<String> ids = new ArrayList<>();
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("proc_ins_id", procInsId);
    queryWrapper.eq("activity_id", activityId);
    List<WfTaskForm> wfTaskFormList = baseMapper.selectList(queryWrapper);
    if (wfTaskFormList.size() > 0) {
      wfTaskFormList.forEach(wfTaskForm -> {
        ids.add(wfTaskForm.getId());
      });
    }
    if (ids.size() > 0) {
      //执行删除
      baseMapper.deleteBatchIds(ids);
    }
    return true;
  }

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (根据实例id查询流程审批表单data)
   * @params [procInsId]
   * @author yanbin
   * @date 2024/4/18 16:36
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public List<WfTaskForm> selectFormDataByProcInsIdToList(String procInsId) {
    return baseMapper.selectFormDataByProcInsIdToList(procInsId);
  }

  /**
   * @return void
   * @description TODO (保存表单内容)
   * @params [wfTaskForm]
   * @author yanbin
   * @date 2024/4/18 15:39
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void saveFormData(WfTaskForm wfTaskForm) {
    String content = wfTaskForm.getFormContent();
    if (StringUtils.isBlank(content)) {
      return;
    }
    List<WfTaskForm> result = new ArrayList<>();
    Map map = (Map) JSON.parse(content);
    map.forEach((key, value) -> {
      WfTaskForm form = new WfTaskForm();
      form.setId(UUID.randomUUID().toString());
      form.setField(key.toString());
      form.setFieldValue(null);
      if (value != null) {
        form.setFieldValue(value.toString());
      }
      form.setProcInsId(wfTaskForm.getProcInsId());
      form.setTaskId(wfTaskForm.getTaskId());
      form.setActivityId(wfTaskForm.getActivityId());
      form.setUserId(wfTaskForm.getUserId());
      result.add(form);
    });
    this.saveBatch(result);
  }

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (根据procInsId和actId查找流程表单dataList)
   * @params [procInsId, activityId]
   * @author yanbin
   * @date 2024/4/18 16:11
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public List<WfTaskForm> selectFormJsonDataByProcInsIdAndActIdToList(String procInsId,
      String activityId) {
    return baseMapper.selectFormJsonDataByProcInsIdAndActIdToList(procInsId, activityId);
  }

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (根据任务taskid获取所有的表单内容)
   * @params [taskId, activityId]
   * @author yanbin
   * @date 2024/4/15 14:25
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public List<WfTaskForm> selectFormJsonDataByTaskIdAndActIdToList(String taskId,
      String activityId) {
    return baseMapper.selectFormJsonDataByTaskIdAndActIdToList(taskId, activityId);
  }

  /**
   * @return java.util.List<com.basic.workflow.domain.WfTaskForm>
   * @description TODO (/查询保险单编号、借款合同编号的所有数据)
   * @params []
   * @author lijingyuan
   * @date 2024/7/16 15:50
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public List<WfTaskForm> selectGuaranteeContractNoAndLoanContractNo() {
    return baseMapper.selectGuaranteeContractNoAndLoanContractNo();
  }

  /**
   * @return com.basic.workflow.domain.WfTaskForm
   * @description TODO (根据实例id和actid获取表单json)
   * @params id startActivityId
   * @author yanbin
   * @date 2025/1/6 11:41
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfTaskForm selectFormJsonByProcInsIdAndActId(String procInsId, String activityId) {
    return baseMapper.selectFormJsonByProcInsIdAndActId(procInsId, activityId);
  }

  /**
   * @return com.basic.workflow.domain.WfTaskForm
   * @description TODO (根据字段值和字段名，节点获取信息，主要获取的是实例id)
   * @params field
   * fieldValue
   * activityIdList
   * @author yanbin
   * @date 2025/4/10 11:58
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfTaskForm selectFormDataByFieldAndFieldValueAndActId(String field, String fieldValue, String[] activityIdList) {
    return baseMapper.selectFormDataByFieldAndFieldValueAndActId(field, fieldValue, activityIdList);
  }
}
