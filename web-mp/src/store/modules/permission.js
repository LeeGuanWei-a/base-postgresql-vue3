import { defineStore } from 'pinia'
import { getRouters } from '@/api/login'

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    /** 后端返回的路由树（标准化后） */
    routes: [],
    /** 动态添加的路由表（暂未做动态 pages 注册，先缓存） */
    addRoutes: [],
    /** 侧边栏菜单（移动端暂未用） */
    sidebarRouters: []
  }),
  actions: {
    async generateRoutes() {
      const res = await getRouters()
      // request.js resolve 的是 body 本身，不是 res.data
      this.routes = res.data || []
      this.addRoutes = this.routes
      this.sidebarRouters = this.routes
      return this.routes
    },
    reset() {
      this.routes = []
      this.addRoutes = []
      this.sidebarRouters = []
    }
  }
})
