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
   * @return TableDataInfo 分类列表，包装为表格数据结构
   * @description 按查询条件返回自定义表单分类列表
   * @params [wfFormCategory] 分类查询条件
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @PreAuthorize("@ss.hasPermi('workflow:systemCategory:list')")
  @GetMapping("/list")
  public TableDataInfo list(WfFormCategory wfFormCategory) {
    List<WfFormCategory> list = wfFormCategoryService.selectWfFormCategoryList(wfFormCategory);
    return getDataTable(list);
  }

  /**
   * @return AjaxResult 操作结果，成功时状态码为 1
   * @description 新增自定义表单分类
   * @params [wfFormCategory] 分类对象
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @PreAuthorize("@ss.hasPermi('workflow:systemCategory:add')")

  @Log(title = "自定义单分类", businessType = BusinessType.INSERT)
  @PostMapping
  public AjaxResult add(@RequestBody WfFormCategory wfFormCategory) {
    wfFormCategoryService.insertWfFormCategory(wfFormCategory);
    return AjaxResult.success();
  }

  /**
   * @return AjaxResult 操作结果，成功时状态码为 1
   * @description 修改自定义表单分类，需携带主键定位记录
   * @params [wfFormCategory] 分类对象
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @PreAuthorize("@ss.hasPermi('workflow:systemCategory:edit')")

  @Log(title = "自定义单分类", businessType = BusinessType.UPDATE)
  @PutMapping
  public AjaxResult edit(@RequestBody WfFormCategory wfFormCategory) {
    wfFormCategoryService.updateWfFormCategory(wfFormCategory);
    return AjaxResult.success();
  }

  /**
   * @return AjaxResult 删除结果，全部成功时状态码为 1
   * @description 按主键批量删除自定义表单分类
   * @params [ids] 分类主键集合
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @PreAuthorize("@ss.hasPermi('workflow:systemCategory:remove')")
  @Log(title = "自定义单分类", businessType = BusinessType.DELETE)
  @DeleteMapping("/{ids}")
  public AjaxResult remove(@PathVariable List<String> ids) {
    return toAjax(wfFormCategoryService.deleteWfFormCategoryByIds(ids));
  }
}
