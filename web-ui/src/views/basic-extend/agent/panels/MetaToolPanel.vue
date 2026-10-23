<script setup name="MetaToolPanel">
/**
 * 元工具（Meta Tool）—— 让模型自己造工具、管理工具清单。
 *
 * 演示要点：
 * 1. 是否启用元工具、已注册的元工具清单（CapabilityDetailCard）；
 * 2. 对话演示：让模型一边查真实数据，一边列出自己可调用的工具。
 */
import { computed, ref } from 'vue'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref('请帮我查一下用户数量，并顺便告诉我你能调用哪些工具。')

const detailCard = ref(null)
const detailSnapshot = ref(null)

/** 从详情中提取工具清单 —— 兼容多种字段命名 */
function pickTools(detail) {
  if (!detail || typeof detail !== 'object') return []
  for (const key of ['tools', 'toolList', 'metaTools', 'items', 'list']) {
    if (Array.isArray(detail[key])) return detail[key]
  }
  return []
}

const toolRows = computed(() =>
  pickTools(detailSnapshot.value).map((item, index) =>
    typeof item === 'string'
      ? { name: item, description: '-', scope: '-' }
      : {
          name: item.name || item.tool || item.id || `工具 #${index + 1}`,
          description: item.description || item.desc || '-',
          scope: item.scope || item.source || item.origin || item.type || '-'
        }
  )
)

const metaInfo = computed(() => {
  const detail = detailSnapshot.value || {}
  const pick = (...keys) => {
    for (const key of keys) {
      if (detail[key] !== undefined && detail[key] !== null && detail[key] !== '') return detail[key]
    }
    return '-'
  }
  return [
    { label: '元工具是否启用', value: pick('metaToolEnabled', 'enabled', 'metaEnabled', 'active') },
    { label: '工具总数', value: pick('toolCount', 'tools', 'total') },
    { label: '可自建工具数', value: pick('dynamicToolCount', 'createdTools', 'selfBuilt') },
    { label: '允许的操作', value: pick('allowedActions', 'actions', 'permissions') }
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
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="success"
      :closable="false"
      show-icon
      title="元工具让模型在运行时按需创建/注册工具（例如把一段 SQL 包装成新工具），并可管理已有工具清单。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>元工具状态</span>
          <el-button link type="primary" @click="refreshDetail">刷新</el-button>
        </div>
      </template>

      <el-descriptions :column="4" border size="small">
        <el-descriptions-item v-for="item in metaInfo" :key="item.label" :label="item.label">
          {{ item.value }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>工具清单</span>
      </template>
      <el-table :data="toolRows" border stripe size="small" empty-text="暂无工具清单">
        <el-table-column prop="name" label="工具名" width="200" show-overflow-tooltip />
        <el-table-column prop="description" label="说明" min-width="260" show-overflow-tooltip />
        <el-table-column prop="scope" label="来源" width="140" />
      </el-table>
    </el-card>

    <CapabilityChatBox capability-id="meta-tool" v-model:prompt="prompt" />

    <CapabilityDetailCard ref="detailCard" capability-id="meta-tool" title="元工具能力详情" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.mt12 {
  margin-top: 12px;
}
</style>
