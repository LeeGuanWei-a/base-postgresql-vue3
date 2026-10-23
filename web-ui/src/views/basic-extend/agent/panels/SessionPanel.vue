<script setup name="SessionPanel">
/**
 * 会话管理 —— 演示 AgentScope 的会话（Session）生命周期。
 *
 * 上半部分：GET /agentscope/session/list 拉取会话列表，列按后端实际返回字段自适应。
 *          支持新建会话（生成随机 sessionId）与删除会话（DELETE /agentscope/session/{id}）。
 * 下半部分：通用对话框，会话 ID 默认跟随表格中选中的那一行（也可在对话框内自行修改）。
 */
import { computed, onMounted, ref } from 'vue'
import { sessionList, sessionRemove } from '@/api/agentscope'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref('你好，帮我看下这个会话的上下文。')

const rows = ref([])
const loading = ref(false)
const errorMsg = ref('')
const selected = ref(null)
const chatRef = ref(null)
const newSessionId = ref('')
const listMeta = ref({})

/** 已知字段的中文列名 */
const LABEL_MAP = {
  sessionId: '会话 ID',
  session_id: '会话 ID',
  sessionid: '会话 ID',
  id: '会话 ID',
  key: '会话 ID',
  isDefault: '默认会话',
  name: '名称',
  title: '标题',
  status: '状态',
  state: '状态',
  messageCount: '消息数',
  message_count: '消息数',
  messageNum: '消息数',
  messages: '消息数',
  msgCount: '消息数',
  count: '消息数',
  size: '消息数',
  total: '消息数',
  memoryCount: '记忆条数',
  memory: '记忆条数',
  createdAt: '创建时间',
  createTime: '创建时间',
  created_time: '创建时间',
  created: '创建时间',
  create_time: '创建时间',
  startTime: '创建时间',
  timestamp: '创建时间',
  updatedAt: '更新时间',
  updateTime: '更新时间',
  lastModified: '更新时间',
  lastActive: '最后活跃',
  lastActiveAt: '最后活跃',
  lastMessage: '最后一条消息',
  preview: '摘要',
  summary: '摘要',
  userId: '用户',
  user: '用户',
  agentName: 'Agent',
  agent: 'Agent',
  model: '模型',
  modelName: '模型'
}

/** 列优先级（越靠前越先展示） */
const PRIORITY = [
  'sessionId',
  'session_id',
  'id',
  'isDefault',
  'status',
  'state',
  'messageCount',
  'message_count',
  'messages',
  'count',
  'size',
  'total',
  'createdAt',
  'createTime',
  'created',
  'startTime',
  'timestamp',
  'updatedAt',
  'lastActive',
  'title',
  'name',
  'userId',
  'agentName',
  'modelName'
]

/** 统一成数组（兼容裸数组 / { list } / { records } / { sessions } 等） */
function toArray(data) {
  if (!data) return []
  if (Array.isArray(data)) return data
  const candidates = [data.list, data.records, data.sessions, data.items, data.rows, data.data]
  for (const c of candidates) {
    if (Array.isArray(c)) return c
  }
  return []
}

/** 取出会话 ID（兼容多种字段名） */
function pickSessionId(row) {
  if (!row) return ''
  if (typeof row === 'string') return row
  return row.sessionId || row.session_id || row.sessionid || row.id || row.key || ''
}

const columns = computed(() => {
  const keys = []
  rows.value.forEach((r) => {
    if (r && typeof r === 'object') {
      Object.keys(r).forEach((k) => {
        if (!keys.includes(k)) keys.push(k)
      })
    }
  })
  const sorted = [...keys].sort((a, b) => {
    const ia = PRIORITY.indexOf(a)
    const ib = PRIORITY.indexOf(b)
    if (ia === -1 && ib === -1) return a.localeCompare(b)
    if (ia === -1) return 1
    if (ib === -1) return -1
    return ia - ib
  })
  const main = sorted.slice(0, 6).map((k) => ({ prop: k, label: LABEL_MAP[k] || k }))
  const rest = sorted.slice(6)
  return { main, rest }
})

/** 单元格文本：对象/数组转 JSON，布尔转是/否 */
function cellText(value) {
  if (value === null || value === undefined || value === '') return '-'
  if (typeof value === 'boolean') return value ? '是' : '否'
  if (typeof value === 'object') {
    const text = JSON.stringify(value)
    return text.length > 120 ? text.slice(0, 120) + '…' : text
  }
  return String(value)
}

function extraText(row) {
  if (!row || typeof row !== 'object') return '-'
  const obj = {}
  columns.value.rest.forEach((k) => {
    obj[k] = row[k]
  })
  return Object.keys(obj).length ? JSON.stringify(obj) : '-'
}

/** 状态标签样式 */
function statusType(value) {
  const s = String(value ?? '').toUpperCase()
  if (!s) return 'info'
  if (['ACTIVE', 'RUNNING', 'ONLINE', 'ALIVE', 'TRUE'].includes(s)) return 'success'
  if (['INACTIVE', 'IDLE', 'OFFLINE', 'CLOSED', 'FALSE', 'EXPIRED'].includes(s)) return 'info'
  if (['ERROR', 'FAILED', 'DEAD'].includes(s)) return 'danger'
  return 'warning'
}

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await sessionList()
    const data = res.data || {}
    rows.value = toArray(data)
    listMeta.value = {
      defaultSessionId: data.defaultSessionId,
      stateStoreType: data.stateStoreType,
      sessionCount: data.sessionCount
    }
  } catch (e) {
    errorMsg.value = e?.message || '会话列表加载失败'
    rows.value = []
  } finally {
    loading.value = false
  }
}

function selectRow(row) {
  selected.value = row
  const id = pickSessionId(row)
  if (id && chatRef.value) {
    chatRef.value.sessionId = id
  }
}

/** 新建会话：生成一个随机 sessionId 填到对话框的会话输入框 */
function createSession() {
  const rand = Math.random().toString(16).slice(2, 10).padEnd(8, '0')
  const id = 'sess-' + rand
  newSessionId.value = id
  if (chatRef.value) {
    chatRef.value.sessionId = id
  }
  selected.value = null
}

async function removeSession(row) {
  const id = pickSessionId(row)
  if (!id) {
    errorMsg.value = '该行没有可用的会话 ID，无法删除'
    return
  }
  try {
    await sessionRemove(id)
    await load()
  } catch (e) {
    errorMsg.value = e?.message || '删除会话失败'
  }
}

onMounted(load)
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="success"
      :closable="false"
      show-icon
      title="会话（Session）承载多轮上下文：同一个 sessionId 复用同一份记忆与任务清单；删除会话即清空它的上下文。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>会话列表</span>
          <div class="card-header-btns">
            <el-button type="primary" size="small" @click="createSession">新建会话</el-button>
            <el-button size="small" :loading="loading" @click="load">刷新</el-button>
          </div>
        </div>
      </template>

      <div class="toolbar">
        <el-input v-model="newSessionId" class="new-session-input" placeholder="新建会话 ID（点击「新建会话」自动生成）" clearable />
        <el-tag v-if="newSessionId" size="small" type="success">待使用：{{ newSessionId }}</el-tag>
        <el-tag v-if="selected" size="small" type="primary">
          已选中：{{ pickSessionId(selected) || '-' }}
        </el-tag>
      </div>

      <el-alert v-if="errorMsg" class="mt8" type="error" :closable="false" :title="errorMsg" />

      <el-descriptions
        v-if="listMeta.defaultSessionId || listMeta.stateStoreType || listMeta.sessionCount != null"
        class="mb8"
        :column="3"
        size="small"
        border
      >
        <el-descriptions-item label="默认会话">{{ listMeta.defaultSessionId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态存储">{{ listMeta.stateStoreType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="会话数">{{ listMeta.sessionCount ?? rows.length }}</el-descriptions-item>
      </el-descriptions>

      <el-table
        v-loading="loading"
        :data="rows"
        border
        stripe
        size="small"
        highlight-current-row
        @current-change="selectRow"
      >
        <el-table-column
          v-for="col in columns.main"
          :key="col.prop"
          :prop="col.prop"
          :label="col.label"
          min-width="140"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <el-tag v-if="col.prop === 'status' || col.prop === 'state'" size="small" :type="statusType(row[col.prop])">
              {{ cellText(row[col.prop]) }}
            </el-tag>
            <span v-else>{{ cellText(row[col.prop]) }}</span>
          </template>
        </el-table-column>

        <el-table-column v-if="columns.rest.length" label="其它字段" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ extraText(row) }}</template>
        </el-table-column>

        <el-table-column label="操作" width="110" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" size="small" @click.stop="removeSession(row)">删除</el-button>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无会话，先在下方对话框发起一轮对话" :image-size="70" />
        </template>
      </el-table>
    </el-card>

    <CapabilityChatBox ref="chatRef" capability-id="session" v-model:prompt="prompt" />

    <CapabilityDetailCard capability-id="session" title="会话能力详情" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-header-btns {
  display: flex;
  gap: 8px;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}
.new-session-input {
  width: 300px;
}
.mt8 {
  margin-top: 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
