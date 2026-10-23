import request from '@/utils/request'

// 查询表单分类列表
export function listFormCategory(query) {
  return request({
    url: '/system/category/list',
    method: 'get',
    params: query
  })
}

// 新增表单分类
export function addFormCategory(data) {
  return request({
    url: '/system/category',
    method: 'post',
    data: data
  })
}

// 修改表单分类
export function updateFormCategory(data) {
  return request({
    url: '/system/category',
    method: 'put',
    data: data
  })
}

// 删除表单分类
export function delFormCategory(ids) {
  return request({
    url: '/system/category/' + ids,
    method: 'delete'
  })
}
