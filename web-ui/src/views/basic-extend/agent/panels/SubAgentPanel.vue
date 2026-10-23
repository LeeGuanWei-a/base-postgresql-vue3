<script setup name="SubAgentPanel">
/**
 * 子智能体（Sub-Agent）—— 演示主 Agent 委派任务给子 Agent 的调用链。
 *
 * 主/子 Agent 名称与工具清单来自 detail 接口；对话走通用能力接口。
 */
import { computed, onMounted, ref } from 'vue'
import { capabilityDetail } from '@/api/agentscope'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref('请让子助手汇总当前系统用户情况')

const detail = ref(null)
const loading = ref(false)
const errorMsg = ref('')

/** 统一工具清单 */
function normalizeTools(raw) {
  if (!raw) return []
  if (Array.isArray(raw)) {
    return raw.map((t) => (typeof t === 'string' ? t : t?.name || t?.toolName || t?.tool || t?.id || '-'))
  }
  if (typeof raw === 'object') return Object.keys(raw)
  return []
}

/** 主 Agent
 *  后端 SubAgentCapability.detail() 返回：
 *  { mainAgent: "sub-agent-master", mainAgentTools: [...], subAgent: "report-sub-agent", subAgentTools: [...] } */
const mainAgent = computed(() => {
  const d = detail.value
  if (!d || Array.isArray(d)) return null
  const m = d.mainAgent || d.main || d.parentAgent || d.parent || d.rootAgent || d.root
  if (!m) return null
  const isString = typeof m === 'string'
  return {
    name: isString ? m : m.name || m.agentName || m.id || '主 Agent',
    description: isString ? d.note || '' : m.description || m.desc || m.summary || '',
    tools: normalizeTools(isString ? d.mainAgentTools || d.mainTools : m.tools || m.toolNames || m.toolkit)
  }
})

/** 子 Agent 列表（后端当前是单个 subAgent 字符串，兼容数组形态） */
const subAgents = computed(() => {
  const d = detail.value
  if (!d || Array.isArray(d)) return []
  const raw = d.subAgents || d.subagents || d.sub_agent_list || d.children
  if (Array.isArray(raw)) {
    return raw.map((s, i) => {
      if (typeof s === 'string') {
        return { name: s, description: '', tools: normalizeTools(d.subAgentTools), model: '' }
      }
      return {
        name: s?.name || s?.agentName || s?.id || `子 Agent${i + 1}`,
        description: s?.description || s?.desc || s?.summary || '',
        tools: normalizeTools(s?.tools || s?.toolNames || s?.toolkit),
        model: s?.model || s?.modelName || ''
      }
    })
  }
  if (d.subAgent) {
    return [
      {
        name: typeof d.subAgent === 'string' ? d.subAgent : d.subAgent.name || '子 Agent',
        description: d.subAgentTool ? '对外入口工具：' + d.subAgentTool : '',
        tools: normalizeTools(d.subAgentTools),
        model: ''
      }
    ]
  }
  return []
})

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await capabilityDetail('sub-agent')
    detail.value = res.data
  } catch (e) {
    errorMsg.value = e?.message || '加载失败'
    detail.value = null
  } finally {
    loading.value = false
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
      title="主 Agent 负责拆解与委派，子 Agent 各自绑定专属工具；子 Agent 的结果回传给主 Agent 后由主 Agent 汇总输出。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>调用链：主 Agent → 子 Agent</span>
          <el-button link type="primary" :loading="loading" @click="load">刷新</el-button>
        </div>
      </template>

      <el-alert v-if="errorMsg" type="warning" :closable="false" :title="errorMsg" />

      <el-skeleton v-else-if="loading && !detail" :rows="3" animated />

      <template v-else>
        <el-descriptions v-if="mainAgent" :column="1" size="small" border class="mb8">
          <el-descriptions-item label="主 Agent">
            <el-tag size="small" type="primary">{{ mainAgent.name }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="职责">{{ mainAgent.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工具清单">
            <el-tag v-for="t in mainAgent.tools" :key="t" class="tag" size="small" effect="plain">{{ t }}</el-tag>
            <span v-if="!mainAgent.tools.length">-</span>
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="subAgents.length" class="sub-list">
          <div class="sub-title">子 Agent（{{ subAgents.length }}）</div>
          <el-table :data="subAgents" border stripe size="small">
            <el-table-column prop="name" label="子 Agent" min-width="140" />
            <el-table-column prop="description" label="职责" min-width="220" show-overflow-tooltip />
            <el-table-column label="工具清单" min-width="220">
              <template #default="{ row }">
                <el-tag v-for="t in row.tools" :key="t" class="tag" size="small" effect="plain">{{ t }}</el-tag>
                <span v-if="!row.tools.length">-</span>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty description="该子 Agent 暂未绑定工具" :image-size="60" />
            </template>
          </el-table>
        </div>

        <el-empty
          v-else-if="!mainAgent"
          description="后端未返回主/子 Agent 信息，请查看下方原始详情"
          :image-size="70"
        />
      </template>
    </el-card>

    <CapabilityChatBox capability-id="sub-agent" v-model:prompt="prompt" />

    <CapabilityDetailCard capability-id="sub-agent" title="主/子 Agent 原始详情" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.sub-list {
  margin-top: 4px;
}
.sub-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-regular);
  margin-bottom: 6px;
}
.tag {
  margin: 0 6px 4px 0;
}
.mb8 {
  margin-bottom: 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
