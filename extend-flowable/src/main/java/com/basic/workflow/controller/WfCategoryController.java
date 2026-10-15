package com.basic.workflow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basic.common.annotation.Log;
import com.basic.common.annotation.RepeatSubmit;
import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.enums.BusinessType;
import com.basic.workflow.domain.WfCategory;
import com.basic.workflow.domain.vo.WfCategoryVo;
import com.basic.workflow.service.IWfCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dom4j.DocumentException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author yanbin
 * @description TODO (流程分类Controller/控制器)
 * @date 2023/11/3 10:41
 */
@Validated
@RequiredArgsConstructor
@RestController
@Api(value = "category", tags = "流程分类管理")
@RequestMapping("/workflow/category")
public class WfCategoryController extends BaseController {

  private final IWfCategoryService categoryService;

  /**
   * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfCategoryVo>
   * @description TODO (流程分类/查询流程分类列表)
   * @params [wfCategory]
   * @author yanbin
   * @date 2023/12/26 11:18
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @ApiOperation(value = "流程分类/查询流程分类列表", notes = "流程分类/查询流程分类列表 2023-12-26 yanbin")
  @PreAuthorize("@ss.hasPermi('workflow:category:list')")
  @GetMapping("/list")
  public TableDataInfo<WfCategoryVo> list(WfCategory wfCategory) {
    // 查询所有符合条件的数据
    List<WfCategoryVo> all = categoryService.queryPageList(wfCategory);
    // 转成树形结构（顶级 + 递归 children）
    List<WfCategoryVo> tree = categoryService.buildCategoryTree(all);
    Page<WfCategoryVo> page = new Page<>();
    page.setRecords(tree);
    page.setTotal(tree.size());
    return TableDataInfo.build(page);
  }

  /**
   * @return java.util.List<com.basic.workflow.domain.vo.WfCategoryVo>
   * @description TODO (流程分类/查询流程分类列表 (不分页))
   * @params [wfCategory]
   * @author yanbin
   * @date 2023/12/26 11:18
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @ApiOperation(value = "流程分类/查询流程分类列表不分页", notes = " 流程分类/查询流程分类列表(不分页)  2023-12-26 yanbin")
  @PreAuthorize("@ss.hasPermi('workflow:category:listWithoutPage')")
  @GetMapping("/listWithoutPage")
  public List<WfCategoryVo> listWithoutPage(WfCategory wfCategory) {
    //获取所有符合查询条件的数据
    List<WfCategoryVo> list = categoryService.queryPageList(wfCategory);
    //获取拼装好的列表
    List<WfCategoryVo> result = categoryService.queryPageChildrenList(list);
    return result;
  }

  /**
   * @return com.basic.common.core.domain.R<java.util.List></java.util.List>
   * com.basic.workflow.domain.vo.WfCategoryVo>>
   * @description TODO (流程分类/查询全部的流程分类列表)
   * @params [category]
   * @author yanbin
   * @date 2023/12/26 11:18
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @ApiOperation(value = "流程分类/查询全部的流程分类列表", notes = "流程分类/查询全部的流程分类列表 2023-12-26 yanbin")
  @GetMapping("/listAll")
  public AjaxResult listAll(WfCategory category) {
    return AjaxResult.success(categoryService.queryList(category));
  }

  /**
   * 导出流程分类列表
   */
//  @PreAuthorize("@ss.hasPermi('workflow:category:export')")
//    @Log(title = "流程分类", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(@Validated WfCategory category, HttpServletResponse response) {
//        List<WfCategoryVo> list = categoryService.queryList(category);
//        ExcelUtil.exportExcel(list, "流程分类", WfCategoryVo.class, response);
//    }

  /**
   * @return com.basic.common.core.domain.R<com.basic.workflow.domain.vo.WfCategoryVo>
   * @description TODO (流程分类/获取流程分类详细信息)
   * @params [categoryId]
   * @author yanbin
   * @date 2023/12/26 11:26
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @ApiOperation(value = "流程分类/获取流程分类详细信息", notes = "流程分类/获取流程分类详细信息 2023-12-26 yanbin")
  @ApiImplicitParam(name = "categoryId", value = "分类Id", required = true, dataType = "Long",
      paramType = "path", dataTypeClass = Long.class)
  @PreAuthorize("@ss.hasPermi('workflow:category:query')")
  @GetMapping("/{categoryId}")
  public AjaxResult getInfo(
      @NotNull(message = "主键不能为空") @PathVariable("categoryId") Long categoryId) {
    return AjaxResult.success(categoryService.queryById(categoryId));
  }

  /**
   * @return com.basic.common.core.domain.AjaxResult
   * @description TODO (新增流程分类)
   * @params [category]
   * @author yanbin
   * @date 2023/12/26 11:26
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Log(title = "流程分类", businessType = BusinessType.INSERT)
  @RepeatSubmit()
  @ApiOperation(value = "流程分类/新增流程分类", notes =
      "新增流程分类(deptId(组织架构不为空),categoryName(分类名称不为空)," +
          "code(分类编码不为空),url(路由跳转地址不为空) ) 2023-12-26 yanbin")
  @ApiImplicitParam(name = "WfCategory", value = "分类实体", required = true, dataType = "WfCategory",
      paramType = "body", dataTypeClass = WfCategory.class)
  @PreAuthorize("@ss.hasPermi('workflow:category:add')")
  @PostMapping()
  public AjaxResult add(@Validated @RequestBody WfCategory category) {
    if (!categoryService.checkCategoryCodeUnique(category)) {
      return AjaxResult.error(
          "新增流程分类'" + category.getCategoryName() + "'失败，流程编码已存在");
    }
    categoryService.insertCategory(category);
    return AjaxResult.success();
  }

  /**
   * @return com.basic.common.core.domain.AjaxResult
   * @description TODO (修改流程分类)
   * @params [category]
   * @author yanbin
   * @date 2023/12/26 11:26
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @ApiOperation(value = "流程分类/修改流程分类", notes = "修改流程分类(categoryId必填) 2023-12-26 yanbin")
  @ApiImplicitParam(name = "WfCategory", value = "分类实体", required = true, dataType = "WfCategory",
      paramType = "body", dataTypeClass = WfCategory.class)
  @Log(title = "流程分类", businessType = BusinessType.UPDATE)
  @RepeatSubmit()
  @PreAuthorize("@ss.hasPermi('workflow:category:edit')")
  @PutMapping()
  public AjaxResult edit(@Validated @RequestBody WfCategory category) {

    if (!categoryService.checkCategoryCodeUnique(category)) {
      return AjaxResult.error(
          "修改流程分类'" + category.getCategoryName() + "'失败，流程编码已存在");
    }
    categoryService.updateCategory(category);
    return AjaxResult.success();
  }

  /**
   * @return com.basic.common.core.domain.AjaxResult
   * @description TODO (删除流程分类)
   * @params [categoryIds]
   * @author yanbin
   * @date 2023/12/26 11:20
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @ApiOperation(value = "流程分类/删除流程分类", notes = "删除流程分类(单个或多个分类id) 2023-12-26 yanbin")
  @ApiImplicitParam(name = "categoryIds", value = "单个或多个分类id", required = true, dataType = "Long[]",
      paramType = "path", dataTypeClass = Long[].class)
  @PreAuthorize("@ss.hasPermi('workflow:category:remove')")

  @Log(title = "流程分类", businessType = BusinessType.DELETE)
  @DeleteMapping("/{categoryIds}")
  public AjaxResult remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] categoryIds) {
    return toAjax(categoryService.deleteWithValidByIds(Arrays.asList(categoryIds), true));
  }

  /**
   * @return com.basic.common.core.domain.AjaxResult
   * @description TODO (查询分类流程信息)
   * @params [wfCategory]
   * @author yanbin
   * @date 2024/9/27 13:39
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @GetMapping("/getCategoryOption")
  @PreAuthorize("@ss.hasPermi('workflow:category:getCategoryOption')")
  public TableDataInfo<WfCategoryVo> getCategoryOption(WfCategory wfCategory)
      throws DocumentException, IOException {
//        return AjaxResult.success(categoryService.selectCategoryOption(wfCategory));
    List<WfCategoryVo> result = categoryService.selectCategoryOption(wfCategory);
    Page<WfCategoryVo> page = new Page<>();
    page.setRecords(result);
    page.setTotal(result.size());
    return TableDataInfo.build(page);
  }
}
