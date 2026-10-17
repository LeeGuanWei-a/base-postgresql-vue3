package com.basic.workflow.domain.vo;

import cn.hutool.core.util.ObjectUtil;
import com.basic.workflow.domain.WfCommentInfo;
import com.basic.workflow.domain.dto.WfUserTaskDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description 流程详情视图对象，汇总任务表单、历史节点、流程 XML 与流程图执行轨迹等详情数据
 * @author yanbin
 * @date 2024/1/16 10:37
 */
@Data
public class WfDetailVo {

    /**
     * 任务表单信息
     */
    private WfTaskFormVo taskFormData;
    /**
     * 历史流程节点信息
     */
    private List<WfProcNodeVo> historyProcNodeList;

    /**
     * 流程表单列表
     */
    private List<WfTaskFormVo> processFormList;

    /**
     * 流程XML
     */
    private String bpmnXml;
    /**
     * 流程执行过程
     */
    private WfViewerVo flowViewer;
    /**
     * 节点表单数据
     */
    private List<Map<String, Object>> formData;
    /**
     * 流程状态   running运行中，finish结束
     */
    private String processStatus;
    /**
     * 获取当前任务信息  任务数量
     */
    private int currentTaskNum;
    /**
     * 当前节点权限
     */
    private String permission;

    /**
     * 获取当前任务信息
     */
    private WfUserTaskDto currentTask;
    /**
     * 意见记录
     */
    private List<WfCommentInfo> commentInfo;

    /**
     * 是否存在任务表单信息
     *
     * @return true:存在；false:不存在
     */
    public Boolean isExistTaskForm() {
        return ObjectUtil.isNotEmpty(this.taskFormData);
    }
}
