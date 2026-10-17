package com.basic.workflow.cmd.rollBack;

import org.flowable.task.api.history.HistoricTaskInstance;

/**
 * @description 撤回策略工厂接口，按历史任务所在节点解析并创建对应的回滚策略
 * @author Zengjunlong
 * @date 15:22 2024/3/7
 */
public interface RollbackStrategyFactory {

    /**
     * @return RollbackOperateStrategy 匹配该历史任务场景的回滚策略，无法识别场景时返回 null
     * @description 按历史任务所在节点解析出对应的回滚策略
     * @params [hisTask] 待回退的历史任务
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    RollbackOperateStrategy createStrategy(HistoricTaskInstance hisTask);

    /**
     * @return boolean 该节点仍有未完成任务时返回 true
     * @description 判断当前节点是否为会签节点且流程实例中仍存在未完成任务
     * @params [template] 回滚参数模板
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    boolean currentMultiInstanceTaskUnfinished(RollbackParamsTemplate template);
}
