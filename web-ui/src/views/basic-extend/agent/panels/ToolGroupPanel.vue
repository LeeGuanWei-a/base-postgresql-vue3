<script setup name="ToolGroupPanel">
/**
 * 工具分组 —— 演示 Toolkit 的工具组（ToolGroup）与分组级 enable / disable。
 *
 * 分组清单与工具全部来自 GET /agentscope/capability/tool-group/detail。
 * 开关走 POST /agentscope/tool-group/toggle?group=xxx&enabled=true|false，
 * 切换后重新拉取 detail，保证页面展示的始终是后端真实状态。
 */
import { computed, onMounted, ref } from 'vue'
import { capabilityDetail, toolGroupToggle } from '@/api/agentscope'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref('按分组列出当前可用的工具，并说明每个分组负责什么。')

const detail = ref(null)
const loading = ref(false)
const errorMsg = ref('')
const activeGroup = ref('')
const toggling = ref('')

/** 把任意形态的工具定义统一成 { name, description, enabled } */
function normalizeTools(raw) {
  if (!raw) return []
  if (Array.isArray(raw)) {
    return raw.map((t) => {
      if (typeof t === 'string') return { name: t, description: '', enabled: true }
      return {
        name: t?.name || t?.toolName || t?.tool || t?.id || '-',
        description: t?.description || t?.desc || t?.summary || '',
        enabled: t?.enabled !== false && t?.active !== false
      }
    })
  }
  if (typeof raw === 'object') {
    return Object.keys(raw).map((k) => {
      const v = raw[k]
      if (v && typeof v === 'object') {
        return {
          name: k,
          description: v.description || v.desc || '',
          enabled: v.enabled !== false && v.active !== false
        }
      }
      return { name: k, description: typeof v === 'string' ? v : '', enabled: true }
    })
  }
  return []
}

/** 把 detail 归一化成统一的分组数组（后端字段：name / description / active / tools） */
const groups = computed(() => {
  const d = detail.value
  if (!d) return []
  const raw =
    (Array.isArray(d) ? d : null) ||
    d.groups ||
    d.toolGroups ||
    d.groupList ||
    d.toolGroupList ||
    null
  if (Array.isArray(raw)) {
    return raw.map((g, i) => ({
      key: g?.name || g?.groupName || g?.group || g?.id || `分组${i + 1}`,
      name: g?.name || g?.groupName || g?.group || g?.id || `分组${i + 1}`,
      description: g?.description || g?.desc || g?.summary || '',
      active: g?.active !== false && g?.enabled !== false,
      tools: normalizeTools(g?.tools || g?.toolNames || g?.items || g?.children)
    }))
  }
  // 扁平结构：detail.tools 里带 group 字段 → 按 group 归拢
  const flat = normalizeTools(d.tools)
  if (flat.length) {
    const map = new Map()
    flat.forEach((t) => {
      const g = t.group || '默认分组'
      if (!map.has(g)) {
        map.set(g, { key: g, name: g, description: '', active: true, tools: [] })
      }
      map.get(g).tools.push(t)
    })
    return Array.from(map.values())
  }
  return []
})

/** 详情里的统计字段（后端：agentName / groupCount / activeGroups / enabledToolCount …） */
const summary = computed(() => {
  const d = detail.value
  if (!d || Array.isArray(d)) return []
  const skip = ['groups', 'toolGroups', 'groupList', 'toolGroupList', 'tools']
  return Object.keys(d)
    .filter((k) => !skip.includes(k))
    .map((k) => ({ key: k, value: d[k] }))
})

const currentGroup = computed(() => {
  if (!groups.value.length) return null
  return groups.value.find((g) => g.key === activeGroup.value) || groups.value[0]
})

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await capabilityDetail('tool-group')
    detail.value = res.data
    if (!activeGroup.value && groups.value.length) {
      activeGroup.value = groups.value[0].key
    }
  } catch (e) {
    errorMsg.value = e?.message || '加载失败'
    detail.value = null
  } finally {
    loading.value = false
  }
}

/**
 * 切换工具组开关。
 *
 * 走 @/api/agentscope.js 的 toolGroupToggle()：查询参数已拼进 URL，
 * 因此 1 秒内切换不同分组不会被 request.js 的防重复提交误拦。
 */
async function toggleGroup(group, enabled) {
  toggling.value = group.key
  errorMsg.value = ''
  try {
    await toolGroupToggle(group.name, enabled)
    await load()
  } catch (e) {
    errorMsg.value = e?.message || '切换工具组失败'
    await load()
  } finally {
    toggling.value = ''
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
      title="工具可以按业务域分组挂载到 Toolkit 上；停用某个分组后，该组工具不再出现在给模型的 JSON Schema 里。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>工具分组（来自 detail 接口）</span>
          <el-button link type="primary" :loading="loading" @click="load">刷新</el-button>
        </div>
      </template>

      <el-alert v-if="errorMsg" type="warning" :closable="false" :title="errorMsg" />

      <el-skeleton v-else-if="loading && !detail" :rows="3" animated />

      <el-empty v-else-if="!groups.length" description="后端未返回工具分组数据" :image-size="70" />

      <template v-else>
        <el-descriptions v-if="summary.length" class="mb8" :column="3" size="small" border>
          <el-descriptions-item v-for="item in summary" :key="item.key" :label="item.key">
            <span v-if="typeof item.value === 'object'">{{ JSON.stringify(item.value) }}</span>
            <span v-else>{{ item.value }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <el-table :data="groups" border stripe size="small" @current-change="(row) => (activeGroup = row?.key)">
          <el-table-column prop="name" label="分组名称" min-width="150" />
          <el-table-column prop="description" label="分组说明" min-width="200" show-overflow-tooltip />
          <el-table-column label="工具数" width="90" align="center">
            <template #default="{ row }">{{ row.tools.length }}</template>
          </el-table-column>
          <el-table-column label="状态" width="150" align="center">
            <template #default="{ row }">
              <el-switch
                :model-value="row.active"
                :loading="toggling === row.key"
                :active-text="'启用'"
                :inactive-text="'停用'"
                inline-prompt
                @update:model-value="(v) => toggleGroup(row, v)"
              />
            </template>
          </el-table-column>
          <el-table-column label="工具" min-width="260">
            <template #default="{ row }">
              <el-tag v-for="t in row.tools" :key="t.name" class="tool-tag" effect="plain" size="small">
                {{ t.name }}
              </el-tag>
              <span v-if="!row.tools.length" class="text-muted">—</span>
            </template>
          </el-table-column>
        </el-table>

        <div v-if="currentGroup" class="group-tools">
          <div class="group-tools-title">
            分组「{{ currentGroup.name }}」的工具明细
            <el-tag size="small" :type="currentGroup.active ? 'success' : 'info'">
              {{ currentGroup.active ? '已启用' : '已停用' }}
            </el-tag>
          </div>
          <el-table :data="currentGroup.tools" border stripe size="small">
            <el-table-column prop="name" label="工具" min-width="160" />
            <el-table-column prop="description" label="说明" min-width="240" show-overflow-tooltip />
            <template #empty>
              <el-empty description="该分组暂无工具" :image-size="60" />
            </template>
          </el-table>
        </div>
      </template>
    </el-card>

    <CapabilityChatBox capability-id="tool-group" v-model:prompt="prompt" />

    <CapabilityDetailCard capability-id="tool-group" title="分组原始详情" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.tool-tag {
  margin: 2px 4px 2px 0;
}
.group-tools {
  margin-top: 12px;
}
.group-tools-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-regular);
  margin-bottom: 6px;
}
.text-muted {
  color: var(--el-text-color-secondary);
}
.mb8 {
  margin-bottom: 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
