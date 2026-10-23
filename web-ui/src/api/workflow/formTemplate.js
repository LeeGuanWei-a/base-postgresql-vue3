import request from '@/utils/request'

// 查询表单模板列表
export function listFormTemplate(query) {
  return request({
    url: '/workflow/formTemplate/list',
    method: 'get',
    params: query
  })
}

// 查询所有表单模板
export function allListFormTemplate(query) {
  return request({
    url: '/workflow/formTemplate/allList',
    method: 'get',
    params: query
  })
}

// 查询表单模板详细
export function getFormTemplate(formId) {
  return request({
    url: '/workflow/formTemplate/' + formId,
    method: 'get'
  })
}

// 保存表单模板
export function saveFormTemplate(data) {
  return request({
    url: '/workflow/formTemplate/saveFormTemplate',
    method: 'post',
    data: data
  })
}

// 删除表单模板
export function delFormTemplate(formIds) {
  return request({
    url: '/workflow/formTemplate/delFormTemplate/' + formIds,
    method: 'delete'
  })
}
