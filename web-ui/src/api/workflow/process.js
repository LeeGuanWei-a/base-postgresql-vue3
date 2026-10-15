import request from '@/utils/request'

// ========== 我的待办 ==========
export function todoList(query) {
  return request({
    url: '/workflow/process/todoList',
    method: 'get',
    params: query
  })
}

// ========== 我的已办 ==========
export function finishedList(query) {
  return request({
    url: '/workflow/process/finishedList',
    method: 'get',
    params: query
  })
}

// ========== 我的已发 ==========
export function ownList(query) {
  return request({
    url: '/workflow/process/ownList',
    method: 'get',
    params: query
  })
}

// ========== 抄送我的 ==========
export function copyList(query) {
  return request({
    url: '/workflow/process/copyList',
    method: 'get',
    params: query
  })
}

// ========== 待签收 ==========
export function claimList(query) {
  return request({
    url: '/workflow/process/claimList',
    method: 'get',
    params: query
  })
}

// ========== 流程中心：按流程状态查实例列表 ==========
// 后端 /workflow/process/selectProcessPrcInstance 走 Flowable 的 HistoricProcessInstanceQuery：
//   status = running   → unfinished()，只返回未结束的实例
//   status = completed → finished()，已结束的实例
//   status = canceled  → finished() + 流程状态变量 = canceled（终止过的实例）
export function centerProcessList(query, status = 'running') {
  return request({
    url: '/workflow/process/selectProcessPrcInstance',
    method: 'get',
    params: { ...query, processStatus: status }
  })
}

// ========== 流程详情（taskId 可选：传了才返回 currentTask） ==========
export function getProcessDetail(procInsId, taskId) {
  return request({
    url: '/workflow/process/detail',
    method: 'get',
    params: { procInsId, taskId }
  })
}

// ========== 流程图 XML ==========
export function getProcessBpmnXml(processDefId) {
  return request({
    url: '/workflow/process/bpmnXml/' + processDefId,
    method: 'get'
  })
}

// ========== 流程图 JSON（用于 viewer） ==========
export function getFlowViewer(procInsId) {
  return request({
    url: '/workflow/process/getFlowViewer/' + procInsId,
    method: 'get'
  })
}

// ========== 流程历史节点 ==========
export function getHistoryProcNodeList(procInsId) {
  return request({
    url: '/workflow/process/historyProcNodeList/' + procInsId,
    method: 'get'
  })
}

// ========== 流程审批记录 ==========
export function getProcessCommentInfo(procInsId) {
  return request({
    url: '/workflow/process/getProcessCommentInfo/' + procInsId,
    method: 'get'
  })
}

// ========== 流程当前任务 ==========
export function getCurrentTaskInfo(procInsId) {
  return request({
    url: '/workflow/process/getCurrentTaskInfo/' + procInsId,
    method: 'get'
  })
}

// ========== 流程变量 ==========
export function getProcessVariables(procInsId) {
  return request({
    url: '/workflow/process/getProcessVariables/' + procInsId,
    method: 'get'
  })
}

// ========== 任务变量 ==========
export function getTaskVariables(taskId) {
  return request({
    url: '/workflow/process/getTaskVariables/' + taskId,
    method: 'get'
  })
}

// ========== 发起流程：获取待发起模型信息 ==========
export function getTodoModel(deploymentId) {
  return request({
    url: '/workflow/process/getTodoModel',
    method: 'get',
    params: { deploymentId }
  })
}

// ========== 流程是否已结束 ==========
export function checkFlowIsEnd(procInsId) {
  return request({
    url: '/workflow/process/checkFlowIsEnd/' + procInsId,
    method: 'get'
  })
}

// ========== 抄送已读 ==========
export function readCopy(data) {
  return request({
    url: '/workflow/process/readCopy',
    method: 'post',
    data: data
  })
}
