package com.basic.workflow.controller;

import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.workflow.domain.bo.WfBoundaryTriggerBo;
import com.basic.workflow.domain.vo.WfBoundaryEventVo;
import com.basic.workflow.service.IWfBoundaryEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 工作流边界事件（Boundary Event）触发控制器。
 *
 * <p>基础路径：{@code /workflow/boundary}</p>
 * <p>提供 signal / message / error / cancel 四种触发入口 + 流程定义上的 boundary 列表查询。
 * 全部使用 {@code workflow:boundary:*} 权限串。</p>
 *
 * @author integration-supplement
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Api(value = "boundary", tags = "工作流边界事件管理,触发与查询")
@RequestMapping("/workflow/boundary")
public class WfBoundaryEventController extends BaseController {

    private final IWfBoundaryEventService boundaryEventService;

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description 触发信号边界事件（全局广播）
     * @params [bo]
     * @author integration-supplement
     * @date 2026-10-15
     */
    @ApiOperation(value = "边界事件管理/触发信号", notes = "2026-10-15 integration-supplement")
    @ApiImplicitParam(name = "bo", value = "触发请求", required = true, dataType = "WfBoundaryTriggerBo", paramType = "body", dataTypeClass = WfBoundaryTriggerBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:boundary:trigger')")
    @PostMapping("/signal")
    public AjaxResult triggerSignal(@RequestBody WfBoundaryTriggerBo bo) {
        return toAjax(boundaryEventService.triggerSignal(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description 触发消息边界事件（按 executionId 精准投递）
     * @params [bo]
     * @author integration-supplement
     * @date 2026-10-15
     */
    @ApiOperation(value = "边界事件管理/触发消息", notes = "2026-10-15 integration-supplement")
    @ApiImplicitParam(name = "bo", value = "触发请求", required = true, dataType = "WfBoundaryTriggerBo", paramType = "body", dataTypeClass = WfBoundaryTriggerBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:boundary:trigger')")
    @PostMapping("/message")
    public AjaxResult triggerMessage(@RequestBody WfBoundaryTriggerBo bo) {
        return toAjax(boundaryEventService.triggerMessage(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description 触发错误边界事件（标记变量，由 ServiceTask 抛 BpmnError）
     * @params [bo]
     * @author integration-supplement
     * @date 2026-10-15
     */
    @ApiOperation(value = "边界事件管理/触发错误", notes = "2026-10-15 integration-supplement")
    @ApiImplicitParam(name = "bo", value = "触发请求", required = true, dataType = "WfBoundaryTriggerBo", paramType = "body", dataTypeClass = WfBoundaryTriggerBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:boundary:trigger')")
    @PostMapping("/error")
    public AjaxResult triggerError(@RequestBody WfBoundaryTriggerBo bo) {
        return toAjax(boundaryEventService.triggerError(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description 触发取消边界事件（删除子流程实例以触发 cancel boundary）
     * @params [bo]
     * @author integration-supplement
     * @date 2026-10-15
     */
    @ApiOperation(value = "边界事件管理/触发取消", notes = "2026-10-15 integration-supplement")
    @ApiImplicitParam(name = "bo", value = "触发请求", required = true, dataType = "WfBoundaryTriggerBo", paramType = "body", dataTypeClass = WfBoundaryTriggerBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:boundary:trigger')")
    @PostMapping("/cancel")
    public AjaxResult triggerCancel(@RequestBody WfBoundaryTriggerBo bo) {
        return toAjax(boundaryEventService.triggerCancel(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description 查询流程定义上注册的 boundary 事件清单（供流程图查看器、调试面板使用）
     * @params [procInsId]
     * @author integration-supplement
     * @date 2026-10-15
     */
    @ApiOperation(value = "边界事件管理/查询 boundary 列表", notes = "2026-10-15 integration-supplement")
    @PreAuthorize("@ss.hasPermi('workflow:boundary:query')")
    @GetMapping("/list/{procInsId}")
    public AjaxResult listBoundaries(@PathVariable("procInsId") String procInsId) {
        List<WfBoundaryEventVo> list = boundaryEventService.listBoundaries(procInsId);
        return AjaxResult.success(list);
    }
}
