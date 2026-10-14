import { defineStore } from 'pinia'
import { login as apiLogin, logout as apiLogout, getInfo } from '@/api/login'
import {
  getToken, setToken, removeToken,
  setUserInfo, getUserInfo, removeUserInfo,
  setPermissions, getPermissions, removePermissions,
  setRoles, getRoles, removeRoles,
  clearAuth
} from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: getUserInfo() || {},
    roles: getRoles() || [],
    permissions: getPermissions() || []
  }),
  actions: {
    /** 启动时从 storage 同步登录态到 state */
    initStorage() {
      this.token = getToken()
      this.userInfo = getUserInfo() || {}
      this.roles = getRoles() || []
      this.permissions = getPermissions() || []
    },

    /** 登录 */
    async login(payload) {
      // request.js resolve 的就是 body（已经是 res.data 内容）
      const { token } = await apiLogin(payload)
      setToken(token)
      this.token = token
      return token
    },

    /** 拉取用户信息 */
    async fetchUserInfo() {
      const res = await getInfo()
      // request.js resolve 的就是 body（已经是 res.data 内容），不要再 .data
      const { user, roles, permissions } = res
      const roleArr = Array.isArray(roles)
        ? roles.map((r) => (typeof r === 'string' ? r : r.roleKey))
        : []
      const permArr = Array.isArray(permissions) ? permissions : []
      setUserInfo(user)
      setRoles(roleArr)
      setPermissions(permArr)
      this.userInfo = user
      this.roles = roleArr
      this.permissions = permArr
      return { user, roles: roleArr, permissions: permArr }
    },

    /** 登出 */
    async logout() {
      try {
        await apiLogout()
      } catch (e) {
        // 即使后端登出失败，前端也清掉状态
      }
      this.resetState()
    },

    resetState() {
      this.token = ''
      this.userInfo = {}
      this.roles = []
      this.permissions = []
      clearAuth()
    },

    /** 单独清理 token（保留 userInfo） */
    clearToken() {
      this.token = ''
      removeToken()
    }
  }
})
