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

    <el-table v-loading="loading" :data="dataList">
      <el-table-column prop="procDefName" label="流程名称" min-width="180" show-overflow-tooltip></el-table-column>
      <el-table-column prop="createTime" label="发起时间" width="180"></el-table-column>
      <el-table-column prop="finishTime" label="结束时间" width="180"></el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.processStatus === 'completed' ? 'success' : 'warning'">
            {{ scope.row.processStatus === 'completed' ? '已结束' : '进行中' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)">详情</el-button>
          <el-button link type="danger" v-if="scope.row.processStatus === 'running'" icon="RefreshLeft" @click="handleRevoke(scope.row)">撤回</el-button>
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
import { ownList } from '@/api/workflow/process'
import { revokeProcess } from '@/api/workflow/task'
import { useRouter } from 'vue-router'
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()
const router = useRouter()

const dataList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)

const data = reactive({ queryParams: { pageNum: 1, pageSize: 10, processName: undefined } })
const { queryParams } = toRefs(data)

async function getList() {
  loading.value = true
  try {
    const res = await ownList(queryParams.value)
    if (res.code === 200) { dataList.value = res.rows; total.value = res.total }
  } finally { loading.value = false }
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleDetail(row) { router.push({ path: '/workflow/process/detail', query: { procInsId: row.procInsId } }) }
async function handleRevoke(row) {
  await proxy.$modal.confirm('确认撤回该流程？')
  const res = await revokeProcess({ procInsId: row.procInsId })
  if (res.code === 200) { proxy.$modal.msgSuccess('撤回成功'); getList() }
}

getList()
</script>
