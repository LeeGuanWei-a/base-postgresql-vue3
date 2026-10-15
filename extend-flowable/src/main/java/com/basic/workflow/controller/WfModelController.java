package com.basic.workflow.controller;

import cn.hutool.core.bean.BeanUtil;
import com.basic.common.annotation.RepeatSubmit;
import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.core.validate.AddGroup;
import com.basic.common.core.validate.EditGroup;
import com.basic.workflow.domain.WfCategory;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.domain.vo.StartProcessInstanceVo;
import com.basic.workflow.domain.vo.WfCategoryVo;
import com.basic.workflow.domain.vo.WfModelExportVo;
import com.basic.workflow.domain.vo.WfModelVo;
import com.basic.workflow.service.IWfCategoryService;
import com.basic.workflow.service.IWfModelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yanbin
 * @description TODO (工作流流程模型管理/控制器)
 * @date 2023/11/3 10:41
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Api(value = "model", tags = "工作流流程模型管理,model引擎类")
@RequestMapping("/workflow/model")
public class WfModelController extends BaseController {
    private final IWfModelService modelService;
    private final IWfCategoryService categoryService;

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfModelVo>
     * @description TODO (查询流程模型列表)
     * @params [modelBo流程模型对象, pageQuery]
     * @author yanbin
     * @date 2023/11/17 16:18
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/查询流程模型列表", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:list')")
    @GetMapping("/list")
    public TableDataInfo<WfModelVo> list(WfModelBo modelBo, PageQuery pageQuery) {
        return modelService.list(modelBo, pageQuery);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo
     * @description TODO (查询流程启动列表)已部署的流程
     * @params [modelBo流程模型对象]
     * @author yanbin
     * @date 2023/11/17 16:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/查询流程启动列表", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "WfModelBo", value = "流程模型实体对象", required = true,
            dataType = "WfModelBo", paramType = "query", dataTypeClass = WfModelBo.class)
    @GetMapping("/listModelLaunch")
    @PreAuthorize("@ss.hasPermi('workflow:model:listModelLaunch')")
    public TableDataInfo listModelLaunch(WfModelBo modelBo) {
        List<WfModelVo> list = modelService.listModelLaunch(modelBo);
        return getDataTable(list);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfModelVo>
     * @description TODO (/查询流程模型列表)历史模型
     * @params [modelBo流程模型对象, pageQuery]
     * @author yanbin
     * @date 2023/11/17 16:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/查询流程模型列表历史模型", notes = "2023-12-26 yanbin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageQuery", value = "分页参数", required = true,
                    dataType = "PageQuery", paramType = "query", dataTypeClass = PageQuery.class)
    })
    @PreAuthorize("@ss.hasPermi('workflow:model:historyList')")
    @GetMapping("/historyList")
    public TableDataInfo<WfModelVo> historyList(WfModelBo modelBo, PageQuery pageQuery) {
        return modelService.historyList(modelBo, pageQuery);
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (获取流程模型详细信息)
     * @params [modelId模型主键]
     * @author yanbin
     * @date 2023/11/17 16:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/获取流程模型详细信息", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "modelId", value = "流程模型id", required = true,
            dataType = "String", paramType = "path", dataTypeClass = String.class)
    @PreAuthorize("@ss.hasPermi('workflow:model:getInfo')")
    @GetMapping(value = "/{modelId}")
    public AjaxResult getInfo(@NotNull(message = "主键不能为空") @PathVariable("modelId") String modelId)
            throws IOException, DocumentException {
        return AjaxResult.success(modelService.getModel(modelId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (获取流程表单详细信息)
     * @params [modelId] 模型主键
     * @author yanbin
     * @date 2023/11/17 16:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/获取流程表单详细信息", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "modelId", value = "流程模型id", required = true,
            dataType = "String", paramType = "path", dataTypeClass = String.class)
    @PreAuthorize("@ss.hasPermi('workflow:model:bpmnXml')")
    @GetMapping(value = "/bpmnXml/{modelId}")
    public AjaxResult getBpmnXml(@NotNull(message = "主键不能为空") @PathVariable("modelId") String modelId)
            throws IOException, DocumentException {
        return AjaxResult.success("操作成功", modelService.queryBpmnXmlById(modelId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (新增流程模型)
     * @params [modelBo]
     * @author yanbin
     * @date 2023/11/17 16:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/新增流程模型", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "modelBo", value = "流程模型实体对象", required = true,
            dataType = "WfModelBo", paramType = "body", dataTypeClass = WfModelBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:model:add')")
    @PostMapping
    public AjaxResult add(@Validated(AddGroup.class) @RequestBody WfModelBo modelBo) {
        return AjaxResult.success(modelService.insertModel(modelBo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (修改流程模型)
     * @params [modelBo]
     * @author yanbin
     * @date 2023/11/17 16:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/修改流程模型", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "modelBo", value = "流程模型实体对象", required = true,
            dataType = "WfModelBo", paramType = "body", dataTypeClass = WfModelBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:model:edit')")
    @PutMapping
    public AjaxResult edit(@Validated(EditGroup.class) @RequestBody WfModelBo modelBo) {
        return AjaxResult.success(modelService.updateModel(modelBo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (保存流程模型)
     * @params [modelBo]
     * @author yanbin
     * @date 2023/11/17 16:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/保存流程模型", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "modelBo", value = "流程模型实体对象", required = true,
            dataType = "WfModelBo", paramType = "body", dataTypeClass = WfModelBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:model:save')")
    @RepeatSubmit()
    @PostMapping("/save")
    public AjaxResult save(@RequestBody WfModelBo modelBo) {
        return toAjax(modelService.saveModel(modelBo));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (设为最新流程模型)
     * @params [modelId]
     * @author yanbin
     * @date 2023/11/17 16:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/设为最新流程模型", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "modelId", value = "流程模型id", required = true,
            dataType = "String", paramType = "path", dataTypeClass = String.class)
    @PreAuthorize("@ss.hasPermi('workflow:model:latest')")
    @RepeatSubmit()
    @PostMapping("/latest")
    public AjaxResult latest(@RequestParam String modelId) {
        return AjaxResult.success(modelService.latestModel(modelId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (删除流程模型)
     * @params [modelIds] 流程模型主键串
     * @author yanbin
     * @date 2023/11/17 16:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/删除流程模型", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "modelIds", value = "单个或多个流程模型id", required = true,
            dataType = "String[]", paramType = "path", dataTypeClass = String[].class)
    @PreAuthorize("@ss.hasPermi('workflow:model:remove')")
    @DeleteMapping("/{modelIds}")
    public AjaxResult remove(@NotEmpty(message = "主键不能为空") @PathVariable String[] modelIds) {
        return AjaxResult.success(modelService.deleteByIds(Arrays.asList(modelIds)));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (部署流程模型)
     * @params [wfModelBo]
     * @author yanbin
     * @date 2023/11/17 16:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/部署流程模型", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "wfModelBo", value = "流程模型实体对象", required = true,
            dataType = "WfModelBo", paramType = "body", dataTypeClass = WfModelBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:model:deploy')")
    @RepeatSubmit()
    @PostMapping("/deploy")
    public AjaxResult deployModel(@RequestBody WfModelBo wfModelBo) throws JsonProcessingException {
        return toAjax(modelService.deployModel(wfModelBo));
    }

    /**
     * @return void
     * @description TODO (导出流程模型数据)
     * @params [modelBo, response]
     * @author yanbin
     * @date 2023/11/17 16:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/导出流程模型数据", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "modelBo", value = "流程模型实体对象", required = true,
            dataType = "WfModelBo", paramType = "body", dataTypeClass = WfModelBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:model:export')")
    @PostMapping("/export")
    public void export(WfModelBo modelBo, HttpServletResponse response) {
        List<WfModelVo> list = modelService.list(modelBo);
        List<WfModelExportVo> listVo = BeanUtil.copyToList(list, WfModelExportVo.class);
        List<WfCategoryVo> categoryVos = categoryService.queryList(new WfCategory());
        Map<String, String> categoryMap =
                categoryVos.stream()
                        .collect(Collectors.toMap(WfCategoryVo::getCode, WfCategoryVo::getCategoryName));
        for (WfModelExportVo exportVo : listVo) {
            exportVo.setCategoryName(categoryMap.get(exportVo.getCategory()));
        }
        //        ExcelUtil.exportExcel(listVo, "流程模型数据", WfModelExportVo.class, response);
    }

    /**
     * @return com.basic.flowable.tool.ReturnVo
     * @description TODO (流程启动 根据流程key启动)
     * @params [startProcessInstanceVo]
     * @author yanbin
     * @date 2023/10/24 16:58
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/流程启动 根据流程key启动", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "startProcessInstanceVo", value = "流程启动vo", required = true,
            dataType = "StartProcessInstanceVo", paramType = "body", dataTypeClass = StartProcessInstanceVo.class)
    @PostMapping("/publishModel")
    @PreAuthorize("@ss.hasPermi('workflow:model:publishModel')")
    public AjaxResult startFormFlow(@RequestBody StartProcessInstanceVo startProcessInstanceVo) {
        return modelService.startFormFlow(startProcessInstanceVo);
    }


    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (流程模型挂起)
     * @params [wfModelBo]
     * @author DYH
     * @date 2023/11/9 10:56
     * @remark
     */
    @ApiOperation(value = "工作流流程模型管理/流程模型挂起(停用)", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "wfModelBo", value = "流程模型实体对象", required = true,
            dataType = "WfModelBo", paramType = "body", dataTypeClass = WfModelBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:model:suspended')")
    @RepeatSubmit()
    @PostMapping("/suspended")
    public AjaxResult suspendedProcessDefinition(@RequestBody WfModelBo wfModelBo) {
        modelService.suspendedProcessDefinition(wfModelBo);
        return AjaxResult.success();
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (流程模型激活)
     * @params [wfModelBo]
     * @author DYH
     * @date 2023/11/9 11:26
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/流程模型激活", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "wfModelBo", value = "流程模型实体对象", required = true,
            dataType = "WfModelBo", paramType = "body", dataTypeClass = WfModelBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:model:activate')")
    @RepeatSubmit()
    @PostMapping("/activate")
    public AjaxResult activateProcessDefinition(@RequestBody WfModelBo wfModelBo) {
        modelService.activateProcessDefinition(wfModelBo);
        return AjaxResult.success();
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (工作流流程模型管理/根据流程分类code获取流程信息)
     * @params [code]
     * @author yanbin
     * @date 2023/12/27 15:31
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程模型管理/根据流程分类code获取流程信息", notes = "2023-12-27 yanbin")
    @ApiImplicitParam(name = "code", value = "分类code", required = true,
            dataType = "String", paramType = "path", dataTypeClass = String.class)
    @GetMapping("/getCategoryToModel/{code}")
    @PreAuthorize("@ss.hasPermi('workflow:model:getCategoryToModel')")
    public AjaxResult getCategoryToModel(@PathVariable String code) {
        return AjaxResult.success(modelService.getCategoryToModel(code));
    }
}
