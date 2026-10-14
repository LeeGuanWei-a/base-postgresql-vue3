import { TOKEN_KEY, USER_INFO_KEY, PERMISSIONS_KEY, ROLES_KEY } from './config'

/** 获取 token */
export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || ''
}

/** 设置 token */
export function setToken(token) {
  uni.setStorageSync(TOKEN_KEY, token)
}

/** 移除 token */
export function removeToken() {
  uni.removeStorageSync(TOKEN_KEY)
}

/** 用户信息 */
export function getUserInfo() {
  return uni.getStorageSync(USER_INFO_KEY) || null
}
export function setUserInfo(info) {
  uni.setStorageSync(USER_INFO_KEY, info)
}
export function removeUserInfo() {
  uni.removeStorageSync(USER_INFO_KEY)
}

/** 权限码 */
export function getPermissions() {
  return uni.getStorageSync(PERMISSIONS_KEY) || []
}
export function setPermissions(perms) {
  uni.setStorageSync(PERMISSIONS_KEY, perms)
}
export function removePermissions() {
  uni.removeStorageSync(PERMISSIONS_KEY)
}

/** 角色码 */
export function getRoles() {
  return uni.getStorageSync(ROLES_KEY) || []
}
export function setRoles(roles) {
  uni.setStorageSync(ROLES_KEY, roles)
}
export function removeRoles() {
  uni.removeStorageSync(ROLES_KEY)
}

/** 清空所有登录态（登出时调用） */
export function clearAuth() {
  removeToken()
  removeUserInfo()
  removePermissions()
  removeRoles()
}
