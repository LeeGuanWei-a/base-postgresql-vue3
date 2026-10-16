<!--流程模型管理-->
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模型名称" prop="modelName">
        <el-input
          v-model="queryParams.modelName"
          placeholder="请输入模型名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模型标识" prop="modelKey">
        <el-input
          v-model="queryParams.modelKey"
          placeholder="请输入模型标识"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流程分类" prop="category">
        <el-select
          v-model="queryParams.category"
          placeholder="请选择流程分类"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="item in categoryOptions"
            :key="item.categoryId"
            :label="item.categoryName"
            :value="item.code"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['workflow:model:add']"
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['workflow:model:remove']"
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="modelList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="模型标识" align="center" prop="modelKey" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="模型名称" align="center" :show-overflow-tooltip="true" min-width="200">
        <template #default="scope">
          <el-button link type="primary" @click="handleViewProcess(scope.row)">
            <span>{{ scope.row.modelName }}</span>
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="流程分类" align="center" prop="category" :formatter="categoryFormat" min-width="120" />
      <el-table-column align="center" label="状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.deploy === true && scope.row.suspended === true" type="info">已挂起</el-tag>
          <el-tag v-else-if="scope.row.deploy === true" type="success">已部署</el-tag>
          <el-tag v-else type="info">未部署</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" align="center" width="170" />
      <el-table-column prop="updateTime" label="更新时间" align="center" width="170" />
      <el-table-column label="操作" align="center" width="200" fixed="right" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            v-hasPermi="['workflow:model:edit']"
            link
            type="primary"
            icon="Edit"
            @click="handleEditRow(scope.row)"
          >设计</el-button>

          <el-button
            v-if="scope.row.pendingRedeploy === true"
            v-hasPermi="['workflow:model:deploy']"
            link
            type="danger"
            icon="Upload"
            @click="handleRedeploy(scope.row)"
          >重新部署</el-button>

          <el-button
            v-if="scope.row.deploy === true && scope.row.suspended !== true"
            v-hasPermi="['workflow:model:suspended']"
            link
            type="warning"
            icon="VideoPause"
            @click="handleSuspended(scope.row)"
          >停用</el-button>

          <el-button
            v-if="scope.row.deploy === true && scope.row.suspended === true"
            v-hasPermi="['workflow:model:activate']"
            link
            type="success"
            icon="VideoPlay"
            @click="handleActivate(scope.row)"
          >激活</el-button>

          <el-button
            v-hasPermi="['workflow:model:remove']"
            link
            type="danger"
            icon="Delete"
            @click="handleDeleteRow(scope.row)"
          >删除</el-button>
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

    <!-- 新增模型弹窗：与列表共用一个表单（process 项目也是这样做的，提交时携带 modelKey / modelName / category / description） -->
    <el-dialog title="新增模型" v-model="addOpen" width="500px" append-to-body :close-on-click-modal="false">
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px">
        <el-form-item label="模型名称" prop="modelName">
          <el-input v-model="addForm.modelName" placeholder="请输入模型名称" />
        </el-form-item>
        <el-form-item label="模型标识" prop="modelKey">
          <el-input v-model="addForm.modelKey" placeholder="请输入模型标识（英文）" />
        </el-form-item>
        <el-form-item label="流程分类" prop="category">
          <el-select v-model="addForm.category" placeholder="请选择流程分类" clearable style="width: 100%">
            <el-option
              v-for="item in categoryOptions"
              :key="item.categoryId"
              :label="item.categoryName"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="description">
          <el-input v-model="addForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addOpen = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitAdd">确 定</el-button>
      </template>
    </el-dialog>

    <!--
      流程图查看：参考 process 项目 model.vue 的「handleReadImage」预览逻辑。
      本项目暂未封装「按 bpmn XML 渲染 SVG」的 viewer，因此先展示 XML 文本，
      后续若引入 bpmn-js 的 Viewer-only 组件可直接换。
    -->
    <el-dialog
      :title="`流程图 - ${processView.name || ''}`"
      v-model="processView.open"
      width="70%"
      append-to-body
      :close-on-click-modal="false"
    >
      <pre v-if="processView.bpmnXml" class="bpmn-xml-pre">{{ processView.bpmnXml }}</pre>
      <el-empty v-else description="该模型未部署或无流程图" />
    </el-dialog>
  </div>
</template>

<script setup>
import {
  listModel,
  addModel,
  delModel,
  suspendedModel,
  activateModel,
  deployModel,
  getModelBpmnXml
} from '@/api/workflow/model'
import { listCategoryAll } from '@/api/workflow/category'
import { useRouter } from 'vue-router'
import { ref, reactive, toRefs, getCurrentInstance, onMounted } from 'vue'

const { proxy } = getCurrentInstance()
const router = useRouter()

const modelList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const submitting = ref(false)
const addOpen = ref(false)
const addFormRef = ref()

// 流程分类下拉
const categoryOptions = ref([])
// modelVo.category 是编码(code)，用此映射在表格中显示分类名
const categoryMap = ref(new Map())

// 流程图查看
const processView = reactive({ open: false, name: '', bpmnXml: '' })

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    modelName: undefined,
    modelKey: undefined,
    category: undefined
  },
  addForm: { modelName: '', modelKey: '', category: '', description: '' },
  addRules: {
    modelName: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
    modelKey: [{ required: true, message: '请输入模型标识', trigger: 'blur' }],
    category: [{ required: true, message: '请选择流程分类', trigger: 'change' }]
  }
})
const { queryParams, addForm, addRules } = toRefs(data)

async function getList() {
  loading.value = true
  try {
    const res = await listModel(queryParams.value)
    if (res.code === 200) {
      modelList.value = res.rows
      total.value = res.total
    }
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  // resetForm 不会清空非 prop 字段，单独把 category 重置回 undefined
  queryParams.value.category = undefined
  handleQuery()
}

function handleSelectionChange(sel) {
  ids.value = sel.map((i) => i.modelId)
  single.value = sel.length !== 1
  multiple.value = !sel.length
}

/** 加载流程分类下拉，并构建 code -> name 映射 */
async function loadCategoryOptions() {
  const res = await listCategoryAll()
  if (res.code === 200) {
    const list = res.data || []
    categoryOptions.value = list
    categoryMap.value = new Map(list.map((c) => [String(c.code), c.categoryName]))
  }
}

/** 表格列：流程分类 formatter（process 项目也是这个写法） */
function categoryFormat(row) {
  if (!row || row.category == null) return '-'
  return categoryMap.value.get(String(row.category)) || row.category
}

function handleAdd() {
  addForm.value = { modelName: '', modelKey: '', category: '', description: '' }
  addOpen.value = true
}

async function submitAdd() {
  await addFormRef.value.validate()
  submitting.value = true
  try {
    const res = await addModel(addForm.value)
    if (res.code === 200) {
      proxy.$modal.msgSuccess('新增成功，即将进入设计器')
      addOpen.value = false
      const newModelId = res.data?.modelId || res.data?.id
      router.push({ path: '/workflow/model/design', query: { id: newModelId } })
    } else {
      proxy.$modal.msgError(res.msg || '新增失败')
    }
  } finally {
    submitting.value = false
  }
}

function handleEdit() {
  handleEditRow({ modelId: ids.value[0] })
}
function handleEditRow(row) {
  router.push({ path: '/workflow/model/design', query: { id: row.modelId } })
}

async function handleSuspended(row) {
  await proxy.$modal.confirm(`确认停用「${row.modelName}」？停用后流程不能再发起，已发起的流程不受影响。`)
  const res = await suspendedModel({ modelId: row.modelId })
  if (res.code === 200) {
    proxy.$modal.msgSuccess('已停用')
    getList()
  } else {
    proxy.$modal.msgError(res.msg || '停用失败')
  }
}

async function handleActivate(row) {
  await proxy.$modal.confirm(`确认激活「${row.modelName}」？`)
  const res = await activateModel({ modelId: row.modelId })
  if (res.code === 200) {
    proxy.$modal.msgSuccess('已激活')
    getList()
  } else {
    proxy.$modal.msgError(res.msg || '激活失败')
  }
}

async function handleRedeploy(row) {
  await proxy.$modal.confirm(
    `确认重新部署「${row.modelName}」？\n将用最新 bpmn XML 覆盖当前部署版本，已发起的流程不受影响。`
  )
  const res = await deployModel({ modelId: row.modelId })
  if (res.code === 200) {
    proxy.$modal.msgSuccess('已重新部署')
    getList()
  } else {
    proxy.$modal.msgError(res.msg || '重新部署失败')
  }
}

async function handleDelete() {
  handleDeleteRow({ modelId: ids.value })
}
async function handleDeleteRow(row) {
  const id = Array.isArray(row.modelId) ? row.modelId.join(',') : row.modelId
  await proxy.$modal.confirm('确认删除选中的流程模型？')
  const res = await delModel(id)
  if (res.code === 200) {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  } else {
    proxy.$modal.msgError(res.msg || '删除失败')
  }
}

/** 点击模型名称：预览流程图（参考 process 项目 handleReadImage） */
async function handleViewProcess(row) {
  if (row.deploy !== true) {
    proxy.$modal.msgWarning('该模型尚未部署，无法预览流程图')
    return
  }
  processView.open = true
  processView.name = row.modelName
  processView.bpmnXml = ''
  try {
    const res = await getModelBpmnXml(row.modelId)
    if (res.code === 200) {
      processView.bpmnXml = res.data || ''
    } else {
      proxy.$modal.msgError(res.msg || '获取流程图失败')
    }
  } catch (e) {
    processView.bpmnXml = ''
  }
}

onMounted(async () => {
  await loadCategoryOptions()
  getList()
})
</script>

<style scoped>
.bpmn-xml-pre {
  max-height: 600px;
  overflow: auto;
  margin: 0;
  padding: 12px;
  background: #fafafa;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
