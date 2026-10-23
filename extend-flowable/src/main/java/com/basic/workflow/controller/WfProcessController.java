package com.basic.workflow.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.system.service.ISysFilesService;
import com.basic.workflow.domain.bo.WfCopyBo;
import com.basic.workflow.domain.vo.*;
import com.basic.workflow.service.IWfCopyService;
import com.basic.workflow.service.IWfProcessService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @description 工作流流程管理控制器，提供流程发起、查询、抄送、表单与流程数据获取等接口
 * @author yanbin
 * @date 2023/11/3 10:41
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "process", description = "工作流流程管理,代办，已发,流程数据获取类")
@RequestMapping("/workflow/process")
public class WfProcessController extends BaseController {

    private final IWfProcessService processService;
    private final IWfCopyService copyService;
    private final ISysFilesService sysFilesService;

    /**
     * @return TableDataInfo<WfTaskVo> 我发起的流程分页列表
     * @description 分页查询当前用户自己发起过的流程实例
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/我拥有的流程(已发)分页", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:ownList')")
    @GetMapping(value = "/ownList")
    public TableDataInfo<WfTaskVo> ownProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        List<WfTaskVo> list = processService.selectPageOwnProcessList(processQuery, pageQuery);
        return getDataTable(list);
    }

    /**
     * @return List<WfTaskVo> 我发起的流程列表
     * @description 不分页查询当前用户自己发起过的流程实例
     * @params [processQuery] 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/我拥有的流程(已发)不分页", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:getOwnProcessList')")
    @GetMapping(value = "/getOwnProcessList")
    public List<WfTaskVo> getOwnProcessList(ProcessQuery processQuery) {
        return processService.selectOwnProcessList(processQuery);
    }

    /**
     * @return TableDataInfo<WfTaskVo> 待办任务分页列表
     * @description 分页查询当前用户权限范围内可办理的待办任务
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/获取待办列表(分页)", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:todoList')")
    @GetMapping(value = "/todoList")
    public TableDataInfo<WfTaskVo> todoProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        return getDataTable(processService.selectPageTodoProcessList(processQuery, pageQuery));
    }

    /**
     * @return List<WfTaskVo> 待办任务列表
     * @description 不分页查询当前用户权限范围内可办理的待办任务
     * @params [processQuery] 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/获取待办列表(不分页)", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:getTodoModel')")
    @GetMapping(value = "/getTodoModel")
    public List<WfTaskVo> getTodoModel(ProcessQuery processQuery) {
        return processService.selectTodoProcessList(processQuery);
    }

    /**
     * @return TableDataInfo<WfTaskVo> 待签任务分页列表
     * @description 分页查询当前用户可以签收的待签任务
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/获取待签列表(分页)", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:claimList')")
    @GetMapping(value = "/claimList")
    public TableDataInfo<WfTaskVo> claimProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        return processService.selectPageClaimProcessList(processQuery, pageQuery);
    }

    /**
     * @return TableDataInfo<WfTaskVo> 已办任务分页列表
     * @description 分页查询当前用户已办理完成的任务
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/获取已办列表(分页)", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:finishedList')")
    @GetMapping(value = "/finishedList")
    public TableDataInfo<WfTaskVo> finishedProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        List<WfTaskVo> list = processService.selectPageFinishedProcessList(processQuery, pageQuery);
        return getDataTable(list);
    }

    /**
     * @return List<WfTaskVo> 已办任务列表
     * @description 不分页查询当前用户已办理完成的任务
     * @params [processQuery] 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/获取已办列表(不分页)", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:selectFinishedProcessList')")
    @GetMapping(value = "/selectFinishedProcessList")
    public List<WfTaskVo> selectFinishedProcessList(ProcessQuery processQuery) {
        return processService.selectFinishedProcessList(processQuery);
    }

    /**
     * @return TableDataInfo 抄送信息分页列表
     * @description 查询抄送给当前用户的流程抄送信息，并按分页参数返回
     * @params [copyBo] 抄送查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/获取抄送列表(分页)", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:copyList')")
    @GetMapping(value = "/copyList")
    public TableDataInfo copyProcessList(WfCopyBo copyBo) {
        List<WfCopyVo> list = copyService.selectList(copyBo);
        startPage();
        return getDataTable(list);
    }

    /**
     * @return List<Map<String, Object>> 抄送信息列表
     * @description 不分页查询抄送信息，返回通用 Map 结构便于前端直接渲染
     * @params [copyBo] 抄送查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/获取抄送列表(不分页)", description = "2023-12-27 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:getCopyProcessList')")
    @GetMapping(value = "/getCopyProcessList")
    public List<Map<String, Object>> getCopyProcessList(WfCopyBo copyBo) {
        List<Map<String, Object>> list = copyService.selectPageList(copyBo);
        return list;
    }

    /**
     * @return AjaxResult 标记结果，成功时状态码为 1
     * @description 按抄送记录主键把该条抄送信息标记为已阅
     * @params [id] 抄送记录主键，以请求体传入
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/把抄送信息标记为已阅", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:readCopy')")
    @PostMapping("/readCopy")
    public AjaxResult readCopy(@RequestBody Long id) {
        copyService.readCopy(id);
        return AjaxResult.success();
    }

    /**
     * @description 查询我发起的流程并组装导出用的状态字段，当前 Excel 写出语句仍被注释，尚未真正输出文件
     * @params [processQuery, response] 流程查询条件；HTTP 响应对象，预留给 Excel 流写出
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/导出我拥有流程列表", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:ownExport')")
    @PostMapping("/ownExport")
    public void ownExport(@Validated ProcessQuery processQuery, HttpServletResponse response) {
        List<WfTaskVo> list = processService.selectOwnProcessList(processQuery);
        List<WfOwnTaskExportVo> listVo = BeanUtil.copyToList(list, WfOwnTaskExportVo.class);
        for (WfOwnTaskExportVo exportVo : listVo) {
            exportVo.setStatus(ObjectUtil.isNull(exportVo.getFinishTime()) ? "进行中" : "已完成");
        }
        //        ExcelUtil.exportExcel(listVo, "我拥有流程", WfOwnTaskExportVo.class, response);
    }

    /**
     * @description 查询待办流程并转换成导出用的 VO 列表，当前 Excel 写出语句仍被注释，尚未真正输出文件
     * @params [processQuery, response] 流程查询条件；HTTP 响应对象，预留给 Excel 流写出
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/导出待办流程列表", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:todoExport')")
    @PostMapping("/todoExport")
    public void todoExport(@Validated ProcessQuery processQuery, HttpServletResponse response) {
        List<WfTaskVo> list = processService.selectTodoProcessList(processQuery);
        List<WfTodoTaskExportVo> listVo = BeanUtil.copyToList(list, WfTodoTaskExportVo.class);
        //        ExcelUtil.exportExcel(listVo, "待办流程", WfTodoTaskExportVo.class, response);
    }

    /**
     * @description 查询待签流程并转换成导出用的 VO 列表，当前 Excel 写出语句仍被注释，尚未真正输出文件
     * @params [processQuery, response] 流程查询条件；HTTP 响应对象，预留给 Excel 流写出
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/导出待签流程列表", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:claimExport')")
    @PostMapping("/claimExport")
    public void claimExport(@Validated ProcessQuery processQuery, HttpServletResponse response) {
        List<WfTaskVo> list = processService.selectClaimProcessList(processQuery);
        List<WfClaimTaskExportVo> listVo = BeanUtil.copyToList(list, WfClaimTaskExportVo.class);
        //        ExcelUtil.exportExcel(listVo, "待签流程", WfClaimTaskExportVo.class, response);
    }

    /**
     * @description 查询已办流程并转换成导出用的 VO 列表，当前 Excel 写出语句仍被注释，尚未真正输出文件
     * @params [processQuery, response] 流程查询条件；HTTP 响应对象，预留给 Excel 流写出
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/导出已办流程列表", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:finishedExport')")
    @PostMapping("/finishedExport")
    public void finishedExport(@Validated ProcessQuery processQuery, HttpServletResponse response) {
        List<WfTaskVo> list = processService.selectFinishedProcessList(processQuery);
        List<WfFinishedTaskExportVo> listVo = BeanUtil.copyToList(list, WfFinishedTaskExportVo.class);
        //        ExcelUtil.exportExcel(listVo, "已办流程", WfFinishedTaskExportVo.class, response);
    }

    /**
     * @description 查询抄送流程并转换成导出用的 VO 列表，当前 Excel 写出语句仍被注释，尚未真正输出文件
     * @params [copyBo, response] 抄送查询条件；HTTP 响应对象，预留给 Excel 流写出
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/导出抄送流程列表", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:copyExport')")
    @PostMapping("/copyExport")
    public void copyExport(WfCopyBo copyBo, HttpServletResponse response) {
//        copyBo.setUserId(copyBo.getUserId());
        List<WfCopyVo> list = copyService.selectList(copyBo);
        //        ExcelUtil.exportExcel(list, "抄送流程", WfCopyVo.class, response);
    }

    /**
     * @return AjaxResult 流程部署上关联的表单内容
     * @description 按流程定义 ID 与流程实例 ID 查询该部署关联的表单信息
     * @params [procDefId, procInsId] 流程定义 ID；流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/查询流程部署关联表单信息", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:getProcessForm')")
    @GetMapping("/getProcessForm")
    public AjaxResult getForm(@RequestParam(value = "procDefId") String procDefId, @RequestParam(value = "procInsId") String procInsId) {
        return AjaxResult.success(processService.selectFormContent(procDefId, procInsId));
    }

    /**
     * @return AjaxResult 流程定义的 BPMN XML 文本
     * @description 按流程定义 ID 读取该流程的 BPMN XML 内容，供流程图渲染使用
     * @params [processDefId] 流程定义 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/读取xml文件", description = "2023-12-26 yanbin")
    @GetMapping("/bpmnXml/{processDefId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:bpmnXml')")
    public AjaxResult getBpmnXml(@PathVariable(value = "processDefId") String processDefId) {
        return AjaxResult.success(processService.queryBpmnXmlById(processDefId));
    }

    /**
     * @return AjaxResult 流程详情，含流转过程与表单数据
     * @description 查询指定流程实例与任务的详情信息，解析 BPMN 时可能抛出 Dom4j 解析异常
     * @params [procInsId, taskId] 流程实例 ID；任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/查询流程详情信息", description = "2023-12-26 yanbin")
    @GetMapping("/detail")
    @PreAuthorize("@ss.hasPermi('workflow:process:detail')")
    public AjaxResult detail(String procInsId, String taskId) throws DocumentException {
        return AjaxResult.success(processService.queryProcessDetail(procInsId, taskId));
    }

    /**
     * @return AjaxResult 含 key 字段的流程定义 KEY
     * @description 按流程定义 ID 取出流程定义 KEY，以 key 字段放进返回结果
     * @params [definitionId] 流程定义 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/根据流程定义ID获取流程key", description = "2023-12-26 yanbin")
    @GetMapping("/getProcessModelKey")
    @PreAuthorize("@ss.hasPermi('workflow:process:getProcessModelKey')")
    public AjaxResult getProcessModelKey(@RequestParam(value = "definitionId") String definitionId) {
        // 流程key
        String key = processService.getProcessModelKey(definitionId);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("key", key);
        return ajax;
    }

    /**
     * @return AjaxResult 流程是否已结束的判断结果
     * @description 按流程实例 ID 判断该流程是否已经结束
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/根据实例id判断流程是否已经结束", description = "2023-12-26 yanbin")
    @GetMapping("/checkFlowIsEnd/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:checkFlowIsEnd')")
    public AjaxResult checkFlowIsEnd(@PathVariable String procInsId) {
        return AjaxResult.success(processService.checkFlowIsEnd(procInsId));
    }

    /**
     * @return AjaxResult 流程实例列表，管理员为全部流程，普通用户为本人参与的流程
     * @description 按当前登录用户的角色区分查询范围，管理员查全部历史流程实例，普通用户只查本人参与过的
     * @params [processQuery] 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/(获取所有流程)区分管理员和普通用户, 管理员获取全部流程，用户获取所参与的流程", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:selectHisProcInsProcess')")
    @GetMapping("/selectHisProcInsProcess")
    public AjaxResult selectHisProcInsProcess(ProcessQuery processQuery) {
        return AjaxResult.success(processService.selectHisProcInsProcess(processQuery));
    }

    /**
     * @return AjaxResult 流程实例对应的业务扩展信息
     * @description 按流程实例 ID 查询该流程的业务扩展信息
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/根据实例id获取流程业务拓展信息", description = "2023-12-26 yanbin")
    @GetMapping("/getProcInsIdToExtends/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:getProcInsIdToExtends')")
    public AjaxResult getProcInsIdToExtends(@PathVariable String procInsId) {
        return AjaxResult.success(processService.getProcInsIdToExtends(procInsId));
    }

    /**
     * @return AjaxResult 已完成节点的历史任务信息列表
     * @description 按流程实例 ID 查询已经走过的节点及其历史任务信息，用于流转轨迹展示
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/获取历史任务信息列表 已经完成的节点信息", description = "2024-1-29 yanbin")
    @GetMapping("/historyProcNodeList/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:historyProcNodeList')")
    public AjaxResult historyProcNodeList(@PathVariable String procInsId) {
        return AjaxResult.success(processService.historyProcNodeList(procInsId));
    }

    /**
     * @return AjaxResult 历史流程表单信息列表
     * @description 按流程实例 ID 查询流转过程中各节点提交过的表单数据
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/获取历史流程表单信息", description = "2024-1-29 yanbin")
    @GetMapping("/processFormList/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:processFormList')")
    public AjaxResult processFormList(@PathVariable String procInsId) {
        return AjaxResult.success(processService.processFormList(procInsId));
    }

    /**
     * @return AjaxResult 流程执行过程数据，含已走与当前节点集合
     * @description 按流程实例 ID 汇总流程执行过程，供流程图高亮已流转路径
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/获取流程执行过程", description = "2024-1-29 yanbin")
    @GetMapping("/getFlowViewer/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:getFlowViewer')")
    public AjaxResult getFlowViewer(@PathVariable String procInsId) {
        return AjaxResult.success(processService.getFlowViewer(procInsId));
    }

    /**
     * @return AjaxResult 当前任务信息
     * @description 按流程实例 ID 与用户 ID 查询该实例当前的运行任务
     * @params [procInsId, userId] 流程实例 ID；用户 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/根据实例id获取当前任务", description = "2024-1-29 yanbin")
    @GetMapping("/getProcInsIdToTask")
    @PreAuthorize("@ss.hasPermi('workflow:process:getProcInsIdToTask')")
    public AjaxResult getProcInsIdToTask(String procInsId, String userId) {
        return AjaxResult.success(processService.getProcInsIdToTask(procInsId, userId));
    }

    /**
     * @return AjaxResult 当前节点（任务）的详情与任务数量
     * @description 按流程实例 ID 查询当前节点的任务详情并统计任务数量
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/根据实例id获取当前节点(任务)的详情，任务数量)", description = "2024-1-29 yanbin")
    @GetMapping("/getCurrentTaskInfo/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:getCurrentTaskInfo')")
    public AjaxResult getCurrentTaskInfo(@PathVariable String procInsId) {
        return AjaxResult.success(processService.getCurrentTaskInfo(procInsId));
    }

    /**
     * @return TableDataInfo<WfTaskVo> 全部流程实例分页列表
     * @description 运维视角分页查询全部流程实例信息，不做参与人过滤
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/运维管理/获取所有实例信息)", description = "2024-2-4 yanbin")
    @GetMapping("/selectProcessPrcInstance")
    @PreAuthorize("@ss.hasPermi('workflow:process:selectProcessPrcInstance')")
    public TableDataInfo<WfTaskVo> selectProcessPrcInstance(ProcessQuery processQuery, PageQuery pageQuery) {
        return processService.selectProcessPrcInstance(processQuery, pageQuery);
    }

    /**
     * @return TableDataInfo 全部任务分页列表
     * @description 运维视角分页查询所有任务信息，不分办理人
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/运维管理/获取所有任务信息)", description = "2024-2-5 yanbin")
    @GetMapping("/selectProcessAllTask")
    @PreAuthorize("@ss.hasPermi('workflow:process:selectProcessAllTask')")
    public TableDataInfo selectProcessAllTask(ProcessQuery processQuery, PageQuery pageQuery) {
        List<WfTaskVo> list = processService.selectProcessAllTask(processQuery, pageQuery);
        startPage();
        return getDataTable(list);
    }

    /**
     * @return TableDataInfo 指定用户相关的任务分页列表
     * @description 运维视角按用户分页查询该用户相关的任务信息
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/运维管理/获取当前任务信息)", description = "2024-2-5 yanbin")
    @GetMapping("/selectProcessTaskByUser")
    @PreAuthorize("@ss.hasPermi('workflow:process:selectProcessTaskByUser')")
    public TableDataInfo selectProcessTaskByUser(ProcessQuery processQuery, PageQuery pageQuery) {
        List<WfTaskVo> list = processService.selectProcessTaskByUser(processQuery, pageQuery);
        startPage();
        return getDataTable(list);
    }

    /**
     * @return TableDataInfo<Object> 待办任务与抄送信息的合并分页结果
     * @description 合并查询待办任务与抄送信息并分页返回，解析时间时可能抛出 ParseException
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/获取代办任务和抄送信息)", description = "2024-2-29 yanbin")
    @GetMapping("/getTaskTodoAndCopy")
    @PreAuthorize("@ss.hasPermi('workflow:process:getTaskTodoAndCopy')")
    public TableDataInfo<Object> getTaskTodoAndCopy(ProcessQuery processQuery, PageQuery pageQuery) throws ParseException {
        return processService.getTaskTodoAndCopy(processQuery, pageQuery);
    }

    /**
     * @return AjaxResult 该任务的流程变量集合
     * @description 按任务 ID 查询当前任务上的流程变量
     * @params [taskId] 流程任务 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/根据任务taskId获取当前任务的变量", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:getTaskVariables')")
    @GetMapping(value = "/getTaskVariables/{taskId}")
    public AjaxResult getTaskVariables(@PathVariable(value = "taskId") String taskId) {
        return AjaxResult.success(processService.getTaskVariables(taskId));
    }

    /**
     * @return AjaxResult 该流程实例的全部变量
     * @description 按流程实例 ID 查询整条流程上的所有变量
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/根据实例procInsId获取流程所有变量", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:getProcessVariables')")
    @GetMapping(value = "/getProcessVariables/{procInsId}")
    public AjaxResult getProcessVariables(@PathVariable(value = "procInsId") String procInsId) {
        return AjaxResult.success(processService.getProcessVariables(procInsId));
    }

    /**
     * @return AjaxResult 流程实例关联的附件列表
     * @description 按流程实例 ID 查询该流程挂载的附件文件清单
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/根据流程实例id获取附件表格", description = "2024-3-5 yanbin")
    @PostMapping("/getFilesByProcessInstanceId/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:getFilesByProcessInstanceId')")
    public AjaxResult getFilesByProcessInstanceId(@PathVariable String procInsId) {
        return AjaxResult.success(sysFilesService.getListByProcessInstanceId(procInsId));
    }

    /**
     * @return AjaxResult 流程流转记录（审批意见）列表
     * @description 按流程实例 ID 查询各节点的审批意见与流转记录
     * @params [procInsId] 流程实例 ID，为空时返回参数校验失败提示
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "工作流流程管理/根据实例procInsId获取流程流转记录(审批意见)", description = "2024-3-15 yanbin")
    @GetMapping("/getProcessCommentInfo/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:getProcessCommentInfo')")
    public AjaxResult handleProcessCommentInfo(@NotNull(message = "操作失误，请联系管理员") @PathVariable String procInsId) {
        return AjaxResult.success(processService.getProcessCommentInfo(procInsId));
    }

}
