package com.basic.workflow.mapper;

import com.basic.workflow.domain.vo.StartProcessInstanceVo;
import java.util.Map;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Repository;

/**
 * @description 流程定义的数据访问接口，按流程模型标识查询对应的流程定义信息
 * @author yanbin
 * @date 2025/01/07
 */
@Repository
public interface WfFlowableProcDefMapper {

  /**
   * @return Map<String, Object> 流程定义查询结果，按字段名映射
   * @description 按流程模型标识查询对应的流程定义信息
   * @params [startProcessInstanceVo] 发起流程参数，提供流程模型标识等查询条件
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Map<String, Object> selectProcDefByModelKey(StartProcessInstanceVo startProcessInstanceVo);

}