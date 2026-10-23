<script setup name="CapabilityDetailCard">
/**
 * 能力详情卡片 —— 展示 /agentscope/capability/{id}/detail 的返回内容。
 *
 * 外部依赖型能力用它来展示"依赖是否就绪 + 缺什么 + 怎么配"。
 */
import { onMounted, ref, watch } from 'vue'
import { capabilityDetail } from '@/api/agentscope'

const props = defineProps({
  /** 能力 id */
  capabilityId: { type: String, required: true },
  /** 标题 */
  title: { type: String, default: '能力详情' },
  /** 是否自动加载 */
  autoLoad: { type: Boolean, default: true }
})

const loading = ref(false)
const detail = ref(null)
const errorMsg = ref('')

async function load() {
  if (!props.capabilityId) return
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await capabilityDetail(props.capabilityId)
    detail.value = res.data
  } catch (e) {
    errorMsg.value = e?.message || '加载失败'
    detail.value = null
  } finally {
    loading.value = false
  }
}

onMounted(load)
watch(() => props.capabilityId, load)
defineExpose({ load, detail })
</script>

<template>
  <el-card shadow="never" class="cap-detail-card">
    <template #header>
      <div class="cap-detail-header">
        <span>{{ title }}</span>
        <el-button link type="primary" :loading="loading" @click="load">刷新</el-button>
      </div>
    </template>

    <el-alert v-if="errorMsg" type="warning" :closable="false" :title="errorMsg" />

    <el-skeleton v-else-if="loading && !detail" :rows="3" animated />

    <el-empty v-else-if="!detail" description="暂无详情数据" :image-size="70" />

    <pre v-else class="cap-detail-json">{{ JSON.stringify(detail, null, 2) }}</pre>

    <slot name="extra" :detail="detail" :reload="load" />
  </el-card>
</template>

<style scoped>
.cap-detail-card {
  margin-bottom: 12px;
}
.cap-detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.cap-detail-json {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
  line-height: 1.6;
  max-height: 420px;
  overflow: auto;
}
</style>
