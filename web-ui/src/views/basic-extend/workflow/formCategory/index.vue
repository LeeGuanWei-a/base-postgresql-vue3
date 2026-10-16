<template>
  <div class="app-container">
    <el-card shadow="never">
      <!-- 顶部：关键字过滤 + 新增顶级分类（对齐 process 的 formInfoPage 交互） -->
      <div class="category-toolbar">
        <el-input
          v-model="filterText"
          placeholder="输入关键字进行过滤"
          clearable
          style="width: 260px"
        />
        <el-popover placement="bottom" :width="360" trigger="click">
          <template #reference>
            <el-button type="primary" plain icon="Plus">新增顶级分类</el-button>
          </template>
          <div class="category-popover">
            <el-input
              v-model="categoryItem.categoryName"
              placeholder="输入分类名称"
              style="margin-right: 5px"
              @keyup.enter="saveCategory('0')"
            />
            <el-button type="primary" @click="saveCategory('0')">提交</el-button>
          </div>
        </el-popover>
        <el-button icon="Refresh" @click="getList">刷新</el-button>
      </div>

      <!-- 分类树：每个节点内联「新增子类 / 修改 / 删除」 -->
      <el-tree
        ref="treeRef"
        :data="categoryOptions"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
        :filter-node-method="filterNode"
        class="category-tree"
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <span class="node-label">{{ data.categoryName }}</span>
            <span class="node-actions">
              <!-- 新增子分类：parent 取当前节点 id -->
              <el-popover placement="bottom" :width="360" trigger="click" @show="openAdd(data)">
                <div class="category-popover">
                  <el-input
                    v-model="categoryItem.categoryName"
                    placeholder="输入分类名称"
                    style="margin-right: 5px"
                    @keyup.enter="saveCategory(data.id)"
                  />
                  <el-button type="primary" @click="saveCategory(data.id)">提交</el-button>
                </div>
                <template #reference>
                  <el-button link type="primary">新增</el-button>
                </template>
              </el-popover>

              <!-- 修改：回填当前节点，parent 保持原值 -->
              <el-popover placement="bottom" :width="360" trigger="click" @show="openEdit(data)">
                <div class="category-popover">
                  <el-input
                    v-model="categoryItem.categoryName"
                    placeholder="输入分类名称"
                    style="margin-right: 5px"
                    @keyup.enter="saveCategory(data.parent)"
                  />
                  <el-button type="primary" @click="saveCategory(data.parent)">提交</el-button>
                </div>
                <template #reference>
                  <el-button link type="primary">修改</el-button>
                </template>
              </el-popover>

              <el-button link type="danger" @click="handleDelete(data)">删除</el-button>
            </span>
          </span>
        </template>
      </el-tree>

      <el-empty v-if="!loading && !categoryOptions.length" description="暂无分类，可点击「新增顶级分类」创建" />
    </el-card>
  </div>
</template>

<script setup>
import {
  listFormCategory,
  addFormCategory,
  updateFormCategory,
  delFormCategory
} from '@/api/workflow/formCategory'
import { ref, reactive, watch, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()

const treeRef = ref()
const categoryOptions = ref([])
const loading = ref(true)
const filterText = ref('')

// 当前操作的分类项（新增时无 id，修改时有 id —— 与 process 的 saveFormCategory 判定方式一致）
const categoryItem = reactive({ id: undefined, categoryName: '', parent: '0' })

/** 关键字过滤（process 用的是 data.label，本表字段为 categoryName） */
watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

function filterNode(value, data) {
  if (!value) return true
  return (data.categoryName || '').indexOf(value) !== -1
}

async function getList() {
  loading.value = true
  try {
    // 后端 selectWfFormCategoryList 会自行构建树（根 + 递归 children）
    const res = await listFormCategory({})
    if (res.code === 200) categoryOptions.value = res.rows || res.data || []
  } finally {
    loading.value = false
  }
}

/** 打开「新增子分类」弹层 */
function openAdd(data) {
  categoryItem.id = undefined
  categoryItem.categoryName = ''
  categoryItem.parent = data.id
}

/** 打开「修改」弹层：回填当前节点数据 */
function openEdit(data) {
  categoryItem.id = data.id
  categoryItem.categoryName = data.categoryName
  categoryItem.parent = data.parent
}

/** 新增/修改共用（与 process 的 saveFormCategory 一致，靠 id 区分） */
async function saveCategory(parent) {
  if (!categoryItem.categoryName) {
    proxy.$modal.msgWarning('请先输入表单分类名称！')
    return
  }
  const params = { ...categoryItem, parent }
  const isEdit = !!categoryItem.id
  const res = isEdit ? await updateFormCategory(params) : await addFormCategory(params)
  if (res.code === 200) {
    proxy.$modal.msgSuccess(isEdit ? '修改分类成功' : '新增分类成功')
    categoryItem.id = undefined
    categoryItem.categoryName = ''
    getList()
  } else {
    proxy.$modal.msgError(res.msg || '保存失败')
  }
}

async function handleDelete(data) {
  await proxy.$modal.confirm(`是否确认删除分类「${data.categoryName}」？`)
  // 与 process 一致：删除接口传数组（后端为 DELETE /system/category/{ids}）
  const res = await delFormCategory([data.id])
  if (res.code === 200) {
    proxy.$modal.msgSuccess('删除分类成功')
    getList()
  }
}

getList()
</script>

<style scoped>
.category-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}
.category-popover {
  display: flex;
  align-items: center;
}
.category-tree {
  max-width: 720px;
}
.custom-tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding-right: 8px;
}
.node-label {
  font-size: 14px;
  color: #303133;
}
.node-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
