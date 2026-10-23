<script setup name="InterruptPanel">
/**
 * 中断运行（Interrupt）—— 演示如何打断一个正在执行的 Agent。
 *
 * 后端契约（InterruptCapabilityController）：
 * - POST /agentscope/interrupt/chat            body: { prompt, sessionId, options: { seconds } }
 *        发起一次耗时对话，期间该会话处于「运行中」；options.seconds 可控制耗时
 * - POST /agentscope/interrupt/stop?sessionId  返回 { sessionId, wasRunning, interrupted, interruptCount, message }
 * - GET  /agentscope/interrupt/status?sessionId 返回 { sessionId, running, controlled, interruptCount, lastResult, lastError }
 */
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { interruptChat, interruptStatus, interruptStop } from '@/api/agentscope'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const sessionId = ref('')
const seconds = ref(30)
const prompt = ref('请执行一个耗时 30 秒的慢任务，用于演示中断。')

const stopping = ref(false)
const querying = ref(false)
const chatting = ref(false)
const errorMsg = ref('')
const stopResult = ref(null)
const statusRaw = ref(null)
const chatResult = ref(null)
let timer = null

/** 归一化状态：兼容 { running } / { active } / { status } / { interrupted } */
const status = computed(() => {
  const d = statusRaw.value
  if (d === null || d === undefined) return null
  if (typeof d === 'boolean') return { running: d, label: d ? '运行中' : '未运行' }
  if (typeof d === 'string') {
    const s = d.toUpperCase()
    const running = ['RUNNING', 'ACTIVE', 'BUSY', 'EXECUTING'].includes(s)
    return { running, label: d }
  }
  if (typeof d === 'object') {
    if (typeof d.running === 'boolean') return { running: d.running, label: d.running ? '运行中' : '未运行' }
    if (typeof d.active === 'boolean') return { running: d.active, label: d.active ? '运行中' : '未运行' }
    if (typeof d.interrupted === 'boolean') return { running: !d.interrupted, label: d.interrupted ? '已中断' : '未中断' }
    if (d.status) {
      const s = String(d.status).toUpperCase()
      const running = ['RUNNING', 'ACTIVE', 'BUSY', 'EXECUTING'].includes(s)
      return { running, label: String(d.status) }
    }
  }
  return null
})

/** 后端返回的最近一次结果 / 错误 */
const lastResult = computed(() => statusRaw.value?.lastResult || '')
const lastError = computed(() => statusRaw.value?.lastError || '')

async function loadStatus(silent = false) {
  if (!sessionId.value) {
    if (!silent) errorMsg.value = '请先填写会话 ID'
    return
  }
  if (!silent) querying.value = true
  try {
    const res = await interruptStatus(sessionId.value)
    statusRaw.value = res.data
  } catch (e) {
    if (!silent) errorMsg.value = e?.message || '查询中断状态失败'
    statusRaw.value = null
  } finally {
    if (!silent) querying.value = false
  }
}

/** 中断 */
async function stop() {
  if (!sessionId.value) {
    errorMsg.value = '请先填写会话 ID'
    return
  }
  stopping.value = true
  errorMsg.value = ''
  try {
    const res = await interruptStop(sessionId.value)
    stopResult.value = res.data ?? res
  } catch (e) {
    errorMsg.value = e?.message || '中断失败'
  } finally {
    stopping.value = false
    await loadStatus(true)
  }
}

/**
 * 发起一次慢任务对话，用于演示「运行中 → 被中断」。
 *
 * 走 @/api/agentscope.js 的 interruptChat()：
 * 1) 该接口会先 registerSession(sessionId, agent)，否则 interrupt() 找不到受控 Agent；
 * 2) 超时已放大到 3 分钟（默认 10s 会提前超时）；
 * 3) body 里带上 options.seconds 控制慢任务耗时。
 */
async function startLongChat() {
  if (!sessionId.value) {
    errorMsg.value = '请先填写会话 ID'
    return
  }
  chatting.value = true
  errorMsg.value = ''
  chatResult.value = null
  startPolling()
  try {
    const res = await interruptChat({
      prompt: prompt.value,
      sessionId: sessionId.value,
      options: { seconds: Number(seconds.value) || 30 }
    })
    chatResult.value = res.data ?? res
  } catch (e) {
    errorMsg.value = e?.message || '慢任务对话失败（被中断属于正常现象）'
  } finally {
    chatting.value = false
    stopPolling()
    await loadStatus(true)
  }
}

/** 对话进行中每 2 秒轮询一次状态，让「运行中」实时可见 */
function startPolling() {
  stopPolling()
  timer = setInterval(() => loadStatus(true), 2000)
}

function stopPolling() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

onUnmounted(stopPolling)

onMounted(() => {
  // 后端默认会话 ID 就是 "default"（InterruptCapability.DEFAULT_SESSION_ID）
  sessionId.value = 'default'
})
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="warning"
      :closable="false"
      show-icon
      title="要看到真实的中断效果：先点「发起慢任务」让该会话跑起来（运行期间状态显示「运行中」），再点「中断运行」把它打断。"
    />
    <el-alert
      class="mb8"
      type="info"
      :closable="false"
      show-icon
      title="也可以在另一个标签页用同一个 sessionId 发起一次慢对话，再回到本页中断；两种方式最终都走 POST /agentscope/interrupt/stop。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>中断运行中的 Agent</span>
      </template>

      <div class="toolbar">
        <el-input
          v-model="sessionId"
          class="session-input"
          placeholder="会话 ID，例如 sess-1a2b3c4d"
          clearable
        >
          <template #prepend>会话</template>
        </el-input>
        <el-input-number v-model="seconds" :min="5" :max="120" :step="5" controls-position="right" />
        <span class="unit">秒</span>
        <el-button type="primary" :loading="chatting" @click="startLongChat">
          <el-icon><component :is="'VideoPlay'" /></el-icon>
          <span>发起慢任务</span>
        </el-button>
        <el-button type="danger" :loading="stopping" @click="stop">
          <el-icon><component :is="'VideoPause'" /></el-icon>
          <span>中断运行</span>
        </el-button>
        <el-button :loading="querying" @click="loadStatus()">
          <el-icon><component :is="'Refresh'" /></el-icon>
          <span>查询状态</span>
        </el-button>
      </div>

      <el-alert v-if="errorMsg" class="mt8" type="error" :closable="false" show-icon :title="errorMsg" />

      <el-descriptions :column="4" size="small" border class="mt12">
        <el-descriptions-item label="会话 ID">{{ sessionId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="运行状态">
          <el-tag v-if="status" size="small" :type="status.running ? 'warning' : 'success'">
            {{ status.label }}
          </el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="受控中">
          <el-tag v-if="statusRaw" size="small" :type="statusRaw.controlled ? 'primary' : 'info'">
            {{ statusRaw.controlled ? '是' : '否' }}
          </el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="中断次数">{{ statusRaw?.interruptCount ?? '-' }}</el-descriptions-item>
      </el-descriptions>

      <div v-if="lastResult || lastError" class="section-title mt12">最近一次执行结果</div>
      <pre v-if="lastResult" class="pre">{{ lastResult }}</pre>
      <pre v-if="lastError" class="pre error">{{ lastError }}</pre>

      <div class="section-title mt12">中断返回结果</div>
      <pre v-if="stopResult" class="pre">{{ JSON.stringify(stopResult, null, 2) }}</pre>
      <el-empty v-else description="尚未执行中断" :image-size="70" />

      <div class="section-title mt12">状态原始返回</div>
      <pre v-if="statusRaw !== null" class="pre">{{ JSON.stringify(statusRaw, null, 2) }}</pre>
      <el-empty v-else description="尚未查询状态" :image-size="70" />
    </el-card>

    <CapabilityDetailCard capability-id="interrupt" title="中断能力详情" class="mt12" />
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.session-input {
  width: 300px;
}
.unit {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-regular);
  margin-bottom: 6px;
}
.pre {
  margin: 0 0 8px;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
  line-height: 1.6;
  max-height: 260px;
  overflow: auto;
  background: var(--el-fill-color-light);
  padding: 8px 10px;
  border-radius: 4px;
}
.pre.error {
  color: var(--el-color-danger);
}
.mb8 {
  margin-bottom: 8px;
}
.mt8 {
  margin-top: 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
