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
 * @description 流程抄送业务实现，负责抄送记录的查询、新增、删除及抄送对象信息补全
 * @author yanbin
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
     * @return WfCopyVo 抄送记录视图对象，记录不存在时返回 null
     * @description 按主键查询单条流程抄送记录并转换成视图对象
     * @params [copyId] 抄送记录主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public WfCopyVo queryById(Long copyId) {
        return baseMapper.selectVoById(copyId);
    }

    /**
     * @return List<Map<String, Object>> 补全发起人昵称、分类路由地址与业务系统类型后的抄送列表
     * @description 查询未读流程抄送列表，并逐行补全发起人昵称、分类绑定路由与业务系统类型
     * @params [bo] 抄送查询条件，固定按状态 0 过滤
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return List<WfCopyVo> 匹配查询条件的抄送视图对象列表
     * @description 查询抄送列表，用户ID为空时强制取当前登录用户，避免查到他人抄送
     * @params [bo] 抄送查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return LambdaQueryWrapper<WfCopy> 拼好状态、用户、流程名称与发起人模糊条件的查询构造器
     * @description 按抄送状态、用户ID、流程名称与发起人名称拼装 Lambda 查询条件
     * @params [bo] 抄送查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return Boolean 批量插入是否成功，抄送用户为空时直接返回 true
     * @description 按抄送用户ID逐个构造未读抄送记录并批量插入，抄送用户为空时不做处理
     * @params [taskBo] 任务参数，含流程实例ID、抄送用户ID、任务名与当前处理人
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @description 按主键取出抄送记录，把状态置为已阅后更新
     * @params [id] 抄送记录主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Override
    public void readCopy(Long id) {
        WfCopy copy = baseMapper.selectById(id);
        copy.setStatus("1");
        baseMapper.updateById(copy);
    }

    /**
     * @description 按流程实例ID集合查出对应抄送记录并物理删除，流程删除时同步清理抄送
     * @params [procInsIds] 流程实例ID集合
     * @author liguanwei
     * @date 2026/10/17 20:52
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
