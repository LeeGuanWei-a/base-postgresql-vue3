<script setup name="ChannelPanel">
/**
 * 渠道接入（外部依赖型）—— 钉钉 / 飞书 / 企微等消息渠道的接入状态。
 *
 * 演示要点：
 * 1. GET /agentscope/channel/status：已注册渠道、Webhook 地址、收发消息数；
 * 2. 展示「模拟渠道回调」的用法与回调地址（可以直接用 curl / Postman 模拟一条入站消息）；
 * 3. 优雅处理「该能力没有 Agent / 能力不可用」。
 */
import { computed, onMounted, ref } from 'vue'
import { channelStatus } from '@/api/agentscope'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

const props = defineProps({
  capability: { type: Object, required: true }
})

const status = ref(null)
const loading = ref(false)
const errorMsg = ref('')

/** 能力是否就绪 */
const capabilityReady = computed(() => props.capability?.enabled !== false)

/** 从状态对象中提取渠道列表 —— 兼容多种字段命名 */
function pickChannels(data) {
  if (!data || typeof data !== 'object') return []
  for (const key of ['channels', 'registered', 'channelList', 'list', 'items']) {
    if (Array.isArray(data[key])) return data[key]
  }
  return []
}

/** 从状态对象中取第一个存在的值 */
function pickValue(data, keys, fallback = '-') {
  if (!data || typeof data !== 'object') return fallback
  for (const key of keys) {
    if (data[key] !== undefined && data[key] !== null && data[key] !== '') return data[key]
  }
  return fallback
}

const channelRows = computed(() =>
  pickChannels(status.value).map((item, index) => {
    if (typeof item === 'string') {
      return { name: item, platform: '-', webhook: '-', received: '-', sent: '-', state: 'unknown' }
    }
    const it = item || {}
    return {
      name: it.name || it.channel || it.id || `渠道 #${index + 1}`,
      platform: it.platform || it.type || it.provider || it.vendor || '-',
      webhook: it.webhook || it.webhookUrl || it.callbackUrl || it.endpoint || '-',
      received: it.received ?? it.receivedCount ?? it.inbound ?? it.inboundCount ?? '-',
      sent: it.sent ?? it.sentCount ?? it.outbound ?? it.outboundCount ?? '-',
      state: it.status || it.state || (it.enabled === false ? 'not-ready' : 'registered')
    }
  })
)

/** 全局回调地址（Webhook） */
const webhookUrl = computed(() => pickValue(status.value, ['webhook', 'webhookUrl', 'callbackUrl', 'callback']))

const summary = computed(() => {
  const data = status.value || {}
  return [
    { label: '已注册渠道数', value: pickValue(data, ['channelCount', 'channels', 'count']) },
    { label: '累计接收消息', value: pickValue(data, ['received', 'receivedCount', 'inbound', 'inboundCount'], 0) },
    { label: '累计发送消息', value: pickValue(data, ['sent', 'sentCount', 'outbound', 'outboundCount'], 0) },
    { label: '是否启用', value: pickValue(data, ['enabled', 'active', 'ready']) }
  ]
})

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await channelStatus()
    status.value = res?.data ?? null
  } catch (e) {
    status.value = null
    errorMsg.value = e?.message || '渠道状态加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="warning"
      :closable="false"
      show-icon
      title="渠道接入依赖第三方 IM 平台（钉钉 / 飞书 / 企微）开放配置。未配置时该能力处于「未就绪」状态，属于预期现象。"
    />

    <el-alert
      v-if="!capabilityReady"
      class="mb8"
      type="info"
      :closable="false"
      show-icon
      title="当前能力标记为「未就绪」：通用对话接口会返回「能力不可用（缺少 Agent Bean）」，属于正常提示，可先完成渠道配置后再试。"
    />

    <el-card shadow="never" class="mb8">
      <template #header>
        <div class="card-header">
          <span>渠道接入状态</span>
          <el-button link type="primary" :loading="loading" @click="load">刷新状态</el-button>
        </div>
      </template>

      <el-alert v-if="errorMsg" type="error" :closable="false" show-icon :title="errorMsg" class="mb8" />

      <el-skeleton v-if="loading && !status" :rows="3" animated />

      <template v-else>
        <el-descriptions :column="4" border size="small" class="mb8">
          <el-descriptions-item v-for="item in summary" :key="item.label" :label="item.label">
            {{ item.value }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="section-title">已注册渠道</div>
        <el-table :data="channelRows" border stripe size="small" empty-text="尚未注册任何渠道">
          <el-table-column prop="name" label="渠道名称" width="160" show-overflow-tooltip />
          <el-table-column prop="platform" label="平台" width="110" />
          <el-table-column prop="webhook" label="Webhook 地址" min-width="240" show-overflow-tooltip />
          <el-table-column prop="received" label="接收消息数" width="110" />
          <el-table-column prop="sent" label="发送消息数" width="110" />
          <el-table-column prop="state" label="状态" width="110">
            <template #default="scope">
              <el-tag size="small" :type="scope.row.state === 'not-ready' ? 'info' : 'success'">
                {{ scope.row.state }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>模拟渠道回调</span>
      </template>

      <el-steps direction="vertical" :active="3" finish-status="success">
        <el-step
          title="在 IM 平台开放平台里配置消息回调地址"
          description="把下方 Webhook 地址填到钉钉 / 飞书 / 企微的「消息接收地址」，并配置加签密钥。"
        />
        <el-step
          title="用 curl / Postman 模拟一条入站消息"
          description="向 Webhook 地址 POST 一条 JSON 报文，平台会把它转成一条入站消息交给智能体处理。"
        />
        <el-step
          title="回到页面刷新，查看接收 / 发送消息数"
          description="接收数随入站消息增长，发送数随智能体回复增长。"
        />
      </el-steps>

      <div class="section-title">Webhook 回调地址</div>
      <el-input :model-value="webhookUrl" readonly placeholder="尚未获取到 Webhook 地址">
        <template #prepend>
          <el-icon><component :is="'Link'" /></el-icon>
        </template>
      </el-input>

      <div class="section-title mt12">回调报文示例</div>
      <pre class="sample-box">curl -X POST "{{ webhookUrl || 'http://<host>/agentscope/channel/webhook' }}" \
  -H "Content-Type: application/json" \
  -d '{"channel":"dingtalk","userId":"u-1001","text":"帮我查一下系统里有多少用户"}'</pre>
    </el-card>

    <el-card shadow="never" class="mb8">
      <template #header>
        <span>通用对话（验证能力是否已装配 Agent）</span>
      </template>
      <CapabilityChatBox capability-id="channel" default-prompt="帮我查一下系统里有多少用户。" />
    </el-card>

    <CapabilityDetailCard capability-id="channel" title="渠道能力详情" class="mt12" />
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-regular);
  margin-bottom: 6px;
}
.sample-box {
  margin: 0;
  padding: 10px 12px;
  background: var(--el-fill-color-light);
  border-radius: 4px;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
  line-height: 1.7;
}
.mt12 {
  margin-top: 12px;
}
</style>
