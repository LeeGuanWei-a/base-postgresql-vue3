<script setup name="StructuredPanel">
/**
 * 结构化输出 —— 演示 AgentScope 的结构化响应（Structured Output）。
 *
 * 该能力有专属接口 POST /agentscope/structured/call（不走通用 chat），
 * 因此这里不使用 CapabilityChatBox，而是自己实现输入 + 结果展示。
 */
import { computed, ref } from 'vue'
import { structuredCall } from '@/api/agentscope'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref('请把本平台的登录流程整理成一份结构化说明。')
const sessionId = ref('')
const loading = ref(false)
const errorMsg = ref('')
const result = ref(null)

const structured = computed(() => result.value?.structured || null)

/** 置信度：兼容 0-1 与 0-100 两种量纲，统一换算成百分比 */
const confidencePercent = computed(() => {
  const c = Number(structured.value?.confidence)
  if (!Number.isFinite(c)) return null
  if (c <= 0) return 0
  if (c <= 1) return Math.round(c * 100)
  return Math.min(100, Math.round(c))
})

const confidenceStatus = computed(() => {
  const p = confidencePercent.value
  if (p === null) return ''
  if (p >= 80) return 'success'
  if (p >= 50) return 'warning'
  return 'exception'
})

/** 标签：兼容字符串数组与逗号分隔字符串 */
const tags = computed(() => {
  const t = structured.value?.tags
  if (!t) return []
  if (Array.isArray(t)) return t.map((x) => (typeof x === 'object' ? JSON.stringify(x) : String(x)))
  return String(t)
    .split(/[,，]/)
    .map((x) => x.trim())
    .filter(Boolean)
})

/** 动作建议：兼容字符串数组与对象数组 */
const actions = computed(() => {
  const a = structured.value?.actions
  if (!a) return []
  if (Array.isArray(a)) return a
  if (typeof a === 'string') {
    return a
      .split(/\n|;|；/)
      .map((x) => x.trim())
      .filter(Boolean)
  }
  return [a]
})

function actionText(item) {
  if (typeof item === 'string') return item
  if (!item || typeof item !== 'object') return String(item)
  return item.title || item.name || item.action || item.desc || JSON.stringify(item)
}

async function send() {
  if (!prompt.value || loading.value) return
  errorMsg.value = ''
  result.value = null
  loading.value = true
  try {
    const res = await structuredCall({
      prompt: prompt.value,
      sessionId: sessionId.value || undefined
    })
    result.value = res.data || {}
  } catch (e) {
    errorMsg.value = e?.message || '结构化调用失败'
  } finally {
    loading.value = false
  }
}

function reset() {
  result.value = null
  errorMsg.value = ''
}
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="success"
      :closable="false"
      show-icon
      title="结构化输出走专属接口 POST /agentscope/structured/call：模型按约定 Schema 返回标题、摘要、标签、置信度与建议动作。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>结构化调用</span>
          <el-tag size="small" type="info">{{ result?.modelName || '未知模型' }}</el-tag>
        </div>
      </template>

      <el-input
        v-model="prompt"
        type="textarea"
        :rows="3"
        placeholder="请输入需要结构化的问题，回车发送（Shift+Enter 换行）"
        @keyup.enter.exact.prevent="send"
      />

      <div class="actions">
        <el-input
          v-model="sessionId"
          class="session-input"
          placeholder="会话 ID（留空=新会话）"
          clearable
        />
        <div class="buttons">
          <el-button type="primary" :loading="loading" @click="send">发送</el-button>
          <el-button @click="reset">重置</el-button>
        </div>
      </div>

      <el-alert v-if="errorMsg" class="mt12" type="error" :closable="false" :title="errorMsg" />

      <div v-if="result" class="result">
        <div class="result-meta">
          <el-tag v-if="result.mock" type="warning" size="small">Mock 模型</el-tag>
          <el-tag v-else type="success" size="small">真实模型</el-tag>
          <el-tag size="small" type="info">{{ result.modelName || '-' }}</el-tag>
          <span v-if="result.elapsedMs != null" class="elapsed">{{ result.elapsedMs }} ms</span>
          <span v-if="result.sessionId" class="elapsed">会话 {{ result.sessionId }}</span>
        </div>

        <div v-if="result.text" class="section">
          <div class="section-title">文本回答</div>
          <pre class="pre">{{ result.text }}</pre>
        </div>

        <el-alert
          v-if="!structured"
          class="mt12"
          type="info"
          :closable="false"
          show-icon
          title="本次没有解析出结构化结果（structured 为空），可能模型未按 Schema 输出。可重试或调整问题描述。"
        />

        <template v-else>
          <div class="section">
            <div class="section-title">结构化结果</div>

            <el-descriptions :column="1" size="small" border>
              <el-descriptions-item label="标题">{{ structured.title || '-' }}</el-descriptions-item>
              <el-descriptions-item label="摘要">{{ structured.summary || '-' }}</el-descriptions-item>
              <el-descriptions-item label="置信度">
                <el-progress
                  v-if="confidencePercent !== null"
                  :percentage="confidencePercent"
                  :status="confidenceStatus"
                  :stroke-width="14"
                  text-inside
                />
                <span v-else>-</span>
              </el-descriptions-item>
              <el-descriptions-item label="标签">
                <el-tag v-for="t in tags" :key="t" class="tag" size="small" effect="plain">{{ t }}</el-tag>
                <span v-if="!tags.length">-</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div v-if="actions.length" class="section">
            <div class="section-title">建议动作（{{ actions.length }}）</div>
            <ol class="actions-list">
              <li v-for="(item, idx) in actions" :key="idx">{{ actionText(item) }}</li>
            </ol>
          </div>

          <div class="section">
            <div class="section-title">原始 JSON</div>
            <pre class="pre">{{ JSON.stringify(structured, null, 2) }}</pre>
          </div>
        </template>
      </div>
    </el-card>

    <CapabilityDetailCard capability-id="structured" title="结构化能力详情（Schema / 字段说明）" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 10px;
}
.session-input {
  width: 240px;
}
.buttons {
  display: flex;
  gap: 8px;
  margin-left: auto;
}
.result {
  margin-top: 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  padding: 10px 12px;
  background: var(--el-fill-color-blank);
}
.result-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}
.elapsed {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
.section {
  margin-top: 12px;
}
.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-regular);
  margin-bottom: 6px;
}
.pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
  line-height: 1.6;
  max-height: 320px;
  overflow: auto;
}
.tag {
  margin: 0 6px 4px 0;
}
.actions-list {
  margin: 0;
  padding-left: 20px;
  font-size: 13px;
  line-height: 1.9;
  color: var(--el-text-color-regular);
}
.mt12 {
  margin-top: 12px;
}
</style>
