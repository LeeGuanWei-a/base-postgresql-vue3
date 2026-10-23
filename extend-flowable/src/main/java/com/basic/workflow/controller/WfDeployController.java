package com.basic.workflow.controller;

import com.basic.common.core.controller.BaseController;
import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.workflow.domain.WfDeployForm;
import com.basic.workflow.domain.vo.WfDeployVo;
import com.basic.workflow.domain.vo.WfFormVo;
import com.basic.workflow.service.IWfDeployFormService;
import com.basic.workflow.service.IWfDeployService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;

/**
 * @description 流程部署管理接口，提供部署列表与可发起部署查询、挂起和激活切换、删除以及部署关联表单的查看
 * @author yanbin
 * @date 2023/11/3 10:41
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "deploy", description = "流程部署管理")
@RequestMapping("/workflow/deploy")
public class WfDeployController extends BaseController {

    private final IWfDeployService deployService;
    private final IWfDeployFormService deployFormService;

    /**
     * @return TableDataInfo<WfDeployVo> 部署记录分页列表
     * @description 按查询条件分页查询流程部署列表
     * @params [processQuery, pageQuery] 部署查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程部署/查询流程部署列表", description = "查询流程部署列表 2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:deploy:list')")
    @GetMapping("/list")
    public TableDataInfo<WfDeployVo> list(ProcessQuery processQuery, PageQuery pageQuery) {
        return deployService.queryPageList(processQuery, pageQuery);
    }

    /**
     * @return TableDataInfo<WfDeployVo> 该流程定义的版本记录分页列表
     * @description 查询指定流程定义的全部历史版本发布记录
     * @params [processKey, pageQuery] 流程定义 KEY；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程部署/查询流程部署版本列表", description = "查询流程部署版本列表 2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:deploy:publishList')")
    @GetMapping("/publishList")
    public TableDataInfo<WfDeployVo> publishList(
            @RequestParam String processKey, PageQuery pageQuery) {
        return deployService.queryPublishList(processKey, pageQuery);
    }

    /**
     * @return AjaxResult 操作结果，成功时状态码为 1
     * @description 激活或挂起指定流程定义，挂起后不可再发起新实例
     * @params [state, definitionId] 目标状态，取激活或挂起；流程定义 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程部署/激活或挂起流程", description = "激活或挂起流程 2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:deploy:changeState')")
    @PutMapping(value = "/changeState")
    public AjaxResult changeState(@RequestParam String state, @RequestParam String definitionId) {
        deployService.updateState(definitionId, state);
        return AjaxResult.success();
    }

    /**
     * @return AjaxResult 操作结果，成功时状态码为 1
     * @description 按部署 ID 批量删除流程部署，并级联清理其流程定义数据
     * @params [deployIds] 部署 ID 数组，不能为空
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程部署/删除流程部署模型", description = "2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:deploy:remove')")
    @DeleteMapping("/{deployIds}")
    public AjaxResult remove(@NotEmpty(message = "主键不能为空") @PathVariable String[] deployIds) {
        deployService.deleteByIds(Arrays.asList(deployIds));
        return AjaxResult.success();
    }

    /**
     * @return AjaxResult 表单信息，未绑定表单时为空
     * @description 查询流程部署绑定的表单配置，可按节点类型区分
     * @params [deployId, activityType] 部署 ID；节点类型，用于区分不同节点绑定的表单
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程部署/查询流程部署关联表单信息", description = "2024-2-27 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:deploy:deployForm')")
    @GetMapping("/deployForm")
    public AjaxResult deployForm(String deployId, String activityType) {
        WfFormVo formVo = deployFormService.selectDeployFormByDeployIdAndType(deployId, activityType);
//        if (Objects.isNull(formVo)) {
//            return AjaxResult.error("请先配置流程表单");
//        }
        return AjaxResult.success(formVo);
    }

    /**
     * @return AjaxResult 部署表单列表，成功时放在 data 中
     * @description 按部署 ID 查询该部署下各节点关联的表单列表
     * @params [deployId] 部署 ID，为空时直接返回参数错误
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Operation(summary =  "流程部署/根据部署deployId查询流程相关部署表单", description = "2024-3-5 yanbin")
    @GetMapping("/getDeployForm/{deployId}")
    @PreAuthorize("@ss.hasPermi('workflow:deploy:getDeployForm')")
    public AjaxResult getDeployForm(@PathVariable String deployId) {
        if (StringUtils.isBlank(deployId)) {
            return AjaxResult.error("参数有误！");
        }
        List<WfDeployForm> list = deployFormService.selectDeployFormByDeployId(deployId);
        return AjaxResult.success(list);
    }
}
