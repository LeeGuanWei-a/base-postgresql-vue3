import { http } from '@/utils/request'

export function listRole(params) {
  return http.get('/system/role/list', params)
}

export function getRole(roleId) {
  return http.get('/system/role/' + roleId)
}

export function addRole(data) {
  return http.post('/system/role', data)
}

export function updateRole(data) {
  return http.put('/system/role', data)
}

export function delRole(roleId) {
  return http.del('/system/role/' + roleId)
}
