import { TOKEN_KEY, USER_INFO_KEY, PERMISSIONS_KEY, ROLES_KEY } from './config'
import type { UserInfo } from '@/types/global'

/** 获取 token */
export function getToken(): string {
  return (uni.getStorageSync(TOKEN_KEY) as string) || ''
}

/** 设置 token */
export function setToken(token: string): void {
  uni.setStorageSync(TOKEN_KEY, token)
}

/** 移除 token */
export function removeToken(): void {
  uni.removeStorageSync(TOKEN_KEY)
}

/** 用户信息 */
export function getUserInfo(): UserInfo | null {
  return (uni.getStorageSync(USER_INFO_KEY) as UserInfo) || null
}
export function setUserInfo(info: UserInfo): void {
  uni.setStorageSync(USER_INFO_KEY, info)
}
export function removeUserInfo(): void {
  uni.removeStorageSync(USER_INFO_KEY)
}

/** 权限码 */
export function getPermissions(): string[] {
  return (uni.getStorageSync(PERMISSIONS_KEY) as string[]) || []
}
export function setPermissions(perms: string[]): void {
  uni.setStorageSync(PERMISSIONS_KEY, perms)
}
export function removePermissions(): void {
  uni.removeStorageSync(PERMISSIONS_KEY)
}

/** 角色码 */
export function getRoles(): string[] {
  return (uni.getStorageSync(ROLES_KEY) as string[]) || []
}
export function setRoles(roles: string[]): void {
  uni.setStorageSync(ROLES_KEY, roles)
}
export function removeRoles(): void {
  uni.removeStorageSync(ROLES_KEY)
}

/** 清空所有登录态（登出时调用） */
export function clearAuth(): void {
  removeToken()
  removeUserInfo()
  removePermissions()
  removeRoles()
}
