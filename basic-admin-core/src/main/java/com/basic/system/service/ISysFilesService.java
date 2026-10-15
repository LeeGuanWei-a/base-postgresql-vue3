package com.basic.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.system.domain.SysFiles;
import com.basic.system.domain.vo.SysFilesVo;

import java.util.List;

/**
 * 附件 业务层
 *
 * @author yanbin
 * @date 2023/11/13
 * @description 流程引擎模块（extend-flowable）依赖此接口查询附件，
 *              集成时必须实现所有方法，否则编译失败
 */
public interface ISysFilesService extends IService<SysFiles> {

    /**
     * 根据流程实体id查询附件列表
     *
     * @param processInstanceId 流程实例ID
     * @return 附件列表
     */
    public List<SysFiles> getListByProcessInstanceId(String processInstanceId);

    /**
     * 通过文件Id集合返回前端文件上传组件所需数据格式
     */
    public List<SysFilesVo> getListForFont(String[] fileIds);

    /**
     * 根据实例id和节点id返回前端文件上传组件所需数据格式
     */
    public List<SysFilesVo> getFilesByProcInsIdAndActivityId(String procInsId, String activityId);

    /**
     * 根据实例id和节点id返回附件
     */
    public List<SysFiles> getFileByProcInsIdAndActivityId(String procInsId, String activityId);

    /**
     * 根据附件id集合查附件
     */
    public List<SysFiles> getFilesByStringIds(String[] fileIds);

    /**
     * 根据绝对路径获取附件
     */
    SysFiles getFilesByAbsolutePath(String absolutePath);
}
