package com.basic.workflow.service.wfProcessTask.claim;

import com.basic.common.exception.ServiceException;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author yanbin
 * @description TODO (floable签收任务)
 * @date 2024/03/01
 */
@Service
public class WfClaimTaskService extends WfProcessTaskBase {
    /**
     * @return boolean
     * @description TODO (认领/签收任务)
     * @params [taskBo]
     * @author yanbin
     * @date 2024/3/4 8:41
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean claim(WfTaskBo taskBo) {
        Task task = taskService.createTaskQuery().taskId(taskBo.getTaskId()).singleResult();
        if (Objects.isNull(task)) {
            throw new ServiceException("任务不存在");
        }
        taskService.claim(taskBo.getTaskId(), taskBo.getCurrentUserId());
        return true;
    }

    /**
     * @return boolean
     * @description TODO (取消认领/签收任务)
     * @params [bo]
     * @author yanbin
     * @date 2024/3/4 11:50
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean unClaim(WfTaskBo bo) {
        taskService.unclaim(bo.getTaskId());
        return true;
    }
}
