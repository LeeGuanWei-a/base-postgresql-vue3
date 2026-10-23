package com.basic.workflow.cmd;

import com.basic.enm.CommentTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.CommentEntity;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.task.Comment;
import org.flowable.engine.task.Event;

/**
 * @description 添加历史审批意见的引擎命令，正文超长时截断摘要而完整内容另存
 * @author yanbin
 * @date 2023/10/24
 */
public class AddHisCommentCmd implements Command<Comment> {

  protected String taskId;
  protected String userId;
  protected String processInstanceId;
  protected String type;
  protected String message;

  /**
   * @description 绑定任务主键、操作人、流程实例、意见类型与正文，构造添加历史审批意见的命令
   * @params [taskId, userId, processInstanceId, type, message] 任务主键，流程级意见时可为空；操作人主键；流程实例主键；意见类型，为空时按待办类型记录；意见正文
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public AddHisCommentCmd(
      String taskId, String userId, String processInstanceId, String type, String message) {
    this.taskId = taskId;
    this.userId = userId;
    this.processInstanceId = processInstanceId;
    this.type = type;
    this.message = message;
  }

  /**
   * @return Comment 已落库的意见实体
   * @description 向引擎插入一条审批意见，正文超长时摘要截断到 3900 字符内而完整内容另存
   * @params [commandContext] Flowable 命令上下文
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  @Override
  public Comment execute(CommandContext commandContext) {
    HistoricProcessInstanceEntity hisProIns = null;
    if (processInstanceId != null) {
      hisProIns =
          CommandContextUtil.getHistoricProcessInstanceEntityManager(commandContext)
              .findById(processInstanceId);
      if (hisProIns == null) {
        throw new FlowableObjectNotFoundException(
            "HistoricProcessInstance " + processInstanceId + " doesn't exist",
            HistoricProcessInstanceEntity.class);
      }
    }
    CommentEntity comment = CommandContextUtil.getCommentEntityManager(commandContext).create();
    comment.setUserId(userId);
    comment.setType((type == null) ? CommentTypeEnum.SP.toString() : type);
    comment.setTime(
        CommandContextUtil.getProcessEngineConfiguration(commandContext)
            .getClock()
            .getCurrentTime());
    comment.setTaskId(taskId);
    comment.setProcessInstanceId(processInstanceId);
    comment.setAction(Event.ACTION_ADD_COMMENT);

    String eventMessage = StringUtils.isNotBlank(message) ? message.replaceAll("\\s+", " ") : "";
    if (eventMessage.length() > 3900) {
      eventMessage = eventMessage.substring(0, 3900) + "...";
    }
    comment.setMessage(eventMessage);
    comment.setFullMessage(message);
    CommandContextUtil.getCommentEntityManager(commandContext).insert(comment);
    return comment;
  }

  protected String getSuspendedTaskException() {
    return "Cannot add a comment to a suspended task";
  }

  protected String getSuspendedExceptionMessage() {
    return "Cannot add a comment to a suspended execution";
  }
}
