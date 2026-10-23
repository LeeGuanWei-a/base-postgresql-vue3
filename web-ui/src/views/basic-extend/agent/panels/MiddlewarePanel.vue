<script setup name="MiddlewarePanel">
/**
 * Middleware 中间件 —— 展示 Middleware 对「系统提示词 / 模型调用」的改写与统计。
 *
 * 演示要点：
 * 1. 改写统计：调用次数、命中改写的次数、最后改写时间等；
 * 2. 最后一次系统提示词：只读文本域单独展示，直观看到中间件改写后的结果；
 * 3. 对照区：发起一次对话 → 列出新增的中间件记录。
 */
import { computed, ref } from 'vue'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref('请用一句话说明你是谁，并调用一次工具。')

const chatBox = ref(null)
const detailCard = ref(null)

const detailSnapshot = ref(null)
const beforeCount = ref(-1)
const newRecords = ref([])
const comparing = ref(false)
const compareError = ref('')

/** 从详情对象中提取中间件记录数组 —— 兼容多种字段命名 */
function pickRecords(detail) {
  if (!detail || typeof detail !== 'object') return []
  const candidates = [
    detail.records,
    detail.middlewares,
    detail.middlewareRecords,
    detail.logs,
    detail.log,
    detail.events,
    detail.list,
    detail.items
  ]
  for (const item of candidates) {
    if (Array.isArray(item)) return item
  }
  return []
}

/** 提取最后一次系统提示词 —— 兼容多种字段命名 */
function pickLastPrompt(detail) {
  if (!detail || typeof detail !== 'object') return ''
  const candidates = [
    detail.lastSystemPrompt,
    detail.lastSysPrompt,
    detail.systemPrompt,
    detail.sysPrompt,
    detail.lastPrompt,
    detail.prompt,
    detail.rewrittenPrompt
  ]
  for (const item of candidates) {
    if (typeof item === 'string' && item.trim()) return item
  }
  return ''
}

function normalizeRecord(item, index) {
  if (typeof item === 'string') {
    return { title: item, time: '', desc: '' }
  }
  if (!item || typeof item !== 'object') {
    return { title: String(item ?? ''), time: '', desc: '' }
  }
  const title =
    item.name || item.middleware || item.hook || item.type || item.stage || item.action || `记录 #${index + 1}`
  const time = item.time || item.timestamp || item.at || item.createTime || ''
  const raw =
    item.payload ?? item.data ?? item.detail ?? item.result ?? item.message ?? item.rewritten ?? item.output ?? ''
  let desc = ''
  if (raw !== null && raw !== undefined && raw !== '') {
    desc = typeof raw === 'string' ? raw : JSON.stringify(raw, null, 2)
  }
  return { title: String(title), time: String(time), desc }
}

const newRecordItems = computed(() => newRecords.value.map(normalizeRecord))

const lastSystemPrompt = computed(() => pickLastPrompt(detailSnapshot.value))

/** 改写统计 —— 从详情里兜底取几个常见统计字段 */
const stats = computed(() => {
  const detail = detailSnapshot.value || {}
  const pick = (...keys) => {
    for (const k of keys) {
      if (detail[k] !== undefined && detail[k] !== null) return detail[k]
    }
    return '-'
  }
  return [
    { label: '中间件数量', value: pick('middlewareCount', 'middlewares', 'count') },
    { label: '调用总次数', value: pick('totalCalls', 'callCount', 'calls', 'invocations') },
    { label: '改写次数', value: pick('rewriteCount', 'rewrites', 'modified', 'modifiedCount') },
    { label: '最后改写时间', value: pick('lastRewriteTime', 'lastModifiedAt', 'lastTime', 'updatedAt') }
  ]
})

async function refreshDetail() {
  const card = detailCard.value
  if (!card) return null
  try {
    await card.load()
    detailSnapshot.value = card.detail || null
    return detailSnapshot.value
  } catch (e) {
    compareError.value = e?.message || '加载中间件详情失败'
    return null
  }
}

async function sendAndCompare() {
  if (comparing.value) return
  comparing.value = true
  compareError.value = ''
  try {
    await refreshDetail()
    const before = pickRecords(detailSnapshot.value)
    beforeCount.value = before.length

    await chatBox.value?.send()

    await refreshDetail()
    const after = pickRecords(detailSnapshot.value)
    newRecords.value = after.slice(before.length)
  } catch (e) {
    compareError.value = e?.message || '对照演示失败'
  } finally {
    comparing.value = false
  }
}
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="success"
      :closable="false"
      show-icon
      title="Middleware 位于系统提示词与模型调用之间，可统一改写提示词、注入上下文、统计调用量。"
    />

    <CapabilityChatBox ref="chatBox" capability-id="middleware" v-model:prompt="prompt" />

    <el-card shadow="never" class="mt12">
      <template #header>
        <div class="card-header">
          <span>改写统计</span>
          <el-button link type="primary" :loading="comparing" @click="sendAndCompare">
            发送并对比
          </el-button>
        </div>
      </template>

      <el-alert v-if="compareError" type="error" :closable="false" :title="compareError" class="mb8" />

      <el-descriptions :column="4" border size="small">
        <el-descriptions-item v-for="item in stats" :key="item.label" :label="item.label">
          {{ item.value }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" class="mt12">
      <template #header>
        <div class="card-header">
          <span>最后一次系统提示词（中间件改写后）</span>
          <el-button link type="primary" @click="refreshDetail">刷新</el-button>
        </div>
      </template>

      <el-input
        type="textarea"
        :rows="8"
        readonly
        resize="none"
        placeholder="暂无系统提示词：发起一次对话后，中间件会记录改写结果"
        :model-value="lastSystemPrompt"
      />
    </el-card>

    <el-card shadow="never" class="mt12">
      <template #header>
        <div class="card-header">
          <span>对照区：本次对话新增的中间件记录</span>
          <span class="tip">调用前 {{ beforeCount < 0 ? '—' : beforeCount }} 条</span>
        </div>
      </template>

      <el-empty
        v-if="!newRecordItems.length"
        description="点击「发送并对比」，查看本次对话触发的中间件改写记录"
        :image-size="80"
      />
      <el-timeline v-else>
        <el-timeline-item
          v-for="(item, index) in newRecordItems"
          :key="index"
          :timestamp="item.time || '未记录时间'"
          placement="top"
          type="primary"
        >
          <div class="timeline-title">{{ item.title }}</div>
          <pre v-if="item.desc" class="timeline-desc">{{ item.desc }}</pre>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <CapabilityDetailCard ref="detailCard" capability-id="middleware" title="Middleware 能力详情" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.tip {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
.timeline-title {
  font-weight: 600;
  font-size: 13px;
}
.timeline-desc {
  margin: 4px 0 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
  line-height: 1.6;
  color: var(--el-text-color-regular);
  background: var(--el-fill-color-light);
  border-radius: 4px;
  padding: 6px 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
