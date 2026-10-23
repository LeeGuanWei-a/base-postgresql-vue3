package com.basic.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.basic.common.exception.ServiceException;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.factory.FlowServiceFactory;
import com.basic.flowable.utils.ModelUtils;
import com.basic.workflow.domain.WfDeployForm;
import com.basic.workflow.domain.WfForm;
import com.basic.workflow.domain.vo.WfFormVo;
import com.basic.workflow.mapper.WfDeployFormMapper;
import com.basic.workflow.mapper.WfFormMapper;
import com.basic.workflow.service.IWfDeployFormService;
import lombok.RequiredArgsConstructor;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @description 流程部署关联表单业务实现类，维护部署与表单的绑定关系并提供按部署查询表单的能力
 * @author yanbin
 * @date 2023/11/3 10:39
 */
@RequiredArgsConstructor
@Service
public class WfDeployFormServiceImpl extends FlowServiceFactory implements IWfDeployFormService {

    private final WfDeployFormMapper baseMapper;

    private final WfFormMapper formMapper;

    /**
     * @return int 新增关联记录的影响行数
     * @description 先删除该部署原有的表单关联，再新增部署与表单的关联记录
     * @params [deployForm] 部署表单关联对象，携带部署 ID 与表单信息
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWfDeployForm(WfDeployForm deployForm) {
        // 删除部署流程和表单的关联关系
        baseMapper.delete(new LambdaQueryWrapper<WfDeployForm>().eq(WfDeployForm::getDeployId, deployForm.getDeployId()));
        // 新增部署流程和表单关系
        return baseMapper.insert(deployForm);
    }

    /**
     * @return boolean 保存成功后返回 true
     * @description 解析 BPMN 模型中开始节点与全部用户任务节点绑定的表单，批量保存为部署表单关联
     * @params [deployId, bpmnModel, modelKey] 部署 ID；流程 BPMN 模型；模型标识
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveInternalDeployForm(String deployId, BpmnModel bpmnModel, String modelKey) {
        List<WfDeployForm> deployFormList = new ArrayList<>();
        // 获取开始节点
        StartEvent startEvent = ModelUtils.getStartEvent(bpmnModel);
        if (ObjectUtil.isNull(startEvent)) {
            throw new RuntimeException("开始节点不存在，请检查流程设计是否有误！");
        }
        // 保存开始节点表单信息
        WfDeployForm startDeployForm = buildDeployForm(deployId, startEvent);
        if (ObjectUtil.isNotNull(startDeployForm)) {
            startDeployForm.setActivityType("startEvent");
            startDeployForm.setModelKey(modelKey);
            deployFormList.add(startDeployForm);
        }
        //保存用户节点表单信息
        Collection<UserTask> userTasks = ModelUtils.getAllUserTaskEvent(bpmnModel);
        if (CollUtil.isNotEmpty(userTasks)) {
            for (UserTask userTask : userTasks) {
                WfDeployForm userTaskDeployForm = buildDeployForm(deployId, userTask);
                if (ObjectUtil.isNotNull(userTaskDeployForm)) {
                    userTaskDeployForm.setActivityType("userTask");
                    userTaskDeployForm.setModelKey(modelKey);
                    deployFormList.add(userTaskDeployForm);
                }
            }
        }
        // 批量新增部署流程和表单关联信息
        baseMapper.insertBatch(deployFormList);
        return true;
    }

    /**
     * @return WfFormVo 部署关联的表单信息
     * @description 按部署 ID 与节点类型查询流程部署关联的表单信息
     * @params [deployId, activityType] 部署 ID；节点类型，用于区分不同节点绑定的表单
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfFormVo selectDeployFormByDeployIdAndType(String deployId, String activityType) {
        return formMapper.selectFormVoList(deployId, activityType);
    }

    /**
     * @description 按部署 ID 删除该部署下的全部部署表单关联
     * @params [deployId] 部署 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void deleteDeployFormForDeployId(String deployId) {
        baseMapper.deleteDeployFormForDeployId(deployId);
    }

    /**
     * @return WfDeployForm 该节点绑定的部署表单，未绑定时返回 null
     * @description 按部署 ID 与节点 ID 查询该节点绑定的部署表单
     * @params [deployId, activityId] 部署 ID；节点（活动）ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfDeployForm selectFormByDeployIdAndActId(String deployId, String activityId) {
        QueryWrapper<WfDeployForm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deploy_id", deployId);
        queryWrapper.eq("activity_id", activityId);
        WfDeployForm wfDeployForm = baseMapper.selectOne(queryWrapper);
        return wfDeployForm;
    }

    /**
     * @return List<WfDeployForm> 该部署下的全部部署表单关联，无数据时为空列表
     * @description 按部署 ID 查询该部署下的全部部署表单关联
     * @params [deployId] 部署 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfDeployForm> selectDeployFormByDeployId(String deployId) {
        QueryWrapper<WfDeployForm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deploy_id", deployId);
        List<WfDeployForm> list = baseMapper.selectList(queryWrapper);
        return list;
    }

    /**
     * @return Boolean 该表单已被部署引用返回 true，否则返回 false
     * @description 按表单 ID 判断该表单是否已被某个流程部署引用
     * @params [formId] 表单 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Boolean selectExistDeployForFormId(String formId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("form_id", formId);
        List<WfDeployForm> wfDeployFormList = baseMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(wfDeployFormList)) {
            return true;
        }
        return false;
    }

    /**
     * @return WfDeployForm 组装好的部署表单关联对象，节点未配置表单时返回 null
     * @description 按节点配置的表单标识查出表单，组装成部署表单关联对象；表单不存在时抛出业务异常
     * @params [deployId, node] 部署 ID；配置了表单的流程节点
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private WfDeployForm buildDeployForm(String deployId, FlowNode node) {
        String formId = ModelUtils.getFormKey(node);
        if (StringUtils.isEmpty(formId)) {
            return null;
        }
        WfForm wfForm = formMapper.selectById(formId);
        if (ObjectUtil.isNull(wfForm)) {
            throw new ServiceException("表单信息查询错误");
        }
        WfDeployForm deployForm = new WfDeployForm();
        deployForm.setDeployId(deployId);
        deployForm.setFormId(formId);
        deployForm.setActivityId(node.getId());
        deployForm.setFormName(wfForm.getFormName());
        deployForm.setActivityName(node.getName());
        deployForm.setContent(wfForm.getContent());
        return deployForm;
    }
}
