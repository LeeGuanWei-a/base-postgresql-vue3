<script>
import { initPermission } from '@/permission'
import { getToken } from '@/utils/auth'

export default {
  onLaunch: function () {
    console.log('App Launch')
    // 注册全局导航守卫
    initPermission()
    // 首次启动：无 token 直接跳登录页（不显示一闪而过的首页）
    // 加 50ms 延迟，确保 navigation 栈就绪
    setTimeout(() => {
      const pages = getCurrentPages()
      const cur = pages[pages.length - 1]
      const curRoute = cur ? (cur.route || cur.__route__) : ''
      if (!getToken() && curRoute !== 'pages/login/index') {
        uni.reLaunch({ url: '/pages/login/index' })
      }
    }, 50)
  },
  onShow: function () {
    console.log('App Show')
  },
  onHide: function () {
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
