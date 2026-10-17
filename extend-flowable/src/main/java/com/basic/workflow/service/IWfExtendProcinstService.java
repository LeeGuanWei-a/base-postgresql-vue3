package com.basic.workflow.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.basic.workflow.domain.ExtendProcinst;

import java.util.List;

/**
 * @description 流程实例业务拓展 Service 接口，维护流程实例与业务数据的关联记录及其历史数据
 * @author yanbin
 * @date 2023/10/26
 */
public interface IWfExtendProcinstService extends IService<ExtendProcinst> {
    /**
     * @description 保存流程实例启动时的业务拓展信息，并同步写一份历史记录
     * @params [extendProcinst] 流程实例的业务拓展信息
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void saveExtendProcinstAndHis(ExtendProcinst extendProcinst);

    /**
     * @return ExtendProcinst 该实例启动时保存的拓展信息，无记录时为 null
     * @description 按流程实例 ID 查询实例启动时保存的业务拓展信息
     * @params [processInstanceId] 流程实例 ID
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    ExtendProcinst selectProcInstanId(String processInstanceId);

    /**
     * @description 按流程实例 ID 集合批量删除这些实例的业务拓展信息
     * @params [procInsIds] 要清理拓展信息的流程实例 ID 集合
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    void delProcessExtend(List<String> procInsIds);
}
