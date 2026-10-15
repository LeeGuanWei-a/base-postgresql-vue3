package com.basic.workflow.controller;

import com.basic.common.annotation.Log;
import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.enums.BusinessType;
import com.basic.common.utils.poi.ExcelUtil;
import com.basic.workflow.domain.WfExpression;
import com.basic.workflow.service.IWfExpressionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author yanbin
 * @description TODO (流程达式Controller/控制器)
 * @date 2023/11/3 10:41
 */
@Api(value = "expression",tags = "流程表达式管理")
@RestController
@RequestMapping("/workflow/expression")
public class WfExpressionController extends BaseController {
    @Autowired
    private IWfExpressionService iWfExpressionService;

    /**
     * @return com.basic.common.core.page.TableDataInfo
     * @description TODO (查询流程表达式列表)
     * @params [wfExpression]
     * @author yanbin
     * @date 2023/12/26 11:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表达式/查询流程表达式列表", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "WfExpression", value = "流程表达式实体", required = true,
            dataType = "WfExpression", paramType = "body", dataTypeClass = WfExpression.class)
    @PreAuthorize("@ss.hasPermi('workflow:expression:list')")
    @GetMapping("/list")
    public TableDataInfo list(WfExpression wfExpression) {
        startPage();
        List<WfExpression> list = iWfExpressionService.selectWfExpressionList(wfExpression);
        return getDataTable(list);
    }

    /**
     * @return void
     * @description TODO (导出流程表达式列表)
     * @params [response, wfExpression]
     * @author yanbin
     * @date 2023/12/26 11:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表达式/导出流程表达式列表", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "WfExpression", value = "流程表达式实体", required = true,
            dataType = "WfExpression", paramType = "body", dataTypeClass = WfExpression.class)
    @PreAuthorize("@ss.hasPermi('workflow:expression:export')")
    @Log(title = "流程达式", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WfExpression wfExpression) {
        List<WfExpression> list = iWfExpressionService.selectWfExpressionList(wfExpression);
        ExcelUtil<WfExpression> util = new ExcelUtil<WfExpression>(WfExpression.class);
        util.exportExcel(response, list, "流程达式数据");
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (获取流程表达式详细信息)
     * @params [id]
     * @author yanbin
     * @date 2023/12/26 11:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表达式/导出流程表达式列表", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "id", value = "表达式id", required = true,
            dataType = "Long", paramType = "path", dataTypeClass = Long.class)
    @PreAuthorize("@ss.hasPermi('workflow:expression:getInfo')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(iWfExpressionService.selectWfExpressionById(id));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (新增流程表达式)
     * @params [wfExpression]
     * @author yanbin
     * @date 2023/12/26 11:48
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表达式/新增流程表达式", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "WfExpression", value = "流程表达式实体", required = true,
            dataType = "WfExpression", paramType = "body", dataTypeClass = WfExpression.class)
    @PreAuthorize("@ss.hasPermi('workflow:expression:add')")
    @Log(title = "流程达式", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WfExpression wfExpression) {
        return toAjax(iWfExpressionService.insertWfExpression(wfExpression));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (修改流程表达式)
     * @params [wfExpression]
     * @author yanbin
     * @date 2023/12/26 11:48
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表达式/修改流程表达式", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "wfExpression", value = "流程表达式实体", required = true,
            paramType = "body",  dataType = "WfExpression", dataTypeClass = WfExpression.class)
    @PreAuthorize("@ss.hasPermi('workflow:expression:edit')")
    @Log(title = "流程达式", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WfExpression wfExpression) {
        return toAjax(iWfExpressionService.updateWfExpression(wfExpression));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (删除流程表达式)
     * @params [ids]
     * @author yanbin
     * @date 2023/12/26 11:48
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表达式/删除流程表达式", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "ids", value = "单个或多个流程表达式id", required = true,
            dataType = "Long[]", paramType = "path", dataTypeClass = Long[].class)
    @PreAuthorize("@ss.hasPermi('workflow:expression:remove')")
    @Log(title = "流程达式", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(iWfExpressionService.deleteWfExpressionByIds(ids));
    }
}
