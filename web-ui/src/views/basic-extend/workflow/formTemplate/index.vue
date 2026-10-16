<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模板名称" prop="formName">
        <el-input
          v-model="queryParams.formName"
          placeholder="请输入模板名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
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
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column prop="formName" label="模板名称" min-width="220" show-overflow-tooltip />
      <el-table-column label="模板内容" min-width="200">
        <template #default="scope">
          <el-tag :type="hasContent(scope.row) ? 'success' : 'info'" size="small">
            {{ hasContent(scope.row) ? `已配置（${widgetCount(scope.row)} 个控件）` : '未配置' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="formId" label="模板ID" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handlePreview(scope.row)">预览</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 新增 / 修改 -->
    <el-dialog :title="dialogTitle" v-model="open" width="820px" append-to-body :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="模板名称" prop="formName">
          <el-input v-model="form.formName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="14" placeholder="表单定义 JSON" />
          <div class="content-tips">
            <el-button link type="primary" @click="formatContent">格式化 JSON</el-button>
            <span class="tips-text">
              模板内容为 vform3 表单 JSON，可将「表单管理」中设计好的表单内容复制过来
            </span>
          </div>
        </el-form-item>
        <el-form-item label="缩略图" prop="imgUrl">
          <el-input v-model="form.imgUrl" placeholder="缩略图地址（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 预览：用与审批一致的表单渲染器展示模板效果 -->
    <el-dialog :title="`模板预览 - ${previewName}`" v-model="previewOpen" width="820px" append-to-body>
      <FormRender v-if="previewJson" :form-json="previewJson" readonly empty-text="模板内容为空" />
      <el-empty v-else description="模板内容为空或不是合法的表单 JSON" />
    </el-dialog>
  </div>
</template>

<script setup>
import {
  listFormTemplate,
  getFormTemplate,
  saveFormTemplate,
  delFormTemplate
} from '@/api/workflow/formTemplate'
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'
import FormRender from '@/components/FormRender/index.vue'

const { proxy } = getCurrentInstance()

const dataList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const open = ref(false)
const submitting = ref(false)
const dialogTitle = ref('')
const formRef = ref()

// 预览
const previewOpen = ref(false)
const previewJson = ref(null)
const previewName = ref('')

const data = reactive({
  // WfFormTemplate 的字段为 formName / content / imgUrl（不是 templateName/code）
  queryParams: { pageNum: 1, pageSize: 10, formName: undefined },
  form: {},
  rules: {
    formName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }]
  }
})
const { queryParams, form, rules } = toRefs(data)

/** 解析模板内容为 vform3 JSON */
function parseContent(row) {
  if (!row?.content) return null
  try {
    const obj = JSON.parse(row.content)
    return obj && obj.widgetList ? obj : null
  } catch (e) {
    return null
  }
}
function hasContent(row) {
  return !!parseContent(row)
}
function widgetCount(row) {
  return parseContent(row)?.widgetList?.length ?? 0
}

async function getList() {
  loading.value = true
  try {
    const res = await listFormTemplate(queryParams.value)
    if (res.code === 200) {
      dataList.value = res.rows
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
  handleQuery()
}

function reset() {
  form.value = { formId: undefined, formName: '', content: '', imgUrl: '' }
}

function handleAdd() {
  reset()
  dialogTitle.value = '新增表单模板'
  open.value = true
}

async function handleUpdate(row) {
  reset()
  const res = await getFormTemplate(row.formId)
  if (res.code === 200 && res.data) {
    form.value = { ...res.data }
    try {
      form.value.content = JSON.stringify(JSON.parse(form.value.content), null, 2)
    } catch (e) {
      /* 非合法 JSON 保持原文 */
    }
  }
  dialogTitle.value = '修改表单模板'
  open.value = true
}

function handlePreview(row) {
  previewJson.value = parseContent(row)
  previewName.value = row.formName
  previewOpen.value = true
}

function formatContent() {
  try {
    form.value.content = JSON.stringify(JSON.parse(form.value.content), null, 2)
    proxy.$modal.msgSuccess('已格式化')
  } catch (e) {
    proxy.$modal.msgError('模板内容不是合法的 JSON')
  }
}

async function submitForm() {
  await formRef.value.validate()
  submitting.value = true
  try {
    const res = await saveFormTemplate(form.value)
    if (res.code === 200) {
      proxy.$modal.msgSuccess(form.value.formId ? '修改成功' : '新增成功')
      open.value = false
      getList()
    } else {
      proxy.$modal.msgError(res.msg || '保存失败')
    }
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await proxy.$modal.confirm(`是否确认删除模板「${row.formName}」？`)
  const res = await delFormTemplate(row.formId)
  if (res.code === 200) {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }
}

getList()
</script>

<style scoped>
.content-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
}
.tips-text {
  color: #909399;
  font-size: 12px;
}
</style>
