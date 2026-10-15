package com.basic.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (flowable 任务运行时的节点信息)  对应表 act_ru_actinst
 * @date 2023/12/29
 */
@Mapper
@Repository
public interface WfActRuActinstMapper {
    /**
     * @return void
     * @description TODO (删除节点信息)
     * @params [runActivityIds]
     * @author yanbin
     * @date 2024/1/23 10:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void deleteRunActinstsByIds(List<String> runActivityIds);

    /**
     * @return void
     * @description TODO (删除活动绑定的任务信息)
     * @params [taskId]
     * @author yanbin
     * @date 2024/2/22 10:44
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteToRuActinstForTaskId(@Param("taskId") String taskId);
}
