package com.basic.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.basic.workflow.domain.ExtendProcinst;

import java.util.List;

/**
 * @author yanbin
 * @description TODO (工作流流程业务拓展表) 流程 业务关联
 * @date 2023/10/26
 */
public interface IWfExtendProcinstService extends IService<ExtendProcinst> {
    void saveExtendProcinstAndHis(ExtendProcinst extendProcinst);

    /**
     * @return com.basic.workflow.domain.ExtendProcinst
     * @description TODO (查询 实例启动时保存信息)
     * @params [processInstanceId]
     * @author yanbin
     * @date 2023/11/17 16:32
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    ExtendProcinst selectProcInstanId(String processInstanceId);

    /**
     * @return void
     * @description TODO (删除历史流程业务拓展信息)
     * @params [procInsIds]
     * @author yanbin
     * @date 2024/1/19 15:47
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void delProcessExtend(List<String> procInsIds);
}
