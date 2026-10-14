import { getToken } from '@/utils/auth'
import { useUserStore } from '@/store/modules/user'

/**
 * 全局导航守卫 —— 对齐 web-ui 的 router.beforeEach
 * - 白名单直接放行
 * - 未登录访问受保护页面 → 跳登录
 * - 已登录访问登录页 → 跳首页
 */
const whiteList = ['/pages/login/index']

export function initPermission() {
  const list = ['navigateTo', 'redirectTo', 'reLaunch', 'switchTab']
  list.forEach((name) => {
    uni.addInterceptor(name, {
      invoke(args) {
        const url = (args && args.url) || ''
        if (!url) return true
        if (whiteList.some((w) => url.startsWith(w))) {
          if (getToken()) {
            uni.reLaunch({ url: '/pages/index/index' })
            return false
          }
          return true
        }
        if (!getToken()) {
          uni.showToast({ title: '请先登录', icon: 'none' })
          uni.reLaunch({ url: '/pages/login/index' })
          return false
        }
        return true
      }
    })
  })

  // 保留对 store 的引用（pinia 已通过 createApp 注入）
  void useUserStore
}
