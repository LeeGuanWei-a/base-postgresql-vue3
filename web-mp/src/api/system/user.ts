import { http } from '@/utils/request'
import type { AjaxResult, PageQuery, PageResult, UserInfo } from '@/types/global'

/** 用户列表（分页） */
export function listUser(params: PageQuery): Promise<AjaxResult<PageResult<UserInfo>>> {
  return http.get<AjaxResult<PageResult<UserInfo>>>('/system/user/list', params)
}

export function getUser(userId: number | string): Promise<AjaxResult<UserInfo>> {
  return http.get<AjaxResult<UserInfo>>('/system/user/' + userId)
}

export function addUser(data: Partial<UserInfo> & { password: string }): Promise<AjaxResult<null>> {
  return http.post<AjaxResult<null>>('/system/user', data)
}

export function updateUser(data: Partial<UserInfo>): Promise<AjaxResult<null>> {
  return http.put<AjaxResult<null>>('/system/user', data)
}

export function delUser(userId: number | string): Promise<AjaxResult<null>> {
  return http.del<AjaxResult<null>>('/system/user/' + userId)
}

/** 修改自己的密码 */
export function updateUserPassword(oldPassword: string, newPassword: string): Promise<AjaxResult<null>> {
  return http.put<AjaxResult<null>>('/system/user/profile/updatePwd', { oldPassword, newPassword })
}

/** 当前用户资料 */
export function getUserProfile(): Promise<AjaxResult<UserInfo>> {
  return http.get<AjaxResult<UserInfo>>('/system/user/profile')
}
