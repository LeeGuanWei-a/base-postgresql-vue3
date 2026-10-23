<script setup name="PermissionPanel">
/**
 * 权限模式（Permission Mode）—— 演示 Agent 执行动作前的人工确认策略。
 *
 * 查询：GET  /agentscope/permission/mode
 * 设置：POST /agentscope/permission/mode   body: { mode, sessionId }
 *
 * 后端字段名以后端实际为准：优先用 mode，失败后自动用 permissionMode 再试一次。
 */
import { computed, onMounted, ref } from 'vue'
import { permissionMode, permissionSetMode } from '@/api/agentscope'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

/** 可选模式（默认值，后端 /mode 会返回 availableModes 覆盖） */
const DEFAULT_MODES = [
  { value: 'DEFAULT', label: 'DEFAULT', desc: '默认：执行可能产生副作用的动作前先征求同意' },
  { value: 'ACCEPT_EDITS', label: 'ACCEPT_EDITS', desc: '自动接受文件编辑类动作，其他动作仍需确认' },
  { value: 'EXPLORE', label: 'EXPLORE', desc: '只读探索：仅允许读取类动作，禁止写入' },
  { value: 'BYPASS', label: 'BYPASS', desc: '跳过所有确认（仅限可信环境）' },
  { value: 'DONT_ASK', label: 'DONT_ASK', desc: '不询问：既不询问也不执行需确认的动作' }
]

const modes = ref([...DEFAULT_MODES])
const currentMode = ref('')
const sessionId = ref('')
const loading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const okMsg = ref('')

/** 从后端返回里提取模式值（兼容字符串 / { mode } / { permissionMode } / { value }） */
function extractMode(data) {
  if (!data) return ''
  if (typeof data === 'string') return data
  if (typeof data === 'object') {
    const v = data.mode || data.permissionMode || data.value || data.current || data.currentMode
    if (typeof v === 'string') return v
    if (v && typeof v === 'object') return v.mode || v.name || ''
  }
  return ''
}

/** 归一化成大写枚举值 */
function normalizeMode(v) {
  const s = String(v || '').trim().toUpperCase()
  if (!s) return ''
  return modes.value.some((m) => m.value === s) ? s : s.replace(/[-\s]/g, '_')
}

const modeDesc = computed(() => modes.value.find((m) => m.value === currentMode.value)?.desc || '当前后端返回了未在列表中的模式')

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await permissionMode()
    const data = res.data || {}
    currentMode.value = normalizeMode(extractMode(data))
    // 后端会返回 availableModes，用它覆盖默认列表
    if (Array.isArray(data.availableModes) && data.availableModes.length) {
      modes.value = data.availableModes.map((m) => {
        const known = DEFAULT_MODES.find((x) => x.value === String(m).toUpperCase())
        return known || { value: String(m).toUpperCase(), label: String(m).toUpperCase(), desc: '后端提供的权限模式' }
      })
    }
  } catch (e) {
    errorMsg.value = e?.message || '查询权限模式失败'
  } finally {
    loading.value = false
  }
}

/** 设置模式：先按 { mode } 提交，失败再按 { permissionMode } 兼容一次 */
async function applyMode(mode) {
  if (!mode) return
  const prev = currentMode.value
  saving.value = true
  errorMsg.value = ''
  okMsg.value = ''
  try {
    try {
      await permissionSetMode({ mode, sessionId: sessionId.value || undefined })
    } catch (firstErr) {
      await permissionSetMode({ permissionMode: mode, sessionId: sessionId.value || undefined })
    }
    currentMode.value = normalizeMode(mode)
    okMsg.value = '权限模式已切换为 ' + currentMode.value
  } catch (e) {
    currentMode.value = prev
    errorMsg.value = e?.message || '设置权限模式失败'
  } finally {
    saving.value = false
  }
}

function onModeChange(mode) {
  applyMode(mode)
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
      title="权限模式决定 Agent 在执行写操作 / 命令 / 工具调用前是否需要人工确认，不同模式对应不同的允许与拒绝规则清单。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>当前权限模式</span>
          <el-button link type="primary" :loading="loading" @click="load">刷新</el-button>
        </div>
      </template>

      <div class="current">
        <el-tag :type="currentMode === 'BYPASS' ? 'danger' : 'success'" size="large" effect="dark">
          {{ currentMode || '未知' }}
        </el-tag>
        <span class="current-desc">{{ modeDesc }}</span>
      </div>

      <el-divider content-position="left">切换模式</el-divider>

      <el-radio-group :model-value="currentMode" :disabled="saving" @change="onModeChange">
        <el-radio v-for="m in modes" :key="m.value" :value="m.value" class="mode-radio">
          {{ m.label }}
        </el-radio>
      </el-radio-group>

      <div class="mode-list">
        <div v-for="m in modes" :key="m.value" class="mode-item" :class="{ active: m.value === currentMode }">
          <el-tag size="small" :type="m.value === currentMode ? 'success' : 'info'">{{ m.label }}</el-tag>
          <span class="mode-item-desc">{{ m.desc }}</span>
        </div>
      </div>

      <el-divider content-position="left">会话</el-divider>
      <el-input v-model="sessionId" class="session-input" placeholder="会话 ID（可留空，后端默认使用 default 会话）" clearable />

      <el-alert v-if="errorMsg" class="mt8" type="error" :closable="false" show-icon :title="errorMsg" />
      <el-alert v-if="okMsg" class="mt8" type="success" :closable="false" show-icon :title="okMsg" />
    </el-card>

    <CapabilityDetailCard capability-id="permission" title="允许 / 拒绝规则清单" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.current {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.current-desc {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}
.mode-radio {
  margin-right: 18px;
}
.mode-list {
  margin-top: 12px;
}
.mode-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 10px;
  border-radius: 4px;
  margin-bottom: 4px;
  background: var(--el-fill-color-lighter);
}
.mode-item.active {
  background: var(--el-color-primary-light-9);
}
.mode-item-desc {
  font-size: 12px;
  color: var(--el-text-color-regular);
}
.session-input {
  width: 320px;
}
.mt8 {
  margin-top: 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
