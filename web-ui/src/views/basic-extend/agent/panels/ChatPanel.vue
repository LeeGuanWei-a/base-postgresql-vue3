<script setup name="ChatPanel">
/**
 * 基础对话 —— 直接复用平台默认智能体（basicAssistantAgent）。
 *
 * 演示 AgentScope ReAct 智能体的最基本形态：系统提示词 + 多轮会话。
 */
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="success"
      :closable="false"
      show-icon
      title="默认智能体：读取 application.yml 中 agentscope.agent.* 配置（名称 / 系统提示词 / 最大迭代轮次）。"
    />
    <CapabilityChatBox
      capability-id="chat"
      default-prompt="你好，请介绍一下你自己以及当前平台的业务数据。"
    />
    <CapabilityDetailCard capability-id="chat" title="智能体运行信息" class="mt12" />
  </div>
</template>

<style scoped>
.mt12 {
  margin-top: 12px;
}
</style>
