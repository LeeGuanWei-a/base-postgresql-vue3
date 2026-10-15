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
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.List;
import java.util.Map;


/**
 * @author yanbin
 * @description TODO (工作流流程管理/控制器)
 * @date 2023/11/3 10:41
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Api(value = "process", tags = "工作流流程管理,代办，已发,流程数据获取类")
@RequestMapping("/workflow/process")
public class WfProcessController extends BaseController {

    private final IWfProcessService processService;
    private final IWfCopyService copyService;
    private final ISysFilesService sysFilesService;

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (我拥有的流程) 已发分页
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2023/11/3 10:42
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/我拥有的流程(已发)分页", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:ownList')")
    @GetMapping(value = "/ownList")
    public TableDataInfo<WfTaskVo> ownProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        List<WfTaskVo> list = processService.selectPageOwnProcessList(processQuery, pageQuery);
        return getDataTable(list);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (工作流流程管理/我拥有的流程(已发)不分页
     * @params [processQuery]
     * @author yanbin
     * @date 2023/12/27 10:04
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/我拥有的流程(已发)不分页", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:getOwnProcessList')")
    @GetMapping(value = "/getOwnProcessList")
    public List<WfTaskVo> getOwnProcessList(ProcessQuery processQuery) {
        return processService.selectOwnProcessList(processQuery);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (获取待办列表)分页
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2023/11/3 10:42
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/获取待办列表(分页)", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:todoList')")
    @GetMapping(value = "/todoList")
    public TableDataInfo<WfTaskVo> todoProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        return getDataTable(processService.selectPageTodoProcessList(processQuery, pageQuery));
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (获取待办列表)不分页
     * @params [processQuery]
     * @author yanbin
     * @date 2023/11/3 10:42
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/获取待办列表(不分页)", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:getTodoModel')")
    @GetMapping(value = "/getTodoModel")
    public List<WfTaskVo> getTodoModel(ProcessQuery processQuery) {
        return processService.selectTodoProcessList(processQuery);
    }


    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (获取待签列表)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2023/11/3 10:42
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/获取待签列表(分页)", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:claimList')")
    @GetMapping(value = "/claimList")
    public TableDataInfo<WfTaskVo> claimProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        return processService.selectPageClaimProcessList(processQuery, pageQuery);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (获取已办列表)分页
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2023/11/3 10:42
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/获取已办列表(分页)", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:finishedList')")
    @GetMapping(value = "/finishedList")
    public TableDataInfo<WfTaskVo> finishedProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        List<WfTaskVo> list = processService.selectPageFinishedProcessList(processQuery, pageQuery);
        return getDataTable(list);
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (工作流流程管理/获取已办列表(不分页))
     * @params [processQuery]
     * @author yanbin
     * @date 2023/12/27 17:02
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/获取已办列表(不分页)", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:selectFinishedProcessList')")
    @GetMapping(value = "/selectFinishedProcessList")
    public List<WfTaskVo> selectFinishedProcessList(ProcessQuery processQuery) {
        return processService.selectFinishedProcessList(processQuery);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfCopyVo>
     * @description TODO (获取抄送列表)
     * @params [copyBo流程抄送对象, pageQuery分页参数]
     * @author yanbin
     * @date 2023/11/3 10:42
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/获取抄送列表(分页)", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:copyList')")
    @GetMapping(value = "/copyList")
    public TableDataInfo copyProcessList(WfCopyBo copyBo) {
        List<WfCopyVo> list = copyService.selectList(copyBo);
        startPage();
        return getDataTable(list);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo
     * @description TODO (工作流流程管理/获取抄送列表(不分页))
     * @params [copyBo]
     * @author yanbin
     * @date 2023/12/27 17:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/获取抄送列表(不分页)", notes = "2023-12-27 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:getCopyProcessList')")
    @GetMapping(value = "/getCopyProcessList")
    public List<Map<String, Object>> getCopyProcessList(WfCopyBo copyBo) {
        List<Map<String, Object>> list = copyService.selectPageList(copyBo);
        return list;
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (把抄送信息标记为已阅)
     * @params [definitionId]
     * @author liangjiayong
     * @date 2023/11/9 17:59
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/把抄送信息标记为已阅", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:readCopy')")
    @PostMapping("/readCopy")
    public AjaxResult readCopy(@RequestBody Long id) {
        copyService.readCopy(id);
        return AjaxResult.success();
    }

    /**
     * @return void
     * @description TODO (导出我拥有流程列表)
     * @params [processQuery, response]
     * @author yanbin
     * @date 2023/11/3 10:43
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/导出我拥有流程列表", notes = "2023-12-26 yanbin")
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
     * @return void
     * @description TODO (导出待办流程列表)
     * @params [processQuery, response]
     * @author yanbin
     * @date 2023/11/3 10:43
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/导出待办流程列表", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:todoExport')")
    @PostMapping("/todoExport")
    public void todoExport(@Validated ProcessQuery processQuery, HttpServletResponse response) {
        List<WfTaskVo> list = processService.selectTodoProcessList(processQuery);
        List<WfTodoTaskExportVo> listVo = BeanUtil.copyToList(list, WfTodoTaskExportVo.class);
        //        ExcelUtil.exportExcel(listVo, "待办流程", WfTodoTaskExportVo.class, response);
    }


    /**
     * @return void
     * @description TODO (导出待签流程列表)
     * @params [processQuery, response]
     * @author yanbin
     * @date 2023/11/3 10:43
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/导出待签流程列表", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:claimExport')")
    @PostMapping("/claimExport")
    public void claimExport(@Validated ProcessQuery processQuery, HttpServletResponse response) {
        List<WfTaskVo> list = processService.selectClaimProcessList(processQuery);
        List<WfClaimTaskExportVo> listVo = BeanUtil.copyToList(list, WfClaimTaskExportVo.class);
        //        ExcelUtil.exportExcel(listVo, "待签流程", WfClaimTaskExportVo.class, response);
    }

    /**
     * @return void
     * @description TODO (导出已办流程列表)
     * @params [processQuery, response]
     * @author yanbin
     * @date 2023/11/3 10:43
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/导出已办流程列表", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:finishedExport')")
    @PostMapping("/finishedExport")
    public void finishedExport(@Validated ProcessQuery processQuery, HttpServletResponse response) {
        List<WfTaskVo> list = processService.selectFinishedProcessList(processQuery);
        List<WfFinishedTaskExportVo> listVo = BeanUtil.copyToList(list, WfFinishedTaskExportVo.class);
        //        ExcelUtil.exportExcel(listVo, "已办流程", WfFinishedTaskExportVo.class, response);
    }

    /**
     * @return void
     * @description TODO (导出抄送流程列表)
     * @params [copyBo, response]
     * @author yanbin
     * @date 2023/11/3 10:43
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/导出抄送流程列表", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "copyBo", value = "流程抄送Bo", required = true, dataType = "WfCopyBo", paramType = "query", dataTypeClass = WfCopyBo.class)
    @PreAuthorize("@ss.hasPermi('workflow:process:copyExport')")
    @PostMapping("/copyExport")
    public void copyExport(WfCopyBo copyBo, HttpServletResponse response) {
//        copyBo.setUserId(copyBo.getUserId());
        List<WfCopyVo> list = copyService.selectList(copyBo);
        //        ExcelUtil.exportExcel(list, "抄送流程", WfCopyVo.class, response);
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (查询流程部署关联表单信息)
     * @params [procDefId, procInsId]
     * @author yanbin
     * @date 2024/3/5 13:53
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/查询流程部署关联表单信息", notes = "2023-12-26 yanbin")
    @ApiImplicitParams({@ApiImplicitParam(name = "procDefId", value = "流程定义id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class), @ApiImplicitParam(name = "deployId", value = "流程部署id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class),})
    @PreAuthorize("@ss.hasPermi('workflow:process:getProcessForm')")
    @GetMapping("/getProcessForm")
    public AjaxResult getForm(@RequestParam(value = "procDefId") String procDefId, @RequestParam(value = "procInsId") String procInsId) {
        return AjaxResult.success(processService.selectFormContent(procDefId, procInsId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (读取xml文件)
     * @params [processDefId 流程定义ID]
     * @author yanbin
     * @date 2023/11/3 10:46
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/读取xml文件", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "processDefId", value = "流程定义id", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    @GetMapping("/bpmnXml/{processDefId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:bpmnXml')")
    public AjaxResult getBpmnXml(@PathVariable(value = "processDefId") String processDefId) {
        return AjaxResult.success(processService.queryBpmnXmlById(processDefId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (查询流程详情信息)
     * @params [procInsId流程实例ID, taskId任务ID]
     * @author yanbin
     * @date 2023/11/3 10:46
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/查询流程详情信息", notes = "2023-12-26 yanbin")
    @ApiImplicitParams({@ApiImplicitParam(name = "procInsId", value = "流程实例id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class), @ApiImplicitParam(name = "taskId", value = "流程任务id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class),})
    @GetMapping("/detail")
    @PreAuthorize("@ss.hasPermi('workflow:process:detail')")
    public AjaxResult detail(String procInsId, String taskId) throws DocumentException {
        return AjaxResult.success(processService.queryProcessDetail(procInsId, taskId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (根据流程定义ID获取流程key)
     * @params [definitionId]
     * @author yanbin
     * @date 2023/11/1 17:33
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/根据流程定义ID获取流程key", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "definitionId", value = "流程定义id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class)
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
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (根据实例id判断流程是否已经结束)
     * @params [procInsId]
     * @author yanbin
     * @date 2023/12/5 17:23
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/根据实例id判断流程是否已经结束", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "procInsId", value = "流程实例id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class)
    @GetMapping("/checkFlowIsEnd/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:checkFlowIsEnd')")
    public AjaxResult checkFlowIsEnd(@PathVariable String procInsId) {
        return AjaxResult.success(processService.checkFlowIsEnd(procInsId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (获取所有流程)区分管理员和普通用户, 管理员获取全部流程，用户获取所参与的流程
     * @params [ProcessQuery]
     * @author yanbin
     * @date 2023/12/27 14:18
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/(获取所有流程)区分管理员和普通用户, 管理员获取全部流程，用户获取所参与的流程", notes = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:process:selectHisProcInsProcess')")
    @GetMapping("/selectHisProcInsProcess")
    public AjaxResult selectHisProcInsProcess(ProcessQuery processQuery) {
        return AjaxResult.success(processService.selectHisProcInsProcess(processQuery));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (根据实例id获取流程业务拓展信息)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/19 16:55
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/根据实例id获取流程业务拓展信息", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "procInsId", value = "流程实例id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class)
    @GetMapping("/getProcInsIdToExtends/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:getProcInsIdToExtends')")
    public AjaxResult getProcInsIdToExtends(@PathVariable String procInsId) {
        return AjaxResult.success(processService.getProcInsIdToExtends(procInsId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (获取历史任务信息列表 已经完成的节点信息)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/29 8:40
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/获取历史任务信息列表 已经完成的节点信息", notes = "2024-1-29 yanbin")
    @ApiImplicitParam(name = "procInsId", value = "流程实例id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class)
    @GetMapping("/historyProcNodeList/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:historyProcNodeList')")
    public AjaxResult historyProcNodeList(@PathVariable String procInsId) {
        return AjaxResult.success(processService.historyProcNodeList(procInsId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (获取历史流程表单信息)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/29 8:44
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/获取历史流程表单信息", notes = "2024-1-29 yanbin")
    @ApiImplicitParam(name = "procInsId", value = "流程实例id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class)
    @GetMapping("/processFormList/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:processFormList')")
    public AjaxResult processFormList(@PathVariable String procInsId) {
        return AjaxResult.success(processService.processFormList(procInsId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (获取流程执行过程)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/29 8:46
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/获取流程执行过程", notes = "2024-1-29 yanbin")
    @ApiImplicitParam(name = "procInsId", value = "流程实例id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class)
    @GetMapping("/getFlowViewer/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:getFlowViewer')")
    public AjaxResult getFlowViewer(@PathVariable String procInsId) {
        return AjaxResult.success(processService.getFlowViewer(procInsId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (根据实例id获取当前任务)
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/29 8:51
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/根据实例id获取当前任务", notes = "2024-1-29 yanbin")
    @ApiImplicitParams({@ApiImplicitParam(name = "procInsId", value = "流程实例id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class),})
    @GetMapping("/getProcInsIdToTask")
    @PreAuthorize("@ss.hasPermi('workflow:process:getProcInsIdToTask')")
    public AjaxResult getProcInsIdToTask(String procInsId, String userId) {
        return AjaxResult.success(processService.getProcInsIdToTask(procInsId, userId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (根据实例id获取当前节点(任务)的详情，任务数量))
     * @params [procInsId]
     * @author yanbin
     * @date 2024/1/29 8:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/根据实例id获取当前节点(任务)的详情，任务数量)", notes = "2024-1-29 yanbin")
    @ApiImplicitParam(name = "procInsId", value = "流程实例id", required = true, dataType = "String", paramType = "query", dataTypeClass = String.class)
    @GetMapping("/getCurrentTaskInfo/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:getCurrentTaskInfo')")
    public AjaxResult getCurrentTaskInfo(@PathVariable String procInsId) {
        return AjaxResult.success(processService.getCurrentTaskInfo(procInsId));
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (工作流流程管理/运维管理/获取所有实例信息)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/2/4 8:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/运维管理/获取所有实例信息)", notes = "2024-2-4 yanbin")
    @GetMapping("/selectProcessPrcInstance")
    @PreAuthorize("@ss.hasPermi('workflow:process:selectProcessPrcInstance')")
    public TableDataInfo<WfTaskVo> selectProcessPrcInstance(ProcessQuery processQuery, PageQuery pageQuery) {
        return processService.selectProcessPrcInstance(processQuery, pageQuery);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (工作流流程管理/运维管理/获取所有任务信息)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/2/5 9:01
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/运维管理/获取所有任务信息)", notes = "2024-2-5 yanbin")
    @GetMapping("/selectProcessAllTask")
    @PreAuthorize("@ss.hasPermi('workflow:process:selectProcessAllTask')")
    public TableDataInfo selectProcessAllTask(ProcessQuery processQuery, PageQuery pageQuery) {
        List<WfTaskVo> list = processService.selectProcessAllTask(processQuery, pageQuery);
        startPage();
        return getDataTable(list);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (工作流流程管理/运维管理/获取当前任务信息)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/2/5 9:01
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/运维管理/获取当前任务信息)", notes = "2024-2-5 yanbin")
    @GetMapping("/selectProcessTaskByUser")
    @PreAuthorize("@ss.hasPermi('workflow:process:selectProcessTaskByUser')")
    public TableDataInfo selectProcessTaskByUser(ProcessQuery processQuery, PageQuery pageQuery) {
        List<WfTaskVo> list = processService.selectProcessTaskByUser(processQuery, pageQuery);
        startPage();
        return getDataTable(list);
    }


    /**
     * @return com.basic.common.core.page.TableDataInfo<java.lang.Object>
     * @description TODO (获取代办任务和抄送信息)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/2/29 15:08
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/获取代办任务和抄送信息)", notes = "2024-2-29 yanbin")
    @GetMapping("/getTaskTodoAndCopy")
    @PreAuthorize("@ss.hasPermi('workflow:process:getTaskTodoAndCopy')")
    public TableDataInfo<Object> getTaskTodoAndCopy(ProcessQuery processQuery, PageQuery pageQuery) throws ParseException {
        return processService.getTaskTodoAndCopy(processQuery, pageQuery);
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (根据任务taskId获取当前任务的变量)
     * @params [taskId] 流程任务Id
     * @author yanbin
     * @date 2023/11/3 10:31
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/根据任务taskId获取当前任务的变量", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "taskId", value = "流程任务id", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    @PreAuthorize("@ss.hasPermi('workflow:process:getTaskVariables')")
    @GetMapping(value = "/getTaskVariables/{taskId}")
    public AjaxResult getTaskVariables(@PathVariable(value = "taskId") String taskId) {
        return AjaxResult.success(processService.getTaskVariables(taskId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (根据实例procInsId获取流程所有变量)
     * @params [taskId]
     * @author yanbin
     * @date 2024/3/5 15:29
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/根据实例procInsId获取流程所有变量", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "procInsId", value = "实例id", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    @PreAuthorize("@ss.hasPermi('workflow:process:getProcessVariables')")
    @GetMapping(value = "/getProcessVariables/{procInsId}")
    public AjaxResult getProcessVariables(@PathVariable(value = "procInsId") String procInsId) {
        return AjaxResult.success(processService.getProcessVariables(procInsId));
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (根据流程实例id获取附件表格)
     * @params [processInstanceId]
     * @author yanbin
     * @date 2024/3/5 15:53
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/根据流程实例id获取附件表格", notes = "2024-3-5 yanbin")
    @ApiImplicitParam(name = "procInsId", value = "实例id", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    @PostMapping("/getFilesByProcessInstanceId/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:getFilesByProcessInstanceId')")
    public AjaxResult getFilesByProcessInstanceId(@PathVariable String procInsId) {
        return AjaxResult.success(sysFilesService.getListByProcessInstanceId(procInsId));
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.WfCommentInfo>
     * @description TODO (根据实例procInsId获取流程流转记录(审批意见))
     * @params [procInsId]
     * @author yanbin
     * @date 2024/3/15 11:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "工作流流程管理/根据实例procInsId获取流程流转记录(审批意见)", notes = "2024-3-15 yanbin")
    @ApiImplicitParam(name = "procInsId", value = "实例id", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    @GetMapping("/getProcessCommentInfo/{procInsId}")
    @PreAuthorize("@ss.hasPermi('workflow:process:getProcessCommentInfo')")
    public AjaxResult handleProcessCommentInfo(@NotNull(message = "操作失误，请联系管理员") @PathVariable String procInsId) {
        return AjaxResult.success(processService.getProcessCommentInfo(procInsId));
    }

}
