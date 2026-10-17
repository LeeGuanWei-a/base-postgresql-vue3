package com.basic.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basic.workflow.domain.ExtendProcinst;
import org.springframework.stereotype.Repository;

/**
 * @description 流程业务拓展实体 Mapper，负责流程实例扩展信息在 tbl_flow_extend_procinst 中的读写
 * @author yanbin
 * @date 2023/10/26
 */
@Repository
public interface IWfExtendProcinstMapper extends BaseMapper<ExtendProcinst> {
}
