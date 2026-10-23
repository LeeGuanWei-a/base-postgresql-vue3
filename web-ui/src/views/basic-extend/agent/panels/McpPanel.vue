<script setup name="McpPanel">
/**
 * MCP 客户端（外部依赖型，默认未就绪）。
 *
 * 演示要点：
 * 1. GET /agentscope/mcp/status：已配置的 MCP Server 列表、每个 Server 的状态、缺什么；
 * 2. POST /agentscope/mcp/reload：重新加载 MCP 配置；
 * 3. 展示后端返回的 yml 配置示例（<pre> 原样输出）；
 * 4. 优雅处理「该能力没有 Agent / 能力不可用」：给出友好提示而不是报错堆栈。
 */
import { computed, onMounted, ref } from 'vue'
import { mcpReload, mcpStatus } from '@/api/agentscope'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

const props = defineProps({
  capability: { type: Object, required: true }
})

const status = ref(null)
const loading = ref(false)
const errorMsg = ref('')
const reloading = ref(false)
const reloadMsg = ref('')

/** 能力是否就绪：后端能力清单里的 enabled 标记 */
const capabilityReady = computed(() => props.capability?.enabled !== false)

/** 从状态对象中提取 Server 列表 —— 兼容多种字段命名 */
function pickServers(data) {
  if (!data || typeof data !== 'object') return []
  for (const key of ['servers', 'mcpServers', 'serverList', 'list', 'items', 'clients']) {
    if (Array.isArray(data[key])) return data[key]
  }
  return []
}

/** 提取「缺什么」 */
function pickMissing(data) {
  if (!data || typeof data !== 'object') return []
  const value = data.missing || data.absent || data.lack || data.requirements || data.requires
  if (Array.isArray(value)) return value
  if (typeof value === 'string' && value.trim()) return [value]
  return []
}

/**
 * 递归寻找 yml 配置示例：
 * 1. 优先匹配 key 含 yml / yaml / example / config 的字符串；
 * 2. 其次找包含 "mcpServers:" 的字符串。
 */
function findYmlExample(obj, depth = 0) {
  if (depth > 4 || obj === null || obj === undefined) return ''
  if (typeof obj === 'string') {
    return /mcpServers\s*:|mcp-servers\s*:|command\s*:/.test(obj) ? obj : ''
  }
  if (Array.isArray(obj)) {
    for (const item of obj) {
      const found = findYmlExample(item, depth + 1)
      if (found) return found
    }
    return ''
  }
  if (typeof obj !== 'object') return ''
  for (const [key, value] of Object.entries(obj)) {
    if (/yml|yaml|example|sample/i.test(key) && typeof value === 'string' && value.trim()) {
      return value
    }
  }
  for (const value of Object.values(obj)) {
    const found = findYmlExample(value, depth + 1)
    if (found) return found
  }
  return ''
}

/** 归一化 Server 状态文案 */
function pickState(item) {
  if (!item || typeof item !== 'object') return 'unknown'
  if (item.status || item.state) return String(item.status || item.state)
  if (item.ready === true) return 'ready'
  if (item.ready === false) return 'not-ready'
  if (item.connected === true) return 'ready'
  if (item.connected === false) return 'not-ready'
  return 'unknown'
}

const serverRows = computed(() =>
  pickServers(status.value).map((item, index) => {
    if (typeof item === 'string') {
      return { name: item, state: 'unknown', command: '-', args: '-', missing: '-', desc: '-' }
    }
    const it = item || {}
    return {
      name: it.name || it.server || it.id || `Server #${index + 1}`,
      state: pickState(it),
      command: it.command || it.cmd || '-',
      args: Array.isArray(it.args) ? it.args.join(' ') : it.args || '-',
      missing: it.missing || it.absent || it.require || '-',
      desc: it.description || it.desc || '-'
    }
  })
)

const missingItems = computed(() => pickMissing(status.value))

const ymlExample = computed(() => findYmlExample(status.value))

const summary = computed(() => {
  const data = status.value || {}
  const pick = (...keys) => {
    for (const key of keys) {
      if (data[key] !== undefined && data[key] !== null && data[key] !== '') return data[key]
    }
    return '-'
  }
  return [
    { label: 'MCP 是否就绪', value: pick('ready', 'enabled', 'available') },
    { label: '已配置 Server 数', value: pick('serverCount', 'servers', 'count') },
    { label: 'Client 数', value: pick('clientCount', 'clients') },
    { label: '配置文件', value: pick('configPath', 'configFile', 'file', 'path') }
  ]
})

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await mcpStatus()
    status.value = res?.data ?? null
  } catch (e) {
    status.value = null
    errorMsg.value = e?.message || 'MCP 状态加载失败'
  } finally {
    loading.value = false
  }
}

async function reload() {
  if (reloading.value) return
  reloading.value = true
  reloadMsg.value = ''
  errorMsg.value = ''
  try {
    const res = await mcpReload()
    reloadMsg.value = res?.msg || res?.data?.message || 'MCP 配置已重新加载'
    await load()
  } catch (e) {
    errorMsg.value = e?.message || 'MCP 重新加载失败'
  } finally {
    reloading.value = false
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
      title="MCP 客户端依赖外部 MCP Server（stdio / sse 方式启动）。未配置时该能力处于「未就绪」状态，属于预期现象。"
    />

    <el-alert
      v-if="!capabilityReady"
      class="mb8"
      type="info"
      :closable="false"
      show-icon
      title="当前能力标记为「未就绪」：通用对话接口会返回「能力不可用（缺少 Agent Bean）」，属于正常提示，可先配置 MCP Server 后重新加载。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>MCP 状态</span>
          <div>
            <el-button link type="primary" :loading="loading" @click="load">刷新状态</el-button>
            <el-button type="primary" plain size="small" :loading="reloading" @click="reload">重新加载</el-button>
          </div>
        </div>
      </template>

      <el-alert v-if="errorMsg" type="error" :closable="false" show-icon :title="errorMsg" class="mb8" />
      <el-alert v-else-if="reloadMsg" type="success" :closable="false" show-icon :title="reloadMsg" class="mb8" />

      <el-skeleton v-if="loading && !status" :rows="3" animated />

      <template v-else>
        <el-descriptions :column="4" border size="small" class="mb8">
          <el-descriptions-item v-for="item in summary" :key="item.label" :label="item.label">
            {{ item.value }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="section-title">已配置的 MCP Server</div>
        <el-table :data="serverRows" border stripe size="small" empty-text="尚未配置任何 MCP Server">
          <el-table-column prop="name" label="Server 名称" width="180" show-overflow-tooltip />
          <el-table-column prop="state" label="状态" width="120">
            <template #default="scope">
              <el-tag size="small" :type="scope.row.state === 'ready' ? 'success' : 'info'">
                {{ scope.row.state }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="command" label="启动命令" width="160" show-overflow-tooltip />
          <el-table-column prop="args" label="参数" min-width="180" show-overflow-tooltip />
          <el-table-column prop="missing" label="缺少内容" min-width="180" show-overflow-tooltip />
        </el-table>

        <div v-if="missingItems.length" class="section-title mt12">接入前需要准备</div>
        <ul v-if="missingItems.length" class="missing-list">
          <li v-for="(item, index) in missingItems" :key="index">{{ item }}</li>
        </ul>
      </template>
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>MCP 配置示例（yml）</span>
      </template>
      <pre v-if="ymlExample" class="yml-box">{{ ymlExample }}</pre>
      <el-empty v-else description="后端未返回 yml 配置示例" :image-size="70" />
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>通用对话（验证能力是否已装配 Agent）</span>
      </template>
      <CapabilityChatBox capability-id="mcp" default-prompt="列出当前可用的 MCP 工具。" />
    </el-card>

    <CapabilityDetailCard capability-id="mcp" title="MCP 能力详情" class="mt12" />
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
.missing-list {
  margin: 0;
  padding-left: 20px;
  color: var(--el-text-color-regular);
  font-size: 13px;
  line-height: 1.9;
}
.yml-box {
  margin: 0;
  padding: 10px 12px;
  background: var(--el-fill-color-light);
  border-radius: 4px;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
  line-height: 1.7;
  max-height: 420px;
  overflow: auto;
}
.mt12 {
  margin-top: 12px;
}
</style>
