package com.basic.workflow.controller;

import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.workflow.domain.bo.WfBoundaryTriggerBo;
import com.basic.workflow.domain.vo.WfBoundaryEventVo;
import com.basic.workflow.service.IWfBoundaryEventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "boundary", description = "工作流边界事件管理,触发与查询")
@RequestMapping("/workflow/boundary")
public class WfBoundaryEventController extends BaseController {

    private final IWfBoundaryEventService boundaryEventService;

    /**
     * @return AjaxResult 触发结果，成功时状态码为 1
     * @description 向流程引擎广播信号，唤起订阅该信号的边界事件分支
     * @params [bo] 触发参数，含流程实例 ID、信号 code 与附加变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "边界事件管理/触发信号", description = "2026-10-15 integration-supplement")
    @PreAuthorize("@ss.hasPermi('workflow:boundary:trigger')")
    @PostMapping("/signal")
    public AjaxResult triggerSignal(@RequestBody WfBoundaryTriggerBo bo) {
        return toAjax(boundaryEventService.triggerSignal(bo));
    }

    /**
     * @return AjaxResult 触发结果，成功时状态码为 1
     * @description 向指定执行实例精准投递消息，触发消息边界事件
     * @params [bo] 触发参数，含流程实例 ID、消息名与附加变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "边界事件管理/触发消息", description = "2026-10-15 integration-supplement")
    @PreAuthorize("@ss.hasPermi('workflow:boundary:trigger')")
    @PostMapping("/message")
    public AjaxResult triggerMessage(@RequestBody WfBoundaryTriggerBo bo) {
        return toAjax(boundaryEventService.triggerMessage(bo));
    }

    /**
     * @return AjaxResult 触发结果，成功时状态码为 1
     * @description 标记错误变量，由后续 ServiceTask 抛出 BpmnError 从而触发错误边界事件
     * @params [bo] 触发参数，含流程实例 ID、错误 code 与附加变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "边界事件管理/触发错误", description = "2026-10-15 integration-supplement")
    @PreAuthorize("@ss.hasPermi('workflow:boundary:trigger')")
    @PostMapping("/error")
    public AjaxResult triggerError(@RequestBody WfBoundaryTriggerBo bo) {
        return toAjax(boundaryEventService.triggerError(bo));
    }

    /**
     * @return AjaxResult 触发结果，成功时状态码为 1
     * @description 终止子流程实例，触发挂在子流程上的取消边界事件
     * @params [bo] 触发参数，含流程实例 ID 与触发原因
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "边界事件管理/触发取消", description = "2026-10-15 integration-supplement")
    @PreAuthorize("@ss.hasPermi('workflow:boundary:trigger')")
    @PostMapping("/cancel")
    public AjaxResult triggerCancel(@RequestBody WfBoundaryTriggerBo bo) {
        return toAjax(boundaryEventService.triggerCancel(bo));
    }

    /**
     * @return AjaxResult 边界事件列表，未注册任何事件时为空列表
     * @description 查询流程实例关联的边界事件注册清单，供流程图查看器与调试面板展示
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "边界事件管理/查询 boundary 列表", description = "2026-10-15 integration-supplement")
    @PreAuthorize("@ss.hasPermi('workflow:boundary:query')")
    @GetMapping("/list/{procInsId}")
    public AjaxResult listBoundaries(@PathVariable("procInsId") String procInsId) {
        List<WfBoundaryEventVo> list = boundaryEventService.listBoundaries(procInsId);
        return AjaxResult.success(list);
    }
}
