package com.basic.workflow.service;


import com.basic.workflow.domain.WfDeployForm;
import com.basic.workflow.domain.vo.WfFormVo;
import org.flowable.bpmn.model.BpmnModel;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (流程部署关联表单Service接口)
 * @date 2023/11/3 10:37
 */
public interface IWfDeployFormService {
    /**
     * @return int
     * @description TODO 新增流程实例关联表单)
     * @params [wfDeployForm]
     * @author yanbin
     * @date 2024/1/23 11:32
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    int insertWfDeployForm(WfDeployForm wfDeployForm);

    /**
     * @return boolean
     * @description TODO (保存流程关联表单)
     * @params [deployId, bpmnModel, modelKey]
     * @author yanbin
     * @date 2024/2/27 14:16
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean saveInternalDeployForm(String deployId, BpmnModel bpmnModel, String modelKey);

    /**
     * @return java.lang.Boolean
     * @description TODO (根据formId判断 form是否已经部署流程)
     * @params [formId]
     * @author yanbin
     * @date 2024/2/27 9:33
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Boolean selectExistDeployForFormId(String formId);

    /**
     * @return com.basic.workflow.domain.vo.WfFormVo
     * @description TODO (查询流程部署关联表单信息)
     * @params [deployId, activityType]
     * @author yanbin
     * @date 2024/2/27 10:06
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfFormVo selectDeployFormByDeployIdAndType(String deployId, String activityType);

    /**
     * @return void
     * @description TODO (根据部署deployId删除流程部署表单信息)
     * @params [deployId]
     * @author yanbin
     * @date 2024/2/27 15:50
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteDeployFormForDeployId(String deployId);

    /**
     * @return com.basic.workflow.domain.WfDeployForm
     * @description TODO (根据部署deployId和activityId查询节点表单)
     * @params [deployId, activityId]
     * @author yanbin
     * @date 2024/2/28 15:40
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfDeployForm selectFormByDeployIdAndActId(String deployId, String taskDefinitionKey);

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfDeployForm>
     * @description TODO (根据部署deployId查询部署表单)
     * @params [deployId]
     * @author yanbin
     * @date 2024/3/5 14:09
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfDeployForm> selectDeployFormByDeployId(String deployId);
}
