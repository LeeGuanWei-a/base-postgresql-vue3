package com.basic.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description 运行时活动实例 Mapper，对应表 act_ru_actinst，提供按活动ID或任务ID删除运行中节点记录的能力
 * @author yanbin
 * @date 2023/12/29
 */
@Mapper
@Repository
public interface WfActRuActinstMapper {
    /**
     * @description 按运行时活动ID集合删除 act_ru_actinst 中的节点记录
     * @params [runActivityIds] 待删除的运行时活动ID集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void deleteRunActinstsByIds(List<String> runActivityIds);

    /**
     * @description 按任务ID删除该任务在 act_ru_actinst 中绑定的运行时活动记录
     * @params [taskId] 任务ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteToRuActinstForTaskId(@Param("taskId") String taskId);
}
