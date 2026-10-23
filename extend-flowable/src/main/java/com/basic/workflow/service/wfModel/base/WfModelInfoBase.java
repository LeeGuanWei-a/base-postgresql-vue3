package com.basic.workflow.service.wfModel.base;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.basic.common.exception.ServiceException;
import com.basic.common.utils.StringUtils;
import com.basic.enm.ProcessStatusEnum;
import com.basic.flowable.factory.FlowServiceFactory;
import com.basic.flowable.utils.SafeXmlUtils;
import com.basic.system.domain.SysFiles;
import com.basic.system.service.ISysFilesService;
import com.basic.workflow.domain.ExtendProcinst;
import com.basic.workflow.domain.vo.StartProcessInstanceVo;
import com.basic.workflow.mapper.WfFlowableProcDefMapper;
import com.basic.workflow.service.*;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import javax.xml.stream.XMLStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description 流程模型服务抽象基类，集中注入模型管理所需的业务服务与 Flowable 引擎客户端。
 * 所有业务服务字段加 {@code @Lazy} 打破与 {@code WfProcessInfoServiceBase} / {@code WfModelInfoServiceBase}
 * 之间的潜在循环依赖（ServiceBase ↔ Base ↔ 父 ServiceBase 形成的 bean 创建回路）。
 * 真正调用时才走代理注入，初始化阶段不会被 Spring 拦截报错。
 * @author yanbin
 * @date 2024/03/05
 */
public abstract class WfModelInfoBase extends FlowServiceFactory {
    @Resource
    @Lazy
    protected IWfFormService wfFormService;
    @Resource
    @Lazy
    protected RuntimeService runtimeService;
    @Resource
    @Lazy
    protected IWfBpmnModelService bpmnModelService;
    @Resource
    @Lazy
    protected IWfExtendProcinstService extendProcinstService;
    @Resource
    @Lazy
    protected IWfCopyService copyService;
    @Resource
    @Lazy
    protected IWfProcessService wfProcessService;
    @Resource
    @Lazy
    protected IWfTaskService wfTaskService;
    @Resource
    @Lazy
    protected ISysFilesService sysFilesService;
    @Resource
    @Lazy
    protected IWfCommentInfoService wfCommentInfoService;
    @Resource
    @Lazy
    protected IWfDeployFormService deployFormService;
    @Resource
    @Lazy
    protected IWfTaskFormService wfTaskFormService;
    @Resource
    @Lazy
    protected WfFlowableProcDefMapper wfFlowableProcDefMapper;

    /**
     * @return boolean 内容以左花括号开头时返回 true
     * @description 判断模型内容是否为旧版设计器的 stencil JSON，Flowable 7 起官方已移除 JSON 与 BPMN 的互转能力
     * @params [content] 模型内容，可能是 BPMN XML 或旧版 JSON
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static boolean looksLikeJsonModel(String content) {
        return content != null && content.trim().startsWith("{");
    }

    /**
     * @return BpmnModel 解析出的流程模型，主流程缺失或解析出错时抛出业务异常
     * @description 用安全的 XML 读取器把 BPMN XML 解析成流程模型，内容为空、缺主流程或解析失败都转成业务异常
     * @params [bpmnXml] BPMN 2.0 XML 字符串
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected BpmnModel parseBpmnModel(String bpmnXml) {
        if (StringUtils.isBlank(bpmnXml)) {
            throw new ServiceException("BPMN XML 为空");
        }
        try {
            XMLStreamReader reader = SafeXmlUtils.createSafeXmlStreamReader(bpmnXml);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(reader);
            if (bpmnModel == null || bpmnModel.getMainProcess() == null) {
                throw new ServiceException("BPMN XML 解析后没有主流程，请检查流程图是否完整");
            }
            return bpmnModel;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("BPMN XML 解析失败：" + e.getMessage());
        }
    }

    /**
     * @return byte[] 序列化后的 BPMN XML 字节数组
     * @description 把流程模型序列化回 BPMN XML 字节，用于落库或导出
     * @params [bpmnModel] 待序列化的流程模型
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    protected byte[] toBpmnXmlBytes(BpmnModel bpmnModel) {
        return new BpmnXMLConverter().convertToXML(bpmnModel);
    }

    /**
     * @return String 写库前取的流程状态，实例已结束时为办结状态
     * @description 用启动参数与流程实例组装流程实例扩展记录并落库，再按历史实例是否已结束决定落库的流程状态
     * @params [params, processInstance, creator] params 流程启动参数；processInstance 刚创建的流程实例；creator 记录创建人
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @description 把启动参数里携带的附件逐个挂到流程实例上，写入实例id、处理人、活动id后保存
     * @params [startProcessInstanceVo, processInstance, activityId] startProcessInstanceVo 流程启动参数，含附件id列表；processInstance 流程实例；activityId 附件归属的流程节点id
     * @author liguanwei
     * @date 2026/10/17 20:52
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
