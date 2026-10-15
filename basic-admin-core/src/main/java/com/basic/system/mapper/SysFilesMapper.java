package com.basic.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basic.system.domain.SysFiles;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 附件 数据层
 */
@Mapper
public interface SysFilesMapper extends BaseMapper<SysFiles> {

    /**
     * 根据流程实例ID查询附件列表
     */
    List<SysFiles> getListByProcessInstanceId(String processInstanceId);

    /**
     * 根据绝对路径获取附件
     */
    SysFiles getFilesByAbsolutePath(String absolutePath);
}
