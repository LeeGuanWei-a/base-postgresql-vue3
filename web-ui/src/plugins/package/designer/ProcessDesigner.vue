<template>
  <div class="my-process-designer">
    <div class="my-process-designer__header">
      <slot name="control-header"></slot>
      <template v-if="!$slots['control-header']">
        <el-button-group key="file-control">
          <el-button :size="headerButtonSize" :type="headerButtonType" :icon="Edit" @click="onSave">保存流程</el-button>
          <el-button :size="headerButtonSize" :type="headerButtonType" :icon="FolderOpened" @click="$refs.refFile.click()">打开文件</el-button>
          <el-tooltip effect="light">
            <template #content>
              <el-button :size="headerButtonSize" type="text" @click="downloadProcessAsXml">下载为XML文件</el-button>
              <br />
              <el-button :size="headerButtonSize" type="text" @click="downloadProcessAsSvg">下载为SVG文件</el-button>
              <br />
              <el-button :size="headerButtonSize" type="text" @click="downloadProcessAsBpmn">下载为BPMN文件</el-button>
            </template>
            <el-button :size="headerButtonSize" :type="headerButtonType" :icon="Download">下载文件</el-button>
          </el-tooltip>
          <el-tooltip effect="light">
            <template #content>
              <el-button :size="headerButtonSize" type="text" @click="previewProcessXML">预览XML</el-button>
              <br />
              <el-button :size="headerButtonSize" type="text" @click="previewProcessJson">预览JSON</el-button>
            </template>
            <el-button :size="headerButtonSize" :type="headerButtonType" :icon="View">预览</el-button>
          </el-tooltip>
          <el-tooltip v-if="simulation" effect="light" :content="simulationStatus ? '退出模拟' : '开启模拟'">
            <el-button :size="headerButtonSize" :type="headerButtonType" :icon="Cpu" @click="processSimulation">模拟</el-button>
          </el-tooltip>
        </el-button-group>

        <el-button-group key="align-control">
          <el-tooltip effect="light" content="向左对齐">
            <el-button :size="headerButtonSize" :icon="Operation" @click="elementsAlign('left')" />
          </el-tooltip>
          <el-tooltip effect="light" content="向右对齐">
            <el-button :size="headerButtonSize" :icon="Operation" @click="elementsAlign('right')" />
          </el-tooltip>
          <el-tooltip effect="light" content="向上对齐">
            <el-button :size="headerButtonSize" :icon="Operation" @click="elementsAlign('top')" />
          </el-tooltip>
          <el-tooltip effect="light" content="向下对齐">
            <el-button :size="headerButtonSize" :icon="Operation" @click="elementsAlign('bottom')" />
          </el-tooltip>
          <el-tooltip effect="light" content="水平居中">
            <el-button :size="headerButtonSize" :icon="Operation" @click="elementsAlign('center')" />
          </el-tooltip>
          <el-tooltip effect="light" content="垂直居中">
            <el-button :size="headerButtonSize" :icon="Operation" @click="elementsAlign('middle')" />
          </el-tooltip>
        </el-button-group>

        <el-button-group key="scale-control">
          <el-tooltip effect="light" content="缩小视图">
            <el-button :size="headerButtonSize" :disabled="defaultZoom <= 0.3" :icon="ZoomOut" @click="processZoom(-0.1)" />
          </el-tooltip>
          <el-button :size="headerButtonSize">{{ Math.floor(defaultZoom * 10 * 10) + "%" }}</el-button>
          <el-tooltip effect="light" content="放大视图">
            <el-button :size="headerButtonSize" :disabled="defaultZoom >= 3.9" :icon="ZoomIn" @click="processZoom(0.1)" />
          </el-tooltip>
          <el-tooltip effect="light" content="重置视图并居中">
            <el-button :size="headerButtonSize" :icon="FullScreen" @click="processReZoom" />
          </el-tooltip>
        </el-button-group>

        <el-button-group key="stack-control">
          <el-tooltip effect="light" content="撤销">
            <el-button :size="headerButtonSize" :disabled="!revocable" :icon="RefreshLeft" @click="processUndo" />
          </el-tooltip>
          <el-tooltip effect="light" content="恢复">
            <el-button :size="headerButtonSize" :disabled="!recoverable" :icon="RefreshRight" @click="processRedo" />
          </el-tooltip>
          <el-tooltip effect="light" content="重新绘制">
            <el-button :size="headerButtonSize" :icon="Refresh" @click="processRestart" />
          </el-tooltip>
        </el-button-group>
      </template>
    </div>
    <!--
      process-design 是主题样式的作用域类（见 theme/process-panel.scss）：
      面板的 .process-panel__container、.panel-tab__title、.element-property 等
      样式都嵌套在 .process-design 之下，缺少它会导致面板样式全部失效。
    -->
    <div class="my-process-designer__content process-design">
      <div ref="bpmnCanvas" class="my-process-designer__canvas"></div>
      <div class="my-process-designer__properties">
        <BpmnPropertiesPanel
          v-if="modelerReady"
          :bpmn-modeler="modelerReady"
          :prefix="prefix"
        />
      </div>
    </div>
    <el-dialog v-model="previewModelVisible" :title="previewType === 'xml' ? '预览XML' : '预览JSON'" width="60%" :close-on-click-modal="false" append-to-body destroy-on-close>
      <pre>{{ previewResult }}</pre>
    </el-dialog>
    <input ref="refFile" type="file" style="display: none" accept=".xml,.bpmn" @change="openFile" />
  </div>
</template>

<script setup>
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css'
import { ref, shallowRef, computed, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import BpmnModeler from 'bpmn-js/lib/Modeler'
import { Edit, FolderOpened, Download, View, Cpu, Operation, ZoomIn, ZoomOut, FullScreen, Refresh, RefreshLeft, RefreshRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
// 主题样式以「全局」方式引入（与 process 的 components/ProcessDesigner/index.vue 做法一致）。
// 不能用 <style scoped> 里的 @use：那会把选择器加上本组件的 data-v 属性，
// 导致 .process-design .process-panel__container 这类「跨组件」样式匹配不到子面板元素。
import '../theme/index.scss'
// 业务定制属性面板（常规/消息与信号/流转条件/表单/任务/多实例/边界事件/监听器/扩展属性/其他）
import BpmnPropertiesPanel from '../penal'
import customTranslate from './plugins/translate/customTranslate'
import translationsCN from './plugins/translate/zh'
import camundaModdleDescriptor from './plugins/descriptor/camundaDescriptor.json'
import activitiModdleDescriptor from './plugins/descriptor/activitiDescriptor.json'
import flowableModdleDescriptor from './plugins/descriptor/flowableDescriptor.json'
import camundaModdleExtension from './plugins/extension-moddle/camunda'
import activitiModdleExtension from './plugins/extension-moddle/activiti'
import flowableModdleExtension from './plugins/extension-moddle/flowable'
import DefaultEmptyXML from './plugins/defaultEmpty'
import { XMLParser } from 'fast-xml-parser'

defineOptions({ name: 'BpmnProcessDesigner' })

const props = defineProps({
  value: { type: String, default: '' },
  processId: { type: String, default: '' },
  processName: { type: String, default: '' },
  translations: { type: Object, default: null },
  additionalModel: { type: [Object, Array], default: null },
  moddleExtension: { type: Object, default: null },
  onlyCustomizeAddi: { type: Boolean, default: false },
  onlyCustomizeModdle: { type: Boolean, default: false },
  simulation: { type: Boolean, default: false },
  keyboard: { type: Boolean, default: true },
  prefix: { type: String, default: 'flowable' },
  events: { type: Array, default: () => ['element.click'] },
  headerButtonSize: { type: String, default: 'small' },
  headerButtonType: { type: String, default: 'primary' },
  height: { type: String, default: 'calc(100vh - 200px)' }
})

// events 默认包含 'element.click'，所以必须把 'element.click' 显式声明
// （Vue 3 要求所有 emit 的事件都必须在 defineEmits 里声明，否则会有运行时警告）
const emit = defineEmits(['save', 'init-finished', 'error', 'commandStack-changed', 'element.click'])

const bpmnCanvas = ref(null)
// 传给属性面板的 modeler 实例（用 shallowRef 让面板在实例就绪后才挂载）
const modelerReady = shallowRef(null)
const refFile = ref(null)
let bpmnModeler = null
const defaultZoom = ref(1)
const previewModelVisible = ref(false)
const simulationStatus = ref(false)
const previewResult = ref('')
const previewType = ref('xml')
const recoverable = ref(false)
const revocable = ref(false)

const additionalModules = computed(() => {
  // 属性面板改用业务定制面板（../penal），这里不再注入官方 BpmnPropertiesPanelModule
  const Modules = []
  if (props.additionalModel) {
    if (Array.isArray(props.additionalModel)) {
      Modules.push(...props.additionalModel)
    } else {
      Modules.push(props.additionalModel)
    }
  }
  const TranslateModule = {
    translate: ['value', customTranslate(props.translations || translationsCN)]
  }
  Modules.push(TranslateModule)
  if (props.prefix === 'camunda') {
    Modules.push(camundaModdleExtension)
  }
  if (props.prefix === 'flowable') {
    Modules.push(flowableModdleExtension)
  }
  if (props.prefix === 'activiti') {
    Modules.push(activitiModdleExtension)
  }
  return Modules
})

const moddleExtensions = computed(() => {
  if (props.onlyCustomizeModdle) return props.moddleExtension
  const extensions = {}
  if (props.prefix === 'camunda') extensions.camunda = camundaModdleDescriptor
  if (props.prefix === 'flowable') extensions.flowable = flowableModdleDescriptor
  if (props.prefix === 'activiti') extensions.activiti = activitiModdleDescriptor
  if (props.moddleExtension) {
    Object.assign(extensions, props.moddleExtension)
  }
  return extensions
})

async function initBpmnModeler() {
  if (!bpmnCanvas.value) {
    console.error('[BpmnDesigner] bpmnCanvas is null - div not ready')
    return
  }
  try {
    bpmnModeler = new BpmnModeler({
      container: bpmnCanvas.value,
      additionalModules: additionalModules.value,
      moddleExtensions: moddleExtensions.value,
      keyboard: props.keyboard
    })
    // 实例就绪后挂载业务属性面板（面板内部会设置 window.bpmnInstances）
    modelerReady.value = bpmnModeler
    const eventBus = bpmnModeler.get('eventBus')
    // 命令栈变化
    eventBus.on('commandStack.changed', () => {
      const stack = bpmnModeler.get('commandStack')
      revocable.value = stack.canUndo()
      recoverable.value = stack.canRedo()
      emit('commandStack-changed', { revocable: revocable.value, recoverable: recoverable.value })
    })
    // 元素点击
    props.events.forEach(eventType => {
      eventBus.on(eventType, evt => {
        emit(eventType, evt)
      })
    })
    // 加载 XML
    const xmlToLoad = props.value && props.value.trim() ? props.value : DefaultEmptyXML('Process_1', '新建流程', 'flowable')
    await newXml(xmlToLoad)
    // 强制重置画布尺寸
    setTimeout(() => {
      if (bpmnModeler) {
        const canvas = bpmnModeler.get('canvas')
        canvas.viewbox()
      }
    }, 100)
    emit('init-finished', bpmnModeler)
  } catch (e) {
    console.error('[BpmnDesigner] init failed:', e)
    ElMessage.error('流程设计器初始化失败: ' + (e.message || e))
  }
}

async function newXml(xml) {
  await bpmnModeler.importXML(xml)
  const canvas = bpmnModeler.get('canvas')
  canvas.zoom('fit-viewport', 'auto')
  defaultZoom.value = canvas.zoom()
}

async function onSave() {
  try {
    const { xml } = await bpmnModeler.saveXML({ format: true })
    emit('save', xml)
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.message || e))
    emit('error', e)
  }
}

function downloadProcessAsXml() { download('xml') }
function downloadProcessAsSvg() { download('svg') }
function downloadProcessAsBpmn() { download('bpmn') }
async function download(type) {
  try {
    let dataUrl, filename
    if (type === 'xml' || type === 'bpmn') {
      const { xml } = await bpmnModeler.saveXML({ format: true })
      dataUrl = 'data:text/xml;charset=UTF-8,' + encodeURIComponent(xml)
      filename = (props.processName || 'bpmn-process') + '.bpmn20.xml'
    } else {
      const { svg } = await bpmnModeler.saveSVG()
      dataUrl = 'data:image/svg+xml;charset=UTF-8,' + encodeURIComponent(svg)
      filename = (props.processName || 'bpmn-process') + '.svg'
    }
    const a = document.createElement('a')
    a.href = dataUrl
    a.download = filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
  } catch (e) {
    ElMessage.error('下载失败: ' + (e.message || e))
  }
}

async function previewProcessXML() {
  try {
    const { xml } = await bpmnModeler.saveXML({ format: true })
    previewResult.value = xml
    previewType.value = 'xml'
    previewModelVisible.value = true
  } catch (e) {
    ElMessage.error('预览失败: ' + (e.message || e))
  }
}

async function previewProcessJson() {
  try {
    const { xml } = await bpmnModeler.saveXML({ format: true })
    const parser = new XMLParser({ ignoreAttributes: false, format: true })
    const result = parser.parse(xml)
    previewResult.value = JSON.stringify(result, null, 2)
    previewType.value = 'json'
    previewModelVisible.value = true
  } catch (e) {
    ElMessage.error('预览失败: ' + (e.message || e))
  }
}

function processSimulation() {
  simulationStatus.value = !simulationStatus.value
  try {
    if (simulationStatus.value) {
      bpmnModeler.get('toggleMode').toggleMode()
    } else {
      bpmnModeler.get('toggleMode').toggleMode()
    }
  } catch (e) {
    console.error('模拟失败', e)
  }
}

function elementsAlign(position) {
  const Selection = bpmnModeler.get('selection')
  const selectedElements = Selection.get()
  if (!selectedElements || selectedElements.length < 2) {
    ElMessage.warning('请选择至少两个元素对齐')
    return
  }
  const Align = bpmnModeler.get('alignElements')
  const modeling = bpmnModeler.get('modeling')
  let alignType
  switch (position) {
    case 'left': alignType = 'left'; break
    case 'right': alignType = 'right'; break
    case 'top': alignType = 'top'; break
    case 'bottom': alignType = 'bottom'; break
    case 'center': alignType = 'center'; break
    case 'middle': alignType = 'middle'; break
    default: alignType = position
  }
  try {
    if (Align) {
      Align.trigger(selectedElements, alignType)
    } else {
      ElMessage.warning('对齐功能不可用')
    }
  } catch (e) {
    ElMessage.error('对齐失败: ' + (e.message || e))
  }
}

function processZoom(delta) {
  const canvas = bpmnModeler.get('canvas')
  const z = canvas.zoom()
  const newZoom = Math.max(0.2, Math.min(4, z + delta))
  canvas.zoom(newZoom)
  defaultZoom.value = newZoom
}

function processReZoom() {
  bpmnModeler.get('canvas').zoom('fit-viewport', 'auto')
  defaultZoom.value = bpmnModeler.get('canvas').zoom()
}

function processUndo() {
  bpmnModeler.get('commandStack').undo()
}

function processRedo() {
  bpmnModeler.get('commandStack').redo()
}

async function processRestart() {
  await newXml(DefaultEmptyXML('Process_1', '新建流程', 'flowable'))
}

function openFile(e) {
  const file = e.target.files[0]
  if (!file) return
  const reader = new FileReader()
  reader.onload = async (ev) => {
    try {
      await newXml(ev.target.result)
    } catch (err) {
      ElMessage.error('文件解析失败: ' + (err.message || err))
    }
  }
  reader.readAsText(file)
  e.target.value = ''
}

watch(() => props.value, async (newVal) => {
  if (newVal && bpmnModeler) {
    try {
      await newXml(newVal)
    } catch (e) {
      console.error('更新XML失败', e)
    }
  }
})

onMounted(async () => {
  await nextTick()
  await initBpmnModeler()
})

onBeforeUnmount(() => {
  if (bpmnModeler) {
    bpmnModeler.destroy()
    bpmnModeler = null
  }
})

defineExpose({
  getModeler: () => bpmnModeler,
  saveXML: () => bpmnModeler?.saveXML({ format: true }),
  importXML: newXml
})
</script>

<style lang="scss" scoped>
/*
 * 主题样式（process-designer.scss / process-panel.scss）已在 <script> 顶部以全局方式引入，
 * 此处只保留本组件的补充样式。
 * 注意：不要再在这里 @use 主题文件——scoped 会给选择器附加 data-v 属性，
 * 使 .process-design .process-panel__container 等跨组件样式失效。
 */
.my-process-designer {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  min-height: 600px;
  background: #fafafa;
  &__header {
    padding: 8px;
    border-bottom: 1px solid #e4e7ed;
    background: #ffffff;
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
  }
  &__content {
    flex: 1;
    display: flex;
    min-height: 500px;
    position: relative;
  }
  &__canvas {
    flex: 1;
    min-width: 0;
    background: #ffffff;
    position: relative;
  }
  // 右侧属性面板容器：宽度由面板自身控制（展开 580px / 收起 30px）
  &__properties {
    flex-shrink: 0;
    height: 100%;
    background: #ffffff;
  }
}

:deep(.djs-palette) {
  background: #ffffff !important;
  border: solid 1px #dcdfe6 !important;
  border-radius: 2px;
}
:deep(.djs-container) {
  width: 100% !important;
  height: 100% !important;
}
:deep(.djs-palette-entries) {
  background: #ffffff !important;
}
:deep(.djs-palette .entry) {
  color: #444 !important;
}
:deep(.djs-palette .entry:hover) {
  color: #409eff !important;
  background: #ecf5ff !important;
}
</style>
