package com.basic.workflow.mapper;

import com.basic.workflow.domain.vo.StartProcessInstanceVo;
import java.util.Map;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Repository;

/**
 * @author yanbin
 * @description TODO (flowable 流程定义mapper)
 * @date 2025/01/07
 */
@Repository
public interface WfFlowableProcDefMapper {

  /**
   * @description TODO (查询流程定义)
   * @params startProcessInstanceVo
   * @return org.flowable.engine.repository.ProcessDefinition
   * @author yanbin
   * @date 2025/1/7 17:37
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  Map<String, Object> selectProcDefByModelKey(StartProcessInstanceVo startProcessInstanceVo);

}