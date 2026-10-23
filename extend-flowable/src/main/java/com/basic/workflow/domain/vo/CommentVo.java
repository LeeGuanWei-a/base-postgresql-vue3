package com.basic.workflow.domain.vo;

import com.basic.enm.CommentTypeEnum;
import com.basic.flowable.common.enums.FlowComment;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 流程审批意见视图对象，承载任务、办理人、意见内容、时间与意见类型等展示信息
 * @author yanbin
 * @date 2023/11/06
 */
public class CommentVo implements Serializable {
    /**
     * 任务id
     */
    protected String taskId;
    /**
     * 添加人
     */
    protected String userId;
    /**
     * 用户的名称
     */
    protected String userName;
    /**
     * 用户的头像链接
     */
    protected String userUrl;
    /**
     * 流程实例id
     */
    protected String processInstanceId;
    /**
     * 意见信息
     */
    protected String message;
    /**
     * 时间
     */
    protected Date time;
    /**
     *  @see  com.basic.enm.CommentTypeEnum
     */
    private String type;
    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 评论全信息
     */
    private String fullMsg;
    /**
     * @description 构造空的意见记录，字段由调用方通过 setter 填充
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public CommentVo(){}
    /**
     * @description 构造流程级意见记录，不关联具体任务
     * @params [userId, processInstanceId, type, message] 添加人ID；流程实例ID；意见类型编码，数字码见 FlowComment，字母码见 CommentTypeEnum；意见内容
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public CommentVo(String userId, String processInstanceId, String type, String message) {
        this.userId = userId;
        this.processInstanceId = processInstanceId;
        this.message = message;
        this.type = type;
    }
    /**
     * @description 构造关联具体任务的意见记录，用于审批意见回显
     * @params [taskId, userId, processInstanceId, type, message] 任务ID；添加人ID；流程实例ID；意见类型编码，数字码见 FlowComment，字母码见 CommentTypeEnum；意见内容
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public CommentVo(String taskId, String userId, String processInstanceId,String type, String message) {
        this.taskId = taskId;
        this.userId = userId;
        this.processInstanceId = processInstanceId;
        this.message = message;
        this.type = type;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    /**
     * @description 设置意见类型编码，同时解析出中文类型名写入 typeName 供前端展示
     * @params [type] 意见类型编码，数字码见 FlowComment，字母码见 CommentTypeEnum
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public void setType(String type) {
        this.type = type;
        this.typeName = resolveTypeName(type);
    }

    /**
     * @return String 意见类型的中文名，无法识别时返回兜底枚举消息
     * @description 解析意见类型编码对应的中文名，优先按 FlowComment 数字码匹配，再处理合成记录的伪类型，最后退回 CommentTypeEnum 兼容历史字母码
     * @params [type] 意见类型编码，数字码见 FlowComment，字母码见 CommentTypeEnum
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    private static String resolveTypeName(String type) {
        if (type == null || type.isEmpty()) {
            return "";
        }
        for (FlowComment flowComment : FlowComment.values()) {
            if (flowComment.getType().equals(type)) {
                return flowComment.getRemark();
            }
        }
        // WfProcessCommentService 为审批记录合成的两条伪记录
        if ("100".equals(type)) {
            return "待办";
        }
        if ("13".equals(type)) {
            return "结束";
        }
        return CommentTypeEnum.getEnumMsgByType(type);
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getFullMsg() {
        return fullMsg;
    }

    public void setFullMsg(String fullMsg) {
        this.fullMsg = fullMsg;
    }
}
