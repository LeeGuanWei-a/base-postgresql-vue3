package com.basic.workflow.domain.vo;

import com.basic.enm.CommentTypeEnum;
import com.basic.flowable.common.enums.FlowComment;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yanbin
 * @description TODO (意见记录vo)
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
    public CommentVo(){}
    public CommentVo(String userId, String processInstanceId, String type, String message) {
        this.userId = userId;
        this.processInstanceId = processInstanceId;
        this.message = message;
        this.type = type;
    }
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

    public void setType(String type) {
        this.type = type;
        this.typeName = resolveTypeName(type);
    }

    /**
     * 解析意见类型的中文名。
     *
     * <p>写入时用的是 {@link FlowComment}（<b>数字码</b>：1 正常 / 2 退回 / 3 驳回 /
     * 11 提交 / 14 会签回退 …），而 {@link CommentTypeEnum} 用的是<b>字母码</b>
     * （SP / BH / TJ …）—— 两套对不上。</p>
     *
     * <p>原来只用 CommentTypeEnum 解析，数字码永远解析为空字符串，
     * 前端拿不到 typeName，只能把 type 这个裸数字显示出来
     * （用户看到审批记录上写着 "11"、"1"、"100"）。</p>
     *
     * <p>现在改为：优先 FlowComment（数字码）→ 再兜底合成记录的伪类型 →
     * 最后退回 CommentTypeEnum（兼容历史字母码数据）。</p>
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
