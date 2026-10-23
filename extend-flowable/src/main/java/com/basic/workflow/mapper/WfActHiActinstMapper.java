package com.basic.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description 历史活动实例 Mapper，对应表 act_hi_actinst，用于清理回滚与任务删除后的历史节点记录
 * @author yanbin
 * @date 2023/12/29
 */
@Mapper
@Repository
public interface WfActHiActinstMapper {
    /**
     * @description 按运行态活动实例 ID 集合批量删除对应的历史活动实例记录
     * @params [runActivityIds] 待清理的运行态活动实例 ID 集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void deleteHisActinstsByIds(List<String> runActivityIds);

    /**
     * @description 按任务 ID 删除该任务所绑定的历史活动实例记录
     * @params [taskId] 任务主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteToHiActinstForTaskId(@Param("taskId") String taskId);
}
