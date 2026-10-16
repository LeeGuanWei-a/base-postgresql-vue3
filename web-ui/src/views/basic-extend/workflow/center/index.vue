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
      <el-col :span="1.5">
        <el-button
          type="danger"
          icon="Delete"
          @click="handleOpenClearAll"
          v-hasPermi="['workflow:process:clearAll']"
        >清空全部流程数据</el-button>
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

    <!-- 清空全部流程数据：危险操作，必须手动输入确认短语 -->
    <el-dialog
      v-model="clearDialogVisible"
      title="清空全部流程数据"
      width="720px"
      :close-on-click-modal="false"
      @close="resetClearDialog"
    >
      <el-alert
        type="error"
        :closable="false"
        show-icon
        title="此操作不可恢复"
        description="将强制终止所有运行中流程，并物理删除流程实例（含历史）、流程定义、部署、模型、审批意见、抄送、表单快照、业务拓展信息等全部流程相关数据。生产环境请勿使用。"
        style="margin-bottom: 12px"
      />

      <el-descriptions v-loading="clearLoading" :column="2" border size="small" style="margin-bottom: 12px">
        <el-descriptions-item label="运行中实例">
          <span style="color: #e6a23c; font-weight: 600">{{ clearPreview.runningInstanceCount ?? '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="历史实例">
          <span style="color: #e6a23c; font-weight: 600">{{ clearPreview.historicInstanceCount ?? '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="部署">
          {{ clearPreview.deploymentCount ?? '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="模型">
          {{ clearPreview.modelCount ?? '-' }}
        </el-descriptions-item>
      </el-descriptions>

      <div v-if="businessCountEntries.length" style="margin-bottom: 12px">
        <div style="font-size: 12px; color: #909399; margin-bottom: 4px">业务流程表行数</div>
        <el-tag
          v-for="item in businessCountEntries"
          :key="item.table"
          size="small"
          type="info"
          style="margin: 0 6px 6px 0"
        >{{ item.table }}: {{ item.count }}</el-tag>
      </div>

      <el-alert
        type="info"
        :closable="false"
        :title="`请输入「${clearConfirmText}」以确认`"
        style="margin-bottom: 8px"
      />
      <el-input
        v-model="clearInput"
        :placeholder="`请输入 ${clearConfirmText}`"
        clearable
      />
      <div style="color: #909399; font-size: 12px; margin-top: 6px">
        <b>不会删除</b>：表结构与权限配置（sys_menu / sys_role_menu）、引擎 schema 版本表（act_ge_property）、
        Liquibase 变更日志。<br>
        <b>清空后</b>：流程分类、表单、模型、流程定义都被清掉，需要重新画流程图并部署才能发起流程。
      </div>

      <template #footer>
        <el-button @click="clearDialogVisible = false">取消</el-button>
        <el-button
          type="danger"
          :loading="clearSubmitting"
          :disabled="clearInput !== clearConfirmText"
          @click="doClearAll"
        >确认清空</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { centerProcessList } from '@/api/workflow/process'
import { stopProcess, abolishProcess } from '@/api/workflow/task'
import { previewClear, clearAllProcessData } from '@/api/workflow/clear'
import { useRouter } from 'vue-router'
import { ref, reactive, computed, toRefs, getCurrentInstance } from 'vue'

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

// ============ 清空全部流程数据 ============
const clearDialogVisible = ref(false)
const clearLoading = ref(false)
const clearSubmitting = ref(false)
const clearPreview = ref({})
const clearInput = ref('')
// 与后端 IWfProcessClearService.CONFIRM_TEXT 保持一致，预览接口会回传覆盖
const clearConfirmText = ref('清空流程')

const businessCountEntries = computed(() => {
  const m = clearPreview.value && clearPreview.value.businessCounts
  if (!m) return []
  return Object.entries(m).map(([table, count]) => ({ table, count }))
})

async function handleOpenClearAll() {
  clearInput.value = ''
  clearDialogVisible.value = true
  clearLoading.value = true
  try {
    const res = await previewClear()
    if (res.code === 200) {
      clearPreview.value = res.data || {}
      if (clearPreview.value.confirmText) {
        clearConfirmText.value = clearPreview.value.confirmText
      }
    } else {
      proxy.$modal.msgError(res.msg || '获取清空预览失败')
    }
  } finally {
    clearLoading.value = false
  }
}

function resetClearDialog() {
  clearInput.value = ''
  clearPreview.value = {}
  clearSubmitting.value = false
}

async function doClearAll() {
  if (clearInput.value !== clearConfirmText.value) return
  clearSubmitting.value = true
  try {
    const res = await clearAllProcessData(clearInput.value)
    if (res.code === 200) {
      const d = res.data || {}
      const tables = d.clearedTableRows ? Object.keys(d.clearedTableRows).length : 0
      proxy.$modal.msgSuccess(
        `清空完成：终止实例 ${d.killedRunningInstances ?? 0} / 删除历史 ${d.deletedHistoricInstances ?? 0} / ` +
        `部署 ${d.deletedDeployments ?? 0} / 模型 ${d.deletedModels ?? 0} / 清理表 ${tables} 张 ${d.clearedTotalRows ?? 0} 行`
      )
      clearDialogVisible.value = false
      ids.value = []
      queryParams.value.pageNum = 1
      getList()
    } else {
      proxy.$modal.msgError('清空失败：' + (res.msg || '未知错误'))
    }
  } catch (e) {
    proxy.$modal.msgError('清空失败：' + (e && e.message ? e.message : e))
  } finally {
    clearSubmitting.value = false
  }
}

getList()
</script>
