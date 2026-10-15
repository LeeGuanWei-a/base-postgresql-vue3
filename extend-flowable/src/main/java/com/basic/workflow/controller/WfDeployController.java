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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;


/**
 * @author yanbin
 * @description TODO (流程部署/控制器)
 * @date 2023/11/3 10:41
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Api(value = "deploy", tags = "流程部署管理")
@RequestMapping("/workflow/deploy")
public class WfDeployController extends BaseController {

    private final IWfDeployService deployService;
    private final IWfDeployFormService deployFormService;

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfDeployVo>
     * @description TODO (查询流程部署列表)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2023/12/26 11:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程部署/查询流程部署列表", notes = "查询流程部署列表 2023-12-26 yanbin")
    @PreAuthorize("@ss.hasPermi('workflow:deploy:list')")
    @GetMapping("/list")
    public TableDataInfo<WfDeployVo> list(ProcessQuery processQuery, PageQuery pageQuery) {
        return deployService.queryPageList(processQuery, pageQuery);
    }

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfDeployVo>
     * @description TODO (查询流程部署版本列表)
     * @params [processKey, pageQuery]
     * @author yanbin
     * @date 2023/12/26 11:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程部署/查询流程部署版本列表", notes = "查询流程部署版本列表 2023-12-26 yanbin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processKey", value = "流程标识(ProcessXXX.bpmn)", required = true,
                    dataType = "String", paramType = "query", dataTypeClass = String.class),
    })
    @PreAuthorize("@ss.hasPermi('workflow:deploy:publishList')")
    @GetMapping("/publishList")
    public TableDataInfo<WfDeployVo> publishList(
            @RequestParam String processKey, PageQuery pageQuery) {
        return deployService.queryPublishList(processKey, pageQuery);
    }

    /**
     * @return com.basic.common.core.domain.R<java.lang.Void>
     * @description TODO (激活或挂起流程)
     * @params [state, definitionId]
     * @author yanbin
     * @date 2023/12/26 11:30
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程部署/激活或挂起流程", notes = "激活或挂起流程 2023-12-26 yanbin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "状态", required = true,
                    dataType = "String", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "definitionId", value = "流程定义id", required = true,
                    dataType = "String", paramType = "query", dataTypeClass = String.class),
    })
    @PreAuthorize("@ss.hasPermi('workflow:deploy:changeState')")
    @PutMapping(value = "/changeState")
    public AjaxResult changeState(@RequestParam String state, @RequestParam String definitionId) {
        deployService.updateState(definitionId, state);
        return AjaxResult.success();
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (删除流程部署模型)
     * @params [deployIds] 流程部署ids
     * @author yanbin
     * @date 2024/1/23 11:31
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程部署/删除流程部署模型", notes = "2023-12-26 yanbin")
    @ApiImplicitParam(name = "deployIds", value = "单个或多个流程部署id", required = true,
            dataType = "String []", paramType = "path", dataTypeClass = String[].class)
    @PreAuthorize("@ss.hasPermi('workflow:deploy:remove')")
    @DeleteMapping("/{deployIds}")
    public AjaxResult remove(@NotEmpty(message = "主键不能为空") @PathVariable String[] deployIds) {
        deployService.deleteByIds(Arrays.asList(deployIds));
        return AjaxResult.success();
    }

    /**
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (查询流程部署关联表单信息)
     * @params [deployId, activityType]
     * @author yanbin
     * @date 2024/2/27 10:06
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程部署/查询流程部署关联表单信息", notes = "2024-2-27 yanbin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deployId", value = "流程部署id", required = true,
                    dataType = "String", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "activityType", value = "节点类型", required = true,
                    dataType = "String", paramType = "query", dataTypeClass = String.class),
    })
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
     * @return com.basic.common.core.domain.AjaxResult
     * @description TODO (根据部署deployId查询流程相关部署表单)
     * @params [deployId]
     * @author yanbin
     * @date 2024/3/5 14:20
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @ApiOperation(value = "流程部署/根据部署deployId查询流程相关部署表单", notes = "2024-3-5 yanbin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deployId", value = "流程部署id", required = true,
                    dataType = "String", paramType = "query", dataTypeClass = String.class),
    })
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
