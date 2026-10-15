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
 * @author yanbin
 * @description TODO (Flowable已办任务查询)
 * @date 2024/03/04
 */
@Service
public class WfFinishTaskService extends WfProcessInfoBase {
    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询已办任务列表) 分页
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/3/4 10:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectPageFinishedProcessList(ProcessQuery processQuery, PageQuery pageQuery) {
        this.fillCurrentUser(processQuery);
        PageUtils.startPage();
        List<WfTaskVo> wfTaskVoList = wfFlowableProcessMapper.selectFinishTaskList(processQuery);
        return wfTaskVoList;
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询已办任务列表) 不分页
     * @params [processQuery]
     * @author yanbin
     * @date 2024/3/4 10:35
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectFinishedProcessList(ProcessQuery processQuery) {
        this.fillCurrentUser(processQuery);
        List<WfTaskVo> wfTaskVoList = wfFlowableProcessMapper.selectFinishTaskList(processQuery);
        return wfTaskVoList;
    }

    /**
     * 填充「已办」查询的当前登录用户条件。
     *
     * <p>mapper 的 selectFinishTaskList 里过滤是这样写的 ——</p>
     * <pre>
     *   &lt;if test="userId!=null and userId!=''"&gt;
     *     and t.assignee_ = #{userId}
     *   &lt;/if&gt;
     * </pre>
     * <p>一旦 userId 为 null，<b>过滤整段不拼接</b>，
     * "已办"列表就会返回所有人的已办任务。这里补上当前登录用户。</p>
     *
     * <p>注意：只填 userId（办理人），不要填 startUserId —— 已办看的是"谁办的"，
     * 不是"谁发起的"。</p>
     */
    private void fillCurrentUser(ProcessQuery processQuery) {
        String userId = processQuery.getUserId();
        if (userId == null || userId.isEmpty()) {
            processQuery.setUserId(String.valueOf(com.basic.common.utils.SecurityUtils.getUserId()));
        }
    }
}
