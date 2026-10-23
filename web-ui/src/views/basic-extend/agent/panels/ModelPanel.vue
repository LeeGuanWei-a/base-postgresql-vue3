<script setup name="ModelPanel">
/**
 * 多厂商模型 —— 走 /agentscope/model/* 专属接口，不使用通用对话框。
 *
 * 演示要点：
 * 1. 厂商列表：GET /agentscope/model/providers，el-select 的值是厂商 key；
 * 2. 指定厂商对话：POST /agentscope/model/chat，展示 text / modelName / mock / elapsedMs；
 * 3. 各厂商的 base-url / model-name / 环境变量名（能力详情卡片）。
 */
import { computed, onMounted, ref } from 'vue'
import { modelChat, modelProviders } from '@/api/agentscope'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const providers = ref([])
const providerLoading = ref(false)
const providerError = ref('')

const provider = ref('')
const modelName = ref('')
const prompt = ref('用一句话介绍你自己，并说明当前使用的模型。')

const sending = ref(false)
const result = ref(null)
const errorMsg = ref('')

/**
 * 归一化厂商数据 —— 后端可能直接返回数组，也可能返回 { providers: [...] }。
 */
function normalizeProviders(data) {
  let list = []
  if (Array.isArray(data)) {
    list = data
  } else if (data && typeof data === 'object') {
    const candidate = data.providers || data.list || data.items || data.data
    list = Array.isArray(candidate) ? candidate : []
  }
  return list
    .map((item) => {
      if (typeof item === 'string') {
        return { key: item, name: item, baseUrl: '-', modelName: '-', envKey: '-', enabled: true }
      }
      if (!item || typeof item !== 'object') return null
      const key = item.key || item.provider || item.id || item.code || item.name || ''
      if (!key) return null
      return {
        key: String(key),
        name: item.name || item.label || item.displayName || item.title || String(key),
        baseUrl: item.baseUrl || item.base_url || item.endpoint || item.url || '-',
        modelName: item.modelName || item.model_name || item.model || '-',
        envKey:
          item.envKey || item.apiKeyEnv || item.envName || item.env || item.keyEnv || '-',
        enabled: item.enabled !== false,
        note: item.note || item.description || item.desc || ''
      }
    })
    .filter(Boolean)
}

const providerOptions = computed(() =>
  providers.value.map((item) => ({
    value: item.key,
    label: item.name,
    disabled: !item.enabled
  }))
)

const currentProvider = computed(() => providers.value.find((item) => item.key === provider.value) || null)

async function loadProviders() {
  providerLoading.value = true
  providerError.value = ''
  try {
    const res = await modelProviders()
    providers.value = normalizeProviders(res?.data)
    if (!provider.value && providers.value.length) {
      provider.value = providers.value[0].key
    }
    syncModelName()
  } catch (e) {
    providers.value = []
    providerError.value = e?.message || '模型厂商列表加载失败'
  } finally {
    providerLoading.value = false
  }
}

/** 切换厂商时同步默认模型名 */
function syncModelName() {
  if (currentProvider.value && currentProvider.value.modelName !== '-') {
    modelName.value = currentProvider.value.modelName
  }
}

function onProviderChange() {
  syncModelName()
}

async function send() {
  if (sending.value) return
  if (!prompt.value) {
    errorMsg.value = '请输入要问的内容'
    return
  }
  if (!provider.value) {
    errorMsg.value = '请先选择模型厂商'
    return
  }
  sending.value = true
  errorMsg.value = ''
  result.value = null
  try {
    const res = await modelChat({
      provider: provider.value,
      prompt: prompt.value,
      model: modelName.value || undefined
    })
    result.value = res?.data || null
  } catch (e) {
    errorMsg.value = e?.message || String(e || '模型调用失败')
    result.value = null
  } finally {
    sending.value = false
  }
}

function reset() {
  result.value = null
  errorMsg.value = ''
  prompt.value = '用一句话介绍你自己，并说明当前使用的模型。'
}

onMounted(loadProviders)
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="success"
      :closable="false"
      show-icon
      title="统一走 OpenAI 兼容协议：切换厂商只需换一个 base-url 与 API Key，接口与提示词完全一致。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>模型厂商</span>
          <el-button link type="primary" :loading="providerLoading" @click="loadProviders">刷新厂商列表</el-button>
        </div>
      </template>

      <el-alert v-if="providerError" type="error" :closable="false" :title="providerError" class="mb8" />

      <el-form label-width="90px">
        <el-form-item label="模型厂商">
          <el-select
            v-model="provider"
            class="provider-select"
            placeholder="请选择模型厂商"
            :loading="providerLoading"
            :disabled="!providers.length"
            @change="onProviderChange"
          >
            <el-option
              v-for="item in providerOptions"
              :key="item.value"
              :label="item.label + (item.value !== item.label ? '（' + item.value + '）' : '')"
              :value="item.value"
              :disabled="item.disabled"
            />
          </el-select>
          <span v-if="!providers.length && !providerLoading" class="tip">后端未返回可用厂商</span>
        </el-form-item>
        <el-form-item label="模型名称">
          <el-input v-model="modelName" class="model-input" placeholder="留空则使用厂商默认模型" clearable />
        </el-form-item>
        <el-form-item label="提问内容">
          <el-input v-model="prompt" type="textarea" :rows="4" placeholder="请输入要问的内容" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="sending" @click="send">发送</el-button>
          <el-button class="ml8" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-alert v-if="errorMsg" type="error" :closable="false" show-icon :title="errorMsg" class="mb8" />

      <div v-if="result" class="result-box">
        <div class="result-meta">
          <el-tag v-if="result.mock" type="warning" size="small">Mock 模型</el-tag>
          <el-tag v-else type="success" size="small">真实模型</el-tag>
          <el-tag size="small" type="info">{{ result.modelName || '-' }}</el-tag>
          <el-tag v-if="result.provider" size="small" type="info">{{ result.provider }}</el-tag>
          <span v-if="result.elapsedMs != null" class="elapsed">{{ result.elapsedMs }} ms</span>
        </div>
        <pre class="result-text">{{ result.text || result.content || result.message || '(无返回内容)' }}</pre>
      </div>
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>厂商配置速查（base-url / model-name / 环境变量）</span>
      </template>
      <el-table :data="providers" border stripe size="small" empty-text="暂无厂商数据">
        <el-table-column prop="name" label="厂商" width="140" />
        <el-table-column prop="key" label="厂商 Key" width="130" />
        <el-table-column prop="baseUrl" label="Base URL" min-width="220" show-overflow-tooltip />
        <el-table-column prop="modelName" label="Model Name" min-width="180" show-overflow-tooltip />
        <el-table-column prop="envKey" label="API Key 环境变量" min-width="200" show-overflow-tooltip />
      </el-table>
    </el-card>

    <CapabilityDetailCard capability-id="model" title="模型能力详情" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.provider-select {
  width: 320px;
}
.model-input {
  width: 320px;
}
.tip {
  margin-left: 10px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
.result-box {
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
.result-text {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
  line-height: 1.6;
  max-height: 420px;
  overflow: auto;
}
.ml8 {
  margin-left: 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
