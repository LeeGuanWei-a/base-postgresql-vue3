import request from '@/utils/request'

// 查询表达式列表
export function listExpression(query) {
  return request({
    url: '/workflow/expression/list',
    method: 'get',
    params: query
  })
}

// 导出表达式
export function exportExpression(query) {
  return request({
    url: '/workflow/expression/export',
    method: 'post',
    params: query
  })
}

// 查询表达式详细
export function getExpression(id) {
  return request({
    url: '/workflow/expression/' + id,
    method: 'get'
  })
}

// 删除表达式
export function delExpression(ids) {
  return request({
    url: '/workflow/expression/' + ids,
    method: 'delete'
  })
}
