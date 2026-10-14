<template>
  <view class="page-notice">
    <view v-for="n in list" :key="n.noticeId" class="notice-item" @click="goDetail(n)">
      <view class="title">
        <up-tag v-if="n.noticeType === '1'" type="success" size="mini" text="通知" />
        <up-tag v-else type="warning" size="mini" text="公告" />
        <text class="t">{{ n.noticeTitle }}</text>
      </view>
      <text class="meta">{{ n.createTime }}</text>
    </view>
    <up-empty v-if="!list.length" text="暂无公告" />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { http } from '@/utils/request'
import type { AjaxResult, PageResult } from '@/types/global'

interface NoticeItem {
  noticeId: number
  noticeTitle: string
  noticeType: string
  createTime: string
  [key: string]: unknown
}

const list = ref<NoticeItem[]>([])

async function load(): Promise<void> {
  const res = await http.get<AjaxResult<PageResult<NoticeItem> | NoticeItem[]>>(
    '/system/notice/list',
    { pageNum: 1, pageSize: 20 },
    { silent: true }
  )
  const data = res.data
  if (Array.isArray(data)) {
    list.value = data
  } else if (data && Array.isArray((data as PageResult<NoticeItem>).rows)) {
    list.value = (data as PageResult<NoticeItem>).rows
  } else {
    list.value = []
  }
}

function goDetail(n: NoticeItem): void {
  uni.navigateTo({ url: '/pages/common/notice/detail?id=' + n.noticeId })
}

load()
</script>

<style lang="scss" scoped>
.page-notice {
  padding: 24rpx;
}
.notice-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  .title {
    display: flex;
    align-items: center;
    gap: 12rpx;
    .t {
      font-size: 30rpx;
      font-weight: 500;
    }
  }
  .meta {
    display: block;
    margin-top: 8rpx;
    color: $uni-text-color-grey;
    font-size: 24rpx;
  }
}
</style>
