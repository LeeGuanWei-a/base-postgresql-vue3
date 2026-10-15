package com.basic.workflow.controller;


import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.workflow.domain.bo.WfFormBo;
import com.basic.workflow.domain.vo.WfFormVo;
import com.basic.workflow.service.IWfFormService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;


/**
 * @author yanbin
 * @description TODO (流程表单Controller/控制器)
 * @date 2023/11/3 10:41
 */
@RequiredArgsConstructor
@RestController
@Api(value = "form", tags = "流程表单管理")
@RequestMapping("/workflow/form")
public class WfFormController extends BaseController {

    private final IWfFormService formService;

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfFormVo>
     * @description TODO (查询流程表单列表)
     * @params [bo, pageQuery]
     * @author yanbin
     * @date 2024/2/26 11:06
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表单/查询流程表单列表", notes = "2023-12-26 yanbin")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageQuery", value = "分页参数", required = true, dataType = "PageQuery", paramType = "query", dataTypeClass = PageQuery.class)})
    @PreAuthorize("@ss.hasPermi('workflow:form:list')")
    @GetMapping("/list")
    public TableDataInfo list(WfFormBo bo) {
        return getDataTable(formService.queryPageList(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (获取流程表单详细信息)
     * @params [formId]
     * @author yanbin
     * @date 2024/2/26 11:06
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表单/获取流程表单详细信息", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "formId", value = "流程表单id", required = true, dataType = "Long", paramType = "path", dataTypeClass = Long.class)
    @PreAuthorize("@ss.hasPermi('workflow:form:getInfo')")
    @GetMapping(value = "/{formId}")
    public AjaxResult getInfo(@NotNull(message = "主键不能为空") @PathVariable("formId") Long formId) {
        return AjaxResult.success(formService.queryById(formId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (根据code标识获取表单内容)
     * @params [code]
     * @author yanbin
     * @date 2024/3/12 15:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表单/根据code标识获取表单内容", notes = "2024-3-12 yanbin")
    @ApiImplicitParam(name = "code", value = "表单code", required = true, dataType = "code", paramType = "path", dataTypeClass = String.class)
    @PreAuthorize("@ss.hasPermi('workflow:form:getFormByCode')")
    @GetMapping(value = "/getFormByCode/{code}")
    public AjaxResult getFormByCode(@NotNull(message = "标识不能为空") @PathVariable("code") String code) {
        return AjaxResult.success(formService.getFormByCode(code));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (保存流程表单)
     * @params [bo]
     * @author yanbin
     * @date 2024/2/26 11:03
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表单/保存流程表单", notes = "2024-2-26 yanbin")
    @ApiImplicitParam(name = "WfFormBo", value = "流程表单WfFormBo", required = true, dataType = "body", paramType = "", dataTypeClass = WfFormBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:form:saveForm')")
    @PostMapping("/saveForm")
    public AjaxResult saveForm(@RequestBody WfFormBo bo) {
        if (formService.checkFormCodeUnique(bo.getCode()) && bo.getFormId() == null) {
            return AjaxResult.error("表单标识已存在！");
        }
        return AjaxResult.success(formService.saveForm(bo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (流程表单/删除表单内容)
     * @params [formIds]
     * @author yanbin
     * @date 2024/2/26 16:19
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程表单/删除表单内容", notes = "2024-2-26 yanbin")
    @ApiImplicitParam(name = "formIds", value = "单个或多个表单模型id", required = true, dataType = "String[]", paramType = "path", dataTypeClass = String[].class)
    @PreAuthorize("@ss.hasPermi('workflow:form:delForm')")
    @DeleteMapping("/delForm/{formIds}")
    public AjaxResult delForm(@NotEmpty(message = "主键不能为空") @PathVariable String[] formIds) {
        return toAjax(formService.delForm(Arrays.asList(formIds)));
    }

    /***
     * @description TODO (根据条件筛选指定code的自定义表单组件字段，返回需要的selectOption格式(“label”,“value”))
     * @params wfFormVo
     * @return com.basic.common.core.domain.AjaxResult
     * @author yanbin
     * @date 2024/12/6 8:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "根据条件筛选指定code的自定义表单组件字段，返回需要的selectOption格式(“label”,“value”)", notes = "2024-12-6 yanbin")
    @ApiImplicitParam(name = "wfFormVo", value = "流程表单wfFormVo", required = true, dataType = "body", paramType = "", dataTypeClass = WfFormBo.class)
    @GetMapping("/getVFormWidgetList")
    @PreAuthorize("@ss.hasPermi('workflow:form:getVFormWidgetList')")
    public AjaxResult getVFormWidgetList(WfFormVo wfFormVo) {
        return AjaxResult.success(formService.getVFormWidgetList(wfFormVo));
    }
}
