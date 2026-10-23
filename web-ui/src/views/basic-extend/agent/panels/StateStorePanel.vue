<script setup name="StateStorePanel">
/**
 * 状态持久化（State Store）—— 展示会话与状态文件的落盘情况。
 *
 * 演示要点：
 * 1. 持久化概览：会话数量、状态文件数量、存储位置；
 * 2. 上下文接续演示：固定同一个 sessionId 连问两次，第二次应能接上第一次的上下文。
 *
 * 注意：通用对话框的 sessionId 是组件内部状态，这里通过组件暴露的实例回写实现「预填」。
 */
import { computed, nextTick, onMounted, ref } from 'vue'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref('我叫小明，我的工号是 10086，请记住我的名字。')

const chatBox = ref(null)
const detailCard = ref(null)

/** 演示用的固定会话 ID —— 两次提问必须一致才能接上上下文 */
const sessionId = ref('demo-state-store-001')
const detailSnapshot = ref(null)
const appliedSession = ref('')

const step1 = ref('我叫小明，我的工号是 10086，请记住我的名字。')
const step2 = ref('我叫什么名字？我的工号是多少？')

/** 从详情中取会话列表 */
function pickSessions(detail) {
  if (!detail || typeof detail !== 'object') return []
  for (const key of ['sessions', 'sessionList', 'conversationList', 'list', 'items']) {
    if (Array.isArray(detail[key])) return detail[key]
  }
  return []
}

/** 从详情中取状态文件列表 */
function pickFiles(detail) {
  if (!detail || typeof detail !== 'object') return []
  for (const key of ['files', 'stateFiles', 'fileList', 'stores', 'list', 'items']) {
    if (Array.isArray(detail[key])) return detail[key]
  }
  return []
}

const sessionRows = computed(() =>
  pickSessions(detailSnapshot.value).map((item) =>
    typeof item === 'string'
      ? { id: item, turns: '-', updatedAt: '-', file: '-' }
      : {
          id: item.sessionId || item.id || item.name || '-',
          turns: item.turns ?? item.messages ?? item.messageCount ?? '-',
          updatedAt: item.updatedAt || item.updateTime || item.lastActive || '-',
          file: item.file || item.path || item.storeFile || '-'
        }
  )
)

const fileRows = computed(() =>
  pickFiles(detailSnapshot.value).map((item) =>
    typeof item === 'string'
      ? { name: item, size: '-', updatedAt: '-' }
      : {
          name: item.name || item.file || item.path || '-',
          size: item.size ?? '-',
          updatedAt: item.updatedAt || item.updateTime || item.modifiedAt || '-'
        }
  )
)

const summary = computed(() => {
  const detail = detailSnapshot.value || {}
  const pick = (...keys) => {
    for (const key of keys) {
      if (detail[key] !== undefined && detail[key] !== null && detail[key] !== '') return detail[key]
    }
    return '-'
  }
  return [
    { label: '持久化会话数', value: pick('sessionCount', 'sessions', 'totalSessions') },
    { label: '状态文件数', value: pick('fileCount', 'files', 'totalFiles') },
    { label: '存储位置', value: pick('storePath', 'root', 'directory', 'path', 'location') },
    { label: '是否启用', value: pick('enabled', 'persisted', 'active') }
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
    return null
  }
}

/** 把演示用的 sessionId 预填进通用对话框 */
function applySession() {
  const box = chatBox.value
  if (!box) return
  try {
    box.sessionId = sessionId.value
    appliedSession.value = sessionId.value
  } catch (e) {
    appliedSession.value = ''
  }
}

function onMountedApply() {
  nextTick(() => {
    applySession()
  })
}

onMounted(onMountedApply)
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="success"
      :closable="false"
      show-icon
      title="State Store 把会话与中间状态落盘：同一个 sessionId 再次提问时，Agent 会自动恢复上文的上下文。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>持久化概览</span>
          <el-button link type="primary" @click="refreshDetail">刷新</el-button>
        </div>
      </template>

      <el-descriptions :column="4" border size="small">
        <el-descriptions-item v-for="item in summary" :key="item.label" :label="item.label">
          {{ item.value }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>上下文接续演示（同一个 sessionId 连问两次）</span>
      </template>

      <el-form label-width="90px" class="demo-form">
        <el-form-item label="会话 ID">
          <el-input
            v-model="sessionId"
            class="session-input"
            placeholder="请填写会话 ID"
            clearable
            @change="applySession"
          />
          <el-button type="primary" plain size="small" class="ml8" @click="applySession">应用到对话框</el-button>
        </el-form-item>
        <el-form-item label="第一步">
          <el-tag effect="plain" class="step-tag" @click="prompt = step1">{{ step1 }}</el-tag>
        </el-form-item>
        <el-form-item label="第二步">
          <el-tag effect="plain" type="success" class="step-tag" @click="prompt = step2">{{ step2 }}</el-tag>
        </el-form-item>
      </el-form>

      <el-alert
        class="mb8"
        type="info"
        :closable="false"
        show-icon
        :title="'当前生效的会话 ID：' + (appliedSession || '尚未应用，请在输入框中填写后点击「应用到对话框」')"
      />

      <CapabilityChatBox ref="chatBox" capability-id="state-store" v-model:prompt="prompt" />
    </el-card>

    <el-card shadow="never" class="mt12">
      <template #header>
        <span>已持久化的会话</span>
      </template>
      <el-table :data="sessionRows" border stripe size="small" empty-text="暂无持久化会话">
        <el-table-column prop="id" label="会话 ID" min-width="180" show-overflow-tooltip />
        <el-table-column prop="turns" label="轮次" width="90" />
        <el-table-column prop="updatedAt" label="最近更新" width="180" />
        <el-table-column prop="file" label="状态文件" min-width="180" show-overflow-tooltip />
      </el-table>
    </el-card>

    <el-card shadow="never" class="mt12">
      <template #header>
        <span>状态文件</span>
      </template>
      <el-table :data="fileRows" border stripe size="small" empty-text="暂无状态文件">
        <el-table-column prop="name" label="文件名" min-width="220" show-overflow-tooltip />
        <el-table-column prop="size" label="大小" width="120" />
        <el-table-column prop="updatedAt" label="更新时间" width="180" />
      </el-table>
    </el-card>

    <CapabilityDetailCard ref="detailCard" capability-id="state-store" title="状态存储能力详情" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.demo-form {
  margin-bottom: 4px;
}
.session-input {
  width: 280px;
}
.step-tag {
  cursor: pointer;
  max-width: 100%;
  height: auto;
  white-space: normal;
  line-height: 1.6;
  padding: 4px 10px;
}
.ml8 {
  margin-left: 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
