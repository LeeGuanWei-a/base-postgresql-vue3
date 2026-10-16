<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="120px">
      <el-form-item label="表达式名称">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column prop="name" label="名称" min-width="160"></el-table-column>
      <el-table-column prop="type" label="类型" width="100"></el-table-column>
      <el-table-column prop="expression" label="表达式" min-width="200" show-overflow-tooltip></el-table-column>
      <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip></el-table-column>
      <el-table-column label="操作" align="center" width="100">
        <template #default="scope">
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup>
import { listExpression, delExpression, exportExpression } from '@/api/workflow/expression'
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()
const dataList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const data = reactive({ queryParams: { pageNum: 1, pageSize: 10, name: undefined } })
const { queryParams } = toRefs(data)

async function getList() {
  loading.value = true
  try {
    const res = await listExpression(queryParams.value)
    if (res.code === 200) { dataList.value = res.rows; total.value = res.total }
  } finally { loading.value = false }
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleExport() { exportExpression(queryParams.value) }
async function handleDelete(row) {
  await proxy.$modal.confirm('确认删除？')
  const res = await delExpression(row.id)
  if (res.code === 200) { proxy.$modal.msgSuccess('删除成功'); getList() }
}
getList()
</script>
