import request from '@/utils/request'
import { getToken } from '@/utils/auth'

/**
 * AgentScope Java 能力展厅 —— 后端接口封装。
 *
 * 约定：
 * - 通用能力接口：/agentscope/capability/{id}/chat | stream | detail
 * - 特殊能力接口：各能力自己的 Controller（见下方分节）
 */

// ---------------------------------------------------------------- 通用

/** 能力清单（后端能力注册表，前端左侧导航的数据源） */
export function listCapabilities() {
  return request({
    url: '/agentscope/capability/list',
    method: 'get'
  })
}

/** 通用同步对话 */
export function capabilityChat(id, data) {
  return request({
    url: `/agentscope/capability/${id}/chat`,
    method: 'post',
    data
  })
}

/** 通用能力详情 */
export function capabilityDetail(id) {
  return request({
    url: `/agentscope/capability/${id}/detail`,
    method: 'get'
  })
}

/**
 * 通用 SSE 流式对话。
 *
 * EventSource 不支持自定义请求头（Authorization），所以这里用 fetch + ReadableStream 手工解析 SSE。
 *
 * @param {string} id 能力 id
 * @param {object} params 查询参数，如 { prompt, sessionId }
 * @param {function} onMessage 每收到一条 data 消息回调
 * @param {AbortSignal} signal 用于中断
 */
export async function capabilityStream(id, params, onMessage, signal) {
  const query = Object.keys(params)
    .filter((k) => params[k] !== undefined && params[k] !== null && params[k] !== '')
    .map((k) => `${encodeURIComponent(k)}=${encodeURIComponent(params[k])}`)
    .join('&')
  const url = `${import.meta.env.VITE_APP_BASE_API}/agentscope/capability/${id}/stream?${query}`

  const response = await fetch(url, {
    method: 'GET',
    headers: {
      Authorization: 'Bearer ' + getToken()
    },
    signal
  })

  if (!response.ok || !response.body) {
    throw new Error(`流式请求失败：HTTP ${response.status}`)
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''

  for (;;) {
    const { done, value } = await reader.read()
    if (done) break
    buffer += decoder.decode(value, { stream: true })

    // SSE 以空行分隔事件
    let idx
    while ((idx = buffer.indexOf('\n\n')) !== -1) {
      const rawEvent = buffer.slice(0, idx)
      buffer = buffer.slice(idx + 2)
      const dataLines = rawEvent
        .split('\n')
        .filter((line) => line.startsWith('data:'))
        .map((line) => line.substring(5).trim())
      if (dataLines.length) {
        onMessage(dataLines.join('\n'))
      }
    }
  }
}

// ---------------------------------------------------------------- 特殊能力

/** 结构化输出 */
export function structuredCall(data) {
  return request({
    url: '/agentscope/structured/call',
    method: 'post',
    data
  })
}

/** 会话列表 */
export function sessionList() {
  return request({
    url: '/agentscope/session/list',
    method: 'get'
  })
}

/** 清除某个会话 */
export function sessionRemove(sessionId) {
  return request({
    url: `/agentscope/session/${encodeURIComponent(sessionId)}`,
    method: 'delete'
  })
}

/** RAG 新增知识 */
export function ragIngest(data) {
  return request({
    url: '/agentscope/rag/ingest',
    method: 'post',
    data
  })
}

/** RAG 知识列表 */
export function ragList() {
  return request({
    url: '/agentscope/rag/list',
    method: 'get'
  })
}

/** RAG 清空知识库 */
export function ragClear() {
  return request({
    url: '/agentscope/rag/clear',
    method: 'delete'
  })
}

/**
 * 工具分组：启用 / 停用某个分组。
 *
 * 查询参数直接拼进 URL —— `request.js` 的防重复提交是按 `url + data` 做键的，
 * 若把参数放在 `params` 里，1 秒内切换不同分组会被误判为重复提交。
 */
export function toolGroupToggle(group, enabled) {
  return request({
    url: `/agentscope/tool-group/toggle?group=${encodeURIComponent(group)}&enabled=${enabled}`,
    method: 'post'
  })
}

/**
 * 中断能力专属对话入口。
 *
 * 与通用 `/agentscope/capability/interrupt/chat` 的区别：这里会先
 * `registerSession(sessionId, agent)`，否则 `interrupt()` 找不到受控 Agent。
 * body: { prompt, sessionId, options: { seconds } }
 */
export function interruptChat(data) {
  return request({
    url: '/agentscope/interrupt/chat',
    method: 'post',
    data,
    timeout: 180000
  })
}

/** 查询权限模式 */
export function permissionMode() {
  return request({
    url: '/agentscope/permission/mode',
    method: 'get'
  })
}

/** 设置权限模式 */
export function permissionSetMode(data) {
  return request({
    url: '/agentscope/permission/mode',
    method: 'post',
    data
  })
}

/** 中断运行中的 Agent */
export function interruptStop(sessionId) {
  return request({
    url: '/agentscope/interrupt/stop',
    method: 'post',
    params: { sessionId }
  })
}

/** 查询中断状态 */
export function interruptStatus(sessionId) {
  return request({
    url: '/agentscope/interrupt/status',
    method: 'get',
    params: { sessionId }
  })
}

/** 可选模型厂商 */
export function modelProviders() {
  return request({
    url: '/agentscope/model/providers',
    method: 'get'
  })
}

/** 指定厂商对话 */
export function modelChat(data) {
  return request({
    url: '/agentscope/model/chat',
    method: 'post',
    data
  })
}

/** MCP 状态 */
export function mcpStatus() {
  return request({
    url: '/agentscope/mcp/status',
    method: 'get'
  })
}

/** MCP 重新加载 */
export function mcpReload() {
  return request({
    url: '/agentscope/mcp/reload',
    method: 'post'
  })
}

/** 渠道状态 */
export function channelStatus() {
  return request({
    url: '/agentscope/channel/status',
    method: 'get'
  })
}

/** 沙箱状态 */
export function sandboxStatus() {
  return request({
    url: '/agentscope/sandbox/status',
    method: 'get'
  })
}

/** 沙箱执行 */
export function sandboxExecute(data) {
  return request({
    url: '/agentscope/sandbox/execute',
    method: 'post',
    data
  })
}

/** 调度器状态 */
export function schedulerStatus() {
  return request({
    url: '/agentscope/scheduler/status',
    method: 'get'
  })
}

/** 新增调度任务 */
export function schedulerAddTask(data) {
  return request({
    url: '/agentscope/scheduler/task',
    method: 'post',
    data
  })
}

/** 删除调度任务 */
export function schedulerRemoveTask(name) {
  return request({
    url: `/agentscope/scheduler/task/${encodeURIComponent(name)}`,
    method: 'delete'
  })
}
