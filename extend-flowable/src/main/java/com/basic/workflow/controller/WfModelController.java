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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description 流程模型管理接口，提供模型的查询与详情、编辑保存、部署发布、挂起激活、导出以及分类下的模型树
 * @author yanbin
 * @date 2023/11/3 10:41
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "model", description = "工作流流程模型管理,model引擎类")
@RequestMapping("/workflow/model")
public class WfModelController extends BaseController {
    private final IWfModelService modelService;
    private final IWfCategoryService categoryService;

    /**
     * @return TableDataInfo<WfModelVo> 流程模型分页列表
     * @description 按查询条件分页查询流程模型列表
     * @params [modelBo, pageQuery] 流程模型查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/查询流程模型列表", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:list')")
    @GetMapping("/list")
    public TableDataInfo<WfModelVo> list(WfModelBo modelBo, PageQuery pageQuery) {
        return modelService.list(modelBo, pageQuery);
    }

    /**
     * @return TableDataInfo 已部署的、可发起的流程分页列表
     * @description 查询流程启动列表，只返回已部署的流程
     * @params [modelBo] 流程模型查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/查询流程启动列表", description = "2023-12-26 yanbin")
    @GetMapping("/listModelLaunch")
    @PreAuthorize("@ss.hasPermi('workflow:model:listModelLaunch')")
    public TableDataInfo listModelLaunch(WfModelBo modelBo) {
        List<WfModelVo> list = modelService.listModelLaunch(modelBo);
        return getDataTable(list);
    }

    /**
     * @return TableDataInfo<WfModelVo> 历史流程模型分页列表
     * @description 分页查询流程模型的历史模型列表
     * @params [modelBo, pageQuery] 流程模型查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/查询流程模型列表历史模型", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:historyList')")
    @GetMapping("/historyList")
    public TableDataInfo<WfModelVo> historyList(WfModelBo modelBo, PageQuery pageQuery) {
        return modelService.historyList(modelBo, pageQuery);
    }

    /**
     * @return AjaxResult 流程模型详情
     * @description 按模型主键获取流程模型详细信息
     * @params [modelId] 模型主键，不能为空
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/获取流程模型详细信息", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:getInfo')")
    @GetMapping(value = "/{modelId}")
    public AjaxResult getInfo(@NotNull(message = "主键不能为空") @PathVariable("modelId") String modelId)
            throws IOException, DocumentException {
        return AjaxResult.success(modelService.getModel(modelId));
    }

    /**
     * @return AjaxResult 模型对应的 BPMN XML 字符串
     * @description 按模型主键查询该模型的 BPMN XML 内容
     * @params [modelId] 模型主键，不能为空
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/获取流程表单详细信息", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:bpmnXml')")
    @GetMapping(value = "/bpmnXml/{modelId}")
    public AjaxResult getBpmnXml(@NotNull(message = "主键不能为空") @PathVariable("modelId") String modelId)
            throws IOException, DocumentException {
        return AjaxResult.success("操作成功", modelService.queryBpmnXmlById(modelId));
    }

    /**
     * @return AjaxResult 新增后的流程模型信息
     * @description 新增流程模型，按新增分组校验入参
     * @params [modelBo] 流程模型业务对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/新增流程模型", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:add')")
    @PostMapping
    public AjaxResult add(@Validated(AddGroup.class) @RequestBody WfModelBo modelBo) {
        return AjaxResult.success(modelService.insertModel(modelBo));
    }

    /**
     * @return AjaxResult 修改后的流程模型信息
     * @description 修改流程模型，按编辑分组校验入参
     * @params [modelBo] 流程模型业务对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/修改流程模型", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:edit')")
    @PutMapping
    public AjaxResult edit(@Validated(EditGroup.class) @RequestBody WfModelBo modelBo) {
        return AjaxResult.success(modelService.updateModel(modelBo));
    }

    /**
     * @return AjaxResult 保存结果，成功时状态码为1
     * @description 保存流程模型的 BPMN 设计内容并处理防重复提交
     * @params [modelBo] 流程模型业务对象，含模型主键与 BPMN XML
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/保存流程模型", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:save')")
    @RepeatSubmit()
    @PostMapping("/save")
    public AjaxResult save(@RequestBody WfModelBo modelBo) {
        return toAjax(modelService.saveModel(modelBo));
    }

    /**
     * @return AjaxResult 设为最新后的流程模型信息
     * @description 把指定流程模型设为该模型的最新版本
     * @params [modelId] 模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/设为最新流程模型", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:latest')")
    @RepeatSubmit()
    @PostMapping("/latest")
    public AjaxResult latest(@RequestParam String modelId) {
        return AjaxResult.success(modelService.latestModel(modelId));
    }

    /**
     * @return AjaxResult 删除结果
     * @description 按模型主键批量删除流程模型及其部署
     * @params [modelIds] 流程模型主键数组，不能为空
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/删除流程模型", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:remove')")
    @DeleteMapping("/{modelIds}")
    public AjaxResult remove(@NotEmpty(message = "主键不能为空") @PathVariable String[] modelIds) {
        return AjaxResult.success(modelService.deleteByIds(Arrays.asList(modelIds)));
    }

    /**
     * @return AjaxResult 部署结果，成功时状态码为1
     * @description 把流程模型发布为流程定义，完成部署
     * @params [wfModelBo] 流程模型业务对象，含模型主键与分类
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/部署流程模型", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:deploy')")
    @RepeatSubmit()
    @PostMapping("/deploy")
    public AjaxResult deployModel(@RequestBody WfModelBo wfModelBo) {
        return toAjax(modelService.deployModel(wfModelBo));
    }

    /**
     * @description 整理导出用的流程模型数据，把分类编码翻译成分类名称，Excel 写出逻辑目前为注释状态
     * @params [modelBo, response] 流程模型查询条件；HTTP 响应对象，供写出 Excel 使用
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/导出流程模型数据", description = "2023-12-26 yanbin")
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
     * @return AjaxResult 流程实例启动结果
     * @description 按流程定义 key 发起一次流程实例，用于表单发布场景
     * @params [startProcessInstanceVo] 流程启动入参，含流程定义 key 与业务变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/流程启动 根据流程key启动", description = "2023-12-26 yanbin")
    @PostMapping("/publishModel")
    @PreAuthorize("@ss.hasPermi('workflow:model:publishModel')")
    public AjaxResult startFormFlow(@RequestBody StartProcessInstanceVo startProcessInstanceVo) {
        return modelService.startFormFlow(startProcessInstanceVo);
    }

    /**
     * @return AjaxResult 挂起结果
     * @description 挂起（停用）指定流程模型对应的流程定义，挂起后不可再发起或推进
     * @params [wfModelBo] 流程模型业务对象，含模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/流程模型挂起(停用)", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:suspended')")
    @RepeatSubmit()
    @PostMapping("/suspended")
    public AjaxResult suspendedProcessDefinition(@RequestBody WfModelBo wfModelBo) {
        modelService.suspendedProcessDefinition(wfModelBo);
        return AjaxResult.success();
    }

    /**
     * @return AjaxResult 激活结果
     * @description 激活指定流程模型对应的已挂起流程定义
     * @params [wfModelBo] 流程模型业务对象，含模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/流程模型激活", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:model:activate')")
    @RepeatSubmit()
    @PostMapping("/activate")
    public AjaxResult activateProcessDefinition(@RequestBody WfModelBo wfModelBo) {
        modelService.activateProcessDefinition(wfModelBo);
        return AjaxResult.success();
    }

    /**
     * @return AjaxResult 该分类下已部署的流程信息
     * @description 按流程分类编码查询其下已部署的流程信息
     * @params [code] 流程分类编码
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程模型管理/根据流程分类code获取流程信息", description = "2023-12-27 yanbin")
    @GetMapping("/getCategoryToModel/{code}")
    @PreAuthorize("@ss.hasPermi('workflow:model:getCategoryToModel')")
    public AjaxResult getCategoryToModel(@PathVariable String code) {
        return AjaxResult.success(modelService.getCategoryToModel(code));
    }
}
