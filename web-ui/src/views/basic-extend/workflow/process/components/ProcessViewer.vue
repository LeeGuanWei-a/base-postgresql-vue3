<template>
  <div class="process-viewer" v-loading="loading">
    <div ref="viewerRef" class="viewer-container" v-show="hasXml"></div>
    <el-empty v-if="!loading && !hasXml" description="暂无流程图" :image-size="80" />
  </div>
</template>

<script setup>
import NavigatedViewer from 'bpmn-js/lib/NavigatedViewer'
import { getProcessDetail } from '@/api/workflow/process'
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'

/**
 * 流程实例流程图（只读）
 *
 * 用 bpmn-js 的 NavigatedViewer 真正渲染 BPMN 图，并按后端返回的高亮集合着色：
 *   /workflow/process/detail 一次就同时返回 bpmnXml 和 flowViewer，
 *   所以这里只需要一个请求，不用再单独取 XML。
 *
 * flowViewer（WfViewerVo）四个集合都是「节点/连线 id」：
 *   finishedTaskSet          已办节点
 *   unfinishedTaskSet        未办节点（即当前待处理，重点高亮）
 *   rejectedTaskSet          驳回节点
 *   finishedSequenceFlowSet  已走过的连线
 */
const props = defineProps({
  procInsId: { type: String, required: true }
})

const loading = ref(false)
const hasXml = ref(false)
const viewerRef = ref()
let viewer = null

async function renderFlow() {
  if (!props.procInsId) return
  loading.value = true
  hasXml.value = false
  destroyViewer()
  try {
    const res = await getProcessDetail(props.procInsId)
    const xml = res && res.data ? res.data.bpmnXml : null
    const flow = (res && res.data && res.data.flowViewer) || {}
    if (res.code !== 200 || !xml) {
      return
    }
    hasXml.value = true
    await nextTick()
    viewer = new NavigatedViewer({ container: viewerRef.value })
    await viewer.importXML(xml)
    const canvas = viewer.get('canvas')
    try {
      canvas.zoom('fit-viewport')
    } catch (e) {
      // 图形信息不全时 fit-viewport 可能失败，不影响渲染
    }
    markAll(canvas, flow.finishedTaskSet, 'highlight-finished')
    markAll(canvas, flow.rejectedTaskSet, 'highlight-rejected')
    markAll(canvas, flow.unfinishedTaskSet, 'highlight-current')
    markAll(canvas, flow.finishedSequenceFlowSet, 'highlight-flow')
  } catch (e) {
    console.error('[ProcessViewer] 渲染流程图失败：', e)
    hasXml.value = false
    destroyViewer()
  } finally {
    loading.value = false
  }
}

/** 批量打标记；节点可能已不存在（历史数据），单个失败不影响整体 */
function markAll(canvas, ids, className) {
  if (!ids) return
  const list = Array.isArray(ids) ? ids : Object.keys(ids)
  list.forEach((id) => {
    try {
      canvas.addMarker(id, className)
    } catch (e) {
      // 忽略：该 id 在当前流程定义里找不到对应图形
    }
  })
}

function destroyViewer() {
  if (viewer) {
    try {
      viewer.destroy()
    } catch (e) {
      // 忽略销毁异常
    }
    viewer = null
  }
}

onMounted(renderFlow)
watch(() => props.procInsId, renderFlow)
onBeforeUnmount(destroyViewer)
</script>

<!--
  bpmn-js 的 marker class 加在 SVG 的 <g> 上，Vue 的 scoped 样式匹配不到，
  所以高亮样式必须写成全局样式。
-->
<style>
.process-viewer .highlight-current .djs-visual > :nth-child(1) {
  fill: rgba(230, 162, 60, 0.35) !important;
  stroke: #e6a23c !important;
}
.process-viewer .highlight-finished .djs-visual > :nth-child(1) {
  fill: rgba(103, 194, 58, 0.35) !important;
  stroke: #67c23a !important;
}
.process-viewer .highlight-rejected .djs-visual > :nth-child(1) {
  fill: rgba(245, 108, 108, 0.35) !important;
  stroke: #f56c6c !important;
}
.process-viewer .highlight-flow .djs-visual > :nth-child(1) {
  stroke: #67c23a !important;
  stroke-width: 3px !important;
}
</style>

<style scoped>
.process-viewer {
  min-height: 300px;
}
.viewer-container {
  width: 100%;
  height: 420px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background: #fff;
}
</style>
