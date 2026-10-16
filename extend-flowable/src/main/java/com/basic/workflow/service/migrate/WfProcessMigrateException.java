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

    public WfProcessMigrateException(String message) {
        super(message);
    }

    public WfProcessMigrateException(String message, Throwable cause) {
        super(message, cause);
    }
}
