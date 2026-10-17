package com.basic.workflow.service.migrate;

/**
 * 流程替换异常（运行中实例迁移到新流程定义）。
 *
 * <p>抛出后调用方应中止当前事务，让 Service 的事务全部回滚。
 * 本异常不携带业务堆栈掩盖，调用方可直接打印 message + cause 定位失败原因。</p>
 *
 * @author integration-supplement
 */
public class WfProcessMigrateException extends RuntimeException {

    /**
     * @description 用失败原因构造异常，提示信息原样传给调用方
     * @params [message] 失败原因，会直接展示给调用方
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfProcessMigrateException(String message) {
        super(message);
    }

    /**
     * @description 用失败原因与原始异常构造异常，保留根因便于定位问题
     * @params [message, cause] 失败原因；触发失败的原始异常
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public WfProcessMigrateException(String message, Throwable cause) {
        super(message, cause);
    }
}
