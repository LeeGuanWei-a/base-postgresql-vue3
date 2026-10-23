<script setup name="FailoverPanel">
/**
 * 模型故障转移（Failover）—— 展示主模型失败后如何降级到备用模型。
 *
 * 演示要点：
 * 1. 故障转移配置：主模型 / 备用模型 / 重试次数（el-descriptions）；
 * 2. 发起一次必然失败的调用：Mock 模式下备用模型是故意失败的，
 *    后端会返回「对话失败：xxx」，面板用 el-alert 优雅展示，而不是白屏。
 */
import { computed, ref } from 'vue'
import { capabilityChat } from '@/api/agentscope'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref(
  '请故意触发一次模型调用失败来演示故障转移：调用一个不可用的模型服务，观察主模型失败后重试与降级到备用模型的过程。'
)

const chatBox = ref(null)
const detailCard = ref(null)

/** 后端返回的错误信息（优雅展示） */
const failMsg = ref('')
/** 故障转移过程日志 */
const failoverTrace = ref([])
const triggering = ref(false)
const traceError = ref('')

const detailSnapshot = ref(null)

/** 从详情对象中取第一个存在的值 */
function pickValue(detail, keys, fallback = '-') {
  if (!detail || typeof detail !== 'object') return fallback
  for (const key of keys) {
    if (detail[key] !== undefined && detail[key] !== null && detail[key] !== '') return detail[key]
  }
  return fallback
}

/** 把备用模型统一成数组展示 */
function pickFallbacks(detail) {
  if (!detail || typeof detail !== 'object') return []
  const candidates = ['fallbackModels', 'backupModels', 'fallbacks', 'fallbackModel', 'backupModel']
  for (const key of candidates) {
    const value = detail[key]
    if (Array.isArray(value)) return value
    if (typeof value === 'string' && value.trim()) return value.split(',').map((s) => s.trim()).filter(Boolean)
  }
  return []
}

const primaryModel = computed(() => pickValue(detailSnapshot.value, ['primaryModel', 'mainModel', 'defaultModel', 'primary', 'model']))
const fallbackModels = computed(() => pickFallbacks(detailSnapshot.value))
const maxRetries = computed(() => pickValue(detailSnapshot.value, ['maxRetries', 'maxRetry', 'retries', 'retryCount', 'retryTimes'], 0))
const currentModel = computed(() => pickValue(detailSnapshot.value, ['currentModel', 'activeModel', 'state', 'status']))
const lastError = computed(() => pickValue(detailSnapshot.value, ['lastError', 'error', 'lastFailure', 'failureReason'], ''))

/** 故障转移过程：优先用后端给的轨迹，没有则按本地推断拼一条 */
const traceItems = computed(() => {
  if (failoverTrace.value.length) return failoverTrace.value
  return []
})

async function refreshDetail() {
  const card = detailCard.value
  if (!card) return null
  try {
    await card.load()
    detailSnapshot.value = card.detail || null
    return detailSnapshot.value
  } catch (e) {
    traceError.value = e?.message || '加载故障转移详情失败'
    return null
  }
}

/**
 * 主动触发一次失败调用，直接调用通用 chat 接口以便拿到后端返回的 message。
 */
async function triggerFailure() {
  if (triggering.value) return
  triggering.value = true
  failMsg.value = ''
  traceError.value = ''
  failoverTrace.value = []
  try {
    const res = await capabilityChat('failover', { prompt: prompt.value })
    failoverTrace.value = [
      { step: '调用主模型', status: 'success', desc: '主模型返回成功' },
      { step: '结果', status: 'success', desc: res?.data?.text || '（无文本）' }
    ]
  } catch (e) {
    const msg = e?.message || String(e || '调用失败')
    failMsg.value = msg
    failoverTrace.value = [
      { step: '调用主模型', status: 'success', desc: '主模型已发起调用' },
      { step: '主模型失败 → 重试', status: 'process', desc: `重试次数上限：${maxRetries.value}` },
      { step: '降级到备用模型', status: 'error', desc: msg }
    ]
  } finally {
    triggering.value = false
  }
}
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="warning"
      :closable="false"
      show-icon
      title="故障转移：主模型调用失败后按重试次数重试，仍失败则降级到备用模型。Mock 模式下备用模型故意失败，用于演示错误链路。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>故障转移配置</span>
          <el-button link type="primary" @click="refreshDetail">刷新</el-button>
        </div>
      </template>

      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="主模型">{{ primaryModel }}</el-descriptions-item>
        <el-descriptions-item label="当前模型">{{ currentModel }}</el-descriptions-item>
        <el-descriptions-item label="备用模型">
          <template v-if="fallbackModels.length">
            <el-tag v-for="item in fallbackModels" :key="item" class="ml8" size="small" type="warning">
              {{ item }}
            </el-tag>
          </template>
          <template v-else>-</template>
        </el-descriptions-item>
        <el-descriptions-item label="重试次数">{{ maxRetries }}</el-descriptions-item>
        <el-descriptions-item label="上次错误" :span="2">
          <span class="error-text">{{ lastError || '暂无错误记录' }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <CapabilityChatBox ref="chatBox" capability-id="failover" v-model:prompt="prompt" />

    <el-card shadow="never" class="mt12">
      <template #header>
        <div class="card-header">
          <span>故障转移过程演示</span>
          <el-button type="danger" plain :loading="triggering" @click="triggerFailure">
            触发一次失败调用
          </el-button>
        </div>
      </template>

      <el-alert
        v-if="failMsg"
        class="mb8"
        type="error"
        :closable="false"
        show-icon
        :title="'调用失败：' + failMsg"
      >
        <template #default>
          <div>这是<b>预期内</b>的错误：Mock 模式下的备用模型被设计成必然失败，用于演示故障转移与错误处理链路。</div>
        </template>
      </el-alert>

      <el-alert v-else-if="traceError" class="mb8" type="warning" :closable="false" :title="traceError" />

      <el-steps v-if="traceItems.length" direction="vertical" :active="traceItems.length" finish-status="success">
        <el-step
          v-for="(item, index) in traceItems"
          :key="index"
          :title="item.step"
          :status="item.status"
          :description="item.desc"
        />
      </el-steps>

      <el-empty v-else description="点击「触发一次失败调用」观察完整的故障转移链路" :image-size="80" />
    </el-card>

    <CapabilityDetailCard ref="detailCard" capability-id="failover" title="故障转移能力详情" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.error-text {
  color: var(--el-color-danger);
  word-break: break-word;
}
.ml8 {
  margin-left: 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
