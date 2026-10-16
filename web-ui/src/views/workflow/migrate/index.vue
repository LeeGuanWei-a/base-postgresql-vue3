<!--
  流程替换（运行中实例迁移到新流程定义）— 独立管理页
  用途：管理员手动迁移单条流程；查询流程替换历史
  路径：/workflow/migrate
  权限：workflow:process:migrate
-->
<template>
  <div class="app-container" v-loading="loading">
    <el-card header="流程替换 - 运行中实例迁移到新流程定义" class="mb-12">
      <el-alert
        type="warning" :closable="false" show-icon
        title="使用说明"
        description="用于人事变动 / 流程版本切换 / 业务模式调整等场景：把一条正在跑的流程实例整条迁到新流程定义继续跑。新流程沿用旧流程的启动人 / 流程名，复制历史意见和附件，可选地镜像推进若干节点，最后废除旧流程。整个过程在单一事务内执行，任何步骤失败整体回滚。"
        class="mb-16"
      />

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" :inline="false">
        <el-form-item label="旧流程实例 id" prop="oldProcInsId">
          <el-input
            v-model="form.oldProcInsId" placeholder="选填或粘贴运行中流程实例 id"
            clearable style="width: 460px"
            @blur="onOldProcInsIdBlur"
          >
            <template #append>
              <el-button :icon="Search" @click="onPickOldInstance">从我的已发中选</el-button>
            </template>
          </el-input>
          <div v-if="oldInstanceInfo" class="info-line">
            <el-tag size="small" type="info">流程定义：{{ oldInstanceInfo.procDefName }}（{{ oldInstanceInfo.procDefKey }}）</el-tag>
            <el-tag size="small" type="warning" v-if="oldInstanceInfo.currentTaskKey">
              当前节点：{{ oldInstanceInfo.currentTaskName || oldInstanceInfo.currentTaskKey }}
            </el-tag>
            <el-tag size="small" type="success">
              已走过 {{ (oldInstanceInfo.finishedUserTaskKeys || []).length }} 个审批节点
            </el-tag>
            <el-tag size="small" type="info">启动人：{{ oldInstanceInfo.startUserId }}</el-tag>
          </div>
        </el-form-item>

        <el-form-item label="新流程定义" prop="newProcessDefinitionKey">
          <el-select
            v-model="form.newProcessDefinitionKey" placeholder="选新流程定义" filterable clearable
            style="width: 460px" @change="onNewKeyChange"
          >
            <el-option
              v-for="m in modelOptions" :key="m.modelId"
              :value="m.modelKey"
              :label="`${m.modelName} (${m.modelKey})`"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="启动变量">
          <el-input
            v-model="form.startVariablesText" type="textarea" :rows="3"
            placeholder='JSON 格式，如 {"st": 0, "amount": 1000}'
            style="width: 460px"
          />
          <div style="color: #909399; font-size: 12px; margin-top: 4px">
            选填，会作为新流程启动时的变量（在 bpmn 表达式中可引用）。
          </div>
        </el-form-item>

        <el-form-item label="镜像推进点">
          <div style="width: 100%">
            <el-alert
              v-if="!hasManualStep"
              type="success" :closable="false" show-icon
              title="自动镜像（推荐）"
              description="未勾选任何节点时，系统会读取旧流程已完成的审批节点历史，按同样顺序把新流程推到与旧流程等价的位置。会签节点按旧流程实际完成的次数推进，不会多推也不会少推。"
              style="margin-bottom: 8px; width: 700px"
            />
            <el-alert
              v-else
              type="warning" :closable="false" show-icon
              title="手动推进模式"
              description="已勾选节点：系统将按「新流程图的走向顺序」依次推进这些节点（不是勾选顺序）。请确认勾选的节点都是旧流程已经走过的，否则会推过头。"
              style="margin-bottom: 8px; width: 700px"
            />
            <el-table :data="advanceStepList" border style="width: 700px" size="small">
              <el-table-column label="选择" width="60" align="center">
                <template #default="scope">
                  <el-checkbox v-model="scope.row.checked" />
                </template>
              </el-table-column>
              <el-table-column prop="taskDefinitionKey" label="新流程 taskKey" min-width="180" />
              <el-table-column prop="name" label="节点名" min-width="120" show-overflow-tooltip />
              <el-table-column label="旧流程已走过" min-width="110" align="center">
                <template #default="scope">
                  <el-tag v-if="scope.row.passedByOld" size="small" type="success">已走过</el-tag>
                  <el-tag v-else size="small" type="info">未走过</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="附加变量" min-width="160">
                <template #default="scope">
                  <el-input v-model="scope.row.variablesText" placeholder='{"flag":0}' size="small" />
                </template>
              </el-table-column>
            </el-table>
            <div v-if="newUserTaskList.length === 0 && form.newProcessDefinitionKey" style="color: #909399; font-size: 12px; margin-top: 4px">
              未取到 userTask 列表（请确认新流程定义已部署）。
            </div>
          </div>
        </el-form-item>

        <el-form-item label="复制历史意见">
          <el-switch v-model="form.copyComments" />
        </el-form-item>

        <el-form-item label="复制附件">
          <el-switch v-model="form.copyAttachments" />
        </el-form-item>

        <el-form-item label="变更原因" prop="reason">
          <el-input
            v-model="form.reason" type="textarea" :rows="2"
            placeholder="例如：人事变动 - 审批人 A 离职，由 B 接替"
            style="width: 460px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" :icon="Position" @click="onSubmit">
            执行迁移
          </el-button>
          <el-button :icon="Refresh" @click="onReset">重置</el-button>
          <el-button :icon="Search" @click="onValidate">先校验</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 选运行中流程实例的弹窗 -->
    <el-dialog
      v-model="pickDialogVisible" title="选择运行中流程实例" width="900px"
      @close="onPickDialogClose"
    >
      <el-form :inline="true" :model="pickQuery">
        <el-form-item label="流程名">
          <el-input v-model="pickQuery.processName" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadPickList">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="pickLoading" :data="pickList" @row-click="onPickRow" highlight-current-row>
        <el-table-column prop="procDefName" label="流程名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="procInsId" label="流程实例 id" min-width="280" />
        <el-table-column prop="createTime" label="发起时间" width="170" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.processStatus === 'running' ? 'warning' : 'info'" size="small">
              {{ scope.row.processStatus === 'running' ? '进行中' : scope.row.processStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="scope">
            <el-button link type="primary" icon="Check" @click.stop="onPickRow(scope.row)">选中</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="pickTotal > 0" :total="pickTotal"
        v-model:page="pickQuery.pageNum" v-model:limit="pickQuery.pageSize"
        @pagination="loadPickList"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { listModelLaunch } from '@/api/workflow/model'
import {
  migrateProcess, validateMigrate,
  listNewProcessUserTasks, getCurrentTaskOfInstance
} from '@/api/workflow/migrate'
import { ownList } from '@/api/workflow/process'
import { ref, reactive, computed, onMounted, getCurrentInstance, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { Search, Refresh, Position } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const route = useRoute()

const loading = ref(false)
const submitting = ref(false)

// ============ 主表单 ============
const formRef = ref()
const form = reactive({
  oldProcInsId: '',
  newProcessDefinitionKey: '',
  startVariablesText: '',
  copyComments: true,
  copyAttachments: true,
  reason: ''
})
const rules = {
  oldProcInsId: [{ required: true, message: '请输入旧流程实例 id', trigger: 'blur' }],
  newProcessDefinitionKey: [{ required: true, message: '请选择新流程定义', trigger: 'change' }],
  reason: [{ required: true, message: '请输入变更原因', trigger: 'blur' }]
}

// 旧流程的查询结果（流程定义 key + 当前 task key + 启动人）
const oldInstanceInfo = ref(null)

// 已部署可启动的模型下拉
const modelOptions = ref([])
// 新流程 userTask 列表（从后端实时取）
const newUserTaskList = ref([])
// 镜像推进点表格（由 newUserTaskList + oldInstanceInfo 派生）
const advanceStepList = ref([])

// 是否走了"手动推进"模式：勾了任意节点就走手动模式，否则走后端自动镜像
const hasManualStep = computed(() => advanceStepList.value.some(s => s.checked))

// ============ 选流程实例弹窗 ============
const pickDialogVisible = ref(false)
const pickLoading = ref(false)
const pickList = ref([])
const pickTotal = ref(0)
const pickQuery = reactive({ pageNum: 1, pageSize: 10, processName: '' })

onMounted(async () => {
  loading.value = true
  try {
    // /listModelLaunch 是分页接口，列表在 res.rows（不是 res.data）
    const res = await listModelLaunch({})
    if (res.code === 200) { modelOptions.value = res.rows || [] }
  } finally { loading.value = false }
  // 从路由 query 读 oldProcInsId（own.vue 跳转过来）
  if (route.query.oldProcInsId) {
    form.oldProcInsId = String(route.query.oldProcInsId)
    await nextTick()
    onOldProcInsIdBlur()
  }
})

// 旧流程实例 id 失焦时拉一下元信息（顺便智能推荐用）
async function onOldProcInsIdBlur() {
  if (!form.oldProcInsId) {
    oldInstanceInfo.value = null
    rebuildAdvanceSteps()
    return
  }
  try {
    const res = await getCurrentTaskOfInstance(form.oldProcInsId)
    if (res.code === 200) {
      oldInstanceInfo.value = res.data
      rebuildAdvanceSteps()
    } else {
      oldInstanceInfo.value = null
      proxy.$modal.msgWarning(res.msg || '未取到旧流程信息')
    }
  } catch (e) {
    oldInstanceInfo.value = null
  }
}

// 选了新流程定义之后，取 userTask 列表
async function onNewKeyChange(key) {
  newUserTaskList.value = []
  if (!key) { rebuildAdvanceSteps(); return }
  try {
    const res = await listNewProcessUserTasks(key)
    if (res.code === 200) { newUserTaskList.value = res.data || [] }
  } catch (e) {
    proxy.$modal.msgError('取新流程 userTask 失败：' + (e.message || e))
  }
  rebuildAdvanceSteps()
}

// 重建推进点列表：只做"展示 + 手动调整"，不再自动勾选。
// 不自动勾选的原因：勾上"旧流程当前所在节点"会把新流程推过该节点（过冲），
// 而留空时后端会按旧流程历史自动镜像，落点与旧流程完全等价。
function rebuildAdvanceSteps() {
  const passed = new Set(
    (oldInstanceInfo.value && oldInstanceInfo.value.finishedUserTaskKeys) || []
  )
  const list = (newUserTaskList.value || []).map(ut => ({
    taskDefinitionKey: ut.id,
    name: ut.name,
    checked: false,
    passedByOld: passed.has(ut.id),
    variablesText: ''
  }))
  advanceStepList.value = list
}

// ============ 选运行中流程实例 ============
function onPickOldInstance() {
  pickDialogVisible.value = true
  loadPickList()
}
async function loadPickList() {
  pickLoading.value = true
  try {
    const res = await ownList(pickQuery)
    if (res.code === 200) {
      pickList.value = (res.rows || []).filter(r => r.processStatus === 'running')
      pickTotal.value = pickList.value.length
    }
  } finally { pickLoading.value = false }
}
function onPickRow(row) {
  form.oldProcInsId = row.procInsId
  pickDialogVisible.value = false
  onOldProcInsIdBlur()
}
function onPickDialogClose() {
  pickList.value = []
  pickTotal.value = 0
  pickQuery.pageNum = 1
}

// ============ 提交 ============
function buildPayload() {
  // 启动变量 JSON 解析
  let startVariables = {}
  if (form.startVariablesText && form.startVariablesText.trim()) {
    try {
      startVariables = JSON.parse(form.startVariablesText)
    } catch (e) {
      throw new Error('启动变量 JSON 格式错误：' + e.message)
    }
  }
  // 推进点列表
  const advanceSteps = advanceStepList.value
    .filter(s => s.checked)
    .map(s => {
      const step = { taskDefinitionKey: s.taskDefinitionKey }
      if (s.variablesText && s.variablesText.trim()) {
        try { step.variables = JSON.parse(s.variablesText) }
        catch (e) { throw new Error(`节点 [${s.taskDefinitionKey}] 附加变量 JSON 格式错误：` + e.message) }
      }
      return step
    })
  return {
    oldProcInsId: form.oldProcInsId,
    newProcessDefinitionKey: form.newProcessDefinitionKey,
    startVariables,
    copyComments: form.copyComments,
    copyAttachments: form.copyAttachments,
    reason: form.reason,
    advanceSteps
  }
}

async function onSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch { return }
  let payload
  try { payload = buildPayload() } catch (e) { proxy.$modal.msgError(e.message); return }

  // 过冲检查：手动勾了"旧流程没走过"的节点，会把新流程推到旧流程更后面
  if (hasManualStep.value) {
    const overshoot = advanceStepList.value
      .filter(s => s.checked && !s.passedByOld)
      .map(s => `${s.name || s.taskDefinitionKey}`)
    if (overshoot.length > 0) {
      try {
        await proxy.$modal.confirm(
          `以下节点的旧流程还没走过，推进它们会让新流程停在旧流程更靠后的位置：\n${overshoot.join('、')}\n\n确定继续吗？`
        )
      } catch {
        return
      }
    }
  }

  try {
    await proxy.$modal.confirm(
      `确认要把流程 [${form.oldProcInsId}] 迁移到 [${form.newProcessDefinitionKey}] 吗？\n此操作会废除旧流程、无法撤销。`
    )
  } catch {
    return
  }
  submitting.value = true
  try {
    const res = await migrateProcess(payload)
    if (res.code === 200) {
      const newId = res.data && res.data.newProcInsId
      const copied = (res.data && res.data.copyCommentCount) || 0
      const attach = (res.data && res.data.copyAttachmentCount) || 0
      const adv = (res.data && res.data.advancedStepCount) || 0
      proxy.$modal.msgSuccess(`迁移成功：意见 ${copied} 条 / 附件 ${attach} 条 / 推进点 ${adv} 个`)
      if (newId) {
        await proxy.$alert(
          `新流程实例 id：${newId}`,
          '迁移成功',
          {
            type: 'success',
            confirmButtonText: '查看新流程',
            cancelButtonText: '留在当前页',
            showCancelButton: true
          }
        ).then(() => {
          proxy.$router.push({ path: '/workflow/process/detail', query: { procInsId: newId } })
        }).catch(() => {})
      }
    } else {
      proxy.$modal.msgError('迁移失败：' + (res.msg || '未知错误'))
    }
  } catch (e) {
    proxy.$modal.msgError('迁移失败：' + (e && e.message ? e.message : e))
  } finally {
    submitting.value = false
  }
}

async function onValidate() {
  let payload
  try { payload = buildPayload() } catch (e) { proxy.$modal.msgError(e.message); return }
  const res = await validateMigrate(payload)
  if (res.code === 200) { proxy.$modal.msgSuccess('参数校验通过') }
  else { proxy.$modal.msgError(res.msg || '校验失败') }
}

function onReset() {
  formRef.value && formRef.value.resetFields()
  oldInstanceInfo.value = null
  newUserTaskList.value = []
  advanceStepList.value = []
}
</script>

<style scoped>
.mb-12 { margin-bottom: 12px; }
.mb-16 { margin-bottom: 16px; }
.info-line { display: flex; gap: 8px; margin-top: 4px; flex-wrap: wrap; }
</style>
