package com.basic.workflow.service;


import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.workflow.domain.vo.WfDeployVo;

import java.util.List;

/**
 * @description 流程部署服务接口，提供部署记录分页查询、版本查询与部署状态维护
 * @author yanbin
 * @date 2023/11/3 10:37
 */
public interface IWfDeployService {
    /**
     * @return TableDataInfo<WfDeployVo> 分页后的流程部署列表
     * @description 按流程查询条件分页查询流程部署记录，返回带总数的表格数据
     * @params [processQuery, pageQuery] processQuery 流程查询条件；pageQuery 分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<WfDeployVo> queryPageList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return TableDataInfo<WfDeployVo> 分页后的流程部署版本列表
     * @description 按流程标识分页查询该流程的所有部署版本记录
     * @params [processKey, pageQuery] processKey 流程标识；pageQuery 分页参数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<WfDeployVo> queryPublishList(String processKey, PageQuery pageQuery);

    /**
     * @description 按流程定义id把流程置为激活或挂起，挂起后该定义下的任务不能再被办理
     * @params [definitionId, stateCode] definitionId 流程定义id；stateCode 目标状态编码，激活或挂起
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void updateState(String definitionId, String stateCode);

    /**
     * @description 按部署id集合级联删除流程部署记录及其关联的流程定义
     * @params [deployIds] 要删除的流程部署id集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteByIds(List<String> deployIds);
}
