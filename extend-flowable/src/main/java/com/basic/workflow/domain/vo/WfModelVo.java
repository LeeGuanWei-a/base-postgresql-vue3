package com.basic.workflow.domain.vo;

import lombok.Data;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程模型视图对象，承载模型标识、分类、版本、表单配置、BPMN XML 与部署状态
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
@Schema(description = "流程分类对象")
public class WfModelVo {
  /** 模型ID */
  @Schema(description = "模型ID ")
  private String modelId;
  /** 模型名称 */
  @Schema(description = "模型名称 ")
  private String modelName;
  /** 模型Key */
  @Schema(description = "模型Key ")
  private String modelKey;
  /** 分类编码 */
  @Schema(description = "分类编码 ")
  private String category;
  /** 版本 */
  @Schema(description = "版本 ")
  private Integer version;
  /** 表单类型 */
  @Schema(description = "表单类型 ")
  private Integer formType;
  /** 表单ID */
  @Schema(description = "表单ID ")
  private String formId;
  /** 模型描述 */
  @Schema(description = "模型描述 ")
  private String description;
  /** 创建时间 */
  @Schema(description = "创建时间 ")
  private Date createTime;
  /** 流程xml */
  @Schema(description = "流程xml ")
  private String bpmnXml;
  /** 表单内容 */
  @Schema(description = "表单内容 ")
  private String content;
  /** 是否已经部署 */
  @Schema(description = "是否已经部署 ")
  private Boolean deploy;
  @Schema(description = "部署id")
  private String deployId;
  /** 更新时间* */
  @Schema(description = "更新时间 ")
  private Date updateTime;
  /** 部署后的流程是否挂起(停用) */
  @Schema(description = "部署后的流程是否挂起(停用) ")
  private Boolean suspended;
  /**
   * 是否待重新部署：
   * model 已部署过、且 bpmn XML 最后修改时间晚于当前部署时间。
   * 含义：模型在设计器里被改动并保存了，但还没有重新部署，前端据此提示用户需要重新部署。
   */
  @Schema(description = "是否待重新部署（bpmn 已修改但未重新部署）")
  private Boolean pendingRedeploy;
}
