package com.basic.workflow.service.wfProcessInfo.extend;

import com.basic.workflow.service.wfProcessInfo.base.WfProcessInfoBase;
import org.springframework.stereotype.Service;


/**
 * @author yanbin
 * @description TODO (工作流流程管理/额外方法类)
 * @date 2025/02/21
 * @remark (流程与业务解耦后，原「根据业务id(businessKey)获取流程详情」的
 * getProcessInfoByBusinessKey 方法已删除：业务侧改为保存 procInsId 关联流程，
 * 引擎的 BUSINESS_KEY_ 不再写入。本类保留作为后续扩展方法的挂载点。)
 */
@Service
public class WfExtendProcessService extends WfProcessInfoBase {
}
