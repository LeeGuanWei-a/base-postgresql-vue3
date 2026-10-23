<script setup name="MemoryPanel">
/**
 * 长期记忆（Long-term Memory）—— 演示 AgentScope 的记忆写入与读取。
 *
 * 写入：通过通用 chat 接口让 Agent 记住一条事实。
 * 读取：直接展示 detail 接口返回的记忆条目。
 */
import { computed, onMounted, ref } from 'vue'
import { capabilityDetail } from '@/api/agentscope'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref('请记住：我的常用项目是 base-postgresql-vue3，后端服务端口 8080，前端开发端口 80。')

const detail = ref(null)
const loading = ref(false)
const errorMsg = ref('')
const detailRef = ref(null)

/** 统一记忆条目：兼容对象数组与纯字符串数组
 *  后端 MemoryCapability.detail() 返回 entries: [{ id, role, time, summary }] */
const entries = computed(() => {
  const d = detail.value
  if (!d) return []
  const raw = Array.isArray(d)
    ? d
    : d.memories || d.memory || d.entries || d.items || d.list || d.records || d.facts || null
  if (Array.isArray(raw)) {
    return raw.map((m, i) => {
      if (typeof m === 'string') {
        return { id: String(i + 1), role: '', content: m, time: '', createdAt: '' }
      }
      return {
        id: String(m?.id ?? m?.key ?? m?.name ?? i + 1),
        key: m?.key || m?.name || m?.title || '',
        role: m?.role || '',
        content: m?.summary || m?.content || m?.text || m?.value || m?.memory || '',
        time: m?.time || m?.createdAt || m?.createTime || m?.created || m?.timestamp || ''
      }
    })
  }
  // key-value 形态的记忆表
  if (raw && typeof raw === 'object') {
    return Object.keys(raw).map((k) => ({
      id: k,
      key: k,
      role: '',
      content: typeof raw[k] === 'string' ? raw[k] : JSON.stringify(raw[k]),
      time: ''
    }))
  }
  return []
})

/** detail 里的统计字段 */
const summary = computed(() => {
  const d = detail.value
  if (!d || Array.isArray(d)) return []
  return Object.keys(d)
    .filter((k) => !['memories', 'memory', 'entries', 'items', 'list', 'records', 'facts'].includes(k))
    .map((k) => ({ key: k, value: d[k] }))
})

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await capabilityDetail('memory')
    detail.value = res.data
  } catch (e) {
    errorMsg.value = e?.message || '加载失败'
    detail.value = null
  } finally {
    loading.value = false
  }
}

/** 刷新记忆（写完记忆后点它看是否落库） */
function reload() {
  load()
  if (detailRef.value?.load) detailRef.value.load()
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
      title="长期记忆会把跨会话的事实沉淀下来：先在下面写入一条记忆，再点「刷新记忆」读取条目，验证记忆是否落库。"
    />

    <el-row :gutter="12" class="mb8">
      <el-col :xs="24" :md="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>① 写入记忆</span>
              <el-tag size="small" type="warning">走通用 chat 接口</el-tag>
            </div>
          </template>
          <CapabilityChatBox capability-id="memory" v-model:prompt="prompt" compact />
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>② 读取记忆</span>
              <el-button link type="primary" :loading="loading" @click="reload">刷新记忆</el-button>
            </div>
          </template>

          <el-alert v-if="errorMsg" type="warning" :closable="false" :title="errorMsg" />

          <el-skeleton v-else-if="loading && !detail" :rows="3" animated />

          <template v-else>
            <el-descriptions v-if="summary.length" :column="2" size="small" border class="mb8">
              <el-descriptions-item v-for="item in summary" :key="item.key" :label="item.key">
                <span v-if="typeof item.value === 'object'">{{ JSON.stringify(item.value) }}</span>
                <span v-else>{{ item.value }}</span>
              </el-descriptions-item>
            </el-descriptions>

            <el-table :data="entries" border stripe size="small" max-height="320">
              <el-table-column prop="id" label="#" width="60" />
              <el-table-column prop="role" label="角色" width="100" />
              <el-table-column prop="content" label="记忆内容" min-width="260" show-overflow-tooltip />
              <el-table-column prop="time" label="写入时间" min-width="160" />
              <template #empty>
                <el-empty description="暂无记忆条目" :image-size="60" />
              </template>
            </el-table>
          </template>
        </el-card>
      </el-col>
    </el-row>

    <CapabilityDetailCard ref="detailRef" capability-id="memory" title="记忆条目原始详情" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.mb8 {
  margin-bottom: 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
