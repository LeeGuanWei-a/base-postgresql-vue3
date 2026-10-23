<script setup name="TaskListPanel">
/**
 * 任务清单（Todo List）—— 演示 AgentScope 的 enableTaskList 能力。
 *
 * 上半部分：后端 detail 接口返回的任务清单状态。
 * 中间部分：前端交互式任务清单演示（el-steps），不走后端。
 * 下半部分：真实对话，让 Agent 自己产出待办。
 */
import { computed, onMounted, ref } from 'vue'
import { capabilityDetail } from '@/api/agentscope'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref('帮我把「新增一个 Agent 能力面板」拆解成待办清单。')

const detail = ref(null)
const loading = ref(false)
const errorMsg = ref('')

/** 后端 TaskListCapability.detail()：
 *  { taskListEnabled, todoTool, maxIters, agentName, taskList: [{id, subject, description, state, owner, createdAt}], taskCount, note } */
const tasks = computed(() => {
  const d = detail.value
  if (!d || Array.isArray(d)) return []
  const raw = d.taskList || d.tasks || d.todos || d.list || []
  if (!Array.isArray(raw)) return []
  return raw.map((t, i) => {
    if (typeof t === 'string') return { id: String(i + 1), subject: t, description: '', state: '', owner: '', createdAt: '' }
    return {
      id: String(t?.id ?? i + 1),
      subject: t?.subject || t?.title || t?.name || '-',
      description: t?.description || t?.desc || '',
      state: t?.state || t?.status || '',
      owner: t?.owner || '',
      createdAt: t?.createdAt || t?.createTime || ''
    }
  })
})

/** 能力开关等配置项 */
const configs = computed(() => {
  const d = detail.value
  if (!d || Array.isArray(d)) return []
  const skip = ['taskList', 'tasks', 'todos', 'list']
  return Object.keys(d)
    .filter((k) => !skip.includes(k))
    .map((k) => ({ key: k, value: d[k] }))
})

function stateType(state) {
  const s = String(state || '').toUpperCase()
  if (['DONE', 'COMPLETED', 'FINISHED', 'SUCCESS'].includes(s)) return 'success'
  if (['TODO', 'PENDING', 'IN_PROGRESS', 'DOING'].includes(s)) return 'warning'
  if (['FAILED', 'ERROR', 'CANCELLED'].includes(s)) return 'danger'
  return 'info'
}

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await capabilityDetail('task-list')
    detail.value = res.data
  } catch (e) {
    errorMsg.value = e?.message || '加载失败'
    detail.value = null
  } finally {
    loading.value = false
  }
}

onMounted(load)

const steps = ref([
  { title: '', done: false },
  { title: '', done: false },
  { title: '', done: false }
])
const activeStep = ref(0)

const stepTitles = computed(() =>
  steps.value.map((s, i) => s.title?.trim() || `第${['一', '二', '三'][i] || i + 1}步（未填写）`)
)

const finishedCount = computed(() => steps.value.filter((s) => s.title?.trim()).length)

function next() {
  if (activeStep.value < steps.value.length - 1) {
    activeStep.value += 1
  }
}

function prev() {
  if (activeStep.value > 0) {
    activeStep.value -= 1
  }
}

function resetSteps() {
  steps.value = [
    { title: '', done: false },
    { title: '', done: false },
    { title: '', done: false }
  ]
  activeStep.value = 0
}

function fillExamples() {
  steps.value = [
    { title: '读取后端能力清单，定位未注册的能力', done: false },
    { title: '实现对应的 CapabilityProvider 与 Agent Bean', done: false },
    { title: '在前端注册面板并刷新页面验证', done: false }
  ]
  activeStep.value = 0
}
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="success"
      :closable="false"
      show-icon
      title="enableTaskList 打开后，Agent 会把复杂目标拆成可勾选的待办清单，并在每轮 ReAct 迭代中更新进度。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>任务清单交互演示（纯前端）</span>
          <div class="card-header-btns">
            <el-button link type="primary" @click="fillExamples">填充示例</el-button>
            <el-button link @click="resetSteps">重置</el-button>
          </div>
        </div>
      </template>

      <el-row :gutter="12">
        <el-col :xs="24" :md="10">
          <div class="field-label">步骤一</div>
          <el-input v-model="steps[0].title" placeholder="例如：读取后端能力清单" clearable />
          <div class="field-label mt8">步骤二</div>
          <el-input v-model="steps[1].title" placeholder="例如：实现 CapabilityProvider" clearable />
          <div class="field-label mt8">步骤三</div>
          <el-input v-model="steps[2].title" placeholder="例如：前端面板联调" clearable />
          <div class="step-actions">
            <el-button :disabled="activeStep <= 0" @click="prev">上一步</el-button>
            <el-button type="primary" :disabled="activeStep >= steps.length - 1" @click="next">下一步</el-button>
            <el-tag size="small" type="info">已填写 {{ finishedCount }}/{{ steps.length }}</el-tag>
          </div>
        </el-col>

        <el-col :xs="24" :md="14">
          <el-steps :active="activeStep" align-center finish-status="success">
            <el-step
              v-for="(title, idx) in stepTitles"
              :key="idx"
              :title="title"
              :description="idx === activeStep ? '进行中' : idx < activeStep ? '已完成' : '待开始'"
            />
          </el-steps>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>后端任务清单状态</span>
          <el-button link type="primary" :loading="loading" @click="load">刷新</el-button>
        </div>
      </template>

      <el-alert v-if="errorMsg" type="warning" :closable="false" :title="errorMsg" />

      <el-skeleton v-else-if="loading && !detail" :rows="3" animated />

      <template v-else>
        <el-descriptions v-if="configs.length" class="mb8" :column="3" size="small" border>
          <el-descriptions-item v-for="item in configs" :key="item.key" :label="item.key">
            <span v-if="typeof item.value === 'object'">{{ JSON.stringify(item.value) }}</span>
            <span v-else>{{ item.value }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <el-table :data="tasks" border stripe size="small">
          <el-table-column prop="subject" label="待办事项" min-width="180" show-overflow-tooltip />
          <el-table-column prop="description" label="说明" min-width="220" show-overflow-tooltip />
          <el-table-column label="状态" width="110" align="center">
            <template #default="{ row }">
              <el-tag size="small" :type="stateType(row.state)">{{ row.state || '-' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="owner" label="负责人" width="110" />
          <el-table-column prop="createdAt" label="创建时间" min-width="160" />
          <template #empty>
            <el-empty description="暂无任务，请先在下方与 Agent 对话让它拆解待办" :image-size="60" />
          </template>
        </el-table>
      </template>
    </el-card>

    <CapabilityChatBox capability-id="task-list" v-model:prompt="prompt" />

    <CapabilityDetailCard capability-id="task-list" title="任务清单状态（后端 detail）" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-header-btns {
  display: flex;
  align-items: center;
  gap: 4px;
}
.field-label {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-bottom: 4px;
}
.step-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
}
.mt8 {
  margin-top: 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
