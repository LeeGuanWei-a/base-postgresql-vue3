import { http } from '@/utils/request'

/** 用户列表（分页） */
export function listUser(params) {
  return http.get('/system/user/list', params)
}

export function getUser(userId) {
  return http.get('/system/user/' + userId)
}

export function addUser(data) {
  return http.post('/system/user', data)
}

export function updateUser(data) {
  return http.put('/system/user', data)
}

export function delUser(userId) {
  return http.del('/system/user/' + userId)
}

/** 修改自己的密码 */
export function updateUserPassword(oldPassword, newPassword) {
  return http.put('/system/user/profile/updatePwd', { oldPassword, newPassword })
}

/** 当前用户资料 */
export function getUserProfile() {
  return http.get('/system/user/profile')
}
