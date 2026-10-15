<template>
  <el-dialog
    :title="dialogTitle"
    v-model="open"
    :width="formJson ? '760px' : '500px'"
    append-to-body
    :close-on-click-modal="false"
  >
    <!--
      顺序很重要：审批意见等"操作字段"放在最上面，业务表单放在下面。
      之前表单在最上面（还带必填项），审批人很容易把"意见"填进业务表单的字段里 —— 实测踩过。
    -->
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item
        v-if="action === 'complete' || action === 'reject' || action === 'mulitiSequentialUnComplete'"
        label="审批意见"
        prop="comment"
      >
        <el-input v-model="form.comment" type="textarea" :rows="3" placeholder="请输入审批意见" />
      </el-form-item>
      <el-form-item v-if="isUserAction" :label="userLabel" prop="userId">
        <div class="picker-row">
          <el-tag
            v-for="u in pickedUsers"
            :key="u.userId"
            closable
            @close="removePickedUser(u.userId)"
          >{{ u.nickName || u.userId }}</el-tag>
          <span v-if="pickedUsers.length === 0" class="picker-empty">未选择</span>
          <el-button size="small" @click="openUserPicker">
            {{ singleUser ? '选择人员' : '添加人员' }}
          </el-button>
        </div>
      </el-form-item>
      <el-form-item v-if="action === 'return'" label="退回节点" prop="targetActivityId">
        <el-select
          v-model="form.targetActivityId"
          placeholder="请选择要退回的节点"
          style="width: 100%"
          :loading="returnNodeLoading"
        >
          <el-option
            v-for="node in returnNodes"
            :key="node.id"
            :label="node.name || node.id"
            :value="node.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-if="action === 'stop'" label="终止原因" prop="comment">
        <el-input v-model="form.comment" type="textarea" :rows="3" placeholder="请输入终止原因" />
      </el-form-item>
      <el-form-item v-if="action === 'complete' || action === 'reject'" label="流程变量">
        <el-input v-model="form.variables" type="textarea" :rows="2" placeholder='JSON 格式，如 {"key":"value"}' />
      </el-form-item>
    </el-form>

    <!--
      业务表单：与上面的「审批意见」是两套独立数据 ——
      审批意见 → act_hi_comment / wf_comment_info（显示在审批记录里）
      业务表单 → wf_task_form.form_content（显示在详情页的「流程表单」卡片里）
    -->
    <div v-if="formJson" class="task-form-area">
      <el-divider />
      <div class="task-form-area__title">业务表单（与上面的审批意见无关，数据单独保存）</div>
      <FormRender
        ref="formRenderRef"
        :form-json="formJson"
        :form-data="formData"
        empty-text=""
      />
    </div>

    <template #footer>
      <el-button @click="open = false">取 消</el-button>
      <el-button type="primary" :loading="submitting" @click="submit">确 定</el-button>
    </template>

    <!-- 人员选择器：转办/委派=单选，加签=多选 -->
    <ProcessUser :select-user-data="userPicker" @updateDialogVisible="onUserPicked" />
  </el-dialog>
</template>

<script setup>
import {
  completeTask,
  rejectTask,
  transferTask,
  delegateTask,
  beforeAddSignTask,
  afterAddSignTask,
  returnTask,
  stopProcess,
  mulitiSequentialUnComplete,
  returnList
} from '@/api/workflow/task'
import { getProcessDetail } from '@/api/workflow/process'
import { ref, reactive, computed, getCurrentInstance } from 'vue'
import FormRender from '@/components/FormRender/index.vue'
import ProcessUser from '@/components/ProcessUser/index.vue'

const emit = defineEmits(['success'])
const { proxy } = getCurrentInstance()

const open = ref(false)
const formRef = ref()
const action = ref('')
const task = ref(null)
const submitting = ref(false)

// ---- 节点自定义表单 ----
const formRenderRef = ref(null)
const formJson = ref(null)          // 表单定义（vform3 JSON）
const formData = ref({})            // 已填数据（回显）

const data = reactive({
  form: { comment: '', userId: '', targetActivityId: '', variables: '' },
  rules: {
    comment: [{ required: true, message: '请输入意见', trigger: 'blur' }],
    userId: [{ required: true, message: '请选择人员', trigger: 'change' }],
    targetActivityId: [{ required: true, message: '请选择退回节点', trigger: 'change' }]
  }
})
const { form, rules } = data

// ---- 人员选择器（转办/委派=单选，加签=多选）----
// ProcessUser 的契约：props.selectUserData = { dialogVisible, info, type, users, data }
//                    emit('updateDialogVisible', { saveBtn, info, data })
const pickedUsers = ref([])
const userPicker = reactive({ dialogVisible: false, info: false, type: '', users: [], data: [] })

/** 需要选人的动作 */
const isUserAction = computed(() => ['transfer', 'delegate', 'addSign'].includes(action.value))
/** 转办/委派只能选一个；加签可以选多个 */
const singleUser = computed(() => action.value === 'transfer' || action.value === 'delegate')
const userLabel = computed(() => (action.value === 'addSign' ? '加签人' : '接收人'))

// ---- 退回节点列表 ----
const returnNodes = ref([])
const returnNodeLoading = ref(false)

const dialogTitle = computed(
  () =>
    ({
      complete: '同意',
      reject: '拒绝',
      transfer: '转办',
      delegate: '委派',
      addSign: '加签',
      return: '退回',
      stop: '终止',
      mulitiSequentialUnComplete: '会签回退'
    })[action.value] || '操作'
)

function openDialog(opts) {
  action.value = opts.action
  task.value = opts.task
  // 必须逐个重置 form 上的字段。
  // 原来写的是 form.value = {...}：那是在 form 上挂了一个名叫 value 的新属性，
  // 和模板 v-model 绑定的 form.comment / form.targetActivityId 根本不是同一个对象，
  // 于是审批意见、退回节点、接收人即使填了也读不到，提交上去全是空字符串。
  form.comment = ''
  form.userId = ''
  form.targetActivityId = ''
  form.variables = ''
  formJson.value = null
  formData.value = {}
  // 每次打开都重置选择器的状态
  pickedUsers.value = []
  userPicker.data = []
  returnNodes.value = []
  open.value = true
  loadTaskForm()
  // 退回：需要"可退回节点"下拉数据（后端按已办过且能到达当前节点筛选）
  if (opts.action === 'return') {
    loadReturnNodes()
  }
}
defineExpose({ open: openDialog })

/** 打开人员选择器；先把已选人员回显进去 */
function openUserPicker() {
  userPicker.info = singleUser.value
  userPicker.data = pickedUsers.value.slice()
  // 先置 false 再置 true：保证 dialogVisible 有变化，选择器内部的 watch 才会触发
  userPicker.dialogVisible = false
  setTimeout(() => {
    userPicker.dialogVisible = true
  }, 0)
}

function onUserPicked(row) {
  userPicker.dialogVisible = false
  if (row && row.saveBtn) {
    pickedUsers.value = row.data || []
    syncUserIds()
  }
}

function removePickedUser(userId) {
  pickedUsers.value = pickedUsers.value.filter((u) => u.userId !== userId)
  syncUserIds()
}

/** form.userId 存逗号分隔的用户 id：既满足表单必填校验，也直接作为提交值 */
function syncUserIds() {
  form.userId = pickedUsers.value.map((u) => u.userId).join(',')
}

/** 加载当前任务可退回的节点列表 */
async function loadReturnNodes() {
  returnNodeLoading.value = true
  try {
    const res = await returnList({
      taskId: task.value?.taskId,
      procInsId: task.value?.procInsId
    })
    // 后端返回 List<FlowElement>，前端只取 id / name
    returnNodes.value = Array.isArray(res?.data) ? res.data : []
  } catch (e) {
    returnNodes.value = []
  } finally {
    returnNodeLoading.value = false
  }
}

/**
 * 加载当前节点绑定的自定义表单
 * 表单定义由流程详情接口返回（WfDetailVo.taskFormData.designerContent）
 */
async function loadTaskForm() {
  // 只有审批/拒绝需要填写表单
  if (!['complete', 'reject'].includes(action.value)) return
  const procInsId = task.value?.procInsId
  if (!procInsId) return
  try {
    const res = await getProcessDetail(procInsId, task.value?.taskId)
    const tf = res?.data?.taskFormData
    if (tf?.designerContent) {
      formJson.value = JSON.parse(tf.designerContent)
    }
    if (tf?.formContent) {
      formData.value = JSON.parse(tf.formContent)
    }
  } catch (e) {
    // 该节点未绑定表单 / 内容异常：静默降级为普通审批
    formJson.value = null
    formData.value = {}
  }
}

async function submit() {
  await formRef.value.validate()

  const params = {
    taskId: task.value?.taskId,
    procInsId: task.value?.procInsId,
    comment: form.comment
  }
  if (form.variables) {
    try {
      params.variables = JSON.parse(form.variables)
    } catch (e) {
      proxy.$modal.msgError('变量 JSON 格式错误')
      return
    }
  }

  // 表单填写数据：随任务提交一并保存到 wf_task_form
  if (formJson.value && formRenderRef.value) {
    try {
      const fd = await formRenderRef.value.getFormData()
      params.formData = JSON.stringify(fd)
    } catch (e) {
      proxy.$modal.msgError('请先完善表单中的必填项')
      return
    }
  }

  submitting.value = true
  let res
  try {
    switch (action.value) {
      case 'complete':
        res = await completeTask({ ...params })
        break
      case 'reject':
        res = await rejectTask({ ...params })
        break
      case 'transfer':
        // 接收人走 assignee：WfTransferTaskService 用 bo.getAssignee() 取接收人并 setAssignee。
        // currentUserId 是操作人，由 api 的 withUser() 自动注入，这里绝不能覆盖。
        res = await transferTask({ ...params, assignee: form.userId })
        break
      case 'delegate':
        // 同上：WfDelegateTaskService 用 bo.getAssignee() 执行 delegateTask
        res = await delegateTask({ ...params, assignee: form.userId })
        break
      case 'addSign':
        // Controller 读取 bo.getUserList()（逗号分隔的用户 id 串）
        res = await beforeAddSignTask({ ...params, userList: form.userId })
        break
      case 'return':
        res = await returnTask({ ...params, targetKey: form.targetActivityId })
        break
      case 'stop':
        res = await stopProcess({ ...params })
        break
      case 'mulitiSequentialUnComplete':
        // 顺序会签回退到本节点上一位处理人（同节点流转）
        res = await mulitiSequentialUnComplete({ ...params })
        break
    }
  } finally {
    submitting.value = false
  }

  if (res && res.code === 200) {
    proxy.$modal.msgSuccess('操作成功')
    open.value = false
    emit('success')
  }
}
</script>

<style scoped>
.task-form-area {
  /* 表单现在在审批意见下方，用上边距分隔 */
  margin-top: 4px;
}
.task-form-area__title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-left: 8px;
  border-left: 3px solid #409eff;
}
/* 人员选择器：标签 + 选择按钮 在一行内自动换行 */
.picker-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
}
.picker-empty {
  font-size: 13px;
  color: #909399;
}
</style>
