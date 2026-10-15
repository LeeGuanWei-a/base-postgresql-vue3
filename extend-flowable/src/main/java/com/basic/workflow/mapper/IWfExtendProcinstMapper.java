package com.basic.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.basic.workflow.domain.ExtendProcinst;
import org.springframework.stereotype.Repository;

/**
 * @author yanbin
 * @description TODO (启动实例记录(历史启动))--》流程业务拓展
 * @date 2023/10/26
 */
@Repository
public interface IWfExtendProcinstMapper extends BaseMapper<ExtendProcinst> {
}
