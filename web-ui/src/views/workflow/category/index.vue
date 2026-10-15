<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="分类名称" prop="categoryName">
        <el-input
          v-model="queryParams.categoryName"
          placeholder="请输入分类名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分类编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入分类编码"
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['workflow:category:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['workflow:category:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['workflow:category:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="categoryList"
      row-key="categoryId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="categoryName" label="分类名称" min-width="200"></el-table-column>
      <el-table-column prop="code" label="分类编码" min-width="160"></el-table-column>
      <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" align="center" width="240" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['workflow:category:edit']">修改</el-button>
          <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)" v-hasPermi="['workflow:category:add']">新增</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['workflow:category:remove']">删除</el-button>
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

<script setup name="Category">
import { listCategory, delCategory } from '@/api/workflow/category'
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()

const categoryList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const isExpandAll = ref(false)
const refreshTable = ref(true)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    categoryName: undefined,
    code: undefined
  }
})

const { queryParams } = toRefs(data)

/** 查询分类列表 */
async function getList() {
  loading.value = true
  try {
    const res = await listCategory(queryParams.value)
    if (res.code === 200) {
      categoryList.value = res.rows
      total.value = res.total
    }
  } finally {
    loading.value = false
  }
}

/** 搜索按钮 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮 */
function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

/** 多选 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.categoryId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 新增 */
function handleAdd(row) {
  const parentId = row?.categoryId || 0
  // 直接跳到 /workflow/category，dataForm 通过 query 参数
  proxy.$router.push({
    path: '/workflow/category/dataForm',
    query: { parentId, id: 0 }
  })
}

/** 修改 */
function handleUpdate(row) {
  const id = row.categoryId || ids.value
  proxy.$router.push({
    path: '/workflow/category/dataForm',
    query: { id }
  })
}

/** 删除 */
async function handleDelete(row) {
  const categoryIds = row.categoryId || ids.value
  await proxy.$modal.confirm('是否确认删除分类编号为"' + categoryIds + '"的数据项？')
  try {
    const res = await delCategory(categoryIds)
    if (res.code === 200) {
      proxy.$modal.msgSuccess('删除成功')
      getList()
    }
  } catch {}
}

/** 展开/折叠 */
function toggleExpandAll() {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  setTimeout(() => {
    refreshTable.value = true
  }, 100)
}

getList()
</script>
