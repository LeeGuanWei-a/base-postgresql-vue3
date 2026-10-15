<template>
  <el-dialog
    v-model="visible"
    title="选择角色"
    width="820px"
    append-to-body
    :close-on-click-modal="false"
    @closed="onClosed"
  >
    <el-form :model="query" inline>
      <el-form-item label="角色名称">
        <el-input
          v-model="query.roleName"
          placeholder="请输入角色名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="权限字符">
        <el-input
          v-model="query.roleKey"
          placeholder="请输入权限字符"
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
      row-key="roleId"
      @selection-change="onSelectionChange"
    >
      <el-table-column type="selection" width="50" reserve-selection />
      <el-table-column prop="roleId" label="角色编号" width="110" />
      <el-table-column prop="roleName" label="角色名称" show-overflow-tooltip />
      <el-table-column prop="roleKey" label="权限字符" show-overflow-tooltip />
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
import { listRole } from '@/api/system/role'
import { ref, reactive, watch, nextTick } from 'vue'

/**
 * 角色选择器（供流程设计器「用户任务-候选角色」使用）
 *
 * 契约（与 penal/task/task-components/UserTask.vue 对齐）：
 *   props.selectRoleData = { dialogVisible, type, users, data }
 *   emit('updateDialogVisible', { saveBtn, data })
 *     - data 元素含 roleId，父组件按 item.roleId 拼接
 */
const props = defineProps({
  selectRoleData: { type: Object, default: () => ({}) }
})
const emit = defineEmits(['updateDialogVisible'])

const visible = ref(false)
const loading = ref(false)
const list = ref([])
const total = ref(0)
const tableRef = ref(null)
const selected = ref([])

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: undefined,
  roleKey: undefined
})

async function load() {
  loading.value = true
  try {
    const res = await listRole(query)
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
  query.roleName = undefined
  query.roleKey = undefined
  handleQuery()
}

function onSelectionChange(rows) {
  selected.value = rows
}

function confirm() {
  emit('updateDialogVisible', { saveBtn: true, data: selected.value })
  visible.value = false
}

function onClosed() {
  emit('updateDialogVisible', { saveBtn: false })
}

watch(
  () => props.selectRoleData?.dialogVisible,
  async (val) => {
    if (!val) return
    visible.value = true
    query.pageNum = 1
    selected.value = []
    await load()
    const pre = props.selectRoleData?.data || []
    await nextTick()
    pre.forEach((r) => {
      const hit = list.value.find((it) => it.roleId === r.roleId)
      if (hit) tableRef.value?.toggleRowSelection(hit, true)
    })
  }
)
</script>
