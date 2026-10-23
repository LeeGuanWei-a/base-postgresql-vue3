package com.basic.workflow.service.wfProcessTask.claim;

import com.basic.common.exception.ServiceException;
import com.basic.workflow.domain.bo.WfTaskBo;
import com.basic.workflow.service.wfProcessTask.base.WfProcessTaskBase;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @description 任务签收服务，负责把待签任务签收给当前用户或撤销签收
 * @author yanbin
 * @date 2024/03/01
 */
@Service
public class WfClaimTaskService extends WfProcessTaskBase {
    /**
     * @return boolean 签收完成即返回 true
     * @description 把任务签收给当前用户，任务不存在时抛出业务异常
     * @params [taskBo] 任务业务对象，含任务主键与当前用户主键
     * @author liguanwei
     * @date 2026/10/17 20:52
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
     * @return boolean 操作完成即返回 true
     * @description 撤销当前用户对该任务的签收
     * @params [bo] 任务业务对象，含任务主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public boolean unClaim(WfTaskBo bo) {
        taskService.unclaim(bo.getTaskId());
        return true;
    }
}
