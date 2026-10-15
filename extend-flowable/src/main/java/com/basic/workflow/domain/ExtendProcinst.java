package com.basic.workflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.basic.common.core.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yanbin
 * @description TODO (流程业务拓展表)
 * @date 2023/10/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_flow_extend_procinst")
public class ExtendProcinst extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 流程实例id
     */
    private String procInsId;
    /**
     * 流程定义id
     */
    private String procDefId;
    /**
     * 模型key
     */
    private String modelKey;
    /**
     * 模型id
     */
    private String modelId;

    /**
     * 流程表单名称
     */
    private String processName;
    /**
     * 流程状态
     */
    private String processStatus;

    /**
     * 流程发起人
     */
    private String currentUserCode;

    /**
     * 租户id(系统标识)
     */
    private String tenantId;
    /**
     * 表单数据
     */
    private String formData;
    /**
     * 发起人名称
     * 说明：该列原注释为「业务产品id」，实际被复用为存储发起人名称，
     * 被 WfProcessInfoBase / WfAllProcessOrTaskService / WfHistoryProcNodeService
     * 等用于展示 startUserName、assigneeName，故予以保留。
     */
    private String currentName;
}
