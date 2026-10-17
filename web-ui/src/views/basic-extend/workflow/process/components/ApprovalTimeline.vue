<template>
  <div class="approval-timeline" v-loading="loading">
    <el-timeline v-if="list.length">
      <el-timeline-item
        v-for="item in list"
        :key="item.id"
        :type="getType(item)"
        :timestamp="item.time || item.endTime"
        placement="top"
      >
        <h4>{{ item.taskName || item.activityName }}</h4>
        <p>处理人：{{ item.assigneeName || item.userName || '-' }}</p>
        <p>意见：{{ item.comment || item.message || '-' }}</p>
        <el-tag v-if="item.type" :type="getTagType(item.type)" size="small">{{ getTypeName(item.type) }}</el-tag>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-else description="暂无审批记录" />
  </div>
</template>

<script setup>
import { getProcessCommentInfo, getHistoryProcNodeList } from '@/api/workflow/process'
import { ref, onMounted, watch } from 'vue'

const props = defineProps({
  procInsId: { type: String, required: true }
})

const loading = ref(false)
const list = ref([])

async function load() {
  if (!props.procInsId) return
  loading.value = true
  try {
    const res = await getProcessCommentInfo(props.procInsId)
    if (res.code === 200 && res.data) {
      list.value = res.data
    } else {
      // fallback: 查历史节点
      const hRes = await getHistoryProcNodeList(props.procInsId)
      if (hRes.code === 200 && hRes.data) list.value = hRes.data
    }
  } finally {
    loading.value = false
  }
}

/**
 * 意见类型码 → 中文
 * 对应后端 FlowComment（数字码）：1 正常 / 2 退回 / 3 驳回 / 4 委派 / 5 转办 /
 * 6 终止 / 7 撤回 / 8 前加签 / 9 后加签 / 10 加签审批 / 11 提交 / 12 跳转 / 14 会签回退。
 * 13 / 100 是后端 WfProcessCommentService 合成的伪类型：结束节点 / 待办节点。
 * 末尾几个英文键是兼容历史数据（早期版本可能写过英文 action）。
 */
const TYPE_TEXT = {
  1: '通过',
  2: '退回',
  3: '驳回',
  4: '委派',
  5: '转办',
  6: '终止',
  7: '撤回',
  8: '前加签',
  9: '后加签',
  10: '加签审批',
  11: '提交',
  12: '跳转',
  13: '结束',
  14: '会签回退',
  100: '待办',
  // 兼容历史英文值
  complete: '同意',
  reject: '驳回',
  stop: '终止',
  abolish: '废除',
  transfer: '转办',
  delegate: '委派'
}

/** 类型码 → 标签颜色 */
const TYPE_TAG = {
  1: 'success',
  2: 'warning',
  3: 'danger',
  6: 'warning',
  7: 'warning',
  11: 'success',
  13: 'info',
  14: 'warning',
  100: 'primary'
}

function getType(item) {
  const t = String(item.type ?? '')
  if (t === '3' || t === 'reject') return 'danger'
  if (t === '2' || t === '6' || t === '7' || t === 'stop') return 'warning'
  if (item.endTime) return 'success'
  return 'primary'
}
function getTagType(type) {
  return TYPE_TAG[String(type)] || 'info'
}
/** 把类型码转成中文；解析不到时兜底显示原值（不再只见裸数字） */
function getTypeName(type) {
  const key = String(type)
  return TYPE_TEXT[key] || key
}

onMounted(load)
watch(() => props.procInsId, load)
</script>
