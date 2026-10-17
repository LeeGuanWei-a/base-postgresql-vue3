package com.basic.workflow.service.impl;

import com.basic.common.core.domain.AjaxResult;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.workflow.domain.bo.WfModelBo;
import com.basic.workflow.domain.vo.StartProcessInstanceVo;
import com.basic.workflow.domain.vo.WfModelVo;
import com.basic.workflow.service.IWfModelService;
import com.basic.workflow.service.wfModel.base.WfModelInfoServiceBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.flowable.engine.repository.Model;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description 流程模型管理服务实现，提供模型的分页查询、详情、保存、部署与删除等能力
 * @author yanbin
 * @date 2023/11/3 10:39
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class WfModelServiceImpl extends WfModelInfoServiceBase implements IWfModelService {
    /**
     * @return TableDataInfo<WfModelVo> 模型分页结果，无匹配时为空集合
     * @description 分页查询流程模型列表，委托模型查询服务按条件过滤
     * @params [modelBo, pageQuery] 查询条件，含模型名称、模型 KEY、分类；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public TableDataInfo<WfModelVo> list(WfModelBo modelBo, PageQuery pageQuery) {
        return wfModelQueryService.list(modelBo, pageQuery);
    }

    /**
     * @return List<WfModelVo> 可发起的模型列表，无匹配时为空列表
     * @description 查询可发起的流程模型列表，供流程发起页选择
     * @params [modelBo] 查询条件，含模型名称、模型 KEY、分类
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfModelVo> listModelLaunch(WfModelBo modelBo) {
        return wfQueryModelStartService.listModelLaunch(modelBo);
    }


    /**
     * @return List<WfModelVo> 模型列表，无匹配时为空列表
     * @description 不分页查询流程模型列表
     * @params [modelBo] 查询条件，含模型名称、模型 KEY、分类
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfModelVo> list(WfModelBo modelBo) {
        return wfModelQueryService.list(modelBo);
    }

    /**
     * @return TableDataInfo<WfModelVo> 历史版本分页结果，无匹配时为空集合
     * @description 分页查询同一模型 KEY 的历史版本，用于查看旧版本记录
     * @params [modelBo, pageQuery] 查询条件，含模型名称、模型 KEY、分类；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public TableDataInfo<WfModelVo> historyList(WfModelBo modelBo, PageQuery pageQuery) {
        return wfModelQueryService.historyList(modelBo, pageQuery);
    }

    /**
     * @return WfModelVo 模型详情，含 BPMN XML、描述与绑定表单等扩展信息
     * @description 查询流程模型详情，模型不存在时抛运行时异常，读取或解析 XML 失败时抛出受检异常
     * @params [modelId] 模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfModelVo getModel(String modelId) throws IOException, DocumentException {
        return wfModelQueryService.getModel(modelId);
    }

    /**
     * @return String BPMN 2.0 XML 文本
     * @description 读取模型里保存的 BPMN XML，供设计器打开与 XML 预览，读取或解析失败时抛出受检异常
     * @params [modelId] 模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public String queryBpmnXmlById(String modelId) throws IOException, DocumentException {
        return wfModelQueryXmlService.queryBpmnXmlById(modelId);
    }


    /**
     * @return Model Flowable 中新建的模型对象，含自动生成的 modelId 与版本号
     * @description 新增流程模型记录，只建空模型，BPMN XML 由后续保存接口写入
     * @params [modelBo] 模型参数，含模型名称、KEY、分类与描述
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Model insertModel(WfModelBo modelBo) {
        return wfModelSaveService.insertModel(modelBo);
    }

    /**
     * @return Object 固定返回 null，调用方忽略返回值
     * @description 修改模型的名称、分类与描述等扩展信息，不涉及 BPMN XML，事务内执行失败即回滚
     * @params [modelBo] 模型参数，需含 modelId，名称与分类会被覆盖
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object updateModel(WfModelBo modelBo) {
        return wfModelSaveService.updateModel(modelBo);
    }

    /**
     * @return Boolean 保存成功返回 true
     * @description 保存模型的 BPMN XML：解析、合规校验、规范化后写入模型资源，校验不通过时抛业务异常并回滚；入参要求另存新版本时会新建一条模型记录
     * @params [modelBo] 模型参数，含 modelId、BPMN XML、流程名与是否另存新版本
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveModel(WfModelBo modelBo) {
        return wfModelSaveXmlService.save(modelBo);
    }

    /**
     * @return Object 固定返回 null，调用方忽略返回值
     * @description 把指定历史版本复制为新版本并置为最新版本，模型已是最新版本时抛运行时异常而不重复创建记录
     * @params [modelId] 要设为最新的模型主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object latestModel(String modelId) {
        return wfModelActivateService.latestModel(modelId);
    }

    /**
     * @return Object 固定返回 null，调用方忽略返回值
     * @description 按模型主键批量删除模型，连带删除每个模型自己的那次部署，不影响同一 KEY 的其它版本
     * @params [ids] 模型主键集合，为空时不做任何处理
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object deleteByIds(Collection<String> ids) {
        return wfModelDeleteService.deleteByIds(ids);
    }

    /**
     * @return boolean 部署且表单关联保存成功返回 true
     * @description 把模型里的 BPMN XML 部署成流程定义并保存各节点表单的部署关联，部署前校验开始与结束节点，XML 为空或仍是旧版设计器 JSON 时直接拒绝
     * @params [wfModelBo] 模型参数，需含 modelId
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deployModel(WfModelBo wfModelBo) {
        return wfModelDeployService.deployModel(wfModelBo);
    }


    /**
     * @return AjaxResult 成功时携带流程实例 ID，流程定义缺失或已挂起时返回错误信息
     * @description 按流程定义发起流程实例，写入表单快照与业务拓展信息后自动流转首节点
     * @params [startProcessInstanceVo] 启动参数，含流程定义 KEY、发起人、表单数据与流程变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public AjaxResult startFormFlow(StartProcessInstanceVo startProcessInstanceVo) {
        return wfModelStartService.startFormFlow(startProcessInstanceVo);
    }


    /**
     * @description 挂起模型对应部署的流程定义，挂起后无法再发起新实例
     * @params [wfModelBo] 模型参数，需含 modelId
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional
    public void suspendedProcessDefinition(WfModelBo wfModelBo) {
        wfModelActivateService.suspendedProcessDefinition(wfModelBo);
    }

    /**
     * @description 激活模型对应部署的流程定义，激活后可再次发起实例
     * @params [wfModelBo] 模型参数，需含 modelId
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional
    public void activateProcessDefinition(WfModelBo wfModelBo) {
        wfModelActivateService.activateProcessDefinition(wfModelBo);
    }

    /**
     * @return Map<String, Object> 含 modelKey、deployId、modelId 的映射，模型尚未部署时返回 null
     * @description 按流程分类取最新版本的模型及其部署信息
     * @params [code] 流程分类编码
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Map<String, Object> getCategoryToModel(String code) {
        Map<String, Object> result = new HashMap<>();
        Model model = repositoryService.createModelQuery().modelCategory(code).latestVersion().singleResult();
        if (model.getDeploymentId() == null) {
            return null;
        }
        result.put("modelKey", model.getKey());
        result.put("deployId", model.getDeploymentId());
        result.put("modelId", model.getId());
        return result;
    }

}
