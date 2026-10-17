package com.basic.workflow.service.wfProcessInfo.allProcessInfo;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.common.utils.DateUtils;
import com.basic.flowable.common.constant.ProcessConstants;
import com.basic.flowable.common.enums.ProcessStatus;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.flowable.utils.ProcessUtils;
import com.basic.workflow.domain.vo.WfTaskVo;
import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description 全部流程实例查询服务，补齐发起人、状态与耗时等展示信息后分页返回流程列表
 * @author yanbin
 * @date 2024/03/04
 */
@Service
public class WfAllProcessService extends WfProcessInfoBase {
    /**
     * @return TableDataInfo<WfTaskVo> 流程实例分页列表
     * @description 查询全部历史流程实例，逐个补齐发起人昵称、流程状态、耗时、流程定义名与业务拓展表信息后内存分页
     * @params [processQuery, pageQuery] 流程实例查询条件；分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public TableDataInfo<WfTaskVo> selectProcessPrcInstance(ProcessQuery processQuery, PageQuery pageQuery) {
        //构建搜索
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .includeProcessVariables()
                .orderByProcessInstanceStartTime().desc();
        //构建查询条件
        ProcessUtils.buildProcessSearch(historicProcessInstanceQuery, processQuery);
        //执行查询
        List<HistoricProcessInstance> historicProcessInstanceList = historicProcessInstanceQuery.list();
        List<WfTaskVo> taskVoList = new ArrayList<>();
        for (HistoricProcessInstance hisIns : historicProcessInstanceList) {
            WfTaskVo taskVo = new WfTaskVo();
            //流程发起人id
            taskVo.setStartUserId(hisIns.getStartUserId());
            //流程发起人name（发起人 id 可能为空或非数字，避免 Long.valueOf 抛异常导致整个列表 500）
            String startUserId = hisIns.getStartUserId();
            taskVo.setStartUserName(
                    (startUserId == null || startUserId.isEmpty())
                            ? null
                            : sysUserService.selectNickNameById(Convert.toLong(startUserId, 0L)));
            HistoricVariableInstance processStatusVariable =
                    historyService
                            .createHistoricVariableInstanceQuery()
                            .processInstanceId(hisIns.getId())
                            .variableName(ProcessConstants.PROCESS_STATUS_KEY)
                            .singleResult();
            String processStatus = null;
            if (ObjectUtil.isNotNull(processStatusVariable)) {
                processStatus = Convert.toStr(processStatusVariable.getValue());
            }
            // 兼容旧流程
            if (processStatus == null) {
                processStatus =
                        ObjectUtil.isNull(hisIns.getEndTime())
                                ? ProcessStatus.RUNNING.getStatus()
                                : ProcessStatus.COMPLETED.getStatus();
            }
            //流程状态
            taskVo.setProcessStatus(processStatus);
            //流程发起时间
            taskVo.setCreateTime(hisIns.getStartTime());
            //流程完结时间
            taskVo.setFinishTime(hisIns.getEndTime());
            //流程实例id
            taskVo.setProcInsId(hisIns.getId());
            // 计算耗时
            if (Objects.nonNull(hisIns.getEndTime())) {
                taskVo.setDuration(DateUtils.getDatePoor(hisIns.getEndTime(), hisIns.getStartTime()));
            } else {
                taskVo.setDuration(DateUtils.getDatePoor(DateUtils.getNowDate(), hisIns.getStartTime()));
            }
            //查询业务拓展表
            // 注意：没有业务拓展记录的实例不能直接丢弃（例如主键长度修复之前发起的老流程就没有拓展记录），
            // 否则「流程中心」看不到这些实例、也就无法在此废除它们。
            WfTaskVo extendTaskVo =
                    this.selectProcessOfExtendProcinst(taskVo, processQuery.getParams(), hisIns.getId());
            if (extendTaskVo != null) {
                taskVo = extendTaskVo;
            }
            //流程发起的定义id
            taskVo.setProcDefId(hisIns.getProcessDefinitionId());
            //流程名称（列表「流程名称」列）：历史实例自带定义名，取不到时回查一次流程定义兜底
            String procDefName = hisIns.getProcessDefinitionName();
            if (procDefName == null || procDefName.isEmpty()) {
                ProcessDefinition hisPd =
                        repositoryService
                                .createProcessDefinitionQuery()
                                .processDefinitionId(hisIns.getProcessDefinitionId())
                                .singleResult();
                if (hisPd != null) {
                    procDefName = hisPd.getName();
                }
            }
            taskVo.setProcDefName(procDefName);
            //流程参数
            taskVo.setVariables(hisIns.getProcessVariables());
            taskVoList.add(taskVo);
        }
        return TableDataInfo.subList(taskVoList, pageQuery.getPageNum(), pageQuery.getPageSize());
    }

}
