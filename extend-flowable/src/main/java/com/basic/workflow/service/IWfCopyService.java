package com.basic.workflow.service;


import com.basic.workflow.domain.bo.WfCopyBo;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.domain.vo.WfCopyVo;

import java.util.List;
import java.util.Map;

/**
 * @description 流程抄送 Service 接口，提供抄送记录的查询、生成、标记已阅与删除能力
 * @author yanbin
 * @date 2023/11/3 10:37
 */
public interface IWfCopyService {

    /**
     * @return WfCopyVo 抄送详情
     * @description 按抄送主键查询流程抄送详情
     * @params [copyId] 抄送记录主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    WfCopyVo queryById(Long copyId);

    /**
     * @return List<Map<String, Object>> 抄送记录列表，每行按字段名映射
     * @description 按查询条件分页查询抄送列表
     * @params [wfCopy] 抄送查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<Map<String, Object>> selectPageList(WfCopyBo wfCopy);

    /**
     * @return List<WfCopyVo> 抄送列表，不分页
     * @description 按查询条件查询抄送列表，不分页
     * @params [wfCopy] 抄送查询条件
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<WfCopyVo> selectList(WfCopyBo wfCopy);

    /**
     * @return Boolean 生成抄送成功返回 true
     * @description 按任务信息生成流程抄送记录
     * @params [taskBo] 任务业务对象，携带流程实例、抄送人等信息
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    Boolean makeCopy(WfTaskBo taskBo);

    /**
     * @description 将指定抄送记录标记为已阅
     * @params [id] 抄送记录主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void readCopy(Long id);

    /**
     * @description 按流程实例 ID 集合删除其历史抄送记录
     * @params [procInsIds] 流程实例 ID 集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void delProcessCopyInfo(List<String> procInsIds);
}
