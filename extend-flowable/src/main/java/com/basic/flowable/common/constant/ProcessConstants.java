package com.basic.flowable.common.constant;

/**
 * @description 流程常量定义，包含 BPMN 文件后缀、审批人/候选人类型、多实例属性及边界事件扩展元素等 key
 * @author yanbin
 * @date 2023/10/19
 */
public class ProcessConstants {

    public static final String SUFFIX = ".bpmn";

    /**
     * 动态数据
     */
    public static final String DATA_TYPE = "dynamic";

    /**
     * 单个审批人
     */
    public static final String USER_TYPE_ASSIGNEE = "assignee";

    /**
     * 候选人
     */
    public static final String USER_TYPE_USERS = "candidateUsers";

    /**
     * 审批组
     */
    public static final String USER_TYPE_ROUPS = "candidateGroups";

    /**
     * 单个审批人
     */
    public static final String PROCESS_APPROVAL = "approval";

    /**
     * 流程表达式
     */
    public static final String SKIP_EXPRESSION = "skipExpression";

    /**
     * 会签人员
     */
    public static final String PROCESS_MULTI_INSTANCE_USER = "userList";

    /**
     * nameapace
     */
    public static final String NAMASPASE = "http://flowable.org/bpmn";

    /**
     * 多实例节点
     */
    public static final String PROCESS_MULTI_INSTANCE = "multiInstance";

    /**
     * 自定义属性 dataType
     */
    public static final String PROCESS_CUSTOM_DATA_TYPE = "dataType";

    /**
     * 自定义属性 userType
     */
    public static final String PROCESS_CUSTOM_USER_TYPE = "userType";

    /**
     * 自定义属性 localScope
     */
    public static final String PROCESS_FORM_LOCAL_SCOPE = "localScope";

    /**
     * 自定义属性 流程状态
     */
    public static final String PROCESS_STATUS_KEY = "processStatus";

    /**
     * 流程跳过
     */
    public static final String FLOWABLE_SKIP_EXPRESSION_ENABLED = "_FLOWABLE_SKIP_EXPRESSION_ENABLED";

    // -----------------------------------------------------------------------
    // 边界事件（Boundary Event）相关常量
    // -----------------------------------------------------------------------

    /**
     * 边界事件自定义扩展元素名前缀
     * <p>实际写到 BPMN 的 flowable 扩展元素 name 形如：{@code boundaryTimerDuration}、{@code boundaryErrorCode}
     * </p>
     */
    public static final String BOUNDARY_EXT_PREFIX = "boundary";

    /**
     * 边界事件类型扩展元素（值取 BoundaryEventTypeEnum.shortName）
     */
    public static final String BOUNDARY_EXT_TYPE = BOUNDARY_EXT_PREFIX + "Type";

    /**
     * 边界事件定时器 - duration
     */
    public static final String BOUNDARY_EXT_TIMER_DURATION = BOUNDARY_EXT_PREFIX + "TimerDuration";

    /**
     * 边界事件定时器 - cycle
     */
    public static final String BOUNDARY_EXT_TIMER_CYCLE = BOUNDARY_EXT_PREFIX + "TimerCycle";

    /**
     * 边界事件定时器 - date
     */
    public static final String BOUNDARY_EXT_TIMER_DATE = BOUNDARY_EXT_PREFIX + "TimerDate";

    /**
     * 边界事件错误码（errorRef / errorCode 业务侧 key）
     */
    public static final String BOUNDARY_EXT_ERROR_CODE = BOUNDARY_EXT_PREFIX + "ErrorCode";

    /**
     * 边界事件信号 code（signalRef / signalCode 业务侧 key）
     */
    public static final String BOUNDARY_EXT_SIGNAL_CODE = BOUNDARY_EXT_PREFIX + "SignalCode";

    /**
     * 边界事件消息 ID（messageRef / messageId 业务侧 key）
     */
    public static final String BOUNDARY_EXT_MESSAGE_ID = BOUNDARY_EXT_PREFIX + "MessageId";

    /**
     * 边界事件 cancelActivity（true=中断宿主活动，false=非中断）
     */
    public static final String BOUNDARY_EXT_CANCEL_ACTIVITY = BOUNDARY_EXT_PREFIX + "CancelActivity";

    /**
     * 边界事件触发后的备注（写入 processStatus 旁的 comment 变量）
     */
    public static final String BOUNDARY_TRIGGER_COMMENT = "boundaryTriggerComment";

    /**
     * 边界事件触发时写入流程状态变量的 key 后缀
     * <p>流程 status = running 期间，触发 boundary 会把 status 改成
     * {@code boundary_<type>}（如 {@code boundary_timer}、{@code boundary_error}），
     * 便于列表筛选"被 boundary 触发的流程"。</p>
     */
    public static final String BOUNDARY_TRIGGER_STATUS_PREFIX = "boundary_";
}
