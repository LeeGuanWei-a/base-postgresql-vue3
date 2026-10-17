package com.basic.workflow.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.basic.common.exception.ServiceException;
import com.basic.common.utils.StringUtils;
import com.basic.workflow.domain.WfTaskForm;
import com.basic.workflow.mapper.WfTaskFormMapper;
import com.basic.workflow.service.IWfTaskFormService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description 流程流转表单业务实现类，保存并查询各流程实例在任务节点提交的表单数据
 * @author yanbin
 * @date 2024/02/28
 */
@Slf4j
@Service
public class WfTaskFormServiceImpl extends ServiceImpl<WfTaskFormMapper, WfTaskForm> implements
    IWfTaskFormService {

  /**
   * @return boolean 删除结果，流程实例 ID 集合为空时直接返回 true
   * @description 按流程实例 ID 集合批量删除流程表单，入参为空时不做删除直接返回
   * @params [procInsIds] 要清理表单的流程实例 ID 集合
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean delProcessForm(List<String> procInsIds) {
    if (procInsIds == null || procInsIds.isEmpty()) {
      return true;
    }
    return this.remove(Wrappers.<WfTaskForm>lambdaQuery()
        .in(WfTaskForm::getProcInsId, procInsIds));
  }

  /**
   * @return WfTaskForm 该节点最新一条流程表单，无记录时为 null
   * @description 按流程实例与活动节点取最新一条流程表单内容
   * @params [procInsId, activityId] 流程实例 ID；活动节点 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfTaskForm selectTaskFormByProcInsIdAndActId(String procInsId, String activityId) {
    return this.getOne(Wrappers.<WfTaskForm>lambdaQuery()
        .eq(WfTaskForm::getProcInsId, procInsId)
        .eq(WfTaskForm::getActivityId, activityId)
        .orderByDesc(WfTaskForm::getCreateTime)
        .last("limit 1"), false);
  }

  /**
   * 根据流程实例 procInsId 和活动 activityId 获取流程表单内容 ---- 用户节点 List。
   */
  @Override
  public List<WfTaskForm> getTaskFormByProcInsIdAndActIdToList(String procInsId,
      String activityId) {
    return baseMapper.selectFormJsonDataByProcInsIdAndActIdToList(procInsId, activityId);
  }

  /**
   * 根据流程任务 taskId 和活动 activityId 获取流程表单内容 ---- 用户节点 List。
   */
  @Override
  public List<WfTaskForm> getTaskFormByTaskIdAndActIdToList(String taskId, String activityId) {
    if (StringUtils.isBlank(taskId)) {
      return null;
    }
    return baseMapper.selectFormJsonDataByTaskIdAndActIdToList(taskId, activityId);
  }

  /**
   * @return Boolean 删除结果，任务 ID 为空时直接返回 true
   * @description 按任务 ID 删除该任务提交的表单内容
   * @params [taskId] 任务 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean delTaskFormByTaskId(String taskId) {
    if (StringUtils.isBlank(taskId)) {
      return true;
    }
    return this.remove(Wrappers.<WfTaskForm>lambdaQuery().eq(WfTaskForm::getTaskId, taskId));
  }

  /**
   * @return Boolean 保存结果，表单内容为空时跳过保存并返回 true
   * @description 保存一次表单提交，先按任务或按实例加节点清掉历史提交再写入一行，实现同任务或同节点的覆盖
   * @params [wfTaskForm] 表单提交数据，含流程实例 ID、节点 ID、可选任务 ID 与表单 JSON 内容
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean saveProcessForm(WfTaskForm wfTaskForm) {
    if (wfTaskForm == null || StringUtils.isBlank(wfTaskForm.getProcInsId())) {
      throw new ServiceException("参数有误：流程实例ID不能为空");
    }
    if (StringUtils.isBlank(wfTaskForm.getActivityId())) {
      throw new ServiceException("参数有误：流程节点ID不能为空");
    }
    if (StringUtils.isBlank(wfTaskForm.getFormContent())) {
      log.debug("节点 {} 本次未提交表单数据，跳过保存", wfTaskForm.getActivityId());
      return true;
    }
    if (StringUtils.isNotBlank(wfTaskForm.getTaskId())) {
      delTaskFormByTaskId(wfTaskForm.getTaskId());
    } else {
      delProcessFormByProcInsIdAndActId(wfTaskForm.getProcInsId(), wfTaskForm.getActivityId());
    }
    saveFormData(wfTaskForm);
    return true;
  }

  /**
   * @return boolean 删除结果
   * @description 按流程实例 ID 与活动节点 ID 删除该节点提交的表单内容
   * @params [procInsId, activityId] 流程实例 ID；活动节点 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean delProcessFormByProcInsIdAndActId(String procInsId, String activityId) {
    return this.remove(Wrappers.<WfTaskForm>lambdaQuery()
        .eq(WfTaskForm::getProcInsId, procInsId)
        .eq(WfTaskForm::getActivityId, activityId));
  }

  /**
   * @return List<WfTaskForm> 该流程实例的审批表单数据列表，无记录时为空列表
   * @description 按流程实例 ID 查询流程审批表单数据
   * @params [procInsId] 流程实例 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public List<WfTaskForm> selectFormDataByProcInsIdToList(String procInsId) {
    return baseMapper.selectFormDataByProcInsIdToList(procInsId);
  }

  /**
   * @description 把本次提交的表单 JSON 原样写成一行记录，内容为空时不落库
   * @params [wfTaskForm] 表单提交数据，从中取表单 ID、实例、任务、节点、处理人与 JSON 内容
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public void saveFormData(WfTaskForm wfTaskForm) {
    String content = wfTaskForm.getFormContent();
    if (StringUtils.isBlank(content)) {
      return;
    }
    WfTaskForm entity = new WfTaskForm();
    entity.setFormId(wfTaskForm.getFormId());
    entity.setProcInsId(wfTaskForm.getProcInsId());
    entity.setTaskId(wfTaskForm.getTaskId());
    entity.setActivityId(wfTaskForm.getActivityId());
    entity.setUserId(wfTaskForm.getUserId());
    entity.setFormContent(content);
    this.save(entity);
  }

  /**
   * @return List<WfTaskForm> 该节点的表单 JSON 数据列表，无记录时为空列表
   * @description 按流程实例 ID 与活动节点 ID 查询流程表单 JSON 数据
   * @params [procInsId, activityId] 流程实例 ID；活动节点 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public List<WfTaskForm> selectFormJsonDataByProcInsIdAndActIdToList(String procInsId,
      String activityId) {
    return baseMapper.selectFormJsonDataByProcInsIdAndActIdToList(procInsId, activityId);
  }

  /**
   * @return List<WfTaskForm> 该任务在指定节点上的表单 JSON 数据列表，无记录时为空列表
   * @description 按任务 ID 与活动节点 ID 查询该任务在各节点提交过的表单 JSON 数据
   * @params [taskId, activityId] 任务 ID；活动节点 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public List<WfTaskForm> selectFormJsonDataByTaskIdAndActIdToList(String taskId,
      String activityId) {
    return baseMapper.selectFormJsonDataByTaskIdAndActIdToList(taskId, activityId);
  }

  /**
   * @return WfTaskForm 该节点上的表单 JSON 数据，无记录时为 null
   * @description 按流程实例 ID 与活动节点 ID 取单条表单 JSON
   * @params [procInsId, activityId] 流程实例 ID；活动节点 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfTaskForm selectFormJsonByProcInsIdAndActId(String procInsId, String activityId) {
    return baseMapper.selectFormJsonByProcInsIdAndActId(procInsId, activityId);
  }

  /**
   * @return WfTaskForm 命中的表单记录，主要取其流程实例 ID，未命中时为 null
   * @description 按字段名、字段值与节点 ID 集合反查表单记录，字段值从表单 JSON 内容中取值
   * @params [field, fieldValue, activityIdList] 字段名；字段值；参与匹配的活动节点 ID 数组
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public WfTaskForm selectFormDataByFieldAndFieldValueAndActId(String field, String fieldValue,
      String[] activityIdList) {
    return baseMapper.selectFormDataByFieldAndFieldValueAndActId(field, fieldValue, activityIdList);
  }
}
