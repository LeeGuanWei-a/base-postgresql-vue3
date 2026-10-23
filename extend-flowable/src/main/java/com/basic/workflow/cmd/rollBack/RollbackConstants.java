package com.basic.workflow.cmd.rollBack;

/**
 * @description 流程撤回相关的变量名常量，含回滚任务执行人、任务类型的前缀标识以及会签实例计数变量
 * @author Zengjunlong
 * @date 17:55 2024/3/6
 */
public interface RollbackConstants {



    /**
     *  配置 任务执行人变量
     */
    String ASSIGNEE_PREFIX_KEY = "ROLLBACK_ASSIGNEE_PREFIX_";

    /**
     *  配置 任务执行人变量
     */
    String TASK_TYPE_PREFIX_KEY = "ROLLBACK_TASK_TYPE_PREFIX_";


    /**
     *  会签任务变量
     */
    interface MultiInstanceConstants{

        String NR_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";
        String NR_OF_COMPLETE_INSTANCES = "nrOfCompletedInstances";
        String NR_OF_INSTANCE = "nrOfInstances";

        String LOOP_COUNTER = "loopCounter";
    }
}
