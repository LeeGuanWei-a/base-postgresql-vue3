<!--
  升级边界事件（Escalation Boundary Event）属性面板
  - escalationRef / escalationCode：可选，用于匹配 ServiceTask 抛的 BpmnEscalation
  - cancelActivity：true=中断宿主活动（默认）
-->
<template>
  <el-form label-width="110px" size="small" @submit.prevent>
    <el-form-item label="升级 code">
      <el-input v-model="escalationCode" placeholder="如：biz-warn-level-1"
                clearable @change="onChangeCode"/>
    </el-form-item>
    <el-form-item label="是否中断宿主">
      <el-switch v-model="cancelActivity" @change="onChangeCancelActivity"/>
    </el-form-item>
    <div style="color:#909399; font-size: 12px; line-height: 1.6">
      升级边界（BPMN 2.0 新引入）：与 error 相似但语义更轻 ——
      表示"业务上需要更高级别关注但不是失败"。
      节点通过 <code>throw new BpmnEscalation(code)</code> 抛出。
    </div>
  </el-form>
</template>

<script>
/**
 * 升级边界事件配置面板
 * @author integration-supplement
 */
export default {
  name: 'EscalationBoundary',
  props: {
    id: String,
    type: String,
    businessObject: Object
  },
  data() {
    return {
      escalationCode: '',
      cancelActivity: true
    }
  },
  watch: {
    businessObject: {
      immediate: true,
      handler() {
        this.initFromBusiness()
      }
    }
  },
  methods: {
    initFromBusiness() {
      if (!this.businessObject) return
      if (this.businessObject.eventDefinitions && this.businessObject.eventDefinitions.length > 0) {
        const def = this.businessObject.eventDefinitions[0]
        this.escalationCode = def.escalationCode || ''
      }
      this.cancelActivity = this.businessObject.cancelActivity !== false
    },
    onChangeCode(val) {
      const el = window.bpmnInstances.bpmnElement
      if (!el || !el.businessObject.eventDefinitions || el.businessObject.eventDefinitions.length === 0) return
      const def = el.businessObject.eventDefinitions[0]
      window.bpmnInstances.modeling.updateModdleProperties(el, def, { escalationCode: val || null })
    },
    onChangeCancelActivity(val) {
      const el = window.bpmnInstances.bpmnElement
      window.bpmnInstances.modeling.updateProperties(el, { cancelActivity: val })
    }
  }
}
</script>

<style scoped lang="scss">
</style>
