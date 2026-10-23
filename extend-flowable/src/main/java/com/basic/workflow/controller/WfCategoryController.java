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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
 * @description 流程分类控制器，提供流程分类的增删改查接口，支持父子层级与逻辑删除
 * @author yanbin
 * @date 2023/11/3 10:41
 */
@Validated
@RequiredArgsConstructor
@RestController
@Tag(name = "category", description = "流程分类管理")
@RequestMapping("/workflow/category")
public class WfCategoryController extends BaseController {

  private final IWfCategoryService categoryService;

  /**
   * @return TableDataInfo<WfCategoryVo> 树形分类列表，包装为分页结构，总数取顶级分类数量
   * @description 查询流程分类并按父子层级组装成树形结构，子分类挂在父级的 children 下
   * @params [wfCategory] 分类查询条件，可按名称、编码等过滤
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Operation(summary =  "流程分类/查询流程分类列表", description = "流程分类/查询流程分类列表 2023-12-26 yanbin")
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
   * @return List<WfCategoryVo> 含子节点的分类列表
   * @description 查询流程分类并拼装成父子层级的完整列表，不分页
   * @params [wfCategory] 分类查询条件
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Operation(summary =  "流程分类/查询流程分类列表不分页", description = " 流程分类/查询流程分类列表(不分页)  2023-12-26 yanbin")
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
   * @return AjaxResult 分类列表，无数据时为空列表
   * @description 查询全部流程分类，供下拉选择等场景一次性取用
   * @params [category] 分类查询条件
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Operation(summary =  "流程分类/查询全部的流程分类列表", description = "流程分类/查询全部的流程分类列表 2023-12-26 yanbin")
  @PreAuthorize("@ss.hasPermi('workflow:category:list')")
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
   * @return AjaxResult 分类详情对象
   * @description 按主键查询单个流程分类的详细信息
   * @params [categoryId] 分类主键，不能为空
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Operation(summary =  "流程分类/获取流程分类详细信息", description = "流程分类/获取流程分类详细信息 2023-12-26 yanbin")
  @PreAuthorize("@ss.hasPermi('workflow:category:query')")
  @GetMapping("/{categoryId}")
  public AjaxResult getInfo(
      @NotNull(message = "主键不能为空") @PathVariable("categoryId") Long categoryId) {
    return AjaxResult.success(categoryService.queryById(categoryId));
  }

  /**
   * @return AjaxResult 操作结果，编码已存在时返回带提示的错误
   * @description 新增流程分类，先校验分类编码唯一，编码重复时直接返回错误且不写入
   * @params [category] 分类对象，分类名称、编码、路由地址等必填
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Log(title = "流程分类", businessType = BusinessType.INSERT)
  @RepeatSubmit()
  @Operation(summary =  "流程分类/新增流程分类", description = "新增流程分类(deptId(组织架构不为空),categoryName(分类名称不为空)," +
          "code(分类编码不为空),url(路由跳转地址不为空) ) 2023-12-26 yanbin")
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
   * @return AjaxResult 操作结果，编码冲突时返回带提示的错误
   * @description 修改流程分类，先校验分类编码未被其它分类占用，冲突时拒绝更新
   * @params [category] 分类对象，主键必填
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Operation(summary =  "流程分类/修改流程分类", description = "修改流程分类(categoryId必填) 2023-12-26 yanbin")
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
   * @return AjaxResult 删除结果，全部成功时状态码为 1
   * @description 批量删除流程分类，删除前由 Service 校验分类是否允许删除
   * @params [categoryIds] 待删除的分类主键数组，不能为空
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Operation(summary =  "流程分类/删除流程分类", description = "删除流程分类(单个或多个分类id) 2023-12-26 yanbin")
  @PreAuthorize("@ss.hasPermi('workflow:category:remove')")

  @Log(title = "流程分类", businessType = BusinessType.DELETE)
  @DeleteMapping("/{categoryIds}")
  public AjaxResult remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] categoryIds) {
    return toAjax(categoryService.deleteWithValidByIds(Arrays.asList(categoryIds), true));
  }

  /**
   * @return TableDataInfo<WfCategoryVo> 分页结构包装的选项列表
   * @description 查询分类关联的流程信息，返回下拉选项形式的数据集合
   * @params [wfCategory] 分类查询条件
   * @author liguanwei
   * @date 2026/10/17 20:52
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
