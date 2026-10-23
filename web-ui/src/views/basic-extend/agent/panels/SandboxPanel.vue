<script setup name="SandboxPanel">
/**
 * 沙箱执行（外部依赖型）—— 在受限环境里执行白名单命令。
 *
 * 演示要点：
 * 1. GET /agentscope/sandbox/status：是否启用、隔离级别、白名单命令清单、接入真沙箱还需要什么；
 * 2. POST /agentscope/sandbox/execute：输入命令执行，展示执行结果；
 * 3. 「命令不在白名单」属于**预期内的拒绝结果**，用 el-alert 友好展示，而不是报错。
 */
import { computed, onMounted, ref } from 'vue'
import { sandboxExecute, sandboxStatus } from '@/api/agentscope'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

const props = defineProps({
  capability: { type: Object, required: true }
})

const status = ref(null)
const loading = ref(false)
const errorMsg = ref('')

const command = ref('echo hello-agentscope')
const executing = ref(false)
/** 执行结果（成功） */
const execResult = ref(null)
/** 执行被拒绝 / 失败 */
const execRejected = ref('')

/** 能力是否就绪 */
const capabilityReady = computed(() => props.capability?.enabled !== false)

function pickValue(data, keys, fallback = '-') {
  if (!data || typeof data !== 'object') return fallback
  for (const key of keys) {
    if (data[key] !== undefined && data[key] !== null && data[key] !== '') return data[key]
  }
  return fallback
}

/** 提取白名单命令 */
function pickWhitelist(data) {
  if (!data || typeof data !== 'object') return []
  for (const key of ['whitelist', 'allowedCommands', 'allowList', 'commands', 'allowed']) {
    const value = data[key]
    if (Array.isArray(value)) return value
    if (typeof value === 'string' && value.trim()) return value.split(/[,\n;]/).map((s) => s.trim()).filter(Boolean)
  }
  return []
}

/** 提取「接入真沙箱需要什么」 */
function pickRequirements(data) {
  if (!data || typeof data !== 'object') return []
  for (const key of ['requirements', 'requires', 'notes', 'dependency', 'dependencies', 'hints']) {
    const value = data[key]
    if (Array.isArray(value)) return value
    if (typeof value === 'string' && value.trim()) return [value]
  }
  return []
}

const whitelist = computed(() => pickWhitelist(status.value))
const requirements = computed(() => pickRequirements(status.value))

const summary = computed(() => {
  const data = status.value || {}
  return [
    { label: '沙箱是否启用', value: pickValue(data, ['enabled', 'available', 'active']) },
    { label: '隔离级别', value: pickValue(data, ['isolation', 'isolationLevel', 'level', 'mode']) },
    { label: '后端实现', value: pickValue(data, ['provider', 'runtime', 'engine', 'backend']) },
    { label: '白名单命令数', value: pickValue(data, ['whitelistCount', 'allowedCount'], whitelist.value.length) }
  ]
})

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await sandboxStatus()
    status.value = res?.data ?? null
  } catch (e) {
    status.value = null
    errorMsg.value = e?.message || '沙箱状态加载失败'
  } finally {
    loading.value = false
  }
}

/** 执行命令 */
async function execute() {
  if (executing.value) return
  if (!command.value || !command.value.trim()) {
    errorMsg.value = '请输入要执行的命令'
    return
  }
  executing.value = true
  errorMsg.value = ''
  execResult.value = null
  execRejected.value = ''
  try {
    const res = await sandboxExecute({ command: command.value.trim() })
    const data = res?.data
    if (data === null || data === undefined || data === '') {
      execResult.value = { text: '（沙箱未返回内容）' }
      return
    }
    if (typeof data === 'string') {
      execResult.value = { text: data }
      return
    }
    const allowed = data.allowed !== false && data.denied !== true && data.success !== false
    if (!allowed) {
      execRejected.value =
        data.reason || data.message || data.error || '命令被沙箱拒绝（不在白名单内或缺少权限）'
      return
    }
    const text =
      data.output ?? data.stdout ?? data.result ?? data.text ?? data.message ?? JSON.stringify(data, null, 2)
    execResult.value = {
      text: typeof text === 'string' ? text : JSON.stringify(text, null, 2),
      exitCode: data.exitCode ?? data.exit ?? '-'
    }
  } catch (e) {
    // 后端以业务异常返回（如命令被拒绝）时，错误信息本身就是演示的一部分
    execRejected.value = e?.message || String(e || '沙箱执行失败')
  } finally {
    executing.value = false
  }
}

onMounted(load)
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="warning"
      :closable="false"
      show-icon
      title="沙箱执行依赖 agentscope-extensions-sandbox（Docker / K8s）。未接入真沙箱时只做白名单校验，命令会被拒绝，属于预期结果。"
    />

    <el-alert
      v-if="!capabilityReady"
      class="mb8"
      type="info"
      :closable="false"
      show-icon
      title="当前能力标记为「未就绪」：通用对话接口会返回「能力不可用（缺少 Agent Bean）」，属于正常提示，可先接入真沙箱后再试。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>沙箱状态</span>
          <el-button link type="primary" :loading="loading" @click="load">刷新状态</el-button>
        </div>
      </template>

      <el-alert v-if="errorMsg" type="error" :closable="false" show-icon :title="errorMsg" class="mb8" />

      <el-skeleton v-if="loading && !status" :rows="3" animated />

      <template v-else>
        <el-descriptions :column="4" border size="small" class="mb8">
          <el-descriptions-item v-for="item in summary" :key="item.label" :label="item.label">
            {{ item.value }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="section-title">白名单命令清单</div>
        <div v-if="whitelist.length" class="whitelist">
          <el-tag v-for="(item, index) in whitelist" :key="index" class="whitelist-tag" size="small" effect="plain">
            {{ item }}
          </el-tag>
        </div>
        <el-empty v-else description="暂无白名单命令（未接入真沙箱时通常为空）" :image-size="70" />

        <div class="section-title mt12">接入真沙箱需要什么</div>
        <ul v-if="requirements.length" class="requirements">
          <li v-for="(item, index) in requirements" :key="index">{{ item }}</li>
        </ul>
        <el-empty v-else description="后端未返回接入说明" :image-size="70" />
      </template>
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>执行一条命令</span>
      </template>

      <el-form label-width="80px">
        <el-form-item label="命令">
          <el-input
            v-model="command"
            placeholder="例如：echo hello-agentscope"
            clearable
            @keyup.enter.exact.prevent="execute"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="executing" @click="execute">执行</el-button>
          <span class="tip">试试输入白名单之外的命令（如 rm -rf /），观察拒绝提示</span>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="execRejected"
        class="mb8"
        type="warning"
        :closable="false"
        show-icon
        title="命令被拒绝"
      >
        <template #default>
          <div>{{ execRejected }}</div>
          <div class="tip">这是<b>预期内</b>的结果：沙箱只允许白名单命令，其余一律拒绝。</div>
        </template>
      </el-alert>

      <div v-if="execResult" class="result-box">
        <div class="result-meta">
          <el-tag type="success" size="small">执行成功</el-tag>
          <el-tag v-if="execResult.exitCode !== undefined" size="small" type="info">
            exitCode {{ execResult.exitCode }}
          </el-tag>
        </div>
        <pre class="result-text">{{ execResult.text }}</pre>
      </div>
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>通用对话（验证能力是否已装配 Agent）</span>
      </template>
      <CapabilityChatBox capability-id="sandbox" default-prompt="在沙箱里执行一条白名单命令。" />
    </el-card>

    <CapabilityDetailCard capability-id="sandbox" title="沙箱能力详情" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-regular);
  margin-bottom: 6px;
}
.whitelist {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.whitelist-tag {
  font-family: Consolas, Monaco, monospace;
}
.requirements {
  margin: 0;
  padding-left: 20px;
  color: var(--el-text-color-regular);
  font-size: 13px;
  line-height: 1.9;
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
.tip {
  margin-left: 10px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
.mt12 {
  margin-top: 12px;
}
</style>
