<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="表单名称" prop="formName">
        <el-input
          v-model="queryParams.formName"
          placeholder="请输入表单名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="表单类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 160px">
          <el-option
            v-for="d in wf_process_form_type"
            :key="d.value"
            :label="d.label"
            :value="d.value"
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="formList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="formName" label="表单名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="code" label="表单编码" min-width="150" show-overflow-tooltip />
      <el-table-column label="表单类型" width="120" align="center">
        <template #default="scope">
          <dict-tag :options="wf_process_form_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="表单分类" width="140" show-overflow-tooltip>
        <template #default="scope">{{ categoryNameOf(scope.row.categoryId) }}</template>
      </el-table-column>
      <el-table-column label="设计状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="hasContent(scope.row) ? 'success' : 'info'" size="small">
            {{ hasContent(scope.row) ? '已设计' : '未设计' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="EditPen" @click="handleDesign(scope.row)">设计表单</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 新增 / 修改：只维护基础信息，表单内容由设计器负责 -->
    <el-dialog :title="dialogTitle" v-model="open" width="620px" append-to-body :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="表单名称" prop="formName">
          <el-input v-model="form.formName" placeholder="请输入表单名称" />
        </el-form-item>
        <el-form-item label="表单编码" prop="code">
          <el-input v-model="form.code" placeholder="唯一标识（英文），如 leaveForm" />
        </el-form-item>
        <el-form-item label="表单类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择表单类型" style="width: 100%">
            <el-option
              v-for="d in wf_process_form_type"
              :key="d.value"
              :label="d.label"
              :value="d.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="表单分类" prop="categoryId">
          <!-- 后端 selectWfFormCategoryList 返回的是树，用树形选择器（与 process 的树形分类选择一致） -->
          <el-tree-select
            v-model="form.categoryId"
            :data="categoryOptions"
            :props="{ label: 'categoryName', children: 'children' }"
            node-key="id"
            value-key="id"
            check-strictly
            clearable
            placeholder="请选择（可不选）"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
        <el-alert
          type="info"
          :closable="false"
          show-icon
          title="表单的字段与布局请在「设计表单」中可视化编辑"
        />
      </el-form>
      <template #footer>
        <el-button @click="open = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">
          {{ form.formId ? '确 定' : '保存并设计' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { listForm, getForm, saveForm, delForm } from '@/api/workflow/form'
import { listFormCategory } from '@/api/workflow/formCategory'
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'

const { proxy } = getCurrentInstance()
const router = useRouter()
// 流程表单类型字典（custom 自定义表单 / system 系统表单 / external 外部表单）
const { wf_process_form_type } = useDict('wf_process_form_type')

const formList = ref([])
const categoryOptions = ref([])
const loading = ref(true)
const submitting = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const open = ref(false)
const dialogTitle = ref('')
const formRef = ref()

const data = reactive({
  queryParams: { pageNum: 1, pageSize: 10, formName: undefined, type: undefined },
  form: {},
  rules: {
    formName: [{ required: true, message: '请输入表单名称', trigger: 'blur' }],
    code: [{ required: true, message: '请输入表单编码', trigger: 'blur' }],
    type: [{ required: true, message: '请选择表单类型', trigger: 'change' }]
  }
})
const { queryParams, form, rules } = toRefs(data)

async function getList() {
  loading.value = true
  try {
    const res = await listForm(queryParams.value)
    if (res.code === 200) {
      formList.value = res.rows
      total.value = res.total
    }
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  const res = await listFormCategory({ pageNum: 1, pageSize: 200 })
  if (res.code === 200) categoryOptions.value = res.rows || []
}

/** 在分类树中按 id 查找名称（后端返回的是树，需递归查找） */
function categoryNameOf(categoryId) {
  if (!categoryId) return '-'
  let hit = null
  const walk = (list) => {
    for (const i of list || []) {
      if (String(i.id) === String(categoryId)) {
        hit = i
        return
      }
      if (i.children?.length) walk(i.children)
      if (hit) return
    }
  }
  walk(categoryOptions.value)
  return hit ? hit.categoryName : categoryId
}

/** 是否已用设计器设计过（content 非空） */
function hasContent(row) {
  const c = row?.content
  if (!c) return false
  const s = String(c).trim()
  return s !== '' && s !== '{}' && s !== 'null'
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
  ids.value = sel.map((i) => i.formId)
  single.value = sel.length !== 1
  multiple.value = !sel.length
}

function reset() {
  form.value = {
    formId: undefined,
    formName: '',
    code: '',
    type: 'custom',
    categoryId: undefined,
    remark: '',
    status: '0'
  }
}

function handleAdd() {
  reset()
  dialogTitle.value = '新增流程表单'
  open.value = true
}

async function handleUpdate(row) {
  reset()
  const formId = row?.formId || ids.value[0]
  const res = await getForm(formId)
  if (res.code === 200) form.value = { ...res.data }
  dialogTitle.value = '修改流程表单'
  open.value = true
}

/** 跳转到表单设计器 */
function handleDesign(row) {
  const formId = row?.formId || ids.value[0]
  router.push({ path: '/workflow/form/design', query: { formId } })
}

async function submitForm() {
  await formRef.value.validate()
  submitting.value = true
  const isAdd = !form.value.formId
  try {
    const res = await saveForm(form.value)
    if (res.code !== 200) {
      proxy.$modal.msgError(res.msg || '保存失败')
      return
    }
    proxy.$modal.msgSuccess(isAdd ? '保存成功，即将进入表单设计' : '修改成功')
    open.value = false
    if (isAdd) {
      // 新增后直接进入设计器（后端 saveForm 返回新建的 formId）
      const newId = res.data?.formId || res.data
      if (newId) {
        router.push({ path: '/workflow/form/design', query: { formId: newId } })
        return
      }
    }
    getList()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  const formIds = row?.formId || ids.value
  await proxy.$modal.confirm('是否确认删除选中的流程表单？')
  const res = await delForm(formIds)
  if (res.code === 200) {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }
}

loadCategories()
getList()
</script>
