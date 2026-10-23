import request from '@/utils/request'

// 查询流程分类列表
export function listCategory(query) {
  return request({
    url: '/workflow/category/list',
    method: 'get',
    params: query
  })
}

// 查询流程分类列表（不分页）
export function listCategoryAll() {
  return request({
    url: '/workflow/category/listAll',
    method: 'get'
  })
}

// 查询流程分类详细
export function getCategory(categoryId) {
  return request({
    url: '/workflow/category/' + categoryId,
    method: 'get'
  })
}

// 新增流程分类
export function addCategory(data) {
  return request({
    url: '/workflow/category',
    method: 'post',
    data: data
  })
}

// 修改流程分类
export function updateCategory(data) {
  return request({
    url: '/workflow/category',
    method: 'put',
    data: data
  })
}

// 删除流程分类
export function delCategory(categoryIds) {
  return request({
    url: '/workflow/category/' + categoryIds,
    method: 'delete'
  })
}

// 校验分类编码唯一
export function checkCategoryCodeUnique(data) {
  return request({
    url: '/workflow/category/checkCategoryCodeUnique',
    method: 'get',
    params: data
  })
}

// 查询分类流程下拉选项
export function getCategoryOption(query) {
  return request({
    url: '/workflow/category/getCategoryOption',
    method: 'get',
    params: query
  })
}
