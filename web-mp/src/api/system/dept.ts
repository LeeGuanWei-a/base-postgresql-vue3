import { http } from '@/utils/request'
import type { AjaxResult } from '@/types/global'

export interface Dept {
  id: number
  parentId: number
  deptName: string
  orderNum: number
  status: string
  children?: Dept[]
  [key: string]: unknown
}

export function listDept(params?: Record<string, unknown>): Promise<AjaxResult<Dept[]>> {
  return http.get<AjaxResult<Dept[]>>('/system/dept/list', params)
}

export function treeselect(): Promise<AjaxResult<Dept[]>> {
  return http.get<AjaxResult<Dept[]>>('/system/dept/treeselect')
}
