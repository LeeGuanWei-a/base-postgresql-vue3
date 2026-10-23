<script setup name="SchedulerPanel">
/**
 * 调度器（外部依赖型）—— 按 cron 表达式定时触发智能体。
 *
 * 演示要点：
 * 1. GET /agentscope/scheduler/status：任务列表（任务名 / cron / 下次执行时间 / 执行次数 / 最近结果）；
 * 2. POST /agentscope/scheduler/task：新增任务（任务名 + cron 表达式 + prompt）；
 * 3. DELETE /agentscope/scheduler/task/{name}：删除任务。
 *
 * cron 采用<b>六段</b>格式：秒 分 时 日 月 周，例如 0 0 9 * * * 表示每天 9 点整执行。
 */
import { computed, onMounted, ref } from 'vue'
import { schedulerAddTask, schedulerRemoveTask, schedulerStatus } from '@/api/agentscope'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

const props = defineProps({
  capability: { type: Object, required: true }
})

const status = ref(null)
const loading = ref(false)
const errorMsg = ref('')

const form = ref({
  name: 'demo-daily-report',
  cron: '0 0 9 * * *',
  prompt: '请汇总一下平台当前的用户、角色与最近操作日志，输出一份日报。'
})
const submitting = ref(false)
const removingName = ref('')

/** 能力是否就绪 */
const capabilityReady = computed(() => props.capability?.enabled !== false)

/** 提取任务列表 —— 兼容多种字段命名 */
function pickTasks(data) {
  if (!data || typeof data !== 'object') return []
  for (const key of ['tasks', 'taskList', 'jobs', 'list', 'items']) {
    if (Array.isArray(data[key])) return data[key]
  }
  return []
}

const taskRows = computed(() =>
  pickTasks(status.value).map((item, index) => {
    if (typeof item === 'string') {
      return { name: item, cron: '-', next: '-', count: '-', last: '-', enabled: true }
    }
    const it = item || {}
    return {
      name: it.name || it.taskName || it.id || `任务 #${index + 1}`,
      cron: it.cron || it.cronExpression || it.schedule || '-',
      next: it.next || it.nextRunTime || it.nextFireTime || it.nextRun || '-',
      count: it.count ?? it.executeCount ?? it.runCount ?? it.times ?? '-',
      last: it.last ?? it.lastResult ?? it.result ?? it.lastMessage ?? '-',
      enabled: it.enabled !== false
    }
  })
)

const summary = computed(() => {
  const data = status.value || {}
  const pick = (...keys) => {
    for (const key of keys) {
      if (data[key] !== undefined && data[key] !== null && data[key] !== '') return data[key]
    }
    return '-'
  }
  return [
    { label: '任务总数', value: pick('taskCount', 'tasks', 'count') },
    { label: '调度器是否启用', value: pick('enabled', 'active', 'started', 'running') },
    { label: '已执行次数', value: pick('totalExecutions', 'executeCount', 'runCount', 0) },
    { label: '时区', value: pick('timezone', 'zone', 'timeZone') }
  ]
})

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await schedulerStatus()
    status.value = res?.data ?? null
  } catch (e) {
    status.value = null
    errorMsg.value = e?.message || '调度器状态加载失败'
  } finally {
    loading.value = false
  }
}

/** 新增任务 */
async function addTask() {
  if (submitting.value) return
  if (!form.value.name || !form.value.name.trim()) {
    errorMsg.value = '请填写任务名'
    return
  }
  if (!form.value.cron || !form.value.cron.trim()) {
    errorMsg.value = '请填写 cron 表达式'
    return
  }
  submitting.value = true
  errorMsg.value = ''
  try {
    // cron 同时给出 cron / cronExpression 两种命名，兼容后端 DTO 字段
    await schedulerAddTask({
      name: form.value.name.trim(),
      cron: form.value.cron.trim(),
      cronExpression: form.value.cron.trim(),
      prompt: form.value.prompt
    })
    await load()
  } catch (e) {
    errorMsg.value = e?.message || String(e || '新增调度任务失败')
  } finally {
    submitting.value = false
  }
}

/** 删除任务 */
async function removeTask(row) {
  if (removingName.value) return
  removingName.value = row.name
  errorMsg.value = ''
  try {
    await schedulerRemoveTask(row.name)
    await load()
  } catch (e) {
    errorMsg.value = e?.message || String(e || '删除调度任务失败')
  } finally {
    removingName.value = ''
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
      title="调度器依赖 agentscope-extensions-scheduler。未接入时任务列表为空，属于预期现象；下方表单可先验证接口契约。"
    />

    <el-alert
      v-if="!capabilityReady"
      class="mb8"
      type="info"
      :closable="false"
      show-icon
      title="当前能力标记为「未就绪」：通用对话接口会返回「能力不可用（缺少 Agent Bean）」，属于正常提示，可先接入调度器后再试。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>调度器状态</span>
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

        <div class="section-title">任务列表</div>
        <el-table :data="taskRows" border stripe size="small" empty-text="暂无调度任务">
          <el-table-column prop="name" label="任务名" width="180" show-overflow-tooltip />
          <el-table-column prop="cron" label="cron 表达式" width="160">
            <template #default="scope">
              <span class="mono">{{ scope.row.cron }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="next" label="下次执行时间" width="180" />
          <el-table-column prop="count" label="执行次数" width="100" />
          <el-table-column prop="last" label="最近结果" min-width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="90" fixed="right">
            <template #default="scope">
              <el-button
                type="danger"
                link
                :loading="removingName === scope.row.name"
                @click="removeTask(scope.row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>新增调度任务</span>
      </template>

      <el-form label-width="90px">
        <el-form-item label="任务名" required>
          <el-input v-model="form.name" placeholder="例如：demo-daily-report" clearable />
        </el-form-item>
        <el-form-item label="cron 表达式" required>
          <el-input v-model="form.cron" placeholder="0 0 9 * * *" clearable />
          <div class="tip">
            六段格式：<b>秒 分 时 日 月 周</b>，例如 <code>0 0 9 * * *</code> 表示每天 9 点整执行。
          </div>
        </el-form-item>
        <el-form-item label="执行 prompt">
          <el-input
            v-model="form.prompt"
            type="textarea"
            :rows="3"
            placeholder="任务触发时交给智能体的问题"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="addTask">新增任务</el-button>
          <el-button class="ml8" @click="load">刷新列表</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>通用对话（验证能力是否已装配 Agent）</span>
      </template>
      <CapabilityChatBox capability-id="scheduler" default-prompt="查询一下当前的调度任务列表。" />
    </el-card>

    <CapabilityDetailCard capability-id="scheduler" title="调度器能力详情" class="mt12" />
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
.mono {
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
}
.tip {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.8;
}
.tip code {
  font-family: Consolas, Monaco, monospace;
  background: var(--el-fill-color-light);
  padding: 0 4px;
  border-radius: 3px;
}
.ml8 {
  margin-left: 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
