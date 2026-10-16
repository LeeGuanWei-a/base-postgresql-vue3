<!--
  条件边界事件（Conditional Boundary Event）属性面板
  - conditionExpression：当条件求值为 true 时触发
  - cancelActivity：true=中断宿主活动
-->
<template>
  <el-form label-width="110px" size="small" @submit.prevent>
    <el-form-item label="触发条件">
      <el-input v-model="conditionExpression" type="textarea" :rows="3"
                placeholder="如：${amount > 10000} 或 ${variables.containsKey('urgent')}"
                @change="onChangeCondition"/>
    </el-form-item>
    <el-form-item label="是否中断宿主">
      <el-switch v-model="cancelActivity" @change="onChangeCancelActivity"/>
    </el-form-item>
    <div style="color:#909399; font-size: 12px; line-height: 1.6">
      条件边界：当条件表达式求值为 true 时触发，可与 timer/error/signal 等类型组合实现复杂场景。
      表达式可直接访问流程变量，如 <code>${amount &gt; 10000}</code>。
    </div>
  </el-form>
</template>

<script>
/**
 * 条件边界事件配置面板
 * @author integration-supplement
 */
export default {
  name: 'ConditionalBoundary',
  props: {
    id: String,
    type: String,
    businessObject: Object
  },
  data() {
    return {
      conditionExpression: '',
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
        // ConditionalEventDefinition#condition 是 FormalExpression，取 body
        if (def.condition && def.condition.body) {
          this.conditionExpression = def.condition.body
        } else if (typeof def.condition === 'string') {
          this.conditionExpression = def.condition
        } else {
          this.conditionExpression = ''
        }
      }
      this.cancelActivity = this.businessObject.cancelActivity !== false
    },
    onChangeCondition(val) {
      const el = window.bpmnInstances.bpmnElement
      if (!el || !el.businessObject.eventDefinitions || el.businessObject.eventDefinitions.length === 0) return
      const def = el.businessObject.eventDefinitions[0]
      // 创建新的 FormalExpression 替换
      const expr = window.bpmnInstances.moddle.create('bpmn:FormalExpression', { body: val || '' })
      window.bpmnInstances.modeling.updateModdleProperties(el, def, { condition: expr })
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
