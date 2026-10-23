package com.basic.workflow.service;


import com.basic.workflow.domain.WfDeployForm;
import com.basic.workflow.domain.vo.WfFormVo;
import org.flowable.bpmn.model.BpmnModel;

import java.util.List;

/**
 * @description 流程部署与表单关联的服务接口，维护部署和内置表单的绑定关系并支持引用判断
 * @author yanbin
 * @date 2023/11/3 10:37
 */
public interface IWfDeployFormService {
    /**
     * @return int 写入成功的记录数
     * @description 新增一条流程部署与表单的关联记录
     * @params [wfDeployForm] 部署表单关联信息，含部署主键、节点主键与表单主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    int insertWfDeployForm(WfDeployForm wfDeployForm);

    /**
     * @return boolean 保存成功返回 true
     * @description 解析流程模型中的表单绑定，保存部署与内置表单的关联关系
     * @params [deployId, bpmnModel, modelKey] 部署主键；流程模型对象；流程模型标识
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean saveInternalDeployForm(String deployId, BpmnModel bpmnModel, String modelKey);

    /**
     * @return Boolean 该表单已被流程部署引用时返回 true
     * @description 判断指定表单是否已被流程部署关联
     * @params [formId] 表单主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Boolean selectExistDeployForFormId(String formId);

    /**
     * @return WfFormVo 该节点绑定的表单信息，未绑定时返回 null
     * @description 按部署主键与节点类型查询部署关联的表单信息
     * @params [deployId, activityType] 部署主键；节点类型
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfFormVo selectDeployFormByDeployIdAndType(String deployId, String activityType);

    /**
     * @description 按部署主键删除其关联的表单绑定记录
     * @params [deployId] 部署主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteDeployFormForDeployId(String deployId);

    /**
     * @return WfDeployForm 该节点绑定的部署表单，未绑定时返回 null
     * @description 按部署主键与节点主键查询节点表单绑定
     * @params [deployId, taskDefinitionKey] 部署主键；节点主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfDeployForm selectFormByDeployIdAndActId(String deployId, String taskDefinitionKey);

    /**
     * @return List<WfDeployForm> 该部署关联的全部表单绑定
     * @description 按部署主键查询该部署下的全部表单绑定记录
     * @params [deployId] 部署主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfDeployForm> selectDeployFormByDeployId(String deployId);
}
