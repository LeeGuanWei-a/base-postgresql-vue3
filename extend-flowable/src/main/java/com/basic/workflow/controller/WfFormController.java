package com.basic.workflow.controller;

import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.workflow.domain.bo.WfFormBo;
import com.basic.workflow.domain.vo.WfFormVo;
import com.basic.workflow.service.IWfFormService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * @description 流程表单管理接口，提供表单的分页查询、详情、新增、修改与删除等 HTTP 入口
 * @author yanbin
 * @date 2023/11/3 10:41
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "form", description = "流程表单管理")
@RequestMapping("/workflow/form")
public class WfFormController extends BaseController {

    private final IWfFormService formService;

    /**
     * @return TableDataInfo 表单分页列表
     * @description 分页查询流程表单列表，可按查询条件过滤
     * @params [bo] 表单查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表单/查询流程表单列表", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:form:list')")
    @GetMapping("/list")
    public TableDataInfo list(WfFormBo bo) {
        return getDataTable(formService.queryPageList(bo));
    }

    /**
     * @return AjaxResult 表单详情
     * @description 按主键查询流程表单的详细信息
     * @params [formId] 表单主键，不能为空
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表单/获取流程表单详细信息", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:form:getInfo')")
    @GetMapping(value = "/{formId}")
    public AjaxResult getInfo(@NotNull(message = "主键不能为空") @PathVariable("formId") String formId) {
        return AjaxResult.success(formService.queryById(formId));
    }

    /**
     * @return AjaxResult 表单内容
     * @description 按表单标识查询表单内容，供业务页面渲染使用
     * @params [code] 表单标识，不能为空
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表单/根据code标识获取表单内容", description = "2024-3-12 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:form:getFormByCode')")
    @GetMapping(value = "/getFormByCode/{code}")
    public AjaxResult getFormByCode(@NotNull(message = "标识不能为空") @PathVariable("code") String code) {
        return AjaxResult.success(formService.getFormByCode(code));
    }

    /**
     * @return AjaxResult 保存结果，表单标识已存在时返回错误提示
     * @description 保存流程表单，表单标识重复时拒绝写入
     * @params [bo] 表单保存参数，含表单标识与表单内容
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表单/保存流程表单", description = "2024-2-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:form:saveForm')")
    @PostMapping("/saveForm")
    public AjaxResult saveForm(@RequestBody WfFormBo bo) {
        if (formService.checkFormCodeUnique(bo.getCode(), bo.getFormId())) {
            return AjaxResult.error("表单标识已存在！");
        }
        return AjaxResult.success(formService.saveForm(bo));
    }

    /**
     * @return AjaxResult 删除结果，全部成功时状态码为 1
     * @description 按主键批量删除流程表单
     * @params [formIds] 表单主键数组，不能为空
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程表单/删除表单内容", description = "2024-2-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:form:delForm')")
    @DeleteMapping("/delForm/{formIds}")
    public AjaxResult delForm(@NotEmpty(message = "主键不能为空") @PathVariable String[] formIds) {
        return toAjax(formService.delForm(Arrays.asList(formIds)));
    }

    /**
     * @return AjaxResult 组件字段的 label/value 选项集合
     * @description 筛选指定表单的组件字段，返回 label/value 形式的选项数据
     * @params [wfFormVo] 表单筛选条件，需指定表单 code 与目标组件字段
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "根据条件筛选指定code的自定义表单组件字段，返回需要的selectOption格式(“label”,“value”)", description = "2024-12-6 yanbin")
    @GetMapping("/getVFormWidgetList")
    @PreAuthorize("@ss.hasPermi('workflow:form:getVFormWidgetList')")
    public AjaxResult getVFormWidgetList(WfFormVo wfFormVo) {
        return AjaxResult.success(formService.getVFormWidgetList(wfFormVo));
    }
}
