// 工作流流程替换（运行中实例迁移到新流程定义）接口
import request from '@/utils/request'

/**
 * 触发流程替换
 * @param {object} data
 *   {
 *     oldProcInsId, newProcessDefinitionKey, startVariables, copyComments,
 *     copyAttachments, reason, advanceSteps: [{ taskDefinitionKey, variables, currentUserId }]
 *   }
 */
export function migrateProcess(data) {
  return request({
    url: '/workflow/process/migrate/replace',
    method: 'post',
    data: data
  })
}

/**
 * 校验替换参数（不实际执行）
 */
export function validateMigrate(data) {
  return request({
    url: '/workflow/process/migrate/validate',
    method: 'post',
    data: data
  })
}

/**
 * 取新流程定义的 userTask 列表（id + name），供前端做"智能推荐同 key"
 */
export function listNewProcessUserTasks(processDefinitionKey) {
  return request({
    url: '/workflow/process/migrate/userTasks/' + processDefinitionKey,
    method: 'get'
  })
}

/**
 * 取旧流程当前 taskDefinitionKey + 流程定义 key，供前端做"老节点 → 新节点"匹配
 */
export function getCurrentTaskOfInstance(procInsId) {
  return request({
    url: '/workflow/process/migrate/currentTask/' + procInsId,
    method: 'get'
  })
}
