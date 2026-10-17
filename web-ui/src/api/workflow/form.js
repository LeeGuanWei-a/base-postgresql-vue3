import request from '@/utils/request'

// 查询表单列表
export function listForm(query) {
  return request({
    url: '/workflow/form/list',
    method: 'get',
    params: query
  })
}

// 查询表单详细
export function getForm(formId) {
  return request({
    url: '/workflow/form/' + formId,
    method: 'get'
  })
}

// 通过 code 获取表单
export function getFormByCode(code) {
  return request({
    url: '/workflow/form/getFormByCode/' + code,
    method: 'get'
  })
}

// 保存表单
export function saveForm(data) {
  return request({
    url: '/workflow/form/saveForm',
    method: 'post',
    data: data
  })
}

// 删除表单
export function delForm(formIds) {
  return request({
    url: '/workflow/form/delForm/' + formIds,
    method: 'delete'
  })
}

// 获取 VForm 组件列表
export function getVFormWidgetList() {
  return request({
    url: '/workflow/form/getVFormWidgetList',
    method: 'get'
  })
}
