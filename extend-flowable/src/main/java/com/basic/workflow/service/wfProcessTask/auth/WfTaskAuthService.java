package com.basic.workflow.service.wfProcessTask.auth;

import cn.hutool.core.util.ObjectUtil;
import com.basic.common.exception.ServiceException;
import com.basic.common.utils.SecurityUtils;
import com.basic.common.utils.StringUtils;
import org.flowable.engine.TaskService;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 任务操作权限校验。
 *
 * <p>背景：各任务动作接口（同意/驳回/退回/转办/委派/加签/会签回退/签收）此前只校验
 * 「菜单权限」（{@code @PreAuthorize}），<b>不校验这个任务是不是你的</b>。
 * 只要拿到 taskId，任何人都能审批任何任务；再加上待办列表此前没按用户过滤，
 * 问题会被放大。</p>
 *
 * <p>本校验允许以下身份操作任务：</p>
 * <ol>
 *   <li>任务处理人 assignee —— 正常办理人</li>
 *   <li>任务所有者 owner —— 转办/委派后原办理人会保留 owner（用于取回、委派人确认等）</li>
 *   <li>候选人 candidate user —— 待签收任务</li>
 *   <li>候选角色 candidate group —— 当前用户的角色命中候选角色</li>
 * </ol>
 *
 * <p>说明：在非 Web 请求线程（拿不到登录用户）下<b>放行并记录告警</b>，
 * 避免影响内部/定时调用；Web 请求一律严格校验。</p>
 */
@Component
public class WfTaskAuthService {

    private static final Logger log = LoggerFactory.getLogger(WfTaskAuthService.class);

    /**
     * 系统内部调用标记。
     *
     * <p>用于"代表他人推进流程"的场景（如流程替换的镜像推进、定时任务批量处理）：
     * 此时当前登录用户不是任务的处理人/候选人，但操作是系统行为，应当放行。</p>
     *
     * <p><b>安全性</b>：这是 {@link ThreadLocal}，只能由服务端代码设置，
     * <b>HTTP 请求体无法设置它</b>（{@code WfTaskBo} 里没有对应字段），
     * 因此不会成为绕过权限的入口。</p>
     */
    private static final ThreadLocal<Boolean> SYSTEM_INVOKE = new ThreadLocal<>();

    /**
     * 系统内部调用的动作，可抛异常。
     */
    @FunctionalInterface
    public interface SystemCallable<T> {
        T call() throws Exception;
    }

    /**
     * 以"系统身份"执行一段逻辑：执行期间跳过任务操作权限校验。
     *
     * <p>示例：</p>
     * <pre>
     * WfTaskAuthService.callAsSystem(() -&gt; {
     *     wfTaskService.complete(bo);
     *     return null;
     * });
     * </pre>
     */
    public static <T> T callAsSystem(SystemCallable<T> action) throws Exception {
        Boolean previous = SYSTEM_INVOKE.get();
        SYSTEM_INVOKE.set(Boolean.TRUE);
        try {
            return action.call();
        } finally {
            if (previous == null) {
                SYSTEM_INVOKE.remove();
            } else {
                SYSTEM_INVOKE.set(previous);
            }
        }
    }

    @Autowired
    private TaskService taskService;

    /**
     * 校验当前登录用户是否有权操作指定任务，无权时抛 {@link ServiceException}。
     *
     * @param taskId 任务id
     */
    public void checkOperateAuth(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            throw new ServiceException("任务id不能为空");
        }
        // 系统内部调用（流程替换镜像推进等）直接放行
        if (Boolean.TRUE.equals(SYSTEM_INVOKE.get())) {
            log.info("系统内部调用，跳过任务操作权限校验。taskId={}", taskId);
            return;
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ServiceException("任务不存在或已结束，请刷新后重试");
        }
        // 当前登录用户及其角色
        final String currentUserId;
        final Set<String> myRoleIds = new HashSet<>();
        try {
            currentUserId = String.valueOf(SecurityUtils.getUserId());
            if (SecurityUtils.getLoginUser().getUser().getRoles() != null) {
                SecurityUtils.getLoginUser().getUser().getRoles()
                        .forEach(role -> myRoleIds.add(String.valueOf(role.getRoleId())));
            }
        } catch (Exception e) {
            // 非请求线程（内部调用/定时任务）没有登录上下文：放行，但留下痕迹
            log.warn("获取当前登录用户失败，跳过任务操作权限校验。taskId={}, 原因={}", taskId, e.getMessage());
            return;
        }
        // 1. 处理人
        if (currentUserId.equals(task.getAssignee())) {
            return;
        }
        // 2. 所有者（转办/委派场景）
        if (currentUserId.equals(task.getOwner())) {
            return;
        }
        // 3. 候选人 / 候选角色
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
        if (ObjectUtil.isNotEmpty(identityLinks)) {
            for (IdentityLink link : identityLinks) {
                if (!"candidate".equals(link.getType())) {
                    continue;
                }
                if (currentUserId.equals(link.getUserId())) {
                    return;
                }
                if (StringUtils.isNotBlank(link.getGroupId()) && myRoleIds.contains(link.getGroupId())) {
                    return;
                }
            }
        }
        // 都不是：拒绝
        log.warn("越权操作任务被拦截。taskId={}, 操作人={}, 任务处理人={}, 任务所有者={}",
                taskId, currentUserId, task.getAssignee(), task.getOwner());
        throw new ServiceException("您不是该任务的办理人或候选人，无权操作该任务");
    }
}
