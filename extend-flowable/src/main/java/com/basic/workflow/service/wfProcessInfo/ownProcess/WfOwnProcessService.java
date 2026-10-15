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
 * @author yanbin
 * @description TODO (Flowable已发任务查询)
 * @date 2024/03/04
 */
@Service
public class WfOwnProcessService extends WfProcessInfoBase {
    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO (查询我的流程列表)已发
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/3/4 10:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectPageOwnProcessList(
            ProcessQuery processQuery, PageQuery pageQuery) {
        PageUtils.startPage();
        List<WfTaskVo> wfTaskVoList = wfFlowableProcessMapper.selectOwnProcessList(processQuery);
        return wfTaskVoList;
    }

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfTaskVo>
     * @description TODO  (查询我的流程列表)已发
     * @params [processQuery]
     * @author yanbin
     * @date 2024/3/4 10:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public List<WfTaskVo> selectOwnProcessList(ProcessQuery processQuery) {
        List<WfTaskVo> wfTaskVoList = wfFlowableProcessMapper.selectOwnProcessList(processQuery);
        return wfTaskVoList;
    }
}
