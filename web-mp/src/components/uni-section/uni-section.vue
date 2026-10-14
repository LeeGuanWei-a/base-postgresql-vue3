<template>
  <view class="uni-section">
    <view v-if="title" class="uni-section__header">
      <text class="uni-section__title">{{ title }}</text>
      <view v-if="subTitle" class="uni-section__sub">
        <text>{{ subTitle }}</text>
      </view>
    </view>
    <view class="uni-section__content">
      <slot />
    </view>
  </view>
</template>

<script>
export default {
  name: 'UniSection',
  props: {
    title: { type: String, default: '' },
    subTitle: { type: String, default: '' },
    type: { type: String, default: '' },
    padding: { type: [Boolean, String], default: false }
  }
}
</script>

<style lang="scss" scoped>
.uni-section {
  background-color: #fff;
  margin: 16rpx 0;
  border-radius: 8rpx;
  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 24rpx;
    font-size: 28rpx;
    color: #303133;
    font-weight: 500;
  }
  &__title {
    flex: 1;
  }
  &__sub {
    color: #909399;
    font-size: 24rpx;
  }
  &__content {
    padding: 0 24rpx 24rpx;
  }
}
</style>
