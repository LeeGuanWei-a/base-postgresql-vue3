// 工作流边界事件（Boundary Event）触发接口
import request from '@/utils/request'

/**
 * 触发信号边界事件
 * @param {object} data { procInsId, eventCode(信号code), comment, variables }
 */
export function triggerBoundarySignal(data) {
  return request({
    url: '/workflow/boundary/signal',
    method: 'post',
    data: data
  })
}

/**
 * 触发消息边界事件
 * @param {object} data { procInsId, eventCode(消息id), comment, variables }
 */
export function triggerBoundaryMessage(data) {
  return request({
    url: '/workflow/boundary/message',
    method: 'post',
    data: data
  })
}

/**
 * 触发错误边界事件（标记流程变量，由 ServiceTask 抛 BpmnError）
 * @param {object} data { procInsId, eventCode(错误码), comment }
 */
export function triggerBoundaryError(data) {
  return request({
    url: '/workflow/boundary/error',
    method: 'post',
    data: data
  })
}

/**
 * 触发取消边界事件
 * @param {object} data { procInsId, comment }
 */
export function triggerBoundaryCancel(data) {
  return request({
    url: '/workflow/boundary/cancel',
    method: 'post',
    data: data
  })
}

/**
 * 查询流程定义上注册的 boundary 事件列表
 * @param {string} procInsId 流程实例id
 */
export function listBoundary(procInsId) {
  return request({
    url: '/workflow/boundary/list/' + procInsId,
    method: 'get'
  })
}
