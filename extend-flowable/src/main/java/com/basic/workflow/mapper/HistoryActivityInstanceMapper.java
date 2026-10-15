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

    @Select("SELECT * FROM act_hi_actinst WHERE PROC_INST_ID_ = #{processInstanceId}")
    List<HistoricActivityInstanceEntityImpl> findList(@Param("processInstanceId") String processInstanceId);

    @Delete("DELETE FROM act_hi_actinst WHERE id_ = #{id}")
    int delete(@Param("id") String id);

    @Delete(value = "delete from act_hi_identitylink where task_id_=#{taskId}")
    void deleteHisIdentitylink(@Param("taskId") String taskId);

    @Delete(value = "delete from act_hi_taskinst where proc_inst_id_=#{processInstanceId} and id_=#{taskId}")
    void deleteHisTask(@Param("processInstanceId") String processInstanceId,@Param("taskId") String taskId);

    @Delete(value = "delete from act_hi_actinst where proc_inst_id_=#{processInstanceId} and task_id_=#{taskId}")
    void deleteHisActinst(@Param("processInstanceId") String processInstanceId, @Param("taskId") String taskId);

    @Delete(value = "delete from wf_comment_info where task_id=#{taskId}")
    void deleteToCommentForTaskId(@Param("taskId") String taskId);

    @Delete(value = "delete from wf_task_form where task_id=#{taskId} and activity_id=#{activityId}")
    void deleteTaskForm(@Param("taskId") String taskId, @Param("activityId") String activityId);


}
