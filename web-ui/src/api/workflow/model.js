import request from '@/utils/request'

// 查询流程模型列表
export function listModel(query) {
  return request({
    url: '/workflow/model/list',
    method: 'get',
    params: query
  })
}

// 查询可发起流程模型列表
export function listModelLaunch(query) {
  return request({
    url: '/workflow/model/listModelLaunch',
    method: 'get',
    params: query
  })
}

// 查询模型历史版本
export function historyList(query) {
  return request({
    url: '/workflow/model/historyList',
    method: 'get',
    params: query
  })
}

// 查询模型详细
export function getModel(modelId) {
  return request({
    url: '/workflow/model/' + modelId,
    method: 'get'
  })
}

// 查询模型 BPMN XML
export function getModelBpmnXml(modelId) {
  return request({
    url: '/workflow/model/bpmnXml/' + modelId,
    method: 'get'
  })
}

// 保存流程模型
export function saveModel(data) {
  return request({
    url: '/workflow/model/save',
    method: 'post',
    data: data
  })
}

// 新增流程模型（创建空 model）
export function addModel(data) {
  return request({
    url: '/workflow/model',
    method: 'post',
    data: data
  })
}

// 最新版本模型
export function latestModel(data) {
  return request({
    url: '/workflow/model/latest',
    method: 'post',
    data: data
  })
}

// 删除流程模型
export function delModel(modelIds) {
  return request({
    url: '/workflow/model/' + modelIds,
    method: 'delete'
  })
}

// 部署流程模型
export function deployModel(data) {
  return request({
    url: '/workflow/model/deploy',
    method: 'post',
    data: data
  })
}

// 导出流程模型
export function exportModel(data) {
  return request({
    url: '/workflow/model/export',
    method: 'post',
    data: data
  })
}

// 发布流程模型
export function publishModel(data) {
  return request({
    url: '/workflow/model/publishModel',
    method: 'post',
    data: data
  })
}

// 挂起流程模型
export function suspendedModel(data) {
  return request({
    url: '/workflow/model/suspended',
    method: 'post',
    data: data
  })
}

// 激活流程模型
export function activateModel(data) {
  return request({
    url: '/workflow/model/activate',
    method: 'post',
    data: data
  })
}
