package com.basic.workflow.controller;

import com.basic.common.annotation.Log;
import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.enums.BusinessType;
import com.basic.workflow.domain.WfFormCategory;
import com.basic.workflow.service.IWfFormCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 自定义单分类Controller
 *
 * @author basic
 * @date 2024-03-27
 */
@RestController
@RequestMapping("/system/category")
public class WfFormCategoryController extends BaseController {

  @Autowired
  private IWfFormCategoryService wfFormCategoryService;

  /**
   * 查询自定义单分类列表
   */
  @PreAuthorize("@ss.hasPermi('workflow:systemCategory:list')")
  @GetMapping("/list")
  public TableDataInfo list(WfFormCategory wfFormCategory) {
    List<WfFormCategory> list = wfFormCategoryService.selectWfFormCategoryList(wfFormCategory);
    return getDataTable(list);
  }

  /**
   * 新增自定义单分类
   */
  @PreAuthorize("@ss.hasPermi('workflow:systemCategory:add')")

  @Log(title = "自定义单分类", businessType = BusinessType.INSERT)
  @PostMapping
  public AjaxResult add(@RequestBody WfFormCategory wfFormCategory) {
    wfFormCategoryService.insertWfFormCategory(wfFormCategory);
    return AjaxResult.success();
  }

  /**
   * 修改自定义单分类
   */
  @PreAuthorize("@ss.hasPermi('workflow:systemCategory:edit')")

  @Log(title = "自定义单分类", businessType = BusinessType.UPDATE)
  @PutMapping
  public AjaxResult edit(@RequestBody WfFormCategory wfFormCategory) {
    wfFormCategoryService.updateWfFormCategory(wfFormCategory);
    return AjaxResult.success();
  }

  /**
   * 删除自定义单分类
   */
  @PreAuthorize("@ss.hasPermi('workflow:systemCategory:remove')")
  @Log(title = "自定义单分类", businessType = BusinessType.DELETE)
  @DeleteMapping("/{ids}")
  public AjaxResult remove(@PathVariable List<String> ids) {
    return toAjax(wfFormCategoryService.deleteWfFormCategoryByIds(ids));
  }
}
