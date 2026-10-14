import { http } from '@/utils/request'
import type { AjaxResult, PageQuery, PageResult } from '@/types/global'

export interface Role {
  roleId: number
  roleName: string
  roleKey: string
  roleSort: number
  status: string
  remark?: string
  [key: string]: unknown
}

export function listRole(params: PageQuery): Promise<AjaxResult<PageResult<Role>>> {
  return http.get<AjaxResult<PageResult<Role>>>('/system/role/list', params)
}

export function getRole(roleId: number | string): Promise<AjaxResult<Role>> {
  return http.get<AjaxResult<Role>>('/system/role/' + roleId)
}

export function addRole(data: Partial<Role>): Promise<AjaxResult<null>> {
  return http.post<AjaxResult<null>>('/system/role', data)
}

export function updateRole(data: Partial<Role>): Promise<AjaxResult<null>> {
  return http.put<AjaxResult<null>>('/system/role', data)
}

export function delRole(roleId: number | string): Promise<AjaxResult<null>> {
  return http.del<AjaxResult<null>>('/system/role/' + roleId)
}
