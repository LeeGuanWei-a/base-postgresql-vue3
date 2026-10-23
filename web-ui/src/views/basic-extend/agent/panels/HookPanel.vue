<script setup name="HookPanel">
/**
 * Hook 拦截 —— 展示 AgentScope Hook 在「模型调用 / 工具调用」前后拦截到的事件流水。
 *
 * 演示要点：
 * 1. 「Hook 事件流水」卡片：后端 /agentscope/capability/hook/detail 的原始详情；
 * 2. 「对照区」：记录发起对话之前的事件条数 → 发起一次对话 → 用 el-timeline 列出新增事件。
 *
 * 说明：后端详情字段名尚未最终定稿，这里对 events / hooks / records / logs 等多种命名做兼容取值。
 */
import { computed, ref } from 'vue'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref('请查询一下平台当前的用户数量，并调用一次工具。')

const chatBox = ref(null)
const detailCard = ref(null)

/** 详情快照（避免依赖子组件内部响应式） */
const detailSnapshot = ref(null)
/** 发起对话之前的事件条数，-1 表示尚未发起过 */
const beforeCount = ref(-1)
/** 本次对话新增的事件 */
const newEvents = ref([])
const comparing = ref(false)
const compareError = ref('')

/**
 * 从详情对象中提取事件数组 —— 兼容后端多种字段命名。
 */
function pickEvents(detail) {
  if (!detail || typeof detail !== 'object') return []
  const candidates = [
    detail.events,
    detail.hooks,
    detail.records,
    detail.logs,
    detail.log,
    detail.timeline,
    detail.list,
    detail.items
  ]
  for (const item of candidates) {
    if (Array.isArray(item)) return item
  }
  return []
}

/** 把一条事件归一化成时间线上可展示的结构 */
function normalizeEvent(item, index) {
  if (typeof item === 'string') {
    return { title: item, time: '', desc: '' }
  }
  if (!item || typeof item !== 'object') {
    return { title: String(item ?? ''), time: '', desc: '' }
  }
  const title =
    item.name || item.event || item.hook || item.type || item.stage || item.action || `事件 #${index + 1}`
  const time = item.time || item.timestamp || item.at || item.createTime || item.occurredAt || ''
  const raw =
    item.payload ?? item.data ?? item.detail ?? item.result ?? item.message ?? item.args ?? item.input ?? ''
  let desc = ''
  if (raw !== null && raw !== undefined && raw !== '') {
    desc = typeof raw === 'string' ? raw : JSON.stringify(raw, null, 2)
  }
  return { title: String(title), time: String(time), desc }
}

const newEventItems = computed(() => newEvents.value.map(normalizeEvent))

const currentCount = computed(() => pickEvents(detailSnapshot.value).length)

/** 重新拉取详情（调用子组件暴露的 load()，成功后再取快照） */
async function refreshDetail() {
  const card = detailCard.value
  if (!card) return null
  try {
    await card.load()
    detailSnapshot.value = card.detail || null
    return detailSnapshot.value
  } catch (e) {
    compareError.value = e?.message || '加载 Hook 事件流水失败'
    return null
  }
}

/** 发起一次对话，并对比出新增的 Hook 事件 */
async function sendAndCompare() {
  if (comparing.value) return
  comparing.value = true
  compareError.value = ''
  try {
    await refreshDetail()
    const before = pickEvents(detailSnapshot.value)
    beforeCount.value = before.length

    await chatBox.value?.send()

    await refreshDetail()
    const after = pickEvents(detailSnapshot.value)
    newEvents.value = after.slice(before.length)
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
      title="Hook 在模型调用 / 工具调用前后插入拦截点，把事件写入流水；下方对照区可看到一次对话新增了哪些事件。"
    />

    <CapabilityChatBox ref="chatBox" capability-id="hook" v-model:prompt="prompt" />

    <el-card shadow="never" class="mt12">
      <template #header>
        <div class="card-header">
          <span>对照区：发起一次对话 → 查看新增的 Hook 事件</span>
          <div>
            <el-button link type="primary" :loading="comparing" @click="sendAndCompare">
              发送并对比
            </el-button>
            <el-button link type="primary" @click="refreshDetail">刷新事件流水</el-button>
          </div>
        </div>
      </template>

      <el-alert v-if="compareError" type="error" :closable="false" :title="compareError" class="mb8" />

      <el-descriptions :column="3" border size="small" class="mb8">
        <el-descriptions-item label="当前事件总数">{{ currentCount }}</el-descriptions-item>
        <el-descriptions-item label="调用前事件数">
          {{ beforeCount < 0 ? '尚未发起对比' : beforeCount }}
        </el-descriptions-item>
        <el-descriptions-item label="本次新增事件">{{ newEvents.length }}</el-descriptions-item>
      </el-descriptions>

      <el-empty
        v-if="!newEventItems.length"
        description="点击「发送并对比」，观察一次对话触发了哪些 Hook 事件"
        :image-size="80"
      />
      <el-timeline v-else>
        <el-timeline-item
          v-for="(item, index) in newEventItems"
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

    <CapabilityDetailCard ref="detailCard" capability-id="hook" title="Hook 事件流水（能力详情）" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
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
