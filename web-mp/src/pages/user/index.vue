<template>
  <view class="page-user">
    <view class="header">
      <up-avatar :src="userInfo?.avatar || defaultAvatar" size="100" :circle="true" />
      <view class="meta">
        <text class="name">{{ userInfo?.userName || '未登录' }}</text>
        <text class="nick">{{ userInfo?.nickName || 'Basic Admin' }}</text>
        <up-tag v-if="userInfo?.admin" type="error" size="mini" text="超管" />
        <up-tag v-else type="primary" size="mini" text="普通用户" />
      </view>
    </view>

    <up-cell-group :border="false">
      <up-cell icon="account-fill" title="个人资料" is-link @click="go('/pages/common/profile/index')" />
      <up-cell icon="lock-fill" title="修改密码" is-link @click="onChangePwd" />
      <up-cell icon="phone-fill" title="绑定手机" :value="maskPhone(userInfo?.phonenumber)" />
      <up-cell icon="email-fill" title="绑定邮箱" :value="userInfo?.email || '-'" />
    </up-cell-group>

    <up-cell-group title="系统" :border="false">
      <up-cell icon="info-circle-fill" title="关于" is-link @click="onAbout" />
      <up-cell icon="reload" title="清理缓存" is-link @click="onClearCache" />
    </up-cell-group>

    <view class="logout">
      <up-button type="error" @click="onLogout">退出登录</up-button>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const defaultAvatar = '/static/avatar.png'

function maskPhone(p) {
  if (!p) return '-'
  return p.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

function go(url) {
  uni.navigateTo({ url })
}

function onChangePwd() {
  uni.showToast({ title: '跳修改密码页（待补）', icon: 'none' })
}
function onAbout() {
  uni.showModal({
    title: '关于',
    content: '基础平台 v1.0.0\n企业级移动办公客户端'
  })
}
function onClearCache() {
  uni.showLoading({ title: '清理中' })
  setTimeout(() => {
    uni.hideLoading()
    uni.showToast({ title: '已清理', icon: 'success' })
  }, 600)
}

function onLogout() {
  uni.showModal({
    title: '提示',
    content: '确认退出登录？',
    success: async (r) => {
      if (r.confirm) {
        await userStore.logout()
        uni.reLaunch({ url: '/pages/login/index' })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.page-user {
  min-height: 100vh;
  background: $uni-bg-color-grey;
}
.header {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 40rpx;
  background: linear-gradient(135deg, $uni-color-primary, #6f8eff);
  color: #fff;
  .meta {
    display: flex;
    flex-direction: column;
    gap: 6rpx;
  }
  .name {
    font-size: 36rpx;
    font-weight: 600;
  }
  .nick {
    font-size: 24rpx;
    color: rgba(255, 255, 255, 0.85);
  }
}
.logout {
  margin: 60rpx 40rpx 40rpx;
}
</style>
