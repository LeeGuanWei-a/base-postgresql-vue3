package com.basic.workflow.service.wfProcessInfo.finishTask;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.utils.PageUtils;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.flowable.utils.ProcessUtils;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 已办任务查询服务，按当前登录用户查询已办结的历史任务列表
 * @author yanbin
 * @date 2024/03/04
 */
@Service
public class WfFinishTaskService extends WfProcessInfoBase {
    /**
     * @return List<WfTaskVo> 当前用户的已办任务列表
     * @description 补齐当前登录用户条件后分页查询已办任务，返回当前页数据由调用方包成分页结果
     * @params [processQuery, pageQuery] 流程查询条件，userId 为空时自动填当前登录用户；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectPageFinishedProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        this.fillCurrentUser(processQuery);
        PageUtils.startPage();
        List<WfTaskVo> wfTaskVoList = wfFlowableProcessMapper.selectFinishTaskList(processQuery);
        return wfTaskVoList;
    }

    /**
     * @return List<WfTaskVo> 当前用户的已办任务列表
     * @description 补齐当前登录用户条件后不分页查询已办任务
     * @params [processQuery] 流程查询条件，userId 为空时自动填当前登录用户
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectFinishedProcessList(ProcessQuery processQuery) {
        this.fillCurrentUser(processQuery);
        List<WfTaskVo> wfTaskVoList = wfFlowableProcessMapper.selectFinishTaskList(processQuery);
        return wfTaskVoList;
    }

    /**
     * @description 给已办查询补上当前登录用户条件，避免 userId 为空时过滤条件整段不拼接而查出所有人的已办任务
     * @params [processQuery] 流程查询条件，仅回填 userId（办理人），不动 startUserId
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private void fillCurrentUser(ProcessQuery processQuery) {
        String userId = processQuery.getUserId();
        if (userId == null || userId.isEmpty()) {
            processQuery.setUserId(String.valueOf(com.basic.common.utils.SecurityUtils.getUserId()));
        }
    }
}
