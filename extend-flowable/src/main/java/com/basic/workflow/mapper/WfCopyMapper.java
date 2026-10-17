package com.basic.workflow.mapper;


import com.basic.common.core.mapper.BaseMapperPlus;
import com.basic.workflow.domain.WfCopy;
import com.basic.workflow.domain.bo.WfCopyBo;
import com.basic.workflow.domain.vo.WfCopyVo;

import java.util.List;
import java.util.Map;

/**
 * @description 流程抄送 Mapper 接口，提供抄送记录的分页查询与视图对象查询
 * @author yanbin
 * @date 2023/11/3 10:39
 */
public interface WfCopyMapper extends BaseMapperPlus<WfCopyMapper, WfCopy, WfCopyVo> {
    /**
     * @return List<Map<String, Object>> 抄送记录的分页列表，每行以字段名为 key
     * @description 按查询条件分页查询流程抄送记录列表
     * @params [bo] 抄送查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<Map<String, Object>> selectPageList(WfCopyBo bo);
    /**
     * @return List<WfCopyVo> 抄送视图对象列表，按查询条件过滤
     * @description 按查询条件查询流程抄送记录并直接返回视图对象列表
     * @params [bo] 抄送查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCopyVo> selectCopyList(WfCopyBo bo);
}
