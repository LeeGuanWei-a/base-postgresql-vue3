package com.basic.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.basic.common.constant.Constants;
import com.basic.common.utils.StringUtils;
import com.basic.system.domain.SysFiles;
import com.basic.system.domain.vo.SysFilesVo;
import com.basic.system.mapper.SysFilesMapper;
import com.basic.system.service.ISysFilesService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 附件 服务层实现
 *
 * @author yanbin
 * @date 2023/11/13
 * @description 提供 extend-flowable 所需的 6 个查询方法
 */
@Service
public class SysFilesServiceImpl extends ServiceImpl<SysFilesMapper, SysFiles> implements ISysFilesService {

    /**
     * 根据流程实例ID查询附件列表
     * 注：数据库列名 process_instance_id 通过 MyBatis-Plus 默认下划线转驼峰映射
     */
    @Override
    public List<SysFiles> getListByProcessInstanceId(String processInstanceId) {
        if (StringUtils.isBlank(processInstanceId)) {
            return new ArrayList<>();
        }
        return baseMapper.getListByProcessInstanceId(processInstanceId);
    }

    /**
     * 通过文件Id集合返回前端文件上传组件所需数据格式
     */
    @Override
    public List<SysFilesVo> getListForFont(String[] fileIds) {
        List<SysFilesVo> rList = new ArrayList<>();
        if (fileIds == null || fileIds.length == 0 || StringUtils.isBlank(fileIds[0])) {
            return rList;
        }
        List<Long> list = Arrays.stream(fileIds).map(Long::valueOf).collect(Collectors.toList());
        List<SysFiles> sysFilesList = this.lambdaQuery().in(SysFiles::getId, list).eq(SysFiles::getIsDel, "0").list();
        for (SysFiles sysFiles : sysFilesList) {
            SysFilesVo sysFilesVo = new SysFilesVo();
            sysFilesVo.setId(sysFiles.getId());
            sysFilesVo.setUrl(Constants.RESOURCE_PREFIX + sysFiles.getRelativePath());
            sysFilesVo.setName(sysFiles.getName());
            sysFilesVo.setPicUrl(Constants.RESOURCE_PREFIX + sysFiles.getRelativePath());
            sysFilesVo.setCreateTime(sysFiles.getCreateTime());
            rList.add(sysFilesVo);
        }
        return rList;
    }

    /**
     * 根据实例ID和节点ID返回前端文件上传组件所需数据格式
     */
    @Override
    public List<SysFilesVo> getFilesByProcInsIdAndActivityId(String procInsId, String activityId) {
        List<SysFilesVo> rList = new ArrayList<>();
        if (StringUtils.isBlank(procInsId) || StringUtils.isBlank(activityId)) {
            return rList;
        }
        List<SysFiles> sysFilesList = this.lambdaQuery()
                .eq(SysFiles::getActivityId, activityId)
                .eq(SysFiles::getProcessInstanceId, procInsId)
                .eq(SysFiles::getIsDel, "0")
                .list();
        for (SysFiles sysFiles : sysFilesList) {
            SysFilesVo sysFilesVo = new SysFilesVo();
            sysFilesVo.setId(sysFiles.getId());
            sysFilesVo.setUrl(Constants.RESOURCE_PREFIX + sysFiles.getRelativePath());
            sysFilesVo.setName(sysFiles.getName());
            sysFilesVo.setPicUrl(Constants.RESOURCE_PREFIX + sysFiles.getRelativePath());
            sysFilesVo.setCreateTime(sysFiles.getCreateTime());
            rList.add(sysFilesVo);
        }
        return rList;
    }

    /**
     * 根据实例ID和节点ID返回附件
     */
    @Override
    public List<SysFiles> getFileByProcInsIdAndActivityId(String procInsId, String activityId) {
        if (StringUtils.isBlank(procInsId) || StringUtils.isBlank(activityId)) {
            return new ArrayList<>();
        }
        return this.lambdaQuery()
                .eq(SysFiles::getActivityId, activityId)
                .eq(SysFiles::getProcessInstanceId, procInsId)
                .eq(SysFiles::getIsDel, "0")
                .list();
    }

    /**
     * 根据附件ID集合查附件
     */
    @Override
    public List<SysFiles> getFilesByStringIds(String[] fileIds) {
        if (fileIds == null || fileIds.length == 0) {
            return new ArrayList<>();
        }
        List<Long> list = Arrays.stream(fileIds).map(Long::valueOf).collect(Collectors.toList());
        return this.lambdaQuery().in(SysFiles::getId, list).eq(SysFiles::getIsDel, "0").list();
    }

    /**
     * 根据绝对路径获取附件
     */
    @Override
    public SysFiles getFilesByAbsolutePath(String absolutePath) {
        if (StringUtils.isBlank(absolutePath)) {
            return null;
        }
        return baseMapper.getFilesByAbsolutePath(absolutePath);
    }
}
