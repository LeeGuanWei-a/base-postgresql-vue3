package com.basic.workflow.controller;

import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.workflow.domain.WfTaskForm;
import com.basic.workflow.service.IWfTaskFormService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (流程/业务表单管理)
 * @date 2024/04/15
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Api(value = "taskForm", tags = "流程/业务表单管理")
@RequestMapping("/workflow/taskForm")
public class WfTaskFormController extends BaseController {

    @Autowired
    private IWfTaskFormService wfTaskFormService;

    /**
     * @return com.basic.flowable.common.core.domain.AjaxResults
     * @description TODO (保存表单审批)
     * @params [wfTaskForm]
     * @author yanbin
     * @date 2024/4/15 16:14
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "保存表单审批", notes = "202-4-15 yanbin")
    @PostMapping("/saveProcessForm")
    @PreAuthorize("@ss.hasPermi('workflow:taskForm:saveProcessForm')")
    public AjaxResult saveProcessForm(@RequestBody WfTaskForm wfTaskForm) {
        return AjaxResult.success(wfTaskFormService.saveProcessForm(wfTaskForm));
    }

    /**
     * @return com.basic.flowable.common.core.domain.AjaxResults
     * @description TODO (根据实例id和actid查找流程审批表单data)
     * @params [procInsId, activityId]
     * @author yanbin
     * @date 2024/4/15 16:16
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "(根据实例id和actid查找流程审批表单data)  申请表单内容", notes = "202-4-15 yanbin")
    @ApiImplicitParams({@ApiImplicitParam(name = "procInsId", value = "流程实例id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class), @ApiImplicitParam(name = "activityId", value = "节点id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class),})
    @GetMapping("/selectFormJsonDataByProcInsIdAndActIdToList")
    @PreAuthorize("@ss.hasPermi('workflow:taskForm:selectFormJsonDataByProcInsIdAndActIdToList')")
    public AjaxResult selectFormJsonDataByProcInsIdAndActIdToList(@RequestParam(value = "procInsId") String procInsId, @RequestParam(value = "activityId") String activityId) {
        return AjaxResult.success(wfTaskFormService.selectFormJsonDataByProcInsIdAndActIdToList(procInsId, activityId));
    }

    /**
     * @return com.basic.flowable.common.core.domain.AjaxResults
     * @description TODO (根据任务taskid获取所有的表单内容)
     * @params [procInsId, activityId]
     * @author yanbin
     * @date 2024/4/15 16:16
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "根据任务taskid获取所有的表单内容", notes = "202-4-15 yanbin")
    @ApiImplicitParams({@ApiImplicitParam(name = "taskId", value = "流程任务id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class), @ApiImplicitParam(name = "activityId", value = "节点id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class),})
    @GetMapping("/selectFormJsonDataByTaskIdAndActIdToList")
    @PreAuthorize("@ss.hasPermi('workflow:taskForm:selectFormJsonDataByTaskIdAndActIdToList')")
    public AjaxResult selectFormJsonDataByTaskIdAndActIdToList(@RequestParam(value = "taskId") String taskId, @RequestParam(value = "activityId") String activityId) {
        return AjaxResult.success(wfTaskFormService.selectFormJsonDataByTaskIdAndActIdToList(taskId, activityId));
    }

    /***
     * @return com.basic.flowable.common.core.domain.AjaxResults
     * @description TODO (根据实例procInsId删除流程表单)
     * @params [procInsIds]
     * @author yanbin
     * @date 2024/4/15 16:22
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "根据实例procInsId删除流程表单", notes = "202-4-15 yanbin")
    @ApiImplicitParams({@ApiImplicitParam(name = "procInsIds", value = "流程实例id集合", required = true, dataType = "String []", paramType = "query", dataTypeClass = String[].class)})
    @DeleteMapping("/delProcessForm/{procInsIds}")
    @PreAuthorize("@ss.hasPermi('workflow:taskForm:delProcessForm')")
    public AjaxResult delProcessForm(@PathVariable String[] procInsIds) {
        List<String> ids = Arrays.asList(procInsIds);
        return AjaxResult.success(wfTaskFormService.delProcessForm(ids));
    }

  /**
   * @return com.basic.common.core.domain.AjaxResult
   * @description TODO (根据实例id和节点actId删除表单内容)
   * @params [procInsId, activityId]
   * @author yanbin
   * @date 2024/4/18 16:33
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @ApiOperation(value = "(根据实例id和节点actId删除表单内容", notes = "202-4-15 yanbin")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "procInsId",
        value = "流程实例id",
        required = true,
        dataType = "String",
        paramType = "query",
        dataTypeClass = String.class),
    @ApiImplicitParam(
        name = "activityId",
        value = "节点id",
        required = true,
        dataType = "String",
        paramType = "query",
        dataTypeClass = String.class),
  })
  @PreAuthorize("@ss.hasPermi('workflow:taskForm:delProcessFormByProcInsIdAndActId')")
  @PostMapping("/delProcessFormByProcInsIdAndActId")
  public AjaxResult delProcessFormByProcInsIdAndActId(@RequestBody Map<String, String> params) {
    String procInsId = params.get("procInsId");
    String activityId = params.get("activityId");
    return AjaxResult.success(
        wfTaskFormService.delProcessFormByProcInsIdAndActId(procInsId, activityId));
  }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (根据实例id查询流程审批表单data)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/4/18 16:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "(根据实例id查询流程审批表单data", notes = "202-4-18 yanbin")
    @ApiImplicitParams({@ApiImplicitParam(name = "procInsId", value = "流程实例id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class)})
    @GetMapping("/selectFormDataByProcInsIdToList/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:taskForm:selectFormDataByProcInsIdToList')")
    public AjaxResult selectFormDataByProcInsIdToList(@PathVariable String procInsId) {
        return AjaxResult.success(wfTaskFormService.selectFormDataByProcInsIdToList(procInsId));
    }
}
