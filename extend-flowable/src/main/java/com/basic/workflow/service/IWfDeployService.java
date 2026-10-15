package com.basic.workflow.service;


import com.basic.common.core.domain.model.PageQuery;
import com.basic.common.core.page.TableDataInfo;
import com.basic.flowable.core.domain.ProcessQuery;
import com.basic.workflow.domain.vo.WfDeployVo;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (流程部署Service接口)
 * @date 2023/11/3 10:37
 */
public interface IWfDeployService {
    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfDeployVo>
     * @description TODO (查询流程部署列表)
     * @params [processQuery, pageQuery]
     * @author yanbin
     * @date 2024/1/23 11:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<WfDeployVo> queryPageList(ProcessQuery processQuery, PageQuery pageQuery);

    /**
     * @return com.basic.common.core.page.TableDataInfo<com.basic.workflow.domain.vo.WfDeployVo>
     * @description TODO (查询流程部署版本列表)
     * @params [processKey, pageQuery]
     * @author yanbin
     * @date 2024/1/23 11:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    TableDataInfo<WfDeployVo> queryPublishList(String processKey, PageQuery pageQuery);

    /**
     * @return void
     * @description TODO (/激活或挂起流程)
     * @params [definitionId, stateCode]
     * @author yanbin
     * @date 2024/1/23 11:28
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void updateState(String definitionId, String stateCode);

    /**
     * @return void
     * @description TODO (删除流程部署模型)
     * @params [deployIds]
     * @author yanbin
     * @date 2024/1/23 11:30
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void deleteByIds(List<String> deployIds);
}
