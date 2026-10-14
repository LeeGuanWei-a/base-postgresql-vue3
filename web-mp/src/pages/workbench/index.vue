<template>
  <view class="page-workbench">
    <view class="search">
      <up-search v-model="keyword" placeholder="搜索菜单" :show-action="false" @search="onSearch" />
    </view>

    <up-cell-group title="系统管理" :border="false">
      <up-cell v-if="hasPermi(['system:user:list'])" icon="account-fill" title="用户管理" is-link @click="goPage('user')" />
      <up-cell v-if="hasPermi(['system:role:list'])" icon="setting-fill" title="角色管理" is-link @click="goPage('role')" />
      <up-cell v-if="hasPermi(['system:dept:list'])" icon="server-man" title="部门管理" is-link @click="goPage('dept')" />
      <up-cell v-if="hasPermi(['system:dict:list'])" icon="list-dot" title="字典管理" is-link @click="goPage('dict')" />
    </up-cell-group>

    <up-cell-group title="系统监控" :border="false">
      <up-cell v-if="hasPermi(['system:logininfor:list'])" icon="eye-fill" title="登录日志" is-link @click="goPage('logininfor')" />
      <up-cell v-if="hasPermi(['system:operlog:list'])" icon="edit-pen-fill" title="操作日志" is-link @click="goPage('operlog')" />
    </up-cell-group>

    <up-cell-group title="系统工具" :border="false">
      <up-cell v-if="hasPermi(['tool:gen:list'])" icon="reload" title="代码生成" is-link @click="goPage('generator')" />
    </up-cell-group>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/modules/user'

const userStore = useUserStore()
const keyword = ref('')

/** 权限判断函数 —— 替代 v-hasPermi 指令（uniapp 3 编译时不支持自定义指令） */
function hasPermi(perms: string[]): boolean {
  const owned = userStore.permissions || []
  if (owned.includes('*:*:*')) return true
  if (!Array.isArray(perms)) return false
  return perms.every((p) => owned.includes(p))
}

function onSearch(): void {
  uni.showToast({ title: '搜索：' + keyword.value, icon: 'none' })
}

function goPage(name: string): void {
  uni.showToast({ title: '进入：' + name, icon: 'none' })
  // TODO: 接入动态路由后改成 uni.navigateTo({ url: `/pages/${name}/index` })
}
</script>

<style lang="scss" scoped>
.page-workbench {
  min-height: 100vh;
  background: $uni-bg-color-grey;
  padding: 20rpx 0;
}
.search {
  padding: 0 20rpx 20rpx;
  background: #fff;
}
</style>
