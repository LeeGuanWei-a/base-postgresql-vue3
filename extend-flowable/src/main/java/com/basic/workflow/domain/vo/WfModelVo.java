package com.basic.workflow.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author yanbin
 * @description TODO (流程模型视图对象VO )
 * @date 2024/1/16 10:37
 */
@Data
@ApiModel(value = "WfCategory", description = "流程分类对象")
public class WfModelVo {
  /** 模型ID */
  @ApiModelProperty(value = "模型ID ")
  private String modelId;
  /** 模型名称 */
  @ApiModelProperty(value = "模型名称 ")
  private String modelName;
  /** 模型Key */
  @ApiModelProperty(value = "模型Key ")
  private String modelKey;
  /** 分类编码 */
  @ApiModelProperty(value = "分类编码 ")
  private String category;
  /** 版本 */
  @ApiModelProperty(value = "版本 ")
  private Integer version;
  /** 表单类型 */
  @ApiModelProperty(value = "表单类型 ")
  private Integer formType;
  /** 表单ID */
  @ApiModelProperty(value = "表单ID ")
  private Long formId;
  /** 模型描述 */
  @ApiModelProperty(value = "模型描述 ")
  private String description;
  /** 创建时间 */
  @ApiModelProperty(value = "创建时间 ")
  private Date createTime;
  /** 流程xml */
  @ApiModelProperty(value = "流程xml ")
  private String bpmnXml;
  /** 表单内容 */
  @ApiModelProperty(value = "表单内容 ")
  private String content;
  /** 是否已经部署 */
  @ApiModelProperty(value = "是否已经部署 ")
  private Boolean deploy;
  @ApiModelProperty(value = "部署id")
  private String deployId;
  /** 更新时间* */
  @ApiModelProperty(value = "更新时间 ")
  private Date updateTime;
  /** 部署后的流程是否挂起(停用) */
  @ApiModelProperty(value = "部署后的流程是否挂起(停用) ")
  private Boolean suspended;
  /**
   * 是否待重新部署：
   * model 已部署过、且 bpmn XML 最后修改时间晚于当前部署时间。
   * 含义：模型在设计器里被改动并保存了，但还没有重新部署，前端据此提示用户需要重新部署。
   */
  @ApiModelProperty(value = "是否待重新部署（bpmn 已修改但未重新部署）")
  private Boolean pendingRedeploy;
}
