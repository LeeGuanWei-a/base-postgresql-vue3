import request from '@/utils/request'

// ========== 流程表单数据（wf_task_form） ==========
// 说明：本模块对接 extend-flowable 的 WfTaskFormController。
// 原 process 项目中的 *ByBusinessKey 系列接口已随「流程与业务解耦」一并移除。

/** 根据流程实例id + 节点id 查询该节点已填的表单数据 */
export function selectFormJsonDataByProcInsIdAndActIdToList(procInsId, activityId) {
  return request({
    url: '/workflow/taskForm/selectFormJsonDataByProcInsIdAndActIdToList',
    method: 'get',
    params: { procInsId, activityId }
  })
}

/** 根据任务id + 节点id 查询该节点已填的表单数据 */
export function selectFormJsonDataByTaskIdAndActIdToList(taskId, activityId) {
  return request({
    url: '/workflow/taskForm/selectFormJsonDataByTaskIdAndActIdToList',
    method: 'get',
    params: { taskId, activityId }
  })
}

/** 根据流程实例id查询表单数据列表 */
export function selectFormDataByProcInsIdToList(procInsId) {
  return request({
    url: '/workflow/taskForm/selectFormDataByProcInsIdToList/' + procInsId,
    method: 'get'
  })
}

/** 保存流程表单数据 */
export function saveProcessForm(data) {
  return request({
    url: '/workflow/taskForm/saveProcessForm',
    method: 'post',
    data: data
  })
}

/** 根据流程实例id集合删除流程表单 */
export function delProcessForm(procInsIds) {
  return request({
    url: '/workflow/taskForm/delProcessForm/' + procInsIds,
    method: 'delete'
  })
}

/** 根据流程实例id + 节点id删除流程表单 */
export function delProcessFormByProcInsIdAndActId(data) {
  return request({
    url: '/workflow/taskForm/delProcessFormByProcInsIdAndActId',
    method: 'post',
    data: data
  })
}
