package com.basic.workflow.service;


import com.basic.workflow.domain.bo.WfCopyBo;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.vo.WfCopyVo;

import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (流程抄送Service接口)
 * @date 2023/11/3 10:37
 */
public interface IWfCopyService {

    /**
     * @return com.basic.workflow.domain.vo.WfCopyVo
     * @description TODO (查询流程抄送)
     * @params [copyId]
     * @author yanbin
     * @date 2024/1/23 11:33
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfCopyVo queryById(Long copyId);

    /**
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @description TODO (查询流程抄送列表)
     * @params [wfCopy]
     * @author yanbin
     * @date 2024/1/23 11:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<Map<String, Object>> selectPageList(WfCopyBo wfCopy);

    /**
     * @return java.util.List<com.basic.workflow.domain.vo.WfCopyVo>
     * @description TODO (查询流程抄送列表)
     * @params [wfCopy]
     * @author yanbin
     * @date 2024/1/23 11:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCopyVo> selectList(WfCopyBo wfCopy);

    /**
     * @return java.lang.Boolean
     * @description TODO (抄送)
     * @params [taskBo]
     * @author yanbin
     * @date 2024/1/23 11:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Boolean makeCopy(WfTaskBo taskBo);

    /**
     * @return void
     * @description TODO (标记抄送为已阅)
     * @params [id]
     * @author yanbin
     * @date 2024/1/23 11:34
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void readCopy(Long id);

    /**
     * @return void
     * @description TODO (根据实例id删除历史抄送)
     * @params [procInsIds]
     * @author yanbin
     * @date 2024/1/17 17:57
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void delProcessCopyInfo(List<String> procInsIds);
}
