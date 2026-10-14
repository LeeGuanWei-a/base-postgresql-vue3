import { defineStore } from 'pinia'
import { getRouters } from '@/api/login'

export interface RouteItem {
  name: string
  path: string
  hidden?: boolean
  redirect?: string
  component?: string
  meta?: {
    title?: string
    icon?: string
    noCache?: boolean
    [key: string]: unknown
  }
  children?: RouteItem[]
  [key: string]: unknown
}

interface PermissionState {
  /** 后端返回的路由树（标准化后） */
  routes: RouteItem[]
  /** 动态添加的路由表（暂未做动态 pages 注册，先缓存） */
  addRoutes: RouteItem[]
  /** 侧边栏菜单（移动端暂未用） */
  sidebarRouters: RouteItem[]
}

export const usePermissionStore = defineStore('permission', {
  state: (): PermissionState => ({
    routes: [],
    addRoutes: [],
    sidebarRouters: []
  }),
  actions: {
    async generateRoutes(): Promise<RouteItem[]> {
      const res = await getRouters()
      // request.js resolve 的是 body 本身，不是 res.data
      this.routes = (res.data as RouteItem[] | undefined) || []
      this.addRoutes = this.routes
      this.sidebarRouters = this.routes
      return this.routes
    },
    reset(): void {
      this.routes = []
      this.addRoutes = []
      this.sidebarRouters = []
    }
  }
})
