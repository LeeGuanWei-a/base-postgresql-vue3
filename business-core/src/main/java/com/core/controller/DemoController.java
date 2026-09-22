package com.core.controller;


import com.basic.common.annotation.Log;
import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.enums.BusinessType;
import com.basic.common.utils.poi.ExcelUtil;
import com.core.domain.Demo;
import com.core.service.DemoService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 示例代码Controller
 *
 * @author basic
 * @date 2022-06-22
 */
@RestController
@RequestMapping("/business/demo")
//@Anonymous  //匿名访问 方便测试
public class DemoController extends BaseController {
    @Autowired
    private DemoService demoService;

    /**
     * 查询示例代码列表
     */
    @PreAuthorize("@ss.hasPermi('business:demo:list')")
    @GetMapping("/list")
    public TableDataInfo list(Demo demo) {
        System.out.println("I am in too....");
        startPage();
        List<Demo> list = demoService.selectDemoList(demo);
        return getDataTable(list);
    }

    /**
     * 导出示例代码列表
     */
    @PreAuthorize("@ss.hasPermi('business:demo:export')")
    @Log(title = "示例代码", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Demo demo) {
        List<Demo> list = demoService.selectDemoList(demo);
        ExcelUtil<Demo> util = new ExcelUtil<Demo>(Demo.class);
        util.exportExcel(response, list, "示例代码数据");
    }

    /**
     * 获取示例代码详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:demo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(demoService.selectDemoById(id));
    }

    /**
     * 新增示例代码
     */
    @PreAuthorize("@ss.hasPermi('business:demo:add')")
    @Log(title = "示例代码", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Demo demo) {
        return toAjax(demoService.insertDemo(demo));
    }

    /**
     * 修改示例代码
     */
    @PreAuthorize("@ss.hasPermi('business:demo:edit')")
    @Log(title = "示例代码", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Demo demo) {
        return toAjax(demoService.updateDemo(demo));
    }

    /**
     * 删除示例代码
     */
    @PreAuthorize("@ss.hasPermi('business:demo:remove')")
    @Log(title = "示例代码", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(demoService.deleteDemoByIds(ids));
    }
}
