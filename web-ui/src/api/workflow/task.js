import request from '@/utils/request'
import useUserStore from '@/store/modules/user'

/**
 * 统一注入当前登录用户 id / 用户名。
 * 后端 WfTaskBo 依赖这两个字段：
 *   currentUserId   —— 记录操作人（审批/签收/转办/委派/退回/终止等）
 *   currentUserName —— 拼审批意见用（转办/委派会拼成 "张三->李四"），
 *                      缺了会让后端 new StringBuilder(null) 抛 NPE
 */
function withUser(data) {
  let userId = ''
  let userName = ''
  try {
    const store = useUserStore()
    userId = store.id
    userName = store.nickName || store.name || ''
  } catch (e) {
    userId = ''
    userName = ''
  }
  // 调用方显式传入的值优先
  return { currentUserId: userId, currentUserName: userName, ...(data || {}) }
}

// ========== 任务操作（基础） ==========

// 完成任务（同意）
export function completeTask(data) {
  return request({
    url: '/workflow/task/complete',
    method: 'post',
    data: withUser(data)
  })
}

// 拒绝任务
export function rejectTask(data) {
  return request({
    url: '/workflow/task/reject',
    method: 'post',
    data: withUser(data)
  })
}

// 退回任务
export function returnTask(data) {
  return request({
    url: '/workflow/task/return',
    method: 'post',
    data: withUser(data)
  })
}

// 多实例顺序回退
export function mulitiSequentialUnComplete(data) {
  return request({
    url: '/workflow/task/mulitiSequentialUnComplete',
    method: 'post',
    data: withUser(data)
  })
}

// 委派任务
export function delegateTask(data) {
  return request({
    url: '/workflow/task/delegate',
    method: 'post',
    data: withUser(data)
  })
}

// 转办任务
export function transferTask(data) {
  return request({
    url: '/workflow/task/transfer',
    method: 'post',
    data: withUser(data)
  })
}

// 签收任务
export function claimTask(data) {
  return request({
    url: '/workflow/task/claim',
    method: 'post',
    data: withUser(data)
  })
}

// 取消签收
export function unClaimTask(data) {
  return request({
    url: '/workflow/task/unClaim',
    method: 'post',
    data: withUser(data)
  })
}

// 加签（前加签/后加签）
export function beforeAddSignTask(data) {
  return request({
    url: '/workflow/task/beforeAddSignTask',
    method: 'post',
    data: withUser(data)
  })
}
export function afterAddSignTask(data) {
  return request({
    url: '/workflow/task/afterAddSignTask',
    method: 'post',
    data: withUser(data)
  })
}

// 终止流程
export function stopProcess(data) {
  return request({
    url: '/workflow/task/stopProcess',
    method: 'post',
    data: withUser(data)
  })
}

// 撤回流程
export function revokeProcess(data) {
  return request({
    url: '/workflow/task/revokeProcess',
    method: 'post',
    data: withUser(data)
  })
}

// 废除流程
export function abolishProcess(data) {
  return request({
    url: '/workflow/task/abolishProcess',
    method: 'post',
    data: withUser(data)
  })
}

// 删除流程任务
export function deleteProcessTask(data) {
  return request({
    url: '/workflow/task/deleteProcessTask',
    method: 'post',
    data: withUser(data)
  })
}

// 修改任务处理人
export function editProcessTaskAssignee(data) {
  return request({
    url: '/workflow/task/editProcessTaskAssignee',
    method: 'post',
    data: withUser(data)
  })
}

// 跳过节点
export function skipUserTask(data) {
  return request({
    url: '/workflow/task/skipUserTask',
    method: 'post',
    data: withUser(data)
  })
}

// 驳回到上一节点
export function rejectLastTask(data) {
  return request({
    url: '/workflow/task/rejectLastTask',
    method: 'post',
    data: withUser(data)
  })
}

// 删除流程实例
export function deleteProcessByIds(procInsIds) {
  return request({
    url: '/workflow/task/deleteProcessByIds/' + procInsIds,
    method: 'delete'
  })
}

// 更新任务评论
export function updateTaskComment(data) {
  return request({
    url: '/workflow/task/updateTaskComment',
    method: 'post',
    data: withUser(data)
  })
}

// 获取当前任务节点
export function getCurrentTaskNode(data) {
  return request({
    url: '/workflow/task/getCurrentTaskNode',
    method: 'post',
    data: withUser(data)
  })
}

// 查询下一节点
export function getNextNode(data) {
  return request({
    url: '/workflow/task/getNextNode',
    method: 'post',
    data: withUser(data)
  })
}

// 流程图 svg
export function getDiagram(procInsId) {
  return request({
    url: '/workflow/task/diagram/' + procInsId,
    method: 'get'
  })
}

// 检查当前用户是否为审批人
export function checkIsAuditUser(data) {
  return request({
    url: '/workflow/task/checkIsAuditUser',
    method: 'post',
    data: withUser(data)
  })
}

// 检查当前用户是否为发起人
export function checkIsStartUser(data) {
  return request({
    url: '/workflow/task/checkIsStartUser',
    method: 'post',
    data: withUser(data)
  })
}

/**
 * 查询当前任务「可退回的节点」列表。
 * 后端 WfReturnTaskService#findReturnTaskList 的筛选逻辑：
 * 该实例中已办过的用户任务，且能（顺序）到达当前节点 —— 即真正可以退回去的节点。
 * 返回 List<FlowElement>，前端只取 id / name。
 */
export function returnList(data) {
  return request({
    url: '/workflow/task/returnList',
    method: 'post',
    data: withUser(data)
  })
}
