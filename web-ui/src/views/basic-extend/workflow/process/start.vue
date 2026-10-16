<template>
  <div class="app-container" v-loading="loading">
    <el-card header="启动流程 - 测试流转" v-if="hasContent">
      <el-descriptions :column="2" border class="mb-20">
        <el-descriptions-item label="流程名称">{{ modelName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="流程标识">{{ modelKey || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="选择流程">
          <el-select
            v-model="selectedModelId"
            placeholder="请选择要启动的流程"
            filterable
            clearable
            style="width: 100%"
            @change="onModelChange"
          >
            <el-option
              v-for="item in modelOptions"
              :key="item.modelId"
              :label="`${item.modelName} (${item.modelKey})`"
              :value="item.modelId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="流程变量">
          <el-input
            v-model="form.variables"
            type="textarea"
            :rows="6"
            placeholder='JSON 格式，如 {"amount":100,"days":3}'
          />
          <div style="color: #909399; font-size: 12px; margin-top: 4px">
            这些变量会成为流程实例的「流程变量」，可在 bpmn 表达式（${}）中引用。
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submit">发起</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-empty v-else description="暂无可启动的流程，请先到「流程模型」部署一个模型" />
  </div>
</template>

<script setup>
import { listModelLaunch, publishModel } from '@/api/workflow/model'
import useUserStore from '@/store/modules/user'
import { useRoute, useRouter } from 'vue-router'
import { ref, reactive, computed, getCurrentInstance, onMounted } from 'vue'

const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const formRef = ref()

// 已部署、可启动的模型下拉数据
const modelOptions = ref([])
// 当前选中的 modelId（下拉框 v-model）
const selectedModelId = ref('')
// 选中的模型名/标识（用于描述区展示）
const modelName = ref('')
const modelKey = ref('')

const data = reactive({
  form: { variables: '{}' },
  // 业务标识非必填：业务侧以保存返回的 procInsId 作为关联依据
  rules: {}
})
const { form, rules } = data

// 是否显示卡片：有 URL 参数 或者 有可启动的模型
const hasContent = computed(() => {
  return !!(modelKey.value || modelOptions.value.length > 0)
})

/**
 * 加载可启动的模型列表（仅最新已部署）
 * 后端 /workflow/model/listModelLaunch 返回的字段：modelId/modelName/modelKey
 */
async function loadModelOptions() {
  loading.value = true
  try {
    const res = await listModelLaunch({ pageNum: 1, pageSize: 500 })
    if (res.code === 200) {
      modelOptions.value = res.rows || []
      // 优先级 1：URL 传入 modelId → 选中对应
      const urlModelId = route.query.modelId
      if (urlModelId) {
        const found = modelOptions.value.find(m => m.modelId === urlModelId)
        if (found) {
          selectedModelId.value = found.modelId
          modelName.value = found.modelName
          modelKey.value = found.modelKey
          return
        }
      }
      // 优先级 2：URL 传入 modelKey → 选中对应
      const urlModelKey = route.query.modelKey
      if (urlModelKey) {
        const found = modelOptions.value.find(m => m.modelKey === urlModelKey)
        if (found) {
          selectedModelId.value = found.modelId
          modelName.value = found.modelName
          modelKey.value = found.modelKey
          return
        }
      }
      // 优先级 3：自动选第一个
      if (modelOptions.value.length > 0) {
        const first = modelOptions.value[0]
        selectedModelId.value = first.modelId
        modelName.value = first.modelName
        modelKey.value = first.modelKey
      }
    }
  } finally {
    loading.value = false
  }
}

/** 下拉切换时同步展示信息 */
function onModelChange(modelId) {
  const found = modelOptions.value.find(m => m.modelId === modelId)
  if (found) {
    modelName.value = found.modelName
    modelKey.value = found.modelKey
  } else {
    modelName.value = ''
    modelKey.value = ''
  }
}

async function submit() {
  await formRef.value.validate()

  if (!selectedModelId.value || !modelKey.value) {
    proxy.$modal.msgError('请先选择要启动的流程')
    return
  }

  let variables = {}
  try {
    variables = form.variables ? JSON.parse(form.variables) : {}
  } catch (e) {
    proxy.$modal.msgError('流程变量 JSON 格式错误')
    return
  }

  submitting.value = true
  try {
    const res = await publishModel({
      // 后端会自行拼接 .bpmn 后缀后匹配 ACT_RE_PROCDEF.KEY_
      processDefinitionKey: modelKey.value,
      modelId: selectedModelId.value,
      // 后端用 String.valueOf 场景要求数字字符串
      currentUserCode: String(userStore.id || ''),
      creator: String(userStore.id || ''),
      currentUserName: userStore.nickName || userStore.name || '',
      formData: JSON.stringify(variables),
      // 必传：后端直接对 variables 做 put，null 会 NPE
      variables
    })
    if (res.code === 200 && res.data) {
      // res.data 即流程实例 id，业务侧应保存它作为与流程的关联依据
      proxy.$modal.msgSuccess('发起成功，流程实例ID：' + res.data)
      router.push({ path: '/workflow/process/detail', query: { procInsId: res.data } })
    } else {
      proxy.$modal.msgError(res.msg || '发起失败，请确认流程已部署')
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadModelOptions()
})
</script>

<style scoped>
.mb-20 {
  margin-bottom: 20px;
}
</style>
