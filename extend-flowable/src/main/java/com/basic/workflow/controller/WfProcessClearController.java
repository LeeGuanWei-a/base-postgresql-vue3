package com.basic.workflow.controller;

import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.workflow.service.IWfProcessClearService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 流程数据清盘（危险操作）。
 *
 * <p>仅用于开发/测试环境重置。会把流程实例、流程定义、部署、模型以及全部 wf_* 业务表清空，
 * <b>不可恢复</b>。</p>
 *
 * <p>两道防线：</p>
 * <ol>
 *   <li>权限串 {@code workflow:process:clearAll}（默认只给超管）</li>
 *   <li>请求体必须带 {@code confirmText=清空流程}，前端要求用户手动输入同样的文字</li>
 * </ol>
 *
 * @author integration-supplement
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(value = "processClear", tags = "工作流流程管理/流程数据清盘（危险操作）")
@RequestMapping("/workflow/process/clear")
public class WfProcessClearController extends BaseController {

    private final IWfProcessClearService wfProcessClearService;

    /**
     * 预览将要删除的数量。
     */
    @ApiOperation(value = "预览将清空的流程数据量", notes = "只统计不删除，供前端二次确认展示")
    @PreAuthorize("@ss.hasPermi('workflow:process:clearAll')")
    @GetMapping("/preview")
    public AjaxResult preview() {
        return AjaxResult.success(wfProcessClearService.preview());
    }

    /**
     * 执行清盘。
     */
    @ApiOperation(value = "清空全部流程数据", notes = "危险操作，不可恢复。需传 confirmText=清空流程")
    @PreAuthorize("@ss.hasPermi('workflow:process:clearAll')")
    @PostMapping("/all")
    public AjaxResult clearAll(@RequestBody Map<String, String> body) {
        String confirmText = body == null ? null : body.get("confirmText");
        log.warn("[process-clear] 收到清盘请求，操作人={} confirmText={}", getUsername(), confirmText);
        Map<String, Object> result = wfProcessClearService.clearAll(confirmText);
        return AjaxResult.success(result);
    }
}
