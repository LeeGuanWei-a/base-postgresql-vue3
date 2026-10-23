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
 * @description 全部任务查询服务，按流程状态分页查询运行中与已完成的流程任务，并支持按用户筛选参与的任务
 * @author yanbin
 * @date 2024/03/04
 */
@Service
public class WfAllTaskService extends WfProcessInfoBase {
    /**
     * @return List<WfTaskVo> 全部任务列表，按流程状态区分运行中与已完成
     * @description 分页查询所有任务，流程状态为 running 时查运行中任务，否则查已完成任务
     * @params [processQuery, pageQuery] 查询条件，含流程名、状态与处理人等；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectProcessAllTask(ProcessQuery processQuery, PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
        List<WfTaskVo> list = new ArrayList<>();
        // 查询待审核的任务
        if (StringUtils.isNotBlank(processQuery.getProcessStatus()) && processQuery.getProcessStatus().equals("running")) {
            list = wfFlowableProcessMapper.selectProcessAllRuTask(processQuery);
        }
        // 查询已完结的任务
        else {
            list = wfFlowableProcessMapper.selectProcessAllHiTask(processQuery);
        }
        return list;
    }

    /**
     * @return List<WfTaskVo> 该用户参与的任务列表，按流程状态区分运行中与已完成
     * @description 非管理员只查自己作为候选人的任务，再按流程状态分页查询运行中或已完成任务
     * @params [processQuery, pageQuery] 查询条件，含流程名、状态与候选人等；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectProcessTaskByUser(ProcessQuery processQuery, PageQuery pageQuery) {
        Long userId = SecurityUtils.getUserId();
        SysUser user = sysUserService.selectUserById(userId);
        if(!user.isAdmin()) {
            processQuery.setCandidateUser(userId.toString());
        }
        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
        List<WfTaskVo> list = new ArrayList<>();

        // 查询待审核的任务
        if (StringUtils.isNotBlank(processQuery.getProcessStatus()) && processQuery.getProcessStatus().equals("running")) {
            list = wfFlowableProcessMapper.selectProcessAllRuTask(processQuery);
        }
        // 查询已完结的任务
        else {
            list = wfFlowableProcessMapper.selectProcessAllHiTask(processQuery);
        }
        return list;
    }
}
