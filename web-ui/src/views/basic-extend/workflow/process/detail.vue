<template>
  <div class="app-container" v-loading="loading">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card header="流程信息">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="流程状态">{{ processStatusText }}</el-descriptions-item>
            <el-descriptions-item label="当前任务数">{{ detail.currentTaskNum ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="当前节点">{{ currentTask?.activityName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="当前处理人">{{ currentTask?.assignee || '-' }}</el-descriptions-item>
            <el-descriptions-item label="节点类型">{{ currentTask?.nodeType || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card header="流程图" class="mt-20">
          <ProcessViewer :proc-ins-id="procInsId" />
        </el-card>

        <!-- 节点绑定的自定义表单（只读展示已填数据） -->
        <el-card v-if="taskFormJson" header="流程表单" class="mt-20">
          <FormRender :form-json="taskFormJson" :form-data="taskFormData" readonly />
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card header="当前任务">
          <div v-if="currentTask">
            <p>任务名称：{{ currentTask.activityName }}</p>
            <p>处理人：{{ currentTask.assignee || '未签收' }}</p>
            <p>节点类型：{{ currentTask.nodeType }}</p>

            <!--
              操作按钮由节点「授权功能」决定（后端 currentTask.permissionJson），
              顺序会签节点额外显示「会签回退」。鼠标悬浮显示各按钮的作用说明。
            -->
            <div v-if="canApprove" class="action-btns">
              <!--
                注意：v-hasPermi 用在这里（el-tooltip 上）而不是 el-button 上。
                该指令是在 mounted 里把元素从 DOM 移除的，如果挂在按钮上，
                tooltip 的 mounted 就找不到子元素了，悬浮说明会整体失效。
                另外按钮上再加一个原生 title 兜底，保证任何情况下悬浮都有说明。

                actionButtons 由节点「授权功能」(permissionJson) 决定，节点勾了哪些就出现哪些。
                因此"前加签/后加签/终止"在节点没勾时不会显示——需先在「流程模型 → 节点 → 授权功能」里勾选。
              -->
              <el-tooltip
                v-for="btn in actionButtons"
                :key="btn.action"
                :content="btn.tip"
                placement="top"
                v-hasPermi="btn.permis"
              >
                <el-button
                  size="small"
                  :type="btn.type"
                  :title="btn.tip"
                  @click="openAction(btn.action)"
                >{{ btn.label }}</el-button>
              </el-tooltip>
              <span v-if="actionButtons.length === 0" class="no-action-tip">
                当前节点未配置「授权功能」，无可执行操作
              </span>
            </div>
          </div>
          <el-empty v-else description="无当前任务（流程已结束，或进入时未携带 taskId）" />
        </el-card>

        <el-card header="审批记录" class="mt-20">
          <ApprovalTimeline :proc-ins-id="procInsId" />
        </el-card>
      </el-col>
    </el-row>

    <TaskDialog ref="taskDialogRef" @success="loadDetail" />
  </div>
</template>

<script setup>
import { getProcessDetail } from '@/api/workflow/process'
import { useRoute } from 'vue-router'
import { ref, computed } from 'vue'
import ProcessViewer from './components/ProcessViewer.vue'
import ApprovalTimeline from './components/ApprovalTimeline.vue'
import TaskDialog from './components/TaskDialog.vue'
import { buildActionButtons } from './components/actionMeta'
import FormRender from '@/components/FormRender/index.vue'

const route = useRoute()
const procInsId = route.query.procInsId
// taskId 可选：后端只有传了 taskId 才会返回 currentTask 与 taskFormData
const taskId = route.query.taskId

const loading = ref(true)
const detail = ref({})
const taskDialogRef = ref()

// WfDetailVo.currentTask (WfUserTaskDto)
const currentTask = computed(() => detail.value.currentTask || null)
// 只有带 taskId 进来才允许在当前页审批
const canApprove = computed(() => !!currentTask.value && !!taskId)

/** 当前节点绑定的自定义表单定义（WfTaskFormVo.designerContent，vform3 的 JSON） */
const taskFormJson = computed(() => {
  const raw = detail.value?.taskFormData?.designerContent
  if (!raw) return null
  try {
    const obj = JSON.parse(raw)
    return obj && obj.widgetList ? obj : null
  } catch (e) {
    return null
  }
})

/** 表单已填数据（WfTaskFormVo.formContent） */
const taskFormData = computed(() => {
  const raw = detail.value?.taskFormData?.formContent
  if (!raw) return {}
  try {
    return JSON.parse(raw)
  } catch (e) {
    return {}
  }
})

const processStatusText = computed(() => {
  const s = detail.value.processStatus
  if (!s) return '-'
  const map = { running: '进行中', finished: '已结束', stop: '已终止', cancel: '已撤销' }
  return map[s] || s
})

async function loadDetail() {
  loading.value = true
  try {
    const res = await getProcessDetail(procInsId, taskId)
    if (res.code === 200) detail.value = res.data || {}
  } finally {
    loading.value = false
  }
}

/**
 * 当前节点可用的操作按钮。
 * 按钮定义在 components/actionMeta.js，由节点「授权功能」(permissionJson) + 是否顺序会签决定：
 *   ① 取自节点「授权功能」（后端 currentTask.permissionJson，节点上勾了哪些就出现哪些）
 *   ② 「会签回退」仅在顺序会签节点 + 授权功能勾选了它时出现
 *   ③ 「前加签/后加签/终止」在节点「授权功能」里勾选后才会出现
 *      （需在「流程模型 → 节点 → 授权功能」里勾选，勾选项见 sys_dict_data wf_process_permission）
 */
const actionButtons = computed(() =>
  buildActionButtons(currentTask.value?.permissionJson, currentTask.value?.sequentialMultiInstance)
)

function openAction(action) {
  taskDialogRef.value.open({
    action,
    task: { taskId, procInsId }
  })
}

loadDetail()
</script>

<style scoped>
.mt-20 {
  margin-top: 20px;
}
.action-btns {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.no-action-tip {
  font-size: 12px;
  color: #909399;
}
</style>
