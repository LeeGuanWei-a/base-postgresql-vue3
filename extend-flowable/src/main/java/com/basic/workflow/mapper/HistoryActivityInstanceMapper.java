package com.basic.workflow.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl;

import java.util.List;

/**
 * @ClassName: HistoryActivityInstanceMapper
 * @Author: huangrenhao
 * @Description:
 * @CreateTime： 2020/2/28 0028 下午 12:19
 * @Version：
 **/
@Mapper
public interface HistoryActivityInstanceMapper {

    /**
     * @return List<HistoricActivityInstanceEntityImpl> 该流程实例的历史活动实例列表，无记录时为空列表
     * @description 按流程实例 id 查询 act_hi_actinst 中的历史活动实例，按查询出的原样返回
     * @params [processInstanceId] 流程实例 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Select("SELECT * FROM act_hi_actinst WHERE PROC_INST_ID_ = #{processInstanceId}")
    List<HistoricActivityInstanceEntityImpl> findList(@Param("processInstanceId") String processInstanceId);

    /**
     * @return int 删除的行数，主键不存在时为 0
     * @description 按历史活动实例主键删除 act_hi_actinst 中的单条历史活动记录
     * @params [id] 历史活动实例主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Delete("DELETE FROM act_hi_actinst WHERE id_ = #{id}")
    int delete(@Param("id") String id);

    /**
     * @description 按任务 id 删除 act_hi_identitylink 中该任务的历史办理人与候选人关联记录
     * @params [taskId] 任务 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Delete(value = "delete from act_hi_identitylink where task_id_=#{taskId}")
    void deleteHisIdentitylink(@Param("taskId") String taskId);

    /**
     * @description 按流程实例 id 与任务 id 同时限定，删除 act_hi_taskinst 中该条历史任务记录
     * @params [processInstanceId, taskId] processInstanceId 流程实例 id；taskId 历史任务 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Delete(value = "delete from act_hi_taskinst where proc_inst_id_=#{processInstanceId} and id_=#{taskId}")
    void deleteHisTask(@Param("processInstanceId") String processInstanceId,@Param("taskId") String taskId);

    /**
     * @description 按流程实例 id 与该任务的 task_id_ 删除 act_hi_actinst 中这次任务办理产生的历史活动实例
     * @params [processInstanceId, taskId] processInstanceId 流程实例 id；taskId 任务 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Delete(value = "delete from act_hi_actinst where proc_inst_id_=#{processInstanceId} and task_id_=#{taskId}")
    void deleteHisActinst(@Param("processInstanceId") String processInstanceId, @Param("taskId") String taskId);

    /**
     * @description 按任务 id 删除 wf_comment_info 中该任务的业务评论记录（本项目评论表，不是 act_hi_comment）
     * @params [taskId] 任务 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Delete(value = "delete from wf_comment_info where task_id=#{taskId}")
    void deleteToCommentForTaskId(@Param("taskId") String taskId);

    /**
     * @description 按任务 id 与节点 activityId 删除 wf_task_form 中该任务节点的表单绑定记录
     * @params [taskId, activityId] taskId 任务 id；activityId 流程节点（活动）id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Delete(value = "delete from wf_task_form where task_id=#{taskId} and activity_id=#{activityId}")
    void deleteTaskForm(@Param("taskId") String taskId, @Param("activityId") String activityId);


}
