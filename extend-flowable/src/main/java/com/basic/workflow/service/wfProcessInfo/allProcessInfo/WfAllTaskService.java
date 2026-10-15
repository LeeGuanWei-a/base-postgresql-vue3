package com.basic.workflow.service.wfProcessInfo.allProcessInfo;

import com.basic.common.core.domain.entity.SysUser;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.utils.SecurityUtils;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import com.github.pagehelper.PageHelper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author yanbin
 * @description TODO (Flowable获取所有任务信息)
 * @date 2024/03/04
 */
@Service
public class WfAllTaskService extends WfProcessInfoBase {
    /**
     * @return TableDataInfo<WfTaskVo>
     * @description TODO (获取所有任务信息)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/3/4 10:56
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectProcessAllTask(ProcessQuery processQuery, PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
        List<WfTaskVo> list = new ArrayList<>();
        //TODO 查询待审核的任务
        if (StringUtils.isNotBlank(processQuery.getProcessStatus()) && processQuery.getProcessStatus().equals("running")) {
            list = wfFlowableProcessMapper.selectProcessAllRuTask(processQuery);
        }
        //TODO 查询已完结的任务
        else {
            list = wfFlowableProcessMapper.selectProcessAllHiTask(processQuery);
        }
        return list;
    }

    public List<WfTaskVo> selectProcessTaskByUser(ProcessQuery processQuery, PageQuery pageQuery) {
        Long userId = SecurityUtils.getUserId();
        SysUser user = sysUserService.selectUserById(userId);
        if(!user.isAdmin()) {
            processQuery.setCandidateUser(userId.toString());
        }
        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
        List<WfTaskVo> list = new ArrayList<>();

        //TODO 查询待审核的任务
        if (StringUtils.isNotBlank(processQuery.getProcessStatus()) && processQuery.getProcessStatus().equals("running")) {
            list = wfFlowableProcessMapper.selectProcessAllRuTask(processQuery);
        }
        //TODO 查询已完结的任务
        else {
            list = wfFlowableProcessMapper.selectProcessAllHiTask(processQuery);
        }
        return list;
    }
}
