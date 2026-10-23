<script setup name="CapabilityChatBox">
/**
 * 通用能力对话框 —— 所有能力面板共用的交互外壳。
 *
 * 统一负责：prompt 输入、会话 ID、SSE 开关、发送、结果展示（文本 / 工具调用 / 结构化数据 / 耗时 / 模型）。
 */
import { computed, ref, watch } from 'vue'
import { capabilityChat, capabilityStream } from '@/api/agentscope'

const props = defineProps({
  /** 能力 id（用于拼接口路径） */
  capabilityId: { type: String, required: true },
  /** 覆盖默认的对话地址（不传则用 /agentscope/capability/{id}/chat） */
  endpoint: { type: String, default: '' },
  /** 是否支持 SSE */
  supportsStream: { type: Boolean, default: true },
  /** 默认 prompt */
  defaultPrompt: { type: String, default: '' },
  /** 额外的 options，随请求一起下发 */
  extraOptions: { type: Object, default: () => ({}) },
  /** 是否显示会话 ID 输入 */
  showSession: { type: Boolean, default: true },
  /** 紧凑模式（面板里空间有限时） */
  compact: { type: Boolean, default: false }
})

const prompt = defineModel('prompt', { type: String, default: '' })
const sessionId = ref('')
const useStream = ref(false)
const loading = ref(false)
const result = ref(null)
const streamText = ref('')
const errorMsg = ref('')
let controller = null

watch(
  () => props.defaultPrompt,
  (val) => {
    if (val && !prompt.value) prompt.value = val
  },
  { immediate: true }
)

const chatUrl = computed(() => props.endpoint || `/agentscope/capability/${props.capabilityId}/chat`)

const resultText = computed(() => {
  if (streamText.value) return streamText.value
  return result.value?.text || ''
})

const toolCalls = computed(() => result.value?.toolCalls || [])

function buildPayload() {
  return {
    prompt: prompt.value,
    sessionId: sessionId.value || undefined,
    options: props.extraOptions
  }
}

async function send() {
  if (!prompt.value || loading.value) return
  errorMsg.value = ''
  result.value = null
  streamText.value = ''

  if (useStream.value && props.supportsStream) {
    await sendStream()
    return
  }

  loading.value = true
  try {
    const res = await capabilityChat(props.capabilityId, buildPayload())
    result.value = res.data
  } catch (e) {
    errorMsg.value = e?.message || '请求失败'
  } finally {
    loading.value = false
  }
}

async function sendStream() {
  loading.value = true
  controller = new AbortController()
  try {
    await capabilityStream(
      props.capabilityId,
      { prompt: prompt.value, sessionId: sessionId.value || undefined },
      (msg) => {
        streamText.value += msg + '\n'
      },
      controller.signal
    )
  } catch (e) {
    errorMsg.value = e?.message || '流式请求失败'
  } finally {
    loading.value = false
    controller = null
  }
}

function stopStream() {
  if (controller) {
    controller.abort()
    controller = null
    loading.value = false
  }
}

function reset() {
  prompt.value = props.defaultPrompt
  result.value = null
  streamText.value = ''
  errorMsg.value = ''
}

defineExpose({ send, stopStream, reset, result, loading, prompt, sessionId })
</script>

<template>
  <div class="cap-chat-box" :class="{ compact }">
    <div class="cap-chat-input">
      <el-input
        v-model="prompt"
        type="textarea"
        :rows="compact ? 2 : 3"
        placeholder="请输入问题，回车发送（Shift+Enter 换行）"
        @keyup.enter.exact.prevent="send"
      />
    </div>

    <div class="cap-chat-actions">
      <el-input
        v-if="showSession"
        v-model="sessionId"
        class="cap-session-input"
        placeholder="会话 ID（留空=新会话）"
        clearable
      />
      <el-checkbox v-if="supportsStream" v-model="useStream" label="SSE 流式" />
      <div class="cap-chat-buttons">
        <el-button v-if="loading && useStream" @click="stopStream">停止</el-button>
        <el-button type="primary" :loading="loading" @click="send">发送</el-button>
        <el-button @click="reset">重置</el-button>
      </div>
    </div>

    <slot name="extra-actions" />

    <el-alert v-if="errorMsg" class="cap-chat-error" type="error" :closable="false" :title="errorMsg" />

    <div v-if="result || streamText" class="cap-chat-result">
      <div class="cap-result-meta">
        <el-tag v-if="result?.mock" type="warning" size="small">Mock 模型</el-tag>
        <el-tag v-else type="success" size="small">真实模型</el-tag>
        <el-tag size="small" type="info">{{ result?.agentName || '-' }}</el-tag>
        <el-tag size="small" type="info">{{ result?.modelName || '-' }}</el-tag>
        <span v-if="result?.elapsedMs != null" class="cap-elapsed">{{ result.elapsedMs }} ms</span>
        <span v-if="result?.sessionId" class="cap-elapsed">会话 {{ result.sessionId }}</span>
      </div>

      <pre v-if="resultText" class="cap-result-text">{{ resultText }}</pre>

      <div v-if="toolCalls.length" class="cap-tool-calls">
        <div class="cap-section-title">工具调用（{{ toolCalls.length }}）</div>
        <el-table :data="toolCalls" size="small" border>
          <el-table-column prop="name" label="工具" width="200" />
          <el-table-column prop="input" label="入参" show-overflow-tooltip />
          <el-table-column prop="output" label="返回" show-overflow-tooltip />
        </el-table>
      </div>

      <div v-if="result?.structured" class="cap-structured">
        <div class="cap-section-title">结构化输出</div>
        <pre class="cap-result-text">{{ JSON.stringify(result.structured, null, 2) }}</pre>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cap-chat-box {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.cap-chat-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.cap-session-input {
  width: 240px;
}
.cap-chat-buttons {
  display: flex;
  gap: 8px;
  margin-left: auto;
}
.cap-chat-error {
  margin: 0;
}
.cap-chat-result {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  padding: 10px 12px;
  background: var(--el-fill-color-blank);
}
.cap-result-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}
.cap-elapsed {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
.cap-result-text {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
  line-height: 1.6;
  max-height: 420px;
  overflow: auto;
}
.cap-tool-calls,
.cap-structured {
  margin-top: 12px;
}
.cap-section-title {
  font-size: 13px;
  color: var(--el-text-color-regular);
  margin-bottom: 6px;
  font-weight: 600;
}
</style>
