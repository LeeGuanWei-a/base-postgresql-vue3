<template>
  <view class="page-profile">
    <up-cell-group title="基本信息" :border="false">
      <up-cell title="账号" :value="userInfo?.userName || '-'" />
      <up-cell title="昵称" :value="userInfo?.nickName || '-'" />
      <up-cell title="手机" :value="userInfo?.phonenumber || '-'" />
      <up-cell title="邮箱" :value="userInfo?.email || '-'" />
      <up-cell title="部门" :value="userInfo?.dept?.deptName || '-'" />
      <up-cell title="角色" :value="rolesText" />
    </up-cell-group>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const rolesText = computed<string>(() => (userStore.roles || []).join('，') || '-')
</script>

<style lang="scss" scoped>
.page-profile {
  background: $uni-bg-color-grey;
  min-height: 100vh;
}
</style>
