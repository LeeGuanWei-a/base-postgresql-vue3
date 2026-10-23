package com.basic.workflow.controller;

import cn.hutool.core.util.ObjectUtil;
import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.dto.WfUserTaskDto;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.IWfTaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @description 工作流任务管理接口，提供任务办理、流程实例终止与删除、流程图查看等 HTTP 入口
 * @author yanbin
 * @date 2023/11/3 10:27
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "task", description = "工作流任务管理,节点任务流转处理类")
@RequestMapping("/workflow/task")
public class WfTaskController extends BaseController {
    private final IWfTaskService flowTaskService;

    /**
     * @return AjaxResult 操作结果，true 表示终止成功
     * @description 取消申请：终止所选流程实例，写入已取消的流程状态并记录终止意见
     * @params [bo] 任务业务对象，含待终止的流程实例id集合与终止意见
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/取消申请流程", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:cancel')")
    @PostMapping(value = "/stopProcess")
    public AjaxResult stopProcess(@RequestBody WfTaskBo bo) {
        return toAjax(flowTaskService.stopProcess(bo));
    }

    /**
     * @return AjaxResult 操作结果，true 表示删除成功
     * @description 按流程实例id数组删除流程实例，连同其实例数据与历史记录一并清理
     * @params [procInsIds] 待删除的流程实例id数组
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/删除流程实例", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:deleteProcessByIds')")
    @DeleteMapping("/deleteProcessByIds/{procInsIds}")
    public AjaxResult deleteProcessByIds(@PathVariable String[] procInsIds) {
        return toAjax(flowTaskService.deleteProcessByIds(procInsIds));
    }

    /**
     * @return AjaxResult 操作结果，true 表示撤回成功
     * @description 撤回已办：把流程撤回到发起人处重新提交，回退到起始节点的办理人
     * @params [bo] 任务业务对象，含流程实例id与当前任务id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/撤回流程已办", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:revokeProcess')")
    @PostMapping(value = "/revokeProcess")
    public AjaxResult revokeProcess(@RequestBody WfTaskBo bo) {
        return toAjax(flowTaskService.revokeProcess(bo));
    }

    /**
     * @return AjaxResult 操作结果，含审批后返回的业务数据
     * @description 审批通过当前任务，按流程定义流转到下一节点并记录审批意见
     * @params [bo] 任务业务对象，含任务id、审批意见与流程变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/审批任务", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:complete')")
    @PostMapping(value = "/complete")
    public AjaxResult complete(@RequestBody WfTaskBo bo) throws Exception {
        return AjaxResult.success(flowTaskService.complete(bo));
    }

    /**
     * @return AjaxResult 操作结果，含回退后返回的业务数据
     * @description 顺序会签审批不通过时，把任务流转回当前处理人的上一位会签人
     * @params [bo] 任务业务对象，含任务id与审批意见
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/完成任务不通过流转回顺序会签的当前处理人的上一个人(顺序会签)", description = "2025-09-28 yanbin")
    @PostMapping("/mulitiSequentialUnComplete")
    @PreAuthorize("@ss.hasPermi('workflow:process:mulitiSequentialUnComplete')")
    public AjaxResult mulitiSequentialUnComplete(@RequestBody WfTaskBo bo) throws Exception {
        return AjaxResult.success(flowTaskService.mulitiSequentialUnComplete(bo));
    }

    /**
     * @return AjaxResult 下一节点的办理人候选信息
     * @description 预先计算流程下一节点的类型与办理人候选，供前端选择审批人
     * @params [vo] 任务视图对象，含流程实例id、任务id与流转变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/获取下一节点用户信息", description = "2023-12-26 yanbin")
    @PostMapping("/nextFlowNode")
    @PreAuthorize("@ss.hasPermi('workflow:process:nextFlowNode')")
    public AjaxResult getNextFlowNode(@RequestBody WfTaskVo vo) throws Exception {
        WfUserTaskDto wfUserTaskDto = flowTaskService.getNextFlowNode(vo);
        return AjaxResult.success(wfUserTaskDto);
    }

    /**
     * @return AjaxResult 当前节点的办理人信息
     * @description 查询当前任务节点的类型与办理人，供前端展示与变更审批人
     * @params [vo] 任务视图对象，含流程实例id与任务id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/获取当前节点用户信息", description = "2023-12-26 yanbin")
    @PostMapping("/getCurrentTaskNode")
    @PreAuthorize("@ss.hasPermi('workflow:process:getCurrentTaskNode')")
    public AjaxResult getCurrentTaskNode(@RequestBody WfTaskVo vo) throws Exception {
        WfUserTaskDto wfUserTaskDto = flowTaskService.getCurrentTaskNode(vo);
        return AjaxResult.success(wfUserTaskDto);
    }

    /**
     * @return AjaxResult 操作结果，true 表示已拒绝
     * @description 拒绝任务：把流程驳回到起始节点重新发起，本次审批到此结束
     * @params [taskBo] 任务业务对象，含任务id与拒绝意见
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/拒绝任务", description = "2023-12-26 yanbin")
    @PostMapping(value = "/reject")
    @PreAuthorize("@ss.hasPermi('workflow:process:reject')")
    public AjaxResult taskReject(@RequestBody WfTaskBo taskBo) {
        return toAjax(flowTaskService.taskReject(taskBo));
    }

    /**
     * @return AjaxResult 操作结果，true 表示已退回
     * @description 把任务退回到指定的历史节点，清理回退点之后的运行态数据并重建目标节点任务
     * @params [bo] 任务业务对象，含任务id与退回目标节点id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/退回任务", description = "2023-12-26 yanbin")
    @PostMapping(value = "/return")
    @PreAuthorize("@ss.hasPermi('workflow:process:return')")
    public AjaxResult taskReturn(@RequestBody WfTaskBo bo) {
        return toAjax(flowTaskService.taskReturn(bo));
    }

    /**
     * @return AjaxResult 可退回的节点列表
     * @description 查出当前任务所有可退回的历史节点，供前端选择退回目标
     * @params [bo] 任务业务对象，含任务id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/获取所有可回退的节点", description = "2023-12-26 yanbin")
    @PostMapping(value = "/returnList")
    @PreAuthorize("@ss.hasPermi('workflow:process:returnList')")
    public AjaxResult findReturnTaskList(@RequestBody WfTaskBo bo) {
        return AjaxResult.success(flowTaskService.findReturnTaskList(bo));
    }

    /**
     * @return String 下一节点的类型标识
     * @description 判断当前任务下一节点是什么类型，供前端决定后续操作方式
     * @params [wfTaskVo] 任务视图对象，含任务id与流程实例id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/获取下一节点是什么类型", description = "2023-12-26 yanbin")
    @PostMapping("/getNextNode")
    @PreAuthorize("@ss.hasPermi('workflow:process:getNextNode')")
    public String getNextNode(@RequestBody WfTaskVo wfTaskVo) {
        return flowTaskService.getNextNode(wfTaskVo);
    }

    /**
     * @return AjaxResult 操作结果，含加签后的任务数据
     * @description 向前加签：在当前任务之前插入所选人员的会签任务，由所选人员先审批
     * @params [bo] 任务业务对象，含任务id与加签人员列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/向前加签", description = "2023-12-26 yanbin")
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
     * @return AjaxResult 操作结果，含加签后的任务数据
     * @description 向后加签：在当前任务之后插入所选人员的会签任务，当前办理人审批后由所选人员接着审批
     * @params [bo] 任务业务对象，含任务id与加签人员列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/向后加签", description = "2023-12-26 yanbin")
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
     * @return AjaxResult 操作结果，true 表示签收成功
     * @description 认领候选任务，把任务办理人签收为当前用户
     * @params [bo] 任务业务对象，含任务id与操作人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/认领/签收任务", description = "2023-12-26 yanbin")
    @PostMapping(value = "/claim")
    @PreAuthorize("@ss.hasPermi('workflow:process:claim')")
    public AjaxResult claim(@RequestBody WfTaskBo bo) {
        return toAjax(flowTaskService.claim(bo));
    }

    /**
     * @return AjaxResult 操作结果，true 表示已取消签收
     * @description 取消认领：把任务退回候选池，清空任务的办理人
     * @params [bo] 任务业务对象，含任务id与操作人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/取消认领/签收任务", description = "2023-12-26 yanbin")
    @PostMapping(value = "/unClaim")
    @PreAuthorize("@ss.hasPermi('workflow:process:unClaim')")
    public AjaxResult unClaim(@RequestBody WfTaskBo bo) {
        return toAjax(flowTaskService.unClaim(bo));
    }

    /**
     * @return AjaxResult 操作结果，true 表示委派成功
     * @description 把任务委派给指定人员办理，被委派人处理后任务回到原办理人手上
     * @params [bo] 任务业务对象，含任务id、被委派人标识与委派人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/委派任务", description = "2023-12-26 yanbin")
    @PostMapping(value = "/delegate")
    @PreAuthorize("@ss.hasPermi('workflow:process:delegate')")
    public AjaxResult delegate(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getTaskId(), bo.getCurrentUserId())) {
            return AjaxResult.error("参数错误！");
        }
        return toAjax(flowTaskService.delegateTask(bo));
    }

    /**
     * @return AjaxResult 操作结果，true 表示转办成功
     * @description 把任务转办给指定人员办理，原办理人不再持有该任务
     * @params [bo] 任务业务对象，含任务id、接收人标识与操作人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/转办任务", description = "2023-12-26 yanbin")
    @PostMapping(value = "/transfer")
    @PreAuthorize("@ss.hasPermi('workflow:process:transfer')")
    public AjaxResult transfer(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getTaskId(), bo.getCurrentUserId())) {
            return AjaxResult.error("参数错误！");
        }
        return toAjax(flowTaskService.transferTask(bo));
    }

    /**
     * @return AjaxResult 操作结果，true 表示变更成功
     * @description 变更任务处理人，同步更新任务办理人与流程实例上的当前处理人
     * @params [bo] 任务业务对象，含任务id、新的处理人与流程状态
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/变更处理人", description = "进行任务taskId,变更的处理人assignee,任务状态processStatus  2024-2-21 yanbin")
    @PostMapping(value = "/editProcessTaskAssignee")
    @PreAuthorize("@ss.hasPermi('workflow:process:editProcessTaskAssignee')")
    public AjaxResult editProcessTaskAssignee(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getTaskId())) {
            return AjaxResult.error("参数错误！");
        }
        return toAjax(flowTaskService.editProcessTaskAssignee(bo));
    }

    /**
     * @return AjaxResult 操作结果，true 表示意见已修改
     * @description 修改已完成任务的审批意见内容，按任务id定位到那条历史意见
     * @params [bo] 任务业务对象，含流程实例id、任务id、新意见与操作人
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/修改审批意见", description = "仅需实例procInsId,任务taskId,意见comment,处理人currentUserId 2024-2-21 yanbin")
    @PostMapping(value = "/updateTaskComment")
    @PreAuthorize("@ss.hasPermi('workflow:process:updateTaskComment')")
    public AjaxResult updateTaskComment(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getTaskId(), bo.getComment(), bo.getCurrentUserId())) {
            return AjaxResult.error("参数错误！");
        }
        return toAjax(flowTaskService.updateTaskComment(bo));
    }

    /**
     * @return AjaxResult 操作结果，true 表示已废除
     * @description 废除所选流程实例：终止流程并标记为已废除，写废除此类记录的历史意见
     * @params [bo] 任务业务对象，含待废除的流程实例id集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/废除所选流程", description = "仅需ProcInsIdList实例id集合 2024-2-22 yanbin")
    @PostMapping(value = "/abolishProcess")
    @PreAuthorize("@ss.hasPermi('workflow:process:abolishProcess')")
    public AjaxResult abolishProcess(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getProcInsIdList())) {
            return AjaxResult.error("参数错误！");
        }
        return toAjax(flowTaskService.abolishProcess(bo));
    }

    /**
     * @return AjaxResult 操作结果，true 表示已删除
     * @description 删除所选任务，连同该任务的运行态数据与历史记录一并清理
     * @params [bo] 任务业务对象，含任务id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/删除所选任务", description = "仅需任务taskId  2024-2-22 yanbin")
    @PostMapping(value = "/deleteProcessTask")
    @PreAuthorize("@ss.hasPermi('workflow:process:deleteProcessTask')")
    public AjaxResult deleteProcessTask(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getTaskId())) {
            return AjaxResult.error("参数错误！");
        }
        return toAjax(flowTaskService.deleteProcessTask(bo));
    }

    /**
     * @return AjaxResult 操作结果，含保存后的附件信息
     * @description 把本次上传的附件挂到指定流程实例上，写入实例id与上传人
     * @params [bo] 任务业务对象，含流程实例id与附件id列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/保存上传的附件", description = "2024-2-23 yanbin")
    @PostMapping(value = "/saveFileToModel")
    @PreAuthorize("@ss.hasPermi('workflow:process:saveFileToModel')")
    public AjaxResult saveFileToModel(@RequestBody WfTaskBo bo) {
        if (ObjectUtil.hasNull(bo.getProcInsId(), bo.getFileList())) {
            return AjaxResult.error("参数错误！");
        }
        return AjaxResult.success(flowTaskService.saveFileToModel(bo));
    }

    /**
     * @return AjaxResult 操作结果，含跳转后的流程数据
     * @description 把当前流程跳转到任意指定节点，清理目标节点之后的数据并重建该节点任务
     * @params [bo] 任务业务对象，含目标节点id与流程实例id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/跳转任意节点", description = "2024-5-28 yanbin")
    @PostMapping(value = "/skipUserTask")
    @PreAuthorize("@ss.hasPermi('workflow:process:skipUserTask')")
    public AjaxResult skipUserTask(@RequestBody WfTaskBo bo) throws Exception {
        if (ObjectUtil.hasNull(bo.getSkipActivityId(), bo.getProcInsId())) {
            throw new RuntimeException("参数错误！");
        }
        return AjaxResult.success(flowTaskService.skipUserTask(bo));
    }

    /**
     * @return AjaxResult 操作结果，含驳回后的流程数据
     * @description 默认驳回：把当前任务驳回到上一节点，由上一节点的办理人重新处理
     * @params [bo] 任务业务对象，含流程实例id与任务id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/默认驳回到上一节点", description = "2024-5-29 yanbin")
    @PostMapping(value = "/rejectLastTask")
    @PreAuthorize("@ss.hasPermi('workflow:process:rejectLastTask')")
    public AjaxResult rejectLastTask(@RequestBody WfTaskBo bo) throws Exception {
        if (ObjectUtil.hasNull(bo.getProcInsId(), bo.getTaskId())) {
            throw new RuntimeException("参数错误！");
        }
        return AjaxResult.success(flowTaskService.rejectLastTask(bo));
    }

    /**
     * @return AjaxResult 流程是否存在的判断结果
     * @description 按流程实例id判断该流程实例是否存在，供前端判断是否需要刷新页面
     * @params [procInsId] 流程实例id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/根据实例id判断流程是否存在", description = "2024-6-4 yanbin")
    @GetMapping("/checkProcessIsExistByProcInsId/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:checkProcessIsExistByProcInsId')")
    public AjaxResult checkProcessIsExistByProcInsId(@NotNull(message = "操作失误，请联系管理员") @PathVariable String procInsId) {
        return AjaxResult.success(flowTaskService.checkProcessIsExistByProcInsId(procInsId));
    }

    /**
     * @return AjaxResult 当前用户是否有权办理该任务的判断结果
     * @description 判断当前登录用户是否为该任务的处理人或候选角色成员，决定是否展示审批入口
     * @params [bo] 任务业务对象，含流程实例id、用户id与角色id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/判断当前用户是否是当前任务的处理人", description = "2024-6-4 yanbin")
    @PostMapping("/checkIsAuditUser")
    @PreAuthorize("@ss.hasPermi('workflow:process:checkIsAuditUser')")
    public AjaxResult checkUserIsAuditByProcInsIdAndUserIdAndGroupId(@RequestBody WfTaskBo bo) {
        return AjaxResult.success(flowTaskService.checkUserIsAuditByProcInsIdAndUserIdAndGroupId(bo));
    }

    /**
     * @return AjaxResult 当前用户是否是流程发起人的判断结果
     * @description 判断当前登录用户是否为该流程实例的发起人，决定是否展示撤回等入口
     * @params [bo] 任务业务对象，含流程实例id、用户id与角色id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/判断当前用户是否是当前流程的发起人", description = "2024-6-4 yanbin")
    @PostMapping("/checkIsStartUser")
    @PreAuthorize("@ss.hasPermi('workflow:process:checkIsStartUser')")
    public AjaxResult checkUserIsStartByProcInsIdAndUserIdAndGroupId(@RequestBody WfTaskBo bo) {
        return AjaxResult.success(flowTaskService.checkUserIsStartByProcInsIdAndUserIdAndGroupId(bo));
    }

    /**
     * @description 渲染流程实例的流程图，把流程走过的节点与当前节点标色后以 PNG 写入响应流
     * @params [response, procInsId] response HTTP 响应对象，用于输出图片流；procInsId 流程实例id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流任务管理/生成流程图", description = "2023-12-26 yanbin")
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
            log.error("生成流程图失败，procInsId={}", procInsId, e);
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                log.warn("关闭流程图输出流失败，procInsId={}, err={}", procInsId, e.getMessage());
            }
        }
    }
}
