<template>
  <view class="page-index">
    <view class="banner">
      <text class="hello">{{ greet }}，{{ userInfo?.userName || '用户' }}</text>
      <text class="sub">{{ now }}</text>
    </view>

    <up-cell-group title="快捷入口" :border="false">
      <up-grid :col="4" :border="false">
        <up-grid-item v-for="m in quickMenus" :key="m.name" @click="goMenu(m)">
          <up-icon :name="m.icon" :size="48" :color="m.color" />
          <text class="grid-text">{{ m.name }}</text>
        </up-grid-item>
      </up-grid>
    </up-cell-group>

    <up-cell-group title="数据概览" :border="false">
      <up-cell icon="account-fill" title="用户管理" value="查看" is-link @click="goPath('/pages/workbench/index')" />
      <up-cell icon="setting-fill" title="角色管理" value="查看" is-link @click="goPath('/pages/workbench/index')" />
      <up-cell icon="file-text-fill" title="通知公告" value="查看" is-link @click="goPath('/pages/common/notice/index')" />
    </up-cell-group>

    <view class="logout">
      <up-button type="error" plain @click="onLogout">退出登录</up-button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/modules/user'

interface QuickMenu { name: string; icon: string; color: string; path: string }

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const now = new Date().toLocaleDateString('zh-CN', { weekday: 'long', month: 'long', day: 'numeric' })
const greet = computed<string>(() => {
  const h = new Date().getHours()
  if (h < 6) return '凌晨好'
  if (h < 12) return '早上好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const quickMenus: QuickMenu[] = [
  { name: '用户', icon: 'account', color: '#4368e3', path: '/pages/workbench/index' },
  { name: '角色', icon: 'setting', color: '#19be6b', path: '/pages/workbench/index' },
  { name: '部门', icon: 'server-man', color: '#ff9900', path: '/pages/workbench/index' },
  { name: '字典', icon: 'list-dot', color: '#9c4dff', path: '/pages/workbench/index' }
]

function goMenu(m: QuickMenu): void {
  goPath(m.path)
}
function goPath(url: string): void {
  uni.navigateTo({ url, fail: () => uni.switchTab({ url }) })
}

function onLogout(): void {
  uni.showModal({
    title: '提示',
    content: '确认退出登录？',
    success: async (r: UniApp.ShowModalRes) => {
      if (r.confirm) {
        await userStore.logout()
        uni.reLaunch({ url: '/pages/login/index' })
      }
    }
  })
}

onShow(() => {
  // 每次显示都刷新一次本地 userInfo（防 pinia 在路由切换后状态被重建）
  userStore.initStorage()
})
</script>

<style lang="scss" scoped>
.page-index {
  min-height: 100vh;
  background: $uni-bg-color-grey;
  padding-bottom: 40rpx;
}
.banner {
  padding: 60rpx 40rpx 40rpx;
  background: linear-gradient(135deg, $uni-color-primary, #6f8eff);
  color: #fff;
  .hello {
    display: block;
    font-size: 40rpx;
    font-weight: 600;
  }
  .sub {
    display: block;
    margin-top: 8rpx;
    color: rgba(255, 255, 255, 0.85);
    font-size: 26rpx;
  }
}
.grid-text {
  display: block;
  font-size: 24rpx;
  margin-top: 8rpx;
  color: $uni-text-color;
}
.logout {
  margin: 60rpx 40rpx 0;
}
</style>
