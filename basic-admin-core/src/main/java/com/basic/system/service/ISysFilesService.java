package com.basic.system.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.basic.system.domain.SysConfig;
import com.basic.system.domain.SysFiles;

import java.util.List;

/**
 * 附件 业务层
 * 
 * @author basic
 */
public interface ISysFilesService extends IService<SysFiles>
{


    /**
     * @return List<SysFiles> 该流程实例的附件列表，当前默认实现返回空列表
     * @description 按流程实例 id 查询附件列表，供工作流模块读取流程附件
     * @params [processInstanceId] 流程实例 id
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    default List<SysFiles> getListByProcessInstanceId(String processInstanceId) {
        return java.util.Collections.emptyList();
    }

}
