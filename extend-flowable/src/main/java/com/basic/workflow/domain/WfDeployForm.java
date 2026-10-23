package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 流程部署与表单的关联对象，对应表 wf_deploy_form，按部署与节点记录绑定的表单内容
 * @author yanbin
 * @date 2023/11/3 10:37
 */
@Data
@Schema(description = "流程实例关联表单对象")
@TableName("wf_deploy_form")
public class WfDeployForm {
    private static final long serialVersionUID = 1L;

    /**
     * 主键。
     * 表 wf_deploy_form.id 是 NOT NULL 主键，而业务上不需要外部指定，
     * 这里交由 MyBatis-Plus 按 ASSIGN_UUID 自动生成 32 位 UUID 写入。
     */
    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 流程部署主键
     */
    @Schema(description = "流程部署主键")
    private String deployId;

    /**
     * 表单id
     */
    @Schema(description = "表单Key")
    private String formId;

    /**
     * 节点Key
     */
    @Schema(description = "节点Key")
    private String activityId;

    /**
     * 模型key
     */
    @Schema(description = "模型key")
    private String modelKey;

    /**
     * 表单名称
     */
    @Schema(description = "表单名称")
    private String formName;

    /**
     * 节点名称
     */
    @Schema(description = "节点名称")
    private String activityName;

    /**
     * 节点类型
     */
    @Schema(description = "节点类型")
    private String activityType;

    /**
     * 表单内容
     */
    @Schema(description = "表单内容")
    private String content;
}
