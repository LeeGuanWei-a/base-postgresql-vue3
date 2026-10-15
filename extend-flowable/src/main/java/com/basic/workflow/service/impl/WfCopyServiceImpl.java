package com.basic.workflow.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.basic.common.utils.DateUtils;
import com.basic.common.utils.StringUtils;
import com.basic.system.service.ISysUserService;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.WfCategory;
import com.basic.workflow.domain.WfCopy;
import com.basic.workflow.domain.bo.WfCopyBo;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.vo.WfCategoryVo;
import com.basic.workflow.domain.vo.WfCopyVo;
import com.basic.workflow.mapper.WfCopyMapper;
import com.basic.workflow.service.IWfCategoryService;
import com.basic.workflow.service.IWfCopyService;
import com.basic.workflow.service.IWfExtendProcinstService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (流程抄送Service业务层处理)
 * @date 2023/11/3 10:39
 */
@RequiredArgsConstructor
@Service
public class WfCopyServiceImpl implements IWfCopyService {

    private final WfCopyMapper baseMapper;
    @Autowired
    private final IWfExtendProcinstService extendProcinstService;
    private final HistoryService historyService;
    private final RepositoryService repositoryService;
    private final IWfCategoryService wfCategoryService;
    private final ISysUserService sysUserService;

    /**
     * @return com.basic.workflow.domain.vo.WfCopyVo
     * @description TODO (查询流程抄送)
     * @params [copyId]
     * @author yanbin
     * @date 2024/1/23 11:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfCopyVo queryById(Long copyId) {
        return baseMapper.selectVoById(copyId);
    }

    /**
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @description TODO (查询流程抄送列表)
     * @params [bo]
     * @author yanbin
     * @date 2024/1/23 11:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<Map<String, Object>> selectPageList(WfCopyBo bo) {
        bo.setStatus("0");
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> list = baseMapper.selectPageList(bo);
        for (Map<String, Object> map : list) {
            map.put("originator_name", sysUserService.selectNickNameById(Long.valueOf(map.get("originator_id").toString())));
            //获取分类绑定的路由地址
            if (StringUtils.isNotBlank(map.get("category_id").toString())) {
                WfCategoryVo wfCategory = wfCategoryService.getCategoryToUrl(map.get("category_id").toString());
                if (wfCategory != null) {
                    map.put("url", wfCategory.getUrl());
                }
            }
            if (StringUtils.isNotBlank(map.get("instance_id").toString())) {
                //流程业务关联查询
                ExtendProcinst extendProcinst = extendProcinstService.selectProcInstanId(map.get("instance_id").toString());
                if (extendProcinst != null) {
                    //业务系统类型
                    map.put("appSn", extendProcinst.getTenantId());
                }
            }
            result.add(map);
        }
        return result;
    }


    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfCopyVo>
     * @description TODO (查询流程抄送列表)
     * @params [bo]
     * @author yanbin
     * @date 2024/1/23 11:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public List<WfCopyVo> selectList(WfCopyBo bo) {
        // 抄送列表必须限定「当前登录用户」：
        // WfCopyMapper.selectCopyList 里的过滤是 <if test="userId !=null and userId !=''">
        // 条件式的 —— userId 为空时过滤整段不拼接，结果就是所有人都能看到所有人的抄送记录。
        if (bo.getUserId() == null || bo.getUserId().isEmpty()) {
            bo.setUserId(String.valueOf(com.basic.common.utils.SecurityUtils.getUserId()));
        }
        return baseMapper.selectCopyList(bo);
    }

    /**
     * @return com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.basic.workflow.domain.WfCopy>
     * @description TODO (构建查询)
     * @params [bo]
     * @author yanbin
     * @date 2024/1/23 11:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private LambdaQueryWrapper<WfCopy> buildQueryWrapper(WfCopyBo bo) {
        bo.setStatus("0");
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<WfCopy> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, WfCopy::getUserId, bo.getUserId());
        lqw.eq(bo.getStatus() != null, WfCopy::getStatus, bo.getStatus());
        lqw.like(StringUtils.isNotBlank(bo.getProcessName()), WfCopy::getProcessName, bo.getProcessName());
        lqw.like(StringUtils.isNotBlank(bo.getOriginatorName()), WfCopy::getOriginatorName, bo.getOriginatorName());
        return lqw;
    }

    /**
     * @return java.lang.Boolean
     * @description TODO (抄送)
     * @params [taskBo]
     * @author yanbin
     * @date 2024/1/23 11:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public Boolean makeCopy(WfTaskBo taskBo) {
        if (StringUtils.isBlank(taskBo.getCopyUserIds())) {
            // 若抄送用户为空，则不需要处理，返回成功
            return true;
        }
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(taskBo.getProcInsId()).singleResult();
        // 流程定义信息
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(historicProcessInstance.getProcessDefinitionId()).singleResult();
        String[] ids = taskBo.getCopyUserIds().split(",");
        List<WfCopy> copyList = new ArrayList<>(ids.length);
        String originatorName = taskBo.getCurrentUserName();
        String title = historicProcessInstance.getProcessDefinitionName();
        if (taskBo.getTaskName() != null) {
            title = "-" + taskBo.getTaskName();
        }
        for (String id : ids) {
            WfCopy copy = new WfCopy();
            copy.setTitle(title);
            copy.setProcessId(historicProcessInstance.getProcessDefinitionId());
            copy.setProcessName(historicProcessInstance.getProcessDefinitionName());
            copy.setDeploymentId(historicProcessInstance.getDeploymentId());
            copy.setInstanceId(taskBo.getProcInsId());
            copy.setTaskId(taskBo.getTaskId());
            copy.setUserId(id);
            copy.setOriginatorId(historicProcessInstance.getStartUserId());
            copy.setOriginatorName(originatorName);
            copy.setStatus("0");
            copy.setCategoryId(pd.getCategory());
            copyList.add(copy);
        }
        return baseMapper.insertBatch(copyList);
    }

    /**
     * @return void
     * @description TODO (标记抄送为已阅)
     * @params [id]
     * @author yanbin
     * @date 2024/1/23 11:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void readCopy(Long id) {
        WfCopy copy = baseMapper.selectById(id);
        copy.setStatus("1");
        baseMapper.updateById(copy);
    }

    /**
     * @return void
     * @description TODO (根据实例id删除历史抄送)
     * @params [procInsIds]
     * @author yanbin
     * @date 2024/1/17 17:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void delProcessCopyInfo(List<String> procInsIds) {
        List<Long> ids = new ArrayList<>();
        for (String procInsId : procInsIds) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("instance_id", procInsId);
            List<WfCopy> wfCopies = baseMapper.selectList(queryWrapper);
            if (wfCopies.size() > 0) {
                wfCopies.forEach(wfCopy -> {
                    ids.add(wfCopy.getCopyId());
                });
            }
        }
        if (ids.size() > 0) {
            //物理删除，流程删除，抄送也删除
            baseMapper.deleteBatchIds(ids);
        }
    }
}
