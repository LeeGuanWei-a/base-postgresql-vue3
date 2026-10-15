package com.basic.workflow.domain.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author yanbin
 * @description TODO (流程任务业务对象)
 * @date 2024/1/16 10:37
 */
@Data
@ApiModel(value = "WfTaskBo", description = "流程任务业务对象")
public class WfTaskBo {
    /**
     * 任务Id
     */
    @ApiModelProperty(value = "任务Id")
    private String taskId;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    /**
     * 用户Id
     */
    @ApiModelProperty(value = "当前处理人currentUserId", required = true)
    //TODO 当前处理人userId
    private String currentUserId;
    /**
     * 当前处理人的角色id
     */
    @ApiModelProperty(value = "当前处理人的角色id", required = true)
    //TODO 当前处理人的角色id
    private List<String> currentGroupId;

    /**
     * 用户昵称
     */
    //TODO 当前处理人昵称
    @ApiModelProperty(value = "当前处理人昵称", required = true)
    private String currentUserName;
    /**
     * 审批人
     */
    //TODO 转办,委派审批人
    @ApiModelProperty(value = "审批人id")
    private String assignee;
    /**
     * 审批人昵称
     */
    //TODO 转办,委派审批人昵称
    @ApiModelProperty(value = "审批人昵称")
    private String assigneeName;
    /**
     * 任务意见
     */
    @ApiModelProperty(value = "任务意见")
    private String comment;
    /**
     * 流程实例Id
     */
    @ApiModelProperty(value = "流程实例Id", required = true)
    private String procInsId;
    /**
     * 节点
     */
    @ApiModelProperty(value = "节点")
    private String targetKey;
    /**
     * 流程变量信息
     */
    @ApiModelProperty(value = "流程变量信息")
    private Map<String, Object> variables;
    /**
     * 审批表单数据
     */
    @ApiModelProperty(value = "审批表单数据")
    private String formData;
    /**
     * 候选人
     */
    @ApiModelProperty(value = "候选人")
    private List<String> candidateUsers;
    /**
     * 审批组
     */
    @ApiModelProperty(value = "审批组")
    private List<String> candidateGroups;
    /**
     * 抄送用户Id
     */
    @ApiModelProperty(value = "抄送用户Id")
    private String copyUserIds;
    /**
     * 下一节点审批人
     */
    @ApiModelProperty(value = "下一节点审批人id字符串")
    private String nextUserIds;
    /**
     * 加签人员
     */
    @ApiModelProperty(value = "加签人员id字符串")
    private String userList;
    /**
     * 附件信息
     */
    @ApiModelProperty(value = "附件信息")
    private List<Map<String, Object>> fileList;
    /**
     * 流程定义id
     **/
    @ApiModelProperty(value = "流程定义id")
    private String procDefId;

    /**
     * 多实例类型
     * 或签ParallelMultiInstance
     * 会签SequentialMultiInstance
     */
    @ApiModelProperty(value = "多实例类型")
    private String multiInstance;

    /**
     * 替换追加审批人
     * addition追加
     * replace替换
     */
    @ApiModelProperty(value = "替换追加审批人 addition追加 replace替换")
    private String pattern;
    /**
     * 串行并行
     * false 并行
     * true 串行
     **/
    @ApiModelProperty(value = "串行并行 false并行 true串行")
    private String isSequential;
    /**
     * 固定值，身份存储
     */
    @ApiModelProperty(value = "固定值0   身份存储1 ")
    private String dataType;

    /**
     * 节点类型
     **/
    @ApiModelProperty(value = "节点类型 ")
    private String nodeType;
    /**
     * 实例id集合
     */
    @ApiModelProperty(value = "实例id集合")
    private List<String> procInsIdList;
    /**
     * 流程状态/任务状态
     */
    @ApiModelProperty(value = "流程状态/任务状态")
    private String processStatus;
    /**
     * 选中的跳转节点
     */
    @ApiModelProperty(value = "选中的跳转节点")
    private String skipActivityId;

    @ApiModelProperty(value = "审核意见类型")
    private String commentType;

    public WfTaskBo() {
        super();
    }

    public WfTaskBo(String procInsId, String copyUserIds, String currentUserId, String currentUserName) {
        this.procInsId = procInsId;
        this.copyUserIds = copyUserIds;
        this.currentUserId = currentUserId;
        this.currentUserName = currentUserName;
    }
}
