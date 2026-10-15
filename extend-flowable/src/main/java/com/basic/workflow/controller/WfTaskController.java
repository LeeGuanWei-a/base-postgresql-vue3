package com.basic.workflow.controller;

import cn.hutool.core.util.ObjectUtil;
import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.dto.WfUserTaskDto;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.IWfTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;


/**
 * @author yanbin
 * @description TODO (工作流任务管理控制器)
 * @date 2023/11/3 10:27
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Api(value = "task", tags = "工作流任务管理,节点任务流转处理类")
@RequestMapping("/workflow/task")
public class WfTaskController extends BaseController {
    private final IWfTaskService flowTaskService;

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (取消申请流程,停止流程)
     * @params [bo]
     * @author yanbin
     * @date 2023/12/26 11:51
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/取消申请流程", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:process:cancel')")
    @PostMapping(value = "/stopProcess")
    public AjaxResult stopProcess(@RequestBody WfTaskBo bo) {
        return toAjax(flowTaskService.stopProcess(bo));
    }


    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (删除流程实例)
     * @params [instanceIds 流程实例ID串]
     * @author yanbin
     * @date 2023/11/3 10:45
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/删除流程实例", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "procInsIds", value = "单个或多个流程实例id", required = true, dataType = "String[]", paramType = "path", dataTypeClass = String[].class)
    @PreAuthorize("@ss.hasPermi('workflow:process:deleteProcessByIds')")
    @DeleteMapping("/deleteProcessByIds/{procInsIds}")
    public AjaxResult deleteProcessByIds(@PathVariable String[] procInsIds) {
        return toAjax(flowTaskService.deleteProcessByIds(procInsIds));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (撤回流程已办)
     * @params [bo]
     * @author yanbin
     * @date 2023/12/26 11:51
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/撤回流程已办", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:process:revokeProcess')")
    @PostMapping(value = "/revokeProcess")
    public AjaxResult revokeProcess(@RequestBody WfTaskBo bo) {
        return toAjax(flowTaskService.revokeProcess(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (审批任务)
     * @params [bo]
     * @author yanbin
     * @date 2023/11/3 10:38
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/审批任务", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:process:complete')")
    @PostMapping(value = "/complete")
    public AjaxResult complete(@RequestBody WfTaskBo bo) throws Exception {
        return AjaxResult.success(flowTaskService.complete(bo));
    }

    /**
     * @description TODO (完成任务不通过流转回顺序会签的当前处理人的上一个人(顺序会签))
     * @params bo
     * @return com.basic.common.core.domain.AjaxResult
     * @author yanbin
     * @date 2025/9/28 8:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/完成任务不通过流转回顺序会签的当前处理人的上一个人(顺序会签)", notes = "2025-09-28 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping("/mulitiSequentialUnComplete")
    @PreAuthorize("@ss.hasPermi('workflow:process:mulitiSequentialUnComplete')")
    public AjaxResult mulitiSequentialUnComplete(@RequestBody WfTaskBo bo) throws Exception {
        return AjaxResult.success(flowTaskService.mulitiSequentialUnComplete(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (获取下一节点用户信息)
     * @params [vo]
     * @author yanbin
     * @date 2023/11/9 17:00
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/获取下一节点用户信息", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "vo", value = "流程任务实体vo", required = true, dataType = "WfTaskVo", paramType = "body", dataTypeClass = WfTaskVo.class)
    @PostMapping("/nextFlowNode")
    @PreAuthorize("@ss.hasPermi('workflow:process:nextFlowNode')")
    public AjaxResult getNextFlowNode(@RequestBody WfTaskVo vo) throws Exception {
        WfUserTaskDto wfUserTaskDto = flowTaskService.getNextFlowNode(vo);
        return AjaxResult.success(wfUserTaskDto);
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (获取当前节点用户信息)
     * @params [vo]
     * @author yanbin
     * @date 2023/11/9 17:00
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/获取当前节点用户信息", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "WfTaskVo", value = "流程任务实体vo", required = true, dataType = "String", paramType = "body", dataTypeClass = WfTaskVo.class)
    @PostMapping("/getCurrentTaskNode")
    @PreAuthorize("@ss.hasPermi('workflow:process:getCurrentTaskNode')")
    public AjaxResult getCurrentTaskNode(@RequestBody WfTaskVo vo) throws Exception {
        WfUserTaskDto wfUserTaskDto = flowTaskService.getCurrentTaskNode(vo);
        return AjaxResult.success(wfUserTaskDto);
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (拒绝任务)
     * @params [taskBo]
     * @author yanbin
     * @date 2023/11/3 10:32
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/拒绝任务", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "taskBo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/reject")
    @PreAuthorize("@ss.hasPermi('workflow:process:reject')")
    public AjaxResult taskReject(@RequestBody WfTaskBo taskBo) {
        return toAjax(flowTaskService.taskReject(taskBo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (退回任务)
     * @params [WfTaskBo]
     * @author yanbin
     * @date 2023/11/3 10:33
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/退回任务", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/return")
    @PreAuthorize("@ss.hasPermi('workflow:process:return')")
    public AjaxResult taskReturn(@RequestBody WfTaskBo bo) {
        return toAjax(flowTaskService.taskReturn(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (获取所有可回退的节点)
     * @params [WfTaskBo]
     * @author yanbin
     * @date 2023/11/3 10:33
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/获取所有可回退的节点", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/returnList")
    @PreAuthorize("@ss.hasPermi('workflow:process:returnList')")
    public AjaxResult findReturnTaskList(@RequestBody WfTaskBo bo) {
        return AjaxResult.success(flowTaskService.findReturnTaskList(bo));
    }

    /**
     * @return java.lang.String
     * @description TODO (获取下一节点是什么类型)
     * @params [wfTaskVo]
     * @author yanbin
     * @date 2023/11/28 17:36
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/获取下一节点是什么类型", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "wfTaskVo", value = "流程任务实体vo", required = true, dataType = "WfTaskVo", paramType = "body", dataTypeClass = WfTaskVo.class)
    @PostMapping("/getNextNode")
    @PreAuthorize("@ss.hasPermi('workflow:process:getNextNode')")
    public String getNextNode(@RequestBody WfTaskVo wfTaskVo) {
        return flowTaskService.getNextNode(wfTaskVo);
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (向前加签)
     * @params [WfTaskBo]
     * @author yanbin
     * @date 2023/11/3 16:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/向前加签", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping("/beforeAddSignTask")
    @PreAuthorize("@ss.hasPermi('workflow:process:beforeAddSignTask')")
    public AjaxResult beforeAddSignTask(@RequestBody WfTaskBo bo) {
        if (bo.getUserList().isEmpty()) {
            return AjaxResult.error("人员未选择!");
        }
        List<String> userCode = Arrays.asList(bo.getUserList().split(","));
        String sign = "before";
        return AjaxResult.success(flowTaskService.addSignTask(bo, userCode, sign));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (向后加签)
     * @params [WfTaskBo]
     * @author yanbin
     * @date 2023/11/3 16:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/向后加签", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping("/afterAddSignTask")
    @PreAuthorize("@ss.hasPermi('workflow:process:afterAddSignTask')")
    public AjaxResult afterAddSignTask(@RequestBody WfTaskBo bo) {
        if (bo.getUserList().isEmpty()) {
            return AjaxResult.error("人员未选择!");
        }
        List<String> userCode = Arrays.asList(bo.getUserList().split(","));
        String sign = "after";
        return AjaxResult.success(flowTaskService.addSignTask(bo, userCode, sign));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (认领/签收任务)
     * @params [bo]
     * @author yanbin
     * @date 2023/11/3 10:36
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/认领/签收任务", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/claim")
    @PreAuthorize("@ss.hasPermi('workflow:process:claim')")
    public AjaxResult claim(@RequestBody WfTaskBo bo) {
        return toAjax(flowTaskService.claim(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (取消认领/签收任务)
     * @params [bo]
     * @author yanbin
     * @date 2023/11/3 10:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/取消认领/签收任务", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/unClaim")
    @PreAuthorize("@ss.hasPermi('workflow:process:unClaim')")
    public AjaxResult unClaim(@RequestBody WfTaskBo bo) {
        return toAjax(flowTaskService.unClaim(bo));
    }


    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (委派任务)
     * @params [bo]
     * @author yanbin
     * @date 2023/11/3 10:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/委派任务", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/delegate")
    @PreAuthorize("@ss.hasPermi('workflow:process:delegate')")
    public AjaxResult delegate(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getTaskId(), bo.getCurrentUserId())) {
            return AjaxResult.error("参数错误！");
        }
        return toAjax(flowTaskService.delegateTask(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (转办任务)
     * @params [bo]
     * @author yanbin
     * @date 2023/11/3 10:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/转办任务", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/transfer")
    @PreAuthorize("@ss.hasPermi('workflow:process:transfer')")
    public AjaxResult transfer(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getTaskId(), bo.getCurrentUserId())) {
            return AjaxResult.error("参数错误！");
        }
        return toAjax(flowTaskService.transferTask(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (变更处理人)
     * @params [bo]
     * @author yanbin
     * @date 2024/2/21 14:05
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/变更处理人", notes = "进行任务taskId,变更的处理人assignee,任务状态processStatus  2024-2-21 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/editProcessTaskAssignee")
    @PreAuthorize("@ss.hasPermi('workflow:process:editProcessTaskAssignee')")
    public AjaxResult editProcessTaskAssignee(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getTaskId())) {
            return AjaxResult.error("参数错误！");
        }
        return toAjax(flowTaskService.editProcessTaskAssignee(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (修改审批意见 已完成的任务才存在意见)
     * @params [bo]
     * @author yanbin
     * @date 2024/2/21 17:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/修改审批意见", notes = "仅需实例procInsId,任务taskId,意见comment,处理人currentUserId 2024-2-21 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/updateTaskComment")
    @PreAuthorize("@ss.hasPermi('workflow:process:updateTaskComment')")
    public AjaxResult updateTaskComment(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getTaskId(), bo.getComment(), bo.getCurrentUserId())) {
            return AjaxResult.error("参数错误！");
        }
        return toAjax(flowTaskService.updateTaskComment(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (废除所选流程)
     * @params [bo]
     * @author yanbin
     * @date 2024/2/22 8:59
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/废除所选流程", notes = "仅需ProcInsIdList实例id集合 2024-2-22 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/abolishProcess")
    @PreAuthorize("@ss.hasPermi('workflow:process:abolishProcess')")
    public AjaxResult abolishProcess(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getProcInsIdList())) {
            return AjaxResult.error("参数错误！");
        }
        return toAjax(flowTaskService.abolishProcess(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (删除所选任务) 仅需任务id
     * @params [bo]
     * @author yanbin
     * @date 2024/2/22 9:25
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/删除所选任务", notes = "仅需任务taskId  2024-2-22 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/deleteProcessTask")
    @PreAuthorize("@ss.hasPermi('workflow:process:deleteProcessTask')")
    public AjaxResult deleteProcessTask(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getTaskId())) {
            return AjaxResult.error("参数错误！");
        }
        return toAjax(flowTaskService.deleteProcessTask(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (保存上传的附件)
     * @params [bo]
     * @author yanbin
     * @date 2024/2/23 11:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/保存上传的附件", notes = "2024-2-23 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/saveFileToModel")
    @PreAuthorize("@ss.hasPermi('workflow:process:saveFileToModel')")
    public AjaxResult saveFileToModel(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getProcInsId(), bo.getFileList())) {
            return AjaxResult.error("参数错误！");
        }
        return AjaxResult.success(flowTaskService.saveFileToModel(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (工作流任务管理/跳转任意节点)
     * @params [bo]
     * @author yanbin
     * @date 2024/5/28 15:36
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/跳转任意节点", notes = "2024-5-28 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/skipUserTask")
    @PreAuthorize("@ss.hasPermi('workflow:process:skipUserTask')")
    public AjaxResult skipUserTask(@RequestBody WfTaskBo bo) throws Exception {
        if (ObjectUtil.hasNull(bo.getSkipActivityId(), bo.getProcInsId())) {
            throw new RuntimeException("参数错误！");
        }
        return AjaxResult.success(flowTaskService.skipUserTask(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (默认驳回到上一节点)
     * @params [bo]
     * @author yanbin
     * @date 2024/5/29 8:46
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/默认驳回到上一节点", notes = "2024-5-29 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping(value = "/rejectLastTask")
    @PreAuthorize("@ss.hasPermi('workflow:process:rejectLastTask')")
    public AjaxResult rejectLastTask(@RequestBody WfTaskBo bo) throws Exception {
        if (ObjectUtil.hasNull(bo.getProcInsId(), bo.getTaskId())) {
            throw new RuntimeException("参数错误！");
        }
        return AjaxResult.success(flowTaskService.rejectLastTask(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (工作流流程管理/根据实例id判断流程是否存在)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/6/4 10:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/根据实例id判断流程是否存在", notes = "2024-6-4 yanbin")
    @ApiImplicitParam(name = "procInsId", value = "实例id", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    @GetMapping("/checkProcessIsExistByProcInsId/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:checkProcessIsExistByProcInsId')")
    public AjaxResult checkProcessIsExistByProcInsId(@NotNull(message = "操作失误，请联系管理员") @PathVariable String procInsId) {
        return AjaxResult.success(flowTaskService.checkProcessIsExistByProcInsId(procInsId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (工作流任务管理/判断当前用户是否是当前任务的处理人)
     * @params [bo]
     * @author yanbin
     * @date 2024/6/4 14:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/判断当前用户是否是当前任务的处理人", notes = "2024-6-4 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping("/checkIsAuditUser")
    @PreAuthorize("@ss.hasPermi('workflow:process:checkIsAuditUser')")
    public AjaxResult checkUserIsAuditByProcInsIdAndUserIdAndGroupId(@RequestBody WfTaskBo bo) {
        return AjaxResult.success(flowTaskService.checkUserIsAuditByProcInsIdAndUserIdAndGroupId(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (工作流任务管理/判断当前用户是否是当前流程的发起人)
     * @params [bo]
     * @author yanbin
     * @date 2024/6/4 14:27
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/判断当前用户是否是当前流程的发起人", notes = "2024-6-4 yanbin")
    @ApiImplicitParam(name = "bo", value = "流程任务实体bo", required = true, dataType = "WfTaskBo", paramType = "body", dataTypeClass = WfTaskBo.class)
    @PostMapping("/checkIsStartUser")
    @PreAuthorize("@ss.hasPermi('workflow:process:checkIsStartUser')")
    public AjaxResult checkUserIsStartByProcInsIdAndUserIdAndGroupId(@RequestBody WfTaskBo bo) {
        return AjaxResult.success(flowTaskService.checkUserIsStartByProcInsIdAndUserIdAndGroupId(bo));
    }

    /**
     * @return void
     * @description TODO (生成流程图)
     * @params [response, procInsId]
     * @author yanbin
     * @date 2024/1/17 17:50
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流任务管理/生成流程图", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "procInsId", value = "流程实例id", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    @RequestMapping("/diagram/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:diagram')")
    public void genProcessDiagram(HttpServletResponse response, @PathVariable("procInsId") String procInsId) {
        InputStream inputStream = flowTaskService.diagram(procInsId);
        OutputStream os = null;
        BufferedImage image = null;
        try {
            image = ImageIO.read(inputStream);
            response.setContentType("image/png");
            os = response.getOutputStream();
            if (image != null) {
                ImageIO.write(image, "png", os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
