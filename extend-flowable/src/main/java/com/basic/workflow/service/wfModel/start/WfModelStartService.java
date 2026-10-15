package com.basic.workflow.service.wfModel.start;

import com.basic.common.core.domain.AjaxResult;
import com.basic.common.utils.StringUtils;
import com.basic.constant.FlowConstant;
import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.common.enums.FlowComment;
import com.basic.flowable.common.enums.ProcessStatus;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.domain.WfCommentInfo;
import com.basic.workflow.domain.WfTaskForm;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.vo.StartProcessInstanceVo;
import com.basic.workflow.service.wfModel.base.WfModelInfoBase;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (Flowable流程启动)
 * @date 2024/03/05
 */
@Service
public class WfModelStartService extends WfModelInfoBase {

  /**
   * @return com.basic.flowable.tool.ReturnVo<org.flowable.engine.runtime.ProcessInstance>
   * @description TODO (流程启动/启动方法选择Id启动 使用repositoryService获取到流程定义对象，然后再获取到流程id)
   * @params [startProcessInstanceVo]
   * @author yanbin
   * @date 2023/10/25 15:36
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Transactional
  public AjaxResult startFormFlow(StartProcessInstanceVo startProcessInstanceVo) {
    AjaxResult ajaxResult = AjaxResult.success();
    //启动流程：流程key与发起人必填
    //businessKey 不再强制要求——业务侧改为保存 procInsId 关联流程，为空时由下方自动生成占位值
    if (StringUtils.isNotBlank(startProcessInstanceVo.getProcessDefinitionKey())
        && StringUtils.isNotBlank(startProcessInstanceVo.getCurrentUserCode())) {
      try {
        // 获取当前用户信息 userId
        String creator = startProcessInstanceVo.getCreator();
        //TODO 1. 1、设置变量 1.1、设置提交人字段为空字符串让其自动跳过 1.2、设置可以自动跳过 1.3、汇报线的参数设置
        // 部署的key  是 流程key+.bpmn
        startProcessInstanceVo.setProcessDefinitionKey(
            startProcessInstanceVo.getProcessDefinitionKey() + ProcessConstants.SUFFIX);
        Map<String, Object> variables = startProcessInstanceVo.getVariables();
        //TODO  1.1、设置提交人字段为空字符串让其自动跳过
        variables.put(FlowConstant.FLOW_SUBMITTER_VAR, "");
        //TODO 1.2、设置可以自动跳过
        if (!variables.containsKey(FlowConstant.FLOWABLE_SKIP_EXPRESSION_ENABLED)) {
          variables.put(FlowConstant.FLOWABLE_SKIP_EXPRESSION_ENABLED, true);
        }
        // TODO 1.3 设置流程状态为进行中
        variables.put(ProcessConstants.PROCESS_STATUS_KEY, ProcessStatus.RUNNING.getStatus());
        //TODO 1.3 流程发起人的参数 ${startCode} eg:这里是身份存储选择了流程发起人，或者表达式选择了提交人的参数
        variables.put("starterCode", creator);
        //TODO 1.4 多实例绑定人员 元素变量
        variables.put("assignee", "");
        if (startProcessInstanceVo.getCurrentUserCode() == null) {
          return AjaxResult.error("工号为：" + startProcessInstanceVo.getCurrentUserCode()
              + "的当前发起人用户匹配不到，请确认!");
        }
        // TODO 2.1当我们流程创建人和发起人、汇报线的参数设置
        startProcessInstanceVo.setCreator(creator);
        startProcessInstanceVo.setCurrentUserCode(creator);
        startProcessInstanceVo.setFormName(
            startProcessInstanceVo.getCurrentUserName() + "发起的流程");
        // TODO 3.启动流程 设置发起人
        identityService.setAuthenticatedUserId(creator);
        // TODO 3.1 流程定义 ProcessDefinition查询流程定义
        Map<String, Object> procDefMap = wfFlowableProcDefMapper.selectProcDefByModelKey(
            startProcessInstanceVo);
        ProcessDefinition processDefinition = null;
        if (procDefMap.size() > 0) {
          processDefinition = repositoryService.createProcessDefinitionQuery()
              .processDefinitionId(procDefMap.get("id_").toString()).singleResult();
        }
        if (processDefinition == null) {
          return AjaxResult.error("流程定义出错，请联系管理员");
        }
//                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(startProcessInstanceVo.getProcessDefinitionKey()).latestVersion().singleResult();
        // TODO 3.2 流程是否挂起
        if (processDefinition.isSuspended()) {
          return AjaxResult.error("流程已挂起，请联系管理员激活!");
        }
        // TODO 4获取当前model的xml
        String xml = wfProcessService.queryBpmnXmlById(processDefinition.getId());
        // TODO 4.1获取当前model下拥有的 userTask模块里选用了表达式的表达式
        List<String> expression = ModelUtils.doXmlToUserTask(xml);
        if (StringUtils.isNotEmpty(expression)) {
          //默认给表达式为空
          expression.forEach(e -> {
            //不包含表达式设置为空
            if (!variables.containsKey(e)) {
              variables.put(e, "");
            }
          });
        }
        // TODO 4.2 设置流程变量
        startProcessInstanceVo.setVariables(variables);
        // TODO 5. 发起流程实例-->
        //  说明：不再设置 businessKey，引擎的 BUSINESS_KEY_ 保持为空。
        //  流程与业务的关联改由业务侧保存本次返回的 procInsId 维护。
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
            .processDefinitionId(processDefinition.getId())
            .name(startProcessInstanceVo.getFormName())
            .variables(startProcessInstanceVo.getVariables()).start();
        //获取bpmnModel
        BpmnModel bpmnModel = bpmnModelService.getBpmnModelByProcessDefId(
            processInstance.getProcessDefinitionId());
        //查找开始节点
        StartEvent start = bpmnModelService.findStartFlowElement(bpmnModel.getMainProcess());
        String activityId = null;
        if (start != null) {
          activityId = start.getId();
          String formId = ModelUtils.getFormKey(start);
          if (formId != null) {
            WfTaskForm wfTaskForm = new WfTaskForm(processInstance.getId(), formId, start.getId(),
                startProcessInstanceVo.getFormData(), startProcessInstanceVo.getCurrentUserCode());
            //TODO  保存表单内容
            wfTaskFormService.saveProcessForm(wfTaskForm);
          }
        }
        // TODO  6.添加流程业务拓展表
        this.createExtendHisProcinst(startProcessInstanceVo, processInstance, creator);
        //TODO 7.处理上传附件
        this.saveFileToModel(startProcessInstanceVo, processInstance,activityId);
        //TODO 8.添加审批意见
        if (StringUtils.isNotBlank(startProcessInstanceVo.getComment())) {
          WfCommentInfo wfCommentInfo = new WfCommentInfo(FlowComment.TJ.getType(), creator,
              processInstance.getProcessInstanceId(), startProcessInstanceVo.getComment(),
              activityId, start.getName());
          wfCommentInfoService.addCommentInfo(wfCommentInfo);
        } else {
          WfCommentInfo wfCommentInfo = new WfCommentInfo(FlowComment.TJ.getType(), creator,
              processInstance.getProcessInstanceId(), processDefinition.getName(), activityId,
              start.getName());
          wfCommentInfoService.addCommentInfo(wfCommentInfo);
        }
        // TODO 9.处理第一个节点    参数:启动的实例，流程发起人  第一个节点为固定值节点即是表达式的节点
        wfTaskService.handleFlowTask(processInstance.getId());
        //TODO 10.发起选择了跳转任意节点
        WfTaskBo wfTaskBos = new WfTaskBo();
        wfTaskBos.setProcInsId(processInstance.getId());
        wfTaskBos.setComment("通过");
        wfTaskBos.setCurrentUserId(startProcessInstanceVo.getCurrentUserCode());
        wfTaskBos.setSkipActivityId(startProcessInstanceVo.getSkipActivityId());
        wfTaskService.skipUserTask(wfTaskBos);
        //TODO 11.处理第一个节点为空且选择了审批人的节点
        wfTaskService.handleNotBlankTask(processInstance.getId(),
            startProcessInstanceVo.getStartUserCodes());
        //TODO 12处理下一节点是交替节点
//                wfTaskService.handleIsRoleReplace(processInstance.getId(), startProcessInstanceVo.getCurrentUserCode());
        //TODO 13.若启动节点选择抄送人 流程启动时可选择抄送人,将信息传给所选择人员
        WfTaskBo wfTaskBo = new WfTaskBo(processInstance.getId(),
            startProcessInstanceVo.getCopyUserIds(), startProcessInstanceVo.getCurrentUserCode(),
            startProcessInstanceVo.getCurrentUserName());
        if (!copyService.makeCopy(wfTaskBo)) {
          throw new RuntimeException("抄送任务失败");
        }
        //推送消息数据
        ajaxResult.put("data", processInstance.getId());
      } catch (Exception e) {
        e.printStackTrace();
        return AjaxResult.error("发起流程失败，原因：" + e.getMessage());
      }
    } else {
      return AjaxResult.error(" Parameters should not be null");
    }
    return ajaxResult;
  }
}
