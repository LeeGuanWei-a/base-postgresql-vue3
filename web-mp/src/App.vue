<script lang="ts">
import { initPermission } from '@/permission'
import { getToken } from '@/utils/auth'

export default {
  onLaunch(): void {
    console.log('App Launch')
    initPermission()
    // 首次启动：无 token 直接跳登录页（不显示一闪而过的首页）
    setTimeout(() => {
      const pages = getCurrentPages()
      const cur = pages[pages.length - 1] as (Record<string, unknown> | undefined)
      const curRoute = cur ? ((cur.route as string) || (cur.__route__ as string) || '') : ''
      if (!getToken() && curRoute !== 'pages/login/index') {
        uni.reLaunch({ url: '/pages/login/index' })
      }
    }, 50)
  },
  onShow(): void {
    console.log('App Show')
  },
  onHide(): void {
    console.log('App Hide')
  }
}
</script>

<style lang="scss">
/* uview-plus 全局样式（H5 走 vite-scss，mp 走 uniapp 自动注入；两边都能识别） */
@import 'uview-plus/index.scss';

page {
  background-color: #f8f8f8;
  color: #303133;
  font-size: 28rpx;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Helvetica Neue', sans-serif;
}

view, text, image {
  box-sizing: border-box;
}
</style>
