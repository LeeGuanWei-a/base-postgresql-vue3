<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="流程名称">
        <el-input v-model="queryParams.processName" placeholder="请输入流程名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="流程标识">
        <el-input v-model="queryParams.processKey" placeholder="请输入流程标识" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="发起人ID">
        <el-input v-model="queryParams.startUserId" placeholder="请输入发起人用户ID" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="未完结" name="running" />
      <el-tab-pane label="已完结" name="completed" />
      <el-tab-pane label="已终止" name="canceled" />
    </el-tabs>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="VideoPause"
          :disabled="ids.length === 0 || activeTab !== 'running'"
          @click="handleBatchStop"
          v-hasPermi="['workflow:process:cancel']"
        >批量终止</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="CircleClose"
          :disabled="ids.length === 0"
          @click="handleBatchAbolish"
          v-hasPermi="['workflow:process:abolishProcess']"
        >批量废除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="procDefName" label="流程名称" min-width="180" show-overflow-tooltip></el-table-column>
      <el-table-column prop="categoryName" label="流程分类" width="120" show-overflow-tooltip></el-table-column>
      <el-table-column prop="startUserName" label="发起人" width="110"></el-table-column>
      <el-table-column prop="createTime" label="发起时间" width="170"></el-table-column>
      <el-table-column prop="duration" :label="activeTab === 'running' ? '已流转' : '耗时'" width="150"></el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.processStatus)">{{ statusText(scope.row.processStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)">详情</el-button>
          <el-button
            v-if="scope.row.processStatus === 'running'"
            link
            type="warning"
            icon="VideoPause"
            @click="handleStop(scope.row)"
            v-hasPermi="['workflow:process:cancel']"
          >终止</el-button>
          <el-button
            link
            type="danger"
            icon="CircleClose"
            @click="handleAbolish(scope.row)"
            v-hasPermi="['workflow:process:abolishProcess']"
          >废除</el-button>
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
import { centerProcessList } from '@/api/workflow/process'
import { stopProcess, abolishProcess } from '@/api/workflow/task'
import { useRouter } from 'vue-router'
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'

/**
 * 流程中心
 *
 * 三个页签对应三种流程状态（后端 /workflow/process/selectProcessPrcInstance 按 processStatus 过滤）：
 *   未完结 running   → 引擎级 unfinished()
 *   已完结 completed → 引擎级 finished()
 *   已终止 canceled  → finished() + 流程状态变量 = canceled
 *
 * 两个操作的区别（很重要）：
 *   终止 = 只结束流程，历史/审批意见/表单快照/抄送**全部保留**，业务侧仍能查到"已终止"
 *   废除 = 先终止再**物理删除**（Flowable 历史 + wf_comment_info + tbl_flow_extend_procinst
 *          + wf_copy + wf_task_form），不可恢复
 */
const { proxy } = getCurrentInstance()
const router = useRouter()

const dataList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const total = ref(0)
const activeTab = ref('running')

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    processName: undefined,
    processKey: undefined,
    startUserId: undefined
  }
})
const { queryParams } = toRefs(data)

async function getList() {
  loading.value = true
  try {
    const res = await centerProcessList(queryParams.value, activeTab.value)
    if (res.code === 200) {
      dataList.value = res.rows || []
      total.value = res.total || 0
    }
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  queryParams.value.pageNum = 1
  ids.value = []
  getList()
}
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}
function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}
function handleSelectionChange(sel) {
  ids.value = sel.map((i) => i.procInsId)
}
function handleDetail(row) {
  router.push({ path: '/workflow/process/detail', query: { procInsId: row.procInsId } })
}

function statusText(status) {
  if (status === 'running') return '流转中'
  if (status === 'canceled') return '已终止'
  if (status === 'completed') return '已完结'
  return status || '-'
}
function statusTagType(status) {
  if (status === 'running') return 'warning'
  if (status === 'canceled') return 'danger'
  if (status === 'completed') return 'success'
  return 'info'
}

/** 终止：只结束流程，不删任何数据 */
async function doStop(procInsIdList) {
  if (!procInsIdList.length) return
  try {
    await proxy.$modal.confirm(
      `确认终止选中的 ${procInsIdList.length} 个流程？终止后流程会结束，但历史记录、审批意见、表单数据都会保留。`
    )
  } catch (e) {
    return // 用户取消
  }
  const res = await stopProcess({ procInsIdList, comment: '流程中心-终止流程' })
  if (res.code === 200) {
    proxy.$modal.msgSuccess('终止成功')
    getList()
  }
}

/** 废除：终止 + 物理删除，不可恢复 */
async function doAbolish(procInsIdList) {
  if (!procInsIdList.length) return
  try {
    await proxy.$modal.confirm(
      `确认废除选中的 ${procInsIdList.length} 个流程？废除会把流程终止并删除（含历史记录、审批意见、表单数据），不可恢复。`
    )
  } catch (e) {
    return // 用户取消
  }
  const res = await abolishProcess({ procInsIdList, comment: '流程中心-废除流程' })
  if (res.code === 200) {
    proxy.$modal.msgSuccess('废除成功')
    getList()
  }
}

function handleStop(row) {
  doStop([row.procInsId])
}
function handleAbolish(row) {
  doAbolish([row.procInsId])
}
function handleBatchStop() {
  doStop(ids.value.slice())
}
function handleBatchAbolish() {
  doAbolish(ids.value.slice())
}

getList()
</script>
