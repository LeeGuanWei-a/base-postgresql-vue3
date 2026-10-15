<template>
  <div class="app-container">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" v-loading="loading">
      <el-form-item label="上级分类" prop="parent">
        <el-tree-select
          v-model="form.parent"
          :data="categoryOptions"
          value-key="categoryId"
          check-strictly
          placeholder="选择上级分类（不选则为顶级）"
          clearable
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="分类名称" prop="categoryName">
        <el-input v-model="form.categoryName" placeholder="请输入分类名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="分类编码" prop="code">
        <el-input v-model="form.code" placeholder="请输入分类编码" maxlength="50" />
      </el-form-item>
      <el-form-item label="路由地址" prop="url">
        <el-input v-model="form.url" placeholder="请输入路由地址（如：business/form）" maxlength="200" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { getCategory, addCategory, updateCategory, listCategoryAll } from '@/api/workflow/category'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()

const loading = ref(false)
const categoryOptions = ref([])

const data = reactive({
  form: {
    categoryId: undefined,
    parent: null,
    categoryName: undefined,
    code: undefined,
    url: undefined,
    remark: undefined
  },
  rules: {
    categoryName: [{ required: true, message: '分类名称不能为空', trigger: 'blur' }],
    code: [{ required: true, message: '分类编码不能为空', trigger: 'blur' }]
  }
})

const { form, rules } = toRefs(data)

/** 加载分类树 */
async function loadCategoryTree() {
  const res = await listCategoryAll()
  if (res.code === 200) {
    // 把所有 categoryId 和 parent 统一转字符串，避免 el-tree-select 比较类型错位
    // 同时加 label 字段（el-tree-select 默认显示 label）
    const normalized = (res.data || []).map(d => ({
      ...d,
      label: d.categoryName,
      categoryId: String(d.categoryId),
      parent: String(d.parent)
    }))
    const treeData = proxy.handleTree(normalized, 'categoryId', 'parent')
    categoryOptions.value = [
      { categoryId: '0', categoryName: '顶级分类', label: '顶级分类', children: treeData }
    ]
  }
}

/** 取消 */
function cancel() {
  router.back()
}

/** 提交 */
async function submitForm() {
  await proxy.$refs.formRef.validate()
  loading.value = true
  try {
    const isAdd = !form.value.categoryId
    const res = isAdd ? await addCategory(form.value) : await updateCategory(form.value)
    if (res.code === 200) {
      proxy.$modal.msgSuccess(isAdd ? '新增成功' : '修改成功')
      router.back()
    }
  } finally {
    loading.value = false
  }
}

;(async () => {
  // id 从 query 参数读取（默认 0 = 新增）
  const id = route.query.id || 0
  await loadCategoryTree()
  if (id && id !== '0') {
    const res = await getCategory(id)
    if (res.code === 200) {
      Object.assign(form.value, res.data)
    }
  } else {
    // 新增时，如果 URL 带了 parentId 则用 parentId，否则 null（不预选）
    const queryParentId = route.query.parentId
    if (queryParentId !== undefined && queryParentId !== '0' && queryParentId !== '') {
      form.value.parent = String(queryParentId)
    } else {
      form.value.parent = null
    }
  }
})()
</script>
