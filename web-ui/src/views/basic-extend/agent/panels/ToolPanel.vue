<script setup name="ToolPanel">
/**
 * 工具调用 —— 演示 ReAct 循环中模型自主决定调用哪个业务工具。
 *
 * 工具来自 PlatformTools / SystemTools，真实查询 PostgreSQL：
 * 用户、字典、角色、操作日志、表行数、当前时间。
 */
import { ref } from 'vue'
import CapabilityChatBox from '../components/CapabilityChatBox.vue'
import CapabilityDetailCard from '../components/CapabilityDetailCard.vue'

defineProps({
  capability: { type: Object, required: true }
})

const prompt = ref('系统里有多少用户？管理员是谁？')

const examples = [
  '系统里有多少用户？管理员是谁？',
  '查询一下 sys_common_status 字典',
  '最近有哪些操作日志？',
  '现在几点了？'
]
</script>

<template>
  <div>
    <el-alert
      class="mb8"
      type="success"
      :closable="false"
      show-icon
      title="工具会真实查询 PostgreSQL。Mock 模式下由关键词匹配决定调用哪个工具，接入真实模型后由模型自行决策。"
    />

    <div class="examples">
      <span class="examples-label">示例问题（点击填充）：</span>
      <el-tag
        v-for="item in examples"
        :key="item"
        class="examples-tag"
        effect="plain"
        @click="prompt = item"
      >
        {{ item }}
      </el-tag>
    </div>

    <CapabilityChatBox capability-id="tool" v-model:prompt="prompt" />

    <CapabilityDetailCard capability-id="tool" title="已注册工具清单" class="mt12" />
  </div>
</template>

<style scoped>
.examples {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}
.examples-label {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.examples-tag {
  cursor: pointer;
}
.mt12 {
  margin-top: 12px;
}
</style>
