package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程流转表单，对应表 wf_task_form，保存流程实例各任务节点提交的表单数据
 * @author yanbin
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
  @Schema(description = "主键id")
  private String id;
  /**
   * 表单id
   */
  @Schema(description = "表单id")
  private String formId;
  /**
   * 实例id
   */
  @Schema(description = "实例id")
  private String procInsId;
  /**
   * 任务id
   */
  @Schema(description = "任务id")
  private String taskId;
  /**
   * 活动id
   */
  @Schema(description = "活动id")
  private String activityId;
  /**
   * 用户id
   */
  @Schema(description = "用户id")
  private String userId;
  /**
   * 本次提交的表单数据（JSON 文本）。
   * <p>一行 = 一次提交：{@code wf_task_form} 里同一条记录既带上下文（实例/任务/节点/处理人），
   * 又带这份 JSON，读取端直接回显即可。
   * <p>历史实现把本字段标成 {@code @TableField(exist = false)} 只写 EAV 行，
   * 导致所有读取处拿到的都是 null，审批详情页表单永远空白，这里改为正常持久化。
   */
  @Schema(description = "表单内容")
  private String formContent;

  /**
   * 兼容列：早期 EAV 拆分存储用的字段名，新数据不再写入。
   * 保留映射是为了能读到历史数据（{@code form_content} 为空时前端可回退展示）。
   */
  @Schema(description = "字段名（历史 EAV 列，新数据不写）")
  private String field;
  /**
   * 兼容列：历史 EAV 拆分存储的字段值，新数据不再写入。
   */
  @Schema(description = "字段值（历史 EAV 列，新数据不写）")
  private String fieldValue;

  /**
   * 创建者
   */
  @TableField(fill = FieldFill.INSERT)
  @Schema(description = "创建者")
  private String createBy;

  /**
   * 创建时间
   */
  @TableField(fill = FieldFill.INSERT)
  @Schema(description = "创建时间")
  private Date createTime;

  /**
   * 更新者
   */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  @Schema(description = "更新者")
  private String updateBy;

  /**
   * 更新时间
   */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  @Schema(description = "更新时间")
  private Date updateTime;

  /**
   * 节点集合
   */
  @Schema(description = "节点集合")
  @TableField(exist = false)
  private String[] activityIdList;
  /**
   * 需要修改的字段值+字段
   */
  @TableField(exist = false)
  private Map<String, String> fileIdMap;
  @TableField(exist = false)
  private Map<String, String> formData;

  /**
   * @description 构造空白表单记录，字段由调用方或持久层填充
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public WfTaskForm() {
    super();
  }

  /**
   * @description 构造不带任务 ID 的表单记录，用于流程发起阶段的表单快照
   * @params [procInsId, formId, activityId, content, userId] 流程实例 ID；表单 ID；节点（活动）ID；本次提交的表单数据 JSON 文本；提交人 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public WfTaskForm(String procInsId, String formId, String activityId, String content,
      String userId) {
    this.activityId = activityId;
    this.formContent = content;
    this.procInsId = procInsId;
    this.formId = formId;
    this.userId = userId;
  }

  /**
   * @description 构造关联具体任务的表单记录，保存该次审批提交的表单快照
   * @params [taskId, content, procInsId, userId, activityId, formId] 任务 ID；本次提交的表单数据 JSON 文本；流程实例 ID；提交人 ID；节点（活动）ID；表单 ID
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
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
