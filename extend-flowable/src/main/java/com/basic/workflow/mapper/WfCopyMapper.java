package com.basic.workflow.mapper;


import com.basic.common.core.mapper.BaseMapperPlus;
import com.basic.workflow.domain.WfCopy;
import com.basic.workflow.domain.bo.WfCopyBo;
import com.basic.workflow.domain.vo.WfCopyVo;

import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (流程抄送Mapper接口)
 * @date 2023/11/3 10:39
 */
public interface WfCopyMapper extends BaseMapperPlus<WfCopyMapper, WfCopy, WfCopyVo> {
    /**
     * @description TODO (查询流程抄送列表)
     * @params [bo]
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @author yanbin
     * @date 2024/1/16 10:44
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<Map<String, Object>> selectPageList(WfCopyBo bo);
    /**
     * @description TODO (查询流程抄送列表)
     * @params [bo]
     * @return java.util.List<com.basic.workflow.domain.vo.WfCopyVo>
     * @author yanbin
     * @date 2024/5/13 10:09
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCopyVo> selectCopyList(WfCopyBo bo);
}
