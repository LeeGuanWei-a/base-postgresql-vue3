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
      <el-table-column prop="taskName" label="当前节点" min-width="120"></el-table-column>
      <el-table-column prop="startUserName" label="发起人" width="100"></el-table-column>
      <el-table-column prop="createTime" label="发起时间" width="180"></el-table-column>
      <el-table-column label="操作" align="center" width="400" class-name="small-padding fixed-width" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)">详情</el-button>
          <!--
            审批按钮由该行节点的「授权功能」决定（列表接口返回 permissionJson），
            顺序会签节点会多一个「会签回退」。鼠标悬浮显示各按钮的作用说明。
            注意：v-hasPermi 挂在 el-tooltip 上，不能挂按钮上 ——
            该指令在 mounted 里把元素从 DOM 移除，挂在按钮上会让 tooltip 找不到子元素而失效。
          -->
          <el-tooltip
            v-for="btn in rowButtons(scope.row)"
            :key="btn.action"
            :content="btn.tip"
            placement="top"
            v-hasPermi="btn.permis"
          >
            <el-button
              link
              :type="btn.type"
              :title="btn.tip"
              @click="openAction(scope.row, btn.action)"
            >{{ btn.label }}</el-button>
          </el-tooltip>
          <!-- 终止不属于「授权功能」，按菜单权限单独控制 -->
          <el-tooltip content="终止整个流程（流程结束，历史记录等数据保留）" placement="top">
            <el-button
              link
              type="danger"
              title="终止整个流程（流程结束，历史记录等数据保留）"
              @click="handleStop(scope.row)"
              v-hasPermi="['workflow:process:cancel']"
            >终止</el-button>
          </el-tooltip>
          <el-dropdown>
            <el-button link type="primary" icon="More"></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleAddSign(scope.row)">加签</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
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

    <TaskDialog ref="taskDialogRef" @success="getList" />
  </div>
</template>

<script setup>
import { todoList } from '@/api/workflow/process'
import { useRouter } from 'vue-router'
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'
import TaskDialog from '../process/components/TaskDialog.vue'
import { buildActionButtons } from '../process/components/actionMeta'

const { proxy } = getCurrentInstance()
const router = useRouter()

const dataList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const total = ref(0)
const taskDialogRef = ref()

const data = reactive({
  queryParams: { pageNum: 1, pageSize: 10, processName: undefined }
})
const { queryParams } = toRefs(data)

async function getList() {
  loading.value = true
  try {
    const res = await todoList(queryParams.value)
    if (res.code === 200) {
      dataList.value = res.rows
      total.value = res.total
    }
  } finally {
    loading.value = false
  }
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleSelectionChange(sel) { ids.value = sel.map(i => i.procInsId) }
function handleDetail(row) { router.push({ path: '/workflow/process/detail', query: { procInsId: row.procInsId, taskId: row.taskId } }) }

/**
 * 该行节点的审批按钮：由节点「授权功能」+ 是否顺序会签决定，
 * 数据来自列表接口（WfTaskVo.permissionJson / sequentialMultiInstance）。
 */
function rowButtons(row) {
  return buildActionButtons(row.permissionJson, row.sequentialMultiInstance)
}
/** 打开审批弹窗，action 取自按钮定义 */
function openAction(row, action) {
  taskDialogRef.value.open({ action, task: row })
}
// 加签 / 终止 不属于「授权功能」的 6 个值，仍按各自的菜单权限单独控制
function handleAddSign(row) { taskDialogRef.value.open({ action: 'addSign', task: row }) }
function handleStop(row) { taskDialogRef.value.open({ action: 'stop', task: row }) }

getList()
</script>
