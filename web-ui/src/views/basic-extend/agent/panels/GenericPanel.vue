<script setup name="GenericPanel">
/**
 * 通用兜底面板 —— 后端有该能力但前端还没做专用面板时使用。
 *
 * 依然可以发起通用对话 + 查看能力详情。
 */
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="info"
      :closable="false"
      show-icon
      title="该能力尚未提供专用面板，当前使用通用对话界面。"
    />
    <CapabilityChatBox :capability-id="capability.id" :default-prompt="capability.params?.[0]?.defaultValue || ''" />
    <CapabilityDetailCard :capability-id="capability.id" class="mt12" />
  </div>
</template>

<style scoped>
.mt12 {
  margin-top: 12px;
}
</style>
