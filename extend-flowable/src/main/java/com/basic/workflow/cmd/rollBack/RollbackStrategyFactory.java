package com.basic.workflow.cmd.rollBack;

import org.flowable.task.api.history.HistoricTaskInstance;

/**
 * @Author Zengjunlong
 * @Description //TODO 撤回策略工厂
 * @Date 15:22 2024/3/7
 * @Param 
 * @return 
 **/
public interface RollbackStrategyFactory {

    /**
     *  创建回滚策略
     * @param hisTask
     * @return
     */
    RollbackOperateStrategy createStrategy(HistoricTaskInstance hisTask);

    /**
     *  当前任务未完成判定
     * @param template
     * @return
     */
    boolean currentMultiInstanceTaskUnfinished(RollbackParamsTemplate template);
}
