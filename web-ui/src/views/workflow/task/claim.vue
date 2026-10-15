<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="流程名称">
        <el-input v-model="queryParams.processName" placeholder="请输入流程名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="procDefName" label="流程名称" min-width="180" show-overflow-tooltip></el-table-column>
      <el-table-column prop="taskName" label="任务名称" min-width="120"></el-table-column>
      <el-table-column prop="startUserName" label="发起人" width="100"></el-table-column>
      <el-table-column prop="createTime" label="发起时间" width="180"></el-table-column>
      <el-table-column label="操作" align="center" width="240" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Check" @click="handleClaim(scope.row)">签收</el-button>
          <el-button link type="warning" icon="RefreshLeft" @click="handleUnClaim(scope.row)">退签</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup>
import { claimList } from '@/api/workflow/process'
import { claimTask, unClaimTask } from '@/api/workflow/task'
import { useRouter } from 'vue-router'
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()
const router = useRouter()

const dataList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const total = ref(0)

const data = reactive({ queryParams: { pageNum: 1, pageSize: 10, processName: undefined } })
const { queryParams } = toRefs(data)

async function getList() {
  loading.value = true
  try {
    const res = await claimList(queryParams.value)
    if (res.code === 200) { dataList.value = res.rows; total.value = res.total }
  } finally { loading.value = false }
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleSelectionChange(sel) { ids.value = sel.map(i => i.taskId) }
function handleDetail(row) { router.push({ path: '/workflow/process/detail', query: { procInsId: row.procInsId, taskId: row.taskId } }) }
async function handleClaim(row) {
  const res = await claimTask({ taskId: row.taskId })
  if (res.code === 200) { proxy.$modal.msgSuccess('签收成功'); getList() }
}
async function handleUnClaim(row) {
  const res = await unClaimTask({ taskId: row.taskId })
  if (res.code === 200) { proxy.$modal.msgSuccess('退签成功'); getList() }
}

getList()
</script>
