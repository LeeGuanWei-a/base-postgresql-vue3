<template>
  <el-dialog
    v-model="visible"
    title="变量选择"
    width="820px"
    append-to-body
    :close-on-click-modal="false"
    @closed="onClosed"
  >
    <el-form :model="query" inline>
      <el-form-item label="变量名称">
        <el-input
          v-model="query.name"
          placeholder="请输入变量名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型">
        <el-input
          v-model="query.type"
          placeholder="请输入类型"
          clearable
          style="width: 160px"
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
      row-key="id"
      @selection-change="onSelectionChange"
    >
      <el-table-column type="selection" width="50" reserve-selection />
      <el-table-column prop="name" label="变量名称" show-overflow-tooltip />
      <el-table-column prop="expression" label="表达式" show-overflow-tooltip />
      <el-table-column prop="type" label="类型" width="130" />
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
import { listExpression } from '@/api/workflow/expression'
import { ref, reactive, watch, nextTick } from 'vue'

/**
 * 变量/分配人员选择器（供流程设计器「用户任务-分配人员」使用）
 *
 * 契约（与 penal/task/task-components/UserTask.vue 对齐）：
 *   props.selectStandData = { dialogVisible, authorityList, data, type, users }
 *     - authorityList 已选变量数组，用于回显
 *     - data          父组件 form1.user（含 expression）
 *   emit('updateDialogVisible', { saveBtn, data })
 *     - 父组件读取 data[0].expression
 */
const props = defineProps({
  selectStandData: { type: Object, default: () => ({}) }
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
  name: undefined,
  type: undefined
})

async function load() {
  loading.value = true
  try {
    const res = await listExpression(query)
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
  query.name = undefined
  query.type = undefined
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
  () => props.selectStandData?.dialogVisible,
  async (val) => {
    if (!val) return
    visible.value = true
    query.pageNum = 1
    selected.value = []
    await load()

    // 回显：优先用 authorityList，其次用 data 里的 expression 反查
    const pre = props.selectStandData?.authorityList
    const preExpr = props.selectStandData?.data?.expression
    await nextTick()
    const mark = (row) => tableRef.value?.toggleRowSelection(row, true)
    if (Array.isArray(pre) && pre.length) {
      pre.forEach((item) => {
        const hit = list.value.find((it) => it.id === item.id || it.expression === item.expression)
        if (hit) mark(hit)
      })
    } else if (preExpr) {
      const hit = list.value.find((it) => it.expression === preExpr)
      if (hit) mark(hit)
    }
  }
)
</script>
