package com.basic.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.flowable.utils.ProcessUtils;
import com.basic.workflow.domain.WfDeployForm;
import com.basic.workflow.domain.vo.WfDeployVo;
import com.basic.workflow.mapper.WfDeployFormMapper;
import com.basic.workflow.service.IWfDeployService;
import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 流程部署管理的业务实现，查询流程定义与部署信息、切换部署挂起状态并删除部署
 * @author yanbin
 * @date 2023/11/3 10:39
 */
@RequiredArgsConstructor
@Service
public class WfDeployServiceImpl implements IWfDeployService {

    private final RepositoryService repositoryService;
    private final WfDeployFormMapper deployFormMapper;

    /**
     * @return TableDataInfo<WfDeployVo> 流程部署分页结果，无数据时返回空分页
     * @description 分页查询最新版本的流程定义，拼上部署信息后按页返回
     * @params [processQuery, pageQuery] 流程定义查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public TableDataInfo<WfDeployVo> queryPageList(ProcessQuery processQuery, PageQuery pageQuery) {
        // 流程定义列表数据查询
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .orderByProcessDefinitionKey()
                .asc();
        // 构建搜索条件
        ProcessUtils.buildProcessSearch(processDefinitionQuery, processQuery);
        long pageTotal = processDefinitionQuery.count();
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<ProcessDefinition> definitionList = processDefinitionQuery.listPage(offset, pageQuery.getPageSize());

        List<WfDeployVo> deployVoList = new ArrayList<>(definitionList.size());
        for (ProcessDefinition processDefinition : definitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            WfDeployVo vo = new WfDeployVo();
            vo.setDefinitionId(processDefinition.getId());
            vo.setProcessKey(processDefinition.getKey());
            vo.setProcessName(processDefinition.getName());
            vo.setVersion(processDefinition.getVersion());
            vo.setCategory(processDefinition.getCategory());
            vo.setDeploymentId(processDefinition.getDeploymentId());
            vo.setSuspended(processDefinition.isSuspended());
            // 流程部署信息
            vo.setCategory(deployment.getCategory());
            vo.setDeploymentTime(deployment.getDeploymentTime());
            deployVoList.add(vo);
        }
        Page<WfDeployVo> page = new Page<>();
        page.setRecords(deployVoList);
        page.setTotal(pageTotal);
        return TableDataInfo.build(page);
    }

    /**
     * @return TableDataInfo<WfDeployVo> 指定流程标识的部署版本分页结果，无数据时返回空分页
     * @description 按流程标识分页查询其全部部署版本，版本号倒序
     * @params [processKey, pageQuery] 流程标识；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public TableDataInfo<WfDeployVo> queryPublishList(String processKey, PageQuery pageQuery) {
        // 创建查询条件
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .orderByProcessDefinitionVersion()
                .desc();
        long pageTotal = processDefinitionQuery.count();
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }
        // 根据查询条件，查询所有版本
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery
                .listPage(offset, pageQuery.getPageSize());
        List<WfDeployVo> deployVoList = processDefinitionList.stream().map(item -> {
            WfDeployVo vo = new WfDeployVo();
            vo.setDefinitionId(item.getId());
            vo.setProcessKey(item.getKey());
            vo.setProcessName(item.getName());
            vo.setVersion(item.getVersion());
            vo.setCategory(item.getCategory());
            vo.setDeploymentId(item.getDeploymentId());
            vo.setSuspended(item.isSuspended());
            return vo;
        }).collect(Collectors.toList());
        Page<WfDeployVo> page = new Page<>();
        page.setRecords(deployVoList);
        page.setTotal(pageTotal);
        return TableDataInfo.build(page);
    }

    /**
     * @description 按状态值激活或挂起指定流程定义，同时联动其流程实例
     * @params [definitionId, state] 流程定义 ID；目标状态，ACTIVE 为激活、SUSPENDED 为挂起，其它值不做处理
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void updateState(String definitionId, String state) {
        if (SuspensionState.ACTIVE.toString().equals(state)) {
            // 激活
            repositoryService.activateProcessDefinitionById(definitionId, true, null);
        } else if (SuspensionState.SUSPENDED.toString().equals(state)) {
            // 挂起
            repositoryService.suspendProcessDefinitionById(definitionId, true, null);
        }
    }

    /**
     * @description 级联删除指定部署及其运行中实例，并清掉这些部署关联的表单绑定记录
     * @params [deployIds] 待删除的部署 ID 集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<String> deployIds) {
        for (String deployId : deployIds) {
            repositoryService.deleteDeployment(deployId, true);
            deployFormMapper.delete(new LambdaQueryWrapper<WfDeployForm>().eq(WfDeployForm::getDeployId, deployId));
        }
    }
}
