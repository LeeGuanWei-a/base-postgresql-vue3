package com.basic.workflow.controller;


import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.core.validate.QueryGroup;
import com.basic.workflow.domain.WfFormTemplate;
import com.basic.workflow.domain.vo.WfFormTemplateVo;
import com.basic.workflow.service.IWfFormTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;


/***
 * @description TODO (流程表单模板Controller/控制器)
 * @author LiangJiaYong
 * @date 2024-3-13 15:03
 */
@RequiredArgsConstructor
@RestController
@Api(value = "form", tags = "流程表单模板管理")
@RequestMapping("/workflow/formTemplate")
public class WfFormTemplateController extends BaseController {

    private final IWfFormTemplateService formTemplateService;

    /***
     * @description TODO (流程表单/查询流程表单模板列表)
     * @params [formTemplate]
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfFormTemplateVo>
     * @author LiangJiaYong
     * @date 2024-3-13 15:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表单/查询流程表单模板列表", notes = "2024-03-13 liangjiayong")
    @PreAuthorize("@ss.hasPermi('workflow:formTemplate:list')")
    @GetMapping("/list")
    public TableDataInfo<WfFormTemplateVo> list(@Validated(QueryGroup.class) WfFormTemplate formTemplate) {
        startPage();
        List<WfFormTemplateVo> list = formTemplateService.queryPageList(formTemplate);
        return getDataTable(list);
    }

    /***
     * @description TODO (流程表单/查询流程表单模板列表(不分页))
     * @params [formTemplate]
     * @return java.util.List<com.basic.workflow.domain.vo.WfFormTemplateVo>
     * @author LiangJiaYong
     * @date 2024-3-14 10:07
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表单/查询流程表单模板列表(不分页)", notes = "2024-03-14 liangjiayong")
    @PreAuthorize("@ss.hasPermi('workflow:formTemplate:allList')")
    @GetMapping("/allList")
    public List<WfFormTemplateVo> allList(@Validated(QueryGroup.class) WfFormTemplate formTemplate) {
        return formTemplateService.queryPageList(formTemplate);
    }

    /***
     * @description TODO (获取流程表单模板详细信息)
     * @params [formId]
     * @return AjaxResult
     * @author LiangJiaYong
     * @date 2024-3-13 15:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表单/获取流程表单详细信息", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:formTemplate:getInfo')")
    @GetMapping(value = "/{formId}")
    public AjaxResult getInfo(@NotNull(message = "主键不能为空") @PathVariable("formId") Long formId) {
        return AjaxResult.success(formTemplateService.queryById(formId));
    }

    /***
     * @description TODO (保存流程表单模板)
     * @params [formTemplate]
     * @return com.basic.common.core.domain.AjaxResult
     * @author LiangJiaYong
     * @date 2024-3-13 16:14
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表单/保存流程表单模板", notes = "2024-03-13 liangjiayong")
    @PostMapping("/saveFormTemplate")
    @PreAuthorize("@ss.hasPermi('workflow:formTemplate:saveForm')")
    public AjaxResult saveForm(@RequestBody WfFormTemplate formTemplate) {
        return AjaxResult.success(formTemplateService.saveFormTemplate(formTemplate));
    }

    /***
     * @description TODO (流程表单/删除表单模板)
     * @params [formIds]
     * @return com.basic.common.core.domain.AjaxResult
     * @author LiangJiaYong
     * @date 2024-3-13 16:13
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表单/删除表单内容", notes = "2024-03-13 liangjiayong")
    @DeleteMapping("/delFormTemplate/{formIds}")
    @PreAuthorize("@ss.hasPermi('workflow:formTemplate:delFormTemplate')")
    public AjaxResult delForm(@NotEmpty(message = "主键不能为空") @PathVariable String[] formIds) {
        return toAjax(formTemplateService.delFormTemplate(Arrays.asList(formIds)));
    }
}
