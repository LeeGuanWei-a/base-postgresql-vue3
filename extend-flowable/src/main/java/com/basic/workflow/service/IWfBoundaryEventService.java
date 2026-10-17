package com.basic.workflow.service;

import com.basic.workflow.domain.bo.WfBoundaryTriggerBo;
import com.basic.workflow.domain.vo.WfBoundaryEventVo;

import java.util.List;

/**
 * 流程边界事件（Boundary Event）运行时触发服务。
 *
 * <p>封装对 Flowable {@code RuntimeService} 的 signal/message/error/cancel 触发调用，
 * 同时把触发记录写进 {@code processStatus} 流程变量，
 * 便于前端列表/详情页标识"此流程是被 boundary 触发的"。</p>
 *
 * <p>本服务为「新增能力」，不修改现有 {@code stopProcess} / {@code abolishProcess} 的人工逻辑。
 * 业务侧如果需要"用 boundary 替代人工取消"，单独在 WfTaskController 里加路由判断。</p>
 *
 * @author integration-supplement
 */
public interface IWfBoundaryEventService {

    /**
     * @return boolean 触发成功返回 true，流程实例或事件编码无效时返回 false
     * @description 按流程实例触发信号边界事件，可全局广播或投递到指定执行实例，并把触发结果写入流程状态变量
     * @params [bo] 触发请求，含流程实例 ID、事件编码与流程变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean triggerSignal(WfBoundaryTriggerBo bo);

    /**
     * @return boolean 触发成功返回 true，未找到对应订阅时返回 false
     * @description 按消息名定位到具体执行实例并投递消息，触发消息边界事件
     * @params [bo] 触发请求，事件编码即消息名，另含流程实例 ID 与流程变量
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean triggerMessage(WfBoundaryTriggerBo bo);

    /**
     * @return boolean 触发成功返回 true，无匹配的错误边界时返回 false
     * @description 以错误编码抛出 BpmnError 交给引擎，定位并触发 errorRef 相同的错误边界事件
     * @params [bo] 触发请求，事件编码即错误编码，意见内容作为错误信息
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean triggerError(WfBoundaryTriggerBo bo);

    /**
     * @return boolean 触发成功返回 true；节点上的 cancel 边界不支持触发时抛业务异常
     * @description 触发事务子流程或调用活动边界上的取消边界事件，普通用户任务节点的取消边界直接抛业务异常
     * @params [bo] 触发请求，含流程实例 ID 与作为取消原因的审批意见
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean triggerCancel(WfBoundaryTriggerBo bo);

    /**
     * @return List<WfBoundaryEventVo> 流程定义阶段注册的边界事件清单
     * @description 从最新部署的流程定义里取该流程实例的边界事件清单，供流程图高亮与调试展示
     * @params [procInsId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfBoundaryEventVo> listBoundaries(String procInsId);
}
