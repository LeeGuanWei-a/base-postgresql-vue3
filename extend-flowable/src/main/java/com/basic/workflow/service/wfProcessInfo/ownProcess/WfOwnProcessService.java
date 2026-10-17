package com.basic.workflow.service.wfProcessInfo.ownProcess;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.utils.DateUtils;
import com.basic.common.utils.PageUtils;
import com.basic.common.utils.StringUtils;
import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.common.enums.ProcessStatus;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.flowable.utils.ProcessUtils;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description 我发起的流程查询服务，分页返回当前登录用户发起的流程实例及其办理状态
 * @author yanbin
 * @date 2024/03/04
 */
@Service
public class WfOwnProcessService extends WfProcessInfoBase {
    /**
     * @return List<WfTaskVo> 我发起的流程分页数据
     * @description 分页查询当前登录用户发起的流程列表
     * @params [processQuery, pageQuery] 流程查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectPageOwnProcessList(
            ProcessQuery processQuery, PageQuery pageQuery) {
        PageUtils.startPage();
        List<WfTaskVo> wfTaskVoList = wfFlowableProcessMapper.selectOwnProcessList(processQuery);
        return wfTaskVoList;
    }

    /**
     * @return List<WfTaskVo> 我发起的流程数据
     * @description 查询当前登录用户发起的流程列表，不分页
     * @params [processQuery] 流程查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectOwnProcessList(ProcessQuery processQuery) {
        List<WfTaskVo> wfTaskVoList = wfFlowableProcessMapper.selectOwnProcessList(processQuery);
        return wfTaskVoList;
    }
}
