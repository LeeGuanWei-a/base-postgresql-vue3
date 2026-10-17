package com.basic.workflow.controller;


import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.core.validate.QueryGroup;
import com.basic.workflow.domain.WfFormTemplate;
import com.basic.workflow.domain.vo.WfFormTemplateVo;
import com.basic.workflow.service.IWfFormTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;


/**
 * @description 流程表单模板控制器，提供表单模板的分页查询、详情查询、保存与批量删除接口
 * @author LiangJiaYong
 * @date 2024-3-13 15:03
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "form", description = "流程表单模板管理")
@RequestMapping("/workflow/formTemplate")
public class WfFormTemplateController extends BaseController {

    private final IWfFormTemplateService formTemplateService;

    /**
     * @return TableDataInfo<WfFormTemplateVo> 模板分页列表
     * @description 分页查询流程表单模板列表
     * @params [formTemplate] 模板查询条件，按查询分组校验
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表单/查询流程表单模板列表", description = "2024-03-13 liangjiayong")
    @PreAuthorize("@ss.hasPermi('workflow:formTemplate:list')")
    @GetMapping("/list")
    public TableDataInfo<WfFormTemplateVo> list(@Validated(QueryGroup.class) WfFormTemplate formTemplate) {
        startPage();
        List<WfFormTemplateVo> list = formTemplateService.queryPageList(formTemplate);
        return getDataTable(list);
    }

    /**
     * @return List<WfFormTemplateVo> 模板列表，无数据时为空列表
     * @description 查询全部流程表单模板，不分页
     * @params [formTemplate] 模板查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表单/查询流程表单模板列表(不分页)", description = "2024-03-14 liangjiayong")
    @PreAuthorize("@ss.hasPermi('workflow:formTemplate:allList')")
    @GetMapping("/allList")
    public List<WfFormTemplateVo> allList(@Validated(QueryGroup.class) WfFormTemplate formTemplate) {
        return formTemplateService.queryPageList(formTemplate);
    }

    /**
     * @return AjaxResult 模板详情
     * @description 按主键查询流程表单模板的详细信息
     * @params [formId] 模板主键，不能为空
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表单/获取流程表单详细信息", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:formTemplate:getInfo')")
    @GetMapping(value = "/{formId}")
    public AjaxResult getInfo(@NotNull(message = "主键不能为空") @PathVariable("formId") String formId) {
        return AjaxResult.success(formTemplateService.queryById(formId));
    }

    /**
     * @return AjaxResult 保存结果
     * @description 保存流程表单模板内容
     * @params [formTemplate] 模板对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表单/保存流程表单模板", description = "2024-03-13 liangjiayong")
    @PostMapping("/saveFormTemplate")
    @PreAuthorize("@ss.hasPermi('workflow:formTemplate:saveForm')")
    public AjaxResult saveForm(@RequestBody WfFormTemplate formTemplate) {
        return AjaxResult.success(formTemplateService.saveFormTemplate(formTemplate));
    }

    /**
     * @return AjaxResult 删除结果，全部成功时状态码为1
     * @description 按主键批量删除流程表单模板
     * @params [formIds] 模板主键数组，不能为空
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表单/删除表单内容", description = "2024-03-13 liangjiayong")
    @DeleteMapping("/delFormTemplate/{formIds}")
    @PreAuthorize("@ss.hasPermi('workflow:formTemplate:delFormTemplate')")
    public AjaxResult delForm(@NotEmpty(message = "主键不能为空") @PathVariable String[] formIds) {
        return toAjax(formTemplateService.delFormTemplate(Arrays.asList(formIds)));
    }
}
