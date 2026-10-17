<template>
  <div class="form-design-page">
    <!--
      页面级工具栏。
      注意：vform3 的设计器**不支持**自定义工具栏插槽（它没有 custom-toolbar / customToolbar 插槽，
      customToolbar 只是其富文本控件内部的 data 属性），因此保存/返回按钮只能放在组件外层。
    -->
    <div class="page-toolbar">
      <div class="page-toolbar__left">
        <el-icon class="page-toolbar__icon"><Document /></el-icon>
        <span class="form-title">{{ formName || '表单设计器' }}</span>
      </div>
      <div class="page-toolbar__right">
        <el-button type="primary" icon="Check" :loading="saving" @click="handleSave">保存表单</el-button>
        <el-button icon="Back" @click="goBack">返 回</el-button>
      </div>
    </div>

    <div class="designer-wrap">
      <v-form-designer
        ref="vfDesignerRef"
        :designer-config="designerConfig"
        :global-dsv="globalDsv"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Document } from '@element-plus/icons-vue'
import { getForm, saveForm } from '@/api/workflow/form'

const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()

const vfDesignerRef = ref(null)
const saving = ref(false)
const formName = ref('')
// 完整的表单记录（保存时回写 name/code/type 等，避免只存内容而丢失元信息）
const formRecord = ref(null)

/**
 * 设计器配置
 * 只使用 vform3 真实支持的选项（其 designerConfig 默认为：
 *   languageMenu / externalLink / formTemplates / eventCollapse / widgetNameReadonly /
 *   clearDesignerButton / previewFormButton / importJsonButton / exportJsonButton /
 *   exportCodeButton / generateSFCButton / toolbarMaxWidth / toolbarMinWidth /
 *   presetCssCode / resetFormJson）
 * 关掉本系统不需要的（语言切换、外链、内置模板、代码导出），保留 JSON 导入导出便于排查。
 */
const designerConfig = reactive({
  languageMenu: false,        // 语言切换：本系统固定中文
  externalLink: false,        // 文档/GitHub 外链
  formTemplates: false,       // 内置模板：本系统有独立的「表单模板」管理
  exportCodeButton: false,    // 导出 Vue 代码
  generateSFCButton: false,   // 生成 SFC
  clearDesignerButton: true,  // 清空设计器
  previewFormButton: true,    // 预览表单
  importJsonButton: true,     // 导入 JSON
  exportJsonButton: true,     // 导出 JSON
  eventCollapse: true,
  widgetNameReadonly: false,
  toolbarMaxWidth: 450,
  toolbarMinWidth: 300,
  resetFormJson: false
})

/** 全局数据源：供表单控件（如下拉框）引用字典；目前留空 */
const globalDsv = reactive({ optionItems: [] })

async function loadForm() {
  const formId = route.query.formId
  if (!formId) return
  const res = await getForm(formId)
  if (res.code === 200 && res.data) {
    formRecord.value = res.data
    formName.value = res.data.formName || ''
    // 表单内容为空时不加载，保留设计器默认空白表单
    if (res.data.content) {
      try {
        vfDesignerRef.value?.setFormJson(JSON.parse(res.data.content))
      } catch (e) {
        proxy.$modal.msgError('表单内容不是合法的 JSON，无法加载到设计器')
      }
    }
  }
}

async function handleSave() {
  if (!vfDesignerRef.value) return
  // getFormJson() 返回 { widgetList, formConfig }
  const formJson = vfDesignerRef.value.getFormJson()
  if (!formRecord.value) {
    proxy.$modal.msgError('缺少表单信息，请从表单管理页进入')
    return
  }
  saving.value = true
  try {
    const res = await saveForm({
      ...formRecord.value,
      content: JSON.stringify(formJson)
    })
    if (res.code === 200) {
      // 提醒：流程走的是「部署时快照」（wf_deploy_form.content），
      // 表单内容改动不会自动影响已部署的流程，必须重新部署才生效。
      proxy.$modal
        .confirm(
          '表单已保存。注意：流程审批时读取的是「部署时的表单快照」，本次修改需要重新部署流程后，才会对新的流程实例生效。',
          '保存成功',
          { confirmButtonText: '去流程模型', cancelButtonText: '留在本页', type: 'success' }
        )
        .then(() => {
          router.push({ path: '/workflow/model' })
        })
        .catch(() => {})
    } else {
      proxy.$modal.msgError(res.msg || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

function goBack() {
  router.push({ path: '/workflow/form' })
}

onMounted(() => {
  loadForm()
})
</script>

<style scoped>
/*
 * 高度说明：
 * vform3 的设计器内部按 `window.innerHeight - 56 - 36` 自行计算滚动区高度，
 * 因此容器**不能**用 overflow:hidden 限死（会把设计器裁掉一块），这里允许滚动。
 */
.form-design-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 84px);
}
.page-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
  height: 48px;
  padding: 0 16px;
  background: #ffffff;
  border-bottom: 1px solid #e4e7ed;
}
.page-toolbar__left {
  display: flex;
  align-items: center;
  gap: 6px;
}
.page-toolbar__icon {
  color: #409eff;
}
.form-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.designer-wrap {
  flex: 1;
  min-height: 0;
  overflow: auto;
  width: 100%;
}

/*
 * 消除设计器整体右移（左侧出现空白）
 * vform3 的根元素是 el-container.main-container，这里强制它从容器左上角开始占满，
 * 排除任何来自祖先/自身的 margin、padding 或居中造成的偏移。
 */
:deep(.main-container) {
  width: 100% !important;
  margin: 0 !important;
  padding: 0 !important;
  justify-content: flex-start !important;
  align-items: stretch !important;
}

/*
 * vform3 的设计器工具栏由 left-toolbar（语言/外链/模板）与 right-toolbar 组成，
 * 而 right-toolbar-con 被写死 width:600px。本系统关掉了左侧那三个按钮（固定中文、
 * 无外链需求、用自有表单模板），于是工具栏左侧会空出一大片。
 * 这里把右侧按钮组改为自适应宽度，让按钮紧凑排布、消除空白。
 * （vform3 的样式是其组件 scoped 样式，特异性更高，故需 !important）
 */
:deep(.right-toolbar) {
  float: none;
  justify-content: flex-end;
}
:deep(.right-toolbar-con) {
  width: auto !important;
}
:deep(.toolbar-container) {
  display: flex;
  justify-content: flex-end;
}

/*
 * 高度修正（左侧组件面板空白的根因）
 *
 * vform3 的 FieldPanel 在 mounted 里把滚动区高度写死为
 *   scrollerHeight = window.innerHeight - 56 + 'px'
 * 也就是「假设设计器独占整个视口」。但在 RuoYi 布局中，可用高度还要减掉
 * Navbar、TagsView 和本页工具栏，实际容器比这个值小得多，
 * 于是 el-aside 被 flex 压缩，而它又带着 overflow-y:hidden，内容就被整块裁掉了。
 * 这里让 vform3 的各级容器改为跟随父容器高度（内联样式需 !important 覆盖）。
 */
:deep(.main-container) {
  height: 100% !important;
}
:deep(.el-aside.side-panel) {
  height: 100%;
}
:deep(.side-scroll-bar) {
  height: 100% !important;
}
:deep(.el-main) {
  height: 100%;
  overflow: auto;
}
</style>
