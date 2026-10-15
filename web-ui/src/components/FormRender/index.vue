<template>
  <div class="form-render-wrapper">
    <v-form-render
      v-if="parsedFormJson"
      ref="vFormRef"
      :form-json="parsedFormJson"
      :form-data="formData"
      :global-dsv="renderDsv"
      :option-data="renderDsv"
      :preview-state="readonly"
    />
    <el-empty v-else :description="emptyText" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'

/**
 * 表单渲染组件（基于 vform3 的 VFormRender）
 *
 * 用途：
 *   1. 审批弹窗中渲染当前节点绑定的表单，供审批人填写
 *   2. 流程详情中只读展示表单已填数据
 *
 * 用法：
 *   <FormRender ref="formRef" :form-json="json" :form-data="data" :readonly="false" />
 *   const data = await formRef.value.getFormData()   // 取填写结果（异步）
 *
 * 注意：vform3 的 getFormData() 返回 Promise；表单校验不通过时会 reject，
 *      因此调用方需要用 try/catch 或 .catch() 处理。
 */
const props = defineProps({
  /** 表单定义：vform3 的 JSON 对象，或后端返回的 JSON 字符串 */
  formJson: { type: [Object, String], default: null },
  /** 已填数据（回显用） */
  formData: { type: Object, default: () => ({}) },
  /** 只读模式（查看场景） */
  readonly: { type: Boolean, default: false },
  /** 全局数据源：供下拉框等控件引用（如字典项） */
  optionData: { type: Object, default: () => ({}) },
  emptyText: { type: String, default: '该节点未绑定表单' }
})

const vFormRef = ref(null)

/** 全局数据源（合并外部传入项，保证 vform 内部始终有该对象） */
const renderDsv = reactive({ optionItems: [], ...props.optionData })

/** 兼容后端返回的字符串形式；无 widgetList 视为未绑定表单 */
const parsedFormJson = computed(() => {
  const raw = props.formJson
  if (!raw) return null
  if (typeof raw === 'object') {
    return raw.widgetList ? raw : null
  }
  try {
    const obj = JSON.parse(raw)
    return obj && obj.widgetList ? obj : null
  } catch (e) {
    return null
  }
})

watch(
  () => props.formData,
  (val) => {
    if (vFormRef.value) vFormRef.value.setFormData(val || {})
  },
  { deep: true }
)

watch(parsedFormJson, (val) => {
  if (val && vFormRef.value) vFormRef.value.setFormJson(val)
})

/** 获取表单填写数据（异步，vform3 校验失败会 reject） */
async function getFormData() {
  if (!vFormRef.value) return {}
  try {
    return await vFormRef.value.getFormData()
  } catch (e) {
    // 校验不通过：向上抛出，让调用方决定是否阻断提交
    throw e
  }
}

function setFormData(data) {
  vFormRef.value?.setFormData(data || {})
}
function setFormJson(json) {
  vFormRef.value?.setFormJson(json)
}
function resetForm() {
  vFormRef.value?.resetForm()
}
/** 切换为只读 */
function disableForm() {
  vFormRef.value?.disableForm()
}
/** 是否已绑定表单 */
function hasForm() {
  return !!parsedFormJson.value
}

defineExpose({ getFormData, setFormData, setFormJson, resetForm, disableForm, hasForm })
</script>

<style scoped>
.form-render-wrapper {
  width: 100%;
}
</style>
