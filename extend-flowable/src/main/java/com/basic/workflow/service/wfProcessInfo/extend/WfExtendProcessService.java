package com.basic.workflow.service.wfProcessInfo.extend;

import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.springframework.stereotype.Service;


/**
 * @description 流程扩展服务，作为流程管理额外方法的挂载点，当前流程与业务已解耦、按 procInsId 关联
 * @author yanbin
 * @date 2025/02/21
 * @remark 原按 businessKey 获取流程详情的 getProcessInfoByBusinessKey 已删除，引擎的 BUSINESS_KEY_ 不再写入，
 * 本类保留作为后续扩展方法的挂载点
 */
@Service
public class WfExtendProcessService extends WfProcessInfoBase {
}
