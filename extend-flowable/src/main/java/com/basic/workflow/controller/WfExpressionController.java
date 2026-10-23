package com.basic.workflow.controller;

import com.basic.common.annotation.Log;
import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.enums.BusinessType;
import com.basic.common.utils.poi.ExcelUtil;
import com.basic.workflow.domain.WfExpression;
import com.basic.workflow.service.IWfExpressionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @description 流程表达式管理控制器，提供表达式的新增、修改、删除、查询与导出接口
 * @author yanbin
 * @date 2023/11/3 10:41
 */
@Tag(name = "expression", description = "流程表达式管理")
@RestController
@RequestMapping("/workflow/expression")
public class WfExpressionController extends BaseController {
    @Autowired
    private IWfExpressionService iWfExpressionService;

    /**
     * @return TableDataInfo 表达式分页列表
     * @description 分页查询流程表达式列表，可按查询条件过滤
     * @params [wfExpression] 表达式查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表达式/查询流程表达式列表", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:expression:list')")
    @GetMapping("/list")
    public TableDataInfo list(WfExpression wfExpression) {
        startPage();
        List<WfExpression> list = iWfExpressionService.selectWfExpressionList(wfExpression);
        return getDataTable(list);
    }

    /**
     * @description 按查询条件把流程表达式数据导出成 Excel 文件写出到响应流
     * @params [response, wfExpression] HTTP 响应对象，用于写出 Excel 流；表达式查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表达式/导出流程表达式列表", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:expression:export')")
    @Log(title = "流程达式", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WfExpression wfExpression) {
        List<WfExpression> list = iWfExpressionService.selectWfExpressionList(wfExpression);
        ExcelUtil<WfExpression> util = new ExcelUtil<WfExpression>(WfExpression.class);
        util.exportExcel(response, list, "流程达式数据");
    }

    /**
     * @return AjaxResult 表达式详情
     * @description 按主键查询流程表达式的详细信息
     * @params [id] 表达式主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表达式/导出流程表达式列表", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:expression:getInfo')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(iWfExpressionService.selectWfExpressionById(id));
    }

    /**
     * @return AjaxResult 新增结果，成功时状态码为 1
     * @description 新增一条流程表达式，内容以请求体提交
     * @params [wfExpression] 表达式对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表达式/新增流程表达式", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:expression:add')")
    @Log(title = "流程达式", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WfExpression wfExpression) {
        return toAjax(iWfExpressionService.insertWfExpression(wfExpression));
    }

    /**
     * @return AjaxResult 修改结果，成功时状态码为 1
     * @description 修改流程表达式，需携带主键定位记录
     * @params [wfExpression] 表达式对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表达式/修改流程表达式", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:expression:edit')")
    @Log(title = "流程达式", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WfExpression wfExpression) {
        return toAjax(iWfExpressionService.updateWfExpression(wfExpression));
    }

    /**
     * @return AjaxResult 删除结果，全部成功时状态码为 1
     * @description 按主键数组批量删除流程表达式
     * @params [ids] 表达式主键数组
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表达式/删除流程表达式", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:expression:remove')")
    @Log(title = "流程达式", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(iWfExpressionService.deleteWfExpressionByIds(ids));
    }
}
