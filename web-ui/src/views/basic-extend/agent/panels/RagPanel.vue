<script setup name="RagPanel">
/**
 * 知识库（RAG）—— 演示知识入库与检索增强问答。
 *
 * 上：GET /agentscope/rag/list 知识列表
 * 中：POST /agentscope/rag/ingest 新增知识
 * 下：CapabilityChatBox 提问，观察检索命中的知识
 */
import { computed, onMounted, ref } from 'vue'
import { ragClear, ragIngest, ragList } from '@/api/agentscope'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref('平台的用户表和角色表分别在哪里？')

const rows = ref([])
const loading = ref(false)
const errorMsg = ref('')
const listMeta = ref({ knowledgeCount: null, extra: [] })

const form = ref({
  title: '',
  content: '',
  tags: ''
})
const submitting = ref(false)
const clearing = ref(false)
const submitMsg = ref('')
const submitError = ref('')

/** 统一成数组（兼容裸数组 / { list } / { records } 等） */
function toArray(data) {
  if (!data) return []
  if (Array.isArray(data)) return data
  const candidates = [data.list, data.records, data.knowledge, data.knowledgeList, data.items, data.rows, data.docs, data.documents]
  for (const c of candidates) {
    if (Array.isArray(c)) return c
  }
  return []
}

const LABEL_MAP = {
  id: 'ID',
  docId: 'ID',
  title: '标题',
  name: '标题',
  content: '内容',
  text: '内容',
  summary: '摘要',
  keywords: '关键字',
  tags: '标签',
  tag: '标签',
  category: '分类',
  source: '来源',
  score: '相关度',
  createdAt: '入库时间',
  createTime: '入库时间',
  updatedAt: '更新时间',
  size: '长度'
}

const listColumns = computed(() => {
  const keys = []
  rows.value.forEach((r) => {
    if (r && typeof r === 'object') {
      Object.keys(r).forEach((k) => {
        if (!keys.includes(k)) keys.push(k)
      })
    }
  })
  const priority = ['id', 'title', 'keywords', 'tags', 'tag', 'summary', 'content', 'text', 'createdAt', 'createTime', 'score', 'source']
  const sorted = [...keys].sort((a, b) => {
    const ia = priority.indexOf(a)
    const ib = priority.indexOf(b)
    if (ia === -1 && ib === -1) return a.localeCompare(b)
    if (ia === -1) return 1
    if (ib === -1) return -1
    return ia - ib
  })
  return sorted.slice(0, 6).map((k) => ({ prop: k, label: LABEL_MAP[k] || k }))
})

function cellText(value) {
  if (value === null || value === undefined || value === '') return '-'
  if (typeof value === 'object') {
    const text = JSON.stringify(value)
    return text.length > 120 ? text.slice(0, 120) + '…' : text
  }
  return String(value)
}

function tagList(value) {
  if (!value) return []
  if (Array.isArray(value)) return value.map((x) => (typeof x === 'object' ? JSON.stringify(x) : String(x)))
  return String(value)
    .split(/[,，]/)
    .map((x) => x.trim())
    .filter(Boolean)
}

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await ragList()
    const data = res.data || {}
    rows.value = toArray(data)
    listMeta.value = {
      knowledgeCount: data.knowledgeCount,
      extra: Object.keys(data).filter((k) => !['knowledge', 'list', 'records', 'items', 'rows'].includes(k))
    }
  } catch (e) {
    errorMsg.value = e?.message || '知识列表加载失败'
    rows.value = []
  } finally {
    loading.value = false
  }
}

async function submit() {
  if (!form.value.title || !form.value.content) {
    submitError.value = '标题与内容都不能为空'
    return
  }
  submitting.value = true
  submitError.value = ''
  submitMsg.value = ''
  try {
    // 后端 RagKnowledgeRequest 字段：title / content / keywords（逗号分隔字符串）
    const res = await ragIngest({
      title: form.value.title,
      content: form.value.content,
      keywords: tagList(form.value.tags).join(',')
    })
    submitMsg.value = '入库成功：' + (res?.msg || res?.data?.title || form.value.title)
    form.value = { title: '', content: '', tags: '' }
    await load()
  } catch (e) {
    submitError.value = e?.message || '入库失败'
  } finally {
    submitting.value = false
  }
}

async function clearAll() {
  clearing.value = true
  submitError.value = ''
  try {
    await ragClear()
    submitMsg.value = '知识库已清空'
    await load()
  } catch (e) {
    submitError.value = e?.message || '清空知识库失败'
  } finally {
    clearing.value = false
  }
}
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="success"
      :closable="false"
      show-icon
      title="RAG 流程：新增知识 → 向量入库 → 提问时检索相关片段 → 把片段拼进上下文交给模型作答。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>知识列表</span>
          <div class="card-header-right">
            <el-tag v-if="listMeta.knowledgeCount != null" size="small" type="info">
              共 {{ listMeta.knowledgeCount }} 条
            </el-tag>
            <el-button link type="primary" :loading="loading" @click="load">刷新</el-button>
            <el-button
              v-if="rows.length"
              link
              type="danger"
              :loading="clearing"
              @click="clearAll"
            >
              清空知识库
            </el-button>
          </div>
        </div>
      </template>

      <el-alert v-if="errorMsg" type="warning" :closable="false" :title="errorMsg" />

      <el-table v-loading="loading" :data="rows" border stripe size="small" max-height="360">
        <el-table-column
          v-for="col in listColumns"
          :key="col.prop"
          :prop="col.prop"
          :label="col.label"
          min-width="140"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <template v-if="col.prop === 'tags' || col.prop === 'tag' || col.prop === 'keywords'">
              <el-tag v-for="t in tagList(row[col.prop])" :key="t" class="tag" size="small" effect="plain">{{ t }}</el-tag>
              <span v-if="!tagList(row[col.prop]).length">-</span>
            </template>
            <span v-else>{{ cellText(row[col.prop]) }}</span>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="知识库为空，先在下方新增一条知识" :image-size="70" />
        </template>
      </el-table>
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>新增知识</span>
      </template>

      <el-form :model="form" label-width="70px" @submit.prevent>
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="例如：平台用户表结构说明" clearable />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="把需要被检索到的知识写在这里（可以是制度、接口说明、字段含义等）"
          />
        </el-form-item>
        <el-form-item label="关键字">
          <el-input v-model="form.tags" placeholder="多个关键字用逗号分隔，例如：用户,数据库,权限" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submit">提交入库</el-button>
          <el-button @click="form = { title: '', content: '', tags: '' }">清空</el-button>
        </el-form-item>
      </el-form>

      <el-alert v-if="submitMsg" type="success" :closable="false" show-icon :title="submitMsg" />
      <el-alert v-if="submitError" type="error" :closable="false" show-icon :title="submitError" />
    </el-card>

    <CapabilityChatBox capability-id="rag" v-model:prompt="prompt" />

    <CapabilityDetailCard capability-id="rag" title="RAG 能力详情（向量库 / 切分策略）" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}
.tag {
  margin: 0 6px 4px 0;
}
.mb8 {
  margin-bottom: 8px;
}
.mt12 {
  margin-top: 12px;
}
</style>
