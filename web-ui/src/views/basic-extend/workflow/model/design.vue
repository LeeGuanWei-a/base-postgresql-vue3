<template>
  <div class="app-container design-container" v-loading="loading">
    <BpmnProcessDesigner
      v-if="modelId"
      :value="bpmnXml"
      :process-id="modelId"
      :additional-model="additionalModel"
      height="100%"
      @save="handleSave"
    />
  </div>
</template>

<style scoped>
.design-container {
  height: calc(100vh - 84px) !important;
  padding: 0 !important;
  margin: 0 !important;
  width: 100% !important;
  overflow: visible !important;
  position: relative;
  z-index: 1;
}
</style>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { getModelBpmnXml, saveModel, deployModel } from '@/api/workflow/model'
import BpmnProcessDesigner from '@/plugins/package/designer'
import CustomPaletteProvider from '@/plugins/package/designer/plugins/palette'
// 引入自定义 ContextPad：把默认的「Append task」从抽象 bpmn:Task 改为 bpmn:UserTask
// （不引入则使用 bpmn-js 内置 context pad，append task 创建的是抽象任务）
import CustomContextPadProvider from '@/plugins/package/designer/plugins/content-pad'

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const loading = ref(false)
const modelId = ref('')
const bpmnXml = ref('')
const additionalModel = [CustomPaletteProvider, CustomContextPadProvider]

onMounted(async () => {
  modelId.value = route.query.id || ''
  if (modelId.value) {
    loading.value = true
    try {
      const res = await getModelBpmnXml(modelId.value)
      if (res.code === 200) {
        bpmnXml.value = res.data || ''
      }
    } finally {
      loading.value = false
    }
  }
})

/**
 * 处理「保存流程」
 * 1. 调 saveModel 把 bpmn XML 写回 ACT_RE_MODEL
 * 2. 保存成功后立即调 deployModel 完成部署（保存即部署）
 * 3. 弹一个「保存并部署成功」提示，关闭设计页并返回 /workflow/model 列表
 */
async function handleSave(xml) {
  const saveRes = await saveModel({ modelId: modelId.value, bpmnXml: xml })
  if (saveRes.code !== 200) {
    // 后端保存前会做「流程合规校验」，不通过时返回多条问题（换行分隔）
    showSaveProblems(saveRes.msg)
    return
  }
  // 保存成功 → 自动部署（用户不再需要手动点「部署」）
  const deployRes = await deployModel({ modelId: modelId.value })
  if (deployRes.code !== 200) {
    // 保存成功但部署失败，仍然提示但允许返回列表
    proxy.$modal.msgError(`保存成功，但自动部署失败：${deployRes.msg || '未知错误'}。请到列表手动部署。`)
    router.push({ path: '/workflow/model' })
    return
  }
  try {
    await proxy.$modal.confirm(
      '保存并部署成功，即将返回流程模型列表。',
      '保存成功'
    )
  } catch (e) {
    // 用户点了取消
  }
  router.push({ path: '/workflow/model' })
}

/**
 * 保存被「流程合规校验」拦下时，把问题清单弹窗完整列出来。
 * 后端把每个问题用换行分隔，第一行是标题。
 */
function showSaveProblems(msg) {
  const text = String(msg || '保存失败')
  if (text.indexOf('\n') === -1) {
    proxy.$modal.msgError(text)
    return
  }
  const lines = text.split('\n').map((s) => s.trim()).filter(Boolean)
  const html =
    '<div style="max-height:420px;overflow:auto;line-height:1.9">' +
    lines.slice(1).map((line) => `<div>${escapeHtml(line)}</div>`).join('') +
    '</div>'
  ElMessageBox.alert(html, lines[0], {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '知道了'
  }).catch(() => {})
}

function escapeHtml(str) {
  return String(str).replace(/[&<>"']/g, (c) => (
    { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]
  ))
}
</script>
