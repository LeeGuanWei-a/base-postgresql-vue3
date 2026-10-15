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
 * @author yanbin
 * @description TODO (流程部署关联表单Service业务层处理)
 * @date 2023/11/3 10:39
 */
@RequiredArgsConstructor
@Service
public class WfDeployFormServiceImpl extends FlowServiceFactory implements IWfDeployFormService {

    private final WfDeployFormMapper baseMapper;

    private final WfFormMapper formMapper;

    /**
     * @return int
     * @description TODO (新增流程实例关联表单)
     * @params [deployForm]
     * @author yanbin
     * @date 2024/1/23 11:33
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
     * @return boolean
     * @description TODO (保存流程关联表单)
     * @params [deployId, bpmnModel, modelKey]
     * @author yanbin
     * @date 2024/2/27 14:16
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
     * @return com.basic.workflow.domain.vo.WfFormVo
     * @description TODO (查询流程部署关联表单信息)
     * @params [deployId, activityType]
     * @author yanbin
     * @date 2024/3/5 14:08
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfFormVo selectDeployFormByDeployIdAndType(String deployId, String activityType) {
        return formMapper.selectFormVoList(deployId, activityType);
    }

    /**
     * @return void
     * @description TODO (根据部署deployId删除流程部署表单信息)
     * @params [deployId]
     * @author yanbin
     * @date 2024/2/27 15:50
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void deleteDeployFormForDeployId(String deployId) {
        baseMapper.deleteDeployFormForDeployId(deployId);
    }

    /**
     * @return com.basic.workflow.domain.WfDeployForm
     * @description TODO (根据部署deployId和activityId查询节点表单)
     * @params [deployId, activityId]
     * @author yanbin
     * @date 2024/2/28 15:40
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
     * @return java.util.List<com.basic.workflow.domain.vo.WfDeployForm>
     * @description TODO (根据部署deployId查询部署表单)
     * @params [deployId]
     * @author yanbin
     * @date 2024/3/5 14:09
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
     * @return java.lang.Boolean
     * @description TODO (根据formId判断 form是否已经部署流程)
     * @params [formId]
     * @author yanbin
     * @date 2024/2/27 9:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Boolean selectExistDeployForFormId(String formId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("form_id", formId);
        List<WfDeployForm> wfDeployFormList = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(wfDeployFormList)) {
            return true;
        }
        return false;
    }

    /**
     * @return com.basic.workflow.domain.WfDeployForm
     * @description TODO (构建部署表单关联信息对象)
     * @params [deployId, node]
     * @author yanbin
     * @date 2024/2/27 9:02
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
