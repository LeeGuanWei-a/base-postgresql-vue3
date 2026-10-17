<template>
  <el-dialog
    v-model="visible"
    :title="single ? '选择人员' : '选择候选人员'"
    width="820px"
    append-to-body
    :close-on-click-modal="false"
    @closed="onClosed"
  >
    <el-form :model="query" inline>
      <el-form-item label="用户名称">
        <el-input
          v-model="query.userName"
          placeholder="登录账号/昵称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号码">
        <el-input
          v-model="query.phonenumber"
          placeholder="手机号码"
          clearable
          style="width: 180px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="list"
      height="360"
      row-key="userId"
      @selection-change="onSelectionChange"
    >
      <el-table-column v-if="!single" type="selection" width="50" reserve-selection />
      <el-table-column v-else width="50">
        <template #default="scope">
          <el-radio v-model="singleId" :value="scope.row.userId" @change="onSingleChange(scope.row)">
            <span />
          </el-radio>
        </template>
      </el-table-column>
      <el-table-column prop="userId" label="用户编号" width="110" />
      <el-table-column prop="userName" label="登录账号" width="140" show-overflow-tooltip />
      <el-table-column prop="nickName" label="用户昵称" show-overflow-tooltip />
      <el-table-column prop="dept.deptName" label="部门" show-overflow-tooltip />
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="query.pageNum"
      v-model:limit="query.pageSize"
      @pagination="load"
    />

    <template #footer>
      <el-button @click="visible = false">取 消</el-button>
      <el-button type="primary" @click="confirm">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { listUser } from '@/api/system/user'
import { ref, reactive, computed, watch, nextTick } from 'vue'

/**
 * 人员选择器（供流程设计器「用户任务-分配人员」使用）
 *
 * 契约（与 penal/task/task-components/UserTask.vue 对齐）：
 *   props.selectUserData = { dialogVisible, info, type, users, data }
 *     - info === true  单选（分配单个办理人）
 *     - info === false 多选（候选人员）
 *     - data           已选人员，用于回显
 *   emit('updateDialogVisible', { saveBtn, info, data })
 *     - data 元素含 userId，父组件按 item.userId 拼接
 *     - data 元素是「扁平精简对象」（见 toSlimUser），不是整行 SysUser，
 *       这一点必须与 process 项目保持一致，否则会触发 Flowable 的扩展元素文本截断问题
 */
const props = defineProps({
  selectUserData: { type: Object, default: () => ({}) }
})
const emit = defineEmits(['updateDialogVisible'])

const visible = ref(false)
const loading = ref(false)
const list = ref([])
const total = ref(0)
const tableRef = ref(null)
const selected = ref([])
const singleId = ref(null)

const single = computed(() => props.selectUserData?.info === true)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: undefined,
  phonenumber: undefined
})

async function load() {
  loading.value = true
  try {
    const res = await listUser(query)
    list.value = res.rows || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  query.pageNum = 1
  load()
}

function resetQuery() {
  query.userName = undefined
  query.phonenumber = undefined
  handleQuery()
}

function onSelectionChange(rows) {
  selected.value = rows
}

function onSingleChange(row) {
  selected.value = [row]
}

/**
 * 只取流程需要的扁平字段回传给父组件（对齐 process 项目 ProcessUser 的数据契约）。
 *
 * 千万不要直接回传整行 SysUser：它带有 dept（内含 children:[]）、roles:[]、params:{} 等
 * 嵌套空数组，序列化后 JSON 文本中间会出现 ']'。
 * 而 Flowable 6.8.0 的 BpmnXMLUtil#parseExtensionElement 对每个文本片段用的是
 * setElementText() 覆盖而不是追加，JDK 的 StAX 解析器遇到中间的 ']' 会把文本拆成多段，
 * 结果只有最后一段被保留 —— 候选人 JSON 被截断，表现为「选完人保存后重新打开候选人是空的」。
 */
function toSlimUser(item) {
  const slim = {}
  if (item.dept) {
    slim.deptName = item.dept.deptName
  }
  slim.deptId = item.deptId
  slim.nickName = item.nickName
  slim.phonenumber = item.phonenumber
  slim.postName = item.postName
  slim.userId = item.userId
  slim.roleName = item.roleName
  slim.roleId = item.roleId
  return slim
}

function confirm() {
  const rows = single.value
    ? singleId.value != null
      ? selected.value.slice(0, 1)
      : []
    : selected.value
  const data = rows.map(toSlimUser)
  emit('updateDialogVisible', { saveBtn: true, info: single.value, data })
  visible.value = false
}

function onClosed() {
  // 父组件仅在 saveBtn 为 true 时处理，这里用于标识"未保存关闭"
  emit('updateDialogVisible', { saveBtn: false })
}

watch(
  () => props.selectUserData?.dialogVisible,
  async (val) => {
    if (!val) return
    visible.value = true
    query.pageNum = 1
    selected.value = []
    singleId.value = null
    await load()
    const pre = props.selectUserData?.data || []
    await nextTick()
    // 兼容两种回显数据结构：{ userId } 或 { id }（UserTask.vue 传入的是 { id, name }）
    const idOf = (u) => u?.userId ?? u?.id
    if (single.value) {
      singleId.value = idOf(pre[0]) ?? null
      selected.value = pre.slice(0, 1)
    } else {
      pre.forEach((u) => {
        const uid = idOf(u)
        const hit = list.value.find((it) => it.userId === uid)
        if (hit) tableRef.value?.toggleRowSelection(hit, true)
      })
    }
  }
)
</script>
