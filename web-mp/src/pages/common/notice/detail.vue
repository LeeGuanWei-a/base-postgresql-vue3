<template>
  <view class="page-notice-detail">
    <view class="title">{{ data.noticeTitle }}</view>
    <view class="meta">
      <up-tag v-if="data.noticeType === '1'" type="success" size="mini" text="通知" />
      <up-tag v-else type="warning" size="mini" text="公告" />
      <text>  {{ data.createTime }}</text>
    </view>
    <view class="content">
      <rich-text :nodes="data.noticeContent || ''"></rich-text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { http } from '@/utils/request'
import type { AjaxResult } from '@/types/global'

interface NoticeItem {
  noticeId?: number
  noticeTitle?: string
  noticeType?: string
  noticeContent?: string
  createTime?: string
  [key: string]: unknown
}

const data = ref<NoticeItem>({})

onLoad((q: { id?: string }) => {
  http.get<AjaxResult<NoticeItem>>('/system/notice/' + q.id, undefined, { silent: true }).then((res) => {
    data.value = res.data || {}
  })
})
</script>

<style lang="scss" scoped>
.page-notice-detail {
  padding: 32rpx;
  background: #fff;
  min-height: 100vh;
  .title {
    font-size: 36rpx;
    font-weight: 600;
    color: $uni-text-color;
  }
  .meta {
    margin-top: 12rpx;
    color: $uni-text-color-grey;
    font-size: 24rpx;
  }
  .content {
    margin-top: 32rpx;
    line-height: 1.7;
    color: $uni-text-color;
  }
}
</style>
