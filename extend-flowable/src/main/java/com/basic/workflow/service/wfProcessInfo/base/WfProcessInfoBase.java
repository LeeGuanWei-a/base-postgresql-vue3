package com.basic.workflow.service.wfProcessInfo.base;

import com.basic.common.utils.StringUtils;
import com.basic.flowable.factory.FlowServiceFactory;
import com.basic.system.service.ISysRoleService;
import com.basic.system.service.ISysUserService;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.vo.WfCategoryVo;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.mapper.WfDeployFormMapper;
import com.basic.workflow.mapper.WfFlowableProcessMapper;
import com.basic.workflow.service.*;
import com.basic.workflow.service.impl.ExtensionElementsService;
import com.basic.workflow.service.impl.WfFlowableTaskServiceImpl;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.history.HistoricTaskInstance;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (flowable流程流转历史抽象服务类)
 * @date 2024/03/04
 */
public abstract class WfProcessInfoBase extends FlowServiceFactory {
    @Resource
    protected WfDeployFormMapper deployFormMapper;
    @Resource
    protected IWfFormService wfFormService;
    @Resource
    protected IWfCategoryService wfCategoryService;
    @Resource
    protected ISysUserService sysUserService;
    @Resource
    protected ISysRoleService sysRoleService;
    @Resource
    protected IWfExtendProcinstService extendProcinstService;
    @Resource
    protected IWfBpmnModelService wfBpmnModelService;
    @Resource
    protected IWfCommentInfoService wfCommentInfoService;
    @Resource
    protected ExtensionElementsService extensionElementsService;
    @Resource
    protected WfFlowableProcessMapper wfFlowableProcessMapper;
    @Resource
    protected WfFlowableTaskServiceImpl wfFlowableTaskService;
    @Resource
    protected IWfTaskFormService wfTaskFormService;
    @Resource
    protected IWfDeployFormService wfDeployFormService;
    @Resource
    protected IWfCopyService wfCopyService;


    /**
     * @return com.basic.workflow.domain.vo.WfTaskVo
     * @description TODO (通过实例id查询拓展表信息，返回taskVO)  用于代办，已办，已发，查询
     * @params [wfTaskVo, params, procInsId]
     * @author yanbin
     * @date 2024/3/4 10:31
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected WfTaskVo selectProcessOfExtendProcinst(WfTaskVo wfTaskVo, Map<String, Object> params, String procInsId) {
        if (StringUtils.isNoneBlank(procInsId)) {
            //流程业务关联查询
            ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(procInsId);
            if (extendProcinst != null) {
                //TODO 如果进行了指定审批人操作，流程定义id发生变化，无法获得分类，部署等信息，故而通过拓展表获得之前的流程定义
                // 流程定义信息
                ProcessDefinition processDefinition =
                        repositoryService
                                .createProcessDefinitionQuery()
                                .processDefinitionId(extendProcinst.getProcDefId())
                                .singleResult();
                //流程发起的部署id
                wfTaskVo.setDeployId(processDefinition.getDeploymentId());
                //流程分类code
                wfTaskVo.setCategory(processDefinition.getCategory());
                //流程分类名称
                wfTaskVo.setCategoryName(wfCategoryService.queryByCode(processDefinition.getCategory()).getCategoryName());
                //获取分类保存的路由跳转地址
                WfCategoryVo wfCategoryVo = wfCategoryService.getCategoryToUrl(processDefinition.getCategory());
                if(wfCategoryVo!=null){
                    wfTaskVo.setUrl(wfCategoryVo.getUrl());
                }
                //获取流程模型
                Model model = repositoryService.createModelQuery().deploymentId(processDefinition.getDeploymentId()).singleResult();
                if (model != null) {
                    wfTaskVo.setModelId(model.getId());
                    wfTaskVo.setModelKey(model.getKey());
                }
                //业务系统类型
                wfTaskVo.setAppSn(extendProcinst.getTenantId());
                //流程申请人name
                wfTaskVo.setStartUserName(extendProcinst.getCurrentName());
            } else {
                return null;
            }
        }
        return wfTaskVo;
    }

    /**
     * @return boolean
     * @description TODO (判断是否加签过)
     * @params [taskId, processInstanceId, parentTaskId]
     * @author yanbin
     * @date 2024/3/4 10:44
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected boolean checkTaskIsSign(String taskId, String processInstanceId, String parentTaskId) {
        boolean flag = false;
        //查询历史任务
        List<HistoricTaskInstance> list = processEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).orderByProcessInstanceId().asc()
                .list();
        for (HistoricTaskInstance task : list) {
            if (StringUtils.isNotEmpty(task.getParentTaskId())) {
                if (task.getParentTaskId().equals(parentTaskId) && task.getId().equals(taskId)) {
                    flag = true;
                }
            }
        }
        return flag;
    }

}
