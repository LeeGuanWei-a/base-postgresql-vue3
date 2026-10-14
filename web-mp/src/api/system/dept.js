import { http } from '@/utils/request'

export function listDept(params) {
  return http.get('/system/dept/list', params)
}

export function treeselect() {
  return http.get('/system/dept/treeselect')
}
