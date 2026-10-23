import request from '@/utils/request'

// 查询流程定义列表
export function listDeploy(query) {
  return request({
    url: '/workflow/deploy/list',
    method: 'get',
    params: query
  })
}

// 查询发布流程定义列表
export function listPublishDeploy(query) {
  return request({
    url: '/workflow/deploy/publishList',
    method: 'get',
    params: query
  })
}

// 修改流程定义状态（激活/挂起）
export function changeDeployState(data) {
  return request({
    url: '/workflow/deploy/changeState',
    method: 'put',
    data: data
  })
}

// 删除流程定义
export function delDeploy(deployIds) {
  return request({
    url: '/workflow/deploy/' + deployIds,
    method: 'delete'
  })
}

// 查询流程定义部署的表单
export function getDeployForm(query) {
  return request({
    url: '/workflow/deploy/deployForm',
    method: 'get',
    params: query
  })
}
