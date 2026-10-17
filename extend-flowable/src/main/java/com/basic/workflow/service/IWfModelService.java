package com.basic.workflow.service;


import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.domain.vo.StartProcessInstanceVo;
import com.basic.workflow.domain.vo.WfModelVo;
import org.dom4j.DocumentException;
import org.flowable.engine.repository.Model;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @description 工作流模型管理服务接口，提供流程模型的查询、编辑、部署与版本管理等操作
 * @author yanbin
 * @date 2023/11/3 10:37
 */
public interface IWfModelService {
    /**
     * @return TableDataInfo<WfModelVo> 流程模型分页列表
     * @description 按查询条件分页查询流程模型列表
     * @params [modelBo, pageQuery] 流程模型查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<WfModelVo> list(WfModelBo modelBo, PageQuery pageQuery);

    /**
     * @return List<WfModelVo> 流程模型列表，不分页
     * @description 按查询条件查询流程模型列表，不分页
     * @params [modelBo] 流程模型查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfModelVo> list(WfModelBo modelBo);


    /**
     * @return TableDataInfo<WfModelVo> 历史流程模型分页列表
     * @description 按查询条件分页查询流程模型的历史版本列表
     * @params [modelBo, pageQuery] 流程模型查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<WfModelVo> historyList(WfModelBo modelBo, PageQuery pageQuery);

    /**
     * @return WfModelVo 流程模型详情
     * @description 按模型主键查询流程模型详情信息
     * @params [modelId] 模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfModelVo getModel(String modelId) throws IOException, DocumentException;

    /**
     * @return String 模型对应的 BPMN XML 内容
     * @description 按模型主键查询该模型的 BPMN XML
     * @params [modelId] 模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    String queryBpmnXmlById(String modelId) throws IOException, DocumentException;

    /**
     * @return Model Flowable 中新建的流程模型记录
     * @description 新增一条流程模型记录
     * @params [modelBo] 流程模型业务对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Model insertModel(WfModelBo modelBo);

    /**
     * @return Object 修改结果
     * @description 修改流程模型的基础信息与 BPMN 内容
     * @params [modelBo] 流程模型业务对象
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Object updateModel(WfModelBo modelBo);

    /**
     * @return Boolean 保存成功返回 true
     * @description 保存流程模型信息与 BPMN 设计内容
     * @params [modelBo] 流程模型业务对象，含模型主键与 BPMN XML
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Boolean saveModel(WfModelBo modelBo);

    /**
     * @return Object 设为最新后的模型信息
     * @description 把指定流程模型设为该流程的最新版本
     * @params [modelId] 模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Object latestModel(String modelId);

    /**
     * @return Object 删除结果
     * @description 按模型主键批量删除流程模型
     * @params [ids] 要删除的模型主键集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Object deleteByIds(Collection<String> ids);

    /**
     * @return boolean 部署成功返回 true
     * @description 把流程模型部署为流程定义
     * @params [modelId] 流程模型业务对象，含模型主键与分类
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean deployModel(WfModelBo modelId);
    /**
     * @return AjaxResult 流程实例启动结果
     * @description 按流程模型 key 发起流程实例
     * @params [startProcessInstanceVo] 流程启动入参，含流程定义 key 与业务变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    AjaxResult startFormFlow(StartProcessInstanceVo startProcessInstanceVo);

    /**
     * @description 挂起（停用）流程模型对应的流程定义
     * @params [wfModelBo] 流程模型业务对象，含模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void suspendedProcessDefinition(WfModelBo wfModelBo);

    /**
     * @description 激活流程模型对应的已挂起流程定义
     * @params [wfModelBo] 流程模型业务对象，含模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void activateProcessDefinition(WfModelBo wfModelBo);

    /**
     * @return List<WfModelVo> 已部署、可发起的流程模型列表
     * @description 查询流程启动列表，只返回已部署的流程
     * @params [modelBo] 流程模型查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfModelVo> listModelLaunch(WfModelBo modelBo);

    /**
     * @return Map<String, Object> 该分类下已部署流程的信息集合
     * @description 按流程分类编码查询其下已部署的流程信息
     * @params [code] 流程分类编码
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Map<String, Object> getCategoryToModel(String code);
}
