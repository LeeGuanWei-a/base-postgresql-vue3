package com.basic.workflow.controller;

import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.workflow.domain.WfTaskForm;
import com.basic.workflow.service.IWfTaskFormService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @description 流程业务表单控制器，提供任务表单数据的保存、按节点查询与删除接口
 * @author yanbin
 * @date 2024/04/15
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "taskForm", description = "流程/业务表单管理")
@RequestMapping("/workflow/taskForm")
public class WfTaskFormController extends BaseController {

    @Autowired
    private IWfTaskFormService wfTaskFormService;

    /**
     * @return AjaxResult 保存结果，成功时携带保存后的表单数据
     * @description 保存任务审批时填写的表单数据
     * @params [wfTaskForm] 表单数据对象，含流程实例、节点与表单内容
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "保存表单审批", description = "202-4-15 yanbin")
    @PostMapping("/saveProcessForm")
    @PreAuthorize("@ss.hasPermi('workflow:taskForm:saveProcessForm')")
    public AjaxResult saveProcessForm(@RequestBody WfTaskForm wfTaskForm) {
        return AjaxResult.success(wfTaskFormService.saveProcessForm(wfTaskForm));
    }

    /**
     * @return AjaxResult 该节点已提交的表单数据列表
     * @description 按流程实例与节点查询该节点已提交的表单数据
     * @params [procInsId, activityId] procInsId 流程实例ID；activityId 节点定义ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "(根据实例id和actid查找流程审批表单data)  申请表单内容", description = "202-4-15 yanbin")
    @GetMapping("/selectFormJsonDataByProcInsIdAndActIdToList")
    @PreAuthorize("@ss.hasPermi('workflow:taskForm:selectFormJsonDataByProcInsIdAndActIdToList')")
    public AjaxResult selectFormJsonDataByProcInsIdAndActIdToList(@RequestParam(value = "procInsId") String procInsId, @RequestParam(value = "activityId") String activityId) {
        return AjaxResult.success(wfTaskFormService.selectFormJsonDataByProcInsIdAndActIdToList(procInsId, activityId));
    }

    /**
     * @return AjaxResult 该任务已提交的表单数据列表
     * @description 按任务与节点查询该任务已提交的表单数据
     * @params [taskId, activityId] taskId 任务ID；activityId 节点定义ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "根据任务taskid获取所有的表单内容", description = "202-4-15 yanbin")
    @GetMapping("/selectFormJsonDataByTaskIdAndActIdToList")
    @PreAuthorize("@ss.hasPermi('workflow:taskForm:selectFormJsonDataByTaskIdAndActIdToList')")
    public AjaxResult selectFormJsonDataByTaskIdAndActIdToList(@RequestParam(value = "taskId") String taskId, @RequestParam(value = "activityId") String activityId) {
        return AjaxResult.success(wfTaskFormService.selectFormJsonDataByTaskIdAndActIdToList(taskId, activityId));
    }

    /**
     * @return AjaxResult 删除结果
     * @description 按流程实例批量删除挂在该实例下的审批表单数据
     * @params [procInsIds] 流程实例ID数组
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "根据实例procInsId删除流程表单", description = "202-4-15 yanbin")
    @DeleteMapping("/delProcessForm/{procInsIds}")
    @PreAuthorize("@ss.hasPermi('workflow:taskForm:delProcessForm')")
    public AjaxResult delProcessForm(@PathVariable String[] procInsIds) {
        List<String> ids = Arrays.asList(procInsIds);
        return AjaxResult.success(wfTaskFormService.delProcessForm(ids));
    }

  /**
   * @return AjaxResult 删除结果
   * @description 按流程实例与节点删除该节点下已提交的表单内容
   * @params [params] 请求体，含 procInsId 与 activityId
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Operation(summary =  "(根据实例id和节点actId删除表单内容", description = "202-4-15 yanbin")
  @PreAuthorize("@ss.hasPermi('workflow:taskForm:delProcessFormByProcInsIdAndActId')")
  @PostMapping("/delProcessFormByProcInsIdAndActId")
  public AjaxResult delProcessFormByProcInsIdAndActId(@RequestBody Map<String, String> params) {
    String procInsId = params.get("procInsId");
    String activityId = params.get("activityId");
    return AjaxResult.success(
        wfTaskFormService.delProcessFormByProcInsIdAndActId(procInsId, activityId));
  }

    /**
     * @return AjaxResult 该流程实例下的表单数据列表，无数据时为空列表
     * @description 按流程实例查询该实例下全部审批表单数据
     * @params [procInsId] 流程实例ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "(根据实例id查询流程审批表单data", description = "202-4-18 yanbin")
    @GetMapping("/selectFormDataByProcInsIdToList/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:taskForm:selectFormDataByProcInsIdToList')")
    public AjaxResult selectFormDataByProcInsIdToList(@PathVariable String procInsId) {
        return AjaxResult.success(wfTaskFormService.selectFormDataByProcInsIdToList(procInsId));
    }
}
