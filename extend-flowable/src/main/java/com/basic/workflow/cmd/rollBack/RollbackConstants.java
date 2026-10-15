package com.basic.workflow.cmd.rollBack;

/**
 * @Author Zengjunlong
 * @Description //TODO 撤回变量名
 * @Date 17:55 2024/3/6
 * @Param 
 * @return 
 **/
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
