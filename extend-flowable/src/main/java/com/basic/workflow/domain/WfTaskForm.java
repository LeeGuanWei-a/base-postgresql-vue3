package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;

/**
 * @author yanbin
 * @description TODO (流程流转表单)
 * @date 2024/02/28
 */
@Data
@TableName("wf_task_form")
public class WfTaskForm implements Serializable {

  private static final long serialVersionUID = 1L;
  /**
   * 主键
   */
  @TableId(value = "id", type = IdType.ASSIGN_UUID)
  @ApiModelProperty(value = "主键id")
  private String id;
  /**
   * 表单id
   */
  @ApiModelProperty(value = "表单id")
  private String formId;
  /**
   * 实例id
   */
  @ApiModelProperty(value = "实例id")
  private String procInsId;
  /**
   * 任务id
   */
  @ApiModelProperty(value = "任务id")
  private String taskId;
  /**
   * 活动id
   */
  @ApiModelProperty(value = "活动id")
  private String activityId;
  /**
   * 用户id
   */
  @ApiModelProperty(value = "用户id")
  private String userId;
  /**
   * 表单内容
   */
  @ApiModelProperty(value = "表单内容")
  @TableField(exist = false)
  private String formContent;

  /**
   * fileId字段
   */
  @ApiModelProperty(value = "fileId字段")
  private String field;
  /**
   * fileIdValue字段值
   */
  @ApiModelProperty(value = "fileIdValue字段值")
  private String fieldValue;

  /**
   * 节点集合
   */
  @ApiModelProperty(value = "节点集合")
  @TableField(exist = false)
  private String[] activityIdList;
  /**
   * 需要修改的字段值+字段
   */
  @TableField(exist = false)
  private Map<String, String> fileIdMap;
  @TableField(exist = false)
  private Map<String, String> formData;

  public WfTaskForm() {
    super();
  }

  public WfTaskForm(String procInsId, String formId, String activityId, String content,
      String userId) {
    this.activityId = activityId;
    this.formContent = content;
    this.procInsId = procInsId;
    this.formId = formId;
    this.userId = userId;
  }

  public WfTaskForm(String taskId, String content, String procInsId, String userId,
      String activityId, String formId) {
    this.activityId = activityId;
    this.formContent = content;
    this.procInsId = procInsId;
    this.formId = formId;
    this.userId = userId;
    this.taskId = taskId;
  }
}
