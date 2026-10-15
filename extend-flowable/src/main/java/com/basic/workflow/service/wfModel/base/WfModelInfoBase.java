package com.basic.workflow.service.wfModel.base;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.basic.common.utils.StringUtils;
import com.basic.enm.ProcessStatusEnum;
import com.basic.flowable.factory.FlowServiceFactory;
import com.basic.system.domain.SysFiles;
import com.basic.system.service.ISysFilesService;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.vo.StartProcessInstanceVo;
import com.basic.workflow.mapper.WfFlowableProcDefMapper;
import com.basic.workflow.service.*;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (flowable流程模型抽象服务类)
 * @date 2024/03/05
 */
public abstract class WfModelInfoBase extends FlowServiceFactory {
    @Resource
    protected BpmnJsonConverter bpmnJsonConverter;
    @Resource
    protected IWfFormService wfFormService;
    @Resource
    protected RuntimeService runtimeService;
    @Resource
    protected IWfBpmnModelService bpmnModelService;
    @Resource
    protected IWfExtendProcinstService extendProcinstService;
    @Resource
    protected IWfCopyService copyService;
    @Resource
    protected IWfProcessService wfProcessService;
    @Resource
    protected IWfTaskService wfTaskService;
    @Resource
    protected ISysFilesService sysFilesService;
    @Resource
    protected IWfCommentInfoService wfCommentInfoService;
    @Resource
    protected IWfDeployFormService deployFormService;
    @Resource
    protected IWfTaskFormService wfTaskFormService;
    @Resource
    protected WfFlowableProcDefMapper wfFlowableProcDefMapper;

    /**
     * @return java.lang.String
     * @description TODO (创建流程实例扩展表信息)
     * @params [params 启动参数, processInstance 流程实例对象, creator 创建人]
     * @author yanbin
     * @date 2024/1/19 15:07
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected String createExtendHisProcinst(
            StartProcessInstanceVo params, ProcessInstance processInstance, String creator) {
        ExtendProcinst extendProcinst = new ExtendProcinst();
        //当前处理人
        extendProcinst.setCurrentUserCode(params.getCurrentUserCode());
        //流程模型id
        extendProcinst.setModelId(params.getModelId());
        //实例id
        extendProcinst.setProcInsId(processInstance.getId());
        //定义id
        extendProcinst.setProcDefId(processInstance.getProcessDefinitionId());
        //流程状态
        extendProcinst.setProcessStatus(ProcessStatusEnum.SPZ.toString());
        //流程名称
        extendProcinst.setProcessName(params.getFormName());
        //modelKey
        extendProcinst.setModelKey(params.getProcessDefinitionKey());
        //表单数据JSON
        extendProcinst.setFormData(params.getFormData());
        //租户id
        extendProcinst.setTenantId(params.getAppSn());
        //创建者
        extendProcinst.setCreator(creator);
        //创建时间
        extendProcinst.setCreateTime(new Date());
        //更新时间
        extendProcinst.setUpdateTime(new Date());
        //生成id
        extendProcinst.setId(IdWorker.get32UUID());
        //创建名名称
        extendProcinst.setCurrentName(params.getCurrentUserName());
        String processStatus = extendProcinst.getProcessStatus();
        HistoricProcessInstance historicProcessInstance =
                historyService
                        .createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstance.getProcessInstanceId())
                        .singleResult();
        //判断当前实例是否结束  未结束
        if (historicProcessInstance.getEndTime() != null) {
            extendProcinst.setProcessStatus(ProcessStatusEnum.BJ.toString());
        }
        extendProcinstService.saveExtendProcinstAndHis(extendProcinst);
        return processStatus;
    }

    /**
     * @return void
     * @description TODO (处理流程启动的附件保存)
     * @params [startProcessInstanceVo, processInstance]
     * @author yanbin
     * @date 2024/1/24 14:10
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected void saveFileToModel(StartProcessInstanceVo startProcessInstanceVo, ProcessInstance processInstance,String activityId) {
        List<Map<String, Object>> fileList = startProcessInstanceVo.getFileList();
        if (StringUtils.isNotEmpty(fileList)) {
            //处理上传的文件
            for (Map<String, Object> file : fileList) {
                SysFiles item = sysFilesService.getById(Long.parseLong(file.get("id").toString()));
                //流程实例id
                item.setProcessInstanceId(processInstance.getId());
                //处理人id
                item.setUserId(Long.valueOf(startProcessInstanceVo.getCurrentUserCode()));
                //处理人nickName
                item.setNickName(startProcessInstanceVo.getCurrentUserName());
                //活动id
                item.setActivityId(activityId);
                sysFilesService.saveOrUpdate(item);
            }
        }
    }
}
